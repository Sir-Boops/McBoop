package me.boops.functions.userhandler;

import org.json.JSONArray;

import me.boops.Main;
import me.boops.functions.file.WriteTextToFile;

public class SetDefault {
	
	public SetDefault (String[] args, JSONArray authFile) {
		
		String username = "";
		
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("--set-default") && (args.length == 2)) {
				username = args[i + 1];
				i = (args.length + 1);
			} else {
				System.out.println("Did you add an uneeded space?");
				return;
			}
		}
		
		for (int i = 0; i < authFile.length(); i++) {
			if(authFile.getJSONObject(i).getString("username").equalsIgnoreCase(username)) {
				authFile.getJSONObject(i).put("default", true);
			} else {
				authFile.getJSONObject(i).put("default", false);
			}
		}
		
		new WriteTextToFile(Main.home_dir + "auth.json", authFile.toString());
		System.out.println("Set the default account to " + username);
	}
}
