package core.image;

import java.util.*;

import core.field.Vec2Field;
import core.math.Vec2;
import core.math.Vec2Transform;
/** Stores a monochrome image of floats
* = < width, height, float-buffer >.
*
* A ScalarImage stores a rectangular array of scalar values.
* The image"s width and height are specified upon construction, and
* a float-buffer is created of size width*height, storing the data
* in left-right, top-down scanline order. Though the scalar data is
* stored internally as floats to save space, they are accessed as
* doubles for compatibility.
*
* The ScalarImage can also be viewed as a continuous field over a
* rectangle, with the value at integer coordinates equal to the scalar
* values, and the values at fractional coordinates interpolated between
* them. Since the values are interpolated, the effective size of the
* field becomes (width-1) by (height-1).
* @author Andreas Sundquist
* @version 1.0
*/
public class ScalarImage {
  /** width, height store the dimensions of the image.
   * size = width*height, the number of scalar values */
  public final int width, height, size;
  /** f[] stores all the scalar values in scanline order */
  public float[] f;
  /** offset[] indexes the left-most pixel of each scanline in f[]
   * The index of pixel (x,y) is therefore offset[y]+x */
  public final int[] offset;
  /** Constructs a new ScalarImage = < width, height, zero-buffer > */  
  public ScalarImage(int width, int height){
    this.width = width;
    this.height = height;
    size = width*height;
    
    f = new float[size];
    offset = new int[height];
    for (int j = 0; j<height; ++j)
      offset[j] = width*j;
    
    for (int k = 0; k<size; ++k)
      f[k] = 0.0f;
  }
  /** Sets all the scalar values to zero */  
  public void SetZero(){
    for (int k = 0; k<size; ++k)
      f[k] = 0.0f;
  }
  /** Clears all the scalar values to zero.
   * This is different from SetZero() in the derived classes AccumImage, etc */ 
  public void Clear(){
    for (int k = 0; k<size; ++k)
      f[k] = 0.0f;
  }
  /** Copies the scalar values from the sub-window of "image" starting at
   *   (xorigin, yorigin) to "this".
   * Requires: the sub-window fits inside "image" */ 
  public void Copy(ScalarImage image, int xorigin, int yorigin){
    if (((xorigin+width)>image.width) || ((yorigin+height)>image.height))
      throw new RuntimeException("ScalarImage.Copy: Window too large");
    
    for (int j = 0, k = 0; j<height; ++j)
      for (int i = 0, l = image.offset[j+yorigin] + xorigin; i<width; ++i, ++k, ++l)
        f[k] = image.f[l];
  }
  /** Copies the scalar values from the top-left corner of "image" to "this"
   * Requires: "image" is at least as large as "this" */ 
  public void Copy(ScalarImage image){
    Copy(image, 0, 0);
  }
  /** Rescales the scalar data in "image" to "this" using bilinear interpolation. */ 
  public void StretchBilinear(ScalarImage image){
    double xscale = (image.width-1.0)/(width-1.0);
    double yscale = (image.height-1.0)/(height-1.0);
    for (int j = height-1, k = size-1; j>=0; --j)
      for (int i = width-1; i>=0; --i, --k)
        f[k] = (float)image.getBilinear(i*xscale, j*yscale);
  }
  /** Returns: true if 0<=x<width and 0<=y<height, false otherwise */ 
  public boolean inBounds(int x, int y){
    return (x>=0) && (y>=0) && (x<width) && (y<height);
  }
  /** Returns: true if 0.0<=x<=(width-1.0) and 0.0<=y<=(height-1.0),
   *   false otherwise, the domain check for when "this" is viewed as
   *   a continuous field. */ 
  public boolean inBounds(double x, double y){
    return (x>=0.0) && (y>=0.0) && (x<=(width-1.0)) && (y<=(height-1.0));
  }
  /** Returns: true if "v" is within the domain of "this" when viewed as
   *   a continuous field. */ 
  public boolean inBounds(Vec2 v){
    return inBounds(v.x, v.y);
  }
  /** Returns: the scalar value at (x, y)
   * Requires: 0<=x<width and 0<=y<height */
  public double get(int x, int y){
    if (inBounds(x,y))
      return (double)f[offset[y] + x];
    else
      throw new DomainException("ScalarImage.get: DomainException at ("+x+","+y+")");
  }
  /** Sets the scalar value at (x, y) to "s". If it is outside the domain
   *   of "this", it has no effect. */  
  public void Set(int x, int y, double s){
    if (inBounds(x,y))
      f[offset[y] + x] = (float)s;
  }
  /** Adds "s" to the scalar value at (x, y). If it is outside the domain
   *   of "this", it has no effect. */
  public void Accumulate(int x, int y, double s){
    if (inBounds(x,y))
      f[offset[y] + x] += (float)s;
  }
  /** Returns: the bilinearly-interpolated value of the continuous field
   *   at (x, y).
   * Requires: (x, y) is inside the domain of the field */ 
  public double getBilinear(double x, double y){
    if (!inBounds(x, y))
      throw new DomainException("ScalarImage.getBilinear: DomainException at ("+x+","+y+")");
   
    int xi, yi;
    double xf, yf;
    if (x==(double)(width-1)) {
      xi = width-2;
      xf = 1.0;
    } else {
      double xpf = Math.floor(x);
      xi = (int)xpf;
      xf = x - xpf;
    }
    if (y==(double)(height-1)) {
      yi = height-2;
      yf = 1.0;
    } else {
      double ypf = Math.floor(y);
      yi = (int)ypf;
      yf = y - ypf;
    }
    
    double b1 = get(xi, yi);
    double b2 = get(xi+1, yi);
    double b3 = get(xi, yi+1);
    double b4 = get(xi+1, yi+1);
    
    double bb1 = b1 + xf*(b2 - b1);
    double bb2 = b3 + xf*(b4 - b3);
    
    return bb1 + yf*(bb2 - bb1);
  }
  /** Returns: the bilinearly-interpolated value of the continuous field
   *   at "v".
   * Requires: "v" is inside the domain of the field */ 
  public double getBilinear(Vec2 v){
    return getBilinear(v.x, v.y);
  }
  /** Bilinearly accumulates "s" to the four integer grid points surrounding
   *   the continuous coordinate (x, y). */  
  public void AccumulateBilinear(double x, double y, double s){
    double xpf = Math.floor(x);
    int xi = (int)xpf;
    double xf = x - xpf;
    
    double ypf = Math.floor(y);
    int yi = (int)ypf;
    double yf = y - ypf;
    
    double b;
    b = (1.0-xf)*(1.0-yf);
    Accumulate(xi, yi, s*b);
    b = xf*(1.0-yf);
    Accumulate(xi+1, yi, s*b);
    b = (1.0-xf)*yf;
    Accumulate(xi, yi+1, s*b);
    b = xf*yf;
    Accumulate(xi+1, yi+1, s*b);
  }
  /** Bilinearly accumulates "s" to the four integer grid points surrounding
   *   the continuous coordinate "v". */ 
  public void AccumulateBilinear(Vec2 v, double s){
    AccumulateBilinear(v.x, v.y, s);
  }
  /** Rescales all the scalar values in "this" by the rule:
   *   f" = f*scale + add */ 
  public void Rescale(double scale, double add){
    for (int k = 0; k<size; ++k)
      f[k] = (float)(f[k]*scale + add);
  }
  /** Transforms all the scalar values in "this" by the rule:
   *   f" = f^exp */
  public void Power(double exp) {
    for (int k = 0; k<size; ++k)
      f[k] = (float)Math.pow(f[k], exp);
  }
  /** Performs a convolution on "this" with a 3x3 kernel and scalar offset. 
   * kernel is of type double[10], and the convolution computed is of the form:
   * out(i,j) = c0 + c1*out(i-1,j-1) + c2*out( i ,j-1) + c3*out(i+1,j-1) +
   * c4*out(i-1, j ) + c5*out( i , j ) + c6*out(i+1, j ) +
   * c7*out(i-1,j+1) + c8*out( i ,j+1) + c9*out(i+1,j+1),
   * where ci = kernel[i] */
  public void Convolve3x3(double[] kernel){ 
    float[] out = new float[size];
    for (int k = 0; k<size; ++k)
      out[k] = (float)kernel[0];
    
    for (int j = height-1, k = width+1, l = 0; j>0; --j, ++k, ++l)
      for (int i = width-1; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[1]*f[l];
    for (int j = height-1, k = width, l = 0; j>0; --j)
      for (int i = width; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[2]*f[l];
    for (int j = height-1, k = width, l = 1; j>0; --j, ++k, ++l)
      for (int i = width-1; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[3]*f[l];
    for (int j = height, k = 1, l = 0; j>0; --j, ++k, ++l)
      for (int i = width-1; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[4]*f[l];
    for (int j = height, k = 0, l = 0; j>0; --j)
      for (int i = width; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[5]*f[l];
    for (int j = height, k = 0, l = 1; j>0; --j, ++k, ++l)
      for (int i = width-1; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[6]*f[l];
    for (int j = height-1, k = 1, l = width; j>0; --j, ++k, ++l)
      for (int i = width-1; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[7]*f[l];
    for (int j = height-1, k = 0, l = width; j>0; --j)
      for (int i = width; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[8]*f[l];
    for (int j = height-1, k = 0, l = width+1; j>0; --j, ++k, ++l)
      for (int i = width-1; i>0; --i, ++k, ++l)
        out[k] += (float)kernel[9]*f[l];

    for (int k = 0; k<size; ++k)
      f[k] = out[k];
    out = null;
  }
  /** Returns: true if any scalar value in "this" is invalid */ 
  public boolean hasSingularity(){
    for (int k = 0; k<size; ++k)
      if (Float.isInfinite(f[k]) || Float.isNaN(f[k]))
        return true;
    return false;
  }
  /** Sets all the scalars in "this" to random values uniformly distributed
   *   over (0.0, 1.0) using the pseudo-random generator "random". */
  public void SetRandom(Random random){
    SetZero();
    for (int k = 0; k<size; ++k)
      f[k] = random.nextFloat();
  }
  /** Sets all the scalars in "this" to random values uniformly distributed
   *   over (0.0, 1.0). */ 
  public void SetRandom() {
    SetRandom(new Random());
  }
  
  public void Vec2FieldMagnitude(Vec2Field field, Vec2Transform ftoi){
    Vec2Transform itof = ftoi.invert();
    Vec2 p = new Vec2(), v = new Vec2();
    
    for (int j = 0, k = 0; j<height; ++j)
      for (int i = 0; i<width; ++i, ++k) {
        p.x = i;
        p.y = j;
        field.get(itof.V(p), v);
        f[k] = (float)Math.sqrt(v.x*v.x + v.y*v.y);
      }
  }
  
  public void Vec2FieldZero(Vec2Field field, Vec2Transform ftoi){
    Vec2Transform itof = ftoi.invert();
    Vec2 p = new Vec2(), v = new Vec2();
    
    for (int j = 0, k = 0; j<height; ++j)
      for (int i = 0; i<width; ++i, ++k) {
        p.x = i;
        p.y = j;
        field.get(itof.V(p), v);
        if ((v.x==0.0) && (v.y==0.0))
          f[k] = 1.0f;
        else
          f[k] = 0.0f;
      }
  }
  
  public void Modulate(ScalarImage image){
    for (int k = 0; k<size; ++k)
      f[k] *= image.f[k];
  }
  
  public void Add(ScalarImage image){
    for (int k = 0; k<size; ++k)
      f[k] += image.f[k];
  }
  
  public void Clamp(double min, double max){
    for (int k = 0; k<size; ++k) {
      if (f[k]<min)
        f[k] = (float)min;
      else if (f[k]>max)
        f[k] = (float)max;
    }
  }
  
}