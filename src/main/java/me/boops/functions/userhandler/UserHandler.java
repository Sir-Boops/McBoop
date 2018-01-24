package me.boops.functions.userhandler;

import org.json.JSONArray;

import me.boops.functions.file.LoadAuthFile;

public class UserHandler {
	
	// 0 => accessToken
	// 1 => clientToken
	// 2 => UUID
	// 3 => username
	public static String[] user = {};
	
	public UserHandler(String dirS, String[] args) {
		
		// Load the auth file
		JSONArray authFile = new LoadAuthFile().load(dirS);
		
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
		user = new GetAccount().get(dirS, authFile, args);
		System.out.println("Logged in as: " + UserHandler.user[3]);
		
	}
}
