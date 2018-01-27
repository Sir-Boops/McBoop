package me.boops.functions.profilemanager;

import java.io.File;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.file.CreateFolder;
import me.boops.functions.file.WriteTextToFile;

public class CreateProfile {
	
	public CreateProfile() {
		
		String profileName = "";
		String profileMCVersion = "";
		
		for(int i = 0; i < Main.args.length; i++) {
			if(Main.args[i].equalsIgnoreCase("--create-profile")) {
				profileName = Main.args[i +1];
				profileMCVersion = Main.args[i + 2];
			}
		}
		
		String path = Main.homeDir + "profiles" + File.separator + profileName + File.separator;
		new CreateFolder(path);
		
		JSONObject profile = new JSONObject().put("mcVersion", profileMCVersion);
		new WriteTextToFile(path + "profile.json", profile.toString());
		
		System.out.println("Created new profile: " + profileName + ", That runs MC Version: " + profileMCVersion);
	}
}
