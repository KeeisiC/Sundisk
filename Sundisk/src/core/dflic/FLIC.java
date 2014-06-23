package core.dflic;

import java.util.*;

import core.field.RandomGridIterator;
import core.field.ScanlineGridIterator;
import core.field.Vec2Field;
import core.field.Vec2Iterator;
import core.image.AccumImage;
import core.image.ScalarImage;
import core.math.Vec2;
import core.math.Vec2Transform;
/** Fast Line Integral Convolution algorithm.
 
* This class performs the Fast Line Integral Convolution algorithm on a
* given input image and vector field, producing an output image. A number 
* of parameters that affect the execution of FLIC can be modified before
* the computation. Among these are changing the input image and vector field
* and the output image, independently changing the mapping between the 
* vector field and both the input and output images, changing the convolution
* kernel width and sampling step, the maximum length to follow any particular
* streamline, the minimum and maximum sampling coverage of each output
* pixel, and an iterator specifying the order in which samples should be
* chosen. Most of these parameters have default values that will result in
* a good rendition of the LIC effect.
 * @author Andreas Sundquist
 * @version 1.0
*/

public class FLIC {
  /* These members privately store the parameters affecting the executiong of
   *   FLIC. In order to modify them, the corresponding accessor methods
   *   should be called. */
  private ScalarImage input;
  private AccumImage output;
  private Vec2Field field;
  private Vec2Transform ftoo, otof, ftoi, itoo;
  private double ostreamlen, ostepsize, omaxlen, mincoverage, maxcoverage;
  private Vec2Iterator iterator;
  private boolean clear, normalize;
  private Vector singularities;
  
