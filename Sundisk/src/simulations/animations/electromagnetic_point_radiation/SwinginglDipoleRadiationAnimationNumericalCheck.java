/**
 * 
 */
package simulations.animations.electromagnetic_point_radiation;

import simulations.Constants;
import simulations.experiments.electromagnetic_point_radiation.SwingingDipoleExperimentNumericalCheck;
import core.math.Vec;
import core.math.Vec3;
import core.rendering.Renderer;

/** 
 
 * @author john
 * radiation fields of a rotating non aligned dipole 
 */
public class SwinginglDipoleRadiationAnimationNumericalCheck {
	static double resScale = 2.;  
	  static int width = (int)(320*resScale);
	  static int height = (int)(240*resScale);
	  static int streamlen = 80;
	  static String fname = "E:\\MyDocuments\\Keeisi_Cabellero\\swinging_dipole\\DLICs\\20degreeZY\\q";
	  static int frames = 31;
	  static double scale = 1.0;
	  static int widthct= 3;
	  static double cTperiod = 1;   
	  
	  public static void main(String[] args){  
		    Vec RegionHue = new Vec(3);
		    RegionHue.x[0] = 0.6;  // hue for the field lines connected to the point charge
		    RegionHue.x[1] = 0.6;  // hue for the field lines connected to the charges generating the constant field
		 //   double Tperiod;
		 //   cTperiod = 1.;
		    // must set halfwidthpixels to 320 and 
		    //  halfwidthct = 2. in Constants.java 
		    int Nframesdelay =0;
		    int Nframesafter = 0;
		    double widthctdiagonal = widthct*Math.sqrt(1.+Math.pow((1.*height)/(1.*width),2));
			double Timetotal = ((1.*width)/(1.*widthct))*(1.*widthctdiagonal/2.+cTperiod)/(1.-(Nframesdelay*1.+Nframesafter*1.)/(frames*1.-1.));
		    double Tperiod = 1.*width/(1.*widthct*Constants.c);
		//	double Timetotal = (widthct/2.)/(1.-(Nframesdelay*1.+Nframesafter*1.)/(frames*1.-1.));
			double dt = Timetotal/(frames*1.-1.);
			double Tdelay = Nframesdelay*dt;
			
		    double angledegrees = 20.;
		    double angleradians = angledegrees*Math.PI/180.;
		    SwingingDipoleExperimentNumericalCheck experiment = 
		      new SwingingDipoleExperimentNumericalCheck(1.0, 2.*Math.PI/Tperiod, angleradians, Tdelay);
		    Renderer renderer = new Renderer();
		    renderer.SetFileName(fname);
		    renderer.SetFrames(frames);
		    renderer.SetWidth(width);
		    renderer.SetHeight(height);
		    renderer.SetStreamLen(streamlen);
		    renderer.SetTimeStep(dt);
		    renderer.SetExperiment(experiment);
		    renderer.SetSymmetry(0);
		    // set axes for plane to be displayed.  For this animation there are a lot of interesting planes.
		    renderer.SetXdir(new Vec3(0.,1.,0));
		    renderer.SetYdir(new Vec3(0.,0.,-1.));   
		    renderer.SetColorMode(0);
		    renderer.SetColorHue(0.1);
		    renderer.SetColorStrength(2.);
		    renderer.SetScale(scale);
		    renderer.SetRegionHue(RegionHue);
		 //   renderer.SetOrigin(new Vec3(1000.,0.,0.));
		  renderer.SetStartFrame(200);
	   	  renderer.SetEndFrame(200);
		    renderer.SetFrameprint(false);
		    renderer.StartRender();

	  }
}
