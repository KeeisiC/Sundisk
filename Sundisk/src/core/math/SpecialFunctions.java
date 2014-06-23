package core.math;

import simulations.Constants;
public class SpecialFunctions {
	
	

	  /**
     * Elliptic integrals: 
     *	This algorithm for the calculation of the complete elliptic
     *	integral (CEI) is presented in papers by Ronald Bulirsch,
     *	Numerical Calculation of Elliptic Integrals and 
     *	Elliptic Functions, Numerische Mathematik 7,
     *	78-90 (1965) and Ronald Bulirsch: Numerical Calculation 
     *	of Elliptic Integrals and Elliptic Functions III,
     *	Numerische Mathematik 13,305-315 (1969).  The definition
     *	of the complete elliptic integral is given in equation (1.1.1.1)
     *	of the 
     *  <a href="C:\Development\Projects\SundquistDLIC\DLICdoc\TEAL_Physics_Math.pdf"> 
     *  TEAL_Physics_Math document </a>.
     *  @param kcc see Section 1.1.1 of the TEAL_Physics_Math documentation for the definition of this parameter
     *  @param pp see Section 1.1.1 of the TEAL_Physics_Math documentation for the definition of this parameter
     *  @param aa see Section 1.1.1 of the TEAL_Physics_Math documentation for the definition of this parameter
     *  @param bb see Section 1.1.1 of the TEAL_Physics_Math documentation for the definition of this parameter
     *  @param accuracy  the desired accuracy
     *  @return the value of the complete elliptic integral for these parameter values
     */

    public static double ellipticIntegral(double kcc, double pp, double aa, double bb, double accuracy) {
        double ca, kc, p, a, b, e, m, f, q, g;
        ca = accuracy;
        kc = kcc;
        p = pp;
        a = aa;
        b = bb;
        if ( kc != 0.0 ) 
        {
        	kc = Math.abs(kc);
        	e = kc;
        	m = 1.0;
        	
        	if (p > 0.) 
        	{
        		p = Math.sqrt(p);
        		b = b/p;
        	} 
        	else 
        	{
        		f = Math.pow(kc,2.0);
        		q = 1.-f;
        		g = 1.-p;
        		f = f-p;
        		q = q*(b-a*p);
        		p = Math.sqrt(f/g);
        		a = (a-b)/g;
        		b = -q/(p*Math.pow(g,2.0)) + a*p;
        	}

        	f = a;
        	a = b/p + a;
        	g = e/p;
        	b = 2.0*(f*g + b);
        	p = p + g;
        	g = m;
        	m = m + kc;
        	
        	while (Math.abs(g - kc) > g*ca) 
        	{
        		kc = 2.0*Math.sqrt(e);
        		e = kc*m;
        		f = a;
        		a = b/p + a;
        		g = e/p;
        		b = 2.0*(f*g + b);
        		p = p + g;
        		g = m;
        		m = m + kc;
        	}
        	
        	return (Math.PI / 2.)*(a*m + b)/(m*(m + p));
        	
        }
        
        else 
        {
        	return 0.0;
        }
    }

    
    /**
     * Smooth off and on function: 
     *	The next three classes return a smooth function that turns off and on, the first derivative of that function,
     *  and the second derivative of that function.  For a complete definition of this function plus a plot of it see
     *  Section 1.5 of the  TEAL_Physics_Math documentation.
     *  @param t the time at which you want to evaluate the smooth function and its derivatives
     *  @param Tdelay the time after which the function starts to turn on
     *  @param T  the time that it takes for the function to turn on
     *  @param TP the time that the function is constant before it starts to turn off
     *  @return the value of the smooth function
     */

    public static double getSmooth(double t, double Tdelay, double T, double TP) {
        /* Returns Smooth at time t */
        double tminus = (t  - Tdelay)/T;
        if (tminus < 0.)
          return 0.; // zero Smooth for t < Tdelay
        else {
          double TP1 = (TP + T )/T;
          double TP2 = (TP + 2*T )/T;
          double tprime = TP2 - tminus;
          double k=0.;
          if ( tminus < 0. ) k = 0. ;
          if ( (tminus >= 0.) && (tminus < 1.))  k = 6*Math.pow(tminus,5)-15*Math.pow(tminus,4)+10*tminus*tminus*tminus;
          if ( (tminus >= 1.)  && ( tminus < TP1 ))  k = 1.;
          if ( ( tminus >= TP1 ) && ( tminus < TP2) )  k = 6*Math.pow(tprime,5.)-15*Math.pow(tprime,4)+10*tprime*tprime*tprime;
          if ( tminus >= TP2 ) k  = 0.;
    //      System.out.println( " t getSmooth " + t + " k " + k );
          return k;
        }
      }
    
