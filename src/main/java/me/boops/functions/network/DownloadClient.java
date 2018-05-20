package me.boops.functions.network;

import java.io.File;

import me.boops.Main;
import me.boops.functions.VersionMeta;

public class DownloadClient {
	
	static public String jarPath = (Main.home_dir + "versions" + File.separator + VersionMeta.ID + ".jar");
	
	public DownloadClient() {
		
		System.out.println("Attempting to download/verify client jar");
		
		// Check if the client jar exists
		// If it does do nothing else download
		// a copy
		
		if(!new File(Main.home_dir + "versions" + File.separator + VersionMeta.ID + ".jar").exists()) {
			System.out.println("Downloading " + VersionMeta.ID + ".jar");
			new FetchRemoteContent().file(VersionMeta.Meta.getJSONObject("downloads").getJSONObject("client").getString("url"),
					Main.home_dir + "versions" + File.separator, VersionMeta.ID + ".jar");
			
		}
		
		System.out.println("Client jar has been downloaded/verfiyed!");
	}	
}
