package RPL;

public class Constants {
	/** INPUT TEST **/
	// fixed RES all node
	// fixed BW of a path (1-hop)
	
	
	// t = 0.004*x + 0.033*x
	
	public final static int MAXINT = Integer.MAX_VALUE;
	public final static double MAXDOUBLE = Double.MAX_VALUE;
	public final static int RANGE = 14*14;
	
	public final static double BW = 1/0.004;
	public final static double  RES = 1/0.033;;
	
	
	public final static double ALPHA = 1; // EXP(-ALPHA*d)
	
	public final static int particles = 20;
	public final static int epchos=1000;

}
