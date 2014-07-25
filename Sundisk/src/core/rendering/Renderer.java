package core.rendering;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import simulations.objects.BaseObject;
import simulations.Constants;
import core.dflic.DFLIC;
import core.field.EMVec2Field;
import core.image.AccumImage;
import core.image.RGBImage;
import core.io.ImageIO;
import core.io.OutputWindow;
import core.math.Vec;
import core.math.Vec2;
import core.math.Vec2Transform;
import core.math.Vec3;
import core.postprocessing.Colorizer;
import simulations.experiments.*;



/**

 *  The Renderer class handles all the code that draws and evolves the DLIC and the experiment.
 *  Built in are several handlers for adjusting all of the rendering parameters that might 
 *  conceivably need adjusting (image size, kernel matrix, streamlength, color coding mode, etc.).
 *
 *  The main() should create an instance of the desired experiment and pass it
 *	to an instance of this renderer, followed (if necessary) by a series of calls to the Renderer's configuration 
 *	handlers, and concluded with a call to the renderer's StartRender() function.  DLIC generation will then proceed
 *	as usual.  
 *
 * @author Michael Danziger
 * @version 1.0s
 */

public class Renderer {
	/** The image has no symmetry */	
	public static final int SYM_NONE = 0;
	/** The image is symmetric about the vertical axis */
	public static final int SYM_VERTICAL = 1;
	/** The image is symmetric about the horizontal axis */
	public static final int SYM_HORIZONTAL = 2;
	/** The image is symmetric about both the vertical and the horizontal axis */
	public static final int SYM_BOTH = 3;
	/** The image is greyscale */
	public static final int CM_GREYSCALE = 0;
	/** The image is a colored image generated using the first color coding algorithm*/
	public static final int CM_COLOR_1 = 1;
	/** The image is a colored image generated using the second color coding algorithm*/
	public static final int CM_COLOR_2 = 2;
	/** The image is a colored image generated using the third color coding algorithm*/
	public static final int CM_COLOR_3 = 3;
	/** The image is a colored image generated using the fourth color coding algorithm, which is regional coloring*/
	public static final int CM_COLOR_4 = 4;

	
	/** As the rendering proceeds, the program displays an output image on the screen frame by frame in "window".*/
	OutputWindow window;
	/** The image as actually computed, which depending on the symmetry can be either 1/4 or 1/2 or the full
	 * final image written to file and displayed in "window".   That is, it may have dimensions <br>
	 * (width/2,height/2)---SYM_BOTH--------symmetric about vertical and horizontal, <br>
	 * (width/2,height)-----SYM_VERTICAL----symmetric about vertical axis, <br>
	 * (width,height/2)-----SYM_HORIZONTAL--symmetric about horizontal axis, <br>
	 * (width,height)-------SYM_NONE--------no symmetry */
    AccumImage output;
	/** The rgb verzion of output with the same dimensions as output.  */
    RGBImage rgbimage; 
	/** The rgb version of output which now has the full (width,height) dimensions, with the 
	 * additional information (if any) computed from the assumed symmetry of the image.  */
    RGBImage outimage;
    /** width of output image in pixels */	
	int width;
	/** half of the width of the output image in pixels.  This is used in situations with symmetry. */
	int halfWidth;
	/** double the width of the output image in pixels.  This is used in situations with symmetry. */
	int doubleWidth;
	/** height of output image in pixels */
	int height;
	/** half of the height of the output image in pixels.  This is used in situations with symmetry. */
	int halfHeight;
	/** double the height of the output image in pixels.  This is used in situations with symmetry. */
	int doubleHeight;
	/** length of convolution kernel in pixels */
	int streamlen;
	  /** number of frames of the animation to be produced.  The sequence numbers
	   * of the frames produced will run from 0000 to (frames-1) */
  	int frames;
	  /** String which is the path for the sequence file name of the tiffs 
	   * produced, including the full path to those files. 
	   * Example: "C:\\DLIC\\UniformField\\ch".  
	   * This string will result in the production a series of tiff files in the folder 
	   * UniformField named ch0000.tiff up to choNNN.tiff, where NNN = (frames -1).*/
	String fname;
	/** The beginning frame of the interval where we will actually render an image. We evolve the experiment
	 * through 0 to frames -1, but we only compute images from startFrame to endFrame. */
  	int startFrame;
  	/** The end frame of the inteval where we will actually render an image. We evolve the experiment
	 * through 0 to frames -1, but we only compute images from startFrame to endFrame.  */
  	int endFrame;
    /** Time step for evolution between frames */
  	double dt; 
    /** Overall scale factor for image*/ 
  	double scale;
  	/** kernel for the image (this controls the blending of the image).  To blend the image, we perform
  	 *  a convolution on 'this' with a 3x3 kernel and scalar offset. 
     * ikernel is of type double[10], and the convolution computed is of the form:
     * out(i,j) = c0 + c1*out(i-1,j-1) + c2*out( i ,j-1) + c3*out(i+1,j-1) +
     * c4*out(i-1, j ) + c5*out( i , j ) + c6*out(i+1, j ) +
     * c7*out(i-1,j+1) + c8*out( i ,j+1) + c9*out(i+1,j+1)
     * where ci = kernel[i] */
  	double[] ikernel;
  	/** The number of times the kernel is applied to the image (normally should be 1).*/
  	int filterRepeat;
  	/** Key to the symmetry of the image.  If the image is symmetric about its horizontal axis,
  	 * vertical axis, or both, we save time by only calculating one half (or one quarter) of the image,
  	 * and "folding" that part over the axis (axes) of symmetry.  The values this integer can have are 
  	 *	"Renderer.SYM_NONE" = 0 for no symmetry (entire image is calculated)
  	 *	"Renderer.SYM_VERTICAL" = 1 for vertical symmetry (left side of the image is flipped to the right side)
  	 *	"Renderer.SYM_HORIZONTAL" = 2 for horizontal symmetry (top half of the image is flipped to the bottom half)
 	 *	"Renderer.SYM_BOTH" = 3 for symmetry in both directions (upper left quadrant is flipped to the other three). */
  	int symmetry;
  /** Unit axis for the X-direction of the image */	
  	Vec3 Xdir;
  /** Unit axis for the Y-direction of the image */	
  	Vec3 Ydir;
  /** The color mode of the image (that is, how the image is colored).  The values are:
  	  	 *	"Renderer.CM_GREYSCALE" = 0 generates a black and white image
  	  	 *	"Renderer.CM_COLOR_1" = 1	generates a "flat" colored image using colorHue, colorSaturation and colorValue
  	  	 *	"Renderer.CM_COLOR_2" = 2 generates a colored image using the first color coding algorithm, based on the 
  	  	 *   magnitude of the field. 
  	     *	"Renderer.CM_COLOR_3" = 3 generates a colored image using the second color coding algorithm, based on the 
	  	 *   magnitude of the field.  */
  	int colorMode;
  	/** The HSV hue of the color used in the image.  The hue value should be a normalized
  	 * value between 0 and 1.0.  Any value above or below that is clamped to 1.0 or 0.*/
  	double colorHue;
	/** The HSV saturation S of the color used in the image for color mode 1.  The value should be a normalized
  	 * value between 0 and 1.0.  Any value above or below that is clamped to 1.0 or 0.*/
  	double colorSaturation;
	/** The HSV value V of the color used in the image for color mode 1.  The value should be a normalized
  	 * value between 0 and 1.0.  Any value above or below that is clamped to 1.0 or 0.*/
  	double colorValue;
  	/** This essentially controls how bright the color
  	 *  will be, and how quickly it starts to fall off to black.  
  	 *  This value will most likely be different for each image. */
  	double colorStrength;
  	/** rate at which color falls to black below the saturation value */
  	double fallOff;
  	/** RegionHue for when we use different hues in different regions of the image */
  	Vec RegionHue;
  	/** RegionSaturation for when we use different saturations in different regions of the image */
  	Vec RegionSaturation;
  	/** RegionValue for when we use different values in different regions of the image */
  	Vec RegionValue;
  	/** RegionWhite option for when we use different values in different regions of the image */
  	Vec RegionWhite;
  	/** RegionFlow for when we use different flow speeds in different regions of the image */
  	Vec RegionFlow;
  	/** Flowspeed when we let have flow along E(B) varying with the magnitude of E(B) */
  	double FluidFlowSpeed;
  	/** Field normalization for the flowspeed when we let have flow along E(B) varying with the magnitude of E(B) 
  	 * The calculated speed is FluidFlowSpeed*(B(E)/Fnorm)^Fpower.  */
  	double Fnorm;
  	/** Field power for the flowspeed when we let have flow along E(B) varying with the magnitude of E(B) */
  	double Fpower;	
  	/** The origin for the plot, non-zero and used only when there is no symmerty */
  	Vec3 origin;
    /** if this is true, we print information for each frame, otherwise we only print summary information */
  	boolean frameprint;
    /** if this is true, the default value, we use a random seed to start off the DLIC; false we use one that is set */
  	boolean randomseed;
  	/** if we are not using a random seed, the default seed that is set (this can be changed by SetSeedSet */
  	int seedset;
  	BaseExperiment experiment;
  	
