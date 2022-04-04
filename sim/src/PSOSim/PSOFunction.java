package PSOSim;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import multihop.Constants;
import multihop.LogPSO;
import multihop.RTable;
import multihop.node.Node;

class PSOFunction {

	private static final double INFINITY = Double.POSITIVE_INFINITY;
	private int nodes;
	private int testCase;

	public PSOFunction(int nodes, Node bestNode, int testCase) {
		this.nodes = nodes;
		this.testCase = testCase;

	}

	/**
	 * @return PSOVector as value: vector {time(p)}
	 */

	public PSOVector multiFunction(PSOParticle p, PSOVector currentWorkload, Node bestNode, List<RTable> rtable,
			HashMap<Integer, List<RTable>> mapRTable) {

		/**
		 * @param workLoad        number of images from 1 service
		 * @param p               percent workload
		 * @param Theta           share service or not ( 0 or 1)
		 * @param currentWorkload current workload at that node
		 * @return total time
		 */
		int workLoad;
		String pid = p.getName();
		//PSOVector personalBest = p.getBestPosition();
		PSOVector time = new PSOVector(p.getPosition().getDim()); // create a new psoVector same value

		PSOVector ratio = p.getPosition().getVectorRatio(); // ratio, get p of PSO vector

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

		// System.out.println("particle: " + p.getPosition().toStringOutput());
		double CWL = 0;

		if (testCase == 2) {
			// calc t_compute (none- t_wait)
			int jj = 0;
			for (RTable r : rtable) {
				workLoad = (int) r.getReq().getWL();
				double t_compute = 0;
				double subWL = ratio.getById(jj) * workLoad; // new WL
				double totalWL = subWL + r.getcWL(); // adding cWL
				t_compute = totalWL / r.getResource();
				r.setTimeCompute(t_compute);

				jj++;
			}
		} else {
			/**
			 * calc t_compute (include t_wait) output: TimeCompute, CWL
			 */
			List<String> check = new ArrayList<String>();
			int j = 0;
			for (RTable r : rtable) {
				workLoad = (int) r.getReq().getWL();
				double t_compute = 0;
				double subWL = ratio.getById(j) * workLoad; // new WL
				double totalWL = subWL + r.getcWL(); // adding cWL
				t_compute = totalWL / r.getResource();

				if (!check.contains(r.getDes())) {

					List<Integer> r2Id = new ArrayList<Integer>();
					int j2 = 0;
					for (RTable r2 : rtable) {
						if ((r2.getDes().equals(r.getDes()))
								&& (r2.getId() != r.getId() || (r2.getReq().getId() != r.getReq().getId()))) {
							r2Id.add(j2);
							t_compute += ratio.getById(j2) * r2.getReq().getWL() / r2.getResource();

						}
						j2++;
					}

					for (Integer i : r2Id) {
						rtable.get(i).setTimeCompute(t_compute);
					}
					r.setTimeCompute(t_compute);
				}
				check.add(r.getDes());
				j++;
			}
		}

		// Adding tran
		int j2 = 0;
		double TMAX = 0;

		for (RTable r : rtable) {
			double t_trans;

			t_trans = (ratio.getById(j2) * r.getReq().getWL() / Constants.BW) * r.getHop();
			if (r.getId() == 0)
				t_trans = 0;

			r.setTimeTrans(t_trans);
			// position.setById(j, r.getTimeTrans() + r.getTimeCompute());

			TMAX = r.getReq().getWL() / Constants.RES[Constants.TYPE.VEHICLE.ordinal()];

			time.setById(j2, (r.getTimeCompute() + r.getTimeTrans()) / TMAX);
			j2++;
		}
		
//	System.out.println(pid+ " Time calc in func: \n" + time.toStringOutput());

		int j3 = 0;
		for (RTable r : rtable) {
			TMAX = r.getReq().getWL() / Constants.RES[Constants.TYPE.VEHICLE.ordinal()];
			double check1 = (r.getTimeCompute() + r.getTimeTrans()) / TMAX - time.getById(j3);
			// System.out.println("rtable: " + j3 + " " + r.getTimeCompute());
			if (check1 != 0) {
				System.out.println("Fail: " + j3 + " " + r.getDes() + " " + check1);
			//	System.out.println(worker.toStringOutput()); 
			
			}

			j3++;
		}
//		 System.out.println(worker.toStringOutput());
//
		int totalworkLoad = 0;
//		for (Integer id : mapRTable.keySet()) {
//			totalworkLoad += mapRTable.get(id).get(0).getReq().getWL();
//		}

		// System.out.println("BEFORE CONT\n" + position.toStringOutput());
		List<String> f2 = new ArrayList<String>();
		f2.add(constraintF2(time, pid,ratio));

		if ((testCase == 6) || (testCase == 7)) {
		//	constraintF5(totalworkLoad, time, pid, rtable, ratio, mapRTable, testCase);
		}

		// update 3/9 for new constraint
		if ((testCase == 1) || (testCase == 2)) {
			constraintF5(totalworkLoad, time, pid, rtable, ratio, mapRTable, testCase, p);
			constraintF6(totalworkLoad, time, CWL, rtable, ratio, mapRTable, testCase);
		}

		// double TMAX= workLoad/Constants.BW+workLoad/Constants.RES;
		// double TMAX= workLoad/Constants.RES;

		// double A=Constants.A;
		// position.div(TMAX/A);

		// constraintF3(totalworkLoad / check.size(), currentWorkload, position, CWL,
		// mapRTable, worker, rtable);
		// constraintF4(totalworkLoad, position, CWL, rtable, worker, mapRTable);

		//
//		FileWriter myWriterPSO;
//		try {
//			myWriterPSO = new FileWriter("topoPSO_log");
//			myWriterPSO.append(position.toStringOutput());
//			myWriterPSO.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		System.out.println("RATIO: " + worker.toStringOutput());
//		System.out.println("POS: " + position.toStringOutput());
		
//		System.out.println(pid+ " Time in func: \n" + time.toStringOutput());		
		return time;

	}
	
	
	static String constraintF2(PSOVector time, String pid,  PSOVector ratio) {
		boolean c = false;
		for (int i = 0; i < time.getVectorCoordinate().length; i++) {
			if (ratio.getById(i) < 0) {
			System.out.println("F2 at " + pid + " " + ratio.toStringOutput() + "\n" + ratio.getById(i));
			time.setById(0, Constants.MAXDOUBLE);
			return pid;				
			}
		}
		return null;

	}

