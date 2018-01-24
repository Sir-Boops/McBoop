package me.boops.functions;

import java.io.File;

import me.boops.Main;
import me.boops.functions.network.FetchRemoteFile;

public class DownloadClient {
	
	static public String jarPath = (Main.homeDir + "versions" + File.separator + VersionMeta.ID + ".jar");
	
	public DownloadClient() {
		
		// Check if the client jar exists
		// If it does do nothing else download
		// a copy
		
		if(!new File(Main.homeDir + "versions" + File.separator + VersionMeta.ID + ".jar").exists()) {
			System.out.println("Downloading " + VersionMeta.ID + ".jar");
			new FetchRemoteFile(VersionMeta.Meta.getJSONObject("downloads").getJSONObject("client").getString("url"),
					Main.homeDir + "versions" + File.separator, VersionMeta.ID + ".jar");
			
		}
	}	
}
