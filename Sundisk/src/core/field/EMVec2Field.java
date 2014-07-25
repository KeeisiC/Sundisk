package core.field; 

import core.math.Vec2;
import core.math.Vec;
import core.math.Vec3;
import simulations.Constants;
import simulations.objects.*;
import simulations.experiments.BaseExperiment;

/** A planar cross-section of an E&M field, or of the four different velocity fields associated
 * with an E&M field.
* <p>
* An EMVec2Field is a type of Vec2Field whose field is calculated from
* an EMSource object. The EMVec2Field can represent one of eight different 
* fields:  the electric field E; the magnetic field B; the ExB drift velocity of
* magnetic monopoles c^2 ExB/E^2; the ExB drift velocity of electric monopoles
* ExB/B^2; a velocity field everywhere along E with magnitude FluidFlowSpeed*((E/Fnorm)^Fpower);
* a velocity field everywhere along B with magnitude FluidFlowSpeed*((B/Fnorm)^Fpower),
* a velocity field everywhere along E with magnitude determined by region in the image, or 
* a velocity field everywhere along B with magnitude determined by region in the image.  
* <p>
* Upon construction, a coordinate system establishes
* the mapping between the Vec2Field plane and the space of the E field, the B field,
* the "electric motion" field, the "magnetic motion"
* field, or the four fluid flow fields.  In the fluid flow cases the 
* "dfield" velocity field is flow along the direction of the either the electric or magnetic field, rather than
* in the ExB direction, with magnitude determined according to the prescriptions give above.  
* @author Andreas Sundquist
* @author John Belcher
* @version 1.0
*/ 
public class EMVec2Field extends Vec2Field {

	  /* The following eight constants are passed into the constructor to
	   * select the type of field that should be represented. */
	  /** In this case "field" is an electric field*/
	  public static final int Efield = 0;
	  /** In this case "field" is a magnetic field*/
	  public static final int Bfield = 1;
	  /** In this case "dfield" is velocity of drifting magnetic monopoles ExB/B^2*/
	  public static final int EfieldMotion = 2;  
	  /** In this case "dfield" is velocity of drifting  monopoles c^2 ExB/E^2*/
	  public static final int BfieldMotion = 3;   
	  /** In this case "dfield" is a speed parallel to "field" E*/
	  public static final int VEfieldMotion = 4;  
	  /** In this case "dfield" is a speed parallel to "field" B*/
	  public static final int VBfieldMotion = 5;  
	  /** In this case "dfield" is a speed parallel to "field" E and varying by region*/
	  public static final int VREfieldMotion = 6;  
	  /** In this case "dfield" is a speed parallel to "field" B and varying by region*/
	  public static final int VRBfieldMotion = 7;  
	  
	  
	  /**  the source of the field */
	  private BaseObject source;
	  /** Coordinate system parameters.  Origin is the center, the axis are unit vectors made from xdir and ydir,
	   * and xgrid and ygrid are scaled by scale of xunit and yunit.  */
	  private Vec3 origin, xgrid, ygrid, xunit, yunit;
	  /** scale establishes the scale mapping, with larger values zooming out.  */
	  private double scale;
	  /** FieldOrMotionFieldType determines what this EMVec2Field represents of the five possible types.   */
	  private int FieldOrMotionFieldType;
	  /** Fluid flow speed constant in pixels per second, for the situation when we are showing flow fields. 
	   * The actual fluid flow speed at a given pixel is
	   * given by FluidFlowSpeed*(B(E)/Fnorm)^Fpower, where B(E) is the field strength value at that pixel.  
	   * allows us to vary the flow speed with B(E) magnitude if desired.  */
	  public double FluidFlowSpeed = 0.;
	  /** The value of to which B(E) is normalized in computing the fluid flow speed. */
	  public double Fnorm = 1.;
	  /** The value of the power to which B(E)/Fnorm is raised in computing the fluid flow speed.   */
	  public double Fpower = 0.;
	  /** Values of the flowspeed by region as determined by experiment.getFlowSpeed, when used.     */
	  public Vec RegionFlow = null;
	  /** The base experiment computing the flow speed by region, if used,  */
	  public BaseExperiment experiment = null;

