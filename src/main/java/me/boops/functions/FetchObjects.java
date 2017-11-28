package me.boops.functions;

import java.io.File;
import java.util.Iterator;

import org.json.JSONObject;

import me.boops.Config;

public class FetchObjects {
	public void Download(JSONObject assetsRaw) throws Exception {
		
		String assetsDir = (Config.rootDir + "assets/");
		JSONObject assets = assetsRaw.getJSONObject("objects");
		
		// Download all the objects
		if (!new File(assetsDir + "objects/").exists()) {
			new File(assetsDir + "objects/").mkdir();
		}
		
		Iterator<?> keys = assets.keys();
		// Convert to a JSONArray
		while (keys.hasNext()) {
			String key = (String) keys.next();
			System.out.println("Downloading asset: " + key.split("/")[key.split("/").length - 1]);
			char[] hashArray = assets.getJSONObject(key).getString("hash").toCharArray();
			String firstTwo = String.valueOf(hashArray[0]) + String.valueOf(hashArray[1]);
			String name = assets.getJSONObject(key).getString("hash");
			new DownloadFile().Download(assetsDir + "objects/" + firstTwo + "/", "https://resources.download.minecraft.net/" + firstTwo + "/" + name, name);
		}

	}
}
