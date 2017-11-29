package me.boops.checks;

import org.json.JSONArray;
import org.json.JSONObject;

import me.boops.net.GetURL;

public class GetVersions {
	public String getReleaseVersion() throws Exception {
		JSONObject versions = new JSONObject(new GetURL().get("https://launchermeta.mojang.com/mc/game/version_manifest.json"));
		return versions.getJSONObject("latest").getString("release");
	}
	
	public String getSnapShotVersion() throws Exception {
		JSONObject versions = new JSONObject(new GetURL().get("https://launchermeta.mojang.com/mc/game/version_manifest.json"));
		return versions.getJSONObject("latest").getString("snapshot");
	}
	
	public JSONArray getAllVersions() throws Exception {
		return new JSONObject(new GetURL().get("https://launchermeta.mojang.com/mc/game/version_manifest.json")).getJSONArray("versions");
	}
}
