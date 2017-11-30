package me.boops.net;

import java.io.File;
import java.util.Iterator;

import org.json.JSONObject;

import me.boops.base.Cache;

public class FetchObjects {
	public void Download(JSONObject assetsRaw) throws Exception {
		
		String assetsDir = (Cache.rootDir + "assets/");
		JSONObject assets = assetsRaw.getJSONObject("objects");
		
		// Download all the objects
		if (!new File(assetsDir + "objects/").exists()) {
			new File(assetsDir + "objects/").mkdir();
		}
		
		ThreadGroup dlGroup = new ThreadGroup("dlGroup");
		
		Iterator<?> keys = assets.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			System.out.println("Downloading asset: " + key.split("/")[key.split("/").length - 1]);
			char[] hashArray = assets.getJSONObject(key).getString("hash").toCharArray();
			String firstTwo = String.valueOf(hashArray[0]) + String.valueOf(hashArray[1]);
			String name = assets.getJSONObject(key).getString("hash");
			while(dlGroup.activeCount() > 10) {
				Thread.sleep(1000);
			}
			new Thread(dlGroup, new Runnable(){
				public void run() {
					try {
						new DownloadFile().Download(assetsDir + "objects/" + firstTwo + "/", "https://resources.download.minecraft.net/" + firstTwo + "/" + name, name);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		
		// Wait for all downloads to finish
		while(dlGroup.activeCount() > 0) {
			Thread.sleep(1000);
		}
	}
}
