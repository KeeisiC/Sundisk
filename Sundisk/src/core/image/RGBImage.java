package core.image;

import core.field.Vec2Field;
import core.field.Vec3Field;
import core.math.Vec2;
import core.math.Vec2Transform;
import core.math.Vec3;
/** Stores an RGB image
*
* = < width, height, red-buffer, green-buffer, blue-buffer >
*
* An RGBImage stores a rectangular, color image in RGB format.
* The image's width and height are specified upon construction, and
* three byte arrays are created to store all the red, green, and blue
* components separately. The pixel data is stored in left-right,
* top-down scanline order. Each component value is from 0 to 255, stored
* in a byte.
* @author Andreas Sundquist
* @version 1.0
*/
public class RGBImage {
  
/** width, height store the dimensions of the image.
	* size = width*height, the number of pixels */
  public final int width, height, size;
/** r, g, and b store the red, green, and blue pixel components */
  public byte[] r, g, b;
/** offset[] indexes the left-most pixel of each scanline in r,g,b[]
   * The index of pixel (x,y) is therefore offset[y]+x */
  public final int[] offset;
  
  /** Constructs a new RGBImage = <width,height,0-buffer,0-buffer,0-buffer> */
  public RGBImage(int width, int height){
    this.width = width;
    this.height = height;
    size = width*height;
    
    r = new byte[size];
    g = new byte[size];
    b = new byte[size];
    offset = new int[height];
    for (int j = 0; j<height; ++j)
      offset[j] = width*j;
    
    Clear();
  }
  /** Sets all the pixels values to zero */
  public void Clear(){
    for (int k = 0; k<size; ++k)
      r[k] = 0;
    for (int k = 0; k<size; ++k)
      g[k] = 0;
    for (int k = 0; k<size; ++k)
      b[k] = 0;
  }
  /** Copies the RGB values from the sub-window of 'image' starting at
   *   (xorigin, yorigin) to 'this'.
   * Requires: the sub-window fits inside 'image' */ 
  public void Copy(RGBImage image, int xorigin, int yorigin){
    if (((xorigin+width)>image.width) || ((yorigin+height)>image.height))
      throw new RuntimeException("RGBImage.Copy: Window too large");
    
    for (int j = 0, k = 0; j<height; ++j) {
      for (int i = 0, l = image.offset[j+yorigin] + xorigin; i<width; ++i, ++k, ++l) {
        r[k] = image.r[l];
        g[k] = image.g[l];
        b[k] = image.b[l];
      }
    }
  }
  /** Copies the RGB values from the top-left corner of 'image' to 'this'
   * Requires: 'image' is at least as large as 'this' */  
  public void Copy(RGBImage image){
    Copy(image, 0, 0);
  }
  /** Returns: the red component of the pixel at (x, y)
   * Requires: 0<=x<width and 0<=y<height */  
  public byte getR(int x, int y){
    return r[offset[y] + x];
  }
  /** Returns: the green component of the pixel at (x, y)
   * Requires: 0<=x<width and 0<=y<height */  
  public byte getG(int x, int y){
    return g[offset[y] + x];
  }
  /** Returns: the blue component of the pixel at (x, y)
   * Requires: 0<=x<width and 0<=y<height */ 
  public byte getB(int x, int y){
    return b[offset[y] + x];
  }
  /** Sets the pixel at (x, y) to the color (R, G, B).
   * Requires: 0<=x<width and 0<=y<height */ 
  public void Set(int x, int y, byte R, byte G, byte B){
    int k = offset[y] + x;
    r[k] = R;
    g[k] = G;
    b[k] = B;
  }
  /** Copies the pixel data in 'this' to the byte-buffer 'buf' in RGB
   * packed order.
   * Requires: buf.length = 3*width*height */  
  public void toRGBBytes(byte[] buf){
    if (buf.length!=(size*3))
      throw new RuntimeException("RGBImage.toRGBBytes: Buffer size mismatch");
    
    for (int k = 0, l = 0; k<size; ++k, l += 3) {
      buf[l] = r[k];
      buf[l+1] = g[k];
      buf[l+2] = b[k];
    }
  }
  /** Copies the pixel data in 'this' to the byte-buffer 'buf' in BGR
   * packed order.
   *Requires: buf.length = 3*width*height */  
  public void toBGRBytes(byte[] buf)

