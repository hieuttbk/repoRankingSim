package PSOSim;

import multihop.Constants;
import multihop.Node;

public class Util {
	
	// cacl distance node A and node B
	public static double calcDistance(Node a, Node b) {
		return (b.getLat()-a.getLat())*(b.getLat()-a.getLat())+(b.getLng()-a.getLng())*(b.getLng()-a.getLng());	
	}
	
	static double caclTimeTrans(Node n, Node bestNode) {
		double t=n.getWL()/Constants.BW;
		int d=(n.getLvl()-bestNode.getLvl());
		if (d<=0) return 0;
		t*=d; // number of path * t_trans
		return t; 
	}
	
	
	
	static double caclTimeComputeMulti(Node n) {
	//	System.out.println("Node compute is " +  n.getId());
		double t=n.getWL()/n.getRes(); 
		return t; 
	}
	
	static double caclTimeTransMulti(Node n, Node bestNode) {
		double t=n.getWL()/Constants.BW;
		int d=(n.getLvl()-bestNode.getLvl());
		if (d<=0) return 0;
		t*=d; // number of path * t_trans
		return t; 
	}
	
	
	
	static double caclTimeCompute(Node n) {
	//	System.out.println("Node compute is " +  n.getId());
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
}