  	/**  flag to make sure the renderer has all the required info before running */
  	boolean OKToRun;
  	
  	/** The default constructor for the Renderer, which sets the default values for the various options and parameters. */
  	public Renderer(){
  		this.width = 640;
  		this.halfWidth = this.width / 2;
  		this.height = 480;
  		this.halfHeight = this.height / 2;
  		this.streamlen = 160;
  		this.fname = "C:\\DLICs\\default";
  		this.frames = 0;
  		/* we assume we are going to render all frames from 0 to frames -1 unless otherwise specified */
  		this.startFrame = 0;
  		this.endFrame = 50000;
  		this.dt = 0.02;
  		this.scale = 1.0;
  		this.ikernel = new double[] {0, 0, 1.0/8, 0, 1.0/8, 1.0/2, 1.0/8, 0, 1.0/8, 0};
  		this.filterRepeat = 1;
  		this.symmetry = SYM_NONE;	
  		this.Xdir = new Vec3(1,0,0);
  		this.Ydir = new Vec3(0,0,-1);
  		this.colorMode = CM_GREYSCALE;
  		this.colorHue = Constants.COLOR_EFIELD;
  		this.colorSaturation = 1.;
  		this.colorValue = 1.;
  		this.colorStrength = 1.;
  		this.fallOff = 1.;
  		this.experiment = null;
  		this.OKToRun = false;
  		this.RegionHue = null;
  		this.RegionSaturation = null;
  		this.RegionValue = null;
  		this.RegionWhite = null;
  		this.RegionFlow = null;
  		this.FluidFlowSpeed = 0.;
  		this.Fnorm = 1.;
  		this.Fpower = 0.;
  		this.origin = new Vec3(0.,0.,0.);
  		this.frameprint = true;
  		this.randomseed = true;
  		this.seedset = 1117518289;
  	
  	}

 
  	/** SetExperiment() sets the experiment that the renderer will render. When running an experiment file, you should
  	 * first create an instance of that experiment, and then pass it to the renderer with this function.  The renderer will
  	 * NOT RUN without an experiment set (an error message in the console will tell you if you forgot to set it).*/
  	public void SetExperiment(BaseExperiment myExperiment){
  		this.experiment = myExperiment;
  		if(myExperiment != null) OKToRun = true;
  		if(myExperiment.FieldType == 0) OKToRun = false;
  		if(myExperiment.FieldMotionType == 0) OKToRun = false;
  	}
  	
