package core.field;

import core.math.Vec3;
import core.math.Vec2;

/** A 3-dimensional vector field.
 *
 * Vector fields are created by deriving subclasses that define a
 * function mapping from Vec3 to Vec3.
 */
public abstract class Vec3Field {
/** Sets 'f' to the value of the field at 'p'. 'p' is not modified
	   * Returns: resulting 'f'
	   * This abstract method *must* be overridden */
  public abstract Vec3 get(Vec3 p, Vec2 xpos, Vec3 f);
  /** Returns: a new Vec3 with the value of the field at 'p'.
   *   'p' is not modified. */
  public Vec3 get(Vec3 p){
    Vec3 f = new Vec3();
    Vec2 xpos = new Vec2();
    return get(p, xpos, f);
  }

}
