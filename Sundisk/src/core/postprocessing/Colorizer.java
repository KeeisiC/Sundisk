package core.postprocessing;

import java.awt.Color;
import core.field.Vec3Field;
import core.math.Vec;
import core.math.Vec3;
import core.math.Vec2;
import simulations.experiments.BaseExperiment;
import simulations.objects.BaseObject;

/**
 * Colorizer adds color to a given LIC image after it has been computed (post processing).
 * It can simply add a color, or add a color and color code according to the magnitude of the field
 * which is used to generate the LIC, in a variety of ways, or add a color which is determined by the
 * region of the image.  Colorizer does the colorization in
 * HSV color space and then converts to RGB color space.  
 * Uses JAVA's Color class for the HSV -> RGB conversion method.
 * 
 * @author Andreas Sundquist
 * @version 1.0
 * */

public class Colorizer extends Vec3Field {	
	 /** Break point level of the strength of the LIC 
	  * field for deciding where to start color fade to black.*/
    public  double saturationPoint;
    /** Hue for the colorization of the field. */
    public double TargetHue;
    /** Saturation for the colorization of the field. */
    public double TargetSaturation;
    /** Value for the colorization of the field. */
    public double TargetValue;
    /** Rate at which color fades to black below the break point. */
    public double fallOff;
    /** Flag for doing more brightening of the image above the break point
     * (brightening flushes out the streaks in the LIC at high field stregth to pure white).
     * We always do some brightening, if this flag is true we do even more.   */
    public boolean bBrighten;
    /** If this flag is true we only color, if it is false we also color code the LIC to indicate the
     * field strength of the vector field used to generate the LIC. */
    public boolean flatColor;
    /** These are the values of the Hues if we color by region. */
    public Vec RegionHue;
    /** These are the values of the Saturations if we color by region. */
    public Vec RegionSaturation;
    /** These are the values of the Values if we color by region. */
    public Vec RegionValue;
    /** These are the values of the white option if we color by region. */
    public Vec RegionWhite;
    /** This is the experiment which determines the colors by region via experiment.getHue() if we color by region. */
    public BaseExperiment experiment;
    /** This is our emsource which we use to get the field if we color by field magnitude.  */
    public BaseObject emsource;
    /** This is the center of the image, used in reconstructing the vector position in space r if we color by region.  */
    public Vec3 symVec3;
    /** This is the X direction in plotting the image, used in reconstructing the vecor position in space r if we color by region.  */
    public Vec3 Xdir;
    /** This is the Y direction in plotting the image, used in reconstructing the vecor position in space r if we color by region.  */
    public Vec3 Ydir;
    /** This is the scale factor.  */
    public double scale;
    /** This constructor is used when we are not coloring by region.  
     * @param MyTargetHue Color hue of field.
     * @param MyTargetSaturation Color saturation of field, only used it flatcolor = true.
     * @param MyTargetValue Color saturation of field, only used if flatcolr = true
     * @param MysaturationPoint Break point on field strength magnitude below which we fade to black.
     * @param MyfallOff Rate at which field color goes to black below break point.
     * @param MyBrighten Determines brightening algorithm.  
     * @param MyflatColor If true colors according to HSV = (MyTargetHue,MyTargetSaturatioin,MyTargetValue)
    */   
    public Colorizer( double MyTargetHue, double MyTargetSaturation, double MyTargetValue, 
    		double MysaturationPoint, double MyfallOff, boolean MyBrighten, boolean MyflatColor)
    {
        this.TargetHue = MyTargetHue;
        this.TargetSaturation = MyTargetSaturation;
        this.TargetValue = MyTargetValue;
        this.saturationPoint = MysaturationPoint;
        this.fallOff = MyfallOff;
        this.bBrighten = MyBrighten;
        this.flatColor = MyflatColor;
        this.RegionHue = new Vec(3);
        this.RegionHue.x[0]=MyTargetHue;
        this.RegionHue.x[1]=MyTargetHue;
        this.RegionHue.x[2]=MyTargetHue;
        this.RegionSaturation = new Vec(3);
        this.RegionSaturation.x[0]=MysaturationPoint;
        this.RegionSaturation.x[1]=MysaturationPoint;
        this.RegionSaturation.x[2]=MysaturationPoint;
        this.RegionValue = new Vec(3);
        this.RegionValue.x[0]=MyTargetSaturation;
        this.RegionValue.x[1]=MyTargetSaturation;
        this.RegionValue.x[2]=MyTargetSaturation;
        this.experiment = null;
        this.emsource = null;
        this.symVec3 = new Vec3(0.,0.,0.);
        this.Xdir = new Vec3(1.,0.,0.);
        this.Ydir = new Vec3(0.,0.,-1.);
        this.scale = 1.;
    }
    /** This constructor is used when we are coloring by region.  
     * @param MyTargetHue Color hue of field.
     * @param MyTargetSaturation Color saturation of field, only used it flatcolor = true.
     * @param MyTargetValue Color saturation of field, only used if flatcolr = true
     * @param MysaturationPoint Break point on field strength magnitude below which we fade to black.
     * @param MyfallOff Rate at which field color goes to black below break point.
     * @param MyBrighten Determines brightening algorithm.  
     * @param MyflatColor If true colors according to HSV = (MyTargetHue,MyTargetSaturatioin,MyTargetValue)
     * @param RegionColor The values of the colors if we color by region. 
     * @param experiment The experiment which determines the colors by region via experiment.getHue() if we color by region. 
     * @param emsource The emsource which we use to get the field if we color by field magnitude. 
     * @param symVec3 The center of the image, used in reconstructing the vector position in space r if we color by region.
     * @param Xdir The X direction in plotting the image, used in reconstructing the vecor position in space r if we color by region.
     * @param Ydir The Y direction in plotting the image, used in reconstructing the vecor position in space r if we color by region.
     * @param scale The scale of the image.  
    */   
    public Colorizer( double MyTargetHue, double MyTargetSaturation, double MyTargetValue, 
    		double MysaturationPoint, double MyfallOff, boolean MyBrighten, boolean MyflatColor, 
    		Vec RegionHue, Vec RegionSaturation, Vec RegionValue, Vec RegionWhite, BaseExperiment experiment, 
    		BaseObject emsource, Vec3 symVec3, Vec3 Xdir, Vec3 Ydir, double scale)
    {
        this.TargetHue = MyTargetHue;
        this.TargetSaturation = MyTargetSaturation;
        this.TargetValue = MyTargetValue;
        this.saturationPoint = MysaturationPoint;
        this.fallOff = MyfallOff;
        this.bBrighten = MyBrighten;
        this.flatColor = MyflatColor;
        this.RegionHue = RegionHue;
        this.RegionWhite = RegionWhite;
        this.RegionSaturation = RegionSaturation;
        this.RegionValue = RegionValue;
        this.experiment = experiment;
        this.emsource = emsource;
        this.symVec3 = symVec3;
        this.Xdir = Xdir;
        this.Ydir = Ydir;
        this.scale = scale;
    }
    /**
    * Takes the input p which codes the x and y field components at this point in the array 
    * and the image value, and returns an RGB color f for the pixel at this point in the array.  
    * @param p   The Vec3 p is input.  The components p.x and p.y are the x and y values of the vector field used to generate the LIC at 
    * this point in the array, and p.z is the image value of the LIC image at this point in the array (0-255).
    * @param f   The RGB values of the color that we are returning for this image value at this point in the
    * array, based on the field magnitude given by the sqrt of the sum of the squares of p.x and p.y (for flatColor
    * not equal to true).  This method is called from RGBImage.fromScalarImageMagnitude
    * @return The RGB color stored as a Vec3.  */   
    public Vec3 get(Vec3 p, Vec2 xpos, Vec3 f)
    {
      double AbsR = Math.sqrt(p.x*p.x + p.y*p.y);
      double MySaturation = 1.0;
      double MyValue = 1.0;
      double MyHue = 1.;
      double MyWhite = 0.;
      /* we reconstruct below the original vector position r in space for this location in the image map, 
       * so that when we want to color according to region in the map we can provide the orginal vector position
       * to determine color to the method experiment.getHue 
      */
      Vec3 r = new Vec3(0.,0.,0);
      Vec3 xgrid, ygrid, xunit,yunit, origin;
  	  xunit = Xdir.unit();
	  yunit = Ydir.unit();
	  xgrid = xunit.scale(scale);
	  ygrid = yunit.scale(scale);
	  origin = symVec3; 
	  r.Set(origin).AddScaled(xgrid, xpos.x).AddScaled(ygrid, xpos.y);
 
      if ( AbsR > saturationPoint ) {
          MySaturation = TargetSaturation*IncreaseSat(AbsR);
          MyValue = TargetValue;
      }
      else {
          MyValue = TargetValue*DecreaseVal(AbsR);
          MySaturation = TargetSaturation;
      }
      
      if ( flatColor ) {
          MySaturation = TargetSaturation;
          MyValue = TargetValue;
      }     

      Vec3 RGBVec;
      
      if (flatColor == false) 
      {
    	  RGBVec = ConvertToRGB( (float)TargetHue, (float)MySaturation, (float)MyValue);
          /* if we are above the saturationPoint and not in color mode 1 (flatColor=true), we apply the Brighten function,  
           * which flushes out the DLIC grain at high field magnitudes (ie, within AbsR)*/
    	  if ( AbsR > saturationPoint )
    	  {
    		  f.x = Brighten(AbsR, p)*(RGBVec.x/255);
    		  f.y = Brighten(AbsR, p)*(RGBVec.y/255);
    		  f.z = Brighten(AbsR, p)*(RGBVec.z/255);
    	  }
    	  else
    	  {
    		  f.x = (p.z)*(RGBVec.x/255);
    		  f.y = (p.z)*(RGBVec.y/255);
    		  f.z = (p.z)*(RGBVec.z/255);
    	  }
      }
	  else
	  {
		  // Color Mode 1 if no experiment is specified
		  // Color Mode 4 if we have an experiment with a method that gives us the hue for various regions of the image
	      MySaturation = TargetSaturation;
	      MyValue = TargetValue;
	      MyHue = TargetHue;
	      Vec MyRegionHSVW = null;
	      if (experiment != null) MyRegionHSVW = experiment.getRegionHSVW(TargetHue, TargetSaturation, TargetValue, r, RegionHue, RegionSaturation, RegionValue, RegionWhite);
	      MyHue= MyRegionHSVW.x[0];
	      MySaturation= MyRegionHSVW.x[1];
	      MyValue= MyRegionHSVW.x[2];
	      MyWhite = MyRegionHSVW.x[3];
	      if (MyWhite < 0.5) {
	      RGBVec = ConvertToRGB( (float)MyHue, (float)MySaturation, (float)MyValue);
		  	f.x = (p.z)*(RGBVec.x/255);
		  	f.y = (p.z)*(RGBVec.y/255);
		  	f.z = (p.z)*(RGBVec.z/255);}
	      else {
	       //  we force the region to be white if MyWhite > .5
			  f.x = 1.;
			  f.y = 1.;
			  f.z = 1.;}
	  }
        
      return f;
    }
  
