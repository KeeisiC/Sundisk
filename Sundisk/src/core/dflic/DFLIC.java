package core.dflic;
/**
 * SundquistDLIC 
 * 
 * 
 */
import java.util.*;
import core.field.ScanlineGridIterator;
import core.field.Vec2Field;
import core.field.Vec2Iterator;
import core.image.AccumImage;
import core.math.Vec2;
import core.math.Vec2Transform;

/** Dynamic Fast Line Integral Convolution algorithm.
 *
 * This class performs the Dynamic Line Integral Convolution algorithm with
 * the FLIC performance enhancement (the F stands for fast LIC). It takes in two time-varying
 * vector fields and produces an output image, with a given mapping between
 * the fields and the image. Additionally, most of the parameters that
 * can be set for the FLIC algorithm can also be specified here:
 * the convolution kernel width, the sample spacing, the maximum streamline
 * length, the minimum and "maximum" output pixel coverage.
 * 
 * A DFLIC object is created once for an entire animation sequence.
 * For each frame, Compute() is called in order to generate a FLIC image
 * for the current frame based on the vector field "field". Next, 
 * Evolve() is called to update the object for the given time step using
 * the vector field "dfield". This can be repeated as long as necessary.
 *
 * Note that the input samples are created so that their intensities 
 * have a uniform distribution over [-1, 1]. The output image will share
 * the same statistics, with an average of 0 and a standard of deviation
 * of Sqrt(1/12).
 * 
 * @author Andreas Sundquist
 * @version 1.0
 */
public class DFLIC {
  
 
  /** privately stores the state of the DFLIC object from
   *   frame-to-frame. In order to modify them, the corresponding accessor
   *   methods should be called. */
  private Vec2Field field, dfield;
  private AccumImage input, output;
  private Vec2Transform ftoo, otof, ftoi, itof;
  private double ostreamlen, ostepsize, omaxlen;
  private double mincoverage, maxcoverage;
  private double[] inputfilterkernel;
  private int inputfilterrepeat;
  private Random random;
  private FLIC flic;
  
  /** DFLIC constructs a new DFLIC object with the given initial parameters. 
   * Note that this does *not* perform any sort of computation.
   * "field" is a Vec2Field whose streamlines we want to visualize. 
   *  It must be updated between frames to represent the vector field 
   *   at different points in time.
   * "dfield" is a Vec2Field that describes how points in "field" are moving
   *   over time. Currently, a simple Euler method is used to integrate the
   *   motion of points on the field lines, so "dfield" need only describe
   *   the instantaneous velocity of the points. Though it is possible to
   *   implement a higher-order integration method, the pay-off is not
   *   significant to warrant the extra computation.
   * "output" must be an AccumImage since the output will be accumulated to
   *   by FLIC, though the resulting image will be normalized before returning.
   * "ftoo" is the transformation from both "field" and "dfield" to "output".
   *   It must be a scaled, rigid transformation. Indeed, this means that
   *   both "field" and "dfield" must reside in the same coordinate system.
   * All other parameters, including the FLIC parameters, will be set to
   *   reasonable default values, which will yield good results. */
  public DFLIC(Vec2Field field, Vec2Field dfield, AccumImage output, Vec2Transform ftoo) {
    flic = new FLIC(null, output, field, null, ftoo);
    SetField(field);
    SetDField(dfield);
    SetOutput(output);
    SetFtoOTransform(ftoo);
    SetDefaultStreamLen();
    SetDefaultStepSize();
    SetDefaultInputFilterKernel();
    SetDefaultInputFilterRepeat();
    SetDefaultNormalize();
    InitializeElements();
    InitializeRandom();
  }
  
 /** Sets the field we would like to visualize to "field" */
  public void SetField(Vec2Field field) {
    this.field = field;
    flic.SetField(field);
  }
  
 /** Sets the motion of the field line points to "dfield" */
  public void SetDField(Vec2Field dfield){
    this.dfield = dfield;
  }

/** Sets the target output image to "output" */
  public void SetOutput(AccumImage output) {
    this.output = output;
    flic.SetOutput(output);
    SetDefaultStreamLen();
  }
  
 /** Sets the mapping from the vector fields to the output image to "ftoo",
   * and adjusts the inverse mapping accordingly.
   * Requires: "ftoo" is a scaled, rigid transformation, or else null */ 
  public void SetFtoOTransform(Vec2Transform ftoo) {
    if (ftoo!=null) {
      this.ftoo = ftoo;
      this.otof = ftoo.invert();
      flic.SetFtoOTransform(ftoo);
    }
  }
  