	static PSOVector constraintF5(int workLoad, PSOVector time, String pid, List<RTable> rtable, PSOVector ratio,
			HashMap<Integer, List<RTable>> mapRTable, int testCase, PSOParticle p) {
		int j = 0;
		double addPen = 0;
		double check = 9999;
		for (RTable r : rtable) {
			double pai = ratio.getById(j);
			double lambWL = r.getResource() / r.getReq().getWL();
			// double t = worker.getById(j)*r.getReq().getWL()/r.getResource(); // time
			// process
//			double t = (worker.getById(j)* r.getReq().getWL() + r.getcWL())  / r.getResource(); // time process include
//																								// cWL
//			int j2 = 0;
//			for (RTable r2 : rtable) {
//				if ((r2.getDes().equals(r.getDes()))
//						&& (r2.getId() != r.getId() || (r2.getReq().getId() != r.getReq().getId()))) {
//					t += worker.getById(j2)*r2.getReq().getWL()/r2.getResource(); // sum up time_process in node: r.getDes()
//				}
//				j2++;
//			}
//			double A= Constants.A;
//			if(testCase==7) {
//				A=0.1;
//			}
			double pen = pai - lambWL;

			if (!r.getDes().equals(r.getReq().getSrcNode().getName())) { // khong xet req node trong dieu kien nay
				// pen = pai - lambWL;
				if (pen > 0) {
					pen = INFINITY;
					time.setById(j, time.getById(j) + pen);
					check = j;
					// System.out.println("pa->i: " + pai + " " + r.getReq().getSrcNode().getName()
					// + "->" + r.getDes());
					// addPen += pen;
					System.out.println("F3 "+ pid  + " :"  + ratio.toStringOutput() + "\n" + r.getReq().getId() + " " + j);
					//System.out.println(p.getBestPosition().toStringOutput());
//					System.out.println("j pa->i: " + j + " p" + r.getReq().getSrcNode().getName() + "->" + r.getDes()
//							+ " :" + pai);
	//				System.out.println(r.toString());

				}
			}
			// addPen += pen;
			j++;

		}

		if (check != 9999) {
			LogPSO log = LogPSO.getInstance();
		//	log.log("\tF3." + j + "\t");
			// log.log("\n" +worker.toStringOutput() + "\n");
		}

		for (int i = 0; i < time.getDim(); i++) {
			// postion.setById(i, postion.getById(i)+ addPen);
		}

//		for (RTable r : rtable) {
//			double t = worker.getById(j)*r.getReq().getWL()/r.getResource(); // time process
//			//double t = (worker.getById(j)+r.getcWL())*r.getReq().getWL()/r.getResource(); // time process include cWL
//			int j2 = 0;
//			for (RTable r2 : rtable) {
//				if ((r2.getDes().equals(r.getDes()))
//						&& (r2.getId() != r.getId() || (r2.getReq().getId() != r.getReq().getId()))) {
//					t += worker.getById(j2)*r2.getReq().getWL()/r2.getResource(); // sum up time_process in node: r.getDes()
//				}
//				j2++;
//			}
//			double A= Constants.A;
//			if(testCase==7) {
//				A=0.1;
//			}
//
//			double pen = t/(Constants.TS*(1+A)); 
//			if (pen > 1) {
//				postion.setById(j, postion.getById(j) + pen);
//			}
//
//			//sump.put(nodeID, p);
//			j++;
//		}
		return time;
	}