  	/** GetExperiment() returns the experiment that is currently set to render.  */
  	public BaseExperiment GetExperiment() {
  		return this.experiment;
  	}
  	
  	/** SetWidth() sets the width of the image, WHICH MUST BE EVEN BECAUSE OF OUR SYMMETRIZATION PROCESSES.*/
  	public void SetWidth(int myWidth) 
  	{
  		if (myWidth % 2 != 0) 
  		{
  			println("Width must have an even value.");
  			OKToRun = false;
  		}
  		else
  		{
  			this.width = myWidth;
  			this.halfWidth = myWidth / 2;
  			this.doubleWidth = myWidth * 2;
  		}
  	}
  	
  	/** GetWidth() returns the width of the image.*/
  	public int GetWidth() 
  	{
  		return this.width;
  	}
  	
  	/** SetHeight() sets the height of the image, WHICH MUST BE EVEN BECAUSE OF OUR SYMMETRIZATION PROCESSES.*/ 	
  	public void SetHeight(int myHeight) 
  	{
  		if (myHeight % 2 != 0) 
  		{
  			println("Height must have an even value.");
  			OKToRun = false;
  		}
  		else
  		{
  			this.height = myHeight;
  			this.halfHeight = myHeight / 2;
  			this.doubleHeight = myHeight * 2;
  		}
  	}
  	
  	/** GetHeight() returns the height of the image.  */
  	public int GetHeight() {
  		return this.height;
  	}
  	
  	/** SetStreamLen() sets the streamlength of the image (streamlen). */
  	public void SetStreamLen(int myStreamlen) {
  		this.streamlen = myStreamlen;
  	}
  	
  	/** GetStreamLen() returns the streamlength of the image (streamlen).  */
  	public int GetStreamLen() {
  		return this.streamlen;
  	}
  	
  	/** SetFileName() sets the filename of the image (including path). 
  	 * Example: "C:\\DLIC\\UniformField\\ch".  */
  	public void SetFileName(String myName) {
  		this.fname = myName;
  	}
  	
  	/** GetFileName() returns the filename of the image (including path). */
  	public String GetFileName() {
  		return this.fname;
  	}
  	
  	/** SetFrames() sets the number of frames to be rendered.  */
  	public void SetFrames(int myFrames) {
  		this.frames = myFrames;
  	}
  	
  	/** SetStartFrame() sets the frame at which DLIC rendering starts  */
  	public void SetStartFrame(int start){
  		this.startFrame = start;
  	}
  	
  	/** SetEndFrame() sets the frame at which DLIC rendering ends  */
  	public void SetEndFrame(int end) {
  		this.endFrame = end;
 		println("endFrame:			" + this.endFrame);
  	}
  	
  	/** GetFrames() returns the number of frames to be rendered.  */
  	public int GetFrames() {
  		return this.frames;
  	}
  	
  	/** SetTimeStep() sets the time step for the evolution of the experiment. */
  	public void SetTimeStep(double myStep) {
  		this.dt = myStep;
  	}
  	
  	/** GetTimeStep() returns the time step for the evolution of the experiment.  */
  	public double GetTimeStep() {
  		return this.dt;
  	}
  	
  	/** SetScale() sets the "scale" of the image.  This should usually be 1.0.  */
  	public void SetScale(double myScale) {
  		this.scale = myScale;
  		if (myScale != 1.0) println("Warning:  Using a scale value other than 1.0 can cause problems.");
  	}

