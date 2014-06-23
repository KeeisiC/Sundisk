/**
 * 
 */
package core.math;

import core.math.SpecialFunctions;


/** A program to list values of a special function in a format that can be imported
 * into Excel and plotted
 * 
 * @author John Belcher
 * @version 1.0
 */
public class MathFunctionTestListing {
	
	  public static void main(String[] args){ 
		   
	  for( int i = 0 ; i < 101 ; i++ )
		    {
		        double x = i*1.;
		     //   getSmooth(double t, double Tdelay, double T, double TP) {
		        double beta = SpecialFunctions.getSmooth(x, 0., 30., 30.);
		        System.out.println(x + ", " + beta);
		    }
	  }
}
