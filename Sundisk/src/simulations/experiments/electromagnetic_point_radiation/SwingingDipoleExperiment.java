package simulations.experiments.electromagnetic_point_radiation;   

import core.math.SpecialFunctions;
import core.math.Vec;
import core.math.Vec3;
import simulations.experiments.*;
import simulations.objects.*;
import simulations.Constants;

/** 
 *   Fields of an swinging electric dipole.
 *
 *   This class computes the evolution of the experiment in which an 
 *   electric dipole vector is rotating about the y-axis from an angle to the z-axis of minus theta to theta
 *   in the x-z plane, creating waves that flow out from the
 *   dipole at the speed of light.  We include near zone, intermediate zone, and 
 *   far zone terms in the expressions for the electric field.
 *   @author Andreas Sundquist, John Belcher
 *   @version 1.0
 */
public class SwingingDipoleExperiment extends BaseExperiment {

/** The magnitude of the rotating dipole moment of the rotating dipole */	
  private double p0;
  /** The angular frequency of rotation of the rotating dipole.  The swing takes place over a time T given by 2 Math.PI/omega */	
  private double omega;
  /** The angle that the dipole moment mades to the z-axis */	
  private double theta;
  /** The delay time before swing starts */
  private double Tdelay;
  
  /** Constructs an instance of the experiment where the dipole
   *   has a constant moment "p0" rotating at a frequency "omega". */ 
  public SwingingDipoleExperiment(double p0, double omega, double theta, double Tdelay){
	/* this experiment is electro-quasi-statics, so we set FieldType accordingly */
	this.FieldType = Constants.FIELD_EFIELD;
	/* this experiment is electro-quasi-statics, so we set FieldMotionType accordingly */
	this.FieldMotionType = Constants.FIELD_MOTION_EFIELD;
    this.p0 = p0;
    this.omega = omega;
    this.theta = theta;
    this.Tdelay = Tdelay;
    
    ConstructEMSource();
  }
 
  private ElectricDipoleSwinging dipole;
  /** Creates the EMSource that represents the experiment and can be used to
   *   compute the E&M fields */ 
  public void ConstructEMSource(){
    dipole = new ElectricDipoleSwinging(new Vec3(0,0,0),p0,omega,theta,Tdelay);
  }
  /** Returns: an EMSource that represents the current experimental state.
   *   It can be used to compute the E&M fields.
   * The dipole is centered at the origin, it lies in the in the xz plane and it is 
   * swinging about the y-axis. */ 
  public BaseObject getEMSource(){
    return dipole;
  }
  /** Evolves the experiment by a time step "dt". */ 
  public void Evolve(double dt){
    /* evolve the dipole by an amount dt */
	double timenow = dipole.getT();
    Vec3 Pcom = dipole.getP(0.0);
    double nframe = timenow/2.0813008130081303;
    double travel = (timenow-Tdelay-320./3.)*Constants.c;
    double Tperiod = 2.*Math.PI/omega;
    double timetotal = 240*2.0813008130081303 - Tperiod;
	double beta = 2.*theta*(180./Math.PI)*(SpecialFunctions.getSmooth(timenow, Tdelay, Tperiod, 100.*Tperiod) - .5) ;
	System.out.println( nframe + ", "  + beta );
//	System.out.println("dipole from SwingingDipoleExperiment   x " + Pcom.x + " y " +Pcom.y  + " z " + Pcom.z);
    dipole.Evolve(dt);
  }
  /**  Method to find the hue in a given region when we are coloring according to region (Color Mode 4).
   * @param TargetHue This is the target hue from the renderer.
   * @param r This is the vector postion of the point in the image.
   * @param RegionColor This is the varous hues for the regions.
   * @return The hue for the part of the image map at r.   
   * */
    public double getHue(double TargetHue, Vec3 r, Vec RegionColor){
    	double MyHue = 0;
	    return MyHue;}
    
    /**  Method to find the HSV values in a given region when we are coloring according to region (Color Mode 4).
     * @param TargetHue This is the target hue from the renderer.
     * @param TargetSaturation This is the target saturation from the renderer.
     * @param TargetValue This is the target value from the renderer.
     * @param r This is the vector position of the point in the image.
     * @param RegionHue This is the various hues for the regions.
     * @param RegionSaturation This is the various saturations for the regions.
     * @param RegionValue This is the various values for the regions.
     * @return The HSV value for the part of the image map at r.   
     * */
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
//    	Vec3 position = r.Sub(charge.p);
    	double xcomp = 0.;
    	double zcomp = 1.;
    	MyHue = RegionHue.x[1];
    	if(zcomp > 0.) MyHue = RegionHue.x[1];
    	else 
    	{
  	         if (zcomp <- 0.) MyHue = RegionHue.x[0];
    	}
    	//
  		MyRegionHSV.x[0]=MyHue;
  		MyRegionHSV.x[1]=MySaturation;
  		MyRegionHSV.x[2]=MyValue;
  	//	MyRegionHSV.x[3]=MyValue;
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