	/** GetScale() returns the "scale" of the image.  */
  	public double GetScale() {
  		return this.scale;
  	}
  	
  	/** SetIKernel() sets the kernel for the image (this controls the blending of the image).  */
  	public void SetIKernel(double[] myIKernel) {
  		this.ikernel = myIKernel;
  	}
  	
  	/** GetIKernel() returns the kernel for the image.  */
  	public double[] GetIKernel() {
  		return this.ikernel;
  	}
  	
  	/** SetIKernelFilterRepeat() sets the number of times the kernel is applied to the image (should usually be 1).*/
  	public void SetIKernelFilterRepeat(int myRepeat){
  		this.filterRepeat = myRepeat;
  	}
  	
  	/** GetIKernelFilterRepeat() returns the number of times the kernel is applied to the image.  */
  	public int GetIKernelFilterRepeat(){
  		return this.filterRepeat;
  	}
  	
  	/** SetSymmetry() sets the symmetry of the image.   */
  	public void SetSymmetry(int mySymmetry) {
  		this.symmetry = mySymmetry;
  	}
  	
  	/** GetSymmetry() returns the symmetry of the image as an integer.  */
  	public int GetSymmetry() {
  		return this.symmetry;
  	}
  	
  	/** SetColorMode() sets the color mode of the image (that is, how the image is colored).   */
  	public void SetColorMode(int myColorMode) {
  		this.colorMode = myColorMode;
  	}
  	
  	/** GetColorMode() returns the color mode of the image as an integer. */
  	public int GetColorMode() {
  		return this.colorMode;
  	}
  	
  	/** SetColorHue() sets the HSV hue of the color used in the image. If using CM_GREYSCALE, this value does nothing.
  	 *  The "myHue" value should be a normalized hue value between 0 and 1.0.  Any value above or below that is clamped to 1.0 or 0.*/
  	public void SetColorHue(double myHue) {
  		if (myHue < 0.0) myHue = 0.0;
  		if (myHue > 1.0) myHue = 1.0;
  		this.colorHue = myHue;
  	}
  	
  	/** GetColorHue() returns the hue of the image as a normalized double. */
  	public double GetColorHue() {
  		return this.colorHue;
  	}
  	
  	/** SetColorSaturation() sets the HSV saturation used in the image. 
  	 *  The "myColorSaturation" value should be a normalized hue value between 0 and 1.0.  Any value above or below that is clamped to 1.0 or 0.*/
  	public void SetColorSaturation(double myColorSaturation) {
  		if (myColorSaturation < 0.0) myColorSaturation = 0.0;
  		if (myColorSaturation > 1.0) myColorSaturation = 1.0;
  		this.colorSaturation = myColorSaturation;
  	}
  	
  	/** GetColorSaturation() returns the HSV saturation of the image as a normalized double. */
  	public double GetColorSaturation() {
  		return this.colorSaturation;
  	}
  	
 
 	/** SetColorValue() sets the HSV value used in the image. 
  	 *  The "myColorValue" value should be a normalized hue value between 0 and 1.0.  Any value above or below that is clamped to 1.0 or 0.*/
  	public void SetColorValue(double myColorValue) {
  		if (myColorValue < 0.0) myColorValue = 0.0;
  		if (myColorValue > 1.0) myColorValue = 1.0;
  		this.colorValue = myColorValue;
  	}
  	
  	/** GetColorValue() returns the HSV value of the image as a normalized double. */
  	public double GetColorValue() {
  		return this.colorValue;
  	}
  	
  	/** SetColorStrength() sets the strength or intensity of the coloring.  */
  	public void SetColorStrength(double myStrength) {
  		this.colorStrength = myStrength;
  	}
  	
  	/** GetColorStrength() returns the color strength of the image. */
  	public double GetColorStrength() {
  		return this.colorStrength;
  	}
  	
  	/** SetFallOff() sets the fallOff of the coloring.  */
  	public void SetFallOff(double myFallOff) {
  		this.fallOff = myFallOff;
  	}
  	
  	/** GetFallOff() returns the fallOff of the image. */
  	public double GetFallOff() {
  		return this.fallOff;
  	}
  	
  	/** SetRegionHue() sets the hues of the various color regions, if used.  */
  	public void SetRegionHue(Vec RegionHue) {
  		this.RegionHue = RegionHue;
  	}
  	
  	/** GetRegionHue() returns colors of the various hue regions, if used. */
  	public Vec GetRegionHue() {
  		return this.RegionHue;
  	}
  	
  	/** SetRegionSaturation() sets the saturations of the various regions, if used.  */
  	public void SetRegionSaturation(Vec RegionSaturation) {
  		this.RegionSaturation = RegionSaturation;
  	}
  	
  	/** GetRegionSaturation() returns saturations of the various regions, if used. */
  	public Vec GetRegionSaturation() {
  		return this.RegionSaturation;
  	}
  	
