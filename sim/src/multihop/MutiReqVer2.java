package multihop;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import PSOSim.PSOSwarm;
import PSOSim.PSOVector;

public class MutiReqVer2 {
	
	public static void main(String[] args) throws IOException {

		/**
		 * ------------------------------- Init node -------------------------------
		 **/

		// TODO:
		// Create initTopo(m, n) > return topo[].
		int _m = 5;
		int _n = 5;
		int len = _m * _n;

		List<Node> topo = new ArrayList<Node>();
		topo = createTopo(_m, _n);
		

		// create grid topo m*n nodes
		setupTopo(topo);
		

		FileWriter myWriterPSO;
		myWriterPSO = new FileWriter("topoPSO.txt");
		myWriterPSO.write("PSO: " + "Number Of Paritcles = " + Constants.particles + "| Epchos = " + Constants.epchos);
		myWriterPSO.write(
				"\n" + "WL\t" + "nodeID\t" + "path\t" + "new_Workload\t" + "timeCompute\t" + "timeTrans\t" + "timeServ\t"+ "curr_Workload\t\n" );

		int WL = 100;

		for (int w = 1; w <= 1; w++) {
			WL = 100 * w;
			//WL = 500;
			myWriterPSO.write("\n");
			//System.out.println("\nWORKLOAD= " + WL);
			List<RTable> rtable = new ArrayList<RTable>();
			
			//create RTable for multi reqs
			for(int reqId=1; reqId<=2;reqId++) {
				Node reqNode = topo.get(reqId*6); 
				rtable = createRoutingTable(topo, rtable, new RequestPSO(reqId, WL, reqNode));
			}
/*
			for(int reqId=1; reqId<=2;reqId++) {

				System.out.println("\nWORKLOAD= " + WL +" | REQ= " + reqId);
				myWriterPSO.write("req"+reqId+"\n");
				
				// update new req_node
				//int idNodeReq = topo.size() + 1;
				//Node req = new Node(idNodeReq, "REQ" + reqId, 1+10*reqId, 1+10*reqId);
				//req.setRes(Constants.RES);

				//updateTopo(topo, req);

				// create topo for the req_node
				rtable = createRoutingTable(topo, rtable, req);
				
				Node bestNode = req; // choose node to process, (only effective with gain-alg)

				System.out.println("\n***********PSO Running***********\n");
				
				
				
				HashMap<Integer, Double> resultPSO = getPSO(WL, bestNode, rtable); // estimate pi for node i

				Set<Integer> rID = resultPSO.keySet();
				for (Integer id : rID) {
					rtable.get(id).setRatio(resultPSO.get(id) / WL);
				}
				
				

				// duplicate calc t_ base ration
				for (RTable r : rtable) {
					double compute = 0;
					double trans;
					double workLoad = WL;
					double subWL= r.getRatio() * workLoad; // new WL
					//System.out.println("0_Adding: " + r.getDes() + " " + subWL);
					double totalWL = subWL + r.getcWL(); // adding cWL
					compute =  totalWL/ r.getResource(); // totalTime
					
					if (r.getNpath() > 1) { // if Multi rout to node r
						for (RTable r2 : rtable) {
							if ((r2.getDes() == r.getDes()) && (r2.getId() != r.getId())) {
								compute += r2.getRatio() * workLoad / r2.getResource(); // adding time(newWL) route2
								subWL+=r2.getRatio() * workLoad; // adding newWL route2
							}
						}
					}
					
					//System.out.println("1_Current: " + r.getDes() + " " + r.getcWL());
					subWL+=r.getcWL();

					trans = (r.getRatio() * workLoad / Constants.BW) * r.getHop();

					if (r.getId() == 0) {
						trans = 0;
					}
					;
					
					
					for (Node t:topo) {
						if(t.getName().equals(r.getDes())) {
							//System.out.println("2_Node: " + t.getName() + " " + subWL);
							t.setWL(subWL);
						}
					}
					
					r.setTimeCompute(compute);
					r.setTimeTrans(trans);
					double ser = compute + trans;
					myWriterPSO.write(WL + " \t " + r.getDes() + " \t " + r.getRoute() + " \t " + r.getRatio() * WL + " \t "
							+ r.getTimeCompute() + " \t " + r.getTimeTrans() + " \t" + ser + "\t" + r.getcWL()+"\n");
					
					
				} // END LOG TIME IN RTABLE
				
				System.out.println("RESULT: ");
				
				rtable.forEach((re) -> {
					System.out.println(">" + re.toString());

				});
				
//				System.out.println("\ncheck Topo: ");
//				topo.forEach((t) -> {
//					System.out.println(">" + t.getName() + " " +  t.getWL());
//
//				});

				System.out.println("Proportion PSO " + bestNode.getName() + " " + resultPSO);
			} //END FOR REQ
*/
		} //END FOR WORKLOAD 
		
		
		
		myWriterPSO.close();
	}

	private static void updateTopo(List<Node> topo, Node req) {
		topo.add(req);
		for (Node node : topo) {
			if (node.checkLK(req) && (node.getId() != req.getId())) {
				node.getNodeLK().add(req);
				req.getNodeLK().add(node);
			}
		}
	}

