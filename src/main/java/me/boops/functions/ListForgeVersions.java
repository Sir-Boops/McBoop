package me.boops.functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.boops.Cache;
import me.boops.net.GetURL;

public class ListForgeVersions {
	public void listVersions() throws Exception {

		JSONObject forgeVersions = new JSONObject(new GetURL().get("https://files.minecraftforge.net/maven/net/minecraftforge/forge/json"));

		if (forgeVersions.getJSONObject("mcversion").has(Cache.runVersion)) {
			System.out.println("Here is a list of forge versions for minecraft " + Cache.runVersion);
			JSONArray versions = forgeVersions.getJSONObject("mcversion").getJSONArray(Cache.runVersion);
			for (int i = 0; i < versions.length(); i++) {
				System.out.println(forgeVersions.getJSONObject("number").getJSONObject(String.valueOf(versions.getInt(i))).getString("version"));
			}
			if (forgeVersions.getJSONObject("promos").has(Cache.runVersion + "-latest")) {
				System.out.println("Latest version: " + forgeVersions.getJSONObject("number").getJSONObject(String.valueOf(forgeVersions.getJSONObject("promos").getInt(Cache.runVersion + "-latest"))).getString("version"));
			}
			if (forgeVersions.getJSONObject("promos").has(Cache.runVersion + "-recommended")) {
				System.out.println("Recommended version: " + forgeVersions.getJSONObject("number").getJSONObject(String.valueOf(forgeVersions.getJSONObject("promos").getInt(Cache.runVersion + "-recommended"))).getString("version"));
			}
		} else {
			System.out.println("Could not find forge versions for version: " + Cache.runVersion);
		}
	}
	
	public String getStableVersion() throws JSONException, Exception {
		JSONObject forgeVersions = new JSONObject(new GetURL().get("https://files.minecraftforge.net/maven/net/minecraftforge/forge/json"));
		return forgeVersions.getJSONObject("number").getJSONObject(String.valueOf(forgeVersions.getJSONObject("promos").getInt(Cache.runVersion + "-recommended"))).getString("version");
	}
	
	public String getLatestVersion() throws JSONException, Exception {
		JSONObject forgeVersions = new JSONObject(new GetURL().get("https://files.minecraftforge.net/maven/net/minecraftforge/forge/json"));
		return forgeVersions.getJSONObject("number").getJSONObject(String.valueOf(forgeVersions.getJSONObject("promos").getInt(Cache.runVersion + "-latest"))).getString("version");
	}
	
}
