/**
 * 
 */
package core.math;

/**
 * 
 * @author john
 */
public class PlotOldSpinningSphereFunctions {

	  public static void main(String[] args){  
		 double t;
		 double I=0.;
		 double II=0.;
		 double III=0.;
		 double IIII=0.;
		 double Tdelay = 0.;
		 double I0 = 0.;
		 double I1 = 1.;
		 double T = 1.;
		 int timetype = 0;
		 timetype = 2;
		 for (int i = 0; i < 200; i++) {
			 t = i*.01 -0.5;
			 I = SpecialFunctions.getI(t, Tdelay, T, I0, I1, timetype);
			 II = SpecialFunctions.getII(t, Tdelay, T, I0, I1, timetype);
			 III= SpecialFunctions.getIII(t, Tdelay, T, I0, I1, timetype);
			 IIII=SpecialFunctions.getIIII(t, Tdelay, T, I0, I1, timetype);
			 System.out.println( t + ", " + I + ", " + II + ", " + III + ", "+ IIII);
	  	 }
	  }
}
