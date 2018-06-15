package me.boops.functions.userhandler;

import org.json.JSONArray;

import me.boops.functions.file.LoadAuthFile;

public class UserHandler {
	
	public String[] user(String[] launcher_args) {
		
		// Load the auth file
		JSONArray authFile = new LoadAuthFile().load();
		
		// See what the user wants to do
		for(int i = 0; i < launcher_args.length; i++) {
			
			// If listing accounts!
			if(launcher_args[i].equalsIgnoreCase("--list-accounts")) {
				new ListAllAccounts(authFile);
				System.exit(0);
			}
			
			if(launcher_args[i].equalsIgnoreCase("--add-account")) {
				new AppendAccount(authFile, launcher_args);
				System.exit(0);
			}
			
			if(launcher_args[i].equalsIgnoreCase("--set-default")) {
				new SetDefault(launcher_args, authFile);
				System.exit(0);
			}
			
			if(launcher_args[i].equalsIgnoreCase("--remove-account")) {
				new RemoveAccount(authFile, launcher_args[i + 1]);
				System.exit(0);
			}
		}
		
		// Grab the account to use!
		String[] user = new GetAccount().get(authFile, launcher_args);
		System.out.println("Logged in as: " + user[3]);
		return user;
	}
}
