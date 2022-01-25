package PSOSim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import multihop.Constants;
import multihop.Node;
import multihop.RTable;

class PSOFunction {

	private int nodes;
	private Node bestNode;

	public PSOFunction(int nodes, Node bestNode) {
		this.nodes = nodes;
		this.bestNode = bestNode;

	}

	/**
	 * @return PSOVector as value: time=f(p)
	 */

	public PSOVector multiFunction(PSOParticle p, int workLoad, PSOVector currentWorkload, Node bestNode,
			List<RTable> rtable, HashMap<Integer, List<RTable>> mapRTable) {
		/**
		 * @param workLoad        number of images from 1 service
		 * @param p               percent workload
		 * @param Theta           share service or not ( 0 or 1)
		 * @param currentWorkload current workload at that node
		 * @return total time
		 */

		PSOVector position = p.getPosition().clone(); // time

		PSOVector worker = p.getPosition().getVectorRatio(); // ratio

		// calcTimeSerMulti(rtable, position, worker, workLoad);
		// TODO position is time_server, rtable is list of node including virtual node
		// must be assigned
//		0 Man Man 0
//		1 W1 Man 1
//		2 W3 W1 2
//		3 W5 W1 2
//		4 W2 Man 1
//		5 W4 W2 2
//		6 W5 W2 2
		
		//System.out.println("particle: " + p.getPosition().toStringOutput());


		double CWL = 0;
		List<String> check = new ArrayList<String>();

		// calc t_compute
		for (RTable r : rtable) {
			double t_compute = 0;
			double subWL = worker.getById(r.getId()) * workLoad; // new WL
			double totalWL = subWL + r.getcWL(); // adding cWL
			t_compute = totalWL / r.getResource();

			if (!check.contains(r.getDes())) {
				// if (r.getNpath() > 1) {

				List<Integer> r2Id = new ArrayList<Integer>();
				for (RTable r2 : rtable) {
					if ((r2.getDes() == r.getDes())
							&& (r2.getId() != r.getId() || (r2.getReq().getId() != r.getReq().getId()))) {
						r2Id.add(r2.getId());
						t_compute += worker.getById(r2.getId()) * workLoad / r2.getResource();

					}
				}

				// r.setTimeCompute(t_compute);
				for (Integer i : r2Id) {
					rtable.get(i).setTimeCompute(t_compute);
				}
				// }
				r.setTimeCompute(t_compute);

				CWL += r.getcWL();

			}
			check.add(r.getDes());
			
		}

		// Adding trans:
		for (RTable r : rtable) {
			double t_trans;

			t_trans = (worker.getById(r.getId()) * workLoad / Constants.BW) * r.getHop();
			if (r.getId() == 0)
				t_trans = 0;

			r.setTimeTrans(t_trans);
			position.setById(r.getId(), r.getTimeTrans() + r.getTimeCompute());
			

		}

		int totalworkLoad = 0;
		for (Integer id : mapRTable.keySet()) {
			totalworkLoad += mapRTable.get(id).get(0).getReq().getWL();
		}
		
		

		constraintF3(totalworkLoad, currentWorkload, position, CWL, mapRTable,worker);
	//	constraintF4(totalworkLoad, position, CWL);

		return position;

	}

	private void calcTimeSerMulti(List<RTable> rtable, PSOVector position, PSOVector worker, double workLoad) {
		// TODO position is time_server, rtable is list of node including virtual node
		// must be assigned
//		0 Man Man 0
//		1 W1 Man 1
//		2 W3 W1 2
//		3 W5 W1 2
//		4 W2 Man 1
//		5 W4 W2 2
//		6 W5 W2 2
		// position.setById(0, worker.getById(0) * workLoad /
		// rtable.get(0).getResource());
		for (RTable r : rtable) {
			double t_compute = 0;
			double t_trans;

			t_compute = worker.getById(r.getId()) * workLoad / r.getResource();
			if (r.getNpath() > 1) {
				// process with r.getDes()
				for (RTable r2 : rtable) {
					if ((r2.getDes() == r.getDes()) && (r2.getId() != r.getId())) {
						t_compute += worker.getById(r2.getId()) * workLoad / r2.getResource();

					}
				}
			}

			t_trans = (worker.getById(r.getId()) * workLoad / Constants.BW) * r.getHop();

			if (r.getId() == 0) {
				t_trans = 0;
			}
			;
			position.setById(r.getId(), t_trans + t_compute);

		}

//		for (int i=1; i<=rtable.size();i++) {	
//			position.setById(i, worker.getById(i)*workLoad/Constants.BW + t_compute);
//		}

	}

