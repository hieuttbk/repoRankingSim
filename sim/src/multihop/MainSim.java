package multihop;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collector;

import PSOSim.PSOSwarm;
import PSOSim.PSOVector;
import multihop.Constants.TYPE;
import multihop.node.NodeBase;
import multihop.node.NodeRSU;
import multihop.node.NodeVehicle;
import multihop.request.RequestBase;
import multihop.request.RequestVehicle;
import multihop.util.AlgUtils;
import multihop.util.TopoUtils;

public class MainSim {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {

		/**
		 * ------------------------------- Prams-------------------------------
		 **/
		// case
		boolean DEBUG = true;
		boolean single;
		int hc = 2;

		/**
		 * --- 1.Create topo---
		 */

		// vehicle node
		int _m = 5,_n = 5;
		List<NodeVehicle> topo = new ArrayList<NodeVehicle>();
		topo = (List<NodeVehicle>) TopoUtils.createTopo(_m, _n, 10, Constants.TYPE.VEHICLE.ordinal()); // init topo Vehicle
		TopoUtils.updateTimeTopo(topo); // adding moving by time for Vehicle
		TopoUtils.setupTopo(topo); // create neighbor  
		// Parent node in overlap range RSU1 and RSU2
		
		// RSU node
		_m = 3;
		_n = 3;
		List<NodeRSU> topoRSU = new ArrayList<NodeRSU>();
		topoRSU = (List<NodeRSU>) TopoUtils.createTopo(_m, _n, 20, Constants.TYPE.RSU.ordinal()); // init topo
		TopoUtils.setupTopoRSU(topoRSU, topo); // create neighbor 

		/**
		 * --- 2. Create N requests --- id workload request node time_start=time_arrival
		 */
		Queue<RequestBase> reqPiority = new PriorityQueue<RequestBase>(); // store req by time and id
		reqPiority = TrafficUtils.createReqList(topo);

		// transfer queue to list (cz index in queue isn't right)
		List<RequestBase> req = new ArrayList<RequestBase>();
		while (reqPiority.size() != 0) {
			req.add(reqPiority.poll());
		}

		/**
		 * Loop ts: 0 -- ts_1 -- ts_1+TS
		 */
		int TS = Constants.TS;
		final int nTS = Constants.TSIM - 1;

//		FileWriter listCWL;
//		listCWL = new FileWriter("topoPSO_cwl.txt");

		FileWriter fListReq; // list requests in each timeslot
		fListReq = new FileWriter("listREQ.txt");
		fListReq.write("\nTS=" + TS + " | nTS=" + nTS);
		fListReq.write("\nTS\tListReqs" + "\n");
		int testCase = 0;