 /** Sets the mapping from the output image to the vector field to "otof",
   *   and adjusts the inverse mapping accordingly.
   * Requires: "otof" is a scaled, rigid transformation, or else null */
  public void SetOtoFTransform(Vec2Transform otof){
    if (otof!=null) {
      this.otof = otof;
      this.ftoo = otof.invert();
      flic.SetFtoOTransform(ftoo);
    }
  }
  
 /** Sets the convolution kernel width (in output image units) to
   *   "ostreamlen". The kernel is always uniform and symmetric.
   * The maximum streamline length is reset to its default value. */ 
  public void SetStreamLen(double ostreamlen){
    this.ostreamlen = ostreamlen;
    flic.SetStreamLen(ostreamlen);
  }
/** Sets the default convolution kernel width, which is one-eighth of the
   *   maximum distance in the output image. */ 
  public void SetDefaultStreamLen() {
    SetStreamLen((int)(Math.sqrt(sqr(output.width) + sqr(output.height))/8));
    //SetStreamLen((output.width>output.height) ? output.width/8 : output.height/8);
  }
  
/** Sets the convolution and streamline step size (in output image units)
   *   to "ostepsize". */ 
  public void SetStepSize(double ostepsize){
    this.ostepsize = ostepsize;
    flic.SetStepSize(ostepsize);
  }
/** Sets the convolution and streamline step size to the default value,
   *   which is one-half of an output pixel. */
  public void SetDefaultStepSize(){
    SetStepSize(0.5);
  }
/** Sets the maximum streamline length (in output image units).  
   *   This is the maximum  length
   *   that FLIC will follow a particular streamline to compute successive
   *   convolutions. Larger lengths generally speed up the FLIC algorithm,
   *   but may run into problems at singularities. If the length is set to
   *   less that the convolution kernel width, the length is reset to the
   *   width of the kernel. */ 
  public void SetMaxLen(double omaxlen){
    this.omaxlen = omaxlen;
    flic.SetMaxLen(omaxlen);
  }
/** Sets the default maximum streamline length, which is four times
   *   the width of the convolution kernel. */ 
  public void SetDefaultMaxlen(){
    SetMaxLen(ostreamlen*4.0);
  } 
/** Sets the minimum coverage of each output pixel. When the FLIC algorithm
   *   is complete, every output pixel will have been hit at least an amount
   *   "mincoverage". Note that because samples are accumulated using a
   *   box-filter, contributions are not necessarily in integer amounts.
   *   Larger values of "mincoverage" may reduce aliasing and improve the quality
   *   of the computation in general, but the extra computational cost usually
   *   does not warrant it. */  
  public void SetMinCoverage(double mincoverage){
    this.mincoverage = mincoverage;
    flic.SetMinCoverage(mincoverage);
  }
 /** Sets the default minimum coverage for output pixels, which is 1.0.
   *   Experimentation has shown that higher values do not usually provide
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
    flic.SetMaxCoverage(maxcoverage);
  }
 /** Sets the default "maximum" coverage of each output pixel, which is 3.0.
   *   The heuristic is not used as extensively as it should be, so the setting
   *   of this value is not so critical, but 3.0 seems to be a reasonable
   *   number. */ 
  public void SetDefaultMaxCoverage(double maxcoverage){
    SetMaxCoverage(3.0);
  }
 /** Sets the input filter 3x3 kernel. */
  public void SetInputFilterKernel(double[] kernel){
    inputfilterkernel = kernel;
  }
/** Sets the default kernel. 
   * The kernel is of type double[10], and the convolution computed is of the form:
   * out(i,j) = c0 + c1*out(i-1,j-1) + c2*out( i ,j-1) + c3*out(i+1,j-1) +
   * c4*out(i-1, j ) + c5*out( i , j ) + c6*out(i+1, j ) +
   * c7*out(i-1,j+1) + c8*out( i ,j+1) + c9*out(i+1,j+1)
   * where ci = kernel[i).  
   * See the method Convolve3x3 in core.image.ScalarImage.java  */
  private double[] defaultkernel = {
    0.0, 1.0/16, 1.0/8, 1.0/16, 1.0/8, 1.0/4, 1.0/8, 1.0/16, 1.0/8, 1.0/16
  };
 /** Sets the default input filter kernel. */ 
  public void SetDefaultInputFilterKernel(){
    SetInputFilterKernel(defaultkernel);
  }
  /** Sets the number of times the input filtering is repeated. Zero disables it */
  public void SetInputFilterRepeat(int repeat){
    inputfilterrepeat = repeat;
  }
  /** By default disables the input filtering. */ 
  public void SetDefaultInputFilterRepeat(){
    SetInputFilterRepeat(0);
  }
  /** Sets whether or not the output is normalized after rendering. */
  public void SetNormalize(boolean normalize){
    flic.SetNormalize(normalize);
  }
  /** By default, the output is normalized after rendering. */
  public void SetDefaultNormalize(){
    SetNormalize(true);
  }
  /** Clears the list of singularities. */
  public void ClearSingularities(){
    flic.ClearSingularities();
  }
  /** Adds a singularity.  A singularity is where streamlines are always terminated. The point
   *   "v" is given in the coordinate system of the vector field. */ 
  public void AddSingularity(Vec2 v){
    flic.AddSingularity(v);
  }
  /** Sets the pseudo-random number generator to "random". By passing in a
   *   Random object seeded with the same value, the results of the DFLIC
   *   algorithm will be completely  deterministic, and yet it will retain
   *   its random distribution. */
  public void SetRandom(Random random){
    this.random = random;
  }
  /** Initializes the random-number generator with a randomly-seeded Random
   *   object */
  public void InitializeRandom(){
    SetRandom(new Random());
  }
  /** Computes the next FLIC image in the animation sequence using "field". 
   *   The input image to FLIC is automatically computed in such a way
   *   to preserve the frame-to-frame coherence of the animation. */ 
  public void Compute(){
    InitializeInput();
    InitializeElements(input.width*input.height*2);  
    ElementIterInit();
    int i;
    while ((i = ElementIterNext())>=0)
      if (input.inBounds(ftoi.V(elp)))
        input.AccumulateBilinear(elp, elc[i]);
      else
        ElementIterDelete();
    
    ElementIterInit();
    while ((i = ElementIterNext())>=0) {
      ftoi.V(elp);
      if (input.getAlpha(round(elp.x), round(elp.y))>2.0)
        ElementIterDelete();
    }
    
    Vec2Iterator iterator = new ScanlineGridIterator(input.width, input.height);
    Vec2 p;
    while ((p = iterator.next())!=null) {
      double alpha = input.getAlpha(round(p.x), round(p.y));
      if (alpha<0.5) {
        p.x += 0.5*random.nextFloat() - 0.25;
        p.y += 0.5*random.nextFloat() - 0.25;
        double c = 2.0*random.nextFloat() - 1.0;
        itof.V(p, elp);
        i = ElementAdd(elp.x, elp.y, c);
        input.AccumulateBilinear(p, c);
      } else if (alpha>2.0) {
        itof.V(p, elp);
        ElementAdd(elp.x, elp.y, input.get(round(p.x), round(p.y)));
      }
    }
    ElementMerge();
    
    /*
    for (int k = input.size-1; k>=0; --k)
      input.a[k] = 1.0f;
    */
    input.Normalize();
    for (i = 0; i<inputfilterrepeat; ++i)
      input.Convolve3x3(inputfilterkernel);
    
    flic.SetIterator(new ElementIterator());
    flic.Compute();
    /*
    p = new Vec2(0,0);
    otof.V(p);
    ftoi.V(p);
    output.Copy(input, round(p.x), round(p.y));
    */
  }
  /** Evolves the DFLIC state by a time step "dt". The integrator takes
   *   time steps at most "maxStep" in size. The elements that comprise
   *   the input image to FLIC are evolved according to "dfield" using 
   *   simple Euler integration. Other integration methods might yield 
   *   slightly more accurate results, but added computational cost does
   *   not produce significant benefits. */
  public void Evolve(double dt, double maxStep){
    while (dt>0) {
      double step = (dt>maxStep) ? maxStep : dt;
      dt -= step;
      ElementIterInit();
      int i;
      Vec2 v = new Vec2();
      while ((i = ElementIterNext())>=0) {
        dfield.get(elp, v);
        elp.AddScaled(v, step);
        elx[i] = (float)elp.x;
        ely[i] = (float)elp.y;
      }
    }
  }
  /** Evolves the DFLIC state by a time step "dt". The elements that comprise
   *   the input image to FLIC are evolved according to "dfield" using 
   *   simple Euler integration. Other integration methods might yield 
   *   slightly more accurate results, but added computational cost does
   *   not produce significant benefits. */ 
  public void Evolve(double dt){
    Evolve(dt, dt);
  }