	public PSOVector mainFunction(PSOParticle p, int workLoad, PSOVector currentWorkload, Node bestNode,
			List<RTable> rtable) {
		/**
		 * @param workLoad        number of images from 1 service
		 * @param p               percent workload
		 * @param Theta           share service or not ( 0 or 1)
		 * @param currentWorkload current workload at that node
		 * @return total time
		 */
		PSOVector position = p.getPosition().clone();
		// Vector p_temp = position.clone(); // for 2nd part

		PSOVector Theta = new PSOVector(nodes);
		for (int i = 0; i < position.getVectorCoordinate().length; i++) {
			if (position.getById(i) != 0) {
				Theta.setById(i, 1);
			}
		}

		PSOVector worker = p.getPosition().getVectorRatio(); // get ratio
		PSOVector worker_temp = worker.clone();
		PSOVector workerCWL = currentWorkload.getVectorRatio(); // Worker current Workload

		// double man = p.getPosition().getById(0);
		// double manCWL = currentWorkload.getById(0); //Manager current Workload

		/*
		 * Calculate time for manager
		 */
//        double Tman = (man*workLoad*0.005 + 0.619)   // y = 0.005*x + 0.0619   ~ P*Wi / Bij
//        				+ (0.0199*man*workLoad+0.291)    // y = 0.0199*x + 0.291 ~ P.Wi / fi
//        				+ (0.0199*manCWL+0.291)*Theta.getById(0) // (y = 0.0199*x + 0.291)*Theta ~ Theta*Ni/fi
//        				+ 0.047675; // Ri / Bij

		/*
		 * calculate time for workers
		 */

		// worker represent t_serve on Node.

		// y = 0.004*x + 0.0619 ~ P*Wi / Bij
		// worker.mul(workLoad * 0.004);

		// double a = worker.getById(0);

		double[] coef = new double[nodes]; // workers = nodes ... ignore Man
		// Arrays.fill(coef, 0.619);
		// worker.add(coef);

		// y = 0.033*x + 0.469 ~ P.Wi / fi
		worker_temp.mul(workLoad * 0.033);
		// Arrays.fill(coef, 0.469);
		worker_temp.add(coef);

		// (y = 0.05*x + 1.244)*Theta ~ Theta*Ni/fi
		// workerCWL.mul(0.033);
		// workerCWL.add(coef);
		// Vector Theta_worker = Theta.getWorkerVector();
		// workerCWL.mulVector(Theta_worker);

		// Ri / Bij
		// Arrays.fill(coef, 0.047675 );
		// worker.add(coef);

		// Sum of 4 parts:

//    	worker.add(worker_temp.getVectorCoordinate());
//    	worker.add(workerCWL.getVectorCoordinate());
//    	
//    	for(int i = 0; i< position.getVectorCoordinate().length; i++){
//    		if(i==0){
//    			position.setById(i, Util.caclTimeCompute(bestNode));
//    			continue;   			
//    		}
//    		for(Node n:bestNode.getNodeChild()) {
//    			position.setById(i, Util.caclTimeCompute(n)+Util.caclTimeTrans(n,bestNode));
//    		}
//    		
//    		
//    		i++;
//
//    		
//    		
//    	//	System.out.println("DEBUG" + i + " " + position.getVectorCoordinate().length);
//    		position.setById(i, worker.getById(i));
//    	}

		// System.out.println("BestNode in PSO is " + bestNode.getId());

		// TODO
		// calc time serve

//		for (Node n : bestNode.getNodeChild()) {
//			position.setById(n.getId(), Util.caclTimeCompute(n) + Util.caclTimeTrans(n, bestNode));
//			if (!n.getNodeChild().isEmpty()) {
//				for (Node nc : n.getNodeChild()) {
//					position.setById(nc.getId(), Util.caclTimeCompute(nc) + 2 * Util.caclTimeTrans(nc, bestNode));
//				}
//			}
//		}
		// System.out.println("CALC TIME");
		// calcTimeSer(bestNode, position);

		// LOL
		// If all nodes join in calc, we can for all node and calc to root.
		// the function aims to scan all child and descendants.
		calcTimeSerMulti(rtable, position, worker, workLoad);
		return position;

	}

	private void calcTimeSer(Node root, PSOVector position) {
		System.out.println("DEBUG calcTimeSer");
		if (!root.getNodeChild().isEmpty()) {
			for (Node n : root.getNodeChild()) {
				position.setById(n.getId(), Util.caclTimeCompute(n) + (n.getLvl()) * Util.caclTimeTrans(n, root));
				// System.out.println("DEBUG calc node " + n.getId() + " -> " + " hc " +
				// n.getLvl());
				calcTimeSer(n, position);
			}
		}

	}