	/**
	 * create topo size m*n
	 * 
	 * @param m: longs
	 * @param n: width
	 */
	private static List<Node> createTopo(int m, int n) {
		int space = 10;
		// int len=m*n;
		List<Node> topo = new ArrayList<Node>();
		// topo.
		int id = 0;
		for (int i = 0; i < m; i++) { // y
			for (int j = 0; j < n; j++) { // x
				topo.add(id, new Node(id, "W" + id, j * space, i * space));
				// topo[id] = new Node (id,"W" + id, j*space,i*space);
				// System.out.println(topo[id].toString());
				topo.get(id).setRes(Constants.RES);
				id++;
			}
		}

		return topo;
	}

	private static List<RTable> createRoutingTable(List<Node> topo, List<RTable> rtable, RequestPSO req) {
		// adding root of req: reqID as name and
		Node root = req.getSrcNode();
		rtable.add(0, new RTable(0, root.getName(), root.getName(), 0, root.getRes()));

		int id = 1;

		for (Node n1 : root.getNodeLK()) {
			n1.setLvl(1);

			rtable.add(id, new RTable(id, n1.getName(), root.getName(), 1, n1.getRes()));
			rtable.get(id).setNpath(1);
			// System.out.println(">>\tADD DES: " + n1.getName() + " ROUTE: " +
			// root.getName() + " | " + 1);

			id++;
		}

		for (Node n1 : root.getNodeLK()) {
			// System.out.println("\n>NODE DIRECT: " + n1.getName() + " ROUTE: " +
			// root.getName());
			for (Node n2 : n1.getNodeLK()) {
				int npath = 2;
				if (n2.getId() != root.getId() && (n2.getLvl() > n1.getLvl())) {
					n2.setLvl(2);
					// adding vNode
					n2.getvNode().add(n1);
					rtable.add(id, new RTable(id, n2.getName(), n1.getName(), 2, n1.getRes()));
					rtable.get(id).setNpath(npath);
					// System.out.println(">>\tADD DES: " + n2.getName() + " ROUTE: " + n1.getName()
					// + " | " + npath);

					id++;

				}
			}
		}
		
		for (RTable r : rtable) {
			for (Node t:topo) {
				if(t.getName().equals(r.getDes())) {
					r.setcWL(t.getWL());
				}
			}
		}

		System.out.println("created RoutingTable >> Routing Table: ");
		rtable.forEach((e) -> {
			System.out.println(">" + e.toString());
		});
		
		return rtable;

	}

	private static void updateGain(Node n) {
		if (n.getLvl() == 0) {
			return;
		}
		// System.out.println(n.getName() + " update to " + n.getParent().getName());
		double k = Util.calcDistance(n, n.getParent());
		k = n.getLvl() - n.getParent().getLvl();
		k = Math.exp(k * Constants.ALPHA);
		// double k=
		// Math.abs((n.getLat()-n.getParent().getLat())*(n.getLat()-n.getParent().getLat())+(n.getLng()-n.getParent().getLng())*(n.getLng()-n.getParent().getLng()));
		// k= 0-Math.sqrt(k);

		// Sum (Res*1/dis)
		// n.getParent().setGain(n.getParent().getGain()+n.getGain()*Math.exp(k)*5000);
		n.getParent().setGain(n.getParent().getGain() + n.getGain() / k);

		// updateGain(n.getParent());

	}

	private static void calcRank(Node[] topo) {
		double rank;

		for (Node node : topo) {
			if (node.getId() == 0) {
				rank = node.getRes() / node.getGain();
			} else {
				rank = (1 / (node.getLvl() - node.getParent().getLvl()) + node.getRes()) / node.getParent().getGain();
			}
			node.setRank(rank);
		}
	}

	private static void updateCluster(Node n) {
		if (n.getLvl() == 0) {
			return;
		}
		n.getParent().getNodeCluster().add(n);
		n.getParent().getNodeCluster().addAll(n.getNodeChild());

		// System.out.println(n.getParent().getId() + " adding " + n.getId() + " and
		// child");
	}

	private static void calcRankCluster(Node[] topo) {
		for (Node node : topo) {

			double rankCluster = node.getRank();
			for (Node n : node.getNodeCluster()) {
				// System.out.println("Find rankCluster node " + node.getId() + " iterator: " +
				// n.getId());
				if (n.getRank() > rankCluster) {
					rankCluster = n.getRank();
				}
			}

			node.setRankCluster(rankCluster);
		}
	}

	private static Node findBestNode(double workLoad, Node[] topo, double lat, double lng) {
		// find range of node near workload and compare rankCluster and choose
		double a = Constants.MAXDOUBLE;
		Node x = new Node(Constants.MAXINT, "x", lat, lng);

		List<Node> listNode = new ArrayList<Node>();

		for (Node node : topo) {
			if (node.checkLK(x))
				listNode.add(node);
		}

		Node bestNode = null;
		for (Node node : listNode) {
			// System.out.println("Process node " + node.getId());

			double k = node.getRankCluster();
			if (k < a) {
				a = k;
				bestNode = node;
			}
		}
		System.out.println("Best Node is " + bestNode.getId());

		return bestNode;
	}