  	/** SetRegionValue() sets the values of the various regions, if used.  */
  	public void SetRegionValue(Vec RegionValue) {
  		this.RegionValue = RegionValue;
  	}
  	
  	/** GetRegionValue() returns saturations of the various regions, if used. */
  	public Vec GetRegionValue() {
  		return this.RegionValue;
  	}
  	
  	/** SetRegionWhite() sets the white option of the various regions, if used.  */
  	public void SetRegionWhite(Vec RegionWhite) {
  		this.RegionWhite = RegionWhite;
  	}
  	
  	/** GetRegionWhite() returns saturations of the various regions, if used. */
  	public Vec GetRegionWhite() {
  		return this.RegionWhite;
  	}
  	/** SetRegionFlow() sets the flow speeds of the various flow regions, if used.  */
  	public void SetRegionFlow(Vec RegionFlow) {
  		this.RegionFlow = RegionFlow;
  	}
  	
  	/** GetRegionFlow() returns flow speeds of the various flow regions, if used. */
  	public Vec GetRegionFlow() {
  		return this.RegionFlow;
  	}
  	
	/** GetFluidFlowSpeed() returns fluid flow speed in the equation FluidFlowSpeed*(B(E)/Fnorm)^Fpower. */
  	public double GetFluidFlowSpeed() {
  		return this.FluidFlowSpeed;
  	}
  	
  	/** SetFluidFlowSpeed() sets the flow speeds in FluidFlowSpeed*(B(E)/Fnorm)^Fpower.  */
  	public void SetFluidFlowSpeed(double FluidFlowSpeed) {
  		this.FluidFlowSpeed = FluidFlowSpeed;
  	}
  	
	/** GetFnorm() returns Fnorm in FluidFlowSpeed*(B(E)/Fnorm)^Fpower. */
  	public double GetFnorm() {
  		return this.Fnorm;
  	}
  	
  	/** SetFnorm() sets the Fnorm in  FluidFlowSpeed*(B(E)/Fnorm)^Fpower.  */
  	public void SetFnorm(double Fnorm) {
  		this.Fnorm = Fnorm;
  	}
  	
	/** GetFpower() returns Fpower in FluidFlowSpeed*(B(E)/Fnorm)^Fpower. */
  	public double GetFpower() {
  		return this.Fpower;
  	}
  	
  	/** SetFpower() sets the Fpower in  FluidFlowSpeed*(B(E)/Fnorm)^Fpower.  */
  	public void SetFpower(double Fpower) {
  		this.Fpower = Fpower;
  	}
  	
	/** GetSeedSet() returns the seed if not using a random seed */
  	public int GetSeedSet() {
  		return this.seedset;
  	}
  	
  	/** SetSeedSet() sets the seed used for a non-random start */
  	public void SetSeedSet(int seedset) {
  		this.seedset = seedset;
  	}
  	

	/** frameprint() returns true if we are printing information for each frame. */
  	public boolean GetFrameprint() {
  		return this.frameprint;
  	}
  	
  	/** () sets the frameprint flag (true for print info each frame, false otherwise)*/
  	public void SetFrameprint(boolean frameprint) {
  		this.frameprint = frameprint;
  	}
  	
	/** GetRandomSeed() returns true if we are using a random seed. */
  	public boolean GetRandomSeed() {
  		return this.randomseed;
  	}
  	
  	/** SetRandomSeed() sets the logic flag if a non-random seed is to be used (false) or not (true). */
  	public void SetRandomSeed(boolean randomseed) {
  		this.randomseed = randomseed;
  	}
  	
	/** GetOrigin() returns the origin of the plot, non-zero only if there is no symmetry. */
  	public Vec3 GetOrigin() {
  		return this.origin;
  	}
  	
  	/** SetOrigin() sets the origin of the plot, non-zero only if there is no symmetry. */
  	public void SetOrigin(Vec3 Origin) {
  		this.origin = Origin;
  	}
  	
  /** Prints out the characteristics of this renderer */	
  	public void PrintRenderInfo() {
  		println("");
  		println("*********** RENDER INFO ***************");
  		println("Image Size:		" + width + " x " + height);
  		println("Steamlength:	" + streamlen);
  		println("StartFrame for rendering   " + startFrame + ";  endFrame for rendering   " + endFrame );
  		println("Filename:   	" + fname);
  		println("Frames:			" + frames);
  		println("Time Step:		" + dt);
  		println("Scale:			" + scale);
  		println("Xdir			" + Xdir.toVector3d());
  		println("Ydir			" + Ydir.toVector3d());
  		println("Symmetry Mode	" + symmetry);
  		println("Color Mode:		" + colorMode);
  		
  		if (colorMode != 0) 
  		{
  		println("Color Hue:		" + colorHue);
  		println("Color Saturation:		" + colorSaturation);
  		println("Color Value:		" + colorValue);
  		println("Color Strength:	" + colorStrength);	
  		println("Fall Off:	" + fallOff);
  	//	println("Region Hue:	(" + RegionHue.x[0]+ ", "+ RegionHue.x[1]+", "+RegionHue.x[2]+")");
  	//	println("Region Saturation:	(" + RegionSaturation.x[0]+ ", "+ RegionSaturation.x[1]+", "+RegionSaturation.x[2]+")");
  	//	println("Region Value:	(" + RegionValue.x[0]+ ", "+ RegionValue.x[1]+", "+RegionValue.x[2]+")");
  	//	println("Region Flow:	(" + RegionFlow.x[0]+ ", "+ RegionFlow.x[1]+", "+RegionFlow.x[2]+")");
  		println("Origin	 " + origin.toVector3d());
  		}
  		
  		println("***************************************");	

   	}
  	
