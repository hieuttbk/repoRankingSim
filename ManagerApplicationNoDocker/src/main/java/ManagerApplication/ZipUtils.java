package ManagerApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;



public class ZipUtils {
	private ZipUtils(){}
	
	private static final String dataDir = ConfigVar.DATADIR;
	
	
	public static void createFile (String path){
		try {
		      File myObj = new File(path);
		      if (myObj.createNewFile()) {
		        System.out.println("File created: " + myObj.getName());
		      } else {
		        System.out.println("File already exists.");
		      }
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }		  
	}
	
	public static void writeFile(String path ,String result){
		 try {
		      FileWriter myWriter = new FileWriter(path,true);
		      myWriter.append(result);
		      myWriter.close();
		    //  System.out.println("Successfully wrote to the file.");
		    } catch (IOException e) {
		    //  System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
	
	
	
	
	
	
	public static void ZipData(String ratioImages, String serviceId) throws IOException{
		int startImage = Integer.valueOf(ratioImages.split("-")[0]);
		int endImage = Integer.valueOf(ratioImages.split("-")[1]);
		List<String> images = new ArrayList<String>();
		for(int i = startImage; i <= endImage; i++){
			images.add(dataDir + "/image"+i+".jpg");
		}
		
		String dataName = "image"+startImage+"-"+endImage+"_"+serviceId;
		
		FileOutputStream fos = new FileOutputStream(dataDir + "/"+ dataName +".zip");
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		
		for (String img : images){
			File imgToZip = new File(img);
			FileInputStream fis = new FileInputStream(imgToZip);
			ZipEntry zipEntry = new ZipEntry(imgToZip.getName());
			zipOut.putNextEntry(zipEntry);
			
			byte[] bytes = new byte[1024];
			int length;
			while((length = fis.read(bytes)) >=0){
				zipOut.write(bytes, 0, length);
			}
			fis.close();
			
		}
		zipOut.close();
		fos.close();
		
		encodeDataAndPush(dataName);
	}
	
	public static void UnzipData(String ratioImages, String serviceId) throws IOException{
		System.out.println("Unzipping data");
		String zipFile = dataDir+"/image"+ratioImages+"_"+serviceId+".zip";
		
		new File(dataDir+"/"+serviceId).mkdirs(); // make folder named serviceID
		
		File destDir = new File(dataDir+"/"+serviceId);
		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry zipEntry = zis.getNextEntry();
		
		while(zipEntry != null){
			File newFile = newFile(destDir, zipEntry);
			FileOutputStream fos = new FileOutputStream(newFile);
			int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
		
	}
	
	//used to support Unzip Method 
	private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
         
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        //System.out.println(destDirPath);
        //System.out.println(destFilePath);
         
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
         
        return destFile;
    }
	
	public static void encodeDataAndPush(String dataName){
		
		String encodedString = "";
		try {		
			byte[] fileContent = FileUtils.readFileToByteArray(new File(dataDir + "/" + dataName+".zip"));
			encodedString = Base64.getEncoder().encodeToString(fileContent);
			System.out.println(encodedString.length());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(encodedString);
		JSONObject obj = new JSONObject();
		obj.put("rn", dataName);
		obj.put("cnf", "application/text");
		obj.put("con", encodedString);
		JSONObject resource = new JSONObject();
		resource.put("m2m:cin", obj);
		RestHttpClient.post(ConfigVar.ORIGINATOR, ConfigVar.MANCSEPOA + "/~/" 
							+ ConfigVar.MANCSEID + "/" + ConfigVar.MANCSENAME + "/DATA" 
							, resource.toString(), 4);
	}
	
}
