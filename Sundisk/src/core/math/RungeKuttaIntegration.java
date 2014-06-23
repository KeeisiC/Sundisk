package core.math;

import core.field.VecTimeField;

/** Runge Kutta Integration 4th order.  
 * @author Andreas Sundquist
 * @author John Belcher
 * @version 1.0 */

public class RungeKuttaIntegration {
  /** If this is true then we step to the requested point in steps of maxstep, 
   * and if it is false we step to the requested point directly in the one step requested. */
  boolean stepped;
  /** The maxstep we take to get to the point requested if stepped is true. */
  double maxStep;
  /** The number of first order equations we are integrating.  That is, this is 
   * the number of dependent variables. */
  int dim = 0;
  /** Variables used to compute the stages of the RK step. */
  private Vec x;
  /** The derivative matrix of the dependent variables we are integrating.  These are 
   * set in the <i>Motion</i> method of the experiment. */
  private VecTimeField field;
  
 /** Constructor for the straight Runge Kutta 4th order integration.   We step 
  * directly to where we want to go in one step.  */  
  public RungeKuttaIntegration(){
    this.stepped = false;
  }
  
 /** Constructor for the stepped Runge Kutta 4th order integration.   We step to 
  * where we want to go in steps that do not exceed maxstep.    
  * @param maxStep The maximum step we take. */ 
  public RungeKuttaIntegration(double maxStep){
    this.stepped = true;
    this.maxStep = maxStep;
  }
  
 /** Sets the maxStep if it has not already been set in the constructor call.  
  * @param maxStep The maximum step we can take. */ 
  public void SetStep(double maxStep){
    this.stepped = true;
    this.maxStep = maxStep;
  }

/** Evolve the system from x at s to the new x at x + ds, in one step or a number of small steps, 
 * depending on what is requested.  Returns the value of x at x + ds in the value of x.  DOES NOT
 * UPDATE THE VALUE OF S TO S + DS!!!!!  
 * @param field gives the time derivatives of the variables in x
 * @param x the vector state of the system, which depends on time 
 * @param s independent variable, e.g. time 
 * @param ds step size in the independent variable 
 * */  
  public void Evolve(VecTimeField field, Vec x, double s, double ds){
    this.field = field;
    this.x = x;
    this.dim = x.dim;
    if (ds>=0.0) {
      /* if stepped=true and ds > maxStep, we take a lot of small steps unti we get within maxstep of s+ds. */
      if (stepped)
        while (ds>maxStep) {
          ComputeStep(s, maxStep);
          s += maxStep;
          ds -= maxStep;
        }
      /* either take the full step for stepped=false or take the last 
       * small step to get to s+ds if stepped=true */
      ComputeStep(s, ds);
    } else {
      ds = -ds;
      if (stepped)
        while (ds>maxStep) {
          ComputeStep(s, -maxStep);
          s -= maxStep;
          ds -= maxStep;
        }
      ComputeStep(s, -ds);
    }
  }

    /** Compute one RK step from s to s + ds.  This is the fourth order RK step. 
     * @param s the independent variable 
     * @param ds the step in the independent variable */
    private void ComputeStep(double s, double ds){
      Vec newx2 = x.newVec();
      Vec newx3 = x.newVec();
      Vec newx4 = x.newVec();
      Vec dx1 = x.newVec();
      Vec dx2 = x.newVec();
      Vec dx3 = x.newVec();
      Vec dx4 = x.newVec();
  	  Vec k1 = x.newVec();
      Vec k2 = x.newVec();
      Vec k3 = x.newVec();
      Vec k4 = x.newVec();
      /* calculate the first estimate k1 of the change in dependent variables at (s + ds) using the derivative
       * at (s,x) */
      k1.AddScaled(field.get(x, s, dx1), ds);
      /* calculate the new values of the dependent values AT THE MIDPOINT using the derivative dx1 at (s,x) */
      newx2 = x.addscaled(dx1, 0.5*ds);  
      /* calculate the second estimate k2 of the change in dependent variables at (s + ds) using the derivative
       * at (s+ds/2,x+k1/2) */
      k2.AddScaled(field.get(newx2, s + 0.5*ds, dx2), ds);
      /* calculate the new values of the dependent values AT THE MIDPOINT using the derivative at (s+ds/2,x+k2/2) */
      newx3 = x.addscaled(dx2, 0.5*ds);  
      /* calculate the third estimate k3 of the change in dependent variables at (s + ds) using the derivative
       * at (s+ds/2,x+k2/2) */
      k3.AddScaled(field.get(newx3, s + 0.5*ds, dx3), ds);
      /* calculate the new values of the dependent values AT THE ENDPOINT using the derivative at (s+ds/2,x+k2/2) */
      newx4 = x.addscaled(dx3, ds);    
      k4.AddScaled(field.get(newx4, s + ds, dx4), ds);
      /* compute the new dependent variable array at s + ds */
      x.AddScaled(k1.add(k4), 1.0/6.0); 
      x.AddScaled(k2.add(k3),1./3.);
    } 
}