		int[] testCaseList = { 1 }; // {1, 2, 6, 7 }
		for (Integer test : testCaseList) {
			testCase = test;
			for (int h = 1; h <= 1; h++) { // hopcount = 1 or 2
				hc = h;
				int opts = h == 1 ? 1 : 2; // hc=1 -> 1opts; hc=2 -> 2 opts
				// opts =1;
				for (int s = 0; s < opts; s++) {
					single = s == 0 ? true : false; // single and multi opts

					// clear node
					for (NodeVehicle n : topo) {
						n.getqReq().clear();
						n.getDoneReq().clear();
						n.setcWL(0);
						n.setaWL(0);
		//				n.setpWL(0);
					}

					// single =false;

					String o = single == true ? "s" : "m";

					FileWriter myWriterPSO;
					myWriterPSO = new FileWriter("topoPSO-" + hc + "." + o + "." + testCase + ".txt");

					FileWriter myWriterPSOserv;
					myWriterPSOserv = new FileWriter("topoPSO_tserv-" + hc + "." + o + "." + testCase + ".txt");

					myWriterPSOserv.write("\n\n" + testCase + "." + hc + "." + single + "\n");
					myWriterPSO.write("\n" + testCase + "." + hc + "." + single + "\n");

					fListReq.write("\n" + testCase + "." + hc + "." + single + "\n");

					for (int t = 1; t <= nTS; t++) {
						System.out.println("\nts=" + t + " ----------------------------------------------------------");
						double ts = TS * t;

						/**
						 * --- 1. Create routing table---
						 */

						HashMap<Integer, List<RTable>> mapRTable = new HashMap<Integer, List<RTable>>();
						List<RTable> rtable = new ArrayList<RTable>();

						// 1.1 prepare request to node in: ts_k < start < ts_k+1
						debug("List REQs:\n ", DEBUG);
						List<NodeVehicle> listNodeReq = new ArrayList<NodeVehicle>(); // node having reqs in ts
						Queue<RequestBase> reqTS = new PriorityQueue<RequestBase>(); // reqTS having in ts

						for (RequestBase r : req) {
							double start = r.getTimeInit();
							if ((start < ts) && (start >= (ts - TS))) {
								listNodeReq.add(r.getSrcNode());
								reqTS.add(r);
								debug(r.getSrcNode().getName() + "." + start + "\t", DEBUG);
							}
						}

						// 1.2 updated create routing-table:rtable and routing-table-with-id:mapRTable
						// with requests
						for (RequestBase r : reqTS) {
							List<RTable> rtableREQ = new ArrayList<RTable>(); // rtable of a request
							int reqId = r.getId();
							NodeVehicle reqNode = r.getSrcNode();
							double WL = r.getWL();
							// rtableREQ = createRoutingTable(topo, rtableREQ, r, listNodeReq, hc, single,
							// t);
							rtableREQ = TopoUtils.createRoutingTable(topo, rtableREQ, r, listNodeReq, hc, single, t);
							rtable.addAll(rtableREQ); // merge all reqs
							mapRTable.put(reqId, rtableREQ); // merge reqs with id
						}

						// TODO: create routing-table for RSU

						/**
						 * --- 2. Run PSO ---
						 */

						// if having req in queue
						if (listNodeReq.size() != 0) {
							// log list_reqs
							fListReq.write(ts + "\t");
							for (RequestBase r : reqTS) {
								fListReq.write(r.getId() + "\t");
							}
							fListReq.write("\n");
							System.out.println("\n***********PSO Running***********\n");

							//HashMap<Integer, Double> resultPSO = getPSO(rtable, mapRTable, testCase, ts);
							
							HashMap<Integer, Double> resultPSO = AlgUtils.getPSO(rtable, mapRTable, testCase, ts);

							Set<Integer> rID = resultPSO.keySet(); // result pso
							for (Integer id : rID) {
								rtable.get(id).setRatio(resultPSO.get(id));
							}

							/**
							 * --- 3. Logging ---
							 */
							// 3.1 t_ser based PSO in rtable
							for (RTable r : rtable) {
								double compute = 0;
								double trans = 0;
								double workLoad = r.getReq().getWL();
								double subWL = r.getRatio() * workLoad; // new WL
								double totalWL = subWL + r.getcWL(); // adding cWL
								compute = totalWL / r.getResource(); // totalTime

								if (testCase != 2) {
									// calc t_process for all paths to node ~ including t_wait
									for (RTable r2 : rtable) {
										if (r2.getDes().equals(r.getDes()) && (r2.getId() != r.getId()
												|| (r2.getReq().getId() != r.getReq().getId()))) {
											// adding route 2hop-2path
											compute += r2.getRatio() * r2.getReq().getWL() / r2.getResource();
											subWL += r2.getRatio() * r2.getReq().getWL(); // adding newWL route2

										}
									}
								}

								trans = (r.getRatio() * workLoad / Constants.BW) * r.getHop();

								if (r.getId() == 0) {
									trans = 0;
								}
								;

								r.setTimeCompute(compute);
								r.setTimeTrans(trans);

								double ser = compute + trans;
								r.setTimeSer(ser);

							} // END 3.1: LOG TIME IN RTABLE

						}

						/**
						 * --- 4. Process CWL and queue in node ---
						 */
						// 4.1 calc assigned workload in node (all cWL) and adding queue
						System.out.println("\nCALC assigned new workload and ADD new reqs to queue");
						// listCWL.write("a");
						for (NodeVehicle n : topo) {
							double aWL = 0; // all assigned workload as new-workload
							boolean check = false;
							for (RTable r : rtable) {
								double move_data = 0;
								if (r.getDes().equals(n.getName())) {
									aWL += r.getRatio() * r.getReq().getWL();
									double t_process = r.getRatio() * r.getReq().getWL() / r.getResource();

									if (r.getDes().equals(r.getReq().getSrcNode().getName())) {
										move_data = aWL - Constants.RES[Constants.TYPE.VEHICLE.ordinal()];
										if (move_data > 0) {
											t_process = 1;
											// listCWL.write("\n" + t + "\t" + n.getId() + "\t" + move_data);
											aWL = Constants.RES[Constants.TYPE.VEHICLE.ordinal()];
										} else {
											move_data = 0;
										}
									}

									// adding queue
									double start = r.getTimeTrans() + (t - 1) * TS; // the arrival task to Node, PSO at
																					// ts
									// double start = r.getTimeTrans()+(t)*TS;
									NodeVehicle srcNode = r.getReq().getSrcNode();
									RequestBase rq = r.getReq();

//									n.getqReq().add(new RequestPSO(r.getReq().getId(), r.getReq().getWL(), n.getId(),
//											r.getRoute(), start, t_process, false, start, start + t_process, srcNode,
//											r.getRatio(), r.getTimeTrans(), r.getTimeSer(), move_data));

									RequestVehicle rv = new RequestVehicle(rq.getId(), rq.getWL(), rq.getSrcNode(),
											rq.getTimeInit(), rq.isDone(), n.getId(), r.getRoute(), start, t_process,
											r.getRatio(), r.getTimeTrans(), start, (start + t_process), r.getTimeSer(),
											move_data);

									n.getqReq().add(rv);

									check = true;
								}
							}

							n.setaWL(n.getaWL() + aWL);
						}

						// 4.2 calc and update queue
						System.out.println("\nUPDATE QUEUE");
						for (NodeVehicle n : topo) {
							boolean check = true;
							while (check && (n.getqReq().peek() != null)) {
								RequestVehicle rv = (RequestVehicle) n.getqReq().peek();
								double start1 = rv.getStart();
								double end1 = rv.getEnd();
								// System.out.print("Node: " + n.getName());
								// System.out.println(" REQ: " + start1 + " -> " + end1);
								check = false;
								if (end1 < ts) {
									n.getqReq().peek().setDone(true);
									n.getDoneReq().add(n.getqReq().peek()); // adding to done req
									// System.out.println("doneREQ: " + n.getqReq().peek().getStart() + " -> " +
									// end1);
									n.getqReq().remove(); // req is done, removing
									RequestVehicle nextReq = (RequestVehicle) n.getqReq().peek(); // update next request
																									// if data sent
									if (nextReq != null) {
										if (end1 > nextReq.getStart()) {
											// System.out.println("update next req start at: " + end1);
											((RequestVehicle) n.getqReq().peek()).setStart(end1);
											((RequestVehicle) n.getqReq().peek()).setEnd(
													end1 + ((RequestVehicle) n.getqReq().peek()).getTimeProcess());
											check = true;
										} else if (end1 < nextReq.getStart() && nextReq.getStart() < ts) {
											check = true;
										}
									}

								}
							}

						}

						// 4.2 caclc cWL
						System.out.println("\n----- Current WL: ");
						// listCWL.write("p");
						for (NodeVehicle n : topo) {
							double pWL = 0; // processed workload
							// double aWL = 0; // all assigned worload
							if ((n.getqReq().size() != 0) || n.getDoneReq().size() != 0) { // node in processing
//								System.out.println("Node " + n.getName());
//									n.getDoneReq().forEach((d) -> {
//										System.out.println("Done req: " + d.getStart() + " " + d.getEnd());
//									});
//									n.getqReq().forEach((q) -> System.out.println("Queue req: " + q.getStart() + " " + q.getEnd()));

								for (RequestBase d : n.getDoneReq()) {
									pWL += (((RequestVehicle) d).getEnd() - ((RequestVehicle) d).getStart())
											* n.getRes();
								}

								if (n.getqReq().peek() != null) {
									double lastStart = ((RequestVehicle) n.getqReq().peek()).getStart();
									if (lastStart < ts) {
										pWL += (ts - lastStart) * n.getRes();
									}
								}
//								System.out.print("Process WL: " + pWL + " / " + n.getaWL());
								n.setcWL((n.getaWL() - pWL) < 0 ? 0 : (n.getaWL() - pWL));
//								System.out.println("\tcWL: " + n.getcWL());
							}
							// listCWL.write(pWL + "\t");

//						if (listNodeReq.contains(n)) {				
//							listCWL.write("\n" + t + "\t" + n.getId() + "\t" + n.getcWL());
//							if (n.getcWL()>0) {n.setcWL(0);}
//						}

						}
//						listCWL.write("\n");
						// log

						// 4.3 moving to cloud

						HashMap<Integer, List<RTable>> mapRTableRSU = new HashMap<Integer, List<RTable>>();
						List<RTable> rtableRSU = new ArrayList<RTable>();

						// 1.1 prepare request to node RSU base: moving-data
						debug("List REQs:\n ", DEBUG);
						List<NodeRSU> listNodeReqRSU = new ArrayList<NodeRSU>(); // node having reqs in ts
						Queue<RequestVehicle> reqTSRSU = new PriorityQueue<RequestVehicle>(); // reqTS having in ts

						// list node moving data and the requestID
						for (NodeRSU n : topoRSU) {
							for (NodeVehicle n1 : n.getNodeChild().get(t)) {
								// n1.getqReq()
							}
						}

						for (RequestBase r : req) {
							double start = r.getTimeInit();
							if ((start < ts) && (start >= (ts - TS))) {
								listNodeReq.add(r.getSrcNode());
								reqTS.add(r);
								debug(r.getSrcNode().getName() + "." + start + "\t", DEBUG);
							}
						}

						// 1.2 updated create routing-table:rtable and routing-table-with-id:mapRTable
						// with requests
						for (RequestBase r : reqTS) {
							List<RTable> rtableREQ = new ArrayList<RTable>(); // rtable of a request
							int reqId = r.getId();
							NodeVehicle reqNode = r.getSrcNode();
							double WL = r.getWL();
							// rtableREQ = createRoutingTable(topo, rtableREQ, r, listNodeReq, hc, single,
							// t);
							rtableREQ = TopoUtils.createRoutingTable(topo, rtableREQ, r, listNodeReq, hc, single, t);
							rtable.addAll(rtableREQ); // merge all reqs
							mapRTable.put(reqId, rtableREQ); // merge reqs with id
						}

					} // endts
					System.out.println("\n----- DONE REQ ------");

					// log node done
//					for (Node n : topo) {
//						if (n.getqReq().size() == 0) {
//							if (n.getDoneReq().size() != 0)
//								System.out.println("Node: " + n.getName());
//							n.getDoneReq().forEach((d) -> System.out
//									.println(d.getId() + "_" + d.getRoute() + ": " + d.getStart() + "\t" + d.getEnd()));
//						}
//					}

					myWriterPSO.write("\nReqID\t" + "a(SrcNode)\t" + "i(DesNode)\t" + "k(Path)\t" + "p(Ratio)\t"
							+ "dtTrans\t" + "tArrival\t" + "start\t" + "end\t" + "timeSer(PSO)\t" + "t_wait\t"
							+ "t_proc\t" + "t_serv\t" + "moved_data" + "\n");

					for( 
					RequestBase r : req) {
						// double endM=0;
						for (NodeVehicle n : topo) {
							for (RequestBase d : n.getDoneReq()) {
								if (d.getId() == r.getId()) {
//									System.out.println("REQ: " + r.getId() + "\n" + "->" + n.getName() + "_"
//											+ d.getRoute() + ": " + d.getStart() + "\t" + d.getEnd());
									RequestVehicle dv = (RequestVehicle) d;
									double t_wait = dv.getStart() - dv.getTimeArrival();
									double t_proc = dv.getEnd() - dv.getStart();
									double t_serv = dv.getTimeTrans() + t_wait + t_proc;
									myWriterPSO.write(r.getId() + "\t" + dv.getSrcNode().getName() + "\t" + n.getName()
											+ "\t" + dv.getRoute() + "\t" + dv.getRatio() + "\t" + dv.getTimeTrans()
											+ "\t" + dv.getTimeArrival() + "\t" + dv.getStart() + "\t" + dv.getEnd()
											+ "\t" + dv.getTimeSer() + "\t" + t_wait + "\t" + t_proc + "\t" + t_serv
											+ "\t" + dv.getMovedData() + "\n");
									// endM=endM>d.getEnd()?endM:d.getEnd();

								}
							}
						}
						// myWriterPSO.write(endM + "\n");
					}

					// calc avg
					double wait = 0;
					int count = 0;
					for (RequestBase r : req) {
						double endM = 0;
						for (NodeVehicle n : topo) {
							for (RequestBase d : n.getDoneReq()) {
								if (d.getId() == r.getId()) {
									endM = endM > ((RequestVehicle) d).getEnd() ? endM : ((RequestVehicle) d).getEnd();
									wait += (((RequestVehicle) d).getStart() - ((RequestVehicle) d).getTimeArrival());
									count++;
								}
							}
						}

						//endM -= Math.floor(((RequestVehicle) r).getTimeArrival());
						//endM -= Math.floor(((RequestVehicle) r).getTimeArrival());
						myWriterPSOserv.write("\n" + r.getId() + "\t" + endM);
					}
//					System.out.println("AVG: " + wait / count);

					myWriterPSOserv.close();
					myWriterPSO.close();
				} // end for each case

			}
		}
		System.out.println("----FINISH-----");
		fListReq.close();
//		listCWL.close();
//		myWriterPSOserv.close();
//		myWriterPSO.close();
	}

//	private static void updateTopo(List<Node> topo, Node req) {
//		topo.add(req);
//		for (Node node : topo) {
//			if (node.checkLK(req) && (node.getId() != req.getId())) {
//				node.getNodeLK().add(req);
//				req.getNodeLK().add(node);
//			}
//		}
//	}

