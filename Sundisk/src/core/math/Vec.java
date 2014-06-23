package core.math;

/** 
 * an n-dimensional vector
 *
 * = < n, (x0, x1, ..., xn-1) >.
 *
 * This class stores and operates on an arbitrary-dimension vector.
 * Methods that begin with a capital letter modify "this", while
 * methods that begin with a lowercase letter create a new Vec
 * to store the result.
 */

public class Vec {
	
  /** dim = n, the dimension of this vector */
  public int dim;
  /** x[] stores the components of this vector */
  public double[] x;
  
 /** Constructs a zero-vector of "dim"-dimension */  
  public Vec(int dim){
    this.dim = dim;
    x = new double[dim];
    for (int i = 0; i<dim; ++i)
    x[i] = 0.0;
  }
 /** Constructs a new vector that is a copy of vector "v" */ 
  public Vec(Vec v){
    dim = v.dim;
    x = new double[dim];
    for (int i = 0; i<dim; ++i)
    x[i] = v.x[i];
  }
/** Returns a new zero-vector of the same dimension as "this" */  
  public Vec newVec(){
    return new Vec(dim);
  }
  /** Returns a copy of "this" */
  public Vec copy(){
    return new Vec(this);
  }
  /** Used internally to verify that "v" has the same dimension as "this" */ 
  private void assertDim(Vec v){
    if (dim!=v.dim)
      throw new RuntimeException("Vec.assertDim: Dimension mismatch!");
  }
  
  /** tolerance is the per-component zero-value maximum */
  public static double tolerance = 1.0e-12;
  /** Sets the value of "this" to that of "v"
   * Returns: resulting "this"
   * Requres: "this" and "v" have the same dimension */
  public Vec Set(Vec v){
    assertDim(v);
    for (int i = 0; i<dim; ++i)
      x[i] = v.x[i];
    return this;
  }
  /** Sets "this" to be the zero-vector with the same dimension as "this"
   * Returns: resulting "this" */ 
  public Vec SetZero(){
    for (int i = 0; i<dim; ++i)
      x[i] = 0.0;
    return this;
  }
  /** Returns: true if "this" is the zero-vector, false otherwise */ 
  public boolean isZero(){
    for (int i = 0; i<dim; ++i)
      if (Math.abs(x[i])>=tolerance)
        return false;
    return true;
  }
  /** Returns: true if "this" and "v" are equal, false otherwise
   * Requires: "this" and "v" have the same dimension */  
  public boolean equals(Vec v){
    assertDim(v);
    for (int i = 0; i<dim; ++i)
      if (Math.abs(x[i]-v.x[i])>=tolerance)
        return false;
    return true;
  }
  /** Adds "v" to "this"
   * Returns: resulting "this"
   * Requires: "this" and "v" have the same dimension */ 
  public Vec Add(Vec v){
    assertDim(v);
    for (int i = 0; i<dim; ++i)
      x[i] += v.x[i];
    return this;
  }
  /** Returns: a new Vec that is the sum of "this" and "v"
   * Requires: "this" and "v" have the same dimension */ 
  public Vec add(Vec v)

  {
    assertDim(v);
    Vec z = new Vec(dim);
    for (int i = 0; i<dim; ++i)
      z.x[i] = x[i] + v.x[i];
    return z;
  }
  /** Adds "s*v" to "this"
   * Returns: resulting "this"
   * Requires: "this" and "v" have the same dimension */  
  public Vec AddScaled(Vec v, double s){
    assertDim(v);
    for (int i = 0; i<dim; ++i)
      x[i] += s*v.x[i];
    return this;
  }
  
  /** Returns: a new Vec that is the sum of "this" and "s*v"
   * Requires: "this" and "v" have the same dimension */
  public Vec addscaled(Vec v, double s){
    assertDim(v);
    Vec z = new Vec(dim);
    for (int i = 0; i<dim; ++i)
      z.x[i] = x[i] + s*v.x[i];
    return z;
  }
  
  /** Subtracts "v" from "this"
   * Returns: resulting "this"
   * Requres: "this" and "v" have the same dimension */  
  public Vec Sub(Vec v){
    assertDim(v);
    for (int i = 0; i<dim; ++i)
      x[i] -= v.x[i];
    return this;
  }
  
  /** Returns: a new Vec that is the difference between "this" and "v"
   * Requires: "this" and "v" have the same dimension */ 
  public Vec sub(Vec v){
    assertDim(v);
    Vec z = new Vec(dim);
    for (int i = 0; i<dim; ++i)
      z.x[i] = x[i] - v.x[i];
    return z;
  }
  
  /** Negates "this"
   * Returns: resulting "this" */ 
  public Vec Neg(){
    for (int i = 0; i<dim; ++i)
      x[i] = -x[i];
    return this;
  }
  
