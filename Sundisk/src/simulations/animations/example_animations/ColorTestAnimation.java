/**
 
 **/
package simulations.animations.example_animations;

import simulations.experiments.example_experiments.ColorTestExperiment;
import core.math.Vec;
import core.rendering.Renderer;

/** An animation designed to illustrate the color coding possible.   
 * We also test ways to color by region and also set flow velocity by region
 * (as opposed to setting color and flow by the magnitude of the field).  
 * 
 * @author John Belcher
 * @version 1.0
 */
public class ColorTestAnimation {
	  /** The width of the image generated.  */
	  static int width = 400;
	  /** The height of the image generated.  */
	  static int height = 50;
	  /** The stream integration length.  */
	  static int streamlen = 50;
	  /** The file name for the files to be generated, with path.  */
	  static String fname = "C:\\Development\\Projects\\SundquistDLIC_Master\\DLICdoc\\DLICs\\colortest\\ct";
	  /** The number of frames to be genererated. */
	  static int frames = 1;
	  /** The time step for the evolution of the system.  */
	  static double dt = .25;
	  

	  public static void main(String[] args){ 
		    double ACT = -10./400.;
		    double BCT = 0.;
		    Vec RegionHue= new Vec(4);
		    RegionHue.x[0] = 145./255.;  // hue for the field lines generated after current is on but not static lines yet
		    RegionHue.x[1] = .0;  // hue for the field lines generated when current is still changing
		    RegionHue.x[2] = 149./255.;  // hue for the field lines are static
		    RegionHue.x[3] = .16;  // hue for the regions where there is no field
		    Vec RegionSaturation= new Vec(4);
		    RegionSaturation.x[0] = .6;  // saturation for the field lines generated when current is totally on but field not static
		    RegionSaturation.x[1] = 1.0;  // saturation for the field lines generated when current is still changing
		    RegionSaturation.x[2] = 57./255.;  // saturation for the field lines are static
		    RegionSaturation.x[3] = 1.;  // saturation for the regions where there is no field
		    Vec RegionValue= new Vec(4);
		    RegionValue.x[0] = 1.;  // value for the field lines generated when current is totally on but field not static
		    RegionValue.x[1] = 1.0;  // value for the field lines generated when current is still changing
		    RegionValue.x[2] = 255./255.;  // value for the field lines are static
		    RegionValue.x[3] = 0.;  // value regions where there is no field
		    Vec RegionWhite= new Vec(4);
		    RegionWhite.x[0] = 0.;  // white option for the field lines generated after current is on but not static lines yet
		    RegionWhite.x[1] = 0.;  // white option for the field lines generated when current is still changing
		    RegionWhite.x[2] = 0.;  // white option  for the field lines are static
		    RegionWhite.x[3] =1.;  // white option for the regions where there is no field
		    
		    ColorTestExperiment experiment = new ColorTestExperiment(ACT,BCT);
		    Renderer renderer = new Renderer();
		    renderer.SetRegionHue(RegionHue);
		    renderer.SetRegionSaturation(RegionSaturation);  
		    renderer.SetRegionValue(RegionValue);  
		    renderer.SetRegionWhite(RegionWhite);  
		    renderer.SetFileName(fname);
		    renderer.SetEndFrame(frames);
		    renderer.SetWidth(width);
		    renderer.SetHeight(height);
		    renderer.SetStreamLen(streamlen);
		    renderer.SetTimeStep(dt);
		    renderer.SetExperiment(experiment);
		    renderer.SetSymmetry(0);
		    renderer.SetFrames(frames);
		    renderer.SetColorStrength(5.);
		    renderer.SetColorMode(1);
		    renderer.SetFallOff(1.);
		    renderer.SetColorHue(0.1);
		    renderer.SetColorSaturation(.7);
		    renderer.SetColorValue(1.);
		    
		    renderer.StartRender();
	  }
}
