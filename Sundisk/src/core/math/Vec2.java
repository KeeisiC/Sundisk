package core.math;

import java.math.*;
import javax.vecmath.*;

/** A 2-dimensional vector
 * = < (x, y) >.
 *
 * This class stores and operates on a 2-dimensional vector.
 * Methods that begin with a capital letter modify "this", while
 * methods that begin with a lowercase letter create a new Vec2
 * to store the result.
 */
public class Vec2 {

  
  /** x and y are the components of this vector */
  public double x,y;
  /** Constructs a new Vec2 = (0, 0) */ 
  public Vec2()

  {
    x = y = 0.0;
  }
  /** Constructs a new Vec2 = "v" */
  public Vec2(Vec2 v)

  {
    x = v.x;
    y = v.y;
  }
  /** Constructs a new Vec2 = (x, y) */  
  public Vec2(double x, double y)
 
  {
    this.x = x;
    this.y = y;
  }
  /** Constructs a new Vec2 = "v"
   * Requires: "v" has dimension 2 */
  public Vec2(Vec v)

  {
    if (v.dim!=2)
      throw new RuntimeException("Vec2.Vec2(Vec): Dimension mismatch!");
    x = v.x[0];
    y = v.x[1];
  }
  /** Returns a new Vec2 = (0, 0) */
  public Vec2 getNew()
 
  {
    return new Vec2();
  }
  /** Returns a copy of "this" */
  public Vec2 copy()
 
  {
    return new Vec2(this);
  }
  
