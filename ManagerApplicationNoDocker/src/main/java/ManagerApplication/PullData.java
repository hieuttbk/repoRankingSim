package ManagerApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

public class PullData {
	
	
	public static long usingDiscovery(String serviceId, String dataSource, String ratioImages) throws IOException{
		Date now = (Calendar.getInstance()).getTime();
		Timestamp tsPull = new Timestamp(now.getTime());
		String dataName = "image"+ratioImages+"_"+serviceId;
		String target = ConfigVar.MANCSEPOA + "/~/" + dataSource ;
		
		//PULL 
		HttpResponse response = new HttpResponse();
		response = RestHttpClient.get(ConfigVar.ORIGINATOR, target + "/DATA/"+dataName+"?con");
		System.out.println("Sending request for Data:" + target + dataName);
		JSONObject json = new JSONObject(response.getBody());
		JSONObject encodedCon = json.getJSONObject("m2m:cin");
		String encodedString = encodedCon.getString("con");
	
		
		String outputFilePath = ConfigVar.DATADIR + "/" + dataName + ".zip";
		File outputFile = new File(outputFilePath);
		byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
		FileUtils.writeByteArrayToFile(outputFile, decodedBytes);
		
		now = (Calendar.getInstance()).getTime();
		return (new Timestamp(now.getTime())).getTime() - tsPull.getTime();
	}
	
	
	
		public static long usingSFTP(String serviceName, String dataSource,
									String serviceId, int startImage, int endImage){
			//dataSource is an IP, need user name:
			//if manager is PC: userName@dataSource
			//String source = "namnguyen@"+dataSource+":/home/namnguyen/data/origin_image/camera.jpg"; 
			String folderCut = "/origin_image";
			String folderDetect = "/cut_image";
			String destinationCut = "/home/namnguyen/data" + folderCut + "/image";
			String destinationDetect = "/home/namnguyen/data" + folderDetect + "/image";
			
			//if source is PI
			String sourceCut = "pi@"+dataSource+":/home/pi/data" + folderCut + "/image";
			String sourceDetect = "pi@"+dataSource+":/home/pi/data" + folderDetect + "/image";
			
			Date now = (Calendar.getInstance()).getTime();
			Timestamp tsPull = new Timestamp(now.getTime());
			//String destination = "/home/pi/data/origin_image";
			int i = endImage;
			while(i >= startImage){
				
				String commandPullCut = "sftp " + sourceCut + i+".jpg" + " " + destinationCut + i +"_" + serviceId+".jpg";
				String commandPullDetect = "sftp " + sourceDetect + i +"_" + serviceId+".jpg" + " " + 
													destinationDetect + i +"_" + serviceId+".jpg" ;
				try {
					Process proc;
					if(serviceName.equals("CutImage")){
						proc = Runtime.getRuntime().exec(new String[] {"/bin/bash","-c",commandPullCut});
						System.out.println(commandPullCut);
					}
					else{
						proc = Runtime.getRuntime().exec(new String[] {"/bin/bash","-c",commandPullDetect});
						System.out.println(commandPullDetect);
					}
					Boolean successful = proc.waitFor()==0 && proc.exitValue()==0;
					System.out.println("data pull successful"+successful);
					
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
					
					String line = null;
					System.out.println("**************");
					while ((line = stdInput.readLine()) != null) {
						System.out.println(line);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				i--;
			}
			now = (Calendar.getInstance()).getTime();
			long delta = (new Timestamp(now.getTime())).getTime() - tsPull.getTime();
			return delta;
			
		}
		
		
		public static long usingSCP(String serviceId, String dataSource, String ratioImages){
			
			
			
			String destinationDetect = "/home/namnguyen/data/cut_image";
			
			
			String sourceDetect = "pi@"+dataSource+":/home/pi/data/cut_image";
			
			
			
			Date now = (Calendar.getInstance()).getTime();
			Timestamp tsPull = new Timestamp(now.getTime());
			Boolean successful = false;
			while(!successful){
				String commandPullDetect = "scp " + sourceDetect + "/image"  + ratioImages+"_"+serviceId+ ".zip" + " "
													+ destinationDetect; 
				try {
					Process proc;
					
					proc = Runtime.getRuntime().exec(new String[] {"/bin/bash","-c",commandPullDetect});
					//System.out.println(commandPullDetect);
					
					successful = proc.waitFor()==0 && proc.exitValue()==0;
					System.out.println("data pull for service " + serviceId  + "successful: "+successful);
					
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
					
					String line = null;
					System.out.println("**************");
					while ((line = stdInput.readLine()) != null) {
						System.out.println(line);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			System.out.println("Done pulling data");
			now = (Calendar.getInstance()).getTime();
			long delta = (new Timestamp(now.getTime())).getTime() - tsPull.getTime();
			return delta;
			
		}
}
