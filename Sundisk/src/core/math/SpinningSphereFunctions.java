package core.math;

import core.postprocessing.Colorizer;
import core.rendering.Renderer;
import simulations.Constants;
public class SpinningSphereFunctions {
	
    /**
     *  General time integrals of various functions for time behavior of current in spinning sphere
     *	The class returns the nth integration (n >= 0) of various functions
     *  @param t the time at which you want to evaluate the nth integral of the specified function
     *  @param Tdelay the time after which the function starts to change
     *  @param T  the time that it takes for the function to change 
     *  @param I0 the final value of the function  (for the harmonic function I0 is its amplitude)
     *  @param TimeType type of time behavior 
     *  @param n the integration value (n>=0)
     *  @return the value of the function and its various integrals
     */

    public static double getSn(double t, double Tdelay, double T, double I0, int TimeType, int n) {
        /* Returns integral at time t */
        double tminus = (t  - Tdelay)/T;
	    double k=0.;
	    double omegat = 2*Math.PI*t/T;

		switch (TimeType) {

		case Constants.TimeBehaviour_RampOffOn:
	      if ( tminus < 0. ) k = 0. ;
		  if ( (tminus >= 0.) && (tminus < 1.))  k = I0N + deltaI*tminus;
		  if ( tminus >= 1.)  k = I1N;
			
			k = Constants.factorial[n];
			
			break;

		}
	 //   System.out.println( " getI " + t + " k " + k );
	    return k;
      }
}  
   