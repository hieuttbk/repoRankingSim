package ManagerApplication;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import PSO.Swarm;
import PSO.Vector;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Handler implements HttpHandler {

	private static ArrayList<Worker> workerCse;
	private static CopyOnWriteArrayList<Worker> qualifiedWorker;
	private static ArrayList<Service> receivedService;
	private static ArrayList<Container> cnt;

	private static final Logger LOGGER = LogManager.getLogger(Handler.class);

	// For test ONLY
	private static int nService = 0;
	private static int workloadTest = 100;
	private static int nTimes = 1;
	//private static int nService = 2;

	public Handler(ArrayList<Worker> workerCse,
			CopyOnWriteArrayList<Worker> qualifiedWorker,
			ArrayList<Service> receivedService, ArrayList<Container> cnt) {
		this.workerCse = workerCse;
		this.qualifiedWorker = qualifiedWorker;
		this.receivedService = receivedService;
		this.cnt = cnt;
	}

	public void handle(HttpExchange httpExchange) {
		// System.out.println("Event Recieved!");

		try {
			InputStream in = httpExchange.getRequestBody();

			String requestBody = "";
			int i;
			char c;
			while ((i = in.read()) != -1) {
				c = (char) i;
				requestBody = (String) (requestBody + c);
			}

			// System.out.println(requestBody);

			Headers inHeader = httpExchange.getRequestHeaders();
			String headerTimeStamp = inHeader.getFirst("X-M2M-OT");
			JSONObject json = new JSONObject(requestBody);

			String responseBudy = "";
			byte[] out = responseBudy.getBytes("UTF-8");
			httpExchange.sendResponseHeaders(200, out.length);
			OutputStream os = httpExchange.getResponseBody();
			os.write(out);
			os.close();

			if (json.getJSONObject("m2m:sgn").has("m2m:vrq")) {
				if (json.getJSONObject("m2m:sgn").getBoolean("m2m:vrq")) {
					// LOGGER.info("Confirm subscription");
				}
			} else {
				JSONObject rep = json.getJSONObject("m2m:sgn")
						.getJSONObject("m2m:nev").getJSONObject("m2m:rep")
						.getJSONObject("m2m:cin");
				String pi = rep.getString("pi"); // = /worker-id/cnt-....
				String fromWorkerId = pi.split("/")[1]; // worker ID
				for (Container ctner : cnt) {
					if (pi.equals(ctner.getContainerID())) {
						JSONArray content = new JSONArray(rep.getString("con"));

						/*
						 * MONITOR worker node
						 */
						if (ctner.getContainerName().equals("MONITOR")) {

							Worker worker = getWorkerById(fromWorkerId);
							worker.setCurrentWorkload(content.getJSONObject(3)
									.getInt("CWORKLOAD"));

							if (worker.getState()) { // get CPU in use
								if (!qualifiedWorker.contains(worker)) {
									qualifiedWorker.add(worker);
								}
							} else { // if not qualified, remove from list
								Boolean rmWorker = qualifiedWorker
										.remove(worker);
								if (rmWorker) {
									// no case to remove worker yet
								}
							}
						}

						/*
						 * Incomming service
						 */
						if (ctner.getContainerName().equals("SERVICE")) {

							Timestamp tns = new Timestamp(
									System.currentTimeMillis());
							long tnsms = System.currentTimeMillis();

							String servName = content.getJSONObject(0)
									.getString("SERVICE");
							String servId = content.getJSONObject(1).getString(
									"SERVICEID");
							int servN = content.getJSONObject(5).getInt(
									"NSERVICE");

							System.out.println("\nNew SERVICE to worker "
									+ fromWorkerId + " SERVICE NUMER: " + servN);

							Worker originalWorker = getWorkerById(fromWorkerId);
							Date now = (Calendar.getInstance()).getTime();
							int workLoad = content.getJSONObject(4).getInt(
									"WORKLOAD");
							Service serv = new Service(servName, servId,
									originalWorker, content.getJSONObject(2)
											.getInt("NOAWORKER"), workLoad,
									(new Timestamp(now.getTime())).toString(),
									servN);

							receivedService.add(serv);
							ManagerApplication.allService.add(serv);

							// print all service and id

							ManagerApplication.timeList.add(new TimeLog(
									ConfigVar.TIMETY.TNS, fromWorkerId, serv
											.getIndexService(), tns, serv
											.getServiceId(), tnsms));
							// System.out.println("Service remaining: ");

							// for (Service service : receivedService) {
							// System.out.println(service.getIndexService() +
							// " _ " + service.getServiceId());
							// }

							/*
							 * Run pso for current service, then send zip file
							 * message PSO.optimalRatio --> return a Map: PC:p1,
							 * pi1: p2, ...
							 */

							// get currentWorkload of all worker
							Vector currentWorkload = getAllCurrentWorkload();
							System.out.println("All nodes current workload: "
									+ currentWorkload.toStringOutput());

							// Config; no deploy in MAN
							// particle, epoch, nodes= w + 1 Man, workLoad,
							// currentWL

							Swarm swarm = new Swarm(20, 1000, workerCse.size(),
									workLoad, currentWorkload);

							
							Map<String, String> ratio = swarm.run(content
									.getJSONObject(1).getString("SERVICEID"));
							 System.out.println(ratio.toString());
							
							// tricky to run on Worker2-Worker1
						//	Map<String, String> ratio = new HashMap<String, String>();
						//	ratio.put("worker-1-id", "1-0");
						//	ratio.put("worker-2-id", "1-" + workLoad);	//deploy on Worker 2
						//	ratio.put("MANAGER", "1-" + workLoad);  	//deploy on Man
							

							System.out.println("[DEBUG]: workerCse.size "
									+ workerCse.size() + ": "
									+ ratio.toString());
							// LOGGER.info("[DEBUG]: ratio " +
							// ratio.toString());

							serv.setRatio(ratio); // update workload ratio for
													// this new service

							// except service orgininal worker
							String[] ratioImages = new String[ratio.size()];

							int k = 0;
							// Create ratio Map{ key = Node; value = workload
							// Ratio }
							for (String key : ratio.keySet()) {
								ratioImages[k] = ratio.get(key);
								k++;
							}

							// wrap ratio Map into command for original worker
							// to zip files
							int zip = Command.zipData(
									originalWorker.getCseId(),
									originalWorker.getCseName(),
									ConfigVar.COMMANDID, ConfigVar.ZIP,
									ratioImages, // ratioImages new
													// String[]{"1-"+workLoad}
									serv.getServiceId());

							if (zip != 201) {
								System.out
										.println("\n-------------ERROR send commmand ZIP---------------\n"
												+ servN + servId);
							}

						}

						/*
						 * Receive service result There are 2 Sub services: Zip
						 * Data + Detect images If Received service result is
						 * ZIP => meaning Zipping data is done from original
						 * worker => process to send command to other workers to
						 * pull zipped data and start detecting
						 * 
						 * If Received service result is DETECT => meaning 1
						 * worker has done its detecting job => count if the
						 * number of worker report done DETECT service = number
						 * of worker got assigned for that service ID => Whole
						 * service is done
						 */

						if (ctner.getContainerName().equals("RESULT")) {
							synchronized (this) {

								String serviceId = content.getJSONObject(0)
										.getString("SERVICEID");
								String service = content.getJSONObject(1)
										.getString("SERVICE");

								// Result of zipping data . STATE == 1 means
								// DONE
								if (service.equals("ZIP")) {
									Timestamp trd = new Timestamp(
											System.currentTimeMillis());
									long trdms = System.currentTimeMillis();

									if (content.getJSONObject(3).getInt(
											"ZIPSTATE") == 1) {
										Service serv = getServiceById(serviceId);

										/*
										 * Send deploy command to workers
										 */

										ManagerApplication.timeList
												.add(new TimeLog(
														ConfigVar.TIMETY.TRD,
														fromWorkerId,
														serv.getIndexService(),
														trd, serviceId, trdms));
										
									
										
																
										Map<String, String> ratio = serv
												.getRatio();
										
										Iterator<Worker> iterator = qualifiedWorker
												.iterator();
										
										while (iterator.hasNext()) {
											Worker worker = iterator.next();
											Command.deployContainer(
													worker.getCseId(), // command
																		// destination
													worker.getCseName(),
													ConfigVar.DEPLOY,
													ConfigVar.COMMANDID,
													serv,
													ratio.get(worker.getCseId()));
											ConfigVar.COMMANDID++;
											serv.increaseAssignedWorker();
										}
										
										
										 
								/*		// * Deploy on Manager
										 
										Worker originalWorker = serv.getOriginalWorker();
										long t1 = System.currentTimeMillis();
										PullData.usingDiscovery(serv.getServiceId(), 
												originalWorker.getCseId()+"/"+originalWorker.getCseName(),  
												ratio.get("MANAGER"));  
										long t2 = System.currentTimeMillis();
										

										ZipUtils.UnzipData(ratio.get("MANAGER"),  
												serv.getServiceId());
										
										DockerController.deployService(ConfigVar.MANCSEPOA, 
												originalWorker.getCseId(), 
												originalWorker.getCseName(), 
												serv.getServiceId(), 
												"DetectImage", 
												ConfigVar.COMMANDID, 
												ratio.get("MANAGER"));  //"1-"+serv.getWorkload(), if only deploy on Man
										
										// to get
										// result//////////////////////////////////
										long t=t2-t1;
										String filename = ConfigVar.DIR
												+ "\\Pull.txt";
										FileWriter fw = null; 
										try {
											fw = new FileWriter(filename, true);
											fw.write(String.valueOf(t)+"\n");

										} catch (IOException ioe) {
											System.err.println("IOException: "
													+ ioe.getMessage());
										} finally {
											if (fw != null) {
												fw.close();
											}
										}

										ConfigVar.COMMANDID++;
										// serv.increaseAssignedWorker();
*/
									}

								}

								if (service.equals("DETECT")) {

									Timestamp tre = new Timestamp(
											System.currentTimeMillis());
									long trems = System.currentTimeMillis();

									Service resultServ = getServiceById(serviceId);
									resultServ.decreaseAssignedWorker();
									nService++;

									// ManagerApplication.timeList
									// .add(new TimeLog(
									// ConfigVar.TIMETY.TP,
									// fromWorkerId,
									// resultServ
									// .getIndexService(),
									// tre, serviceId, XXX));
									//

									ManagerApplication.timeList
											.add(new TimeLog(
													ConfigVar.TIMETY.TRE,
													fromWorkerId, resultServ
															.getIndexService(),
													tre, serviceId, trems));

									// LOGGER.info("[DEBUG] TRE from worker: " +
									// fromWorkerId );

									System.out.println("RESP of service = "
											+ resultServ.getIndexService()
											+ " from: " + fromWorkerId);
									
									
								//	resultServ.updateServiceStatus(true); // tricky for test W-W
									
									// System.out
									// .println("Service "
									// + resultServ
									// .getIndexService()
									// + " need RESP form nWORKERS: "
									// + resultServ
									// .getNumberOfAssignedWorker()
									// + "\n");

									if (resultServ.getNumberOfAssignedWorker() == 0) {
										resultServ.updateServiceStatus(true);
									} else {
										System.out
												.println("Service = "
														+ resultServ
																.getIndexService()
														+ " need RESP from nWorkers: "
														+ resultServ
																.getNumberOfAssignedWorker());
										// LOGGER.info("Waiting for other workers for service {}",
										// resultServ.getServiceId());
									}

									if (getServiceById(serviceId)
											.getServiceStatus()) {

										// command cz order of timelog.

										// ManagerApplication.timeList
										// .add(new TimeLog(
										// ConfigVar.TIMETY.TRS,
										// fromWorkerId,
										// resultServ
										// .getIndexService(),
										// tre, serviceId, trems));

										System.out
												.println("----------------------------------> FULL SERVICE DONE FOR SERVICE NUMER: "
														+ resultServ
																.getIndexService());
										Date now = (Calendar.getInstance())
												.getTime();
										Timestamp tsNow = new Timestamp(
												now.getTime());
										long fullService = tsNow.getTime()
												- Timestamp.valueOf(
														resultServ.getTime())
														.getTime();

										// LOGGER.info("Toltal time full service with ID: {}",
										// fullService,
										// resultServ.getServiceId());

										// to get
										// result//////////////////////////////////

										String filename = ConfigVar.DIR
												+ "\\MyResult.txt";
										FileWriter fw = null; // the true will
																// append the
																// new data
										
										
										
										// resultServ ~ Service
										// log each round
										
										String result1 = String
												.valueOf(resultServ
														.getIndexService());

										for (int wid = 1; wid <= ConfigVar.WORKERS; wid++) { // worker
											String workerID = "worker-"
													+ String.valueOf(wid)
													+ "-id";
											for (TimeLog t : ManagerApplication.timeList) {
												if (t.getServiceID()
														.equals(resultServ
																.getServiceId())) {
													if (t.getWorkerID()
															.equals(workerID)) {
														result1 += "  "
																+ String.valueOf(t
																		.getTimems());
													}
												}
											}
										}
																
										try {
											fw = new FileWriter(filename, true);
										
											
											
											/*fw.write("service number: "
													+ resultServ
															.getIndexService()
													+ " serviceId: "
													+ resultServ.getServiceId()
													+ " workload: "
													+ resultServ.getWorkload()
													+ " Tprocess: "
													+ fullService + "\n" );
*/
											fw.write(result1+"\n");   // log each round
											
											
											// + " Ratio: " +
											// resultServ.getRatio().toString()
											// +"\n");//appends the string to
											// the file

										} catch (IOException ioe) {
											System.err.println("IOException: "
													+ ioe.getMessage());
										} finally {
											if (fw != null) {
												fw.close();
											}
										}
										// ////////////////////////////////////////////////

										// indexService++;
										receivedService.remove(resultServ);
										receivedService.trimToSize(); // decrease
																		// size
										System.out.println("[DEBUG] receivedService.size "
												+ nService);

										// ---------------------------RESULT----------------------------------
										if (ManagerApplication.allService
												.size() == 30) {
											//Thread.sleep(180000);
											if (nService >= 90) { // 1 test 
											//	Thread.sleep(60000); // 1 test error
												ZipUtils.createFile(ConfigVar.DIR_R);
												ZipUtils.writeFile(
														ConfigVar.DIR_R,
														System.lineSeparator());
												for (Service a : ManagerApplication.allService) { // list
																									// indexService

													String result = String
															.valueOf(a
																	.getIndexService());

													for (int wid = 1; wid <= ConfigVar.WORKERS; wid++) { // worker
														String workerID = "worker-"
																+ String.valueOf(wid)
																+ "-id";
														for (TimeLog t : ManagerApplication.timeList) {
															if (t.getServiceID()
																	.equals(a
																			.getServiceId())) {
																if (t.getWorkerID()
																		.equals(workerID)) {
																	LOGGER.info("[ALL OF TIME] +  Worker "
																			+ +wid
																			+ " = "
																			+ t.genTime());
																	result += "  "
																			+ String.valueOf(t
																					.getTimems());
																}
															}
														}
													}
													ZipUtils.writeFile(
															ConfigVar.DIR_R,
															result);
													ZipUtils.writeFile(
															ConfigVar.DIR_R,
															System.lineSeparator());
												}
											}
											else{ // ERROR
												for (Service a : ManagerApplication.allService) {
													if(!a.getServiceStatus()){
														String result = String
																.valueOf(a
																		.getIndexService());

														for (int wid = 1; wid <= ConfigVar.WORKERS; wid++) { // worker
															String workerID = "worker-"
																	+ String.valueOf(wid)
																	+ "-id";
															for (TimeLog t : ManagerApplication.timeList) {
																if (t.getServiceID()
																		.equals(a
																				.getServiceId())) {
																	if (t.getWorkerID()
																			.equals(workerID)) {
																		LOGGER.info("[ALL OF TIME] +  Worker "
																				+ +wid
																				+ " = "
																				+ t.genTime());
																		result += "  "
																				+ String.valueOf(t
																						.getTimems());
																	}
																}
															}
														}
														ZipUtils.writeFile(
																ConfigVar.DIR_R,
																result);
														ZipUtils.writeFile(
																ConfigVar.DIR_R,
																System.lineSeparator());
													}
												}
											}
											;
										}
										// -----------------------END
										// RESULT------------------------------------------

									}

								}

							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param workerId
	 *            : get worker with given Id
	 * @return worker
	 */
	private static Worker getWorkerById(String workerId) {
		for (Worker worker : workerCse) {
			if ((worker.getCseId()).equals(workerId))
				return worker;
		}
		System.out.println("No worker found!");
		return null;
	}

	/**
	 * 
	 * @param serviceId
	 *            : serviceID get from message
	 * @return service with given ID
	 */
	static Service getServiceById(String serviceId) {
		Iterator<Service> iterator = receivedService.iterator();
		while (iterator.hasNext()) {
			Service service = iterator.next();
			if (serviceId.equals(service.getServiceId()))
				return service;
		}
		System.out.println("No service found!");
		return null;
	}

	/**
	 * 
	 * @return current Workload of all qualified worker
	 */
	private static Vector getAllCurrentWorkload() {
		double[] cWorkload = new double[qualifiedWorker.size()]; // workers

		int index = 0;
		// cWorkload[index] = getNodeCurrentWorkload(); //manager
		for (Worker worker : qualifiedWorker) {
			cWorkload[index] = worker.getCurrentWorkload(); // workers
			index++;
		}
		Vector currentWorkload = new Vector(cWorkload);
		return currentWorkload;
	}

	private static int getNodeCurrentWorkload() {
		int currentWorkload = 0;
		File folder = new File(ConfigVar.DATADIR);
		File[] listOfFile = folder.listFiles();
		if (listOfFile.length == 0) {
			System.out.println("Empty");
		}
		for (File file : listOfFile) {
			if (file.isDirectory()) {
				System.out.println(file.getAbsolutePath());
				folder = new File(file.getAbsolutePath());
				currentWorkload += folder.listFiles().length;
			}
		}
		System.out.println(currentWorkload);
		return currentWorkload;
	}

}
