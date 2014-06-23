package core.math;

/** Mat2 is a 2x2 matrix
 * = < [[a, b],
 *      [c, d]] >
 *
 * This class stores and operates on a 2x2 matrix.
 * Methods that begin with a capital letter modify "this", while
 * methods that begin with a lowercase letter create a new Mat2
 * to store the result.
 */
public class Mat2 {
  /** a,b,c,d are the components of the matrix */
  public double a,b,c,d;
  
  /** Constructs a new Mat2 = [[1, 0],
   *                          [0, 1]] */ 
  public Mat2(){
    SetIdent();
  }
  /** Constructs a new Mat2 = "m" */ 
  public Mat2(Mat2 m){
    a = m.a;
    b = m.b;
    c = m.c;
    d = m.d;
  }
  /** Constructs a new Mat2 = [[a, b],
   *                          [c, d]] */  
  public Mat2(double a, double b, double c, double d){
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }
  
  /** Do not modify Zero = the zero matrix*/
  public static Mat2 Zero = new Mat2(0.0, 0.0, 0.0, 0.0);
   /** Do not modify Ident = the identity matrix */
  public static Mat2 Ident = new Mat2(1.0, 0.0, 0.0, 1.0);
  /** Do not modify Invalid = an invalid Mat2 */
  public static Mat2 Invalid = new Mat2(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
                                        Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
  /** Do not modify tolerance is the per-component zero-value maximium */
  public static double tolerance = 1.0e-12;
  
  /** Sets the value of "this" to that of "m"
   * Returns: resulting "this" */  
  public Mat2 Set(Mat2 m){
    a = m.a;
    b = m.b;
    c = m.c;
    d  = m.d;
    return this;
  }
  /** Sets "this" to [[a, b],
   *                 [c, d]].
   * Returns: resulting "this" */ 
  public Mat2 Set(double a, double b, double c, double d){
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
    return this;
  }
  /** Sets "this" to zero.
   * Returns: resulting "this" */ 
  public Mat2 SetZero(){
    a = b = c = d = 0.0;
    return this;
  }
  /** Sets "this" to the identity matrix.
   * Returns" resulting "this" */ 
  public Mat2 SetIdent(){
    a = d = 1.0;
    b = c = 0.0;
    return this;
  }
  /** Returns: true if "this" is zero, false otherwise */
  public boolean isZero(){
    return (Math.abs(a)<tolerance) && (Math.abs(b)<tolerance) && 
           (Math.abs(c)<tolerance) && (Math.abs(d)<tolerance);
  }
  /** Returns: true if "this" equals "m", false otherwise */  
  public boolean equals(Mat2 m){
    return (Math.abs(a-m.a)<tolerance) && (Math.abs(b-m.b)<tolerance) && 
           (Math.abs(c-m.c)<tolerance) && (Math.abs(d=m.d)<tolerance);
  }
  /** Adds "m" to "this"
   * Returns: resulting "this" */
  public Mat2 Add(Mat2 m){
    a += m.a;
    b += m.b;
    c += m.c;
    d += m.d;
    return this;
  }
  /** Returns: a new Mat2 that is the sum of "this" and "m" */ 
  public Mat2 add(Mat2 m){
    return new Mat2(a + m.a, b + m.b, c + m.c, d + m.d);
  }
  /** Adds "s*m" to "this"
   * Returns: resulting "this" */ 
  public Mat2 AddScaled(Mat2 m, double s){
    a += s*m.a;
    b += s*m.b;
    c += s*m.c;
    d += s*m.d;
    return this;
  }
  /** Returns: a new Mat2 that is the sum of "this" and "s*m" */ 
  public Mat2 addscaled(Mat2 m, double s){
    return new Mat2(a + s*m.a, b + s*m.b, c + s*m.c, d + s*m.d);
  }
  /** Subtracts "m" from "this" 
   * Returns: resulting "this" */ 
  public Mat2 Sub(Mat2 m){
    a -= m.a;
    b -= m.b;
    c -= m.c;
    d -= m.d;
    return this;
  }
  /** Returns: a new Mat2 that is the difference between "this" and "m" */
  public Mat2 sub(Mat2 m){
    return new Mat2(a - m.a, b - m.b, c - m.c, d - m.d);
  }
  /** Negates "this"
   * Returns: resulting "this" */ 
  public Mat2 Neg(){
    a = -a;
    b = -b;
    c = -c;
    d = -d;
    return this;
  }
  /** Returns: a new Mat2 that is the opposite of "this" */  
  public Mat2 neg() {
    return new Mat2(-a, -b, -c, -d);
  }
  /** Returns: the dot product of "this" and "m" */  
  public double dot(Mat2 m){
    return a*m.a + b*m.b + c*m.c + d*m.c;
  }
  /** Scales "this" by "s"
   * Returns: resulting "this" */ 
  public Mat2 Scale(double s){
    a *= s;
    b *= s;
    c *= s;
    d *= s;
    return this;
  }
  /** Returns: a new Mat2 that is "s*this" */  
  public Mat2 scale(double s){
    return new Mat2(a*s, b*s, c*s, d*s);
  }
  /** Multiplies "this" to the left by "m"
   * Returns: resulting "this" */ 
  public Mat2 Mul(Mat2 m){
    double ap = m.a*a + m.b*c;
    double bp = m.a*b + m.b*d;
    double cp = m.c*a + m.d*c;
    double dp = m.c*b + m.d*d;
    a = ap;
    b = bp;
    c = cp;
    d = dp;
    return this;
  }
  /** Returns: a new Mat2 that is "m*this" */ 
  public Mat2 mul(Mat2 m){
    return new Mat2(m.a*a + m.b*c, m.a*b + m.b*d, m.c*a + m.d*c, m.c*b + m.d*d);
  }
  /** Multiplies "this" to the right by "m"
   * Returns: resulting "this" */ 
  public Mat2 RMul(Mat2 m){
    double ap = a*m.a + b*m.c;
    double bp = a*m.b + b*m.d;
    double cp = c*m.a + d*m.c;
    double dp = c*m.b + d*m.d;
    a = ap;
    b = bp;
    c = cp;
    d = dp;
    return this;
  }
  /** Returns: a new Mat2 that is "this*m" */  
  public Mat2 rmul(Mat2 m){
    return new Mat2(a*m.a + b*m.c, a*m.b + b*m.d, c*m.a + d*m.c, c*m.b + d*m.d);
  }
  /** Returns: a new Vec2 that is "this*v" */  
  public Vec2 mul(Vec2 v){
    return new Vec2(a*v.x + b*v.y, c*v.x + d*v.y);
  }
  /** Returns: a new Vec2 that is "v*this" */ 
  public Vec2 lmul(Vec2 v) {
    return new Vec2(a*v.x + c*v.y, b*v.x + d*v.y);
  }
  /** Returns: the determinant of "this" */  
  public double det() {
    return a*d - b*c;
  }
  /** Inverts "this"
   * Returns: resulting "this"
   * Requires: "this" is not singular */ 
  public Mat2 Invert(){
    double det = a*d - b*c;
    if (det==0.0)
      throw new RuntimeException("Mat2.Invert: Singular matrix!");
    det = 1.0/det;
    double temp = a;
    a = d*det;
    d = temp*det;
    b = -b*det;
    c = -c*det;
    return this;
  }
  /** Returns: a new Mat2 that is the inverse of "this"
   * Requires: "this" is not singular */  
  public Mat2 invert(){
    double det = a*d - b*c;
    if (det==0.0)
      throw new RuntimeException("Mat2.invert: Singular matrix!");
    det = 1.0/det;
    return new Mat2(d*det, -b*det, -c*det, a*det);
  }
  /** Returns: a new Vec2 containing the x-axis of "this" when it is used as
   *   a linear transform left-multiplying a vector */  
  public Vec2 xaxis(){
    return new Vec2(a, c);
  }
  /* Returns: a new Vec2 containing the y-axis of "this" when it is used as
   *   a linear transform left-multiplying a vector */ 
  public Vec2 yaxis(){
    return new Vec2(b, d);
  }
  /* Returns: true if the x- and y-axes of "this" as a left-multiplying
   *   linear transform are orthogonal, false otherwise */ 
  public boolean isOrthogonal(){
    return (Math.abs(xaxis().dot(yaxis()))<tolerance);
  }
  /** Returns: true if the x- and y-axes of "this" as a left-multiplying
   *   linear transform are orthonormal, false otherwise */
  public boolean isOrthoNormal(){
    if (!isOrthogonal())
      return false;
    if (Math.abs(xaxis().len()-1.0)>=tolerance)
      return false;
    if (Math.abs(yaxis().len()-1.0)>=tolerance)
      return false;
    return true;
  }
  /** Returns: true if the x- and y-axes of "this" as a left-multiplying
   *   linear transform are orthogonal and of equal magnitude, else false */  
  public boolean isOrthoScaled(){
    if (!isOrthogonal())
      return false;
    if (Math.abs(xaxis().len()-yaxis().len())>=tolerance)
      return false;
    return true;
  }
  /** Returns: the geometric mean of the magnitude of the x- and y-axes of
   *   "this" used as a left-multiplying linear transform */
  public double getScale(){
    return Math.sqrt(xaxis().len() * yaxis().len());
  }
  
}