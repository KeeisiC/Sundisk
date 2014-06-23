package simulations.experiments.electromagnetic_point_radiation;   

import core.math.SpecialFunctions;
import core.math.Vec;
import core.math.Vec3;
import simulations.experiments.*;
import simulations.objects.*;
import simulations.Constants;

/** 
 *   Fields of a dipole that turns on and off
 *

 *   @author Andreas Sundquist
 *   @version 1.0
 */
public class OnOffDipoleExperiment extends BaseExperiment {

	  private double p0, p1, T, TP, Tdelay;
	  private double t;
  
  /** Constructs an instance of the experiment where the dipole
   *   has a constant moment "p0" added to an oscillation of
   *   amplitude "p1" at a frequency "omega". */ 
  public OnOffDipoleExperiment(double p0, double p1, double T, double TP, double Tdelay){
	/* this experiment is electro-quasi-statics, so we set FieldType accordingly */
	this.FieldType = Constants.FIELD_EFIELD;
	/* this experiment is electro-quasi-statics, so we set FieldMotionType accordingly */
	this.FieldMotionType = Constants.FIELD_MOTION_EFIELD;
	  /* Constructs an instance of the experiment where the dipole has a maximum
	   *   moment of 'p0'.  The dipole begins to turn on at time Tdelay, turns on from Tdelay to Tdelay +T,
	   *   stays constant for a time TP, then turns off from time TP+Tdelay+T  to TP + Tdelay+ T+ T, and is zero thereafter
	   *   */
	    this.p0 = p0;
	    this.p1 = p1;
	    this.T = T;
	    this.TP = TP;
	    this.Tdelay = Tdelay;
	    t = 0.0;
	    
	    ConstructEMSource();
	  }
 
  private ElectricOnOffDipole dipole;
  /** Creates the EMSource that represents the experiment and can be used to
   *   compute the E&M fields */ 
  public void ConstructEMSource(){
    dipole = new ElectricOnOffDipole(new Vec3(0,0,0), new Vec3(0,0,1.),p0,p1,T,TP,Tdelay);
  }
  /** Returns: an EMSource that represents the current experimental state.
   *   It can be used to compute the E&M fields.
   * The dipole is centered at the origin, and its direction is along the
   *   z-axis. */ 
  public BaseObject getEMSource(){
    return dipole;
  }
  /** Evolves the experiment by a time step "dt". */ 
  public void Evolve(double dt){
    /* evolve the dipole by an amount dt */
    dipole.Evolve(dt);
    double thistime = dipole.getT();
    Vec3 p = Vec3.Zhat.scale(p0+p1*SpecialFunctions.getSmooth(thistime, Tdelay, T, TP));
    Vec3 pdot = Vec3.Zhat.scale(p1*SpecialFunctions.getSmoothDot(thistime, Tdelay, T, TP));
    System.out.println(" t:" + thistime + ";  p.z: " + p.z + "; pdot.z: " + pdot.z);
  }
  public Vec getRegionHSVW(double TargetHue, double TargetSaturation, double TargetValue, Vec3 r,
  		Vec RegionHue, Vec RegionSaturation, Vec RegionValue, Vec RegionWhite){
	    Vec MyRegionHSV = new Vec(4);
	    double MyHue = TargetHue;
	    double MySaturation = TargetSaturation;
	    double MyValue = TargetSaturation;
	    //
	    // set the hue of the regions
	    //
 // 	double bs = Math.pow(q*Constants.Efactor/Math.abs(E),0.5);

		MyRegionHSV.x[0]=MyHue;
		MyRegionHSV.x[1]=MySaturation;
		MyRegionHSV.x[2]=MyValue;
	    return MyRegionHSV;
}

    
    /**  Method to find the flow speed in a given region when we are determining that speed according to region.
     * This method is used when we have set experiment.FieldMotionType to one of either Constants.FIELD_MOTION_VREFIELD 
     * or Constants.FIELD_MOTION_VRBFIELD.
     * @param r This is the vector postion of the point in the image.
     * @param RegionFlow This is the flow speeds for the regions.
     * @return The flow speed for the part of the image map at r.   
     * */
    public double getFlowSpeed(Vec3 r, Vec RegionFlow) {
	    double MyFlowSpeed = 0;;
 		return MyFlowSpeed;	}  
}
