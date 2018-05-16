package me.boops.functions.network;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import me.boops.Main;
import me.boops.functions.file.CreateFolder;

public class FetchRemoteContent {

	// If downloading a file call this
	public void file(String URL, String destDir, String fileName) {

		if (fileName.isEmpty()) {
			fileName = URL.substring(URL.lastIndexOf("/") + 1, URL.length());
		}
		new CreateFolder(destDir);
		fetch(URL, destDir, fileName);
	}
	
	// If reading text call this
	public String text(String URL) {
		return fetch(URL, "", "");
	}
	
	// If you need to know if it failed call this
	public boolean fileStatus(String URL, String destDir, String fileName) {

		if (fileName.isEmpty()) {
			fileName = URL.substring(URL.lastIndexOf("/") + 1, URL.length());
		}
		new CreateFolder(destDir);
		String status = fetch(URL, destDir, fileName);
		
		boolean ans = false;
		
		if(!status.isEmpty()) {
			ans = true;
		}
		return ans;
	}
	
	// Internal downloader
	private String fetch(String URL, String dist_dir, String file_name) {
		
		String ans = "";
		
		try {

			// Turn the URL into a URL
			URL url = new URL(URL);

			// Set the client options
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10 * 1000);
			conn.setConnectTimeout(10 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", Main.HttpUser);

			// Connect to the remote server
			conn.connect();

			// Open all the needed streams
			InputStream is = conn.getInputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			// If we are downloading text don't open it just define
			FileOutputStream fos = null;
			if(!dist_dir.isEmpty() && !file_name.isEmpty()) {
				fos = new FileOutputStream(new File(dist_dir + file_name));
			}

			// Copy the content from the remote server
			int inByte;
			while ((inByte = is.read()) != -1) {
				bos.write(inByte);
			}

			// If we are writing to a file do that now
			if(!dist_dir.isEmpty() && !file_name.isEmpty()) {
				fos.write(bos.toByteArray());
				fos.close();
			}
			
			// If we are reading text return that to ans here
			if(dist_dir.isEmpty() && file_name.isEmpty()) {
				ans = new String(bos.toByteArray());
			}

			is.close();
			bos.close();

		} catch (Exception e) {
			ans = "true";
		}
		
		return ans;
		
	}

}
