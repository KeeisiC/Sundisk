
package simulations.objects;

import core.math.Vec3;
import simulations.objects.ElectricDipole;
import core.math.SpecialFunctions;

/**
 * An extension of the ElectricDipole class that replaces
 * the constant electric dipole moment with one that is oscillating. We assume
 * that the dipole is fixed in direction along the z-axis, with its magnitude 
 * changing as defined below.  
 * @author Andreas Sundquist
 * @version 1.0
 */
public class ElectricOnOffDipole extends ElectricDipole {
	/** The amplitude of the non-time varying dipole moment */	
	  public double p0;
	/** The amplitude of the time varying dipole moment */	
	  public double p1;
	  /** The dipole takes a time T to turn off or on  */	
	  public double T;
	  /** The time between the time the dipole is fully turned on and the time it begins to turn off*/	
	  public double TP;
	  /** The delay time before the dipole starts to turn on */	
	  public double Tdelay;
	  
	/** Constructs an ElectricOnOffDipole centered at "x" at t = 0. The
	  *   parameter "p" is ignored, because we are creating a dipole which is always
	  *   along the z axis */   
	    public ElectricOnOffDipole(Vec3 x, Vec3 p, double p0, double p1, double T, double TP, double Tdelay){
	      super(x, p);
	      this.p0 = p0;
	      this.p1=p1;
	      this.T = T;
	      this.TP = TP;
	      this.Tdelay = Tdelay;
	    }
	    /** Returns the dipole moment at a time retarded by dt.
	     * We use the method getT to find out the current time of the dipole */   
	    public Vec3 getP(double dt) {
	      double tretarded = getT() - dt;
	      return Vec3.Zhat.scale(p0+p1*SpecialFunctions.getSmooth(tretarded, Tdelay, T, TP));
	    }
	    /** Returns the first time derivative of the dipole moment at a 
	     *   time retarded by dt 
	     *   We use the method getT to find out the current time of the dipole */    
	    public Vec3 getDP(double dt) {
		double tretarded = getT() - dt;
		 return Vec3.Zhat.scale(p1*SpecialFunctions.getSmoothDot(tretarded, Tdelay, T, TP));
	    }
	    /** Returns the second time derivative of the dipole moment at a
	     *   time retarded by dt.  We use the method getT to find out 
	     *   the current time of the dipole */     
	    public Vec3 getDDP(double dt) {
	    double tretarded = getT() - dt;
	    return Vec3.Zhat.scale(p1*SpecialFunctions.getSmoothDotDot(tretarded, Tdelay, T, TP));
	    }
	    
	  }
	  
