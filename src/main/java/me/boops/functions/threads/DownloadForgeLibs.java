package me.boops.functions.threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.crypto.Sha1SumFile;
import me.boops.functions.file.CreateFolder;
import me.boops.functions.file.ReadTextFromFile;
import me.boops.functions.file.WriteTextToFile;
import me.boops.functions.network.FetchRemoteFile;
import me.boops.functions.network.FetchRemoteText;

public class DownloadForgeLibs implements Runnable {

	private String file_name;
	private String file_path;
	private String full_path;
	private String[] urls;
	
	public DownloadForgeLibs(String file_name, String file_path) {
		this.file_name = file_name;
		this.file_path = file_path;
		this.full_path = (Main.homeDir + "libraries" + File.separator + this.file_path + this.file_name);
		this.urls = new String[] {
				"https://repo1.maven.org/maven2/" + this.file_path + this.file_name,
				"https://repo.spongepowered.org/maven/" + this.file_path + this.file_name,
				"https://libraries.minecraft.net/" + this.file_path + this.file_name};
	}
	
	@Override
	public void run() {
		
		new CreateFolder(Main.homeDir + "libraries" + File.separator + this.file_path);
		
		// Check to see if we have the lib already!
		if(!new File(this.full_path).exists()) {
			
			// If we don't have the file already!
			
			System.out.println("Downloading: " + this.file_name);
			
			boolean got_file = false;
			int attempts = 0;
			
			// While we have tried less then 3 times and still have not found the file
			while(!got_file && attempts < 3) {
				got_file = downloadFile(this.urls[attempts]);
				attempts++;
			}
			
			// At this point we should have the file
			// Get the sha1sum
			String sum = new FetchRemoteText().fetch(this.urls[attempts - 1] + ".sha1");
			
			// Put the sum and the url it was downloaded from into a JSON object to be
			// saved next to the file incase of need to redownload
			JSONObject json_file = new JSONObject();
			json_file.put("url", this.urls[attempts - 1] );
			json_file.put("sum", sum);
			
			// Write the json file
			new WriteTextToFile(this.full_path + ".json", json_file.toString());
			
			// Check the file again!
			checkFile();
			
		} else {
			
			// The file is already there so just verify it
			checkFile();
			
		}
	}
	
	private boolean downloadFile(String url) {
		boolean ans = true;
		
		try {
			
			URL URL = new URL(url);
			
			HttpsURLConnection conn = (HttpsURLConnection) URL.openConnection();
			conn.setReadTimeout(10 * 1000);
			conn.setConnectTimeout(10 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", Main.HttpUser);
			
			conn.connect();
			
			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(new File(this.full_path));
			int inByte;

			while ((inByte = is.read()) != -1) {
				fos.write(inByte);
			}
			
			fos.close();
			is.close();
			
		} catch (Exception e) {
			ans = false;
		}
		
		return ans;
	}
	
	private void checkFile() {
		// Make sure there is a file here!
		if(new File(this.full_path + ".json").exists()) {
			// Load the file json and generate a file sum
			JSONObject json_file = new JSONObject(new ReadTextFromFile().read(this.full_path + ".json"));
			String sum = new Sha1SumFile().sum(this.full_path);
			
			if(!sum.equals(json_file.get("sum"))){
				// The sum does not match
				System.out.println("Found bad lib: " + this.file_name + " Redownloading...");
				reDownload();
			}
		}
	}
	
	private void reDownload() {
		// Load json_file
		JSONObject json_file = new JSONObject(new ReadTextFromFile().read(this.full_path + ".json"));
		
		// Download the file again!
		new FetchRemoteFile(json_file.getString("url"), Main.homeDir + "libraries" + File.separator + this.file_path, "");
		System.out.println(json_file.getString("url"));
		
		// Now sum and check the sum
		String sum = new Sha1SumFile().sum(this.full_path);
		if(!sum.equals(json_file.get("sum"))) {
			// Failed again try again
			reDownload();
		}
		
	}
	
}
