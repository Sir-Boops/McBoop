package me.boops.functions.network;

import java.io.File;

import org.json.JSONObject;

import me.boops.Main;

public class DownloadClient {
	
    private String version_id;
	static public String jarPath;
	
	public DownloadClient(String[] meta) {
		
		System.out.println("Attempting to download/verify client jar");
		
		this.version_id = meta[0];
		
		// Check if the client jar exists
		// If it does do nothing else download
		// a copy
		
		if(!new File(Main.home_dir + "versions" + File.separator + this.version_id + ".jar").exists()) {
			System.out.println("Downloading " + this.version_id + ".jar");
			new FetchRemoteContent().file(new JSONObject(meta[1]).getJSONObject("downloads").getJSONObject("client").getString("url"),
					Main.home_dir + "versions" + File.separator, this.version_id + ".jar");
			
		}
		
		DownloadClient.jarPath = (Main.home_dir + "versions" + File.separator + this.version_id + ".jar");
		System.out.println("Client jar has been downloaded/verfiyed!");
	}	
}
