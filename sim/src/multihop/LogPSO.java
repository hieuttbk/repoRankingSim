package multihop;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LogPSO {
	
	private final String logFile = "debugPSO.txt";
	private PrintWriter writer;
    private static LogPSO instance;
     
    private LogPSO(){
    	FileWriter fw;
		try {
			fw = new FileWriter(logFile);
	        writer = new PrintWriter(fw, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
     
    public static synchronized LogPSO getInstance(){
        if(instance == null){
            instance = new LogPSO();
        }
        return instance;
    }
    
    public void log(String log) {
    	writer.print(log);
    	System.out.println(log);
    }
    
}