  /** do not modify Zero = the zero vector */
  public static Vec2 Zero = new Vec2(0.0, 0.0);
  /** do not modify Xhat = the unit x-axis */
  public static Vec2 Xhat = new Vec2(1.0, 0.0);
  /** do not modify Yhat = the unit y-axis */
  public static Vec2 Yhat = new Vec2(0.0, 1.0);
  /** do not modify Cartesian an array of the cartesian axes */
  public static Vec2[] Cartesian = {Xhat, Yhat};
  /** do not modify Invalid = an invalid Vec */
  public static Vec2 Invalid = new Vec2(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
  /** do not modify tolerance the per-component zero-value maximum */
  public static double tolerance = 1.0e-12;
  /** Sets the value of "this" to that of "v"
   * Returns: resulting "this" */
  public Vec2 Set(Vec2 v)
 
  {
    x = v.x;
    y = v.y;
    return this;
  }
  /** Sets "this" to (x, y)
   * Returns: resulting "this" */ 
  public Vec2 Set(double x, double y)

  {
    this.x = x;
    this.y = y;
    return this;
  }
  /** Sets "this" to "v"
   * Returns: resulting "this"
   * Requires: "v" has dimension 2 */ 
  public Vec2 Set(Vec v)

  {
    if (v.dim!=2)
      throw new RuntimeException("Vec2.Set(Vec): Dimension mismatch!");
    x = v.x[0];
    y = v.x[1];
    return this;
  }
  /** Sets "this" to zero
   * Returns: resulting "this" */ 
  public Vec2 SetZero()

  {
    x = y = 0.0;
    return this;
  }
  /** Returns: true if "this" is zero, false otherwise */ 
  public boolean isZero()

  {
    return (Math.abs(x)<tolerance) && (Math.abs(y)<tolerance);
  }
  /** Returns: true if "this" equals "v", false otherwise */ 
  public boolean equals(Vec2 v)

  {
    return (Math.abs(x-v.x)<tolerance) && (Math.abs(y-v.y)<tolerance);
  }
  /** Adds "v" to "this"
   * Returns: resulting "this" */ 
  public Vec2 Add(Vec2 v)

  {
    x += v.x;
    y += v.y;
    return this;
  }
  /** Adds (x, y) to "this"
   * Returns: resulting "this" */
  public Vec2 Add(double x, double y)

  {
    this.x += x;
    this.y += y;
    return this;
  }
  /** Returns: a new Vec2 that is the sum of "this" and "v" */ 
  public Vec2 add(Vec2 v)

  {
    return new Vec2(x + v.x, y + v.y);
  }
  /** Returns: a new Vec2 that is the sum of "this" and (x, y) */
  public Vec2 add(double x, double y)

  {
    return new Vec2(this.x + x, this.y + y);
  }
  /** Adds "s*v" to "this"
   * Returns: resulting "this" */
  public Vec2 AddScaled(Vec2 v, double s)

  {
    x += s*v.x;
    y += s*v.y;
    return this;
  }
  /** Adds "s * (x, y)" to "this"
   * Returns: resulting "this" */
  public Vec2 AddScaled(double x, double y, double s)

  {
    this.x += s*x;
    this.y += s*y;
    return this;
  }
  /** Returns: a new Vec2 that is the sum of "this" and "s*v" */
  public Vec2 addscaled(Vec2 v, double s)

  {
    return new Vec2(x + s*v.x, y + s*v.y);
  }
  /** Returns: a new Vec2 that is the sum of "this" and "s * (x, y)" */
  public Vec2 addscaled(double x, double y, double s)

  {
    return new Vec2(this.x + s*x, this.y + s*y);
  }
  /** Subtracts "v" from "this"
   * Returns: resulting "this" */ 
  public Vec2 Sub(Vec2 v)

  {
    x -= v.x;
    y -= v.y;
    return this;
  }
  /** Subtracts (x, y) from "this"
   * Returns: resulting "this" */ 
  public Vec2 Sub(double x, double y)

  {
    this.x -= x;
    this.y -= y;
    return this;
  }
  /** Returns: a new Vec2 that is the difference between "this" and "v" */
  public Vec2 sub(Vec2 v)

  {
    return new Vec2(x - v.x, y - v.y);
  }
  /** Returns: a new Vec2 that is the difference between "this" and (x, y) */
  public Vec2 sub(double x, double y)
 
  {
    return new Vec2(this.x - x, this.y - y);
  }
  /** Negates "this"
   * Returns: resulting "this" */
  public Vec2 Neg()

  {
    x = -x;
    y = -y;
    return this;
  }
  /** Returns: a new Vec2 that is the opposite of "this" */ 
  public Vec2 neg()
 
  {
    return new Vec2(-x, -y);
  }
  /** Returns: the dot product of "this" and "v" */
  public double dot(Vec2 v)

  {
    return x*v.x + y*v.y;
  }
  /** Returns: the dot product of "this" and (x, y) */
  public double dot(double x, double y)

  {
    return this.x*x + this.y*y;
  }
  /** Returns: the magnitude of "this" */ 
  public double len()
 
  {
    return Math.sqrt(x*x + y*y);
  }
  /** Returns: the magnitude squared of "this" */ 
  public double len2()
 
  {
    return x*x + y*y;
  }
  /** Returns: the magnitude cubed of "this" */ 
  public double len3()

  {
    return Math.pow(x*x + y*y, 1.5);
  }
  /** Scales "this" by "s"
   * Returns: resulting "this" */ 
  public Vec2 Scale(double s)

  {
    x *= s;
    y *= s;
    return this;
  }
  /** Returns: a new Vec2 that is "s*this" */ 
  public Vec2 scale(double s)

  {
    return new Vec2(x*s, y*s);
  }
  /** Scales each component of "this" by the corresponding component of "v"
   * Returns: resulting "this" */  
  public Vec2 Scale(Vec2 v)

  {
    x *= v.x;
    y *= v.y;
    return this;
  }
  /** Returns: a new Vec2 that is "this" with its components scaled by "v" */ 
  public Vec2 scale(Vec2 v)

  {
    return new Vec2(x*v.x, y*v.y);
  }
  /** Rescales "this" to be of unit magnitude
   * Returns: resulting "this"
   * Requires: "this" is not zero */ 
  public Vec2 Unit()

  {
    double l = len();
    if (l==0.0)
      throw new RuntimeException("Vec2.Unit: Zero vector!");
    else
      Scale(1.0/l);
    return this;
  }
  /** Returns: a new Vec2 that is "this" scaled to unit magnitude
   * Requires: "this" is not zero */ 
  public Vec2 unit()

  {
    double l = len();
    if (l==0.0)
      throw new RuntimeException("Vec2.unit: Zero vector!");
    else
      return scale(1.0/l);
  }
  /** Returns: the magnitude of cross product of "this" and "b" */
  public double cross(Vec2 b)
 
  {
    return x*b.y - y*b.x;
  }
  /** Isolates the component of "this" parallel to "d"
   * Returns: resulting "this"
   * Requires: "d" is not zero */ 
  public Vec2 Para(Vec2 d)

  {
    Vec2 du = d.unit();
    du.Scale(du.dot(this));
    Set(du);
    return this;
  }
  /** Returns: a new Vec2 that is the component of "this" parallel to "d"
   * Requires: "d" is not zero */ 
  public Vec2 para(Vec2 d)

  {
    Vec2 du = d.unit();
    du.Scale(du.dot(this));
    return du;
  }
  /** Isolates the component of "this" parallel to "d"
   * Returns: resulting "this"
   * Requires: "d" has unit magnitude */
  public Vec2 ParaUnit(Vec2 d)
 
  {
    Set(d.scale(d.dot(this)));
    return this;
  }
  /** Returns: a new Vec2 that is the component of "this" parallel to "d"
   * Requires: "d" has unit magnitude */ 
  public Vec2 paraunit(Vec2 d)

  {
    return d.scale(d.dot(this));
  }
  /** Isolates the component of "this" perpendicular to "d"
   * Returns: resulting "this"
   * Requires: "d" is not zero */ 
  public Vec2 Perp(Vec2 d)

  {
    Set(this.sub(para(d)));
    return this;
  }
  /** Returns: a new Vec2 that is the component of "this" perpendicular to "d"
   * Requires: "d" is not zero */
  public Vec2 perp(Vec2 d)

  {
    return this.sub(para(d));
  }
  /** Isolates the component of "this" perpendicular to "d"
   * Returns: resulting "this"
   * Requires: "d" has unit magnitude */  
  public Vec2 PerpUnit(Vec2 d)

  {
    Set(this.sub(paraunit(d)));
    return this;
  }
  /** Returns: a new Vec2 that is the component of "this" perpendicular to "d"
   * Requires: "d" has unit magnitude */ 
  public Vec2 perpunit(Vec2 d)

  {
    return this.sub(paraunit(d));
  }
  /** Multiplies "this" to the left by "m"
   * Returns: resulting "this" */  
  public Vec2 Mul(Mat2 m)

  {
    double xp = m.a*x + m.b*y;
    double yp = m.c*x + m.d*y;
    x = xp;
    y = yp;
    return this;
  }
  /** Returns: a new Vec2 that is "m*this" */
  public Vec2 mul(Mat2 m)

  {
    return new Vec2(m.a*x + m.b*y, m.c*x + m.d*y);
  }
  /** Multiplies "this" to the right by "m"
   * Returns: resulting "this" */  
  public Vec2 RMul(Mat2 m)

  {
    double xp = m.a*x + m.c*y;
    double yp = m.b*x + m.d*y;
    x = xp;
    y = yp;
    return this;
  }
  /** Returns: a new Vec2 that is "this*m" */  
  public Vec2 rmul(Mat2 m)

  {
    return new Vec2(m.a*x + m.c*y, m.b*x + m.d*y);
  }
  /** Returns: a new Vec that is < 2, "this" > */ 
  public Vec toVec()

  {
    Vec v = new Vec(2);
    v.x[0] = x;
    v.x[1] = y;
    return v;
  }
  /** Returns: a new Vector2d that equals "this" */
  public Vector2d toVector2d()
 
  {
    return new Vector2d(x, y);
  }
  /** Returns: a new Vector2f that equals "this" */ 
  public Vector2f toVector2f()

  {
    return new Vector2f((float)x, (float)y);
  }
}