package PSOSim;

import java.util.Map;

import ManagerApplication.Worker;
import PSO.Swarm;
import PSO.Vector;

public class MainPSO {
	public static void main(String[] args) {
		
	//	int particles, int epochs, int nodes, int workLoad, Vector currentWorkload
		int particles = 20;
		int epchos=1000;
		int nnodes = 6;
		int workLoad=100;
		
		int nworker=6;
		
		double[] cWorkload = new double[nworker]; // workers
		for (int i=0;i<cWorkload.length;i++) {
			cWorkload[i]=0;
		}

		
		Vector currentWorkload = new Vector(cWorkload);
		
		Swarm swarm = new Swarm(particles,epchos, nnodes,
				workLoad, currentWorkload);

		Map<String, String> ratio = swarm.run("service-id-string");
		 System.out.println(ratio.toString());
	}

}
