package core.field;

import core.math.Vec2;
import core.math.Vec3;

/** A 3-D vector field on a 2-D domain.
*
* Vector fields are created by deriving subclasses that define a
* function mapping from Vec2 to Vec3.
*/
public abstract class Vec2Vec3Field {
/** Sets 'f' to the value of the field at 'p'. 'p' is not modified
* Returns: resulting 'f'
* * This abstract method *must* be overridden */ 
public abstract Vec3 get(Vec2 p, Vec3 f);
/** Returns: a new Vec3 with the value of the field at 'p'.
 *   'p' is not modified. */
  public Vec3 get(Vec2 p){
    Vec3 f = new Vec3();
    return get(p, f);
  }
}
