package core.field;

import core.math.Vec2;

 /**   Iterates over an integer, rectangular grid in scanline order. 
  * @author  Andreas Sundquist
  * @version 1.0 */

public class ScanlineGridIterator implements Vec2Iterator {

/** Constructs a ScanlineGridIterator that iterates over the integer
* grid [0, width-1] x [0, height-1] in left-right, top-down order. */
  public ScanlineGridIterator(int width, int height){
    this(0, 0, width, height);
  }
  /** Constructs a ScanlineGridIterator that iterates over the integer grid
   * [xorigin, xorigin+width-1] x [yorigin, yorigin+height-1] in left-right,
   * top-down order. */  
  public ScanlineGridIterator(int xorigin, int yorigin, int width, int height){
    xmin = xorigin;
    ymin = yorigin;
    xmax = xorigin+width-1;
    ymax = yorigin+height-1;
    x = xmin-1;
    y = ymin;
  }
  /** Returns: null if there are no more points in the sequence, else
   *          a Vec2 whose value is the next point. The returned Vec2
   *          may be modified by the caller. The same Vec2 may be
   *          written to again on the subsequent call to next(). */  
  public Vec2 next(){
    ++x;
    if (x>xmax) {
      x = xmin;
      ++y;
      if (y>ymax)
        return null;
    }
    v.x = x;
    v.y = y;
    return v;
  }

  private int xmin, xmax, ymin, ymax;
  private int x, y;
  private Vec2 v = new Vec2();
  
} 
