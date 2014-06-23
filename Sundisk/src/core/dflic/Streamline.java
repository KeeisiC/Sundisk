package core.dflic;

import java.util.*;

import core.field.Vec2Field;
import core.field.Vec2UnitField;
import core.math.Vec2;

/** Computes sample points along streamlines.
*
* This class is used by FLIC in order to trace streamlines and compute
* equispaced sample points along them. Though it is publicly declared,
* it probably isn't very useful for anything else.
*  @author Andreas Sundquist
* @version 1.0 
*/
public class Streamline {
  private Vec2Field field;
  private double length, stepsize;
  private boolean reverse;
  private double minstep, maxstep, tolerance, lengthstep;
  private double safety = 0.99;
  private Vector singularities = new Vector();
  private double singularitylimit;
  
  /** Constructs a new Streamline object that stores the parameters for
   *   successive streamline computations. The field that is traced is set to
   *   "field". All the remaining parameters are set to their default values. */ 
  public Streamline(Vec2Field field){
    SetField(field);
    SetLength(0.0);
    SetDefaultStepSize();
    SetDefaultMinStep();
    SetDefaultMaxStep();
    SetDefaultTolerance();
    SetDefaultSingularityLimit();
  }
  /** Constructs a new Streamline object that stores the parameters for
   *   successive streamline computations. The field that is traced is set to
   *   "field", and the sample step size along the field lines is set to
   *   "stepsize". All other parameters are set to their deafult values. */ 
  public Streamline(Vec2Field field, double stepsize){
    SetField(field);
    SetLength(0.0);
    SetStepSize(stepsize);
    SetDefaultMinStep();
    SetDefaultMaxStep();
    SetDefaultTolerance();
    SetDefaultSingularityLimit();
  }
 
  /** Constructs a new Streamline object that stores the parameters for
   *   successive streamline computations. The field that is traced is set to
   *   "field", the sample step size along the lines is set to "stepsize",
   *   the minimum and maximum RK4 step sizes are se to "minstep" and "maxstep",
   *   and the integration error tolerance is set to "tolerance". */
  public Streamline(Vec2Field field, double stepsize, double minstep, double maxstep, double tolerance){
    SetField(field);
    SetLength(0.0);
    SetStepSize(stepsize);
    SetMinStep(minstep);
    SetMaxStep(maxstep);
    SetTolerance(tolerance);
    SetDefaultSingularityLimit();
  }
  /** Sets the vector field that is traced to "field" */ 
  public void SetField(Vec2Field field){
    this.field = new Vec2UnitField(field);
  }
  /** Sets the distance that the streamline traced to "length". If length is
   *   positive, the streamline goes along the direction of the vector field,
   *   while if it is negative, it goes in the opposite direction. */ 
  public void SetLength(double length){
    this.length = abs(length);
    reverse = (length<0.0);
  }
  /** Sets the spacing between sample points on the streamline to "stepsize" */ 
  public void SetStepSize(double stepsize){
    this.stepsize = stepsize;
  }
  /** Sets the default sample spacing, which is 0.5 */ 
  public void SetDefaultStepSize(){
    SetStepSize(0.5);
  }
  /** Sets the minimum RK4 step size before terminating to "minstep" */  
  public void SetMinStep(double minstep){
    this.minstep = minstep;
  }
  /** Sets the default minimum RK4 step size, which is 10^-1 */  
  public void SetDefaultMinStep(){
    SetMinStep(1e-1);
  }
 /** Sets the maximum RK4 step size to "maxstep" */ 
  public void SetMaxStep(double maxstep){
    this.maxstep = maxstep;
  }
  /** Sets the default maximum RK4 step size, which is 10 */ 
  public void SetDefaultMaxStep(){
    SetMaxStep(1e1);
  }
  /** Sets the RK4 integrator error tolerance to "tolerance". If the error 
   *   during a single step is every larger, the step size is reduced. */ 
  public void SetTolerance(double tolerance){
    this.tolerance = tolerance;
  }
  /** Sets the default error tolerance, which is 5*10^-2 */  
  public void SetDefaultTolerance(){
    SetTolerance(5e-2);
  }
  /** Sets the radius around a singularity at which a streamline will be
   *   terminated to "limit". */  
  public void SetSingularityLimit(double limit){
    this.singularitylimit = limit*limit;
  }
  /** Sets the default singularity limit to the step size. */
  public void SetDefaultSingularityLimit(){
    //SetSingularityLimit(Math.pow(minstep*minstep*maxstep, 1.0/3));
    SetSingularityLimit(stepsize);
  }
  /** Adds a singularity where streamlines are terminated when they come
   *   within the singularity limit. The point "v" is given in the coordinate
   *   system of the vector field. */ 
  public void AddSingularity(Vec2 v){
    singularities.add(v);
  }
  
