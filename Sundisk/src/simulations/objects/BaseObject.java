package simulations.objects;

import core.math.Vec3;

/**
 * This is the abstract base class for physical objects.  Its methods describe how a given source
 * object (for example a line of current or a point charge) produce electromagnetic and other fields.
 * Every derived subclass must provide a method to compute the various fields 
 * in the rest frame of the observer by defining the Vec3 to Vec3 mapping in the methods Efield(), 
 * Bfield(), and Pfield().
 * 
 * @author Andreas Sundquist
 * @author John Belcher
 * @version 1.0
 */

public abstract class BaseObject {
/**
 * Sets 'E' to the value of the electric field at 'x'. 'x' is not modified.
 * Returns: resulting 'E'
 * @param x the position of the observer 
 * @param E the electric field at the observer's position
 * @return E the electric field at the observer's position
 */
  public abstract Vec3 Efield(Vec3 x, Vec3 E);
  
  /**
   * Sets 'B' to the value of the magnetic field at 'x'. 'x' is not modified.
   * Returns: resulting 'B'.
   *  * @param x the position of the observer 
 * @param B the magnetic field at the observer's position
 * @return B the magnetic field at the observer's position
   */
  public abstract Vec3 Bfield(Vec3 x, Vec3 B);
  
  /**
   * Sets 'P' to the value of the Pauli field at 'x'. 'x' is not modified.
   * Returns: resulting 'P'.
   * @param x the position of the observer 
   * @param P the Pauli field at the observer's position
   * @return P the Pauli field at the observer's position
   */
  public  Vec3 Pfield(Vec3 x, Vec3 P){
      return P.SetZero();  
    }
  
  /** Returns: a new Vec3 with the value of the electric field at 'x'.
   *   'x' is not modified. 
   *   @param x the position of the observer 
   *   @return E the electric field at the position of the observer */
    public Vec3 Efield(Vec3 x){
    Vec3 E = new Vec3();
    return Efield(x, E);
  }
  /** Returns: a new Vec3 with the value of the Pauli field at 'x'.
     *   'x' is not modified. 
     *   @param x the position of the observer 
     *   @return P the Pauli field at the position of the observer */
   public Vec3 Pfield(Vec3 x){
    Vec3 P = new Vec3();
    return Pfield(x, P);
  }
  
 /** Returns: a new Vec3 with the value of the magnetic field at 'x'.
    *   'x' is not modified. 
    *   @param x the position of the observer 
    *   @return B the magnetic field at the position of the observer */
  public Vec3 Bfield(Vec3 x){
    Vec3 B = new Vec3();
    return Bfield(x, B);
  }
  
  /** Evolves the object by the amount of time 'dt'. 
   * @param dt the time step*/ 
  public void Evolve(double dt){
  }
  
}