	/**
	 * create topo size m*n
	 * 
	 * @param m:     longs
	 * @param n:     width
	 * @param space: distance node to node
	 * @param type:  0 - vehicle, 1- RSU/MN, 2 - Server/IN
	 */
//	private static List<NodeBase> createTopo(int m, int n, int space, int type) {
//		NodeBase node = null;
//		List<NodeBase> topo = new ArrayList<NodeBase>();
//		int id = 0;
//		for (int i = 0; i < m; i++) { // y
//			for (int j = 0; j < n; j++) { // x
//
//				switch (type) {
//				case 0:
//					node = new NodeVehicle(id, "V" + String.valueOf(id), j * space, i * space, Constants.RANGE[type],
//							Constants.RES[type]);
//					break;
//				case 1:
//					node = new NodeRSU(id, "R" + String.valueOf(id), j * space, i * space, Constants.RANGE[type],
//							Constants.RES[type]);
//					break;
//				case 2:
//					node = new NodeRSU(id, "S" + String.valueOf(id), j * space, i * space, Constants.RANGE[type],
//							Constants.RES[type]);
//					break;
//				default:
//					System.out.println("ERR-TYPE of nodes");
//					break;
//				}
//				topo.add(id, node);
//				topo.get(id).setRes(Constants.RES[type]);
//				topo.get(id).setRange(Constants.RANGE[type]);
//				id++;
//			}
//		}
//
//		return topo;
//	}

//	private static List<Node> findNeighbour(List<Node> topo, Node root) {
//		List<Node> neigbourNode = new ArrayList<Node>();
//		int id = 1;
//
//		for (Node n1 : root.getNodeLK()) {
//			n1.setLvl(1);
//			neigbourNode.add(n1);
//			id++;
////			System.out.println(">>\tADD" + id + " " + n1.getId());
//		}
//
//		for (Node n1 : root.getNodeLK()) {
//			for (Node n2 : n1.getNodeLK()) {
//				int npath = 2;
//				if (n2.getId() != root.getId() && (n2.getLvl() > n1.getLvl()) && (!neigbourNode.contains(n2))) {
//					n2.setLvl(2);
//					neigbourNode.add(n2);
//					// System.out.println(">>\tADD" + id + " " + n2.getId());
//
//					id++;
//				}
//			}
//		}
//
//		for (Node t : topo) {
//			t.setLvl(Constants.MAXINT);
//		}
//		return neigbourNode;
//
//	}