    public static double getSmoothDot(double t, double Tdelay, double T, double TP) {
        /* Returns the first time derivative of Smooth */
        double tminus = (t - Tdelay)/T;
        if (tminus < 0.)
          return 0.; 
        else {
          double TP1 = (TP + T )/T;
          double TP2 = (TP + 2*T )/T;
          double tprime = TP2 - tminus;
          double k=0.;
          if ( tminus < 0. ) k = 0. ;
          if ( (tminus >= 0.) && (tminus < 1.))  k = 30.*Math.pow(tminus,4)-60.*Math.pow(tminus,3)+30.*Math.pow(tminus,2);
          if ( (tminus >= 1.)  && ( tminus < TP1 ))  k = 0;
          if ( ( tminus >= TP1 ) && ( tminus < TP2) )  k = -(30.*Math.pow(tprime,4)-60.*Math.pow(tprime,3)+30.*Math.pow(tprime,2));
          if ( tminus >= TP2 ) k  = 0.;
          k = k/T;
          return k;
        }
    }
    
    public static double getSmoothDotDot(double t, double Tdelay, double T, double TP) {
        /* Returns the second time derivative of omega */
       double tminus = (t - Tdelay)/T;
        if (tminus < 0.)
          return 0.; 
        else {
          double TP1 = (TP + T )/T;
          double TP2 = (TP + 2*T )/T;
          double tprime = TP2 - tminus;
          double k=0.;
          if ( tminus < 0. ) k = 0. ;
          if ( (tminus >= 0.) && (tminus < 1.))  k = 120*Math.pow(tminus,3)-180*Math.pow(tminus,2)+60*Math.pow(tminus,1);
          if ( (tminus >= 1.)  && ( tminus < TP1 ))  k = 0;
          if ( ( tminus >= TP1 ) && ( tminus < TP2) )  k = 120*Math.pow(tprime,3)-180*Math.pow(tprime,2)+60*Math.pow(tprime,1);
          if ( tminus >= TP2 ) k  = 0.;
          k = k/(T*T);
          return k;
        }
      }
    
    
   
    /**
     * Smooth and ramp off and on and sinusoidal functions for spinning sphere: 
     *	The next four classes return a smooth function or a ramp function or a sinusoidal function
     *  that goes from one constant level to another, and the first, second, and third
     *  integral of that function (except for the sinusoidal function) 
     *  @param t the time at which you want to evaluate the smooth function and its three integrals
     *  @param Tdelay the time after which the function starts to change
     *  @param T  the time that it takes for the function to change  (this is the period of the sinusoidal function)
     *  @param I0 the initial value of the function  (for the harmonic functin I0 is its amplitude)
     *  @param I1 the final value of the function
     *  @param TimeType if this is 1 we have a smooth off on function, if 2 we have a ramp off on function, if 3 we have a sinusoidal function
     *  @return the value of the smooth function and its various integrals
     */

