package me.boops.functions;

import java.io.File;

import org.json.JSONObject;

import me.boops.functions.file.CreateFolder;
import me.boops.functions.file.ReadTextFromFile;
import me.boops.functions.file.WriteTextToFile;

public class ProfileManager {
	
	public String getProfile(String dirS, String[] args) {
		String ans = "";
		String path = dirS + "profiles" + File.separator;
		new CreateFolder(path);
		
		for(int i = 0; i < args.length; i++) {
			
			if(args[i].equalsIgnoreCase("--create-profile")) {
				CreateProfile(path, args);
				System.exit(0);
			}
			
			if(args[i].equalsIgnoreCase("--update-profile")) {
				CreateProfile(path, args);
				System.exit(0);
			}
			
			if(args[i].equalsIgnoreCase("--profile")) {
				String version = readProfile(path, args);
				ans = version;
			}
			
		}
		
		return ans;
	}
	
	public String getProfileName(String[] args) {
		String ans = "";
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("--profile")) {
				ans = args[i + 1];
			}
		}
		return ans;
	}
	
	private void CreateProfile(String dirS, String[] args) {
		
		String profileName = "";
		String profileMCVersion = "";
		
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("--create-profile")) {
				profileName = args[i +1];
				profileMCVersion = args[i + 2];
			}
		}
		
		String path = dirS + profileName + File.separator;
		new CreateFolder(path);
		
		JSONObject profile = new JSONObject().put("mcVersion", profileMCVersion);
		new WriteTextToFile(path + "profile.json", profile.toString());
		
	}
	
	private String readProfile(String dirS, String[] args) {
		
		String profileName = "";
		
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("--profile")) {
				profileName = args[i + 1];
			}
		}
		
		JSONObject profile = new JSONObject(new ReadTextFromFile().read(dirS + profileName + File.separator + "profile.json"));
		
		return profile.getString("mcVersion");
		
	}
	
}
