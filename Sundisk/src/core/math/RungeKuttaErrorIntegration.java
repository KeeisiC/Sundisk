package core.math;

import java.math.*;

import core.field.VecTimeField;

/** RungeKutta Integration with error analysis */

public class RungeKuttaErrorIntegration {
  
  boolean stepped;
  double maxStep;
  
  public RungeKuttaErrorIntegration()
  {
    stepped = false;
  }
  
  public RungeKuttaErrorIntegration(double maxStep)
  {
    stepped = true;
    this.maxStep = maxStep;
  }
  
  public void SetStep(double maxStep)
  {
    stepped = true;
    this.maxStep = maxStep;
  }
  
  int dim = 0;
  private Vec xinput;
  private VecTimeField field;
  private double eps, hmin;
  
  public Vec ComputeStep(double s, double ds, int dim)
  {  
    double s1 = s;
    double h1 = ds;
    //double hmin = h1/1000.;
    double s2 = s1 + h1;  // we put the end point to s1+h, so that one step would take us from s1 to s2
    double hdid=0.; double hnext=0.;  // these are varialbes that set by rungeKuttaQualityControl;  they are
    //                                   respectively the step actually taken and an estimate of the next step;
    //                                   that will give new values with an error less than eps
    int nstp;
    Vec Xtry = new Vec(dim);
    Vec X = new Vec(dim);
    Vec Xscal = new Vec(dim);
    Vec Xstart = new Vec(dim);
    Vec dXds = new Vec(dim);
    double[] array = new double[dim+4];
    double h;
    int maxstp=1000;  // maximum number of steps we allow in getting from s1 to s2
    int note = 0;   //  these are flags for the quality of the process.  if note != 0 flawed
    int nok=0;       // numnber of steps to get to desired result                                                        
    int nbad=0;      // number of steps we had to take to get there                    
    double tiny=1E-30;
    if ((s2-s1) > 0.) h = Math.abs(h1); else h = - Math.abs(h1); 
    s=s1;      
    Xstart = this.xinput.scale(1.);
    Xtry = Xstart.scale(1.);  
    //System.out.println ( " compute step " + eps ); // 
    for (nstp = 1; nstp <= maxstp; nstp++) 
    {
        field.get(Xtry, s, dXds);
        for (int i = 0 ; i<dim;i++)  Xscal.x[i] = Math.abs(Xtry.x[i])+Math.abs(h*dXds.x[i])+tiny;  //  this is the array used for error estimate
        if ((s+h-s2)*(s+h-s1)> 0.)  h=s2-s;  // in our scheme where s2 = s1 + h, we normally try to go from s1 to s2 in one step;  if we take more
        //                                   than one step this if statement guarantees that when we take a step that passes s2, then we reset the
        //                                   stepsize so that we end up exactly at s2 on the last step
        array = rungeKuttaQualityControl(field,Xtry,dXds,s,h,eps,Xscal,dim);  // this gets us a new value of the dependent values for a step that is less
        //                                  than or equal to h, but in any case has an error less than eps.  The actual size of the step is returned
        //                                  in the higher storage location of array, as indicated below.
        for ( int i = 0; i<dim; i++) Xtry.x[i] = array[i];  // the lower n storage positions of array contain the new dependent values
        //System.out.println ( " RKSTEPPED  first X " + Xtry.x[4] ); // 
        note = (int)array[dim];
        hdid = array[dim+1];
        hnext = array[dim+2];
        s = array[dim+3];
        if (note == 1) {
            System.out.println ( " note = 1 stepped runge kutta " ); //  
            break;
        }
        if (hdid == h) nok = nok + 1; else nbad = nbad + 1;
        if ((s-s2)*(s2-s1) >= 0.) 
        {
             for (int i = 0 ; i<dim;i++) Xstart.x[i] = Xtry.x[i];   
             //System.out.println ( " RKSTEPPED  first X " + Xstart.x[4] ); // 
             break;
        }
        if(Math.abs(hnext) < hmin) {
                //note = 2;  // step size less than preset minimum
                //System.out.println( " hnext from main " + hnext );
                //System.out.println ( " step size less than preset minimum in stepped runge kutta " ); //  step size too small
                h = hnext;
        }
    }                                                          
    if ( nstp == maxstp+1 ) System.out.println ( " too many steps in stepped runge kutta " + nstp); //  too many steps
    this.xinput = Xstart.scale(1.);
    return this.xinput;
    
    }

