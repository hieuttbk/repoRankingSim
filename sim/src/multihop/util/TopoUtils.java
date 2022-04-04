package multihop.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import multihop.Constants;
import multihop.RTable;
import multihop.node.NodeBase;
import multihop.node.NodeRSU;
import multihop.node.NodeVehicle;
import multihop.request.RequestBase;

public class TopoUtils {

	/**
	 * create topo size m*n
	 * 
	 * @param m:     longs
	 * @param n:     width
	 * @param space: distance node to node
	 * @param type:  0 - vehicle, 1- RSU/MN, 2 - Server/IN
	 */
	public static List<? extends NodeBase> createTopo(int m, int n, int space, int type) {

		NodeBase node = null;
		List<NodeBase> topo = new ArrayList<NodeBase>();
		int id = 0;
		for (int i = 0; i < m; i++) { // y
			for (int j = 0; j < n; j++) { // x
				switch (type) {
				case 0:
					node = new NodeVehicle(id, "V" + String.valueOf(id), j * space, i * space, Constants.RANGE[type],
							Constants.RES[type]);
					break;
				case 1:
					node = new NodeRSU(id, "R" + String.valueOf(id), j * space, i * space, Constants.RANGE[type],
							Constants.RES[type]);
					break;
				case 2:
					// TODO:
				default:
					System.out.println("ERR-TYPE of nodes");
					break;
				}

				topo.add(id, node);
				id++;
			}
		}

		return topo;
	}

	public static void updateTimeTopo(List<NodeVehicle> topo) {
		// moving simulation for vehicle
		Random generator = new Random(); // gen random number
		double pi = Math.PI;
		final double[] c_velo = { 2, 2.5, 3, 0 };
		final double[] c_ts = { 5, 4, 3.3, Constants.MAXINT };
		final double[] c_phi = { 0, pi / 2, pi / 2, pi };

		for (NodeVehicle n : topo) {
			double[] x = new double[Constants.TSIM];
			double[] y = new double[Constants.TSIM];
			double[] v = new double[Constants.TSIM];
			double[] phi = new double[Constants.TSIM];
			double[] sign = new double[Constants.TSIM];

			x[0] = n.getLat();
			y[0] = n.getLng();
			int id = generator.nextInt(3);

			// id = 3; // move with v =0 ; fix

			v[0] = c_velo[id];
			double cts = c_ts[id];
			phi[0] = 0;
			sign[0] = 0;

			for (int i = 1; i < Constants.TSIM; i++) {
				sign[i] = sign[i - 1] + 1;

				x[i] = x[i - 1] + v[i - 1] * 1 * Math.cos(phi[i - 1]);
				y[i] = y[i - 1] + v[i - 1] * 1 * Math.sin(phi[i - 1]);

				double dt = sign[i] - cts;

				if (dt > 0) { // can be change
					int change = generator.nextInt(3); // random 0 1 2 3
					if (change < 2) { // change
						if ((phi[i - 1] == 0) || (phi[i - 1] == pi)) {
							int a = (int) x[i - 1];
							int b = (int) x[i];
							if ((a / 10) != (b / 10)) {
								phi[i] = phi[i - 1] + c_phi[change + 1];
								sign[i] = 0;
								if (a % 10 == 0) {
									x[i] = a;
								} else {
									x[i] = (a / 10 + 1) * 10;
								}
								y[i] = y[i - 1] + x[i] - a;

							}

						} else {
							int a = (int) y[i - 1];
							int b = (int) y[i];
							if ((a / 10) != (b / 10)) {
								phi[i] = phi[i - 1] + c_phi[change + 1];
								sign[i] = 0;
								if (a % 10 == 0) {
									y[i] = a;
								} else {
									y[i] = (a / 10 + 1) * 10;
								}

								x[i] = x[i - 1] + y[i] - a;
							}

						}

					}
				}

				v[i] = v[i - 1];
			}
			n.setX(x);
			n.setY(y);
			n.setSign(sign);
			n.setPhi(phi);
		}
	}

	/**
	 * @param topo = list node
	 * @return topo = list node adding neighbour node/ nodelk
	 */

