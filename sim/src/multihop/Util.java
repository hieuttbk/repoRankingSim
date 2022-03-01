package multihop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Util {
	
	// cacl distance node A and node B
	public static double calcDistance(Node a, Node b) {
		return (b.getLat()-a.getLat())*(b.getLat()-a.getLat())+(b.getLng()-a.getLng())*(b.getLng()-a.getLng());	
	}
	
	public static double caclTimeTrans(Node n, Node bestNode) {
		double t=n.getWL()/Constants.BW;
		int d=(n.getLvl()-bestNode.getLvl());
		if (d<=0) return 0;
		t*=d; // number of path * t_trans
		return t; 
	}
	
	public static double caclTimeCompute(Node n) {
		double t=n.getWL()/n.getRes(); 
		return t; 
	}
	
	static double calcTime(double WL) {
		
		if (WL==0) return 0;
		
		double BW = Constants.BW;
		double R = Constants.RES;
		
		double t_tran1 = WL /BW; // 0.004*WL + 0.0619
		double t_compute = WL / R; // 0.033*WL+0.469
		double t_tran2=0.048;
		
		return t_tran1+t_compute+t_tran2;
	}
	
	
	public static HashMap<String, Integer> getPahts(List<RTable> rtable) {
		
		HashMap<String, Integer> paths = new HashMap<String, Integer>(); // paths of each node

		List<String> check = new ArrayList<String>();
		for (RTable r : rtable) {
			int path = 1;
			String nodeID = r.getDes();
			if (!check.contains(nodeID)) {
				for (RTable r2 : rtable) {
					if ((r2.getDes().equals(r.getDes()))
							&& (r2.getId() != r.getId() || (r2.getReq().getId() != r.getReq().getId()))) {
						path++;
					}
				}
				paths.put(nodeID, path);
				check.add(nodeID);
			}

		}
		return paths;
	}

	public static double[] getRandP(int len) {
	   	double sum = 0;
	   	double[] randP = new double[len];
    	for(int i = 0; i< len; i++){
    		randP[i]=rand();
    		sum += randP[i];
    	}
    	
    	for(int i = 0; i<len; i++){
    		double value = randP[i];
    		randP[i] = value/sum;
    	}
		return randP;
	}

	private static double rand() {
		Random r = new java.util.Random();
        return r.nextDouble(); //generate random from [0.0,1.0)
	}
}
