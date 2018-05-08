package me.boops.functions;

import java.io.File;
import java.util.Iterator;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.file.CreateFolder;
import me.boops.functions.file.WriteTextToFile;
import me.boops.functions.network.FetchRemoteText;

public class InstallAssets {
	
	public static String path = (Main.homeDir + "assets" + File.separator);

	public InstallAssets() {
		
		System.out.println("Installing/Verifying assets");
		
		JSONObject assetIndex = VersionMeta.Meta.getJSONObject("assetIndex");
		JSONObject assetList = new JSONObject(new FetchRemoteText().fetch(assetIndex.getString("url")));
		
		new CreateFolder(Main.homeDir + "versions");
		new WriteTextToFile(Main.homeDir + "assets" + File.separator + "indexes" + File.separatorChar + assetIndex.getString("id") + ".json", assetList.toString());
		
		downloadLibs(assetList.getJSONObject("objects"), (Main.homeDir + "assets" + File.separator + "objects" + File.separator));
		
		System.out.println("All assets verifyed/downloaded");
		
	}
	
	private void downloadLibs(JSONObject list, String dirS) {
		
		Iterator<?> keys = list.keys();
		ThreadGroup DLGroup = new ThreadGroup("DLGroup");
		
		while(keys.hasNext()) {
			
			// Cap out at 10 threads
			while(DLGroup.activeCount() > 10) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// Create and start the new thread
			String key = (String) keys.next();
			Thread thread = new Thread(DLGroup, new DownloadAssetsThread(key, list.getJSONObject(key).getString("hash"), dirS));
			thread.start();
		}
		
		// Wait for all threads to finish!
		while(DLGroup.activeCount() > 0) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
