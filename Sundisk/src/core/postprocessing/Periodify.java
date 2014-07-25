package core.postprocessing;

import java.io.*;
import core.image.RGBImage;
import core.io.ImageIO;

/** Blends together frames of an animation to produce a continuous loop.
*
* An output image sequence is created by blending together pairs of images
*   from an input image sequence. The contrast is adjusted, assuming that
*   the image pairs are uncorrelated.
*   
*   @author Andreas Sundquist
*   @version 1.0
*   */

public class Periodify {
  /** Width of the image. */
  static int width = 640;   // 1920;
  /** Height of the image. */
  static int height = 480;  // 1200;
  /** Input sequence filename. */
 static String infname = "C:\\Development\\Projects\\SundquistDLIC\\DLICs\\oscDipole\\new";
 // static String infname = "C:\\Users\\john\\Documents\\price_E_oct_2012\\er";  
  /** Output sequence filename. */
  static String outfname = "C:\\Development\\Projects\\SundquistDLIC\\DLICs\\oscDipoleSmooth\\new";
 //  static String outfname = "C:\\Documents and Settings\\john\\My Documents\\unknown1\\dip";
  /** Number of output sequence frames. This is the total number, counting from 0, so the last pic will have a (frames-1) sequence number.*/
  static int frames = 25;
  /** Number of overlap frames.  Note that the input file we are blending must have 
   * (frames+overlap) number of frames total (counting from 0) or the program will terminate
   * trying to read a non-existent image file.  For example, if frames = 100 and overlap = 30,
   * then the input image files must be numbered from 0 to 129, that is, there are 130 of them. */
  static int overlap = 5;
  
  /** Returns one of the input images to blend for output frame "frame". 
   * @param frame The number of the output frame to be blended.
   * @return The number of the input frame from the first part of that series to be blended. */
  static int frame0(int frame){
    return frame;
  }
  /** Returns the other input image to blend for the output frame "frame". 
   * @param frame The number of the output frame to be blended.
   * @return The number of the input frame from the upper part of that series to be blended. */
  static int frame1(int frame){
      return frames+frame;
  }
  /** Returns the blending coefficient for output frame "frame". The pair of
   *   input images are blended in a linear fashion, where a blending
   *   coefficient of 0.0 produces the input image specified by frame0(), 
   *   and 1.0 produces the input image specified by frame1(). 
   *   @param frame The number of the output frame to be blended.
   *   @return The faction of the image from the first part of the input series to be added into the 
   *   blend.  Note that the fraction of the image from the upper part of the input series to be blended into 
   *   the output frame is 1 minus this number. 
   *   @return The blending coefficient.*/
  static double blend(int frame) {
    if (frame < overlap)
      return (overlap-frame)/(1.*overlap);
    else
      return 0.0;
  }
 /** Creates and writes the blended image files. */ 
  public static void main(String[] args){
      byte[] buf0R = new byte[width*height];
      byte[] buf0G = new byte[width*height];
      byte[] buf0B = new byte[width*height];
      
      byte[] buf1R = new byte[width*height];
      byte[] buf1G = new byte[width*height];
      byte[] buf1B = new byte[width*height];
      
      byte[] bufR = new byte[width*height];
      byte[] bufG = new byte[width*height];
      byte[] bufB = new byte[width*height];
      
      RGBImage buf0Image = new RGBImage(width, height);
      RGBImage buf1Image = new RGBImage(width, height);
      RGBImage bufImage = new RGBImage(width, height);
      
      for (int frame = 0; frame<frames; ++frame) {
         if (frame < overlap)
         {
            print("Frame "+frame+": ");
            print("Loading "+frame0(frame)+" & "+frame1(frame)+"... ");
            buf0Image = ImageIO.ReadTIFF(infname+PadZeros(frame0(frame), 4)+".tif");
            buf0R = buf0Image.r; 
            buf0G = buf0Image.g;
            buf0B = buf0Image.b;
            buf1Image = ImageIO.ReadTIFF(infname+PadZeros(frame1(frame), 4)+".tif");
            buf1R = buf1Image.r;
            buf1G = buf1Image.g;
            buf1B = buf1Image.b;
            print("Blending "+blend(frame)+"... ");
            Blend(buf0R, buf1R, blend(frame), bufR);
            Blend(buf0G, buf1G, blend(frame), bufG);
            Blend(buf0B, buf1B, blend(frame), bufB);
         
            bufImage.r = bufR;
            bufImage.g = bufG;
            bufImage.b = bufB;
                  
             print("Saving "+frame+"... ");
             ImageIO.WriteTIFF(bufImage, outfname+PadZeros(frame, 4)+".tif");
         }
         else
         {
             buf0Image = ImageIO.ReadTIFF(infname+PadZeros(frame0(frame), 4)+".tif");
             ImageIO.WriteTIFF( buf0Image, outfname+PadZeros(frame, 4)+".tif");
         }
         
         println("");
      }
      println("Execution finished normally, all image files requested have been combined and a periodic sequence produced.");
    System.exit(0);
  }
      
