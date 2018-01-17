package me.boops.functions;

import java.io.File;

import org.json.JSONObject;

import me.boops.functions.network.FetchRemoteFile;

public class DownloadClient {
	
	public DownloadClient(JSONObject downloads, String dirS, String version) {
		
		if(!new File(dirS + "versions" + File.separator + version + ".jar").exists()) {
			System.out.println("Downloading " + version + ".jar");
			new FetchRemoteFile(downloads.getJSONObject("client").getString("url"), dirS + "versions" + File.separator, version + ".jar");
			
		}
	}	
}
