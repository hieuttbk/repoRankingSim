package ManagerApplication;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import ManagerApplication.ConfigVar.TIMETY;

public class TimeLog {
	TIMETY type;
	String workerID;
	int roundID; // index service
	Timestamp valueTime;
	String serviceID;
	long timems;
			
	public TimeLog(TIMETY tn, String workerID, int roundID, Timestamp ts1, String serviceID, long timems) {
		super();
		this.type = tn;
		this.workerID = workerID;
		this.roundID = roundID;
		this.valueTime = ts1;
		this.serviceID=serviceID;
		this.timems=timems;
	}
	

	public long getTimems() {
		return timems;
	}


	public void setTimems(long timems) {
		this.timems = timems;
	}


	public String getServiceID() {
		return serviceID;
	}


	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}


	public Timestamp getValueTime() {
		return valueTime;
	}


	public void setValueTime(Timestamp valueTime) {
		this.valueTime = valueTime;
	}


	public String getWorkerID() {
		return workerID;
	}
	public void setWorkerID(String workerID) {
		this.workerID = workerID;
	}
	public int getRoundID() {
		return roundID;
	}
	public void setRoundID(int roundID) {
		this.roundID = roundID;
	}
	
	
	public String genTime(){
		String timeFormat = null;
		timeFormat = type + "." + String.valueOf(workerID) + "." + String.valueOf(roundID) + " = " + valueTime ;
		return timeFormat;
		
	}
	

}
