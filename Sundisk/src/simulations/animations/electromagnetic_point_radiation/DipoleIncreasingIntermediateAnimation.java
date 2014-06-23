/**
 * 
 */
package simulations.animations.electromagnetic_point_radiation;

import simulations.Constants;
import simulations.experiments.electromagnetic_point_radiation.OnOffDipoleExperiment;
import core.math.Vec3;
import core.rendering.Renderer;

/**
 * 
 * @author john
 * @see simulations.experiments.faradaysLaw.FallingRingExperiment#FallingRingExperiment(double, double, double, double, double)
 * @see simulations.experiments.faradaysLaw.FallingRingExperiment#Evolve(double) Evolve
 * @see simulations.objects.ElectricOscillatingDipole#ElectricOscillatingDipole(Vec3, Vec3, double, double, double, double) ElectricOscillatingDipole
 */
public class DipoleIncreasingIntermediateAnimation {
	static double resScale = 2.;  
	  static int width = (int)(320*resScale);
	  static int height = (int)(240*resScale);
	  static int streamlen = 100;
	  static String fname = "C:\\Development\\Projects\\SundquistDLIC_2014\\DLICs\\DipoleIncreasingIntermediateNew\\j";
	  static int frames = 101;  //252; 
	  static double scale = 1.;
	  static int widthct= 4;
	  static double cTperiod = 1;   
 
	  public static void main(String[] args){  
		  //public OnOffDipoleExperiment(double p0, double p1, double T, double TP, double Tdelay)
		    // must set halfwidthpixels to 320 and 
		    //  halfwidthct = 2. in Constants.java 
		    int Nframesdelay = 0;
		    int Nframesafter = 10;
		    double diagonalct = widthct*Math.sqrt(1.+((1.*height)/(1.*width))*(1.*height)/(1.*width));
			double Timetotal = ((1.*width)/(1.*widthct))*(1.*diagonalct/2.+cTperiod)/(1.-(Nframesdelay*1.+Nframesafter*1.)/(frames*1.-1.));
		    double T = 1.*width/(1.*widthct*Constants.c);
			double dt = Timetotal/(frames*1.-1.);
			double Tdelay = Nframesdelay*dt;
		    OnOffDipoleExperiment experiment = 
		      new OnOffDipoleExperiment(1.0,0.5, T, 5*Timetotal,Tdelay); 
		    Renderer renderer = new Renderer();
		    renderer.SetFileName(fname);
		    renderer.SetEndFrame(frames);
		    renderer.SetFrames(frames);
		    renderer.SetWidth(width);
		    renderer.SetHeight(height);
		    renderer.SetStreamLen(streamlen);
		    renderer.SetTimeStep(dt);
		    renderer.SetExperiment(experiment);
		    renderer.SetSymmetry(3);
		    renderer.SetColorMode(1);
		    renderer.SetColorStrength(0.003);
		    renderer.SetColorHue(Constants.COLOR_EFIELD);
		    renderer.SetFallOff(2.5);
//		    renderer.SetColorSaturation(.5);
//		    renderer.SetOrigin(Origin);
		   // renderer.SetStartFrame(1);
			//renderer.SetEndFrame(1);
		    renderer.StartRender();
	  }
}