  /** Constructs an instance of a FLIC with the given initial parameters. 
   *   Note that this does *not* actually perform the computation, but retains
   *   the state of the parameters over the course of potentially many similar
   *   FLIC computations.
   * The "input" image must be a ScalarImage or derived type. Input samples
   *   are assumed to have a Gaussian distribution with an average of zero, 
   *   and final sample values are rescaled to renormalize the variance.
   *   If this is not the desired effect, a post-processing step can be used
   *   to renormalize the entire output image.
   * The "output" image must be an AccumImage or derived type since it will
   *   be accumulated to. The image is normalized at the end of the FLIC
   *   computation.
   * Note that because the input is convolved along streamlines, the input
   *   image domain must be larger than the output image domain. In other
   *   words, for each output pixel, when mapped to the corresponding input
   *   pixel, there must be enough pixels around that input pixel to produce
   *   a set of samples for the convolution.
   * The "field" can be any type of Vec2Field, but field lines will always
   *   be reparameterized by arc-length and is therefore independent of
   *   the magnitude of the vectors.
   * "ftoi" defines the transformation from the vector field to the input
   *   image. This transformation *must* be rigid, although it can be scaled
   *   as well.
   * "ftoo" defines the transformation from the vector field to the output
   *   image. This transformation *must* be rigid, although it can be scaled
   *   as well.
   * All other parameters are set to reasonable, default values.
   * Requires: "ftoi" and "ftoo" describe scaled, rigid transformations
   *   or are null */
  public FLIC(ScalarImage input,AccumImage output,Vec2Field field,Vec2Transform ftoi,Vec2Transform ftoo){
    SetInput(input);
    SetOutput(output);
    SetField(field);
    SetFtoITransform(ftoi);
    SetFtoOTransform(ftoo);
    SetDefaultStreamLen();
    SetDefaultStepSize();
    SetDefaultMaxLen();
    SetDefaultMinCoverage();
    SetDefaultMaxCoverage();
    SetDefaultIterator();
    SetDefaultClear();
    SetDefaultNormalize();
    ClearSingularities();
  }
  /** Sets the input image to "input" */  
  public void SetInput(ScalarImage input){
    this.input = input;
  }
  /** Sets the output image to "output" */
  public void SetOutput(AccumImage output){
    this.output = output;
  }
  /** Sets the vector field to "field" */ 
  public void SetField(Vec2Field field){
    this.field = field;
  }
  /** Sets the vector field-to-input image transformation to "ftoi".
   * Requires: "ftoi" must be a scaled, rigid transformation, or else null */ 
  public void SetFtoITransform(Vec2Transform ftoi){
    if (ftoi!=null) {
      if (!ftoi.isRigidScaled())
        throw new RuntimeException("FLIC.SetFtoITransform: Not a scaled rigid transform!");
      this.ftoi = ftoi;
      if (ftoo!=null)
        this.itoo = ftoi.invert().concatenate(ftoo);
    }
  }
  /** Sets the vector field-to-output image transformation to "ftoo".
   * Requires: "ftoo" must be a scaled, rigid transformation, or else null */ 
  public void SetFtoOTransform(Vec2Transform ftoo){
    if (ftoo!=null) {
      if (!ftoo.isRigidScaled())
        throw new RuntimeException("FLIC.SetFtoOTransform: Not a scaled rigid transform!");
      this.ftoo = ftoo;
      this.otof = ftoo.invert();
      if (ftoi!=null)
        this.itoo = ftoi.invert().concatenate(ftoo);
    }
  }
  /** Sets the convolution kernel width (in output image units) to
   *   "ostreamlen". The kernel is always uniform and symmetric.
   * The maximum streamline length is reset to its default value. */
  public void SetStreamLen(double ostreamlen){
    this.ostreamlen = ostreamlen;
    SetDefaultMaxLen();
  }
  /** Sets the default convolution kernel width, which is one-eighth of the
   *   maximum distance in the output image. */
  public void SetDefaultStreamLen(){
    SetStreamLen((output.width>output.height) ? output.width/8 : output.height/8);
  }
  /** Sets the convolution and streamline step size (in output image units)
   *   to "ostepsize". */
  public void SetStepSize(double ostepsize){
    this.ostepsize = ostepsize;
  }
  /** Sets the convolution and streamline step size to the default value,
   *   which is one-half of an output pixel. */ 
 public void SetDefaultStepSize(){
    SetStepSize(0.5);
  }
 /** Sets the maximum streamline length (in output image units), the length
  *   that FLIC will follow a particular streamline to compute successive
  *   convolutions. Larger lengths generally speed up the FLIC algorithm,
  *   but may run into problems at singularities. If the length is set to
  *   less that the convolution kernel width, the length is reset to the
  *   width of the kernel. */
  public void SetMaxLen(double omaxlen){
    if (omaxlen<ostreamlen)
      this.omaxlen = ostreamlen;
    else
      this.omaxlen = omaxlen;
  }
  /** Sets the default maximum streamline length, which is four times
   *   the width of the convolution kernel. */ 
  public void SetDefaultMaxLen(){
    SetMaxLen(ostreamlen*8.0);
  }
  /** Sets the minimum coverage of each output pixel. When the FLIC algorithm
   *   is complete, every output pixel will have been hit at least an amount
   *   "mincoverage". Note that because samples are accumulated using a
   *   box-filter, contributions are not necessarily in integer amounts.
   * Larger values of "mincoverage" may reduce aliasing and improve the quality
   *   of the computation in general, but the extra computational cost usually
   *   does not warrant it. */ 
  public void SetMinCoverage(double mincoverage){
    this.mincoverage = mincoverage;
  }
  /** Sets the default minimum coverage for output pixels, which is 1.0.
   *   Experimentation has shown that higher values doesn"t usually provide
   *   much added benefit. */ 
  public void SetDefaultMinCoverage(){
    SetMinCoverage(1.0);
  }
  /** Sets the "maximum" coverage of each output pixel. This is not a maximum
   *   in a strict sense - indeed, when the FLIC algorithm is complete, there
   *   may be pixels that have been hit more than that. This value is instead
   *   used as a heuristic in order to determine when sets of pixels have
   *   been hit enough times so that it might be a better idea to try an
   *   altogether different streamline. */  
  public void SetMaxCoverage(double maxcoverage){
    this.maxcoverage = maxcoverage;
  }
  /** Sets the default "maximum" coverage of each output pixel, which is 3.0.
   *   The heuristic is not used as extensively as it should be, so the setting
   *   of this value is not so critical, but 3.0 seems to be a reasonable
   *   number. */ 
  public void SetDefaultMaxCoverage(){
    SetMaxCoverage(3.0);
  }
  /** Sets the order in which streamline seed points are chosen to that
   *   sequence produced by "iterator". Generally, the more random the 
   *   sequence of points, the better the results, though it may produce
   *   significantly different output images depending on the particular
   *   random sequence. Frame-to-frame coherence in an animation is 
   *   particularly difficult to obtain without care.
   * The iterator must be reset before every call to Compute(), or else
   *   the default iterator will be invoked. */ 
  public void SetIterator(Vec2Iterator iterator){
    this.iterator = iterator;
  }
  /** Sets the default ordering of streamline seed points, which is a pseudo-
   *   random sequence of points. */ 
  public void SetDefaultIterator() {
    iterator = new RandomGridIterator(output.width, output.height);
  }
  /** Sets the default ordering of streamline seed points, a pseudo-random
   *   sequence of points generated by the random-number generator "random".
   * By passing in a "random" object seeded with the same value, the set
   *   of streamline seed points becomes deterministic (but randomly 
   *   distributed all the same). */  
  public void SetDefaultIterator(Random random){
    iterator = new RandomGridIterator(output.width, output.height, random);
  }
  /** Sets whether or not to normalize the output after rendering. */
  public void SetNormalize(boolean normalize){
    this.normalize = normalize;
  }
  /** By default, the output is normalized after rendering. */ 
  public void SetDefaultNormalize(){
    SetNormalize(true);
  }
  /** Sets whether or not the output is cleared prior to rendering. */ 
  public void SetClear(boolean clear){
    this.clear = clear;
  }
  /** By default, the output is cleared before rendering. */ 
  public void SetDefaultClear(){
    SetClear(true);
  }
  /** Clears the list of singularities. */
  public void ClearSingularities(){
    singularities = new Vector();
  }
  /** Adds a singularity, where streamlines are always terminated. The point
  "v" is given in the coordinate system of the vector field. */
  public void AddSingularity(Vec2 v){
    singularities.add(v);
  }
  