  	/* IMPORTANT:  These are for setting Xdir and Ydir, the coordinate system for the image...
  	* IF THE DLIC LOOKS STRANGE, TRY CHANGING THESE VECTORS */
 /** Set the X coordinate unit vector of the image.  */ 	
  	public void SetXdir(Vec3 myXdir){
  		this.Xdir = myXdir;
  	}
 /** Get the X coordinate unit vector of the image.  */	
  	public Vec3 GetXdir(){
  		return this.Xdir;
  	}
 /** Set the Y coordinate unit vector of the image.  */		
  	public void SetYdir(Vec3 myYdir){
  		this.Ydir = myYdir;
  	}
 /** Get the Y coordinate unit vector of the image.  */	 	
  	public Vec3 GetYdir(){
  		return this.Ydir;
  	}
  	
  	/** CheckStatus() is only used internally by Renderer. */
  	boolean CheckStatus() {
  		return OKToRun;
  	}
  	/** InitOutputWindow() is only used internally by Renderer. */
  	void InitOutputWindow(){
  		window = new OutputWindow("DFLIC", width, height);	
  		outimage = new RGBImage(width, height);	
  	}
  
  	/** StartRender() starts the program rendering.  If for whatever reason the renderer doesn't have sufficient information
  	* to start the rendering process, it will tell you with a message in the console.*/
  	public void StartRender() 
  	{
  		if (frames == 0 ) {
  			println("core.rendering.Renderer is terminating execution because");
  			println("the user has not set the number of frames in the animation " +
  					"by a call to renderer.SetFrames()");
  			return;
  		}
  		if ( (origin.len() != 0.) &&  (symmetry != SYM_NONE) ) {
  			println("core.rendering.Renderer is terminating execution because");
  			println("the origin is not equal to zero and symmetry is not equal to SYM_NONE");
  			return; 
  		}
  		if (CheckStatus() != true) {
  			println("core.rendering.Renderer is terminating execution");
  			println("because an experiment was not specified, or " +
  					"because user did not set FieldType or FieldMotionType, " +
  					"or an odd width or height was set.");
  			return;
  		}
  		if (CheckStatus() == true) {
  			println("Starting DFLIC");
    		/* Initialize a random seed a print its seed value so that we can
    		 *   later use the same seed value for debugging or rendering consistency if desired. */
    		Random random = new Random(); 
    		int seed = random.nextInt();
    		if(randomseed) random = new Random(seed);
    		else random = new Random(seedset);
    		if(!randomseed) println("Not using a random seed, seedset is " + seedset);
    		println("Random seed = "+seed);
    		InitOutputWindow();
  		    BaseObject emsource = experiment.getEMSource();
			EMVec2Field field;
			EMVec2Field dfield = null;	    
		    Vec3 symVec3;
		    Vec2 symVec2;
		    
		    switch (symmetry) {
		    	case Renderer.SYM_NONE:
		    		symVec3 = new Vec3(origin.x,0,origin.z);
		    		symVec2 = new Vec2(halfWidth, halfHeight);
		    		output = new AccumImage(width, height);
					rgbimage = new RGBImage(width, height);
		    		break;
		    	case Renderer.SYM_VERTICAL:
		    		symVec3 = origin;
		    		symVec3 = new Vec3(-halfWidth/2,0,origin.z);
		    		symVec2 = new Vec2(halfWidth/2, halfHeight);
		    		output = new AccumImage(halfWidth, height);
					rgbimage = new RGBImage(halfWidth, height);
		    		break;
		    	case Renderer.SYM_HORIZONTAL:
		    		symVec3 = new Vec3(origin.x,0,halfHeight/2);
		    		symVec2 = new Vec2(halfWidth, halfHeight/2);
		    		output = new AccumImage(width, halfHeight);
					rgbimage = new RGBImage(width, halfHeight);
		    		break;
		    	case Renderer.SYM_BOTH:
		    		symVec3 = new Vec3(-halfWidth/2,0,halfHeight/2);
		    		symVec2 = new Vec2(halfWidth/2, halfHeight/2);
		    		output = new AccumImage(halfWidth, halfHeight);
					rgbimage = new RGBImage(halfWidth, halfHeight);
		    		break;
		    	default:
		    		symVec3 = new Vec3(0,0,0);
		    		symVec2 = new Vec2(halfWidth, halfHeight);
		    		break;
		    }
		    			
		    
		    if (experiment.FieldType == Constants.FIELD_EFIELD)
		    {
		    	println("Our F field is an E_FIELD");
    			field = new EMVec2Field(emsource, symVec3, Xdir, Ydir, scale, EMVec2Field.Efield);
		    }
		    else
		    {
		    	println("Our F field is a B_FIELD");
		    	field = new EMVec2Field(emsource, symVec3, Xdir, Ydir, scale, EMVec2Field.Bfield);
		    }
		    
		    if (experiment.FieldMotionType == Constants.FIELD_MOTION_EFIELD) {
		    	println("Our D field is the drift velocity of magnetic monopoles c^2 ExB/E^2 ");
		    	dfield = new EMVec2Field(field, EMVec2Field.EfieldMotion);}
		    
		    if (experiment.FieldMotionType == Constants.FIELD_MOTION_VEFIELD) {
    			println("Our D field is along E with speed FluidFlowSpeed*(E/Fnorm)^Fpower ");
	    		println("Our D field is along B with speed FluidFlowSpeed*(B/Fnorm)^Fpower ");
	    		println(" FluidFlowSpeed = " + FluidFlowSpeed);
	    		println(" Fnorm = " + Fnorm);
	    		println(" Fpower = " + Fpower);
    			dfield = new EMVec2Field(field, EMVec2Field.VEfieldMotion,FluidFlowSpeed,Fnorm,Fpower);}
		    
		    if (experiment.FieldMotionType == Constants.FIELD_MOTION_VREFIELD) {
	    		println("Our D field is along E with speed determined by region of the image ");
		    	dfield = new EMVec2Field(field, EMVec2Field.VRBfieldMotion,experiment,RegionFlow);}
		    
		    if (experiment.FieldMotionType == Constants.FIELD_MOTION_BFIELD) {
    			println("Our D field is the drift velocity of electric monopoles  ExB/B^2 ");
    			dfield = new EMVec2Field(field, EMVec2Field.BfieldMotion);}
		    
		    if (experiment.FieldMotionType == Constants.FIELD_MOTION_VBFIELD) {
	    		println("Our D field is along B with speed FluidFlowSpeed*(B/Fnorm)^Fpower ");
	    		println(" FluidFlowSpeed = " + FluidFlowSpeed);
	    		println(" Fnorm = " + Fnorm);
	    		println(" Fpower = " + Fpower);
		    	dfield = new EMVec2Field(field, EMVec2Field.VBfieldMotion,FluidFlowSpeed,Fnorm,Fpower);}
		    
		    if (experiment.FieldMotionType == Constants.FIELD_MOTION_VRBFIELD) {
	    		println("Our D field is along B with speed determined by region of the image ");
		    	dfield = new EMVec2Field(field, EMVec2Field.VRBfieldMotion,experiment,RegionFlow);}
  		
  		
  		  /* Construct a new DFLIC object that will be used to render the
   		  *   sequence of animation frames over time. The only parameter changed
   		  *   is the convolution kernel width */
  	 		DFLIC dflic = new DFLIC(field, dfield, output, new Vec2Transform(symVec2, 1));
   		 	dflic.SetRandom(random);
  		  	dflic.SetStreamLen(streamlen);
  		  	
  		  	dflic.SetInputFilterKernel(ikernel);
   		 	dflic.SetInputFilterRepeat(filterRepeat);
  	
  			PrintRenderInfo();
  		
  			for (int frame = 0; frame < frames; ++frame) {
  				if(frameprint) println("Frame "+frame+"...");
      			if ((frame>=startFrame) && (frame<=endFrame)) {
      				if(frameprint) println("Computing DFLIC...");
      				/* Compute the next frame image */
       				dflic.Compute();      	 			
      				output.Rescale(1.5, 0.0);
      				output.Rescale(0.5, 0.5);
      				if(frameprint) println(" ");
      				if(frameprint) println("Converting image, adding color if specified. ");
        			/* Rescale the output image range from [-1, 1] to [0, 1] */
        			//output.Rescale(0.5, 0.5);
       			 	/* Turn the scalar image in output into an RGB image in rgbimage, with
       			 	 * the same dimensions as output. */
        			switch (colorMode) {
        				case Renderer.CM_GREYSCALE:
        					rgbimage.fromScalarImage(output, 0, 0, 256, 256, 256);
        					break;
        				case Renderer.CM_COLOR_1:
        					rgbimage.fromScalarImageMagnitude(output, field, new Vec2Transform(symVec2, 1.0), 
        							new Colorizer(colorHue,colorSaturation,colorValue,colorStrength,fallOff, false, true, RegionHue, RegionSaturation, RegionValue, RegionWhite, experiment,emsource, symVec3, Xdir, Ydir, scale));
        					break;
        				case Renderer.CM_COLOR_2:
        					rgbimage.fromScalarImageMagnitude(output, field, new Vec2Transform(symVec2, 1.0), 
        							new Colorizer(colorHue,colorSaturation,colorValue,colorStrength,fallOff, false, false));
							break;
        				case Renderer.CM_COLOR_3:
        					rgbimage.fromScalarImageMagnitude(output, field, new Vec2Transform(symVec2, 1.0), 
        							new Colorizer(colorHue,colorSaturation,colorValue,colorStrength,fallOff, true, false));
							break;
           				case Renderer.CM_COLOR_4:
           			// 		println("Region Saturation Renderer:	(" + RegionSaturation.x[0]+ ", "+ RegionSaturation.x[1]+", "+RegionSaturation.x[2]+")");
        					rgbimage.fromScalarImageMagnitude(output, field, new Vec2Transform(symVec2, 1.0), 
        					new Colorizer(colorHue,colorSaturation,colorValue,colorStrength,fallOff, false, true, RegionHue, RegionSaturation, RegionValue, RegionWhite, experiment,emsource, symVec3, Xdir, Ydir, scale));
							break;
						default:
							rgbimage.fromScalarImage(output, 0, 0, 256, 256, 256);
							break;
        			}
        			
       			 	/* Use the  RGB image in rgbimage to produce the full image outimage, with
       			 	 * dimensions (width,height), using the assumed symmetry properties of the image. */
        			if(frameprint) println("Reconstructing the full image using the symmetry properties specified. ");
        			switch (symmetry) {
        				case Renderer.SYM_NONE:
        					outimage = rgbimage;
        					break;
        				case Renderer.SYM_VERTICAL:
        					for (int j = 0; j<height; ++j)
          						for (int i = 0; i<halfWidth; ++i) {
           						byte r = rgbimage.getR(i,j);
            					byte g = rgbimage.getG(i,j);
            					byte b = rgbimage.getB(i,j);
            					outimage.Set(i, j, r, g, b);
            					outimage.Set(width-1-i, j, r, g, b);
            
            					//outimage.Set(i, height-1-j, r, g, b);
            					//outimage.Set(width-1-i, height*2-1-j, r, g, b);
          					}
          					break;
          				case Renderer.SYM_HORIZONTAL:
          					for (int i = 0; i<width; ++i)
          						for (int j = 0; j<halfHeight; ++j) {
           						byte r = rgbimage.getR(i,j);
            					byte g = rgbimage.getG(i,j);
            					byte b = rgbimage.getB(i,j);
            					outimage.Set(i, j, r, g, b);
            					//outimage.Set(width-1-i, j, r, g, b);
            
            					outimage.Set(i, height-1-j, r, g, b);
            					//outimage.Set(width-1-i, height*2-1-j, r, g, b);
          					}
          					break;
          				case Renderer.SYM_BOTH:
          					for (int j = 0; j<halfHeight; ++j)
          						for (int i = 0; i<halfWidth; ++i) {
           						byte r = rgbimage.getR(i,j);
            					byte g = rgbimage.getG(i,j);
            					byte b = rgbimage.getB(i,j);
            					outimage.Set(i, j, r, g, b);
            					outimage.Set(width-1-i, j, r, g, b);
            
            					outimage.Set(i, height-1-j, r, g, b);
            					outimage.Set(width-1-i, height-1-j, r, g, b);
          					}
          					break;
          				default:
          					outimage = rgbimage;
          					break;
        			}
        			
        			/* Copy the full RGB image to the window and update the screen */
       				outimage.toRGBBytes(window.getByteBuffer());
       				window.Refresh();
      	
        			/* Write the full RGB image to disk */
       				if(frameprint) println("Writing image to file "+fname+PadZeros(frame, 4)+".tif");
       				ImageIO.WriteTIFF(outimage, fname+PadZeros(frame, 4)+".tif");
       				if(frameprint) println("Evolving dflic one time step " + dt);
     				dflic.Evolve(dt, dt);
     			}  			
      			else {
      			if(frameprint)	println("DFLIC not computed for this frame by request: StartFrame = " + startFrame + ", EndFrame = " + endFrame );
      				}
      			
      			if(frameprint) println("Evolving experiment one time step " + dt);
       			experiment.Evolve(dt);
    		}
  		   println("");
  		   println("Execution finished normally, all image files requested have been generated.");
  		   
  		   try {
  		      System.out.println("Hit [Enter] to quit...");
  		      new BufferedReader(new InputStreamReader(System.in),1).readLine();
  		   } catch (Exception e) {
  		     System.out.println(e.getMessage());
  		   }
  		    /* Clean up */
  		    window.dispose();
  		    System.exit(0);
  		}
  	}
  /** A method to pad the names of the image files produced with beginning zeroes. 
   *  The string representation of N is padded up to a length 'len'. For example:
 	  *   (N,len) = (12,4) => returns "0012" */  	
  	private static String PadZeros(int x, int len){
    	String s = new Integer(x).toString();
   		 StringBuffer z = new StringBuffer();
    	for (int i = s.length(); i<len; ++i)
      	z.append("0");
    	z.append(s);
    	return z.toString();
  	}
 /** A local way to print a string */ 
  	private static void println(String s){
    	System.out.println(s);
  	}
}




















