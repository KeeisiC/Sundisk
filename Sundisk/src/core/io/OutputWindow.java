package core.io;

import java.lang.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.color.*;
/** An RGB frame-buffer accessible window
*  = < title, width, height, byte-buffer >.
*
* On construction, a window with the specified width, height, and title
* appears. This window can be drawn to via Raster or Graphics operations,
* or its contents can be accessed directly via an RGB byte-buffer.
* Since the window contents are effectively double-buffered, an explicit
* call to Refresh() is required to update the window.
* @author Andreas Sundquist
* @version 1.0
*/
public class OutputWindow {
  
	  /** Consturcts a new OutputWindow = < Title, Width, Height, zero-buffer >.
	   * Because of AWT inconsistencies, it uses an ugly hack... appears to work
	   * well on Windows, but not so well on UNIX. */

  public OutputWindow(String Title, int Width, int Height){
    // FIX!!!
    f = new OutputFrame(Title);

    f.setVisible(true);

    f.setResizable(false);
    SetSize(Width,Height);

    f.setVisible(false);
    f.setVisible(true);
    //SetSize(Width,Height);
  }
  /** Sets the size of the window to (Width, Height) */
  public void SetSize(int Width, int Height){
    Insets insets = f.getInsets();
    f.setSize(Width + insets.left + insets.right, Height + insets.top + insets.bottom);

    if (o != null)
      f.remove(o);
    o = new OutputComponent(Width,Height);
    f.add(o);
  }
  /** Changes the title of the window to 'Title' */
  public void SetTitle(String Title){
    f.setTitle(Title);
  }
  /* Destroys the window and any resources associated with it */
  public void dispose(){
    f.dispose();
  }
  /** Draws the back-buffer contents to the screen */
  public void Refresh(){
    f.paint(f.getGraphics());
  }
  /** Returns: a Graphics object that can be used to draw on the back-buffer */
  public Graphics getBufferedGraphics(){
    return o.getGraphics();
  }
  /** Returns: a WritableRaster that cna be used to draw to the back-buffer */
  public WritableRaster getRaster(){
    return o.getRaster();
  }
  /** Returns: a byte array that contains the contents of the back-buffer.
   *   The pixels are stored in scanline order in packed RGB byte-components. */
  public byte[] getByteBuffer()

  {
    return o.getByteBuffer();
  }

  private OutputFrame f;
  private OutputComponent o = null;
   
  private class OutputFrame extends Frame {
    public OutputFrame(String Title) {
      super(Title);
    }

    public void paint(Graphics g) {
      paintComponents(g);
    }

    public void update(Graphics g) {
      paint(g);
    }
  }

  private class OutputComponent extends Component {

    private byte[] byteBuffer;
    private WritableRaster raster;
    private Image bufferedImage;

    public OutputComponent(int Width, int Height)
    {
    int ComponentOffset[] = {0,1,2};
    int ComponentBits[] = {8,8,8};

      byteBuffer = new byte[Width*Height*3];

      try {
        raster = Raster.createWritableRaster(
        new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, Width, Height, 3, Width*3,
        ComponentOffset),
        new DataBufferByte(byteBuffer, Width*Height*3), new Point(0,0));

        bufferedImage = new BufferedImage(
        new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB), ComponentBits,
        false, false, ColorModel.OPAQUE, DataBuffer.TYPE_BYTE),
        raster, false, null);
      } catch (Exception e) {
        System.out.println("Exception: "+e.getMessage());
      }

      Graphics g = bufferedImage.getGraphics();
      g.clearRect(0, 0, Width, Height);
    }

    public void paint(Graphics g)
    {
      g.drawImage(bufferedImage, 0, 0, this);
    }

    public void update(Graphics g)
    {
      paint(g);
    }

    public Graphics getBufferedGraphics()
    {
      return bufferedImage.getGraphics();
    }

    public WritableRaster getRaster()
    {
      return raster;
    }

    public byte[] getByteBuffer()
    {
      return byteBuffer;
    }
  }

}