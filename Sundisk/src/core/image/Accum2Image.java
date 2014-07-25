package core.image;
/** Stores a float image with alpha^2
* = < width, height, float-buffer, alpha-buffer, minalpha, coverage >.
*
* An Accum2Image extends AccumImage so that the samples contribute
* in a such a way that the output variance is normalized. The float channel
* stores the sample sum with the alpha premultiplied, and the alpha channel
* stores the sum of the squares of the alpha amounts. This way,
* computing the final sample value is simple:
*
* sample = Sum(s[i]*sa[i])/Sqrt(Sum(sa[i]^2))
*        = f/Sqrt(a)
*
* If the samples accumulated have a Gaussian distribution, the final sample
* will have the same distribution.
* @author Andreas Sundquist
* @version 1.0 
*/
public class Accum2Image extends AccumImage {
  
	  /** Constructs a new Accum2Image =
	   *   < width, height, zero-buffer, zero-buffer, 1.0, 0 > */ 
  public Accum2Image(int width, int height){
    super(width, height);
  }
  /** Sets the samples in "this" to zero, with an alpha of 1.0 */
  public void SetZero(){
    SetZero();
  }
  /** Clears both the scalar and alpha components in "this" */
  public void Clear(){
    Clear();
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
  /** Divides out the alpha component in "this", renormalizing it to 1.0.
   * Since the alpha channel stores the sum of the squares of the alphas,
   * we actually divide out the square root of the alpha value. */ 
  public void Normalize(){
    for (int k = 0; k<size; ++k) {
      if (a[k]!=0.0f)
        f[k] /= Math.sqrt(a[k]);
      else
        f[k] = 0.0f;
      a[k] = 1.0f;
    }
    if (1.0>=minalpha)
      coverage = size;
    else
      coverage = 0;
  }
  /** Normalize the alpha value of a particular pixel, dividing out the square
   * root of the alpha channel. */ 
  public void Normalize(int x, int y){
    int k = offset[y] + x;
    if (a[k]>=minalpha)
      --coverage;
    if (a[k]!=0.0f)
      f[k] /= Math.sqrt(a[k]);
    else
      f[k] = 0.0f;
    a[k] = 1.0f;
    if (a[k]>=minalpha)
      ++coverage;
  }
  /** Returns: the value at (x, y) with the alpha divided out.
   * Requires: 0<=x<width and 0<=y<height */  
  public double get(int x, int y){
    if (inBounds(x,y)) {
      int k = offset[y] + x;
      if (a[k]!=0.0f)
        return (double)(f[k]/Math.sqrt(a[k]));
      else
        return 0.0;
    } else
      throw new DomainException();
  }
  /** Sets the scalar and alpha value at (x, y) to (s, sa^2).
   * If (x, y) is out-of-bounds, this has no effect. */ 
  public void Set(int x, int y, double s, double sa) {
    if (inBounds(x,y)) {
      int k = offset[y] + x;
      if (a[k]>=minalpha)
        --coverage;
      f[k] = (float)s;
      a[k] = (float)(sa*sa);
      if (a[k]>=minalpha)
        ++coverage;
    }
  }
  /** Adds "s" and "sa"^2 to the scalar and alpha values at (x, y) if it is
   *   in-bounds. */ 
  public void Accumulate(int x, int y, double s, double sa)

  {
    if (inBounds(x,y)) {
      int k = offset[y] + x;
      if (a[k]>=minalpha)
        --coverage;
      f[k] += (float)s;
      a[k] += (float)(sa*sa);
      if (a[k]>=minalpha)
        ++coverage;
    }
  }
  
}