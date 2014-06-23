package simulations.experiments.example_experiments;

import simulations.Constants;
import simulations.objects.BaseObject;
import simulations.objects.EMCollection;
import simulations.objects.ColorTestField;
import core.field.VecTimeField;
import core.math.RungeKuttaIntegration;
import core.math.Vec;
import core.math.Vec2;
import core.math.Vec3;
import simulations.experiments.BaseExperiment;

/** Color Test exeriment with a simple field
 * 
 * @author John Belcher
 * @version 1.0
 */
public class ColorTestExperiment extends BaseExperiment{
    /**  The value at the origin of the color test field */
    private double ACT;
    /**  The slope of the color test field */
    private double BCT;
    /**  The time t */
    private double t;
    
    /** Constructs the color test field  
     * @param ACT the value of the z component of the field at the origin
     * @param BCT the slope of the z component of the field */ 
    public ColorTestExperiment(double ACT, double BCT) {
      /* this experiment is electro-quasi-statics, so we set FieldType accordingly */
      this.FieldType = Constants.FIELD_EFIELD;
      /* this experiment is electro-quasi-statics, so we set FieldMotionType accordingly */
      this.FieldMotionType = Constants.FIELD_MOTION_EFIELD;
      this.ACT = ACT;
      this.BCT = BCT;
      t = 0.0;	    
      ConstructEMSource();
    }

    /** The first charge, which moves */
    private ColorTestField ctf;
    private EMCollection collection;
    /** Construct the EMColletion object  */
    public void ConstructEMSource(){
      ctf = new ColorTestField(ACT,BCT);
      collection = new EMCollection();
      collection.Add(ctf);
    }
  
    /** Returns the BaseObject that is colortest field alone */
    public BaseObject getEMSource(){
      return collection;
    }
 
    /** The equation of motion governing the evolution of the system */
    private class Motion extends VecTimeField {
    /** Given the state of the system p at time t, computes its first time derivatives and puts 
     * them in v, and returns v.  The vector p is the position (p.x[0]) and speed (p.x[1]) of the charge 
     * moving along the z-axis , v.x[0] = p.x[1], and  v.x[1] is the acceleration, and is computed
     * from the coulomb force between the two charges.
     * @param p the position (first location p.x[0]) and the speed (second location p.x[1]) of 
     * the moving charge.
     * @param t the time
     * @param v the derivative of the p vector--so v.x[0] is the speed and v.x[1] is the acceleration, as
     * calculated using Coubomb's law for the force betweeen the charges.   */ 
      public Vec get(Vec p, double t, Vec v){
    	  /* p is the position and speed of the moving charge along the z-axis */
    	  /* v is the derivative of these quantities */
        //v.x[0] = p.x[1];
        // v.x[1] = q*q1/((Math.pow(Math.abs(p.x[0]-z1),2.)));  
        return v;
      }
    }
    
    /** The equation of motion for the system (just Coulomb repulsion) */
    private Motion equations = new Motion();
    /** The integrator used to evolve the system */
    private RungeKuttaIntegration integrator = new RungeKuttaIntegration();
  /** Evolves the experiment a time step dt */
    public void Evolve(double dt, double maxStep){
    integrator.SetStep(maxStep);
    /* Collect the current system coordinates into a vector of dependent variables for integration.*/
      Vec p = new Vec(2);
     // p.x[0] = z;
     // p.x[1] = v;
      /* Evolve the system by the time step dt */ 
      integrator.Evolve(equations, p, t, dt);
      /* Evolve advances p but does NOT advance t, so we advance it in the step below */
      t = t + dt;
      /* "p" now contains the system coordinates at the new time.  */
      /* Get the new first derivatives wrt t at their new values. */
      Vec dpdt = new Vec(2);
      equations.get(p, t, dpdt);
      /* update the state of the system with the new values */
     // z = p.x[0]; 
     // v = p.x[1];  
     // charge.p = new Vec3(0, 0, z);
     // charge.v = new Vec3(0, 0, v);
    }
    
public double getHue(double TargetHue, Vec2 xpos, Vec RegionColor, Vec RegionParameter ){
     return TargetHue;}

public double getFlowSpeed(Vec3 r, Vec RegionFlow, Vec RegionParameter) {
	return 0.;	}

