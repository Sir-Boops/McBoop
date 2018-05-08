package me.boops.functions.threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import me.boops.Main;
import me.boops.functions.file.CreateFolder;

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
				"https://libraries.minecraft.net/" + this.file_path + this.file_name,
				"https://repo.spongepowered.org/maven/" + this.file_path + this.file_name,
				"http://central.maven.org/maven2/" + this.file_path + this.file_name};
	}
	
	@Override
	public void run() {
		
		new CreateFolder(Main.homeDir + "libraries" + File.separator + this.file_path);
		
		// Check to see if we have the lib already!
		if(!new File(this.full_path).exists()) {
			
			System.out.println("Downloading: " + this.file_name);
			
			boolean got_file = false;
			int attempts = 0;
			
			// While we have tried less then 3 times and still have not found the file
			while(!got_file && attempts < 3) {
				got_file = downloadFile(this.urls[attempts]);
				attempts++;
			}
		}
	}
	
	private boolean downloadFile(String url) {
		boolean ans = true;
		
		try {
			
			URL URL = new URL(url);
			
			HttpURLConnection conn = (HttpURLConnection) URL.openConnection();
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
}
