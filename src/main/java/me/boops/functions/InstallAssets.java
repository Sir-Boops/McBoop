package me.boops.functions;

import java.io.File;
import java.util.Iterator;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.file.CreateFolder;
import me.boops.functions.file.WriteTextToFile;
import me.boops.functions.network.FetchRemoteFile;
import me.boops.functions.network.FetchRemoteText;

public class InstallAssets {
	
	public static String path = (Main.homeDir + "assets" + File.separator);

	public InstallAssets() {
		
		JSONObject assetIndex = VersionMeta.Meta.getJSONObject("assetIndex");
		JSONObject assetList = new JSONObject(new FetchRemoteText().fetch(assetIndex.getString("url")));
		
		new CreateFolder(Main.homeDir + "versions");
		new WriteTextToFile(Main.homeDir + "assets" + File.separator + "indexes" + File.separatorChar + assetIndex.getString("id") + ".json", assetList.toString());
		
		downloadLibs(assetList.getJSONObject("objects"), (Main.homeDir + "assets" + File.separator + "objects" + File.separator));

	}
	
	private void downloadLibs(JSONObject list, String dirS) {
		
		Iterator<?> keys = list.keys();
		
		while(keys.hasNext()) {
			
			String key = (String) keys.next();
			String hash = list.getJSONObject(key).getString("hash");
			String firstTwo = hash.substring(0, 2);
			
			String URL = ("https://resources.download.minecraft.net/" + firstTwo + "/" + hash);
			String distDir = (dirS + firstTwo + File.separator);
			
			if(!new File(distDir + hash).exists()) {
				System.out.println("Downloading: " + key.substring(key.lastIndexOf("/") + 1, key.length()));
				new FetchRemoteFile(URL, distDir, "");
			}
		}
	}
}