	private static HashMap<Integer, Double> getProportion(double workload, Node bestNode) {
		// System.out.println("DEBUG for node " + bestNode.getId());
		HashMap<Integer, Double> result = new HashMap<Integer, Double>();
		int num = bestNode.getNodeChild().size() + 1;
		double[] p = new double[num];

		p[0] = bestNode.getRes() / bestNode.getGain();
		result.put(bestNode.getId(), p[0]);
		bestNode.setWL(p[0] * workload);

		int i = 1;
		for (Node n : bestNode.getNodeChild()) {
			double k = Util.calcDistance(n, n.getParent());
			k = n.getLvl() - n.getParent().getLvl();
			k = Math.exp(k * Constants.ALPHA);

			p[i] = (n.getGain() / k) / bestNode.getGain();
			result.put(n.getId(), p[i]);
			n.setWL(p[i] * workload);
			i++;
		}
		double test = 0;
		for (int j = 0; j < p.length; j++) {
			test += p[j];
			// System.out.println("DEBUG p " + j + " " + p[j] );
		}
		// System.out.println("DEBUG sumP" +test );

		return result;

	}

	/**
	 * get rations for networks using PSO config PSO prams in the method
	 * 
	 * @param workload is total workload need assigned
	 * @param bestNode is the data owner node, others connect to it
	 * @param rtable   is full info of network, can cacl time_trans, time_compute
	 *                 using prams in the table
	 * @return ration p as HashMap<id,value>
	 */
	private static HashMap<Integer, Double> getPSO(double workload, Node bestNode, List<RTable> rtable) {

		HashMap<Integer, Double> result = new HashMap<Integer, Double>();
		// int num = bestNode.getNodeChild().size()+1;
		// int num = 6; // number of node in network
		int num = rtable.size();
		double[] p = new double[num];

		int particles = Constants.particles;
		int epchos = Constants.epchos;
		int nnodes = num; // number of nodes/dimenssion
		int workLoad = (int) workload;

		int nworker = num;

		double[] cWorkload = new double[nworker]; // workers
		for (int i = 0; i < cWorkload.length; i++) {
			cWorkload[i] = 0;
		}

		PSOVector currentWorkload = new PSOVector(cWorkload);

		PSOSwarm swarm = new PSOSwarm(particles, epchos, nnodes, workLoad, currentWorkload, bestNode, rtable);

		Map<Integer, Double> ratio = swarm.run("service-id-string");
		// ` System.out.println(ratio.toString());

//		p[0]=bestNode.getRes()/bestNode.getGain();
//		result.put(bestNode.getId(), p[0]);
//		bestNode.setWL(p[0]*workload);
//		
//		int i=1;
//		for(Node n:bestNode.getNodeChild()) {
//			double k = Util.calcDistance(n, n.getParent());
//			p[i]=(n.getGain()*Constants.FIXNUM/k)/bestNode.getGain();
//			result.put(n.getId(), p[i]);
//			n.setWL(p[i]*workload);
//			i++;
//		}
		result = (HashMap<Integer, Double>) ratio;

		return result;

	}

	private static List<HashMap<Integer, Double>> getAllProportion(double workload, Node bestNode) {
		List<HashMap<Integer, Double>> result = new ArrayList<HashMap<Integer, Double>>();
		int num = bestNode.getNodeChild().size() + 1;

		result.add(0, getProportion(workload, bestNode));

		Set<Integer> cnode = result.get(0).keySet();
		int i = 0;
		double[] p = new double[num];

		for (Integer n : cnode) {
			p[i] = result.get(0).get(n);
			// System.out.println("D " + i + p[i]+num);
			i++;
		}

		i = 1;
		for (Node n : bestNode.getNodeChild()) {
			result.add(i, getProportion(p[i] * workload, n));
			i++;
		}

		return result;

	}

	private static void setupTopo(List<Node> topo) {
		for (Node node : topo) {
			for (Node nodec : topo) {

				if ((node.checkLK(nodec)) && (node.getId() != nodec.getId())) {
					node.getNodeLK().add(nodec); // set neighbor

//					if (nodec.getLvl() > node.getLvl()) { // set hopcount
//						nodec.setLvl(node.getLvl() + 1);
//
//					//	if (nodec.getParent() == null) {
//						//	node.getNodeChild().add(nodec); // add Child
//						//	nodec.setParent(node); // add Parent - faking !!! when a child node connects to only
//													// node-as-parents
//					//	}
//
////						
////						double k= Math.abs((node.getLat()-nodec.getLat())*(node.getLat()-nodec.getLat())+(node.getLng()-nodec.getLng())*(node.getLng()-nodec.getLng()));
////						k= 0-Math.sqrt(k);
////						node.setGain(node.getGain()+nodec.getGain()*Math.exp(k)); // calcGain
//					}
				}
			}
		}

	}

	private static int findMaxLvl(Node[] topo) {
		int max = 0;
		for (Node node : topo) {
			if (node.getLvl() > max)
				max = node.getLvl();
		}
		return max;
	}

}
