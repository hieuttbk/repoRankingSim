/**
 * 
 */
package ManagerApplication;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author namnguyen
 *
 */
public class Service {
	private String serviceId;
	private	Worker originalWorker;  // where the service comes from
	private int NOAWorker; //Number of Assigned Workers
	private String serviceName;
	private boolean doneStatus;
	private int workLoad;
	private String ts; //for timestamp
	private ArrayList<Worker> assignedWorker;
	private Map<String, String> ratio;
	private int nService; //index of service
	
	public Service(String serviceName, String serviceId, Worker originalWorker, int NOAWorker,
					int workLoad, String ts, int nService){
		this.serviceId = serviceId;
		this.serviceName = serviceName;
		this.originalWorker = originalWorker;
		this.NOAWorker = NOAWorker;
		this.workLoad = workLoad;
		this.doneStatus = false;
		this.ts = ts;
		this.ratio = null;
		this.nService = nService;
	}
	
	public String getTime(){ //for timestamp
		return ts;
	}
	
	public String getServiceName(){
		return serviceName;
	}
	
	public void setServiceName(String serviceName){
		this.serviceName = serviceName;
	}
	
	public String getServiceId(){
		return serviceId;
	}
	
	public Worker getOriginalWorker(){
		return originalWorker;
	}
	
	public int getWorkload(){
		return workLoad;
	}
	
	public int getNumberOfAssignedWorker(){
		return NOAWorker;
	}
	
	public void increaseAssignedWorker(){
		NOAWorker+=1;
	}
	
	public void decreaseAssignedWorker(){
		NOAWorker-=1;
	}
	
	public void setRatio(Map<String, String> ratio){
		this.ratio = ratio;
		//System.out.println("inside Service class");
		//System.out.println(this.ratio.toString());
	}
	
	public Map<String, String> getRatio(){
		return ratio;
	}
	
	public boolean getServiceStatus(){
		return doneStatus;
	}
	
	
	public void updateServiceStatus(boolean status){
		this.doneStatus = status;
	}
	
	public void assignedToWorker(Worker worker){
		assignedWorker.add(worker);
	}
	
	public ArrayList<Worker> getAssignedWorker(){
		return assignedWorker;
	}
	
	//for Test
	public int getIndexService(){
		return nService;
	}
}