  /** Private temporary variables */
  private double fstreamlen, istreamlen, fstepsize, fmaxlen, fcurlen;
  private Streamline fstream, bstream;
  private int sampleoffset;
  private Vec2[] samplev;
  private double[] sample;
  
  /** Executes the FLIC algorithm, filtering the input image using the given
   *   vector field to produce an output image.
   * The given iterator is used to sample the input image until the 95% of
   *   the output image pixels have achieved the minimum coverage requirement.
   *   Then, the remaining output pixels are filled in using the LIC algorithm.
   *   This results in improved performance since it becomes increasingly 
   *   difficult to randomly select an uncovered pixel.
   * Requires: all the parameters are valid */ 
  public void Compute(){
    if (iterator==null)
      SetDefaultIterator();
    
    if (clear)
      output.Clear();
    fstreamlen = otof.getScale()*ostreamlen;
    istreamlen = ftoi.getScale()*fstreamlen;
    fstepsize = otof.getScale()*ostepsize;
    fmaxlen = otof.getScale()*omaxlen;
    
    fstream = new Streamline(field, fstepsize);
    bstream = new Streamline(field, fstepsize);
    Enumeration enume = singularities.elements();
    while (enume.hasMoreElements()) {
      Vec2 v = (Vec2)enume.nextElement();
      fstream.AddSingularity(v);
      bstream.AddSingularity(v);
    }
    
    InitializeContrib(ceil(istreamlen)+1);
    
    sampleoffset = ceil(0.5*fmaxlen/fstepsize)+1;
    samplev = new Vec2[sampleoffset*2+1];
    sample = new double[sampleoffset*2+1];
    for (int i = sampleoffset*2; i>=0; --i) {
      samplev[i] = new Vec2();
      sample[i] = 0.0;
    }
    
    System.out.println("Beginning FLIC...");

    fcurlen = 0.5*fmaxlen + 1e-3;
    fstream.SetLength(fcurlen);
    bstream.SetLength(-fcurlen);
    int targetcoverage = output.size*95/100;
    targetcoverage *= 10;
    output.minalpha = mincoverage;
    Vec2 p;
    int last = 0;
    while ((output.coverage<targetcoverage) && ((p = iterator.next())!=null)) {
      ComputeStream(p);
      if (output.coverage>=(last+10000)) {
        last += 10000;
        System.out.print((last/1000)+"K ");
        if (((last/10000)%25)==0)
          System.out.println();
      }
    }
    
    System.out.println();
    System.out.println("Finishing remaining streams...");
    fcurlen = 0.5*fstreamlen + 1e-3;
    fstream.SetLength(fcurlen);
    bstream.SetLength(-fcurlen);
    iterator = new ScanlineGridIterator(output.width, output.height);
    last = 0;
    while ((p = iterator.next())!=null) {
      if (round(p.y)>=(last+50)) {
        last += 50;
        System.out.print(last+" ");
        if ((last%1250)==0)
          System.out.println();
      }
      //System.out.println("(x,y) = ("+p.x+","+p.y+")");
      while (output.getAlpha(round(p.x), round(p.y))<mincoverage) {
        ComputeStream(p);
      }
    }
    
    iterator = null;
    if (normalize)
      output.Normalize();
  }
  
