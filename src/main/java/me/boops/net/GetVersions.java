package me.boops.net;

import org.json.JSONArray;
import org.json.JSONObject;

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
	
	public JSONObject getAllVersionsRaw() throws Exception {
		return new JSONObject(new GetURL().get("https://launchermeta.mojang.com/mc/game/version_manifest.json"));
	}
}
