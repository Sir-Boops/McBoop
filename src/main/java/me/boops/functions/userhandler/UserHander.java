package me.boops.functions.userhandler;

import org.json.JSONArray;

import me.boops.functions.file.LoadAuthFile;

public class UserHander {
	
	public String[] getUser(String dirS, String[] args) {
		
		// Load the auth file
		JSONArray authFile = new LoadAuthFile().load(dirS);
		String[] ans = new String[] {};
		
		// See what the user wants to do
		for(int i = 0; i < args.length; i++) {
			
			// If listing accounts!
			if(args[i].equalsIgnoreCase("--list-accounts")) {
				new ListAllAccounts(authFile);
				System.exit(0);
			}
			
			if(args[i].equalsIgnoreCase("--add-account")) {
				new AppendAccount(dirS, authFile, args);
				System.exit(0);
			}
			
			if(args[i].equalsIgnoreCase("--set-default")) {
				new SetDefault(args, dirS, authFile);
				System.exit(0);
			}
			
		}
		
		// Grab the account to use!
		ans = new GetAccount().get(dirS, authFile, args);
		System.out.println("Logged in as: " + ans[3]);
		return ans;
		
	}
}
