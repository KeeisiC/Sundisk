package core.field;

import java.math.*;

import core.math.Vec2;

/** 
 *   Precomputed Vec2Field on a grid with bilinear interpolation.
 *
 * A Vec2FieldCache wraps around a given Vec2Field, precomputing the
 * field on a specified grid in order to speed up later computation.
 * The continuous values between grids are filled in by bilinear
 * interpolation.
 *
 * If get() is called with a point outside the region that is cached, 
 * it will fall through and call get() for the original field.
 */
public class Vec2FieldCache extends Vec2Field {
  private Vec2Field field;
  private Vec2 p1, p2;
  private int width, height, size;
  private float[] x, y;
  private int[] offset;
  private double mx, my;
  private double sx, sy, ox, oy;
  
  /** Constructs a new Vec2FieldCache that approximates "field" inside the 
   * rectangular region whose corners are given by "p1" and "p2". The number
   * of horizontal and vertical grid points is given by "width" and "height".
   * Note that Compute() must be called before any call to get(). */
  public Vec2FieldCache(Vec2Field field, Vec2 p1, Vec2 p2, int width, int height){
    this.field = field;
    this.p1 = p1;
    this.p2 = p2;
    this.width = width;
    this.height = height;
    mx = width - 1.0;
    my = height - 1.0;
    sx = mx/(p2.x - p1.x);
    sy = my/(p2.y - p1.y);
    ox = -p1.x;
    oy = -p1.y;
    
    size = width*height;
    x = new float[size];
    y = new float[size];
    offset = new int[height];
  }
  /** Precomputes the field at every point of the grid. This must be called
   * before any call to get(). */ 
  public void Compute(){
    double isx = 1.0/sx;
    double isy = 1.0/sy;
    Vec2 p = new Vec2();
    Vec2 f = new Vec2();
    for (int j = 0, k = 0; j<height; ++j) {
      offset[j] = k;
      for (int i = 0; i<width; ++i, ++k) {
        p.Set(p1.x + isx*i, p1.y + isy*j);
        field.get(p, f);
        x[k] = (float)f.x;
        y[k] = (float)f.y;
      }
    }
  }
  /** Sets "f" to an approximate value of the field at "p". Inside the
   *   cached region, the value is bilinearly interpolated between the four
   *   surrounding grid points, while outside, the original field function
   *   is queried. "p" is not modified
   * Returns: resulting "f" */
  public Vec2 get(Vec2 p, Vec2 f){
    double px = (p.x + ox)*sx;
    double py = (p.y + oy)*sy;
    if ((px<0.0) || (py<0.0) || (px>mx) || (py>my))
      return field.get(p, f);
    
    int ix, iy;
    double fx, fy;
    if (px==mx) {
      ix = width - 2;
      fx = 1.0;
    } else {
      double pxf = Math.floor(px);
      ix = (int)pxf;
      fx = px - pxf;
    }
    if (py==my) {
      iy = height - 2;
      fy = 1.0;
    } else {
      double pyf = Math.floor(py);
      iy = (int)pyf;
      fy = py - pyf;
    }
    
    int o = offset[iy] + ix;
    double x1 = x[o] + fx*(x[o+1] - x[o]);
    o += width;
    double x2 = x[o] + fx*(x[o+1] - x[o]);
    f.x = x1 + fy*(x2 - x1);
    
    o -= width;
    x1 = y[o] + fy*(y[o+1] - y[o]);
    o += width;
    x2 = y[o] + fy*(y[o+1] - y[o]);
    f.y = x1 + fy*(x2 - x1);
    
    return f;
  }
  
}
