package simulations.experiments;

import simulations.objects.*;
import core.math.Vec;
import core.math.Vec2;
import core.math.Vec3;


/** This is the abstract parent class that all actual experiments should extend.
*  When creating an experiment subclass, you must provide an implementation for the five functions
*  declared here:  ConstructEMSource(), getEMSource(), Evolve(), getHue(), and getFlowSpeed()
*
*	The content of those functions can be whatever you want (and you can have additional functions or sub-classes, 
*	such as the Motion class used for calling RungeKuttaIntegration), but they must all be defined in some way.
*
*	FieldType and FieldMotionType should also be set on a case by case basis, as the renderer uses these values to
*	decide which type of field to draw and what type of motion field to evolve that field.  If you end up with 
*   no image, or just "static", it may means that the DLIC is trying 
*   to draw the electric field of a magnetostatic experiment, or vice versa, and these two variables are the first 
*   thing to check.
*   
* @author Michael Danziger
* @author John Belcher
* @version 1.0
*/
abstract public class BaseExperiment {
	
	/** type of field for this experiment, where zero is NOT a valid field type.  "FieldType" must be correctly
	 * set in the experiment that extends BaseExperiment.  */
	public int FieldType = 0;
	/** type of motion field for this experiment, where zero is NOT a valid field motion type.  "FieldMotionType" must be correctly
	 * set in the experiment that extends BaseExperiment.  */
	public int FieldMotionType = 0;	
	/** the normalization for the field if we are showing fluid flow */
	public double Fnorm = 1.;
	/** the overall speed multiplier if we are showing fluid flow, in pixels per second */
	public double FluidFlowSpeed = 0.;
	/** the power dependence on the magnitude of the field is we are showing fluid flow */
	public double Fpower = 0.;
	
	/** The number of Runge Kutta steps taken to get from t to t + dt.  If an experiment needs an integrator,
	 * most experiments use a simple 
	 * RungeKuttaIntegration scheme to evolve the Motion equations, with no error estimate.  Since it 
	 * takes almost no time to integrate compared to the computationally demanding FLIC image processing, 
	 * we routinely take many many small steps to go from t to t +dt as a way ensure accuracy. The parameter
	 * <i>numberSmallSteps</i> is the number of steps we take between t and t + dt, set to 5 for the default. 
	 * NOTE THAT NOT ALL experiments need an integrator, since sometimes the evolution of the system is 
	 * given by analytic expressions, and we do not need to integrate a system of ODEs to find the evolution. */ 
	public int numberSmallSteps = 5;
	/** If the experiment uses a RungeKuttaIntegration scheme which calculates an internal fractional 
	 * error estimate to evolve the Motion equations (not implemented at present in 1.0), then eps is the 
	 * allowed fractional error for the step from t to t+dt for a requested time step dt.  */ 
	public double eps = 0.001;
	
	/** FieldType for electrostatics experiments. */
	/** constructs the EM source consisting of a number of BaseObjects and adds them to the EMCollection 
	 * for this experiment */	
	abstract public void ConstructEMSource();
	/** returns the collecton of BaseObjects constructed in ConstructEMSource() */
	abstract public BaseObject getEMSource();
	/** Evolves the properties of the collection of BaseObjects in the experiment with time */	
	abstract public void Evolve(double dt);
	abstract public Vec getRegionHSVW(double TargetHue, double TargetSaturation, double TargetValue, Vec3 r, Vec RegionHue, Vec RegionSaturation, Vec RegionValue, Vec RegionWhite);
	abstract public double getFlowSpeed(Vec3 r, Vec RegionFlow);
}
