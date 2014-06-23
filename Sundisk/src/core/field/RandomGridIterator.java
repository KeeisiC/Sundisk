package core.field;

import java.util.*;

import core.math.Vec2;
/**
 *   Iterates over an integer, rectangular grid in a pseudo-random order.
 * 
 * Because it is difficult to quickly produce a fully random ordering
 * of all the grid points, this class uses a pseudo-random technique:
 * The grid is divided evenly into square blocks, and a random point is
 * chosen in the top-left block. The corresponding points in all the rest of
 * the blocks are next in the sequence. Points are chosen in the block
 * in a truly random fashion until all the points on the grid are covered.
 * @author Andreas Sundquist
 * @version 1.0
 */
public class RandomGridIterator implements Vec2Iterator {

/** Constructs a RandomGridIterator that iterates pseudo-randomly over the
 * integer grid [0, width-1] x [0, height-1]. */
public RandomGridIterator(int width, int height){
    this(width, height, new Random());
  }

/** Constructs a RandomGridIterator.  This iterates pseudo-randomly over the
 * integer grid [0, width-1] x [0, height-1] using the random sequence
 * generator 'random'. */
  public RandomGridIterator(int width, int height, Random random) {
    xmin = 0;
    ymin = 0;
    xmax = width-1;
    ymax = height-1;
    blocksize = (width>height) ? width/8 : height/8;
    this.random = random;
    Initialize();
  }
  
  /** Constructs a RandomGridIterator that iterates pseudo-randomly over the
   * integer grid [xorigin, xorigin+width-1] x [yorigin, yorigin+height-1]. */
  public RandomGridIterator(int xorigin, int yorigin, int width, int height){
    this(xorigin, yorigin, width, height, new Random());
  }
  /** Constructs a RandomGridIterator that iterates pseudo-randomly over the
   * integer grid [xorigin, xorigin+width-1] x [yorigin, yorigin+height-1]
   * using the random sequence generator 'random'. */ 
  public RandomGridIterator(int xorigin, int yorigin, int width, int height, Random random){
    xmin = xorigin;
    ymin = yorigin;
    xmax = xorigin+width-1;
    ymax = yorigin+height-1;
    blocksize = (width>height) ? width/8 : height/8;
    this.random = random;
    Initialize();
  }
  /** Returns: null if there are no more points in the sequence, else
   *          a Vec2 whose value is the next point. The returned Vec2
   *          may be modified by the caller. The same Vec2 may be
   *          written to again on the subsequent call to next(). */ 
  public Vec2 next(){
    if (x>xmax) {
      x = xmin+gx;
      y += blocksize;
      if (y>ymax) {
        if (coverage>coveragelimit) {
          do {
            gx = random.nextInt(blocksize);
            gy = random.nextInt(blocksize);
          } while (covered[gy][gx]);
          covered[gy][gx] = true;
          --coverage;
          x = xmin+gx;
          y = ymin+gy;
        } else {
          if (coverage==0) {
            x = xmax+1;
            y = ymax+1;
            return null;
          }
          int n = random.nextInt(coverage);
          gx = gy = 0;
          while (covered[gy][gx] || (n>0)) {
            if (!covered[gy][gx])
              --n;
            if (++gx==blocksize) {
              gx = 0;
              ++gy;
            }
          }
          covered[gy][gx] = true;
          --coverage;
          x = xmin+gx;
          y = ymin+gy;
        }
      }
    }
    v.x = x;
    v.y = y;
    x += blocksize;
    return v;
  }

  private static int coveragelimit = 100;
  private int xmin, xmax, ymin, ymax;
  private int blocksize, coverage, gx, gy, x, y;
  private boolean[][] covered;
  private Random random;
  private Vec2 v = new Vec2();
  
  private void Initialize()
  {
    covered = new boolean[blocksize][blocksize];
    for (int j = 0; j<blocksize; ++j)
      for (int i = 0; i<blocksize; ++i)
        covered[j][i] = false;
    coverage = blocksize*blocksize;
    
    gx = x = xmax+1;
    gy = y = ymax+1;
  }
  
} 
