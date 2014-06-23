package core.math;

import java.math.*;

import core.field.VecTimeField;
/** Euler integration scheme */

public class EulerIntegration {
  
  boolean stepped;
  double maxStep;
  
  public EulerIntegration()
  {
    stepped = false;
  }
  
  public EulerIntegration(double maxStep)
  {
    stepped = true;
    this.maxStep = maxStep;
  }
  
  public void SetStep(double maxStep)
  {
    stepped = true;
    this.maxStep = maxStep;
  }
  
  public void Evolve(VecTimeField field, Vec x, double s,double ds)
  {
    Vec dx = x.newVec();
    
    if (ds>=0.0) {
      if (stepped)
        while (ds>maxStep) {
          dx = field.get(x, s);
          x.AddScaled(dx, maxStep);
          s += maxStep;
          ds -= maxStep;
        }
      field.get(x, s, dx);
      x.AddScaled(dx, ds);
    } else {
      ds = -ds;
      if (stepped)
        while (ds>maxStep) {
          dx = field.get(x, s);
          x.AddScaled(dx, -maxStep);
          s -= maxStep;
          ds -= maxStep;
        }
      field.get(x, s, dx);
      x.AddScaled(dx, -ds);
    }
  }
  
}