	static PSOVector constraintF6(int workLoad, PSOVector time, double cWL, List<RTable> rtable, PSOVector ratio,
			HashMap<Integer, List<RTable>> mapRTable, int testCase) {
		int j = 0;
		double addPen = 0;
		double check = 9999;
		List<Integer> checked = new ArrayList<Integer>();
		for (RTable r : rtable) {

			double t_ser = 0;
			int reqID = r.getReq().getId();
			if (!checked.contains(reqID)) {
				double t_ser_r = 0;
				double WLlamb = r.getReq().getWL() / r.getResource();
				if (!r.getDes().equals(r.getReq().getSrcNode().getName())) {
					t_ser_r = time.getById(j);
				}
				int j2 = 0;
				for (RTable r2 : rtable) {
					double t_ser_r2 = time.getById(j2);
					if ((r2.getReq().getId() == reqID) && (r2.getId() != r.getId())) {
						t_ser = (t_ser_r > t_ser_r2) ? t_ser_r : t_ser_r2;
					}
					j2++;
				}

				double pen = t_ser - WLlamb;

				if (pen > 0) {
					pen = INFINITY;
					time.setById(j, t_ser + pen);
					// addPen += pen;
					check = j;
				}
			}
			checked.add(reqID);
			j++;

		}

		if (check != 9999) {
			LogPSO log = LogPSO.getInstance();
			System.out.println("F4: " + ratio.toStringOutput()) ;
			// log.log("\tF4." + j + "\t");
		}

		for (int i = 0; i < time.getDim(); i++) {
			// postion.setById(i, postion.getById(i)+ addPen);
		}

//		for (RTable r : rtable) {
//			double t = worker.getById(j)*r.getReq().getWL()/r.getResource(); // time process
//			//double t = (worker.getById(j)+r.getcWL())*r.getReq().getWL()/r.getResource(); // time process include cWL
//			int j2 = 0;
//			for (RTable r2 : rtable) {
//				if ((r2.getDes().equals(r.getDes()))
//						&& (r2.getId() != r.getId() || (r2.getReq().getId() != r.getReq().getId()))) {
//					t += worker.getById(j2)*r2.getReq().getWL()/r2.getResource(); // sum up time_process in node: r.getDes()
//				}
//				j2++;
//			}
//			double A= Constants.A;
//			if(testCase==7) {
//				A=0.1;
//			}
//
//			double pen = t/(Constants.TS*(1+A)); 
//			if (pen > 1) {
//				postion.setById(j, postion.getById(j) + pen);
//			}
//
//			//sump.put(nodeID, p);
//			j++;
//		}
		return time;
	}

//	private void calcTimeSerMulti(List<RTable> rtable, PSOVector position, PSOVector worker, double workLoad) {
//		// TODO position is time_server, rtable is list of node including virtual node
//		// must be assigned
////		0 Man Man 0
////		1 W1 Man 1
////		2 W3 W1 2
////		3 W5 W1 2
////		4 W2 Man 1
////		5 W4 W2 2
////		6 W5 W2 2
//		// position.setById(0, worker.getById(0) * workLoad /
//		// rtable.get(0).getResource());
//		for (RTable r : rtable) {
//			double t_compute = 0;
//			double t_trans;
//
//			t_compute = worker.getById(r.getId()) * workLoad / r.getResource();
//			if (r.getNpath() > 1) {
//				// process with r.getDes()
//				for (RTable r2 : rtable) {
//					if ((r2.getDes().equals(r.getDes())) && (r2.getId() != r.getId())) {
//						t_compute += worker.getById(r2.getId()) * workLoad / r2.getResource();
//
//					}
//				}
//			}
//
//			t_trans = (worker.getById(r.getId()) * workLoad / Constants.BW) * r.getHop();
//
//			if (r.getId() == 0) {
//				t_trans = 0;
//			}
//			;
//			position.setById(r.getId(), t_trans + t_compute);
//
//		}
//
////		for (int i=1; i<=rtable.size();i++) {	
////			position.setById(i, worker.getById(i)*workLoad/Constants.BW + t_compute);
////		}
//
//	}

//	public PSOVector mainFunction(PSOParticle p, int workLoad, PSOVector currentWorkload, Node bestNode,
//			List<RTable> rtable) {
//		/**
//		 * @param workLoad        number of images from 1 service
//		 * @param p               percent workload
//		 * @param Theta           share service or not ( 0 or 1)
//		 * @param currentWorkload current workload at that node
//		 * @return total time
//		 */
//		PSOVector position = p.getPosition().clone();
//		// Vector p_temp = position.clone(); // for 2nd part
//
//		PSOVector Theta = new PSOVector(nodes);
//		for (int i = 0; i < position.getVectorCoordinate().length; i++) {
//			if (position.getById(i) != 0) {
//				Theta.setById(i, 1);
//			}
//		}
//
//		PSOVector worker = p.getPosition().getVectorRatio(); // get ratio
//		PSOVector worker_temp = worker.clone();
//		PSOVector workerCWL = currentWorkload.getVectorRatio(); // Worker current Workload
//
//		// double man = p.getPosition().getById(0);
//		// double manCWL = currentWorkload.getById(0); //Manager current Workload
//
//		/*
//		 * Calculate time for manager
//		 */
////        double Tman = (man*workLoad*0.005 + 0.619)   // y = 0.005*x + 0.0619   ~ P*Wi / Bij
////        				+ (0.0199*man*workLoad+0.291)    // y = 0.0199*x + 0.291 ~ P.Wi / fi
////        				+ (0.0199*manCWL+0.291)*Theta.getById(0) // (y = 0.0199*x + 0.291)*Theta ~ Theta*Ni/fi
////        				+ 0.047675; // Ri / Bij
//
//		/*
//		 * calculate time for workers
//		 */
//
//		// worker represent t_serve on Node.
//
//		// y = 0.004*x + 0.0619 ~ P*Wi / Bij
//		// worker.mul(workLoad * 0.004);
//
//		// double a = worker.getById(0);
//
//		double[] coef = new double[nodes]; // workers = nodes ... ignore Man
//		// Arrays.fill(coef, 0.619);
//		// worker.add(coef);
//
//		// y = 0.033*x + 0.469 ~ P.Wi / fi
//		worker_temp.mul(workLoad * 0.033);
//		// Arrays.fill(coef, 0.469);
//		worker_temp.add(coef);
//
//		// (y = 0.05*x + 1.244)*Theta ~ Theta*Ni/fi
//		// workerCWL.mul(0.033);
//		// workerCWL.add(coef);
//		// Vector Theta_worker = Theta.getWorkerVector();
//		// workerCWL.mulVector(Theta_worker);
//
//		// Ri / Bij
//		// Arrays.fill(coef, 0.047675 );
//		// worker.add(coef);
//
//		// Sum of 4 parts:
//
////    	worker.add(worker_temp.getVectorCoordinate());
////    	worker.add(workerCWL.getVectorCoordinate());
////    	
////    	for(int i = 0; i< position.getVectorCoordinate().length; i++){
////    		if(i==0){
////    			position.setById(i, Util.caclTimeCompute(bestNode));
////    			continue;   			
////    		}
////    		for(Node n:bestNode.getNodeChild()) {
////    			position.setById(i, Util.caclTimeCompute(n)+Util.caclTimeTrans(n,bestNode));
////    		}
////    		
////    		
////    		i++;
////
////    		
////    		
////    	//	System.out.println("DEBUG" + i + " " + position.getVectorCoordinate().length);
////    		position.setById(i, worker.getById(i));
////    	}
//
//		// System.out.println("BestNode in PSO is " + bestNode.getId());
//
//		// TODO
//		// calc time serve
//
////		for (Node n : bestNode.getNodeChild()) {
////			position.setById(n.getId(), Util.caclTimeCompute(n) + Util.caclTimeTrans(n, bestNode));
////			if (!n.getNodeChild().isEmpty()) {
////				for (Node nc : n.getNodeChild()) {
////					position.setById(nc.getId(), Util.caclTimeCompute(nc) + 2 * Util.caclTimeTrans(nc, bestNode));
////				}
////			}
////		}
//		// System.out.println("CALC TIME");
//		// calcTimeSer(bestNode, position);
//
//		// LOL
//		// If all nodes join in calc, we can for all node and calc to root.
//		// the function aims to scan all child and descendants.
//		//calcTimeSerMulti(rtable, position, worker, workLoad);
//		return position;
//
//	}

//	private void calcTimeSer(Node root, PSOVector position) {
//		System.out.println("DEBUG calcTimeSer");
//		if (!root.getNodeChild().isEmpty()) {
//			for (Node n : root.getNodeChild()) {
//				position.setById(n.getId(), Util.caclTimeCompute(n) + (n.getLvl()) * Util.caclTimeTrans(n, root));
//				// System.out.println("DEBUG calc node " + n.getId() + " -> " + " hc " +
//				// n.getLvl());
//				calcTimeSer(n, position);
//			}
//		}
//
//	}

