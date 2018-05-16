package me.boops.functions.forgehandler;

import java.io.File;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.file.ReadTextFromFile;
import me.boops.functions.file.WriteTextToFile;
import me.boops.functions.network.FetchRemoteContent;

public class ForgeJSONMaster {
	
	private String file_path = (Main.homeDir + "forge" + File.separator);
	
	public JSONObject json() {
		
		JSONObject ans = new JSONObject();
		
		// Check if master.json is present
		if(new File(this.file_path + "master.json").exists()) {
			// The master json is there now check for the master_cache.json file
			if(new File(this.file_path + "master_cache.json").exists()) {
				// And we have the cache file!
				
				// Read the cache file and see if we need
				// To update or if we can just serve the cached version
				long current_time = System.currentTimeMillis() / 1000;
				long cache_time = new JSONObject(new ReadTextFromFile().read(this.file_path + "master_cache.json")).getLong("last_update") + 3600;
				
				// If cache is older then 1 hour refresh it
				if(current_time < cache_time) {
					// Cache is still young so use it
					ans = new JSONObject(new ReadTextFromFile().read(this.file_path + "master.json"));
				} else {
					ans = reloadCache();
				}
			} else {
				ans = reloadCache();
			}
		} else {
			ans = reloadCache();
		}
		return ans;
	}
	
	private JSONObject reloadCache() {
		
		// Fetch the forge master json
		JSONObject master_list = new JSONObject(new FetchRemoteContent().text("https://files.minecraftforge.net/maven/net/minecraftforge/forge/json"));
		
		// Write the master json to file
		new WriteTextToFile(this.file_path + "master.json", master_list.toString());
		
		// Create the master_cache.json file
		JSONObject master_cache = new JSONObject();
		long current_time = System.currentTimeMillis() / 1000;
		master_cache.put("last_update", current_time);
		new WriteTextToFile(this.file_path + "master_cache.json", master_cache.toString());
		
		
		return master_list;
	}
}