  private double abs(double x){
    return (x<0) ? (-x) : x;
  }
  /** Begins the streamline computation at point "p" with the default length. */  
  public void Start(Vec2 p){
    ComputeStart(p);
  }
  /** Begins the streamline computation at point "p" with a length "length".
   *   If "length" is positive, it goes along the direction of the vector field,
   *   and if it is negative it goe sin the opposite direction. */ 
  public void Start(Vec2 p, double length){
    SetLength(length);
    ComputeStart(p);
  }
  /** Returns the next point along the streamline. If there are no more points,
   *   it returns null. The point returned may be modified. */ 
  public Vec2 Next(){
    if (num<0)
      return null;
    if (ComputeNext())
      return v;
    else
      if (num==0) {
        num = -1;
        return v;
      } else {
        num = -1;
        return null;
      }
  }
  /** Terminates the streamline immediately. Further calls to Next() will return
   *   null until a new streamline is started. */
  public void Stop(){
    num = -1;
  }
  /** Returns whether or not the streamline has reached its end. */ 
  public boolean stopped(){
    return (num==-1);
  }
  
  /* Temporary private variables */
  private Vec2 x = new Vec2(), v = new Vec2();
  private Vec2 dx = new Vec2(), odx = new Vec2();
  private Vec2 nx = new Vec2(), tx = new Vec2();
  private Vec2 A = new Vec2(), B = new Vec2(), C = new Vec2(), D = new Vec2();
  private Vec2 cx = new Vec2(), cx2 = new Vec2(), cx3 = new Vec2(), cdx = new Vec2();
  private double ds, step, l, l1, l2;
  private int num;
  
  private void ComputeStart(Vec2 x0)
  {
    ds = length;
    step = maxstep;
    lengthstep = Math.sqrt(minstep*maxstep);
    num = 0;
    l = l1 = l2 = 0.0;
    
    nx.Set(x0);
    field.get(nx, dx);
    v.Set(x0);
  }
  
  private boolean ComputeNext()
  {
    if (l<l2) {
      v.Set(cx);
      cx3.Add(cdx);
      cx2.Add(cx3);
      cx.Add(cx2);
      l += stepsize;
      ++num;
      return true;
    }
    
    while ((ds>0.0) && (!dx.isZero())) {
      x.Set(nx);
      odx.Set(dx);
      l1 = l2;
      
      if (step>ds)
        step = ds;
      double laststep, error;
      do {
        laststep = step;
        if (reverse)
          step = -step;
          
        tx.Set(odx).Scale(0.5*step);
        nx.Set(x).AddScaled(odx, 0.5*step);
        
        field.get(nx, dx);
        tx.AddScaled(dx, step);
        nx.Set(x).AddScaled(dx, 0.5*step);
        
        field.get(nx, dx);
        tx.AddScaled(dx, step);
        nx.Set(x).AddScaled(dx, step);
        
        field.get(nx, dx);
        nx.Set(x).AddScaled(tx.AddScaled(dx, 0.5*step), 1.0/3.0);
        
        tx.Set(dx);
        field.get(nx, dx);
        tx.Sub(dx).Scale(step);
        error = (1.0/6.0)*tx.len();
        
        if (reverse)
          step = -step;
          
        if (error==0.0)
          step = (ds<maxstep) ? ds : maxstep;
        else {
          step *= Math.pow(safety*tolerance/error, 0.2);
          if (step>maxstep)
            step = maxstep;
        }
        if (laststep<lengthstep) {
          tx.Set(nx).Sub(x);
          double dl = 0.25*tx.len();
          if (reverse)
            dl = -dl;
          laststep = tx.Set(odx).Sub(dx).Scale(dl).Add(nx).Sub(x).Scale(0.5).len();
          laststep += tx.Add(x).Sub(nx).len();
        }
      } while ((error>tolerance) && (laststep>minstep));
      if ((error>tolerance) || (laststep<minstep))
        return false;
      ds -= laststep;
      
      Enumeration enume = singularities.elements();
      while (enume.hasMoreElements()) {
        Vec2 v = (Vec2)enume.nextElement();
        if (A.Set(v).Sub(nx).len2()<singularitylimit)
          return false;
      }
      
      l2 = l1 + laststep;
      if (l2>length)
        l2 = length;
      if ((l1<=l) && (l<l2)) {
        double dl = reverse ? (-laststep) : laststep;
        
        A.SetZero().AddScaled(x, 2.0).AddScaled(nx, -2.0).AddScaled(odx, dl).AddScaled(dx, dl);
        B.SetZero().AddScaled(nx, 3.0).AddScaled(x, -3.0).AddScaled(odx, -2.0*dl).AddScaled(dx, -dl);
        C.Set(odx).Scale(dl);
        D.Set(x);
        
        dl = abs(dl);
        double t = (l - l1)/dl;
        double dt = stepsize/dl;        
        
        cx.Set(A).Scale(t).Add(B).Scale(t).Add(C).Scale(t).Add(D);
        t -= dt;
        cx2.Set(A).Scale(t).Add(B).Scale(t).Add(C).Scale(t).Add(D);
        t -= dt;
        cx3.Set(A).Scale(t).Add(B).Scale(t).Add(C).Scale(t).Add(D);
        
        cx3.Add(cx).AddScaled(cx2, -2.0);
        cx2.Neg().Add(cx);
        
        dt = dt*dt*dt;
        cdx.Set(A).Scale(6.0*dt);
        
        v.Set(cx);
        cx3.Add(cdx);
        cx2.Add(cx3);
        cx.Add(cx2);
        l += stepsize;
        return true;
      }
    }
    return false;    
  }
    
}