    /**
     * Defines the value of S in HSV if we are above the break point in field strength.
     * @param fieldMag The value of magnitude of the field used to generate the LIC at this point.
     * @return S
    */
    private double IncreaseSat(double fieldMag)
    {
      double S = Math.abs(Math.pow((saturationPoint/fieldMag), 0.5));
      return S;
    }
    /**
     * Defines the value of V in HSV if we are below the break point in field strength.
     * @param fieldMag The value of magnitude of the field used to generate the LIC at this point.
     * @return V
    */
    private double DecreaseVal(double fieldMag)
    {
      double V = Math.pow((fieldMag/saturationPoint),fallOff);
      return V;
    }
    /**
     * Converts our HSV values to RGB values.  
     * @param Hue H of HSV, as specified.
     * @param Saturation S of HSV, computed based on the break point and fieldMag if flatColor == false.
     * @param Value V of HSV, computed based on the break point and fieldMag if flatColor == false.
     * @return The RGB colors stored in a Vec3.  
    */
    private Vec3 ConvertToRGB( float Hue, float Saturation, float Value)
    {
      int RGBInt = Color.HSBtoRGB( Hue, Saturation, Value);
      Color MyColor = new Color(RGBInt);
      int MyRed = MyColor.getRed();
      int MyGreen = MyColor.getGreen();
      int MyBlue = MyColor.getBlue();
      Vec3 MyVec = new Vec3((double)MyRed, (double)MyGreen, (double)MyBlue);
      return MyVec;
    }
    /**
     * Brightens the image if we are well above the break point.  This flushes out the grain in the LIC.  
     * @param fieldMag The magnitude of the field at this point in the array.
     * @param p The quantity p.z is the only component of p used here, and it is the image value of the LIC image 
     * at this point in the array (0-255).
     * @return The brightened value of the image pixel.  
    */   
    private double Brighten(double fieldMag, Vec3 p){
    	double totalBright;
    	/* if we are in color mode 2 (bBrigthen = true), we really flush out 
    	 * the DLIC grain at high field values, otherwise not so much (for 
    	 * color mode 1 (bBrigthen = false)).  Note we always flush it out to some extent. */
    	if (bBrighten == true) 
    	{
    		double brightAdd = ( 1.0 - IncreaseSat(fieldMag));
    		totalBright = (p.z) + brightAdd;
    	}
    	else
    	{
    		double brightAdd = ( 1.0 - Math.pow(IncreaseSat(fieldMag),0.5));
    		totalBright = (p.z) + Math.pow(brightAdd,2);
    	}
        
        if (totalBright > 1.0)
            totalBright = 1.0;
            
        return totalBright;
    }
    /** A local way to print a string */ 
  	private static void println(String s){
    	System.out.println(s);
  	}
  }

