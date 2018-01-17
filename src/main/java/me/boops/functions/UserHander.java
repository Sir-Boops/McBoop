package me.boops.functions;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserHander {
	
	public String[] getUser(String dirS, String[] args) {
		
		// Load the auth file
		JSONArray authFile = new LoadAuthFile().load(dirS);
		String[] ans = new String[] {};
		
		// See what the user wants to do
		for(int i = 0; i < args.length; i++) {
			
			// If listing accounts!
			if(args[i].equalsIgnoreCase("--list-all-accounts")) {
				listAllAccounts(authFile);
				System.exit(0);
			}
			
			if(args[i].equalsIgnoreCase("--add-account")) {
				appendAccount(dirS, authFile, args);
				System.exit(0);
			}
			
			if(args[i].equalsIgnoreCase("--set-default")) {
				setDefault(args, dirS, authFile);
				System.exit(0);
			}
			
			if(args[i].equalsIgnoreCase("--account")) {
				ans = getAccount(args, dirS, authFile);
			}
			
		}
		
		// None were defined so grab the default!
		ans = getDefault(dirS, authFile);
		
		return ans;
		
	}
	
	public void listAllAccounts(JSONArray auth) {
		
		System.out.println("Here is a list of known accounts:");
		
		for(int i = 0; i < auth.length(); i++) {
			if(auth.getJSONObject(i).getBoolean("default")) {
				System.out.println(auth.getJSONObject(i).getString("username") + " - Default!");
			}
		}
		
	}
	
	public void appendAccount(String dirS, JSONArray authFile, String[] args) {
		
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
		}
		
		authFile.put(entry);
		new WriteTextToFile(dirS + "auth.json", authFile.toString());
		System.out.println("Added the " + auth.getJSONObject("selectedProfile").getString("name") + " account!");
	}
	
	public void setDefault (String[] args, String dirS, JSONArray authFile) {
		
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
		
		new WriteTextToFile(dirS + "auth.json", authFile.toString());
		System.out.println("Set the default account to " + username);
	}
	
	public String[] getAccount(String[] args, String dirS, JSONArray authFile) {
		
		String[] ans = new String[] {};
		String username = "";
		
		String accessToken = "";
		String clientToken = "";
		
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("--account") && (args.length == 2)) {
				username = args[i + 1];
				i = (args.length + 1);
			} else {
				System.out.println("Did you add an uneeded space?");
				return ans;
			}
		}
		
		for(int i = 0; i < authFile.length(); i++) {
			if(authFile.getJSONObject(i).getString("username").equalsIgnoreCase(username)) {
				accessToken = authFile.getJSONObject(i).getString("accessToken");
				clientToken = authFile.getJSONObject(i).getString("clientToken");
				i = (authFile.length() + 1);
			}
		}
		
		JSONObject auth = new MojangAuth().refresh(accessToken, clientToken);
		
		for(int i = 0; i < authFile.length(); i++) {
			if(authFile.getJSONObject(i).getString("username").equalsIgnoreCase(username)) {
				authFile.getJSONObject(i).put("accessToken", auth.getString("accessToken"));
				authFile.getJSONObject(i).put("clientToken", auth.getString("clientToken"));
				new WriteTextToFile(dirS + "auth.json", authFile.toString());
				i = (authFile.length() + 1);
			}
		}
		
		ans = new String[] {auth.getString("accessToken"), auth.getString("clientToken"), auth.getJSONObject("selectedProfile").getString("id"), 
				auth.getJSONObject("selectedProfile").getString("name")};
		return ans;
	}
	
	public String[] getDefault(String dirS, JSONArray authFile) {
		
		String[] ans = new String[] {};
		String username = "";
		
		String accessToken = "";
		String clientToken = "";
		
		for(int i = 0; i < authFile.length(); i++) {
			if(authFile.getJSONObject(i).getBoolean("default")) {
				username = authFile.getJSONObject(i).getString("username");
			}
		}
		
		for(int i = 0; i < authFile.length(); i++) {
			if(authFile.getJSONObject(i).getString("username").equalsIgnoreCase(username)) {
				accessToken = authFile.getJSONObject(i).getString("accessToken");
				clientToken = authFile.getJSONObject(i).getString("clientToken");
				i = (authFile.length() + 1);
			}
		}
		
		JSONObject auth = new MojangAuth().refresh(accessToken, clientToken);
		
		for(int i = 0; i < authFile.length(); i++) {
			if(authFile.getJSONObject(i).getString("username").equalsIgnoreCase(username)) {
				authFile.getJSONObject(i).put("accessToken", auth.getString("accessToken"));
				authFile.getJSONObject(i).put("clientToken", auth.getString("clientToken"));
				new WriteTextToFile(dirS + "auth.json", authFile.toString());
				i = (authFile.length() + 1);
			}
		}
		
		ans = new String[] {auth.getString("accessToken"), auth.getString("clientToken"), auth.getJSONObject("selectedProfile").getString("id"), 
				auth.getJSONObject("selectedProfile").getString("name")};
		return ans;
	}
	
}
