package core.math;

/** Stores and operates on a 2-dimensional affine transformation.
 * The transformation is given by = < m, o > s.t. v' = m*v + o.
 *
 */
public class Vec2Transform {

  
  /** matrix = m, the linear transformation */
  protected Mat2 matrix;
  /** origin = o, the translation */
  protected Vec2 origin;
  /** Constructs a new Vec2Transform = < scale*I, origin > */ 
  public Vec2Transform(Vec2 origin, double scale)

  {
    this.origin = origin;
    matrix = new Mat2();
    matrix.SetIdent();
    matrix.Scale(scale);
  }
  /** Constructs a new Vec2Transform = < matrix, origin > */
  public Vec2Transform(Vec2 origin, Mat2 matrix)

  {
    this.origin = origin;
    this.matrix = matrix;
  }
  /** Constructs a new Vec2Transform = < [ xaxis, yaxis ], origin > */  
  public Vec2Transform(Vec2 origin, Vec2 xaxis, Vec2 yaxis)

  {
    this.origin = origin;
    matrix = new Mat2(xaxis.x, yaxis.x, xaxis.y, yaxis.y);
  }
  /**  Constructs a new Vec2Transform = 'transform' */
  public Vec2Transform(Vec2Transform transform)
 
  {
    matrix = transform.matrix;
    origin = transform.origin;
  }
  /** Returns: a new Vec2Transform that is the inverse of 'this' */
  public Vec2Transform invert()

  {
    Mat2 pmatrix = matrix.invert();
    Vec2 porigin = matrix.mul(origin.neg());
    return new Vec2Transform(porigin, pmatrix);
  }
  /** Returns: a new Vec2Transform that is the concatenation of 'this'
   *   followed by 'transform' */ 
  public Vec2Transform concatenate(Vec2Transform transform)

  {
    Mat2 pmatrix = matrix.mul(transform.matrix);
    Vec2 porigin = transform.matrix.mul(origin).add(transform.origin);
    return new Vec2Transform(porigin, pmatrix);
  }
  /** Returns: a new Vec2Transform that is 'this' translated by 'origin' */
  public Vec2Transform translate(Vec2 origin)

  {
    return new Vec2Transform(this.origin.add(origin), matrix);
  }
  /** Returns: a new Vec2Transform that is 'this' scaled by 'scale' */
  public Vec2Transform scale(double scale)

  {
    return new Vec2Transform(origin.scale(scale), matrix.scale(scale));
  }
  /** Returns: a new Vec2Transform that is 'this' rotated counter-clockwise
   *   by 'theta' */ 
  public Vec2Transform rotate(double theta)

  {
    double c = Math.cos(theta);
    double s = Math.sin(theta);
    Mat2 pmatrix = new Mat2(c, s, -s, c);
    return concatenate(new Vec2Transform(Vec2.Zero, pmatrix));
  }
  /** Returns: a new Vec2Transform that is 'this' reflected across 'd' */ 
  public Vec2Transform reflect(Vec2 d)

  {
    d = d.unit();
    Mat2 pmatrix = new Mat2(2*d.x*d.x-1, 2*d.x*d.y, 2*d.x*d.y, 2*d.y*d.y-1);
    return concatenate(new Vec2Transform(Vec2.Zero, pmatrix));
  }
  /** Returns: true if 'this' is a rigid transformation, otherwise false */
  public boolean isRigid()
 
  {
    return matrix.isOrthoNormal();
  }
  /** Returns: true if 'this' is a scaled rigid transformation, else false */
  public boolean isRigidScaled()

  {
    return matrix.isOrthoScaled();
  }
  /** Returns: the scaling of the transform, assuming it is rigid */ 
  public double getScale()

  {
    return matrix.getScale();
  }
  /** Returns: a new Vec2 that is 'p' transformed by 'this' */ 
  public Vec2 v(Vec2 p)

  {
    return matrix.mul(p).Add(origin);
  }
  /** Transforms 'p' y 'this'
   * Returns: resulting 'p' */
  public Vec2 V(Vec2 p)

  {
    p.Mul(matrix).Add(origin);
    return p;
  }
  /** Sets v to the transformation of 'p' by 'this'
   * Returns: resulting 'v' */ 
  public Vec2 V(Vec2 p, Vec2 v)

  {
    v.Set(p).Mul(matrix).Add(origin);
    return v;
  }
  
}