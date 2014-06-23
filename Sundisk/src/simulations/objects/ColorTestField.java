package simulations.objects;

import simulations.Constants;
import core.math.Vec3;

/** 
 * Color Test Field.  This BaseObject calculates a simple field linear field in the
 * z direction that we can use in color testing. */
public class ColorTestField extends BaseObject {
  /** the value of the field at the origin of the point charge */
  public double BCT;
  /** the slope of the field at the origin */
  public double ACT;
  
/** constructor for color test field
 *  @param BCT the value of the field at the origin
 *  @param ACT the slope of the field at the origin 
 *  */
  public ColorTestField(double ACT, double BCT){
    this.BCT= BCT;
    this.ACT= ACT;
  }
  
  /** the electric field of our color test field
   * @param x the position of the observer
   * @param E the electric field at the position of the observer
   * @return E the electric field at the position of the observer 
   */
  public Vec3 Efield(Vec3 x, Vec3 E) {
    /* E is in the z direction and this component varies linearly with x */
    E = new Vec3(0.,0.,0.);  
    E.z = (x.x + 200.)* ACT + BCT;
    return E;
  }
  
  /** the magnetic field of the color test field is zero 
   * @param x the position of the observer
   * @param B the magnetic field at the observer's position */
  public Vec3 Bfield(Vec3 x, Vec3 B) {
    return B.SetZero();
  }
  
  /** writes properties of the point charge to a string */  
       public String toString() {
	    return " ColorTestField:  ACT " + this.ACT + " BCT "+ this.BCT + ")";
    }

}