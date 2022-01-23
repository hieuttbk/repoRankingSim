package multihop;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import PSOSim.Swarm;
import PSOSim.Vector;

public class MainRPL {
	public static void main(String[] args) throws IOException {

		/**
		 * ------------------------------- Init node -------------------------------
		 **/

		// location
		Node m = new Node(0, "Man", 0, 0);
		Node w1 = new Node(1, "W1", 10, 0);
		Node w2 = new Node(2, "W2", 0, 10);
		Node w3 = new Node(3, "W3", 20, 0);
		Node w4 = new Node(4, "W4", 0, 20);
		Node w5 = new Node(5, "W5", 10, 10);
		Node topo[] = { m, w1, w2, w3, w4, w5 };

		// level
		m.setLvl(0);

		// create Res and gain. -- fixed for same input.
		for (Node node : topo) {
			// node.setRes((10- node.getId())*10);
			node.setRes(Constants.RES);
			node.setGain(node.getRes());
		}

		/** Find neighbor and Child **/
		// in oneM2M, neighbor is determined by registry procedures

		setupTopo(topo);
		createRoutingTable(topo);

		// update gain from high level to low auto
		int maxLvl = findMaxLvl(topo);
		for (int g = maxLvl; g > 0; g--) {
			for (Node node : topo) {
				if (node.getLvl() == g) {
					updateGain(node);
					updateCluster(node);
				}
			}
		}

		calcRank(topo);

		// findCluster(m);
		calcRankCluster(topo);

		for (Node n : topo) {
			System.out.println(
					"Node: " + n.getId() + " Rank: " + n.getRank() + " RankCluster: " + n.getRankCluster() + "");

		}

//		 debug infomation of network
		System.out.println("\n>>> SHOW NODE INFO");
		for (Node node : topo) {
			System.out.println("\nNode " + node.getName() + ": lvl = " + node.getLvl() + " | gain = " + node.getGain()
					+ " | res = " + node.getRes());

			System.out.print("  Neighbour: ");
			for (Node nodec : node.getNodeLK()) {
				System.out.print(nodec.getName() + ", ");
			}

			System.out.print("\n  Child: ");
			for (Node nodec : node.getNodeChild()) {
				System.out.print(nodec.getName() + ", ");
			}
		}

		// create table routing
		// id Des Route
		for (Node node : topo) {
			// System.out.println("\nNode " + node.getName() + ": lvl = " + node.getLvl() +
			// " | gain = "+ node.getGain() + " | res = "+ node.getRes());

			System.out.print("  Neighbour: ");
			for (Node nodec : node.getNodeLK()) {
				System.out.print(nodec.getName() + ", ");
			}

			System.out.print("\n  Child: ");
			for (Node nodec : node.getNodeChild()) {
				System.out.print(nodec.getName() + ", ");
			}
		}

		FileWriter myWriter;

		myWriter = new FileWriter("topoRANKING.txt");

		FileWriter myWriterPSO;

		myWriterPSO = new FileWriter("topoPSO.txt");

		myWriter.write("TOPO\n");
		myWriter.write("Name\t" + "ID\t" + "Child\t" + "HopCount\t" + "Gain\t" + "Res\t\n");
		for (Node node : topo) {

			myWriter.write(node.getName() + "\t" + node.getId() + "\t" + node.getChildString() + "\t" + node.getLvl()
					+ "\t" + node.getGain() + "\t" + node.getRes() + "\n");

		}
		// myWriter.close();
		myWriter.write("\nRANKING: " + "anpha = " + Constants.ALPHA);
		myWriterPSO.write("PSO: " + "Number Of Paritcles = " + Constants.particles + "| Epchos = " + Constants.epchos);

		int WL = 80;
		for (Node n : topo) {
			myWriter.write("\nNode: " + n.getId() + " Rank: " + n.getRank() + " RankCluster: " + n.getRankCluster());

		}

		myWriter.write("\n\n" + "WL\t" + "nodeID\t" + "worload\t" + "timeCompute\t" + "timeTrans\t" + "timeServ\t\n");
		myWriterPSO.write("\n" + "WL\t" + "nodeID\t" + "worload\t" + "timeCompute\t" + "timeTrans\t" + "timeServ\t\n");

		for (int w = 1; w <= 5; w++) {

			myWriter.write("\n");
			myWriterPSO.write("\n");

			WL = 100 * w;

			System.out.println("\nWORKLOAD = " + WL);
			// System.out.println("\n\n >>> FIND BEST NODE");
			double lat = 0.5;
			double lng = 0.5;
			Node nNode = findBestNode(WL, topo, lat, lng); // find the best node

			// fix node is Man/0
			Node bestNode = m;
			// System.out.println("BestNode is: " + bestNode.getName());

			System.out.println(">>> ESTIMATE TARA");
			System.out.println("***********RANKING***********\n");

			// Setup Ranking

			// LogData logdt= new LogData(WL);

			HashMap<Integer, Double> result = getProportion(WL, bestNode); // estimate pi for node i

			// System.out.println("Proportion " + bestNode.getName() + "\n" + result );

			List<HashMap<Integer, Double>> allResult = getAllProportion(WL, bestNode);

			for (HashMap<Integer, Double> listP : allResult) {
				// System.out.println(listP);
			}

			// calc T_serve
			double t_ser = 0;
			double t_trans = 0;
			double t_compute = 0;

			Map<String, Double> time = new HashMap<String, Double>();

			for (Node node : topo) {
				nNode = m;
				// System.out.println("Time compute of node " + node.getId() + " = " +
				// Util.caclTimeCompute(node));
				t_trans = 0;
				t_compute = 0;
				t_compute += Util.caclTimeCompute(node);

				// TODO:
				// bestNode is optimal

				// System.out.println("Time trans of node " + node.getId() + " = " +
				// Util.caclTimeTrans(node,bestNode));
				t_trans += Util.caclTimeTrans(node, nNode);

				t_ser = t_compute + t_trans;

				// System.out.println("Time of node " + node.getId() + " = " + t_ser);

				time.put(node.getName(), t_ser);

				// System.out.println(node.getId() + "\t" + node.getWL() + "\t" +
				// Util.caclTimeCompute(node) + "\t" + Util.caclTimeTrans(node,bestNode));

				myWriter.write(WL + " \t " + node.getId() + " \t " + node.getWL() + " \t " + Util.caclTimeCompute(node)
						+ " \t " + Util.caclTimeTrans(node, nNode) + " \t " + t_ser + "\n");
				// myWriter.close();
			}

			Set<String> timeSet = time.keySet();
			for (String nodeName : timeSet) {
				if (time.get(nodeName) > 0) {
					// System.out.println("Time of node " + nodeName + " = " + time.get(nodeName));
				}
			}

			Map.Entry<String, Double> maxEntry = null;

			for (Map.Entry<String, Double> entry : time.entrySet()) {
				if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
					maxEntry = entry;
				}
			}
			// System.out.println("Time = " + maxEntry.getKey() + " is " +
			// maxEntry.getValue());

//			  System.out.println("***********PSO***********\n"); HashMap<Integer,Double>
//			  resultPSO = getPSO(WL, bestNode); // estimate pi for node i
//			  
//			  
//			  Set<Integer> nodeID = resultPSO.keySet(); for (Integer id:nodeID) { for (Node
//			  node:topo) { if (node.getId()==id) { node.setWL(resultPSO.get(id)); } } } //
//			  System.out.println("Proportion PSO " + bestNode.getName() + " " + resultPSO
//			  );
//			  

			// calc T_serve t_ser=0; t_trans=0; t_compute=0;

			Map<String, Double> timePSO = new HashMap<String, Double>();

			for (Node node : topo) {
				t_trans = 0;
				t_compute = 0;

				System.out.println("Time compute of node " + node.getId() + " = " + Util.caclTimeCompute(node));
				t_compute += Util.caclTimeCompute(node);

				System.out.println("Time trans of node " + node.getId() + " = " + Util.caclTimeTrans(node, bestNode));
				t_trans += Util.caclTimeTrans(node, bestNode);

				t_ser = t_compute + t_trans;
				System.out.println("Time of node " + node.getId() + " = " + t_ser);

				timePSO.put(node.getName(), t_ser);

				myWriterPSO
						.write(WL + " \t " + node.getId() + " \t " + node.getWL() + " \t " + Util.caclTimeCompute(node)
								+ " \t " + Util.caclTimeTrans(node, bestNode) + " \t " + t_ser + "\n");
			}

			Set<String> timeSetPSO = timePSO.keySet();
			for (String nodeName : timeSetPSO) {
				if (timePSO.get(nodeName) > 0) {
					System.out.println("Time of node " + nodeName + " = " + timePSO.get(nodeName));
				}
			}

			Map.Entry<String, Double> maxEntryPSO = null;

			for (Map.Entry<String, Double> entry : timePSO.entrySet()) {
				if (maxEntryPSO == null || entry.getValue().compareTo(maxEntryPSO.getValue()) > 0) {
					maxEntryPSO = entry;
				}
			}
			System.out.println("PSO Time = " + maxEntryPSO.getKey() + " is " + maxEntryPSO.getValue());

			System.out.println("***********COMPARE***********" + (maxEntryPSO.getValue() - maxEntry.getValue()));
			System.out.println("PSO - Ranking\n" + maxEntryPSO.getKey() + " is " + maxEntryPSO.getValue() + "\n"
					+ maxEntry.getKey() + " is " + maxEntry.getValue());

		}
		myWriter.close();
		myWriterPSO.close();