  private int sqr(int x){
    return x*x;
  }
  
  private int ceil(double x){
    return (int)Math.ceil(x);
  }
  
  private int round(double x){
    return (int)Math.floor(x + 0.5);
  }
  
  private void InitializeInput(){
    int width = output.width + 2*ceil(0.5*ostreamlen);
    int height = output.height + 2*ceil(0.5*ostreamlen);
    if ((input==null) || (input.width!=width) || (input.height!=height)) {
      input = new AccumImage(width, height);
      ftoi = ftoo.translate(new Vec2(ceil(0.5*ostreamlen), ceil(0.5*ostreamlen)));
      itof = ftoi.invert();
    } else
      input.Clear();
    flic.SetInput(input);
    flic.SetFtoITransform(ftoi);
  }
  
  private int elmax, elempty;
  private float[] elx, ely, elc;
  private int[] elnext, eltemp;
  private int eliter, eliterlast;
  private Vec2 elp = new Vec2();
  private int[] elcurrent, elnew;
  
  private void InitializeElements()
  {
    elmax = 0;
    elempty = -1;
    elx = ely = elc = null;
    elnext = eltemp = null;
    elcurrent = new int[2];
    elcurrent[0] = elcurrent[1] = -1;
    elnew = new int[2];
    elnew[0] = elnew[1] = -1;
  }
  