  /** Loads a monochrome TGA image specified by "fname" into the byte buffer
   *   "buf". The buffer "buf" needs to be at least of size width*height. 
   *   @param fname The name of the input TGA image.
   *   @param buf The buffer into which the image is loaded.  */     
  static void LoadMonoTGA(String fname, byte[] buf){
    byte[] dummy = new byte[18];
    try {
      FileInputStream file = new FileInputStream(fname);
      file.read(dummy,0,18);
      file.read(buf,0,width*height);
      file.close();
    } catch (Exception e) {
    }
  }
  /** Writes a monochrome TGA image specified by "fname" using the image data
   *   in "buf", and the image dimensions specified by "width" and "height". 
   *   @param fname The name of the input TGA image.
   *   @param buf The buffer into which the image has been loaded.  */      
  static void SaveMonoTGA(String fname, byte[] buf){
    try {
      file = new FileOutputStream(fname);
      WriteByte(0);
      WriteByte(0);
      WriteByte(3);
      WriteByte(0);
      WriteByte(0);
      WriteByte(0);
      WriteByte(0);
      WriteByte(0);
      WriteWord(0);
      WriteWord(0);
      WriteWord(width);
      WriteWord(height);
      WriteByte(8);
      WriteByte(0x20);
      file.write(buf,0,width*height);
      file.close();
    } catch (Exception e) {
      System.out.print("Error writing file!");
    }
  }
  
  /** Blends together two images "buf0" and "buf1" to produce "buf" using the
   *   blending coefficient "blend". A value of 0.0 reproduces "buf0", while a value of 
   *   1.0 reproduces "buf1", and 0.5 is half way between the two. 
   *   @param buf0 The first image to be blended.
   *   @param buf1 The second image to be blended.
   *   @param blend The blending coefficient (blend = 0 gives all "buf0"). 
   *   @param buf The blended image.  */  
  static void Blend(byte[] buf0, byte[] buf1, double blend, byte[] buf) {
    double s = 1.0/Math.sqrt(blend*blend + (1-blend)*(1-blend));
    for (int k = width*height-1; k>=0; --k) {
      int i0 = i(buf0[k]);
      int i1 = i(buf1[k]);
      double o = i0 + blend*(i1-i0);
      double oi = round(o);
      if (oi<0)
        oi = 0;
      if (oi>255)
        oi = 255;
      buf[k] = (byte)oi;
    }
  }
 
  /** For byte b >= 0 return int b, otherwise return 256 + int b.
   * @param b The byte.   
   * @return The integer defined above.  */
  
  static int i(byte b){
    if (b>=0)
      return (int)b;
    else
      return 256+(int)b;
  }
 /** Round a double up to the nearest integer.  
  * @param x The double to be rounded. 
  * @return The integer. */ 
  static int round(double x){
    return (int)Math.floor(x + 0.5);
  }
  
  static FileOutputStream file = null;
  static byte[] bytes = new byte[8];
 
  /** Write byte to file.
   * @param x The byte to be written.  */
  static void WriteByte(int x) throws IOException{
    bytes[0] = (byte)x;
    file.write(bytes,0,1);
  }
  
 /** Write word to file.
  * @param x The word to be written.  */
  static void WriteWord(int x) throws IOException{
    bytes[0] = (byte)(x & 0x00FF);
    bytes[1] = (byte)((x & 0xFF00)>>8);
    file.write(bytes,0,2);
  }
  
  /** Returns a string representation of "x" that is padded with zeros
   * up to a length "len". For example, (x,len) = (12,4) we return "0012" 
   * @param x The string for the maximum number of pad zeroes.  
   * @param len The length of the output string that is x plus the padding zeroes.  
   * @return The padded string.  */
  private static String PadZeros(int x, int len){
    String s = new Integer(x).toString();
    StringBuffer z = new StringBuffer();
    for (int i = s.length(); i<len; ++i)
      z.append("0");
    z.append(s);
    return z.toString();
  }
  /** Output routine for strings.  
   * @param s The string to be output.  */
  private static void println(String s){
    System.out.println(s);
  }
  /** Output routine for strings.  
   * @param s The string to be output.  */
  private static void print(String s){
    System.out.print(s);
  }
  
}
