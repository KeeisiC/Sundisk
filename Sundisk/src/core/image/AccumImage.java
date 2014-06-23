package core.image;

import core.math.Vec2;

/** Stores a monochrome float image with alpha
 * = < width, height, float-buffer, alpha-buffer, minalpha, coverage >.
 *
 * An AccumImage extends ScalarImage to include an alpha component for every
 * scalar value. The alpha values are pre-multiplied into the scalar, so
 * to obtain the scalar value at a pixel, the alpha must be divided out of
 * it. This sort of image is useful for accumulation operations.
 *
 * Each AccumImage also automatically maintains information about the
 * coverage of the image. Every time a pixel surpasses the "minalpha"
 * requirement, "coverage" is incremented to indicated the total 
 * count of such pixels.
 * @author Andreas Sundquist
 * @version 1.0
 */

public class AccumImage extends ScalarImage {
	
  /** a[] stores all the alpha values for the corresponding array f[] */
  public float[] a;
  /** minalpha defines the minimum alpha required for a pixel to be "covered" */
  public double minalpha;
  /** coverage indicates the number of pixels whose alpha exceeds "minalpha" */
  public int coverage;
  
  /** Constructs a new AccumImage = 
   *   < width, height, zero-buffer, zero-buffer, 1.0, 0 > */
  public AccumImage(int width, int height){
    super(width, height);
    a = new float[size];
    for (int k = 0; k<size; ++k)
      a[k] = 0.0f;
    minalpha = 1.0;
    coverage = 0;
  }
  /** Sets the samples in "this" to zero, with an alpha of 1.0 */
  public void SetZero(){
    super.SetZero();
    for (int k = 0; k<size; ++k)
      a[k] = 1.0f;
    if (1.0>=minalpha)
      coverage = size;
    else
      coverage = 0;
  }
  /** Clears both the scalar and alpha components in "this" */ 
  public void Clear(){
    super.Clear();
    for (int k = 0; k<size; ++k)
      a[k] = 0.0f;
    coverage = 0;
  }
  /** Sets the scalar component of "this" to the alpha component */  
  public void SetAlpha(){
    for (int k = 0; k<size; ++k) {
      f[k] = a[k];
      a[k] = 1.0f;
    }
    if (1.0>=minalpha)
      coverage = size;
    else
      coverage = 0;
  }
  /** Copies the scalar and alpha values from the sub-window of "image"
   *   starting at (xorigin, yorigin) to "this".
   * Requires: the sub-window fits inside "image" */
  public void Copy(AccumImage image, int xorigin, int yorigin){
    if (((xorigin+width)>image.width) || ((yorigin+height)>image.height))
      throw new RuntimeException("AccumImage.Copy: Window too large");
    
    for (int j = 0, k = 0; j<height; ++j)
      for (int i = 0, l = image.offset[j+yorigin] + xorigin; i<width; ++i, ++k, ++l) {
        f[k] = image.f[l];
        a[k] = image.a[l];
      }
  }
  /** Copies the scalar and alpha values from the top-left corner of "image"
   *   to "this"
   * Requires: "image" is at least as large as "this" */ 
  public void Copy(AccumImage image)

  {
    Copy(image, 0, 0);
  }
  /** Divides out the alpha component in "this", renormalizing it to 1.0 */ 
  public void Normalize(){
    for (int k = 0; k<size; ++k) {
      if (a[k]!=0.0f)
        f[k] /= a[k];
      else
        f[k] = 0.0f;
      a[k] = 1.0f;
    }
    if (1.0>=minalpha)
      coverage = size;
    else
      coverage = 0;
  }
  /** Divides out the alpha component at coordinate (x, y) in "this", 
   *   renormalizing it to 1.0
   * Requires: 0<=x<width, 0<=y<height */ 
  public void Normalize(int x, int y){
    int k = offset[y] + x;
    if (a[k]>=minalpha)
      --coverage;
    if (a[k]!=0.0f)
      f[k] /= a[k];
    else
      f[k] = 0.0f;
    a[k] = 1.0f;
    if (a[k]>=minalpha)
      ++coverage;
  }
  /** Returns: the value at (x, y) with the alpha divided out
   * Requires: 0<=x<width and 0<=y<height */  
  public double get(int x, int y){
    if (inBounds(x,y)) {
      int k = offset[y] + x;
      if (a[k]!=0.0f)
        return (double)(f[k]/a[k]);
      else
        return 0.0;
    } else
      throw new DomainException();
  }
  /** Returns: the alpha value at (x, y)
   * Requires: 0<=x<width and 0<=y<height */  
  public double getAlpha(int x, int y){
    if (inBounds(x,y))
      return a[offset[y] + x];
    else
      return 0.0;
  }
  /** Sets the scalar and alpha value at (x, y) to (s, sa).
   * If (x, y) is out-of-bounds, this has no effect. */ 
  public void Set(int x, int y, double s, double sa){
    if (inBounds(x,y)) {
      int k = offset[y] + x;
      if (a[k]>=minalpha)
        --coverage;
      f[k] = (float)s;
      a[k] = (float)sa;
      if (a[k]>=minalpha)
        ++coverage;
    }
  }
  /** Adds "s" to the scalar value at (x, y) and increments the corresponding
   *   alpha value. If (x, y) is out-of-bounds, it has no effect. */ 
  public void Accumulate(int x, int y, double s) {
    if (inBounds(x,y)) {
      int k = offset[y] + x;
      if (a[k]>=minalpha)
        --coverage;
      f[k] += (float)s;
      a[k] += 1.0f;
      if (a[k]>=minalpha)
        ++coverage;
    }
  }
  /** Adds "s" and "sa" to the scalar and alpha values at (x, y) if it is
   *   in-bounds. */  
  public void Accumulate(int x, int y, double s, double sa){
    if (inBounds(x,y)) {
      int k = offset[y] + x;
      if (a[k]>=minalpha)
        --coverage;
      f[k] += (float)s;
      a[k] += (float)sa;
      if (a[k]>=minalpha)
        ++coverage;
    }
  }
  /** Bilinearly accumulates "s" to the four integer grid points surrounding
   *   the continuous coordinate (x, y), weighting the alpha accumulation
   *   bilinearly as well. */ 
  public void AccumulateBilinear(double x, double y, double s) {
    double xpf = Math.floor(x);
    int xi = (int)xpf;
    double xf = x - xpf;
    
    double ypf = Math.floor(y);
    int yi = (int)ypf;
    double yf = y - ypf;
    
    double b;
    b = (1.0-xf)*(1.0-yf);
    Accumulate(xi, yi, s*b, b);
    b = xf*(1.0-yf);
    Accumulate(xi+1, yi, s*b, b);
    b = (1.0-xf)*yf;
    Accumulate(xi, yi+1, s*b, b);
    b = xf*yf;
    Accumulate(xi+1, yi+1, s*b, b);
  }
  /** Bilinearly accumulates "s" to the four integer grid points surrounding
   *   the continuous coordinate "v", weighting the alpha accumulation
   *   bilinearly as well. */
  public void AccumulateBilinear(Vec2 v, double s){
    AccumulateBilinear(v.x, v.y, s);
  }
  /** Returns: true if an scalar or alpha value in "this" is invalid */
  public boolean hasSingularity(){
    if (hasSingularity())
      return true;
    for (int k = 0; k<size; ++k)
      if (Float.isInfinite(a[k]) || Float.isNaN(a[k]))
        return true;
    return false;
  }
}