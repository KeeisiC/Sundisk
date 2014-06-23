/**
 * 
 */
package core.math;
import simulations.Constants;
import java.math.BigInteger;
/**
 * 
 * @author john
 */
public class PlotNewSpinningSphereFunctions {

	  public static void main(String[] args){  
		 double t;
		 double I=0.;
		 double II=0.;
		 double III=0.;
		 double IIII=0.;
		 double Tdelay = 0.;
		 double I0 = 1.;
		 double T = 1.;
		 int timetype = 0;
		 int offsetfactorial = 20;
		 timetype = Constants.TimeBehaviour_RampOffOn;
		 for (int i = 0; i < 1; i++) {
			 t = i*.01 -0.5;
			 I = SpinningSphereFunctions.getSn(t, Tdelay, T, I0, timetype, 0 + offsetfactorial);
			 II = SpinningSphereFunctions.getSn(t, Tdelay, T, I0, timetype, 1 + offsetfactorial);
			 III = SpinningSphereFunctions.getSn(t, Tdelay, T, I0, timetype, 2 + offsetfactorial);
			 IIII = SpinningSphereFunctions.getSn(t, Tdelay, T, I0, timetype, 3 + offsetfactorial);
			 System.out.println( t + ", " + I + ", " + II + ", " + III + ", "+ IIII);
	  	 }
		 BigInteger n = BigInteger.ONE;
	        for (int i=1; i<=20; i++) {
	            n = n.multiply(BigInteger.valueOf(i));
	            System.out.println(i + "! = " + n);}

	  
}
}