       public void Evolve(VecTimeField field, Vec xinput, double s, double ds, double eps, double hmin)
    {
         this.field = field;
         this.xinput = xinput;
         this.eps = eps;
         this.hmin = hmin;
         if (dim!=xinput.dim) {
         dim = xinput.dim;
         
    }
    
      xinput = ComputeStep(s,ds,dim);
      //System.out.println("absolutely newest PRIVATE THIS integrator.Evolve " + s +  "  ds " + ds +  " x2 " + xinput.x[4] );
      
  }      
    /**
     * Given a  value of X and dX/ds at s, a stepsize h, an allowed error, and a way to
     * compute dX/ds at new points, this method will return a new value
     * of the dependent variables X at s, where s incremented over s by an amount
     * not more than h, but perhaps much less than h, but with an error in X of less
     * than the desired error.  The size of the step actually taken is hdid.  If we are
     * taking too small a step for the error we desire, we return hnext as an estimate
     * of the step size for the next step which will still be just within our desired error.
     * 
     * @param Xnew This is the valve of X at s
     * @param dXds The derivative dX/ds at out initial value of the independent parameter.  
     * @param sgiven This is the initial value of the independent variable.
     * @param htry The initial value of the stepsize to try, which is subsequently modified depending
     *             on the estimated error and our desired error.
     * @param eps Desired error.
     * @param Xscal Defines the meaning of error.
     * @return The new value the dependent values, with other things stored in array, e.g. the new s, hdid, hnext, and so on
     */
    public static double[] rungeKuttaQualityControl(VecTimeField field, Vec Xnew, Vec dXds, double sgiven, double htry, double eps, Vec Xscal, int dim){
	    double s,ssav,hh,h,temp,errmax,pgrow,pshrnk,fcor,safety,errcon;
	    Vec errvector = new Vec(dim);
	    Vec dXsav = new Vec(dim);
	    Vec Xsav = new Vec(dim);
	    Vec Xtemp = new Vec(dim);
            double hdid=0.; double hnext=0.;
            double[] array = new double[dim+4];
            //System.out.println( " htry from quality control " + htry );
            array[dim]=0.;
	    fcor = 0.3333333333333333;
	    safety = 0.9;
	    int note = 0;
	    pgrow = -0.333333333333;
	    pshrnk = - 0.5;
	    errcon = Math.pow(safety/4.,3.);
	    ssav=sgiven;
	    for (int i = 0; i < dim;i++) {
		 Xsav.x[i] = Xnew.x[i];
		 dXsav.x[i] = dXds.x[i];
            }
	    h = htry;
	    for (;;) 
	    {
		    hh=0.5*h;
		    // first get RK4 estimate of X at s+h by taking two steps of h/2
		    // this is the first step
		    Xtemp = rungeKuttaFourthOrder(field, Xsav,dXsav,ssav,hh,dim);
                    
		    s = ssav+hh;
		    field.get(Xtemp,s,dXds);   
		    // this is the second step--now we have taken two steps of h/2+h/2 = h
 		    Xnew = rungeKuttaFourthOrder(field, Xtemp,dXds,s,hh,dim);
		    s = ssav+h;
		    if (s == ssav) 
		    {
		        note = 1;  // if we are here then we have cut down on the stepsize until not significant
                        array[dim]=1.;
		        break;
		    }
		    // now go to h in one step of h and compare to the two h/2 steps
		    // this allows us to make an estimate of error
		    Xtemp = rungeKuttaFourthOrder(field, Xsav,dXsav,ssav,h,dim);
		    errmax = 0.0;
		    for (int i=0;i < dim;i++) 
		    {
		         Xtemp.x[i]=Xnew.x[i]-Xtemp.x[i];
		         errvector.x[i]=Xtemp.x[i]/Xscal.x[i];
			 temp=Math.abs(errvector.x[i]);
			 if (errmax < temp) errmax = temp;
		    }
		    errmax = errmax/eps;
                    //System.out.println( " error " + h + " errmax " + errmax );
		    if(errmax > 1.0) 
		    {
		    // if our error is too large shrink stepsize and go back and try again
		        h = safety*h*Math.pow(errmax,pshrnk);
                        //System.out.println( " error too large:  errmax " + errmax + " h " + h );
		    }
		    else 
		    {
		     // if our error is ok, estimate the increase in step size
		     // on the next call to get desired accuracy, and return
                         //System.out.println( " hdid from quality control " + hdid + " errcon " + errcon);
			 hdid = h;
			 if (errmax > errcon) hnext = safety*h*Math.pow(errmax,pgrow); 
			 else hnext = 4.0*h;
                         //System.out.println( " error OK:  errmax " + errmax + " hnext " + hnext );
			 break;  // this is how we get out of the for(;;) loop
		    }
            //end of for(;;) loop		    
	    }
	    
	    // have gotten desired accuracy with stepsize s, which may be less than requested,
	    // now return estimate of X at s within our error, corrected to 6th order
	    for (int i=0;i < dim;i++) Xnew.x[i] = Xnew.x[i] + fcor*Xtemp.x[i];
            //System.out.println( " from quality control first X " + Xnew.x[4] );
            for (int i=0;i < dim;i++) array[i] = Xnew.x[i];
            //System.out.println( " from quality control first array " + array[4] );//  store dependent values in first n locations of array
            array[dim]=note;  // store in upper locations other variables, here is note
            array[dim+1]=hdid;  //  store the actualy size of the step we took 
            array[dim+2]=hnext;  //  store the estimate of the next step that will be within eps error limit
            array[dim+3]=s;  // store independent variable for the end step here
	    // now return new dependent value arrays plus other information in array 
	    return array;
    }        
	