    /** Evolves the experiment by a time step "dt" using an RK4 integrator by taking numberSmallSteps between
     * t and t + dt, for accuracy. */
    public void Evolve(double dt){
  	  /* we simply call the Evolve(double dt,double maxStep) method with maxstep = dt/numberSmallSteps. */
	      Evolve(dt, dt/numberSmallSteps);
	    }
    
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
  	    Vec MyRegionHSVW = new Vec(4);
  	    double MyHue = TargetHue;
  	    double MySaturation = TargetSaturation;
  	    double MyValue = TargetValue;
  	    double MyWhite = 0.;
  	    //
  	    // first we set the hue saturation and value of the regions
  	    //
 //     	double rmag =  r.len();
 //     	double ts = spinningsphere.getT()-Tdelay;
//      	double rx1, rx2, rlimit;
      	MyHue = RegionHue.x[0];
      	MySaturation = RegionSaturation.x[0];
      	MyValue = RegionValue.x[0];
      	MyWhite = RegionWhite.x[0];
        MyRegionHSVW.x[0]=TargetHue;
        MyRegionHSVW.x[1]=TargetSaturation;
        MyRegionHSVW.x[2]=TargetValue;
	    MyRegionHSVW.x[3] = MyWhite;
   //   	if ( ts >=0. ) {
          //consider the outwardly propagating shell from the turn-on ; this is the simple case
   //   		rx1 = R + ts*Constants.c;
      //   		rx2 = R + (ts-T)*Constants.c;
         	// first, has the current stopped changing yet?  
         	// no it is still changing
   //   		if ( ts < T  &&  rmag >= R  &&  rmag <= rx1 ) {
   //   			MyHue = RegionHue.x[1];
   //   			MySaturation = RegionSaturation.x[1]; 
  //    	    	MyValue = RegionValue.x[1];
 //     	    	MyWhite = RegionWhite.x[1];
  //    		}
  //    	// yes it has stopped changing 
  //    		if ( ts >= T && rmag < rx1  && rmag >= rx2 ) {
   //   			MyHue = RegionHue.x[1];
  //    			MySaturation = RegionSaturation.x[1];    
 //     	    	MyValue = RegionValue.x[1];
 //     	    	MyWhite = RegionWhite.x[1];
      	//	}
   //   		
      	   	// yes it has stopped changing AND there are regions where we only have static fields
   //   		if ( ts >= T + 2*R/Constants.c && rmag < R + (ts - T - 2*R/Constants.c)*Constants.c  && rmag > R) {
   //   			MyHue = RegionHue.x[2];
   ////   			MySaturation = RegionSaturation.x[2];    
  //    	    	MyValue = RegionValue.x[2];
      	    	MyWhite = RegionWhite.x[2];
  //    		}
      		
