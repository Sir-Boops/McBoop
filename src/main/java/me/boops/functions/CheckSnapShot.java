package me.boops.functions;

import org.json.JSONObject;

import me.boops.net.GetURL;

public class CheckSnapShot {
	public CheckSnapShot() throws Exception {
		JSONObject versions = new JSONObject(new GetURL().get("https://launchermeta.mojang.com/mc/game/version_manifest.json"));
		System.out.println("Current release version is: " + versions.getJSONObject("latest").getString("snapshot"));
		System.exit(0);
	}
}