	/**
	 * @param i - timeslot
	 */
//	private static List<RTable> createRoutingTable(List<NodeVehicle> topo, List<RTable> rtable, RequestBase req,
//			List<NodeVehicle> listNodeReq, int MAX, boolean single, int i) {
//		// adding root of req: reqID as name and
//		i = i - 1;
//		NodeVehicle root = req.getSrcNode();
//		rtable.add(0, new RTable(0, root.getName(), root.getName(), 0, root.getRes(), req));
//
//		int id = 1;
//
//		for (NodeVehicle n1 : root.getNodeLK().get(i)) {
//			if (root.getNodeLK().get(i + 1).contains(n1)) { // n1 is still neigbours in next ts
//				// System.out.println("Node: " + root.getId() + "neigh: " + n1.getId() + "at: "
//				// + i);
//				n1.setLvl(1);
//				if (!listNodeReq.contains(n1)) {
//					rtable.add(id, new RTable(id, n1.getName(), root.getName(), 1, n1.getRes(), req));
//					rtable.get(id).setNpath(1);
//					id++;
//				}
//			}
//
//			// System.out.println(">>\tADD DES: " + n1.getName() + " ROUTE: " +
//			// root.getName() + " | " + 1);
//
//		}
//
////		for (Node n1 : root.getNodeLK()) {
////			// System.out.println("\n>NODE DIRECT: " + n1.getName() + " ROUTE: " +
////			// root.getName());
////			for (Node n2 : n1.getNodeLK()) {
////				int npath = 2;
////				if (n2.getId() != root.getId() && (n2.getLvl() > n1.getLvl()) && (!listNodeReq.contains(n2))) {
////					n2.setLvl(2);
////					// adding vNode
////					n2.getvNode().add(n1);
////					rtable.add(id, new RTable(id, n2.getName(), n1.getName(), 2, n1.getRes(), req));
////					rtable.get(id).setNpath(npath);
////					// System.out.println(">>\tADD DES: " + n2.getName() + " ROUTE: " + n1.getName()
////					// + " | " + npath);
////
////					id++;
////
////				}
////			}
////		}
//
////		for (Node n2 : topo) {
////			if(n2.getLvl()==2) {
////				for (Node n3:n2.getNodeLK()) {
////					int npath=3;
////					if (n3.getId() != root.getId() && (n3.getLvl() > n2.getLvl()) && (!listNodeReq.contains(n3))) {
////					n3.setLvl(3);
////					rtable.add(id, new RTable(id, n3.getName(), n2.getName(), 2, n3.getRes(), req));
////					rtable.get(id).setNpath(npath);
////					
////					 System.out.println(">>\tADD DES: " + n3.getName() + " ROUTE: " + n2.getName()
////					 + " | " + npath);
////					
////					id++;
////					}
////				}
////			}
////		}
//
//		// Caclc with >=2 hops
//		for (int maxHop = 2; maxHop <= MAX; maxHop++) {
//			for (NodeVehicle n : topo) {
//				if (n.getLvl() == (maxHop - 1)) {
//					for (NodeVehicle n1 : n.getNodeLK().get(i)) {
//						int npath = maxHop;
//						if (n1.getId() != root.getId() && (n1.getLvl() > n.getLvl()) && (!listNodeReq.contains(n1))) {
//							n1.setLvl(maxHop);
//							rtable.add(id, new RTable(id, n1.getName(), n.getName(), maxHop, n1.getRes(), req));
//							rtable.get(id).setNpath(npath);
//
//							// System.out.println(">>\tADD DES: " + n1.getName() + " ROUTE: " + n.getName()
//							// + " | " + npath);
//
//							id++;
//						}
//
//					}
//
//				}
//			}
//		}
//
//		// update cWL of node to routing table
//		for (RTable r : rtable) {
//			for (NodeVehicle t : topo) {
//				if (t.getName().equals(r.getDes())) {
//					r.setcWL(t.getcWL());
//					// System.out.println("Adding r: " + r.toString() + " " +r.getcWL());
//				}
//			}
//		}
//
////		System.out.println("\ncreated RoutingTable: ");
////		rtable.forEach((e) -> {
////			System.out.println(">" + " " + e.toString());
////		});
//
//		if (single) {
//			// delete multi-route
//			List<String> desNode = new ArrayList<String>();
//			List<RTable> rmNode = new ArrayList<RTable>();
//			List<RTable> rtableClone = new ArrayList<RTable>();
//			rtableClone = rtable;
//
//			for (RTable r : rtableClone) {
//				if (!desNode.contains(r.getDes())) { // new desNode
//					desNode.add(r.getDes());
//
//				} else {
//					rmNode.add(r);
//				}
//			}
//			for (RTable rm : rmNode) {
//				rtable.remove(rm);
//			}
//			// end delete
//		}
//
//		return rtable;
//
//	}

