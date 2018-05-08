package me.boops.functions.userhandler;

import org.json.JSONArray;
import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.file.WriteTextToFile;
import me.boops.functions.mojangauth.AuthLogin;

public class AppendAccount {
	
	public AppendAccount(JSONArray authFile, String[] args) {
		
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
		
		// Check if there is a default already set if not tell the user
		boolean found_default = false;
		for(int i = 0; i < authFile.length(); i++) {
			if(authFile.getJSONObject(i).getBoolean("default")) {
				found_default = true;
			}
		}
		
		// If we don't find a default already in there set this one to be default!
		if(!found_default) { 
			System.out.println("No default account has been found!");
			System.out.println("This account will be set to your default.");
			System.out.println("You can change this later if you wish");
			entry.put("default", true);
			System.out.println("");
		}
		
		authFile.put(entry);
		new WriteTextToFile(Main.homeDir + "auth.json", authFile.toString());
		System.out.println("Added the " + auth[3] + " account!");
	}
	
	
}
