package core.field;

import core.math.Vec2;

/** A 2-dimensional vector field.
*
* Vector fields are created by deriving subclasses that define a
* function mapping from Vec2 to Vec2.
* @author  Andreas Sundquist
* @version 1.0 */

public abstract class Vec2Field {
  
/** Sets 'f' to the value of the field at 'p'. 'p' is not modified
	   * Returns: resulting 'f'
	   * This abstract method *must* be overridden */
  public abstract Vec2 get(Vec2 p, Vec2 f);
  
 /** Returns: a new Vec2 with the value of the field at 'p'.
   *   'p' is not modified. */
  
  public Vec2 get(Vec2 p){
    Vec2 f = new Vec2();
    return get(p, f);
  }

}