	/**
	 * get rations for networks using PSO config PSO prams in the method
	 * 
	 * @param workload  is total workload need assigned
	 * @param bestNode  is the data owner node, others connect to it
	 * @param rtable    is full info of network, can cacl time_trans, time_compute
	 *                  using prams in the table
	 * @param mapRTable
	 * @param ts
	 * @return ration p as HashMap<id,value>
	 */
	private static HashMap<Integer, Double> getPSO(List<RTable> rtable, HashMap<Integer, List<RTable>> mapRTable,
			int testCase, double ts) {

		HashMap<Integer, Double> result = new HashMap<Integer, Double>();
		// int num = bestNode.getNodeChild().size()+1;
		// int num = 6; // number of node in network
		int num = rtable.size();
		double[] p = new double[num];

		int particles = Constants.particles;
		int epchos = Constants.epchos;
		int nnodes = num; // number of nodes/dimenssion

		int nworker = num;

		double[] cWorkload = new double[nworker]; // workers
		for (int i = 0; i < cWorkload.length; i++) {
			cWorkload[i] = 0;
		}

		PSOVector currentWorkload = new PSOVector(cWorkload);

		PSOSwarm swarm = new PSOSwarm(particles, epchos, nnodes, currentWorkload, rtable, mapRTable, testCase);
		// LogPSO log1 = LogPSO.getInstance();
		// log1.log("ts: " + ts + "\n");
		System.out.println("ts = " + ts);
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

	/**
	 * @param topo = list node
	 * @return topo = list node adding neighbour node/ nodelk
	 */

//	private static void setupTopo(List<Node> topo) {
//
//		for (Node node : topo) {
//			for (int i = 0; i < Constants.TSIM; i++) {
//				Vector<Node> neighNode = new Vector<Node>();
//				// System.out.println("\nNode " + node.getName() + " at i: " + i + " add nei:
//				// ");
//
//				for (Node nodec : topo) {
//					if ((node.checkLK(nodec, i)) && (node.getId() != nodec.getId())) {
//						// node.getNodeLK().add(nodec); // set neighbor
//						neighNode.add(nodec);
//						// System.out.print(nodec.getName() + " ");
//					}
//				}
//				node.getNodeLK().add(i, neighNode);
//			}
//		}
//
//	}
//
//	private static void setupTopoRSU(List<Node> topoRSU, List<Node> topo) {
//		for (Node node : topoRSU) {
//			// adding nodeLK as child-node of RSU
//			for (int i = 0; i < Constants.TSIM; i++) {
//				Vector<Node> neighNode = new Vector<Node>();
//				for (Node nodec : topo) { // for in topo-vehicle
//					if (node.checkLKRSU(nodec, i)) { // just check range, don't check id
//						neighNode.add(nodec);
//					}
//				}
//				node.getNodeLK().add(i, neighNode);
//			}
//
//			// adding nodeLKRSU as RSU-neigbour-node
//			// Vector<Node> neighNodeRSU = new Vector<Node>();
//			for (Node nodec : topoRSU) {
//				if ((node.checkLK(nodec)) && (node.getId() != nodec.getId())) {
//					node.getNodeLKRSU().add(nodec);
//				}
//			}
//		}
//
//	}

//	private static int findMaxLvl(Node[] topo) {
//		int max = 0;
//		for (Node node : topo) {
//			if (node.getLvl() > max)
//				max = node.getLvl();
//		}
//		return max;
//	}
	private static void debug(String s, boolean mode) {
		if (mode)
			System.out.println(s);
	}

}