	/**
	 * Sum pi = 1
	 * 
	 * @param p         particle
	 * @param mapRTable
	 * @return true if not satisfy
	 */
	static boolean constraintF1(PSOParticle p, HashMap<Integer, List<RTable>> mapRTable) {
		// System.out.println(p.getPosition().toStringOutput());
		int j = 0;
		int fix = 0;
		for (Integer id : mapRTable.keySet()) {
			double checkSum = 0;

			List<RTable> rTable = mapRTable.get(id);

			for (int i = 0; i < rTable.size(); i++) {
				// System.out.println(p.getPosition().getById(j));
				checkSum += p.getPosition().getById(j);
				j++;
			}

			if (checkSum != 1.0) {
				return true;
				// double delta = p.getPosition().getById(j-1)+1-checkSum;

//				double d1 = (1 - checkSum) / rTable.size();
//				double d2 = 0;
//
//				for (int i = 0; i < rTable.size(); i++) {
//					d2 = p.getPosition().getById(fix) + d1;
//					p.getPosition().setById(fix, d2);
//
//					fix++;
//				}

			}
			if (fix != j) {
				fix = j;
			}
			;

		}
//		
//		
//		if (p.getPosition().getSum() != 1.0) {
//			// System.out.println("F1 = true");
//			return true;
//		}
		return false;
	}

	/**
	 * Pi >= 0
	 * 
	 * @param p
	 * @return true if not satisfy
	 */
	static boolean constraintF2(PSOParticle p) {
		PSOVector position = p.getPosition();
		for (int i = 0; i < position.getVectorCoordinate().length; i++) {
			if (position.getById(i) < 0)
				return true;
		}
		return false;
	}

	/**
	 * pi*W <= (W + sum(N)) / n
	 * 
	 * @param cWL
	 * @param mapRTable
	 * @param worker 
	 * 
	 * @param p
	 * @return true if not satisfy
	 */
	static PSOVector constraintF3(int workLoad, PSOVector currentWorkload, PSOVector postion, double cWL,
			HashMap<Integer, List<RTable>> mapRTable, PSOVector worker) {
		// double averageWorkload = (workLoad + currentWorkload.getSum()) /
		// postion.getVectorCoordinate().length;
		//System.out.println("worker: " + worker.getById(1));
		
		final int  MINIWL=4;
		int j=0;
		for (Integer id : mapRTable.keySet()) {
			List<RTable> rtable = mapRTable.get(id);
			double WL = rtable.get(0).getReq().getWL();
			HashMap<String, Double> wlNode = new HashMap<String, Double>();

			for (RTable r : rtable) {
				String nodeID = r.getDes();
				double wlN=0;
				int path =1;
				for (RTable r2 : rtable) {
					if((r2.getDes()==r.getDes())&&(r.getId()!=r2.getId())) {
						double test1 = worker.getById(r.getId());
						wlN+=worker.getById(r.getId())*WL;
						if (r2.getNpath()>path) path = r2.getNpath();
					}
				}
				wlNode.put(nodeID, wlN/path);
			}

						
			// calc p
			for (RTable r : rtable) {
				double pen1 = worker.getById(r.getId()) - wlNode.get(r.getDes());
				double pen2 = worker.getById(r.getId()) - wlNode.get(r.getDes())/MINIWL;

				//System.out.println("pen: " + worker.getById(r.getId()));
				if (pen1 > 0) {
				postion.setById(j, postion.getById(j) + pen1);
				}
				
				if (pen2 < 0) {
					postion.setById(j, postion.getById(j) + pen2);
					}
				
				j++;
			}
			

			
		}

//		double averageWorkload = (workLoad + cWL) / postion.getVectorCoordinate().length;
//		boolean check = true;
//		for (int i = 0; i < postion.getVectorCoordinate().length; i++) {
//			double pen = postion.getById(i) - averageWorkload / workLoad;
//			if (pen > 0) {
//				postion.setById(i, postion.getById(i) + pen);
//				check = false;
//			}
//		}
		return postion;
	}

	/**
	 * pW >= aW aW=10
	 * 
	 * @param p
	 * @param workLoad
	 * @return
	 */
	static PSOVector constraintF4(int workLoad, PSOVector postion, double cWL) {
		boolean check = true;

		double averageWorkload = (workLoad + cWL) / postion.getVectorCoordinate().length;
		double a = averageWorkload / 10;
		for (int i = 0; i < postion.getVectorCoordinate().length; i++) {
			double pen = (postion.getById(i) - a) / workLoad;
			if (pen < 0)
				postion.setById(i, postion.getById(i) - pen);
			check = false;
		}
		return postion;
	}
}
