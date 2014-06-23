package simulations;

/** Electromagnetic constants and field and field motion constants.
*
* The class defines the integers for various field types and field motion
* types.  This class also defines the free-space permittivity and permeability
* somewhat arbitrarily so that the speed of light is 1 pixel per unit time.
 */

public class Constants {
	
	/**  symmetry options  */
	public static final int  SymPlotNone =0;
	public static final int  SymPlotLeftRight =1;
	public static final int  SymPlotTopBottom =2;
	public static final int  SymPlotLeftRightTopBottom =3;
	
	 /** Plot electric potential of a point charge*/
	public static final int PlotPotential = 1;
	 /** Plot electric field of a point charge*/
	public static final int PlotField = 2;
	
    /** Time behavior which is a smooth off on behavior*/
	public static final int TimeBehaviour_SmoothOffOn = 1;
	/** Time behavior which is a ramp on behavior*/
	public static final int TimeBehaviour_Ramp_On = 2;
	/** Time behavior which is a ramp off behavior*/
	public static final int TimeBehaviour_Ramp_Off = 8;
	/** Time behavior which is sinusoidal*/
	public static final int TimeBehaviour_Harmonic = 3;
	/** Time behavior which is sinusoidal after time = 0 */
	public static final int TimeBehaviour_SemiHarmonic_On = 4;
	/** Time behavior which is constant acceleration behavior*/
	public static final int TimeBehaviour_ConstantAcceleration = 5;
	/** Time behavior which is arc tangent behavior*/
	public static final int TimeBehaviour_ArcTan = 6;
	/** Time behavior which is sinusoidal before time = 0 */
	public static final int TimeBehaviour_SemiHarmonic_Off = 7;
	
	/** FieldType for experiments which display electric field. */
	public static final int FIELD_EFIELD = 1;
	/** FieldType for experiments which display magnetic field.*/	
	public static final int FIELD_BFIELD = 2;
	/** FieldMotionType for experiments where the electric field is displayed. */	
	public static final int FIELD_MOTION_EFIELD = 1;
	/** FieldMotionType for experiments where the magnetic field is displayed. */		
	public static final int FIELD_MOTION_BFIELD = 2;
	/** FieldMotionType for fluid flow parallel to E field. */
	public static final int FIELD_MOTION_VEFIELD = 3;
	/** FieldMotionType for fluid flow parallel to B field. */
	public static final int FIELD_MOTION_VBFIELD = 4;
	/** FieldMotionType for fluid flow varying by spatial region. */
	public static final int FIELD_MOTION_VREFIELD = 5;
	/** FieldMotionType for fluid flow varying by spatial region. */
	public static final int FIELD_MOTION_VRBFIELD = 6;
	
	/** values of factorial n for n = 0 to 21 */
	public static final double factorial[] = {1.,1.,2.,6.,24.,120.,720.,5040.,
		40320.,362880.,3628800.,39916800.,479001600.,6227020800.,87178291200.,
		1307674368000.,20922789888000.,355687428096000.,6402373705728000.,
		121645100408832000.,2432902008176640000.}; 
	
	/** The "colorHue" value used as the default for electric fields */
	public static final double COLOR_EFIELD = 0.1;
	/** The "colorHue" value used as the default for magnetic fields */
	public static final double COLOR_BFIELD = 0.5961;
	/** The "colorHue" value used as the default for magnetic fields */
	public static final double COLOR_POTFIELD = 0.45;

	/** episilon naught */
	public static double e0 = 1.;
	/** mhu naught */
	public static  double u0 = 1.;
	/** the speed of light computed from above values in pixels per unit time */
	public  static double c = 1.;  //1./Math.sqrt(e0*u0);
	/** the speed of light squared computed from above values */
	public  static double c2 = c*c;
	/** 1/(4 pi epsilon naught) in coulomb's law */
	public  static double Efactor = 1.0/(4.0*Math.PI*e0);
	/** mhu naught over 4 pi in Biot Savart Law */
	public  static double Bfactor = u0/(4.0*Math.PI);
	
	/**  gravitational plot options  */
	public static final int  gPlotEposeigen =1;
	public static final int  gPlotEnegeigen =2;
	public static final int  gPlotEdotr =3;
	public static final int  gPlotEdottheta =4;
}