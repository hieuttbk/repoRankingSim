package ManagerApplication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CopyOnWriteArrayList; 

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class ManagerApplication {
	
	private static String[] container = { "SERVICE", "MONITOR", "RESULT" };
	private static final int workerIndex = ConfigVar.WORKERS; 
	private static final ArrayList<Worker> workerCse = new ArrayList<Worker>(); // list of worker
	private static final CopyOnWriteArrayList<Worker> qualifiedWorker = new CopyOnWriteArrayList<Worker>(); //qualified workers to deploy
	private static final ArrayList<Service> receivedService = new ArrayList<Service>(); //
	static final ArrayList<Service> allService = new ArrayList<Service>(); //
	private static final ArrayList<Container> cnt = new ArrayList<Container>();
	static ArrayList<TimeLog> timeList= new ArrayList<TimeLog>(); 
	
	//Single thread of monitoring workers
	private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	//LOGGER 
	private static final Logger LOGGER = LogManager.getLogger(ManagerApplication.class);
	
	
	public static void main(String[] args) {

		// Start server
		HttpServer server = null;
		try {
			server = HttpServer.create(new InetSocketAddress(ConfigVar.AEPORT), 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		server.createContext("/", new Handler(workerCse, qualifiedWorker, receivedService, cnt)); 
		server.setExecutor(Executors.newFixedThreadPool(4)); 
		server.start();

		// register AE in manager CSE
		JSONArray array = new JSONArray();
		array.put(ConfigVar.APPPOA);
		JSONObject obj = new JSONObject();
		obj.put("rn", ConfigVar.AENAME);
		obj.put("api", 12346);
		obj.put("rr", true);
		obj.put("poa", array);
		JSONObject resource = new JSONObject();
		resource.put("m2m:ae", obj);
		RestHttpClient.post(ConfigVar.ORIGINATOR, ConfigVar.MANCSEPOA + "/~/" 
												+ ConfigVar.MANCSEID + "/" + ConfigVar.MANCSENAME, 
												resource.toString(), 2);
		LOGGER.info("Registered ManagerAE to CSE");

		
		// subscribe to all needed container
		for(int i=1; i<=workerIndex; i++){
			String workerPoa = "/worker-"+i+"-id/worker-"+i; 
			noticedWorkerCse(new Worker("worker-"+i, "worker-"+i+"-id", workerPoa));
			for (String j : container) {
				array = new JSONArray();
				array.put(ConfigVar.NU);
				obj = new JSONObject();
				obj.put("nu", array); 
				obj.put("rn", ConfigVar.AESUB);
				obj.put("nct", 2);
				resource = new JSONObject();
				resource.put("m2m:sub", obj);
				HttpResponse response = new HttpResponse();
				response = RestHttpClient.post(ConfigVar.ORIGINATOR, ConfigVar.MANCSEPOA
						+ "/~/"+ "worker-"+i+"-id"+"/"+"worker-"+i+"/" + j, resource.toString(), 23);
				// store container and Parent ID
				cnt.add(new Container(j, (new JSONObject(response.getBody()))
						.getJSONObject("m2m:sub").get("pi").toString()));
				//LOGGER.info("Subscribed to {}'s {} container", workerName, j);
			
		}
		
		
		for(Container test : cnt){
			System.out.println(test.getContainerName() + " " + test.getContainerID());
		}
		

		/**
		 * Periodic sending monitor status
		 * include Resource info
		 * include currentWorkload
		 */
		final Runnable periodCheck = new Runnable(){
			public void run() {
				//LOGGER.info("Periodic checkup on workers");
				for(Worker worker : workerCse){	
					try{
						int statusCode = Command.monitorResource(
								worker.getCsePoa(),
								worker.getCseId(),
								worker.getCseName(),
								ConfigVar.COMMANDID,
								1);
						ConfigVar.COMMANDID++;
						if (statusCode == 201) {
							//LOGGER.info("Sent monitor command to {}", worker.getCseId());
						}		
					}catch(Exception e){
						System.out.println("Something Wrong...");
						
					}
				}
			}	
		};
		scheduler.scheduleAtFixedRate(periodCheck, 5000, 2000, TimeUnit.MILLISECONDS);
		//task, run_after, interval, time_unit
		}
	}
	
	private static void noticedWorkerCse(Worker worker) {
		if (workerCse.contains(worker)) {
			System.out.println("Worker already noticed");
		} else {
			workerCse.add(worker);
		}
	}
	
	
}
