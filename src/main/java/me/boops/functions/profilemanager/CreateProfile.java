package me.boops.functions.profilemanager;

import java.io.File;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.file.CreateFolder;
import me.boops.functions.file.WriteTextToFile;

public class CreateProfile {
	
	public CreateProfile(String name, String mcID) {
		
		String path = Main.homeDir + "profiles" + File.separator + name + File.separator;
		new CreateFolder(path);
		
		JSONObject profile = new JSONObject().put("mcVersion", mcID);
		new WriteTextToFile(path + "profile.json", profile.toString());
		
		System.out.println("Created new profile: " + name + ", That runs MC Version: " + mcID);
	}
}
