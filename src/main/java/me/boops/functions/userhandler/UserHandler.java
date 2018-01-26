package me.boops.functions.userhandler;

import org.json.JSONArray;

import me.boops.Main;
import me.boops.functions.file.LoadAuthFile;

public class UserHandler {
	
	// 0 => accessToken
	// 1 => clientToken
	// 2 => UUID
	// 3 => username
	public static String[] user = {};
	
	public UserHandler() {
		
		// Load the auth file
		JSONArray authFile = new LoadAuthFile().load();
		
		// See what the user wants to do
		for(int i = 0; i < Main.args.length; i++) {
			
			// If listing accounts!
			if(Main.args[i].equalsIgnoreCase("--list-accounts")) {
				new ListAllAccounts(authFile);
				System.exit(0);
			}
			
			if(Main.args[i].equalsIgnoreCase("--add-account")) {
				new AppendAccount(authFile, Main.args);
				System.exit(0);
			}
			
			if(Main.args[i].equalsIgnoreCase("--set-default")) {
				new SetDefault(Main.args, authFile);
				System.exit(0);
			}
			
			if(Main.args[i].equalsIgnoreCase("--remove-account")) {
				new RemoveAccount(authFile, Main.args[i + 1]);
				System.exit(0);
			}
		}
		
		// Grab the account to use!
		user = new GetAccount().get(authFile, Main.args);
		System.out.println("Logged in as: " + UserHandler.user[3]);
		
	}
}
