package me.boops.functions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import me.boops.functions.file.CreateFolder;

public class DownloadClient {
	
	public DownloadClient(JSONObject downloads, String dirS, String version) {
		
		if(!new File(dirS + "versions" + File.separator + version + ".jar").exists()) {
			
			System.out.println("Downloading " + version + ".jar");
			downloadFile(downloads.getJSONObject("client").getString("url"), dirS + "versions" + File.separator, version + ".jar");
			
		}
	}
	
private void downloadFile(String URL, String destDir, String fileName) {
		
		new CreateFolder(destDir);
		
		try {
			
			URL url = new URL(URL);
			
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10 * 1000);
			conn.setConnectTimeout(10 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux X11; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
			
			conn.connect();
			
			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(new File(destDir + fileName));
			int inByte;
			
			while ((inByte = is.read()) != -1) {
				fos.write(inByte);
			}
			
			is.close();
			fos.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
