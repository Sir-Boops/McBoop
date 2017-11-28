package me.boops.functions;

import org.json.JSONObject;

import me.boops.net.GetURL;

public class CheckRelease {
	public CheckRelease() throws Exception {
		JSONObject versions = new JSONObject(new GetURL().get("https://launchermeta.mojang.com/mc/game/version_manifest.json"));
		System.out.println("Current release version is: " + versions.getJSONObject("latest").getString("release"));
		System.exit(0);
	}
}
