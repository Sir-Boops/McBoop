package me.boops.functions.userhandler;

import org.json.JSONArray;

import me.boops.Main;
import me.boops.functions.file.WriteTextToFile;

public class RemoveAccount {
	
	public RemoveAccount(JSONArray authfile, String name) {
		
		JSONArray ans = new JSONArray();
		
		for(int i = 0; i < authfile.length(); i++) {
			if(!authfile.getJSONObject(i).getString("username").equalsIgnoreCase(name)) {
				ans.put(authfile.get(i));
			} else {
				System.out.println("Removed account: " + authfile.getJSONObject(i).getString("username"));
			}
		}
		
		new WriteTextToFile(Main.home_dir + "auth.json", ans.toString());
		
	}	
}
