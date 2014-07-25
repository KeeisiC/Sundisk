package simulations.objects;

import simulations.Constants;
import core.math.Vec3;
/** This is a quasi-static electric dipole moving at constant velocity
 * that can be extended to a time varying 
 * radiating electric dipole (see for example ElectricOscillatingDipole). In this routine
 * we set the time derivative and the second time derivative of the dipole moment vector to 
 * zero, even though we have in the expressions for the electric and magnetic field the full
 * radiation correct terms for a point electric dipole.  To do radiaton terms we have to put in 
 * correct expressions for the time derivatives of the dipole moment, as in ElectricOscillatingDipole.
 * <b>Important note: </b> for color coding purposes, where we want to color code the DLIC according
 * to the magnitude of the electric field, we multiply the overall electric field of this dipole by one factor
 * of the radius and scale it by 1/100.  <b>If you want this dipole to interact with other electromagnetic
 * objects you must compensate for this!!!!</b>  
 * @author Andreas Sunquist
 * @version 1.0 
 */
public class ElectricDipole extends BaseObject {
  /** The position of the dipole. */
  public Vec3 x;
  /** The constant velocity of the dipole. */
  public Vec3 v;
  /** The dipole moment of the dipole. */
  public Vec3 p;
  /** The time. */
  public double t; 
  /** Create an electric dipole with zero velocity at t = 0. */ 
  public ElectricDipole(Vec3 x, Vec3 p)
  {
    this.x = x;
    this.v = Vec3.Zero;
    this.p = p;
    this.t = 0;
  }
  /** Create an electric dipole with non-zero velocity at t = 0. */ 
  public ElectricDipole(Vec3 x, Vec3 v, Vec3 p)
  {
    this.x = x;
    this.v = v;
    this.p = p;
    this.t = 0;
  }
  /** Get the time.  This allows us the find the current time for this dipole. */  
  public double getT(){
    return this.t;
  }

  /** Get the dipole moment vector.  Here we always return the constant 
   * dipole moment vector p, which does not evolve in time. 
   * To extend this to non-quasi-static dipoles or radiating dipoles, 
   * this method must be overridden. 
   * See for example ElectricOscillatingDipole */
  public Vec3 getP(double dtretarded){
    return p;
  }
  /** Get the time derivative of dipole moment vector of the dipole. 
   * Here we set this to zero, so that we have a quasi-static electric dipole.  
   * To extend this to non-quasi-static dipoles, this method must be overridden, 
   * see for example ElectricOscillatingDipole */  
 
  public Vec3 getDP(double dtretarded){
    return Vec3.Zero;
  }
  /** Get the second time derivative of dipole moment vector of the dipole. 
   * Here we set this to zero, so that we have a quasi-static electric dipole.  
   * To extend this to non-quasi-static dipoles, this method must be overridden, 
   * see for example ElectricOscillatingDipole */  
  public Vec3 getDDP(double dtretarded){
    return Vec3.Zero; 
  }
  /** Evolve the dipole time and position. 
   * This needs to be improved if the dipole is not moving with constant speed. */
  public void Evolve(double dt){
	t += dt;
    x.AddScaled(v,dt);
  }
  
  /** Compute the electric field at position x and time t */
  public Vec3 Efield(Vec3 x, Vec3 E)
  {
      /*  calculate the vector from the dipole to the position to the observation
       * point  */
    Vec3 r = x.sub(this.x);
   // System.out.println( " Efield " + t + " r.x " + r.x + " r.y " + r.y + " r.z " + r.z);
    double rmag = r.len();
    if (rmag==0.0)
      return E.SetZero();
    else {
      r.Scale(1.0/rmag);
      /* calculate the speed of light in terms of the half width of the image in pixels 
       divided by the half width in units of cT */
      /*  calculate the speed of light travel time dtretarded from the position of the dipole
       * to the observation point */
      double dtretarded = rmag/Constants.c;
      /*  get the dipole moment vector at the retarded time t - dtretarded */
      Vec3 p = getP(dtretarded);
      /*  calculate E1, the quasi-static term in the total electric field */
      Vec3 E1 = r.scale(3.0*p.dot(r)).Sub(p).Scale(1.0/(rmag*rmag*rmag));
      /*  get the time derivative of the dipole moment vector at the retarded time t - dtretarded */
      p = getDP(dtretarded);
      /*  calculate E2, the intermediate term in the total electric field */
      Vec3 E2 = r.scale(3.0*p.dot(r)).Sub(p).Scale(1.0/(Constants.c*rmag*rmag));
      /*  get the second time derivative of the dipole moment vector at the retarded time t - dtretarded */
      p = getDDP(dtretarded);
      /*  calculate E3, the radiation term in the total electric field */
      Vec3 E3 = p.Cross(r).Cross(r).Scale(1.0/(Constants.c*Constants.c*rmag));
      /*  get the total electric field */
      E.Set(E1).Add(E2).Add(E3).Scale(Constants.Efactor);
      /* multiply by r so that the radiation term stays constant at large distances
       * and scale by a factor of 1/100  */
      E.Scale(rmag/100.);
      return E;
    }
  }
  /** Compute the magnetic field at position x and time t */
  public Vec3 Bfield(Vec3 x, Vec3 B)
  {
    Vec3 r = x.sub(this.x);
  //  System.out.println( " Bfield " + t + " r.x " + r.x + " r.y " + r.y + " r.z " + r.z);
    double rmag = r.len();
    if (rmag==0.0)
      return B.SetZero();
    else {
      r.Scale(1.0/rmag);
      /* calculate the speed of light in terms of the half width of the image in pixels 
      divided by the half width in units of cT */
      /*  calculate the speed of light travel time dtretarded from the position of the dipole
       * to the observation point */
      double dtretarded = rmag/Constants.c;
      
      Efield(x, B).Cross(v).Scale(-1.0/Constants.c2);    // this is zero for dipole at rest
      
      Vec3 p = getDP(dtretarded);
      Vec3 B1 = p.cross(r).Scale(1.0/(Constants.c*Constants.c*rmag*rmag));  
      
      p = getDDP(dtretarded);
      Vec3 B2 = p.cross(r).Scale(1.0/(Constants.c*Constants.c*Constants.c*rmag));
      
      B1.Add(B2).Scale(Constants.Efactor);
      B.Add(B1);
       // scale B by r so that radiation field does not fall off at infinity
      B.Scale(rmag/100.);
      return B;
    }
  }

}