      	// consider the inwardly propagating shell from the turn-on; this is more complicated
     //    		rx1 = R -ts*Constants.c;
   //      		rx2 = R - (ts-T)*Constants.c;
         	// first, has the current stopped changing yet?
         	// no it is still changing
  //       	   if( ts < T ) {
         		   // has the initial pulse reached the origin yet?
         		   // no it has not
     //    		   if ( rx1 > 0. ) {
     //    			   if ( rmag < R && rmag > rx1 ) {
     //    				   MyHue = RegionHue.x[1];
     //    				   MySaturation = RegionSaturation.x[1];   
     //    				   MyValue = RegionValue.x[1];
     //    				   MyWhite = RegionWhite.x[1];
     //    			   }
     //    		   }
         		   // yes it has
     //    		   else {
     //    			   rlimit = Math.max(R, -rx1) ;
     //    			   if ( rmag < rlimit ) {
     ////    				   MyHue = RegionHue.x[1];
     //    				   MySaturation = RegionSaturation.x[1]; 
     //    				   MyValue = RegionValue.x[1];
     //    				   MyWhite = RegionWhite.x[1];
        // 			   }
      //   		   }
       //  	   }
         	// yes, it has stopped changing
         	//   else {
         		   // case I:  neither the initial or the ending pulse has reached the origin yet
         	//	   if ( rx1 > 0 && rx2 > 0 ) {
         //			   if ( rmag > rx1 && rmag <= rx2 ) {
         //				   MyHue = RegionHue.x[1];
         //				   MySaturation = RegionSaturation.x[1];    
         //				   MyValue = RegionValue.x[1];
         //				   MyWhite = RegionWhite.x[1];
        // 			   }
       //  		   }
         		   // case II:  the initial pulse has reached the origin and the ending pulse has not
        // 		   if ( rx1 <= 0 && rx2 > 0 ) {
       //  			   rlimit = Math.max(-rx1,rx2);
       //  			   if ( rmag <= rlimit ) {
       //  				   MyHue = RegionHue.x[1];
       //  				   MySaturation = RegionSaturation.x[1];   
       //  				   MyValue = RegionValue.x[1];
       //  				   MyWhite = RegionWhite.x[1];
       //  			   }
         	//	   }
         //		 // case III:  both the initial pulse and the ending pulse have reached the origin 
       ////  		   if ( rx1 <= 0 && rx2 <= 0 ) {
         //			   if ( rmag <= -rx1 && rmag > -rx2 ) {
       //  				   MyHue = RegionHue.x[1];
       //  				   MySaturation = RegionSaturation.x[1];    
       //  				   MyValue = RegionValue.x[1];
       //  				   MyWhite = RegionWhite.x[1];
      //   			   }
      //   			 if (  rmag < -rx2 ) {
      // 				   MyHue = RegionHue.x[2];
      // 				   MySaturation = RegionSaturation.x[2];    
      // 			       MyValue = RegionValue.x[2];
      // 			       MyWhite = RegionWhite.x[2];
      // 			   }
         			   
     //    		   }   
       //  	   }
         	
     // 	}
      	// 
      	// now we set the values for the regions where the radiation has not yet reached
     //   	if (ts < 0.) {
  	//		MyHue = RegionHue.x[3];
  	//		MySaturation = RegionSaturation.x[3];    
  	//		MyValue = RegionValue.x[3];
  	//		MyWhite = RegionWhite.x[3];}
     //   	if ( ts >=0. ) {
            //consider the outwardly propagating shell from the turn-on  
    //    		rx1 = R + ts*Constants.c;
    //    		if (   rmag >= rx1 ) {
    //  			MyHue = RegionHue.x[3];
     // 			MySaturation = RegionSaturation.x[3];    
     // 			MyValue = RegionValue.x[3];
     // 			MyWhite = RegionWhite.x[3];}
        	// consider the inwardly propagating shell from the turn-on;
     //      	rx1 = R -ts*Constants.c;
     //        if (   rmag < rx1 ) {
     //				MyHue = RegionHue.x[3];
     //				MySaturation = RegionSaturation.x[3];    
    // 				MyValue = RegionValue.x[3];
    // 				MyWhite = RegionWhite.x[3];}
     //   	}
      	
      //    System.out.println( " rmag " + rmag + " t " + ts + " MyHue " + MyHue );
      //  	if ( TimeType != Constants.TimeBehaviour_Harmonic && TimeType != Constants.TimeBehaviour_SemiHarmonic_On && TimeType != Constants.TimeBehaviour_SemiHarmonic_Off) {
      //  		MyRegionHSVW.x[0]=MyHue;
     //   		MyRegionHSVW.x[1]=MySaturation;
     //   		MyRegionHSVW.x[2]=MyValue;
      //  		MyRegionHSVW.x[3]=MyWhite;
      //  	}
      //  	else {
      //		MyRegionHSVW.x[0]=TargetHue;
      //		MyRegionHSVW.x[1]=TargetSaturation;
      //		MyRegionHSVW.x[2]=TargetValue;
     //   	}
  	    return MyRegionHSVW;
    }
      public double getFlowSpeed(Vec3 r, Vec RegionFlow) {
  	    double MyFlowSpeed = 0;;
   		return MyFlowSpeed;	}
}
