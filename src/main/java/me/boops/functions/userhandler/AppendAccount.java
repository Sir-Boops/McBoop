package me.boops.functions.userhandler;

import org.json.JSONArray;
import org.json.JSONObject;

import me.boops.functions.file.WriteTextToFile;
import me.boops.functions.network.MojangAuth;

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
		
		JSONObject auth = new MojangAuth().auth(username, password);
		JSONObject entry = new JSONObject().put("clientToken", auth.getString("clientToken")).put("accessToken", auth.getString("accessToken"))
				.put("username", auth.getJSONObject("selectedProfile").getString("name")).put("id", auth.getJSONObject("selectedProfile").getString("id"));
		
		for(int i = 0; i < authFile.length(); i++) {
			if(authFile.getJSONObject(i).getString("username").equalsIgnoreCase(auth.getJSONObject("selectedProfile").getString("name"))) {
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
		System.out.println("Added the " + auth.getJSONObject("selectedProfile").getString("name") + " account!");
	}
	
	
}
