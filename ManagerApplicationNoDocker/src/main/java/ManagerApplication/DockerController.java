package ManagerApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class DockerController {
	private static final DockerController docker = new DockerController();
	
	private DockerController(){}
	
	public static DockerController getInstance(){
		return docker;
	}
		

	//official
	public static void deployService(String csePoa, String cseId, String cseName, 
										String serviceId, String service, int commandIdNumber,
										String ratioImages, long... time) throws Exception {
		int startImage = Integer.valueOf(ratioImages.split("-")[0]);
		int endImage = Integer.valueOf(ratioImages.split("-")[1]);
		String command = service.split("Image")[0];

		if (command.isEmpty()) {
			throw new Exception("Empty Command!");
		}
		if (service == "") {
			throw new Exception("Null Service!");
		}
		String timeCommand = "";
		for (long t : time) {
			timeCommand += t + " ";
		}
		/*String commandDeploy = "docker exec " + service
				+ " python3 /opt/generateCommand.py " + command + " " + csePoa
				+ " " + cseId + " " + cseName + " " +  " " + commandIdNumber + " " 
				+ serviceId + " " + startImage + " " + endImage + " "  
				+ timeCommand;*/
		try {
			String path=ConfigVar.DIR+"\\data\\docker";
			String pathF=path+"\\detect_n_send_2.py";
			
			List<String> commands = new ArrayList<String>();		
	        commands.add("python"); // command
	        commands.add(pathF); // command
		    commands.add(csePoa);
	        commands.add(cseId);
			commands.add(cseName);
			commands.add(String.valueOf(commandIdNumber));
			commands.add(String.valueOf(serviceId));
			commands.add(String.valueOf(startImage));
			commands.add(String.valueOf(endImage));
			commands.add(String.valueOf(timeCommand));

	        // creating the process
	        ProcessBuilder pb = new ProcessBuilder(commands);		

			pb.directory(new File(path));
			System.out.println(commands);
	 
	        // startinf the process
			if(endImage!=0){
	        Process process = pb.start();
			}else{
				System.out.println("No images to process");
			}
			
			
		/*	Process proc = Runtime.getRuntime().exec(commandDeploy);

			Boolean successful = proc.waitFor() == 0 && proc.exitValue() == 0;
			System.out.println("Deploy container for service "+ serviceId +" :" + successful);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));

			String line = null;
			System.out.println("**************");
			while ((line = stdInput.readLine()) != null) {
				System.out.println(line);
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
}