    public static double getI(double t, double Tdelay, double T, double I0, double I1, int TimeType) {
        /* Returns Smooth at time t */
        double tminus = (t  - Tdelay)/T;
        double I01 = Math.abs(I0)+Math.abs(I1);
        double deltaI = (I1-I0)/I01;
        double I0N = I0/I01;
        double I1N = I1/I01;
	    double k=0.;
	    double omegat = 2*Math.PI*t/T;

		switch (TimeType) {
		case Constants.TimeBehaviour_SmoothOffOn:
		    if ( tminus < 0. ) k = I0N ;
		    if ( (tminus >= 0.) && (tminus < 1.))  k = I0N + deltaI*(6*Math.pow(tminus,5)-15*Math.pow(tminus,4)+10*Math.pow(tminus,3));
		    if ( tminus >= 1.)  k = I1N;
			break;
		case Constants.TimeBehaviour_Ramp_On:
	    	if ( tminus < 0. ) k = I0N ;
			if ( (tminus >= 0.) && (tminus < 1.))  k = I0N + deltaI*tminus;
			if ( tminus >= 1.)  k = I1N;
			break;
		case Constants.TimeBehaviour_ConstantAcceleration:
		    if ( tminus < 0. ) k = 0. ;
		    if ( (tminus >= 0.) && (tminus < 1.))  k = I0N*Math.pow(tminus,2);
		    if ( tminus >= 1.)  k = I0N;
			break;
		case Constants.TimeBehaviour_Harmonic:
			k = I0N * Math.cos(omegat);
			break;
		case Constants.TimeBehaviour_SemiHarmonic_On:
	    	if ( tminus < 0. ) k = 0.;
	    	else k = I0N * Math.sin(omegat);
			break;
		case Constants.TimeBehaviour_SemiHarmonic_Off:
	    	if ( tminus > 0. ) k = 0.;
	    	else k = I0N * Math.sin(omegat);
			break;
		}
	 //   System.out.println( " getI " + t + " k " + k );
	    return k;
      }
    
    public static double getII(double t, double Tdelay, double T, double I0, double I1, int TimeType) {
        /* Returns integral of the smooth function at time t */
        double tminus = (t  - Tdelay)/T;
        double I01 = Math.abs(I0)+Math.abs(I1);
        double deltaI = (I1-I0)/I01;
        double I0N = I0/I01;
        double I1N = I1/I01;
	    double omegat = 2*Math.PI*t/T;
	    double fac1 = 2.*Math.PI;
	    double k=0.;
	    
		switch (TimeType) {
		case Constants.TimeBehaviour_SmoothOffOn:
		    if ( tminus < 0. ) k = I0N*tminus;
		    if ( (tminus >= 0.) && (tminus < 1.))  k = I0N*tminus + deltaI*(Math.pow(tminus,6)-3*Math.pow(tminus,5)+2.5*Math.pow(tminus,4));
		    if ( tminus >= 1.)  k = I1N*tminus-deltaI/2.;
			break;
		case Constants.TimeBehaviour_Ramp_On:
			if ( tminus < 0. ) k = I0N*tminus;
			if ( (tminus >= 0.) && (tminus < 1.)) k =  I0N*tminus + 0.5*deltaI*tminus*tminus;
			if ( tminus >= 1.)  k = I1N*tminus-deltaI/2.;
			break;
		case Constants.TimeBehaviour_ConstantAcceleration:
		    if ( tminus < 0. ) k = 0. ;
		    if ( (tminus >= 0.) && (tminus < 1.))  k = I0N*Math.pow(tminus,3)/3.;
		    if ( tminus >= 1.)  k = I0N*(tminus-2./3.);
		    break;
		case Constants.TimeBehaviour_Harmonic:
			k = I0N*Math.sin(omegat)/fac1;
			break;
		case Constants.TimeBehaviour_SemiHarmonic_On:
	    	if ( tminus < 0. ) k = 0.;
	    	else k = I0N * (1.-Math.cos(omegat))/fac1;
	    	break;
		}
		
	//    System.out.println( " getII " + t + " k " + k );
	    return k;
      }
  
