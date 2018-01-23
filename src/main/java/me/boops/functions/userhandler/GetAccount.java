package me.boops.functions.userhandler;

import org.json.JSONArray;

import me.boops.functions.file.WriteTextToFile;
import me.boops.functions.mojangauth.MojangAuth;

public class GetAccount {
	public String[] get(String dirS, JSONArray authFile, String[] args) {
		String username = "";
		
		String accessToken = "";
		String clientToken = "";
		String properName = "";
		String userUUID = "";
		
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
		
		System.out.println("Trying to login as: " + username);
		
		for(int i = 0; i < authFile.length(); i++) {
			if(authFile.getJSONObject(i).getString("username").equalsIgnoreCase(username)) {
				accessToken = authFile.getJSONObject(i).getString("accessToken");
				clientToken = authFile.getJSONObject(i).getString("clientToken");
				properName = authFile.getJSONObject(i).getString("username");
				userUUID = authFile.getJSONObject(i).getString("id");
				i = (authFile.length() + 1);
			}
		}
		
		String[] auth = new MojangAuth().getSession(accessToken, clientToken);
		
		// Check if we should write the token to the auth file
		if(Boolean.getBoolean(auth[2])) {
			for(int i = 0; i < authFile.length(); i++) {
				if(authFile.getJSONObject(i).getString("username").equalsIgnoreCase(username)) {
					authFile.getJSONObject(i).put("accessToken", auth[0]);
					authFile.getJSONObject(i).put("clientToken", auth[1]);
					new WriteTextToFile(dirS + "auth.json", authFile.toString());
					i = (authFile.length() + 1);
				}
			}
		}
		
		return new String[] {auth[0], auth[1], userUUID, properName};
	}
}