	  /** Constructs a new EMVec2Field that calculates the type of field specified
	   * by "FieldOrMotionFieldType" produced by the EMSource "source". The coordinate system
	   * is established by picking the "center" and the two axes "xdir" and "ydir".
	   * The parameter "scale" establishes the scale mapping, where larger values
	   * "zoom out". */
	  public EMVec2Field(BaseObject source, Vec3 center, Vec3 xdir, Vec3 ydir, 
	    double scale, int FieldOrMotionFieldType){
	    this.source = source;
	    this.scale = scale;
	    xunit = xdir.unit();
	    yunit = ydir.unit();
	    xgrid = xunit.scale(scale);
	    ygrid = yunit.scale(scale);
	    origin = center;
	    this.FieldOrMotionFieldType = FieldOrMotionFieldType;    
	  }
	  /** Constructs a new EMVec2Field that represents the EMSource and coordinate
	   * system as the given "field", but with a different field type given by
	   * "FieldOrMotionFieldType". */
	  public EMVec2Field(EMVec2Field field, int FieldOrMotionFieldType){
	    source = field.source;
	    xunit = field.xunit;
	    yunit = field.yunit;
	    xgrid = field.xgrid;
	    ygrid = field.ygrid;
	    origin = field.origin;
	    this.FieldOrMotionFieldType = FieldOrMotionFieldType;
	  }
	  
	  public EMVec2Field(EMVec2Field field, int FieldOrMotionFieldType, double FluidFlowSpeed, double Fnorm, double Fpower){
		  source = field.source;
		  xunit = field.xunit;
		  yunit = field.yunit;
		  xgrid = field.xgrid;
		  ygrid = field.ygrid;
		  origin = field.origin;
		  this.FieldOrMotionFieldType = FieldOrMotionFieldType;
		  this.FluidFlowSpeed = FluidFlowSpeed;
		  this.Fnorm = Fnorm;
		  this.Fpower = Fpower;
	  }
	  
	  public EMVec2Field(EMVec2Field field, int FieldOrMotionFieldType, BaseExperiment experiment, Vec RegionFlow ){
		  source = field.source;
		  xunit = field.xunit;
		  yunit = field.yunit;
		  xgrid = field.xgrid;
		  ygrid = field.ygrid;
		  origin = field.origin;
		  this.FieldOrMotionFieldType = FieldOrMotionFieldType;
		  this.RegionFlow = RegionFlow;
		  this.experiment = experiment;

	  }
	  public Vec2 transform(Vec3 x) {
	    x = x.sub(origin);
	    Vec2 p = new Vec2();
	    p.x = x.dot(xunit);
	    p.y = x.dot(yunit);
	    p.x /= scale;
	    p.y /= scale;
	    return p;
	  }
	  
	  public Vec2 transformV(Vec3 dx){
	    Vec2 p = new Vec2();
	    p.x = dx.dot(xunit);
	    p.y = dx.dot(yunit);
	    p.x /= scale;
	    p.y /= scale;
	    return p;
	  }
	  
	  private Vec3 r = new Vec3();
	  private Vec3 v = new Vec3();
	  private Vec3 E = new Vec3(), B = new Vec3();
	  