  {
    if (buf.length!=(size*3))
      throw new RuntimeException("RGBImage.toBGRBytes: Buffer size mismatch");
    
    for (int k = 0, l = 0; k<size; ++k, l += 3) {
      buf[l] = b[k];
      buf[l+1] = g[k];
      buf[l+2] = r[k];
    }
  }
  /** Sets 'this' to a colorized representation of 'image' within the
   * sub-window starting at (xorigin, yorigin). The (R,G,B) triad is the
   * color of a scalar value of 1.0, and the resulting RGB values are all
   * clamped to the 0-255 range.
   * Requires: the sub-window fits within 'image' */ 
  public void fromScalarImage(ScalarImage image, int xorigin, int yorigin, 
    double R, double G, double B){
    if (((xorigin+width)>image.width) || ((yorigin+height)>image.height))
      throw new RuntimeException("RGBImage.fromScalarImage: Window too large");
    
    for (int j = 0, k = 0; j<height; ++j) {
      for (int i = 0, l = image.offset[j+yorigin] + xorigin; i<width; ++i, ++k, ++l) {
        r[k] = (byte)clamp(image.f[l]*R, 0, 255);
        g[k] = (byte)clamp(image.f[l]*G, 0, 255);
        b[k] = (byte)clamp(image.f[l]*B, 0, 255);
      }
    }
  }
  /** Sets 'this' to a colorized representation of 'image' within the window
   * starting at the top-left corner. The (R,G,B) triad is the color of a
   * scalar value of 1.0, and the resulting RGB values are all clamped
   * to the 0-255 range.
   * Requires: 'image' is at least as large as 'this' */  
  public void fromScalarImage(ScalarImage image, double R, double G, double B){
    fromScalarImage(image, 0, 0, R, G, B);
  }
  
  public void fromScalarImageMagnitude(ScalarImage image, Vec2Field f, 
    Vec2Transform ftoi, Vec3Field func)
  {
    Vec2Transform itof = ftoi.invert();
    Vec2 p = new Vec2(), v = new Vec2(); Vec2 xpos = new Vec2();
    Vec3 value = new Vec3(), color = new Vec3();
    
    for (int j = 0, k = 0; j<height; ++j)
      for (int i = 0; i<width; ++i, ++k) {
        p.x = i + 0.5;
        p.y = j + 0.5;
        xpos = itof.V(p);
        f.get(xpos, v);
        value.x = v.x;
        value.y = v.y;
        value.z = image.f[k];
        func.get(value, xpos, color);
        r[k] = (byte)clamp(color.x*255, 0, 255);
        g[k] = (byte)clamp(color.y*255, 0, 255);
        b[k] = (byte)clamp(color.z*255, 0, 255);
      }
  } 
  
  public void RfromScalarImage(ScalarImage image, double R)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBImage.RfromScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k)
      r[k] = (byte)clamp(image.f[k]*R, 0, 255);
  }
  
  public void RtoScalarImage(ScalarImage image)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBImage.RtoScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k) {
      int i = (int)r[k];
      if (i<0)
        i += 256;
      image.f[k] = i;
    }
  }
  
  public void GfromScalarImage(ScalarImage image, double G)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBImage.GfromScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k)
      g[k] = (byte)clamp(image.f[k]*G, 0, 255);
  }
  
  public void GtoScalarImage(ScalarImage image)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBImage.GtoScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k) {
      int i = (int)g[k];
      if (i<0)
        i += 256;
      image.f[k] = i;
    }
  }
  
  public void BfromScalarImage(ScalarImage image, double B)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBImage.BfromScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k)
      b[k] = (byte)clamp(image.f[k]*B, 0, 255);
  }
  
  public void BtoScalarImage(ScalarImage image)
  {
    if ((width!=image.width) || (height!=image.height))
      throw new RuntimeException("RGBImage.BtoScalarImage: Window size mismatch!");
    for (int k = 0; k<size; ++k) {
      int i = (int)b[k];
      if (i<0)
        i += 256;
      image.f[k] = i;
    }
  }
  
  private double clamp(double x, double min, double max)
  {
    if (x<min)
      return min;
    else if (x>max)
      return max;
    else
      return x;
  }

}