	public static void setupTopo(List<NodeVehicle> topo) {

		for (NodeVehicle node : topo) {
			for (int i = 0; i < Constants.TSIM; i++) {
				Vector<NodeVehicle> neighNode = new Vector<NodeVehicle>();
				// System.out.println("\nNode " + node.getName() + " at i: " + i + " add nei:
				// ");

				for (NodeVehicle nodec : topo) {
					if ((node.checkLK(nodec, i)) && (node.getId() != nodec.getId())) {
						// node.getNodeLK().add(nodec); // set neighbor
						neighNode.add(nodec);
						// System.out.print(nodec.getName() + " ");
					}
				}
				node.getNodeLK().add(i, neighNode);
			}
		}

	}

	public static void setupTopoRSU(List<NodeRSU> topoRSU, List<NodeVehicle> topo) {
		for (NodeRSU node : topoRSU) {
			// adding nodeLK as child-node of RSU
			for (int i = 0; i < Constants.TSIM; i++) {
				Vector<NodeVehicle> neighNode = new Vector<NodeVehicle>();
				for (NodeVehicle nodec : topo) { // for in topo-vehicle
					if (node.checkLK(nodec, i)) { // just check range, don't check id
						neighNode.add(nodec);
					}
				}
				node.getNodeChild().add(i, neighNode);
			}

			// adding nodeLKRSU as RSU-neigbour-node
			// Vector<Node> neighNodeRSU = new Vector<Node>();
			for (NodeRSU nodec : topoRSU) {
				if ((node.checkLK(nodec)) && (node.getId() != nodec.getId())) {
					node.getNodeNeigbour().add(nodec);
				}
			}
		}

	}

	/**
	 * @param i - timeslot
	 */
	public static List<RTable> createRoutingTable(List<NodeVehicle> topo, List<RTable> rtable, RequestBase req,
			List<NodeVehicle> listNodeReq, int MAX, boolean single, int i) {
		// adding root of req: reqID as name and
		i = i - 1;
		NodeVehicle root = req.getSrcNode();
		rtable.add(0, new RTable(0, root.getName(), root.getName(), 0, root.getRes(), req));

		int id = 1;

		for (NodeVehicle n1 : root.getNodeLK().get(i)) {
			if (root.getNodeLK().get(i + 1).contains(n1)) { // n1 is still neigbours in next ts

				n1.setLvl(1);
				if (!listNodeReq.contains(n1)) {
					rtable.add(id, new RTable(id, n1.getName(), root.getName(), 1, n1.getRes(), req));
					rtable.get(id).setNpath(1);
					id++;
				}
			}

		}

		// Caclc with >=2 hops
		for (int maxHop = 2; maxHop <= MAX; maxHop++) {
			for (NodeVehicle n : topo) {
				if (n.getLvl() == (maxHop - 1)) {
					for (NodeVehicle n1 : n.getNodeLK().get(i)) {
						int npath = maxHop;
						if (n1.getId() != root.getId() && (n1.getLvl() > n.getLvl()) && (!listNodeReq.contains(n1))) {
							n1.setLvl(maxHop);
							rtable.add(id, new RTable(id, n1.getName(), n.getName(), maxHop, n1.getRes(), req));
							rtable.get(id).setNpath(npath);
							id++;
						}

					}

				}
			}
		}

		// update cWL of node to routing table
		for (RTable r : rtable) {
			for (NodeVehicle t : topo) {
				if (t.getName().equals(r.getDes())) {
					r.setcWL(t.getcWL());
					// System.out.println("Adding r: " + r.toString() + " " +r.getcWL());
				}
			}
		}

		if (single) {
			// delete multi-route
			List<String> desNode = new ArrayList<String>();
			List<RTable> rmNode = new ArrayList<RTable>();
			List<RTable> rtableClone = new ArrayList<RTable>();
			rtableClone = rtable;

			for (RTable r : rtableClone) {
				if (!desNode.contains(r.getDes())) { // new desNode
					desNode.add(r.getDes());

				} else {
					rmNode.add(r);
				}
			}
			for (RTable rm : rmNode) {
				rtable.remove(rm);
			}
			// end delete
		}

		return rtable;

	}

	public TopoUtils() {
		// TODO Auto-generated constructor stub
	}

}
