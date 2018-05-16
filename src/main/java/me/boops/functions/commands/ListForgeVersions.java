package me.boops.functions.commands;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import me.boops.functions.network.FetchRemoteContent;

public class ListForgeVersions {

	public ListForgeVersions(String[] args) {

		JSONObject forgeJSON = new JSONObject(new FetchRemoteContent().text("https://files.minecraftforge.net/maven/net/minecraftforge/forge/json"));

		String reqVersion = "";

		if(args.length == 2) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equalsIgnoreCase("--list-forge-versions")) {
					reqVersion = args[i + 1];
				}
			}
		}

		if (reqVersion.isEmpty()) {
			printVersions(forgeJSON);
		} else {
			printReqVersion(forgeJSON, reqVersion);
		}

	}

	private void printVersions(JSONObject forgeJSON) {
		
		Iterator<?> keys = forgeJSON.getJSONObject("mcversion").keys();
		
		System.out.println("");
		System.out.println("Here are a list of forge versions you can run:");
		System.out.println("");

		while (keys.hasNext()) {

			String key = (String) keys.next();
			System.out.println(key);

		}
		
		System.out.println("");
		System.out.println("");
	}
	
	private void printReqVersion(JSONObject forgeJSON, String version) {
		
		JSONArray buildArr = forgeJSON.getJSONObject("mcversion").getJSONArray(version);
		
		System.out.println("");
		System.out.println("Here are a list of forge versions for " + version + " you can run:");
		System.out.println("");
		
		for(int i = 0; i < buildArr.length(); i++) {
			
			System.out.println(forgeJSON.getJSONObject("number").getJSONObject(String.valueOf(buildArr.getInt(i))).getString("version"));
			
		}
		System.out.println("");
	}
}