  private void InitializeElements(int max)
  {
    if (max<=elmax)
      return;
    
    float[] tempf = new float[max];
    if (elx!=null)
      System.arraycopy(elx, 0, tempf, 0, elmax);
    elx = tempf;
    
    tempf = new float[max];
    if (ely!=null)
      System.arraycopy(ely, 0, tempf, 0, elmax);
    ely = tempf;
    
    tempf = new float[max];
    if (elc!=null)
      System.arraycopy(elc, 0, tempf, 0, elmax);
    elc = tempf;
    
    int[] tempi = new int[max];
    if (elnext!=null)
      System.arraycopy(elnext, 0, tempi, 0, elmax);
    elnext = tempi;
    
    for (int i = elmax; i<(max-1); ++i)
      elnext[i] = i+1;
    elnext[max-1] = elempty;
    elempty = elmax;
    
    eltemp = new int[max];
    
    elmax = max;
  }
  
  private void ElementIterInit()
  {
    eliter = -1;
    eliterlast = -1;
  }
  
  private int ElementIterNext()
  {
    eliterlast = eliter;
    if (eliter<0)
      eliter = elcurrent[0];
    else
      eliter = elnext[eliter];
    if (eliter>=0) {
      elp.x = elx[eliter];
      elp.y = ely[eliter];
    }
    return eliter;
  }
  
  private void ElementIterDelete()
  {
    if (eliterlast<0) {
      elcurrent[0] = elnext[eliter];
    } else
      elnext[eliterlast] = elnext[eliter];
    if (elnext[eliter]<0)
      elcurrent[1] = eliterlast;
    elnext[eliter] = elempty;
    elempty = eliter;
    eliter = eliterlast;
  }
  
  private int ElementAdd(double x, double y, double c)
  {
    int i = elempty;
    if (i<0)
      throw new RuntimeException("DFLIC.ElementAdd: No more free elements!");
    elempty = elnext[i];
    elx[i] = (float)x;
    ely[i] = (float)y;
    elc[i] = (float)c;
    elnext[i] = -1;
    if (elnew[1]<0)
      elnew[0] = elnew[1] = i;
    else {
      elnext[elnew[1]] = i;
      elnew[1] = i;
    }
    return i;
  }
  
  private void ElementMerge()
  {
    int n = 0, j = elnew[0];
    for ( ; j>=0; ++n, j = elnext[j])
      eltemp[n] = j;
    if (n==0)
      return;
    for (int i = 0; i<n; ++i) {
      int r = random.nextInt(n);
      int temp = eltemp[i];
      eltemp[i] = eltemp[r];
      eltemp[r] = temp;
    }
    elnext[eltemp[n-1]] = -1;
    for (int i = n-2; i>=0; --i)
      elnext[eltemp[i]] = eltemp[i+1];
    if (elcurrent[1]<0) {
      elcurrent[0] = eltemp[0];
      elcurrent[1] = eltemp[n-1];
    } else {
      elnext[elcurrent[1]] = eltemp[0];
      elcurrent[1] = eltemp[n-1];
    }
    elnew[0] = elnew[1] = -1;
  }
  
  private class ElementIterator implements Vec2Iterator {
    public ElementIterator()
    {
      eliter = -1;
    }
    
    public Vec2 next()
    {
      if (eliter<0)
        eliter = elcurrent[0];
      else
        eliter = elnext[eliter];
      if (eliter<0)
        return null;
      elp.x = elx[eliter];
      elp.y = ely[eliter];
      return ftoo.V(elp);
    }
  }
  
}