    public static double getIII(double t, double Tdelay, double T, double I0, double I1, int TimeType) {
        /* Returns integral of integral of the smooth function at time t */
        double tminus = (t  - Tdelay)/T;
        double I01 = Math.abs(I0)+Math.abs(I1);
        double deltaI = (I1-I0)/I01;
        double I0N = I0/I01;
        double I1N = I1/I01;
	    double omegat = 2*Math.PI*t/T;
	    double fac1 = 2.*Math.PI;
	    double k=0.;
	    
	    switch (TimeType) {
		case Constants.TimeBehaviour_SmoothOffOn:
			if ( tminus < 0. ) k = 0.5*I0N*Math.pow(tminus,2);
			if ( (tminus >= 0.) && (tminus < 1.))  k = 0.5*I0N*Math.pow(tminus,2) + deltaI*(Math.pow(tminus,7)/7.-0.5*Math.pow(tminus,6)+0.5*Math.pow(tminus,5));
			if ( tminus >= 1.)  k = 0.5*I1N*Math.pow(tminus,2) + deltaI*(-0.5*tminus + 1./7.);
			break;
		case Constants.TimeBehaviour_Ramp_On:
			if ( tminus < 0. ) k = 0.5*I0N*Math.pow(tminus,2);
			if ( (tminus >= 0.) && (tminus < 1.)) k = 0.5*I0N*Math.pow(tminus,2) + deltaI*(Math.pow(tminus,3))/6.;
			if ( tminus >= 1.)  k = 0.5*I1N*Math.pow(tminus,2) + deltaI*(-0.5*tminus + 1./6.);
			break;
		case Constants.TimeBehaviour_ConstantAcceleration:
		    if ( tminus < 0. ) k = 0. ;
		    if ( (tminus >= 0.) && (tminus < 1.))  k = I0N*Math.pow(tminus,4)/12.;
		    if ( tminus >= 1.)  k = I0N*(Math.pow(tminus-1., 2)/2. + (tminus-1.)/3.+1./12.);
		    break;
		case Constants.TimeBehaviour_Harmonic:
			k = -1.*I0N *(Math.cos(omegat)-1.)/(fac1*fac1);
			break;
		case Constants.TimeBehaviour_SemiHarmonic_On:
	    	if ( tminus < 0. ) k = 0.;
	    	else k = I0N * (omegat-Math.sin(omegat))/(fac1*fac1);
	    	break;
		}

	//    System.out.println( " getIII " + t + " k " + k );
	    return k;
      }
    
    public static double getIIII(double t, double Tdelay, double T, double I0, double I1, int TimeType) {
        /* Returns integral of integral of integral of the smooth function at time t */
        double tminus = (t  - Tdelay)/T;
        double I01 = Math.abs(I0)+Math.abs(I1);
        double deltaI = (I1-I0)/I01;
        double I0N = I0/I01;
        double I1N = I1/I01;
        double sixth = 1./6.;
        double fraction = 10./336.;
	    double omegat = 2*Math.PI*t/T;
	    double fac1 = 2.*Math.PI;
	    double k=0.;
	    
	    switch (TimeType) {
		case Constants.TimeBehaviour_SmoothOffOn:
		    if ( tminus < 0. ) k = sixth*I0N*Math.pow(tminus,3);
		    if ( (tminus >= 0.) && (tminus < 1.))  k = sixth*I0N*Math.pow(tminus,3) + deltaI*(Math.pow(tminus,8)/56.-Math.pow(tminus,7)/14.+Math.pow(tminus,6)/12.);
		    if ( tminus >= 1.)  k = sixth*I1N*Math.pow(tminus,3) + deltaI*(-Math.pow(tminus,2)/4. + tminus/7.- fraction);
			break;
		case Constants.TimeBehaviour_Ramp_On:
		    if ( tminus < 0. ) k = sixth*I0N*Math.pow(tminus,3);
		    if ( (tminus >= 0.) && (tminus < 1.))  k = sixth*I0N*Math.pow(tminus,3) + deltaI*(Math.pow(tminus,4))/24.;
		    if ( tminus >= 1.)  k = sixth*I1N*Math.pow(tminus,3) + deltaI*(-Math.pow(tminus,2)/4. + tminus/6.- 1./24.);
			break;
		case Constants.TimeBehaviour_ConstantAcceleration:
		    if ( tminus < 0. ) k = 0. ;
		    if ( (tminus >= 0.) && (tminus < 1.))  k = I0N*Math.pow(tminus,5)/60.;
		    if ( tminus >= 1.)  k = I0N*(Math.pow(tminus-1., 3)/6. + Math.pow(tminus-1., 2)/6.+(tminus-1.)/12.+1./60.);
		    break;
		case Constants.TimeBehaviour_Harmonic:
			k = I0N *(omegat - Math.sin(omegat))/(fac1*fac1*fac1);
			break;
		case Constants.TimeBehaviour_SemiHarmonic_On:
	    	if ( tminus < 0. ) k = 0.;
	    	else k = I0N * (omegat*omegat/2. + Math.cos(omegat)-1.)/(fac1*fac1*fac1);
	    	break;
		}
	    
//	    System.out.println( " getIIII " + t + " k " + k );
	    return k;
      }
}