  private static int floor(double x){
    return (int)Math.floor(x);
  }
  
  private static int ceil(double x){
    return (int)Math.ceil(x);
  }
  
  private static int round(double x){
    return (int)Math.floor(x + 0.5);
  }
  
  private static int min(int x, int y){
    return (x<y) ? x : y;
  }
  
  private static int max(int x, int y){
    return (x>y) ? x : y;
  }
  
  private int contribsize;
  private double[][] contrib;
  private double contribtot;
  
  private Vec2 p, op = new Vec2();
  
  /** Performs a FLIC (or LIC) streamline computation with the seed point "p0".
   *   "p0" is not modified. */ 
  private void ComputeStream(Vec2 p0)
  
  {
    if (!output.inBounds(p0))
      return;
    if (output.getAlpha(round(p0.x), round(p0.y))>=maxcoverage)
      return;
    
    int i, smin, smax, imin, imax;
    
    otof.V(p0, op);
    fstream.Start(op);
    bstream.Start(op);
    
    int n = floor(0.5*fstreamlen/fstepsize);
    int m = floor(fcurlen/fstepsize);
    double total = 0.0;
    for (i = sampleoffset; (i<=(sampleoffset+n)) && ((p = fstream.Next()) != null); )
      if (!input.inBounds(ftoi.V(p)))
        fstream.Stop();
      else {
        total += sample[i] = input.getBilinear(p);
        ContribAdd(samplev[i++].Set(p));
      }
    smax = i - 1;
    bstream.Next();
    for (i = sampleoffset-1; (i>=(sampleoffset-n)) && ((p = bstream.Next()) != null); )
      if (!input.inBounds(ftoi.V(p)))
        bstream.Stop();
      else {
        total += sample[i] = input.getBilinear(p);
        ContribAdd(samplev[i--].Set(p));
      }
    smin = i + 1;
    itoo.V(samplev[sampleoffset], op);
    output.AccumulateBilinear(op, ContribRescale(total));
    //output.Accumulate(round(op.x), round(op.y), ContribRescale(total), 1.0);
    if ((smax<(sampleoffset+n)) || (smin>(sampleoffset-n))) {
      i = i;
    }
    
    if ((m>n) && ((!fstream.stopped()) || (!bstream.stopped()))) {
      m -= n;
      int excess = 0;
      if (!fstream.stopped()) {
        for (i = sampleoffset + 1; (i<=(sampleoffset + m)) && (i<=smax); ) {
          if ((i-n-1)>=smin) {
            total -= sample[i-n-1];
            ContribDel(samplev[i-n-1]);
          }
          p = fstream.Next();
          if (p!=null)
            if (input.inBounds(ftoi.V(p))) {
              total += sample[smax = i+n] = input.getBilinear(p);
              ContribAdd(samplev[i+n].Set(p));
            } else
              fstream.Stop();
          itoo.V(samplev[i++], op);
          /*
          if (output.getAlpha(round(op.x), round(op.y))>=maxcoverage) {
            ++excess;
            if (excess>5)
              fstream.Stop();
          } else
            excess = (excess>0) ? excess-1 : 0;
          */
          output.AccumulateBilinear(op, ContribRescale(total));
          //output.Accumulate(round(op.x), round(op.y), ContribRescale(total), 1.0);
        }
      
        imin = max(i-n-1, smin);
        for (i = smax; i>=imin; --i)
          ContribClear(samplev[i]);
        contribtot = 0.0;
      
        if (!bstream.stopped()) {
          total = 0.0;
          imin = max(sampleoffset - n, smin);
          imax = min(sampleoffset + n, smax);
          for (i = imin; i<=imax; ++i) {
            total += sample[i];
            ContribAdd(samplev[i]);
          }
        }
      }
      
      if (!bstream.stopped()) {
        excess = 0;
        for (i = sampleoffset - 1; (i>=(sampleoffset - m)) && (i>=smin); ) {
          if ((i+n+1)<=smax) {
            total -= sample[i+n+1];
            ContribDel(samplev[i+n+1]);
          }
          p = bstream.Next();
          if (p!=null)
            if (input.inBounds(ftoi.V(p))) {
              total += sample[smin = i-n] = input.getBilinear(p);
              ContribAdd(samplev[i-n].Set(p));
            } else
              bstream.Stop();
          itoo.V(samplev[i--], op);
          /*
          if (output.getAlpha(round(op.x), round(op.y))>=maxcoverage) {
            ++excess;
            if (excess>5)
              bstream.Stop();
          } else
            excess = (excess>0) ? excess-1 : 0;
          */
          output.AccumulateBilinear(op, ContribRescale(total));
          //output.Accumulate(round(op.x), round(op.y), ContribRescale(total), 1.0);
        }
        
        imax = min(i+n+1, smax);
        for (i = smin; i<=imax; ++i)
          ContribClear(samplev[i]);
      }
    
    } else
      for (i = smin; i<=smax; ++i)
        ContribClear(samplev[i]);
    
    contribtot = 0.0;
    for (i = smin; i<=smax; ++i)
      sample[i] = 0.0;
  }
  