  /** Returns: a new Vec that is the opposite of "this" */  
  public Vec neg(){
    Vec z = new Vec(dim);
    for (int i = 0; i<dim; ++i)
      z.x[i] = -x[i];
    return z;
  }
  /** Returns: the dot product of "this" and "v"
   * Requires: "this" and "v" have the same dimension */ 
  public double dot(Vec v){
    assertDim(v);
    double sum = 0.0;
    for (int i = 0; i<dim; ++i)
      sum += x[i]*v.x[i];
    return sum;
  }
  /** Returns: the magnitude of "this" */  
  public double len(){
    double sum = 0.0;
    for (int i = 0; i<dim; ++i)
      sum += x[i]*x[i];
    return Math.sqrt(sum);
  }
  /** Returns: the magnitude squared of "this" */ 
  public double len2(){
    double sum = 0.0;
    for (int i = 0; i<dim; ++i)
      sum += x[i]*x[i];
    return sum;
  }
  
  /** Returns: the magnitude cubed of "this" */  
  public double len3(){
    double sum = 0.0;
    for (int i = 0; i<dim;  ++i)
      sum += x[i]*x[i];
    return Math.pow(sum, 1.5);
  }
  
  /** Scales "this" by "s"
   * Returns: resulting "this" */ 
  public Vec Scale(double s){
    for (int i = 0; i<dim; ++i)
      x[i] *= s;
    return this;
  }
  /** Returns: a new Vec that is "s*this" */ 
  public Vec scale(double s) {
    Vec z = new Vec(dim);
    for (int i = 0; i<dim; ++i)
      z.x[i] = s*x[i];
    return z;
  }
  
  /** Scales each component of "this" by the corresponding component of "v"
   * Returns: resulting "this"
   * Requires: "this" and "v" have the same dimension */ 
  public Vec Scale(Vec v){
    assertDim(v);
    for (int i = 0; i<dim; ++i)
      x[i] *= v.x[i];
    return this;
  }
  
  /** Returns: a new Vec that is "this" with its components scaled by "v"
   * Requires: "this" and "v" have the same dimension */ 
  public Vec scale(Vec v){
    assertDim(v);
    Vec z = new Vec(dim);
    for (int i = 0; i<dim; ++i)
      z.x[i] = x[i]*v.x[i];
    return z;
  }
  
  /** Rescales "this" to be of unit magnitude
   * Returns: resulting "this"
   * Requires: "this" is not zero */  
  public Vec Unit() {
    double l = len();
    if (l==0.0)
      throw new RuntimeException("Vec.Unit: Zero vector!");
    else
      Scale(1.0/l);
    return this;
  }
  /** Returns: a new Vec that is "this" scaled to unit magnitude
   * Requires: "this" is not zero */   
  public Vec unit(){
    double l = len();
    if (l==0.0)
      throw new RuntimeException("Vec3.unit: Zero vector!");
    else
      return scale(1.0/l);
  }
  /** Isolates the component of "this" parallel to "d"
   * Returns: resulting "this"
   * Requires: "d" is not zero */ 
  public Vec Para(Vec d) {
    Vec du = d.unit();
    du.Scale(du.dot(this));
    Set(du);
    return this;
  }
  
  /** Returns: a new Vec that is the component of "this" parallel to "d"
   * Requires: "d" is not zero */ 
  public Vec para(Vec d){
    Vec du = d.unit();
    du.Scale(du.dot(this));
    return du;
  }
  /** Isolates the component of "this" parallel to "d"
   * Returns: resulting "this"
   * Requires: "d" has unit magnitude */ 
  public Vec ParaUnit(Vec d){
    Set(d.scale(d.dot(this)));
    return this;
  }
  /** Returns: a new Vec that is the component of "this" parallel to "d"
   * Requires: "d" has unit magnitude */  
  public Vec paraunit(Vec d) {
    return d.scale(d.dot(this));
  }
  /** Isolates the component of "this" perpendicular to "d"
   * Returns: resulting "this"
   * Requires: "d" is not zero */ 
  public Vec Perp(Vec d){
    Set(this.sub(para(d)));
    return this;
  }
  /** Returns: a new Vec that is the component of "this" perpendicular to "d"
   * Requires: "d" is not zero */ 
  public Vec perp(Vec d){
    return this.sub(para(d));
  }
  /** Isolates the component of "this" perpendicular to "d"
   * Returns: resulting "this"
   * Requires: "d" has unit magnitude */  
  public Vec PerpUnit(Vec d){
    Set(this.sub(paraunit(d)));
    return this;
  }
  
  /** Returns: a new Vec that is the component of "this" perpendicular to "d"
   * Requires: "d" has unit magnitude */ 
  public Vec perpunit(Vec d){
    return this.sub(paraunit(d));
  }
  
  /** Returns: a new Vec2 that is equal to "this"
   * Requires: "this" is 2-dimensional */ 
  public Vec2 toVec2(){
    if (dim!=2)
      throw new RuntimeException("Vec.toVec2: Dimension mismatch!");
    return new Vec2(x[0], x[1]);
  }
  
  /** Returns: a new Vec3 that is equal to "this"
   * Requires: "this" is 3-dimensional */
  public Vec3 toVec3(){
    if (dim!=3)
      throw new RuntimeException("Vec.toVec3: Dimension mismatch!");
    return new Vec3(x[0], x[1], x[2]);
  }
  
}