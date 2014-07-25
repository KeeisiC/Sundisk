package core.field;

import core.math.Vec;
/** An n-dimensional+time vector field.
*
* Vector fields are created by deriving subclasses that define a
* function mapping from (Vec,time) to Vec2.
*
* This class can be used to describe any time-dependent function.
* Note that the implementation of get(Vec p, double t) in this abstract
* base class assumes that both the domain and range are of the same
* dimension, and if this is not the case, the method must be overridden.
*
* One possible usage of this class is to describe the equations of motion
* of a dynamical system. Given the independent coordinates as an input,
* a VecTimeField can return the time derivatives of these coordinates,
* useful when integrating the system.
*/

public abstract class VecTimeField {
	
	  /** Sets "v" to the value of the field at "p" at time "t". 
	   *   "p" is not modified
	   * Returns: resulting "v" */
 public abstract Vec get(Vec p, double t, Vec v );

 /** Returns: a new Vec with the value of the field at "p". It is assumed
  *   that the range of the field is the same as the domain. This method must
  *   be overridden if that is not the case. "p" is not modified. */
  public Vec get(Vec p, double t){
    Vec v = p.newVec();
    get(p, t, v);
    return v;
  }
  
}