  private void InitializeContrib(int size)
  {
    contrib = new double[size][size];
    contribsize = size;
    for (int j = 0; j<size; ++j)
      for (int i = 0; i<size; ++i)
        contrib[j][i] = 0.0;
    contribtot = 0.0;
  }
  
  private void ContribAccum(int x, int y, double a)
  {
    double c = contrib[y][x];
    contribtot -= c*c;
    //contribtot -= c;
    c += a;
    contrib[y][x] = c;
    contribtot += c*c;
    //contribtot += c;
  }
  
  private void ContribAdd(Vec2 p)
  {
    if (p==null)
      return;
    double xpf = Math.floor(p.x);
    double ypf = Math.floor(p.y);
    int i = ((int)xpf) % contribsize;
    int j = ((int)ypf) % contribsize;
    int i2 = ((i+1)==contribsize) ? 0 : i+1;
    int j2 = ((j+1)==contribsize) ? 0 : j+1;
    double xf = p.x-xpf;
    double yf = p.y-ypf;
    
    ContribAccum(i, j, (1.0-xf)*(1.0-yf));
    ContribAccum(i2, j, xf*(1.0-yf));
    ContribAccum(i, j2, (1.0-xf)*yf);
    ContribAccum(i2, j2, xf*yf);
  }

  private void ContribDel(Vec2 p)
  {
    if (p==null)
      return;
    double xpf = Math.floor(p.x);
    double ypf = Math.floor(p.y);
    int i = ((int)xpf) % contribsize;
    int j = ((int)ypf) % contribsize;
    int i2 = ((i+1)==contribsize) ? 0 : i+1;
    int j2 = ((j+1)==contribsize) ? 0 : j+1;
    double xf = p.x-xpf;
    double yf = p.y-ypf;
    
    ContribAccum(i, j, -(1.0-xf)*(1.0-yf));
    ContribAccum(i2, j, -xf*(1.0-yf));
    ContribAccum(i, j2, -(1.0-xf)*yf);
    ContribAccum(i2, j2, -xf*yf);
  }
  
  private void ContribClear(Vec2 p)
  {
    if (p==null)
      return;
    int i = (floor(p.x)) % contribsize;
    int j = (floor(p.y)) % contribsize;
    int i2 = ((i+1)==contribsize) ? 0 : i+1;
    int j2 = ((j+1)==contribsize) ? 0 : j+1;
    contrib[j][i] = 0.0;
    contrib[j][i2] = 0.0;
    contrib[j2][i] = 0.0;
    contrib[j2][i2] = 0.0;
  }
  
  private double ContribRescale(double total)
  {
    return total/Math.sqrt(contribtot);
    //return total/contribtot;
  }
}
