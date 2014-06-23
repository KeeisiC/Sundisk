package core.field;

import core.math.Vec2;
/** 
 *   Interface for producing a sequence of points on a plane.
 *
 * This interface is mostly used to iterate over a set of points in
 * a field. For example, ScanlineGridIterator produces the sequence of
 * integer points going left-right, top-down in scanline order, while 
 * RandomGridIterator produces pseudo-random points that cover a
 * rectangular region.
 */
public interface Vec2Iterator {
	
	  /** Returns: null if there are no more points in the sequence, else
	   *          a Vec2 whose value is the next point. The returned Vec2
	   *          may be modified by the caller. The same Vec2 may be
	   *          written to again on the subsequent call to next(). */ 
  public Vec2 next();
}