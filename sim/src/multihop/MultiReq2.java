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
import java.util.stream.Collector;

import PSOSim.PSOSwarm;
import PSOSim.PSOVector;

public class MultiReq2 {

	public static void main(String[] args) throws IOException {

		/**
		 * ------------------------------- Prams-------------------------------
		 **/
		//case
		boolean single;
		
		// topo
		int _m = 5;
		int _n = 5;
		int len = _m * _n;

		// logging
		FileWriter myWriterPSO;
		FileWriter myWriterPSOserv;
		FileWriter myWriterPSOtraff;
		myWriterPSO = new FileWriter("topoPSO"+Constants.testcase+".txt");
		myWriterPSOserv = new FileWriter("topoPSO_tserv"+ Constants.testcase +".txt");
		myWriterPSOtraff = new FileWriter("topoPSO_traff.txt");

		// request
		int WL = (int) (len * Constants.lambda * Constants.tlambda); // 20reqs/s/node
		int numREQ = Constants.NUM_REQ;
		Queue<RequestPSO> req = new PriorityQueue<RequestPSO>();

		Random generator = new Random(); // random number REQ
		// int id = generator.nextInt(24);

		for (int hc = Constants.MAXHOP; hc <= Constants.MAXHOP; hc++) { // for hopcount
			// logging
//			myWriterPSOtraff.write("HopCount = " + hc + " | WL = " + WL + " | t_reqs= " + Constants.tlambda + " | lambda= "
//					+ Constants.lambda);

//			myWriterPSO.write("\n" + "WL\t" + "reqID\t" + "desNode\t" + "path\t" + "ration\t" + "timeTrans"
//					+ "timeServ\t" + "\n");

			/**
			 * --- 1.Create topo m*n ---
			 */
			List<Node> topo = new ArrayList<Node>();
			topo = createTopo(_m, _n);
			setupTopo(topo);

			/**
			 * --- 2. Create N requests --- id workload request node time_start=time_arrival
			 */
//		//	int[] fixedNode = new int[1000];
//			int[] fixedNode = Constants.node;
//		//	int[] fixedNode = { 4, 1, 22, 19, 3, 1, 9, 20, 11, 5 };
//			double []dT = Constants.DT;
//			int []data = {50,100,150,100,150,50,100,50,150,100};
//			myWriterPSOtraff.write("\n List REQ: \n" + "id\t" + "srcNode\t" + "timeArrival\t" + "workLoad\t"
//			 + "\n");
//			double reqTime = 0;
//			for (int i = 1; i <= Constants.NUM_REQ; i++) {
//				int idNode = fixedNode[i-1];
//				// fixed
//				//idNode = fixedNode[i - 1];
//				int wl= data[(i-1)%10];
//				int idReq = i;
//				req.add(new RequestPSO(idReq, wl, topo.get(idNode), reqTime, reqTime));
//
//				// log list-req
//				myWriterPSOtraff.write(i + "\t" + idNode + "\t" +reqTime + "\t" + wl + "\n");
//
//				reqTime += dT[i-1];
//				//fixedNode[i-1]=idNode;
//			}
			
			// gen REQ 1/3
			int []data= {24,30,60};
			//int []fixedNode = {9,15};
			//int []fixedNode = {1,4,7,15,18,20};
			int []fixedNode = {1,3,5,7,9,12,14,16,18,19,21,23};
			double []dT = Constants.DT;
			myWriterPSOtraff.write("\n List REQ: \n" + "id\t" + "srcNode\t" + "timeArrival\t" + "workLoad\t"
					 + "\n");
			double reqTime = 0;
			int idReq=1;
			for (int n=0; n<fixedNode.length;n++) {
				//reqTime=Constants.ST[n];
				int idNode = fixedNode[n];
				for(int i=1;i<=dT.length;i++) {
					int wl= data[2];
					req.add(new RequestPSO(idReq, wl, topo.get(idNode), reqTime, reqTime));
					myWriterPSOtraff.write(idReq + "\t" + idNode + "\t" +reqTime + "\t" + wl + "\n");
					reqTime += 1;
					idReq++;
				}
			}
			
			
			
			//System.out.println(Arrays.toString(fixedNode));
			// req.add(new RequestPSO(1,500,topo.get(0),0.5));
			// req.add(new RequestPSO(2,500,topo.get(0),2.5));

			/**
			 * Loop ts: 0 -- ts_1 -- ts_1+TS
			 */
			int TS = Constants.TS;
			final int nTS = Constants.TSIM;
			myWriterPSOtraff.write("\nTS=" + TS + " | nTS=" + nTS);
			myWriterPSOtraff.write("\nTS\tListReqs" + "\n");

			for(int i=0;i<2;i++) {
				single = i==0?true:false;
				myWriterPSOserv.write("\n\nsingle = " + single + "\n");
				for (int t = 1; t <= nTS; t++) {
					System.out.println("\nts=" + t + " ----------------------------------------------------------");
					double ts = TS * t;

					/**
					 * --- 1. Create routing table---
					 */
					HashMap<Integer, List<RTable>> mapRTable = new HashMap<Integer, List<RTable>>();
					List<RTable> rtable = new ArrayList<RTable>();

					// 1.1 adding req ts_k < start < ts_k+1
					System.out.print("List REQs:\n ");
					List<Node> listNodeReq = new ArrayList<Node>();
					Queue<RequestPSO> reqTS = new PriorityQueue<RequestPSO>();
					for (RequestPSO r : req) {
						double start = r.getStart();
						if ((start < ts) && (start >= (ts - TS))) {
							listNodeReq.add(r.getSrcNode());
							reqTS.add(r);
							System.out.print(r.getSrcNode().getName() + "." + start + "\t");
						}
					}

					// 1.2 updated create RTable for multi reqs
					for (RequestPSO r : reqTS) {
						List<RTable> rtableREQ = new ArrayList<RTable>();
						int reqId = r.getId();
						Node reqNode = r.getSrcNode();
						rtableREQ = createRoutingTable(topo, rtableREQ, new RequestPSO(reqId, WL, reqNode), listNodeReq,
								hc, single);
						rtable.addAll(rtableREQ);
						mapRTable.put(reqId, rtableREQ);
					}

					// 1.2 create RTable for multi reqs
//					for (int reqId = 1; reqId <= listNodeReq.size(); reqId++) {
//						List<RTable> rtableREQ = new ArrayList<RTable>();
//						int id = reqId - 1;
//						Node reqNode = listNodeReq.get(id);
//						rtableREQ = createRoutingTable(topo, rtableREQ, new RequestPSO(reqId, WL, reqNode), listNodeReq,
//								hc);
//						rtable.addAll(rtableREQ);
//						mapRTable.put(reqId, rtableREQ);
//					}

					// System.out.println(listNodeReq);

					// if having req in queue
					if (listNodeReq.size() != 0) {

					//	 log list_reqs
						myWriterPSOtraff.write(ts + "\t");
						for(RequestPSO r:reqTS) {
							myWriterPSOtraff.write(r.getId() + "\t");
						}
						myWriterPSOtraff.write("\n");

						/**
						 * --- 2. Run PSO ---
						 */
						System.out.println("\n***********PSO Running***********\n");
						Node bestNode = topo.get(0); // choose node to process, (only effective with gain-alg)

						HashMap<Integer, Double> resultPSO = getPSO(WL, bestNode, rtable, mapRTable); // estimate pi for
																										// node i

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

							if(Constants.testcase!=2) {
								// calc t_process for all paths to node ~ including t_wait
								for (RTable r2 : rtable) {
									if (r2.getDes().equals(r.getDes())
											&& (r2.getId() != r.getId() || (r2.getReq().getId() != r.getReq().getId()))) {
										compute += r2.getRatio() * r2.getReq().getWL() / r2.getResource(); // adding time(newWL) route2
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

						// 3.2 t_ser in node - error bz rid and listNodeReq
//						myWriterPSO.write("\nreqId\t" + "nodeRequest\t" + "t_serve\t" + "serve_rate\t" + "noNeigbour\t" + "\n");
//						for (int rid = 1; rid <= Constants.NUM_REQ; rid++) {
//							double max = 0;
//							double wlR = 0;
//							for (RTable r : rtable) {
//								double sumTime = r.getTimeCompute() + r.getTimeTrans();
//								if ((r.getReq().getId() == rid) && (sumTime > max)) {
//									wlR = r.getReq().getWL();
//									max = sumTime; // timetran=0
//								//	System.out.println("REQ: " + rid + " " + max);
//								}
//							}
	//
//							myWriterPSO.write(rid + "\t" + listNodeReq.get(rid - 1).getId() + "\t" + max + "\t" + max / wlR + "\t"
//									+ findNeighbour(topo, listNodeReq.get(rid - 1)).size() + "\t" + "\n");
	//
//						}

						// 3.3 wl and neighbour in node
//						HashMap<String, Integer> paths = Util.getPahts(rtable);
//						System.out.println("PAHTS " + paths);
//						myWriterPSO.write("\nNode\t" + "WL\t" + "noNeigbour\t" + "\n");
	//
//						int nnodes = 0;
//						for (Node t1 : topo) {
//							if (t1.getWL() != 0) {
////								myWriterPSO.write(
////										t.getId() + "\t" + t.getWL() / WL + "\t" + paths.get(String.valueOf(t.getId())) + "\n");
//								myWriterPSO.write(t1.getId() + "\t" + t1.getWL() + "\t" + findNeighbour(topo, t1).size() + "\n");
//								nnodes++;
//							}
//						}

						// 3.4 rtable
						System.out.println("\n----- RESULT (P_a->i)_k: | (p_src->des)_route");
						rtable.forEach((re) -> System.out.println(">" + re.toString()));

						// 3.5 logging wl sorted in node
//						System.out.println("\n----- WL sorted in node: ");
//						List<Node> topoSort = topo;
//						Collections.sort(topoSort, new Comparator<Node>() {
//							@Override
//							public int compare(Node o1, Node o2) {
//								return o1.getWL() < o2.getWL() ? 1 : -1;
//							}
//						});
//						
//						System.out.println("Node\t" + "npaths\t" + "WL(ratio)\t");
//						double std = 0;
//						for (Node t1 : topoSort) {
//							if (t1.getWL() != 0) {
//								std += (t1.getWL() / WL - (double) (Constants.NUM_REQ) / nnodes)
//										* (t1.getWL() / WL - (double) (Constants.NUM_REQ) / nnodes);
//								System.out.println(t1.getId() + "\t" + paths.get(String.valueOf(t1.getId())) + "\t" + t1.getWL() / WL);
//							}
//						}
	//
//						System.out.println("STD: " + Math.sqrt(std / nnodes));
					}

					/**
					 * --- 4. Process CWL and queue in node ---
					 */
					// 4.1 calc assigned workload in node (all cWL) and adding queue
					System.out.println("\nCALC assigned new workload and ADD new reqs to queue");
					for (Node n : topo) {
						double aWL = 0; // all assigned workload as new-workload
						boolean check = false;
						for (RTable r : rtable) {
							if (r.getDes().equals(n.getName())) {
								aWL += r.getRatio() * r.getReq().getWL();
								double t_process = r.getRatio() * r.getReq().getWL() / r.getResource();
								// adding queue
								double start = r.getTimeTrans() + (t - 1) * TS; // the arrival task to Node, PSO at ts
								// double start = r.getTimeTrans()+(t)*TS;
								Node srcNode = r.getReq().getSrcNode();
								n.getqReq()
										.add(new RequestPSO(r.getReq().getId(), r.getReq().getWL(), n.getId(), r.getRoute(),
												start, t_process, false, start, start + t_process, srcNode, r.getRatio(),
												r.getTimeTrans(), r.getTimeSer()));
								check = true;
							}
						}

//						if (check)
//							System.out.println("Timeslot: " + t + " Node: " + n.getName() + " aWL=" + aWL);
						n.setcWL(n.getcWL() + aWL);
					}

					// 4.2 calc and update queue
					System.out.println("\nUPDATE QUEUE");
					for (Node n : topo) {
						boolean check = true;
						while (check && (n.getqReq().peek() != null)) {

							double start1 = n.getqReq().peek().getStart();
							double end1 = n.getqReq().peek().getEnd();
							System.out.print("Node: " + n.getName());
							System.out.println(" REQ: " + start1 + " -> " + end1);
							check = false;
							if (end1 < ts) {
								n.getqReq().peek().setDone(true);
								n.getDoneReq().add(n.getqReq().peek()); // adding to done req
							//	System.out.println("doneREQ: " + n.getqReq().peek().getStart() + " -> " + end1);
								n.getqReq().remove(); // req is done, removing
								RequestPSO nextReq = n.getqReq().peek(); // update next request if data sent
								if (nextReq != null) {
									if (end1 > nextReq.getStart()) {
							//			System.out.println("update next req start at: " + end1);
										n.getqReq().peek().setStart(end1);
										n.getqReq().peek().setEnd(end1 + n.getqReq().peek().getTimeProcess());
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
					for (Node n : topo) {
						double pWL = 0; // processed workload
						// double aWL = 0; // all assigned worload
						if ((n.getqReq().size() != 0) || n.getDoneReq().size() != 0) { // node in processing
//							System.out.println("Node " + n.getName());
//							n.getDoneReq().forEach((d) -> {
//								System.out.println("Done req: " + d.getStart() + " " + d.getEnd());
//							});
//							n.getqReq().forEach((q) -> System.out.println("Queue req: " + q.getStart() + " " + q.getEnd()));

							for (RequestPSO d : n.getDoneReq()) {
								pWL += (d.getEnd() - d.getStart()) * n.getRes();
							}

							if (n.getqReq().peek() != null) {
								double lastStart = n.getqReq().peek().getStart();
								if (lastStart < ts) {
									pWL += (ts - lastStart) * n.getRes();
								}
							}
							// System.out.println("Process WL: " + pWL + " / " + n.getcWL());
							n.setcWL((n.getcWL() - pWL) < 0 ? 0 : (n.getcWL() - pWL));
//							System.out.println("cWL: " + n.getcWL());
						}

					}

					// log

				} // endts
				System.out.println("\n----- DONE REQ ------");
				
				//log node done
				for (Node n : topo) {
					if (n.getqReq().size() == 0) {
						if (n.getDoneReq().size() != 0)
							System.out.println("Node: " + n.getName());
						n.getDoneReq().forEach((d) -> System.out
								.println(d.getId() + "_" + d.getRoute() + ": " + d.getStart() + "\t" + d.getEnd()));
					}
				}

				myWriterPSO.write("\nReqID\t" + "a(SrcNode)\t" + "i(DesNode)\t" + "k(Path)\t" + "p(Ratio)\t"
						+ "dtTrans\t" + "tArrival\t" + "start\t" + "end\t" + "timeSer(PSO)\t" + "t_wait\t" + "t_proc\t" + "t_serv\t" + "\n");

				for (RequestPSO r : req) {
					// double endM=0;
					for (Node n : topo) {
						for (RequestPSO d : n.getDoneReq()) {
							if (d.getId() == r.getId()) {
								System.out.println("REQ: " + r.getId() + "\n" + "->" + n.getName() + "_" + d.getRoute()
										+ ": " + d.getStart() + "\t" + d.getEnd());
								double t_wait = d.getStart() - d.getTimeArrival();
								double t_proc = d.getEnd()-d.getStart();
								double t_serv = d.getTimeTrans()+t_wait + t_proc;
								myWriterPSO.write(r.getId() + "\t" + d.getSrcNode().getName() + "\t" + n.getName() + "\t"
										+ d.getRoute() + "\t" + d.getRatio() + "\t" + d.getTimeTrans() + "\t"
										+ d.getTimeArrival() + "\t" + d.getStart() + "\t" + d.getEnd() + "\t"
										+ d.getTimeSer() + "\t" + t_wait + "\t" 
										+ t_proc + "\t" +  t_serv + "\t" 
										+ "\n");
								// endM=endM>d.getEnd()?endM:d.getEnd();
							}
						}
					}
					// myWriterPSO.write(endM + "\n");
				}

				double wait = 0;
				int count=0;
				for (RequestPSO r : req) {
					double endM = 0;
					for (Node n : topo) {
						for (RequestPSO d : n.getDoneReq()) {
							if (d.getId() == r.getId()) {
								endM = endM > d.getEnd() ? endM : d.getEnd();
								wait+=(d.getStart() - d.getTimeArrival());
								count++;
							}
						}
					}
					endM -= Math.floor(r.getTimeArrival());
					myWriterPSOserv.write("\n" + endM);
				}
				System.out.println("AVG: " + wait/count);
			}
			

			
		} // end hc
		myWriterPSOtraff.close();
		myWriterPSOserv.close();
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
				topo.add(id, new Node(id, String.valueOf(id), j * space, i * space));
				// topo[id] = new Node (id,"W" + id, j*space,i*space);
				// System.out.println(topo[id].toString());
				topo.get(id).setRes(Constants.RES);
				id++;
			}
		}

		return topo;
	}

	private static List<Node> findNeighbour(List<Node> topo, Node root) {
		List<Node> neigbourNode = new ArrayList<Node>();
		int id = 1;

		for (Node n1 : root.getNodeLK()) {
			n1.setLvl(1);
			neigbourNode.add(n1);
			id++;
//			System.out.println(">>\tADD" + id + " " + n1.getId());
		}

		for (Node n1 : root.getNodeLK()) {
			for (Node n2 : n1.getNodeLK()) {
				int npath = 2;
				if (n2.getId() != root.getId() && (n2.getLvl() > n1.getLvl()) && (!neigbourNode.contains(n2))) {
					n2.setLvl(2);
					neigbourNode.add(n2);
					// System.out.println(">>\tADD" + id + " " + n2.getId());

					id++;
				}
			}
		}

		for (Node t : topo) {
			t.setLvl(Constants.MAXINT);
		}
		return neigbourNode;

	}

	private static List<RTable> createRoutingTable(List<Node> topo, List<RTable> rtable, RequestPSO req,
			List<Node> listNodeReq, int MAX, boolean single) {
		// adding root of req: reqID as name and
		Node root = req.getSrcNode();
		rtable.add(0, new RTable(0, root.getName(), root.getName(), 0, root.getRes(), req));

		int id = 1;

		for (Node n1 : root.getNodeLK()) {
			n1.setLvl(1);
			if (!listNodeReq.contains(n1)) {
				rtable.add(id, new RTable(id, n1.getName(), root.getName(), 1, n1.getRes(), req));
				rtable.get(id).setNpath(1);
				id++;
			}

			// System.out.println(">>\tADD DES: " + n1.getName() + " ROUTE: " +
			// root.getName() + " | " + 1);

		}

//		for (Node n1 : root.getNodeLK()) {
//			// System.out.println("\n>NODE DIRECT: " + n1.getName() + " ROUTE: " +
//			// root.getName());
//			for (Node n2 : n1.getNodeLK()) {
//				int npath = 2;
//				if (n2.getId() != root.getId() && (n2.getLvl() > n1.getLvl()) && (!listNodeReq.contains(n2))) {
//					n2.setLvl(2);
//					// adding vNode
//					n2.getvNode().add(n1);
//					rtable.add(id, new RTable(id, n2.getName(), n1.getName(), 2, n1.getRes(), req));
//					rtable.get(id).setNpath(npath);
//					// System.out.println(">>\tADD DES: " + n2.getName() + " ROUTE: " + n1.getName()
//					// + " | " + npath);
//
//					id++;
//
//				}
//			}
//		}

//		for (Node n2 : topo) {
//			if(n2.getLvl()==2) {
//				for (Node n3:n2.getNodeLK()) {
//					int npath=3;
//					if (n3.getId() != root.getId() && (n3.getLvl() > n2.getLvl()) && (!listNodeReq.contains(n3))) {
//					n3.setLvl(3);
//					rtable.add(id, new RTable(id, n3.getName(), n2.getName(), 2, n3.getRes(), req));
//					rtable.get(id).setNpath(npath);
//					
//					 System.out.println(">>\tADD DES: " + n3.getName() + " ROUTE: " + n2.getName()
//					 + " | " + npath);
//					
//					id++;
//					}
//				}
//			}
//		}

		// int MAX=3;
		for (int maxHop = 2; maxHop <= MAX; maxHop++) {
			for (Node n : topo) {
				if (n.getLvl() == (maxHop - 1)) {
					for (Node n1 : n.getNodeLK()) {
						int npath = maxHop;
						if (n1.getId() != root.getId() && (n1.getLvl() > n.getLvl()) && (!listNodeReq.contains(n1))) {
							n1.setLvl(maxHop);
							rtable.add(id, new RTable(id, n1.getName(), n.getName(), maxHop, n1.getRes(), req));
							rtable.get(id).setNpath(npath);

							// System.out.println(">>\tADD DES: " + n1.getName() + " ROUTE: " + n.getName()
							// + " | " + npath);

							id++;
						}
					
					}

				}
			}
		}

		// update cWL of node to routing table
		for (RTable r : rtable) {
			for (Node t : topo) {
				if (t.getName().equals(r.getDes())) {
					r.setcWL(t.getcWL());
					// System.out.println("Adding r: " + r.toString() + " " +r.getcWL());
				}
			}
		}

//		System.out.println("\ncreated RoutingTable: ");
//		rtable.forEach((e) -> {
//			System.out.println(">" + " " + e.toString());
//		});
		
		if (single) {
			// delete multi-route
			List<String> desNode = new ArrayList<String>();
			List<RTable> rmNode = new ArrayList<RTable>();
			List<RTable> rtableClone = new ArrayList<RTable>();
			rtableClone = rtable;
			
			for (RTable r:rtableClone) {
				if(!desNode.contains(r.getDes())) { // new desNode
					desNode.add(r.getDes());

				}else {
					rmNode.add(r);
				}
			}
			for (RTable rm:rmNode) {
				rtable.remove(rm);
			}
			// end delete 
		}

		
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
	 * @param workload  is total workload need assigned
	 * @param bestNode  is the data owner node, others connect to it
	 * @param rtable    is full info of network, can cacl time_trans, time_compute
	 *                  using prams in the table
	 * @param mapRTable
	 * @return ration p as HashMap<id,value>
	 */
	private static HashMap<Integer, Double> getPSO(double workload, Node bestNode, List<RTable> rtable,
			HashMap<Integer, List<RTable>> mapRTable) {

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

		PSOSwarm swarm = new PSOSwarm(particles, epchos, nnodes, workLoad, currentWorkload, bestNode, rtable,
				mapRTable);

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

	/**
	 * @param topo = list node
	 * @return topo = list node adding neighbour node/ nodelk
	 */

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
