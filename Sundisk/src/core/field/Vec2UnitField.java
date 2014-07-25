package core.field;

import core.math.Vec2;

/** Normalized 2-D vector field.
 *
 * Vec2UnitField is a subclass of Vec2Field and transforms a given
 * Vec2Field so that the magnitude at every point in the field is
 * one, but the direction is the same.
 */
public class Vec2UnitField extends Vec2Field  {

  private Vec2Field field;
  /** Constructs a new Vec2UnitField that is the same as 'field' but with
   *   all the vector values normalize to unit magnitude. */ 
  public Vec2UnitField(Vec2Field field){
    this.field = field;
  }
  /** Sets 'f' to the value of the field at 'p', scaled to unit magnitude.
   *   'p' is not modified.
   * Returns: resulting 'f' */ 
  public Vec2 get(Vec2 p, Vec2 f){
    field.get(p, f);
    if (!f.isZero())
      f.Unit();
    return f;
  }
  
}
