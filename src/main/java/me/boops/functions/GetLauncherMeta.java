package me.boops.functions;

import org.json.JSONObject;

import me.boops.base.Config;
import me.boops.net.GetURL;

public class GetLauncherMeta {
	public JSONObject get() throws Exception {
		
		// Get the version list
		JSONObject versionList = new JSONObject(new GetURL().get("https://launchermeta.mojang.com/mc/game/version_manifest.json"));
		
		String metaURL = "";
		
		// Find the requested version url in the list
		for(int i = 0; i < versionList.getJSONArray("versions").length(); i++) {
			if(versionList.getJSONArray("versions").getJSONObject(i).getString("id").equalsIgnoreCase(Config.runVersion)) {
				metaURL = versionList.getJSONArray("versions").getJSONObject(i).getString("url");
			}
		}
		
		// Check to make sure we found a version
		if(metaURL.isEmpty()) {
			System.out.println("Could not find requested version!");
			System.exit(0);
		}
		
		// Get the launchermeta json
		JSONObject meta = new JSONObject(new GetURL().get(metaURL));
		
		return meta;
	}
}
