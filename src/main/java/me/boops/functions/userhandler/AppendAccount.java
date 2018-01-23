package me.boops.functions.userhandler;

import org.json.JSONArray;
import org.json.JSONObject;

import me.boops.functions.file.WriteTextToFile;
import me.boops.functions.mojangauth.AuthLogin;

public class AppendAccount {
	
	public AppendAccount(String dirS, JSONArray authFile, String[] args) {
		
		String username = "";
		String password = "";
		
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("--add-account") && (args.length == 3)) {
				username = args[i + 1];
				password = args[i + 2];
				i = (args.length + 1);
			} else {
				System.out.println("Did you forget to type your password?");
				return;
			}
		}
		
		String[] auth = new AuthLogin().login(username, password);
		JSONObject entry = new JSONObject().put("clientToken", auth[1]).put("accessToken", auth[0])
				.put("username", auth[3]).put("id", auth[2]);
		
		for(int i = 0; i < authFile.length(); i++) {
			if(authFile.getJSONObject(i).getString("username").equalsIgnoreCase(auth[3])) {
				authFile.remove(i);
				i = (authFile.length() + 1);
			}
		}
		
		if(authFile.length() <= 0) {
			entry.put("default", true);
		} else {
			entry.put("default", false);
		}
		
		authFile.put(entry);
		new WriteTextToFile(dirS + "auth.json", authFile.toString());
		System.out.println("Added the " + auth[3] + " account!");
	}
	
	
}
