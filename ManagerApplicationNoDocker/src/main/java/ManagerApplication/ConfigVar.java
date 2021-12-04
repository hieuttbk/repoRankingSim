package ManagerApplication;

import java.text.SimpleDateFormat;

public class ConfigVar {
	private ConfigVar(){}
	
	public enum TIMETY {TNS, TCD, TRD, TCE, TRE, TRS, TP;} // notifySevice, commandData, commandExec + response...
	
	public static final String ORIGINATOR = "admin:admin";
	public static final String CSEPROTOCOL = "http";

	//public static final String MANCSEIP = "192.168.1.113"; 
	public static final String MANCSEIP = "127.0.0.1"; 
	public static final int MANCSEPORT = 8080;
	public static final String MANCSEID = "in-cse";
	public static final String MANCSENAME = "in-name";

	public static final String AENAME = "ManagerAE";
	public static final String AEPROTOCOL = "http";
	public static final String AEIP = "127.0.0.1"; 
	public static final int AEPORT = 1600;
	public static final String AESUB = "ManagerSub";

	public static final String MANCSEPOA = CSEPROTOCOL + "://" + MANCSEIP + ":"+ MANCSEPORT;
	public static final String APPPOA = AEPROTOCOL+"://"+AEIP+":"+AEPORT;
	public static final String NU = "/"+MANCSEID+"/"+MANCSENAME+"/"+AENAME;
	
	public static int COMMANDID = 0; // for command indexing 
	public static final int DEPLOY = 2;
	public static final int ZIP = 3;
	
	public static final int WORKERS = 3; //range( 1 -> n );
	
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss"); //date format
	
	//public static final String DATADIR = "/home/namnguyen/data/cut_image"; // data storage
	public static final String DATADIR = "D:\\2021\\FL\\namnguyen\\data\\cut_image";
	public static final String DIR = "D:\\2021\\FL\\namnguyen";
	public static final String DIR_R = "D:\\2021\\FL\\namnguyen\\test-MESH\\resultIM-MAN.txt";
	
}