		;

		// test with WL

		// show level

		// find parent

		// show parent

		// BFS to show what?

	}

	private static void createRoutingTable(Node[] topo) {
		Node root = topo[0];
		for (Node node:root.getNodeLK()) {
			System.out.println(">>ADD DES: " + node.getName() + " ROUTE: DIRECT" + "	HOP	1");
			for (Node n1:node.getNodeLK()) {
				if(n1.getId()!=root.getId()) {
					System.out.println(">>ADD DES: " + n1.getName() + " ROUTE: " + node.getName() + "	HOP	2");
				}
			}
		} 
		
		for (Node node : topo) {
			
			
			
			for (Node nodec : topo) {
				if ((node.checkLK(nodec)) && (node.getId() != nodec.getId())) {
					

					if (nodec.getLvl() > node.getLvl()) { // set hopcount
						nodec.setLvl(node.getLvl() + 1);
						
						
						node.getNodeLK().add(nodec); // set neighbor
						
						if (nodec.getParent() == null) {
							node.getNodeChild().add(nodec); // add Child
							nodec.setParent(node); // add Parent - faking !!! when a child node connects to only
													// node-as-parents
						}

					}
				}
			}
		}

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
			System.out.println("Process node " + node.getId());

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

//	private static HashMap<Integer, Double> getPSO(double workload, Node bestNode) {
//		HashMap<Integer, Double> result = new HashMap<Integer, Double>();
//		// int num = bestNode.getNodeChild().size()+1;
//		int num = 6; // number of node in network
//		double[] p = new double[num];
//
//		int particles = Constants.particles;
//		int epchos = Constants.epchos;
//		int nnodes = num;
//		int workLoad = (int) workload;
//
//		int nworker = num;
//
//		double[] cWorkload = new double[nworker]; // workers
//		for (int i = 0; i < cWorkload.length; i++) {
//			cWorkload[i] = 0;
//		}
//
//		Vector currentWorkload = new Vector(cWorkload);
//
//		Swarm swarm = new Swarm(particles, epchos, nnodes, workLoad, currentWorkload, bestNode);
//
//		Map<Integer, Double> ratio = swarm.run("service-id-string");
//		// ` System.out.println(ratio.toString());
//
////		p[0]=bestNode.getRes()/bestNode.getGain();
////		result.put(bestNode.getId(), p[0]);
////		bestNode.setWL(p[0]*workload);
////		
////		int i=1;
////		for(Node n:bestNode.getNodeChild()) {
////			double k = Util.calcDistance(n, n.getParent());
////			p[i]=(n.getGain()*Constants.FIXNUM/k)/bestNode.getGain();
////			result.put(n.getId(), p[i]);
////			n.setWL(p[i]*workload);
////			i++;
////		}
//		result = (HashMap<Integer, Double>) ratio;
//
//		return result;
//
//	}

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

	private static void setupTopo(Node[] topo) {
		for (Node node : topo) {
			for (Node nodec : topo) {

				if ((node.checkLK(nodec)) && (node.getId() != nodec.getId())) {
					node.getNodeLK().add(nodec); // set neighbor

					if (nodec.getLvl() > node.getLvl()) { // set hopcount
						nodec.setLvl(node.getLvl() + 1);

						if (nodec.getParent() == null) {
							node.getNodeChild().add(nodec); // add Child
							nodec.setParent(node); // add Parent - faking !!! when a child node connects to only
													// node-as-parents
						}

//						
//						double k= Math.abs((node.getLat()-nodec.getLat())*(node.getLat()-nodec.getLat())+(node.getLng()-nodec.getLng())*(node.getLng()-nodec.getLng()));
//						k= 0-Math.sqrt(k);
//						node.setGain(node.getGain()+nodec.getGain()*Math.exp(k)); // calcGain
					}
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
