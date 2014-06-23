package core.postprocessing;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import core.dflic.FLIC;
import core.field.Vec2Field;
import core.field.Vec2Iterator;
import core.image.AccumImage;
import core.image.RGBImage;
import core.image.ScalarImage;
import core.io.ImageIO;
import core.io.OutputWindow;
import core.math.Vec2;
import core.math.Vec2Transform;


public class ColorCoding {
  
  static int width = 800;
  static int height = 800;
  static int streamlen = 80;
 
  
  static double Ggradient(double x)
  {
    x = 4-x;
    if (x<0.0)
      x = 0.0;
    if (x>1.0)
      x = 1.0;
    return x;
  }
  
  static double Rgradient(double x)
  {
    if (x<1)
      x = 1-x;
    else if (x<3)
      x = -2+x;
    else
      x = 6-x;
    if (x<0.0)
      x = 0.0;
    if (x>1.0)
      x = 1.0;
    return x;
  }
  
  static double Bgradient(double x)
  {
    if (x<4)
      x = 2-x;
    else if (x<6)
      x = -4+x;
    else
      x = 7-x;
    if (x<0.0)
      x = 0.0;
    if (x>1.0)
      x = 1.0;
    return x;
  }
  
  static double Wgradient(double x)
  {
    x = 1.0-x/8.0;
    if (x<0.0)
      x = 0.0;
    if (x>1.0)
      x = 1.0;
    return x;
  }
  
  static class NoFLIC implements Vec2Iterator {
    public Vec2 next()
    {
      return null;
    }
  }
  
  
  static class Field extends Vec2Field {
    double x1, y1, x2, y2;
    
    public Field() {
      x1 = width*5/6;
      x2 = width*1/6;
      y1 = height*1./5. ; // height*1/4;
      y2 = height*1./2. ; // height*3/4;
    }
    
    public Vec2 get(Vec2 p, Vec2 f)
    {
      double factor1 = 1.;
      double factor2 = 1.;
      double R1 = ((p.x-x1)*(p.x-x1) + (p.y-y1)*(p.y-y1));
      double R2 = ((p.x-x2)*(p.x-x2) + (p.y-y2)*(p.y-y2));
      double r1 = Math.pow(R1,-1.5);
      double r2 = Math.pow(R2,-1.5);
      f.x = factor1*(p.x-x1)*r1 + factor2*(p.x-x2)*r2;
      f.y = factor1*(p.y-y1)*r1 + factor2*(p.y-y2)*r2;
      return f;
    }
  }
  
  static class Field1 extends Vec2Field {
	    double ACT,BCT;
	    
	    public Field1() {
	      ACT = 5.;
	      BCT = 100.;
	    }
	    
	    public Vec2 get(Vec2 p, Vec2 f)
	    {
	      f.x = 0;
	      f.y = BCT*(p.x-width/2.)+ACT;
	      return f;
	    }
	  }
  
  public static void main(String[] args)
  {
    Random random = new Random(); // 1:45 for (640, 480, 80)
    int seed = random.nextInt();
    //seed = -1180828986 + 1;
    random = new Random(seed);
    System.out.println("Random seed = "+seed);
    OutputWindow window;
	window = new OutputWindow("DFLIC", width, height);	
    
    ScalarImage input = new ScalarImage(width + streamlen, height + streamlen);
    input.SetRandom(random);
    input.Rescale(2.0, -1.0);
    double[] ikernel = {0.0, 1.0/16, 1.0/8, 1.0/16, 1.0/8, 1.0/4, 1.0/8, 1.0/16, 1.0/8, 1.0/16};
    input.Convolve3x3(ikernel);
    input.Rescale(1.4, 0.0);
    
    AccumImage output = new AccumImage(width, height);
    
    Vec2Field field = new Field();
    
   // FLIC2 flic = new FLIC2(input, output, field, 
   //   new Vec2Transform(new Vec2((width+streamlen)/2, (height+streamlen)/2), 0.5),
   //   new Vec2Transform(new Vec2(width/2, height/2), 0.5));
    FLIC flic = new FLIC(input, output, field, 
      new Vec2Transform(new Vec2(streamlen/2, streamlen/2), 1.0),
      new Vec2Transform(new Vec2(0, 0), 1.0));
    flic.SetStreamLen(streamlen);
    //flic.SetIterator(new NoFLIC());
    flic.Compute();
    
    System.out.println("Colorizing...");
    RGBImage rgbimage = new RGBImage(width, height);
    double[] kernel = {0.0, -0.5, -1.0, 0.0, -1.0, 0.0, 1.0, 0.0, 1.0, 0.5};
    output.Convolve3x3(kernel);
    output.Rescale(0.5, 0.0);
    double[] kernel2 = {0.0, 0.0, 0.125, 0.0, 0.125, 0.5, 0.125, 0.0, 0.125, 0.0};
    output.Convolve3x3(kernel2);
    output.Rescale(0.5, 0.5);
    rgbimage.fromScalarImageMagnitude(output, field,
    		new Vec2Transform(new Vec2(0, 0), 1.0), new Colorizer(0.6, 1.,1.,.0001, 1., false, true));
    ImageIO.WriteTIFF(rgbimage,"c:\\test1.tif");
	rgbimage.toRGBBytes(window.getByteBuffer());
	window.Refresh();
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
