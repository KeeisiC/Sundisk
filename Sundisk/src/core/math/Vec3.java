package core.math;

import java.math.*;
import javax.vecmath.*;
/**  Store and operate on a 3-dimensional vector
* = < (x, y, z) >.
*
* This class stores and operates on a 3-dimensional vector.
* Methods that begin with a capital letter modify 'this', while
* methods that begin with a lowercase letter create a new Vec3
* to store the result.
* @author Andreas Sundquist
* @version 1.0
*/

public class Vec3 {
  

  /** x, y, and z are the components of this vector */  
  public double x,y,z;
  /** Constructs a new Vec3 = (0, 0, 0) */ 
  public Vec3()

  {
    x = y = z = 0.0;
  }
  /** Constructs a new Vec2 = 'v' */ 
  public Vec3(Vec3 v)

  {
    x = v.x;
    y = v.y;
    z = v.z;
  }
  /** Constructs a new Vec3 = (x, y, z) */ 
  public Vec3(double x, double y, double z)

  {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  /** Constructs a new Vec3 = 'v'
   * Requires: 'v' has dimension 3 */  
  public Vec3(Vec v)

  {
    if (v.dim!=3)
      throw new RuntimeException("Vec3.Vec3(Vec): Dimension mismatch!");
    x = v.x[0];
    y = v.x[1];
    z = v.x[2];
  }
  /** Returns a new Vec2 = (0, 0, 0) */ 
  public Vec3 getNew()

  {
    return new Vec3();
  }
  /** Returns a copy of 'this' */
  public Vec3 copy()

  {
    return new Vec3(this);
  }
  
    /** should not be modified Zero = the zero vector */
  public static Vec3 Zero = new Vec3(0.0, 0.0, 0.0);
  /** should not be modified Xhat = the unit x-axis */
  public static Vec3 Xhat = new Vec3(1.0, 0.0, 0.0);
  /** should not be modified Yhat = the unit x-axis */
  public static Vec3 Yhat = new Vec3(0.0, 1.0, 0.0);
  /** should not be modified Zhat = the unit x-axis */
  public static Vec3 Zhat = new Vec3(0.0, 0.0, 1.0);
  /** should not be modified Cartesian array of the cartesian axes */
  public static Vec3[] Cartesian = {Xhat, Yhat, Zhat};
  /** should not be modified Invalid = an invalid Vec3 */
  public static Vec3 Invalid = new Vec3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
  /** should not be modified tolerance the per-component zero-value maximum */
  public static double tolerance = 1.0e-12;
  /** Sets the value of 'this' to that of 'v'
   * Returns: resulting 'this' */
  public Vec3 Set(Vec3 v)

  {
    x = v.x;
    y = v.y;
    z = v.z;
    return this;
  }
  /** Sets 'this' to (x, y, z)
   * Returns: resulting 'this' */ 
  public Vec3 Set(double x, double y, double z)

  {
    this.x = x;
    this.y = y;
    this.z = z;
    return this;
  }
  /** Sets 'this' to 'v'
   * Returns: resulting 'this'
   * Requires: 'v' has dimension 3 */
  public Vec3 Set(Vec v)

  {
    if (v.dim!=3)
      throw new RuntimeException("Vec3.Set(Vec): Dimension mismatch!");
    x = v.x[0];
    y = v.x[1];
    z = v.x[2];
    return this;
  }
  /** Sets 'this' to zero
   * Returns: resulting 'this' */  
  public Vec3 SetZero()

  {
    x = y = z = 0.0;
    return this;
  }
  /** Returns: true if 'this' is zero, false otherwise */
  public boolean isZero()

  {
    return (Math.abs(x)<tolerance) && (Math.abs(y)<tolerance) && 
           (Math.abs(z)<tolerance);
  }
  /** Returns: true if 'this' equals 'v', false otherwise */
  public boolean equals(Vec3 v)

  {
    return (Math.abs(x-v.x)<tolerance) && (Math.abs(y-v.y)<tolerance) && 
           (Math.abs(z-v.z)<tolerance);
  }
  /** Adds 'v' to 'this'
   * Returns: resulting 'this' */  
  public Vec3 Add(Vec3 v)

  {
    x += v.x;
    y += v.y;
    z += v.z;
    return this;
  }
  /** Adds (x, y, z) to 'this'
   * Returns: resulting 'this' */
  public Vec3 Add(double x, double y, double z)

  {
    this.x += x;
    this.y += y;
    this.z += z;
    return this;
  }
  /** Returns: a new Vec3 that is the sum of 'this' and 'v' */
  public Vec3 add(Vec3 v)
 
  {
    return new Vec3(x + v.x, y + v.y, z + v.z);
  }
  /** Returns: a new Vec3 that is the sum of 'this' and (x, y, z) */ 
  public Vec3 add(double x, double y, double z)

  {
    return new Vec3(this.x + x, this.y + y, this.z + z);
  }
  /** Adds 's*v' to 'this'
   * Returns: resulting 'this' */ 
  public Vec3 AddScaled(Vec3 v, double s)

  {
    x += s*v.x;
    y += s*v.y;
    z += s*v.z;
    return this;
  }
  /** Adds 's * (x, y, z)' to 'this'
   * Returns: resulting 'this' */
  public Vec3 AddScaled(double x, double y, double z, double s)

  {
    this.x += s*x;
    this.y += s*y;
    this.z += s*z;
    return this;
  }
  /** Returns: a new Vec3 that is the sum of 'this' and 's*v' */ 
  public Vec3 addscaled(Vec3 v, double s)

  {
    return new Vec3(x + s*v.x, y + s*v.y, z + s*v.z);
  }
  /** Returns: a new Vecc3 that is the sum of 'this' and 's * (x, y, z)' */ 
  public Vec3 addscaled(double x, double y, double z, double s)
 
  {
    return new Vec3(this.x + s*x, this.y + s*y, this.z + s*z);
  }
  /** Subtracts 'v' from 'this'
   * Returns: resulting 'this' */ 
  public Vec3 Sub(Vec3 v)

  {
    x -= v.x;
    y -= v.y;
    z -= v.z;
    return this;
  }
  /** Subtracts (x, y, z) from 'this'
   * Returns: resulting 'this' */
  public Vec3 Sub(double x, double y, double z)

  {
    this.x -= x;
    this.y -= y;
    this.z -= z;
    return this;
  }
  /** Returns: a new Vec3 that is the difference between 'this' and 'v' */
  public Vec3 sub(Vec3 v)

  {
    return new Vec3(x - v.x, y - v.y, z - v.z);
  }
  /** Returns: a new Vec3 that is the difference between 'this' and (x, y, z) */   
  public Vec3 sub(double x, double y, double z)
 
  {
    return new Vec3(this.x - x, this.y - y, this.z - z);
  }
  /** Negates 'this'
   * Returns: resulting 'this' */ 
  public Vec3 Neg()
 
  {
    x = -x;
    y = -y;
    z = -z;
    return this;
  }
  /** Returns: a new Vec3 that is the opposite of 'this' */ 
  public Vec3 neg()

  {
    return new Vec3(-x, -y, -z);
  }
  /** Returns: the dot product of 'this' and 'v' */ 
  public double dot(Vec3 v)

  {
    return x*v.x + y*v.y + z*v.z;
  }
  /** Returns: the dot product of 'this' and (x, y, z) */
  public double dot(double x, double y, double z)

  {
    return this.x*x + this.y*y + this.z*z;
  }
  /** Returns: the magnitude of 'this' */ 
  public double len()

  {
    return Math.sqrt(x*x + y*y + z*z);
  }
  /** Returns: the magnitude squared of 'this' */
  public double len2()

  {
    return x*x + y*y + z*z;
  }
  /** Returns: the magnitude cubed of 'this' */  
  public double len3()

  {
    return Math.pow(x*x + y*y + z*z, 1.5);
  }
  /** Scales 'this' by 's'
   * Returns: resulting 'this' */ 
  public Vec3 Scale(double s)

  {
    x *= s;
    y *= s;
    z *= s;
    return this;
  }
  /** Returns: a new Vec3 that is 's*this' */
  public Vec3 scale(double s)
 
  {
    return new Vec3(x*s, y*s, z*s);
  }
  /** Scales each component of 'this' by the corresponding component of 'v'
   * Returns: resulting 'this' */ 
  public Vec3 Scale(Vec3 v)

  {
    x *= v.x;
    y *= v.y;
    z *= v.z;
    return this;
  }
  /** Returns: a new Vec3 that is 'this' with its components scaled by 'v' */
  public Vec3 scale(Vec3 v)
  
  {
    return new Vec3(x*v.x, y*v.y, z*v.z);
  }
  /** Rescales 'this' to be of unit magnitude
   * Returns: resulting 'this'
   * Requires: 'this' is not zero */ 
  public Vec3 Unit()

  {
    double l = len();
    if (l==0.0)
      throw new RuntimeException("Vec3.Unit: Zero vector!");
    else
      Scale(1.0/l);
    return this;
  }
  /** Returns: a new Vec3 that is 'this' scaled to unit magnitude 
   * Requires: 'this' is not zero */ 
  public Vec3 unit()

  {
    double l = len();
    if (l==0.0)
      throw new RuntimeException("Vec3.unit: Zero vector!");
    else
      return scale(1.0/l);
  }
  /** Sets 'this' to be the cross product of 'this' and 'b'
   * Returns: resulting 'this' */  
  public Vec3 Cross(Vec3 b)

  {
    double x = this.y*b.z - this.z*b.y;
    double y = this.z*b.x - this.x*b.z;
    double z = this.x*b.y - this.y*b.x;
    this.x = x;
    this.y = y;
    this.z = z;
    return this;
  }
  /** Sets 'this' to be the cross product of 'a' and 'b'
   * Returns: resulting 'this' */ 
  public Vec3 Cross(Vec3 a, Vec3 b)

  {
    x = a.y*b.z - a.z*b.y;
    y = a.z*b.x - a.x*b.z;
    z = a.x*b.y - a.y*b.x;
    return this;
  }
  /** Returns: a new Vec3 that is the cross product of 'this' and 'b' */
  public Vec3 cross(Vec3 b)

  {
    return new Vec3(y*b.z - z*b.y, z*b.x - x*b.z, x*b.y - y*b.x);
  }
  /** Isolates the component of 'this' parallel to 'd'
   * Returns: resulting 'this'
   * Requires: 'd' is not zero */ 
  public Vec3 Para(Vec3 d)

  {
    Vec3 du = d.unit();
    du.Scale(du.dot(this));
    Set(du);
    return this;
  }
  /** Returns: a new Vec3 that is the component of 'this' parallel to 'd'
   * Requires: 'd' is not zero */ 
  public Vec3 para(Vec3 d)

  {
    Vec3 du = d.unit();
    du.Scale(du.dot(this));
    return du;
  }
  /** Isolates the component of 'this' parallel to 'd'
   * Returns: resulting 'this'
   * Requires: 'd' has unit magnitude */ 
  public Vec3 ParaUnit(Vec3 d)

  {
    Set(d.scale(d.dot(this)));
    return this;
  }
  /** Returns: a new Vec3 that is the component of 'this' parallel to 'd'
   * Requires: 'd' has unit magnitude */ 
  public Vec3 paraunit(Vec3 d)

  {
    return d.scale(d.dot(this));
  }
  /** Isolates the component of 'this' perpendicular to 'd'
   * Returns: resulting 'this'
   * Requires: 'd' is not zero */ 
  public Vec3 Perp(Vec3 d)

  {
    Set(this.sub(para(d)));
    return this;
  }
  /** Returns: a new Vec2 that is the component of 'this' perpendicular to 'd'
   * Requires: 'd' is not zero */ 
  public Vec3 perp(Vec3 d)

  {
    return this.sub(para(d));
  }
  /** Isolates the component of 'this' perpendicular to 'd'
   * Returns: resulting 'this'
   * Requires: 'd' has unit magnitude */
  public Vec3 PerpUnit(Vec3 d)
 
  {
    Set(this.sub(paraunit(d)));
    return this;
  }
  /** Returns: a new Vec3 that is the component of 'this' perpendicular to 'd'
   * Requires: 'd' has unit magnitude */ 
  public Vec3 perpunit(Vec3 d)

  {
    return this.sub(paraunit(d));
  }
  /** Returns: a new Vec that is < 3, 'this' > */ 
  public Vec toVec()

  {
    Vec v = new Vec(3);
    v.x[0] = x;
    v.x[1] = y;
    v.x[2] = z;
    return v;
  }
  /** Returns: a new Vector3d that equals 'this' */
  public Vector3d toVector3d()

  {
    return new Vector3d(x, y, z);
  }
  /** Returns: a new Vector3f that equals 'this' */ 
  public Vector3f toVector3f()

  {
    return new Vector3f((float)x, (float)y, (float)z);
  }

}