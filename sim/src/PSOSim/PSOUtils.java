package PSOSim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import multihop.RTable;

public class PSOUtils {
	
	
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

//
	public static double[] getRandP(int len) {
		double sum = 0;
		double[] randP = new double[len];
		for (int i = 0; i < len; i++) {
			randP[i] = rand();
			sum += randP[i];
		}

		for (int i = 0; i < len; i++) {
			double value = randP[i];
			randP[i] = value / sum;
		}
		return randP;
	}

	private static double rand() {
		Random r = new java.util.Random();
		return r.nextDouble(); // generate random from [0.0,1.0)
	}

	public PSOUtils() {
		// TODO Auto-generated constructor stub
	}

}
