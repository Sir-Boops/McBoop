package me.boops.functions;

import org.json.JSONArray;
import org.json.JSONObject;

import me.boops.net.GetURL;

public class ListAllVersions {
	public ListAllVersions() throws Exception {
		System.out.println("Here is a list of versions you can play");
		JSONArray versionList = new JSONObject(new GetURL().get("https://launchermeta.mojang.com/mc/game/version_manifest.json")).getJSONArray("versions");
		for(int i = 0; i < versionList.length(); i++) {
			System.out.println(versionList.getJSONObject(i).getString("id"));
		}
	}
}
