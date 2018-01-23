package me.boops.functions.userhandler;

import org.json.JSONArray;
import org.json.JSONObject;

import me.boops.functions.file.WriteTextToFile;
import me.boops.functions.network.MojangAuth;

public class GetAccount {
	public String[] get(String dirS, JSONArray authFile, String[] args) {
		
		String[] ans = new String[] {};
		String username = "";
		
		String accessToken = "";
		String clientToken = "";
		
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("--account")) {
				username = args[i + 1];
			}
		}
		
		if(username.isEmpty()) {
			for(int i = 0; i < authFile.length(); i++) {
				if(authFile.getJSONObject(i).getBoolean("default")) {
					username = authFile.getJSONObject(i).getString("username");
				}
			}
		}
		
		for(int i = 0; i < authFile.length(); i++) {
			if(authFile.getJSONObject(i).getString("username").equalsIgnoreCase(username)) {
				accessToken = authFile.getJSONObject(i).getString("accessToken");
				clientToken = authFile.getJSONObject(i).getString("clientToken");
				i = (authFile.length() + 1);
			}
		}
		
		JSONObject auth = new MojangAuth().refresh(accessToken, clientToken);
		
		for(int i = 0; i < authFile.length(); i++) {
			if(authFile.getJSONObject(i).getString("username").equalsIgnoreCase(username)) {
				authFile.getJSONObject(i).put("accessToken", auth.getString("accessToken"));
				authFile.getJSONObject(i).put("clientToken", auth.getString("clientToken"));
				new WriteTextToFile(dirS + "auth.json", authFile.toString());
				i = (authFile.length() + 1);
			}
		}
		
		ans = new String[] {auth.getString("accessToken"), auth.getString("clientToken"), auth.getJSONObject("selectedProfile").getString("id"), 
				auth.getJSONObject("selectedProfile").getString("name")};
		return ans;
	}
}
