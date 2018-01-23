package me.boops.functions.userhandler;

import org.json.JSONArray;

public class ListAllAccounts {
	
	
	public ListAllAccounts(JSONArray auth) {
		
		System.out.println("Here is a list of known accounts:");
		
		for(int i = 0; i < auth.length(); i++) {
			if(auth.getJSONObject(i).getBoolean("default")) {
				System.out.println(auth.getJSONObject(i).getString("username") + " - Default!");
			} else {
				System.out.println(auth.getJSONObject(i).getString("username"));
			}
		}
	}
}
