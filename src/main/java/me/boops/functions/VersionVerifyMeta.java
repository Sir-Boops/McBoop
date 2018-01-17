package me.boops.functions;

import org.json.JSONObject;

import me.boops.functions.network.FetchRemoteText;

public class VersionVerifyMeta {
	
	public String getMeta(String[] args) {
		
		// Grab the meta file
		JSONObject metaFile = new JSONObject(new FetchRemoteText().fetch("https://launchermeta.mojang.com/mc/game/version_manifest.json"));
		
		String mcVersion = "";
		
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("--run")) {
				mcVersion = args[i + 1];
				i =(args.length + 1);
			}
		}
		
		String versionMeta = "";
		
		for(int i = 0; i < metaFile.getJSONArray("versions").length(); i++) {
			if(metaFile.getJSONArray("versions").getJSONObject(i).getString("id").equalsIgnoreCase(mcVersion)) {
				versionMeta = metaFile.getJSONArray("versions").getJSONObject(i).getString("url");
			}
		}
		
		if(versionMeta.isEmpty()) {
			System.out.println("Could not find requested version!");
			System.exit(1);
		}
		
		
		return versionMeta;
	}
}