	  /** Sets "f" to the value of the EM field at "p". "p" is not modified
	   * Returns: resulting "f" */ 
	  public Vec2 get(Vec2 p, Vec2 f){
		/* We reconstruct the value of the vector position in space "r" so that we can find the fields 
		 * at that point to set the flow speed in our eight different cases */
	    r.Set(origin).AddScaled(xgrid, p.x).AddScaled(ygrid, p.y);

	    /* determine what field or motion field type this is of the eight possibilities and set the field accordingly */
	    switch (FieldOrMotionFieldType) {
	    /* this field is the electric field */
	      case Efield:
	        source.Efield(r, v);
	        break;
	        
		/* this field is the magnetic field */
	      case Bfield:
	        source.Bfield(r, v);
	        break;
	       
	    /* this field is the drift velocity field of magnetic monopoles c^2 ExB/E^2  */
	      case EfieldMotion: {
	        source.Efield(r, E);
	        source.Bfield(r, B);
	        double len2 = E.len2();
	        double len = E.len();
	        if (len2>0.0) {
	        		v.Set(E).Cross(B).Scale(Constants.c2/len2);  
	        	/**	v.Add(E.Scale(3.*Constants.c/len)); add this statement in if you want a field aligned flow as well 
	        	 * as the ExB flow speed  John Belcher 3/22/2014 */
	        }
	        else v.SetZero();
	        break;
	      }
	      
	    /* this field is the drift velocity field of electric monopoles  ExB/B^2  */
	      case BfieldMotion: {
	        source.Efield(r, E);
	        source.Bfield(r, B);
	        double len2 = B.len2();
	        if (len2>0.0) v.Set(E).Cross(B).Scale(1.0/len2);
	        else v.SetZero();
	//        double vmag = v.len();
	//        double rmag = r.len();
	//        double Emag = E.len();
	//        double Bmag = B.len();
	//        double costheta = r.z/rmag;
	//        double sintheta = Math.sqrt(1.-costheta*costheta);
	//        if ( rmag > 90. && rmag < 100. && sintheta > 0.995)  {
	//        	System.out.println( "begin dump ");
	//        	System.out.println( "rmag " + rmag + " vmag " + vmag + " Emag " + Emag + " Bmag "+ Bmag);
	//        	System.out.println( "B vector " + B.x + ", "+ B.y + ", " + B.z );
	 //       	System.out.println( "E vector " + E.x + ", "+ E.y + ", " + E.z );
	        	
	  //      }
	        break;
	      }
	      
	 /* this field is the velocity field parallel to E with magnitude FluidFlowSpeed*((E/Fnorm)^Fpower)  */
	     case VEfieldMotion: {
	        source.Efield(r, E);
	        double len = E.len();
	        if (len > 0.0)
	        {
	          if(this.Fpower == 0.) {
	        	  v.Set(E).Scale(this.FluidFlowSpeed/len); 
	          }
	          else v.Set(E).Scale((this.FluidFlowSpeed/len)*Math.pow(len/this.Fnorm,this.Fpower));
	        }
	        else v.SetZero();
	        break;
	      }
	     
	/* this field is the velocity field parallel to B with magnitude FluidFlowSpeed*((B/Fnorm)^Fpower)  */      
	       case VBfieldMotion: {
	        source.Bfield(r, B);
	        double len = B.len();
	        if (len > 0.0)
	        {
		          if(this.Fpower == 0.) {
		        	  v.Set(B).Scale(this.FluidFlowSpeed/len); 
		        	// v.Add(new Vec3(500,0.,0.));  // put this in or take it out for Neil Banas experiment
		          }
		          else v.Set(B).Scale((this.FluidFlowSpeed/len)*Math.pow(len/this.Fnorm,this.Fpower));
		    }
	        else v.SetZero();
	        break;
	      }
	       
  	 /* this field is the velocity field parallel to E with magnitude set by region of image  */
	     case VREfieldMotion: {
	    	double FlowSpeed;
	        source.Efield(r, E);
	        FlowSpeed = experiment.getFlowSpeed(r,RegionFlow);
	        double len = E.len();
	        if (len > 0.0){
	        	v.Set(E).Scale(FlowSpeed/len); }
	        else v.SetZero();
	        break;
	      }
     
 	 /* this field is the velocity field parallel to B with magnitude set by region of image  */
     case VRBfieldMotion: {
    	double FlowSpeed;
        source.Efield(r, B);
        FlowSpeed = experiment.getFlowSpeed(r,RegionFlow);
        double len = B.len();
        if (len > 0.0){
        	v.Set(B).Scale(FlowSpeed/len); }
        else v.SetZero();
        break;
      }
	       
	    }
	    return f.Set(v.dot(xunit), v.dot(yunit));
	  }
}