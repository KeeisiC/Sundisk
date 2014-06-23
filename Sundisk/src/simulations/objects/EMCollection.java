package simulations.objects;


import java.util.*;

import core.math.Vec3;
/** A class which contains a collection of objects which extend BaseObject.  This class knows how to compute the 
 * electric and magnetic and etc. fields of all the base objects of which it is comprised.   
 * @author Andreas Sundquist
 * @version 1.0
 * */

public class EMCollection extends BaseObject{
 /** sources is a vector which contains references to the base objects in the EMCollection */
  public Vector sources;
/** The constructor for EMCollection.  Simply creates a new vector, which is subsequently added to */
  public EMCollection(){
    sources = new Vector();
  }
 /** a temporary location for computation of the total electric field and etc. */ 
  private Vec3 temp = new Vec3();
 /** This method computes the total electric field of all the EM objects in the collection 
   * @param x the position of the observer 
   * @param E the total electric field at the position of the observer 
   * @return E the total electric field at the position of the observer 
   * */
  public Vec3 Efield(Vec3 x, Vec3 E){
    E.SetZero();
    Enumeration enumeration = sources.elements();
    while (enumeration.hasMoreElements()) {
      BaseObject source = (BaseObject)enumeration.nextElement();
      E.Add(source.Efield(x, temp));
    }
    return E;
  }
  
  /** This method computes the total magnetic field of all the EM objects in the collection 
   * @param x the position of the observer 
   * @param B the total magnetic field at the position of the observer 
   * @return B the total magnetic field at the position of the observer 
   * */ 
  public Vec3 Bfield(Vec3 x, Vec3 B){
    B.SetZero();
    Enumeration enumeration = sources.elements();
//	System.out.println( " Bfield called  " + x.x );
    while (enumeration.hasMoreElements()) {
      BaseObject source = (BaseObject)enumeration.nextElement();
//	  System.out.println( "  vector before object added  " + B.x + "  " + B.y + "  " +  B.z  );
      B.Add(source.Bfield(x, temp));
//	  System.out.println( " another object "  );
//	  System.out.println( "  vector after object added  " + B.x + "  " + B.y + "  " +  B.z  );
    }
    return B;
  }
  
  /** This method computes the total Pauli field of all the EM objects in the collection 
   * @param x the position of the observer 
   * @param P the total Pauli field at the position of the observer 
   * @return P the total Pauli field at the position of the observer 
   * */ 
    public Vec3 Pfield(Vec3 x, Vec3 P){
        P.SetZero();
        Enumeration enumeration = sources.elements();
        while (enumeration.hasMoreElements()) {
          BaseObject source = (BaseObject)enumeration.nextElement();
         P.Add(source.Pfield(x, temp));
    }
    return P;
    }
  /** Evolves all the base objects with the naive evolution specified.  Should not be used.  
   * @param dt time step
   */
  public void Evolve(double dt){
    Enumeration enumeration = sources.elements();
    while (enumeration.hasMoreElements()) {
      BaseObject em = (BaseObject)enumeration.nextElement();
      em.Evolve(dt);
    }
  }
 /** adds a BaseObject to the collection of EM objects in the collection 
  * @param em the base object to be added */
  public void Add(BaseObject em ) {
    sources.add(em);
  }
  /** removes a BaseObject from the collection of EM objects in the collection 
   * @param em the base object to be removed */
  public void Remove(BaseObject em){
    sources.remove(em);
  }
  
}