         /**
       * This is the basic fourth order RungeKutta stepping routine.
       * Given the value X of the dependent variables at s, and a
       * step size h, and the ability to evaluate the derivatives
       * dX/ds at any s, the routine returns an estimate of the value
       * of X at s + h.
       * 
       * @param X_at_s This is the initial value of X at the starting value of s.
       * @param dXds_at_s This is the initial value of the derivatives dX/ds at the 
       * starting value of s.  This derivative must be provided to
       * the routine.
       * @param s_start The initial value of the independent variable.
       * @param h The step size.
       * @return An estimate of the value of the dependent variables X at
       * s + h.
       */
        public static Vec rungeKuttaFourthOrder(VecTimeField field, Vec X_at_s, Vec dXds_at_s, 
                                                double s_start, double h, int dim) 
        {
            double sh;
            Vec X_at_sph = new Vec(dim);
            Vec Xt = new Vec(dim);
            Vec dXt = new Vec(dim);
            Vec dXm = new Vec(dim);
            double h6 = h/6.;
            double hh = h/2.;
            for (int i=0;i < dim;i++) Xt.x[i] = X_at_s.x[i]+hh*dXds_at_s.x[i];
            sh = s_start + hh;
            field.get(Xt,sh,dXt);
            for (int i=0;i <dim;i++) Xt.x[i] = X_at_s.x[i] + hh*dXt.x[i];
            field.get(Xt,sh,dXm);
            for (int i=0;i <dim;i++) 
            {
                Xt.x[i] = X_at_s.x[i]+h*dXm.x[i];
                dXm.x[i] = dXt.x[i]+dXm.x[i];
            }
            field.get(Xt,s_start+h,dXt);
            for (int i=0;i < dim;i++) 
            X_at_sph.x[i] = X_at_s.x[i] + h6 * (dXds_at_s.x[i] +dXt.x[i] + 2.*dXm.x[i]);
            double shhh= s_start + h;
            field.get(X_at_s,sh,dXt);  // make sure we return things to the way they were before returning
            return X_at_sph;
        }


  
}