package me.boops.functions.commands;

import org.json.JSONObject;

import me.boops.functions.network.FetchRemoteText;

public class CommandListAllVersions {
	
	public CommandListAllVersions() {
		
		System.out.println("Here is a list of versions you can run");
		
		JSONObject versionList = new JSONObject(new FetchRemoteText().fetch("https://launchermeta.mojang.com/mc/game/version_manifest.json"));
		
		for(int i = 0; i < versionList.getJSONArray("versions").length(); i++) {
			System.out.println(versionList.getJSONArray("versions").getJSONObject(i).getString("id"));
		}
		
		System.out.println("");
		System.out.println("Current stable is: " + versionList.getJSONObject("latest").getString("release"));
		System.out.println("Current snapshot is: " + versionList.getJSONObject("latest").getString("snapshot"));
		System.out.println("");
		System.out.println("");
		
	}
}
