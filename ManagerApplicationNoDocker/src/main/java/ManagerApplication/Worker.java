package ManagerApplication;


public class Worker {
	private String cseName;
	private String cseId;
	private String csePoa;
	private Boolean state;
	private int currentWorkload;
	
	
	public Worker(String cseName, String cseId, String csePoa){
		this.cseId = cseId;
		this.cseName = cseName;
		this.csePoa = csePoa;
		this.state = true; // true = can be use, false = cannot be used
		this.currentWorkload = 0;
		 
	}
	
	public String getCseId(){
		return cseId;
	}
	
	public String getCseName(){
		return cseName;
	}
	
	public String getCsePoa(){
		return csePoa;
	}
	
	public Boolean getState(){
		return state;
	}
	
	public void setState(Boolean state){
		this.state = state;
	}
	
	public int getCurrentWorkload(){
		return currentWorkload;
	}
	
	public void setCurrentWorkload(int currentWorkload){
		this.currentWorkload = currentWorkload;
	}
	
	
}