	/**
	 * Sum pi = 1
	 * 
	 * @param p         particle
	 * @param mapRTable
	 * @return true if not satisfy
	 */
	static boolean constraintF1(PSOParticle p, HashMap<Integer, List<RTable>> mapRTable) {
		// System.out.println(p.getPosition().toStringOutput());
		LogPSO log = LogPSO.getInstance();
		int j = 0;
    	Set<Integer> keySet= mapRTable.keySet();
    	List<Integer> sortedList = new ArrayList<>(keySet);
    	Collections.sort(sortedList);
    	
    	
    	for (Integer id:sortedList) { // req 0, 1
			double checkSum = 0;

			List<RTable> rTable = mapRTable.get(id);

			for (int i = 0; i < rTable.size(); i++) {
				// System.out.println(p.getPosition().getById(j));
				checkSum += p.getPosition().getById(j);
				j++;
			}
			double check = checkSum - 1;
			double MIN = 1 / 1E8;
			if (check > MIN) {
				System.out.println("checkSum = " + checkSum);
				log.log("\tF1\t");
				return true;
			}
			;

		}
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
	    		System.out.println("F2 at: " + p.getName() + " " + p.getPosition().getVectorRatio().toStringOutput());		
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
	 * @param rtable
	 * 
	 * @param p
	 * @return true if not satisfy
	 */
//	static PSOVector constraintF3(int workLoad, PSOVector currentWorkload, PSOVector postion, double cWL,
//			HashMap<Integer, List<RTable>> mapRTable, PSOVector worker, List<RTable> rtable) {
//		// double averageWorkload = (workLoad + currentWorkload.getSum()) /
//		// postion.getVectorCoordinate().length;
//		// System.out.println("worker: " + worker.getById(1));
//		int numM = 1;
//		double B = Constants.B;
//		for (Integer id : mapRTable.keySet()) {
//			List<RTable> rtableMap = mapRTable.get(id);
//			List<String> check = new ArrayList<String>();
//			for (RTable r : rtableMap) {
//				String nodeID = r.getDes();
//				if (!check.contains(nodeID)) {
//					numM++;
//					check.add(nodeID);
//				}
//			}
//		}
//
//		numM /= mapRTable.size();
//		double NM = Constants.NUM_REQ / numM;
//
//		HashMap<String, Integer> paths = Util.getPahts(rtable);
//
//		int j = 0;
//		for (RTable r : rtable) {
//			double path = paths.get(r.getDes());
//			double pen1 = worker.getById(j) - NM / path;
//			// double pen2= worker.getById(j) - (NM / path)/10;
//			String debug = r.getDes();
//
//			if (pen1 > 0) {
//				postion.setById(j, postion.getById(j) + pen1 * B);
//
//				// if (debug.equals("14")) {
////					 System.out.println(" Route " + r.getRoute() + " p "
////					 +worker.getById(j));
////				}
//			}
//
////			if (pen2 < 0) {
////			postion.setById(j, postion.getById(j) + pen2);
////			}
//
//			j++;
//		}
//
////		for (Integer id : mapRTable.keySet()) {
////			List<RTable> rtable = mapRTable.get(id);
////			double WL = rtable.get(0).getReq().getWL();
////			HashMap<String, Double> wlNode = new HashMap<String, Double>();
////
////			for (RTable r : rtable) {
////				String nodeID = r.getDes();
////				double wlN=worker.getById(r.getId())*WL;
////				int path =1;
////				for (RTable r2 : rtable) {
////					if((r2.getDes()==r.getDes())&&(r.getId()!=r2.getId())) {
////						double test1 = worker.getById(r.getId());
////						wlN+=worker.getById(r2.getId())*WL;
////						//if (r2.getNpath()>path) path = r2.getNpath();
////						path++;
////					}
////				}
////				wlNode.put(nodeID, wlN*path);
////			}
////
////						
////			// calc p
////			for (RTable r : rtable) {
////				double pen1 = worker.getById(r.getId()) - workLoad/wlNode.get(r.getDes());
////				double pen2 = worker.getById(r.getId()) - wlNode.get(r.getDes())/2;
////
////				//System.out.println("pen: " + worker.getById(r.getId()));
////				if (pen1 > 0) {
////				postion.setById(j, postion.getById(j) + pen1);
////				}
////				
//////				if (pen2 < 0) {
//////					postion.setById(j, postion.getById(j) + pen2);
//////					}
////				
////				j++;
////			}
////			
////
////			
////		}
//
////		double averageWorkload = (workLoad + cWL) / postion.getVectorCoordinate().length;
////		boolean check = true;
////		for (int i = 0; i < postion.getVectorCoordinate().length; i++) {
////			double pen = postion.getById(i) - averageWorkload / workLoad;
////			if (pen > 0) {
////				postion.setById(i, postion.getById(i) + pen);
////				check = false;
////			}
////		}
//		return postion;
//	}

	/**
	 * pW >= aW aW=10
	 * 
	 * @param p
	 * @param workLoad
	 * @param rtable
	 * @param worker
	 * @param mapRTable
	 * @return
//	 */
//	static PSOVector constraintF4(int workLoad, PSOVector postion, double cWL, List<RTable> rtable, PSOVector worker,
//			HashMap<Integer, List<RTable>> mapRTable) {
////		boolean check = true;
////		int numM = 1;
////		double B=Constants.B;
////		for (Integer id : mapRTable.keySet()) {
////			List<RTable> rtableMap = mapRTable.get(id);
////			List<String> check = new ArrayList<String>();
////			for (RTable r : rtableMap) {
////				String nodeID = r.getDes();
////				if (!check.contains(nodeID)) {
////					numM++;
////					check.add(nodeID);
////				}
////			}
////		}
////
////		numM /= mapRTable.size();
//		HashMap<String, Integer> paths = Util.getPahts(rtable);
////
////		double NM = Constants.NUM_REQ / numM;
//
//		double a = Constants.RES[Constants.TYPE.VEHICLE.ordinal()] / Constants.BW;
//		HashMap<String, Double> sump = new HashMap<String, Double>();
//		double C = Constants.C;
//		int j = 0;
//
//		for (RTable r : rtable) {
//			String nodeID = r.getDes();
//			double p = worker.getById(j);
//
//			int j2 = 0;
//			for (RTable r2 : rtable) {
//				if ((r2.getDes().equals(r.getDes()))
//						&& (r2.getId() != r.getId() || (r2.getReq().getId() != r.getReq().getId()))) {
//					p += worker.getById(j2);
//
//				}
//				j2++;
//			}
//
//			double path = paths.get(r.getDes());
//			// a=(NM / path)/5;
//			double pen = p - a;
//
//			if (pen < 0) {
//				postion.setById(j, postion.getById(j) - pen * C);
//			}
//
//			sump.put(nodeID, p);
//			j++;
//		}
////		double averageWorkload = (workLoad + cWL) / postion.getVectorCoordinate().length;
////		// double a = averageWorkload / 10;
////		
////		for (int i = 0; i < postion.getVectorCoordinate().length; i++) {
////			double pen = (postion.getById(i) - a) / workLoad;
////			if (pen < 0)
////				postion.setById(i, postion.getById(i) - pen);
////			check = false;
////		}
//		return postion;
//	}
}
