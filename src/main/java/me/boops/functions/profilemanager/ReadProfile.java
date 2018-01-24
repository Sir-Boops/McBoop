package me.boops.functions.profilemanager;

import java.io.File;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.file.ReadTextFromFile;

public class ReadProfile {

	public String read() {
		
		String profileName = "";
		String path = Main.homeDir + "profiles" + File.separator;
		
		for(int i = 0; i < Main.args.length; i++) {
			if(Main.args[i].equalsIgnoreCase("--profile")) {
				profileName = Main.args[i + 1];
			}
		}
		
		JSONObject profile = new JSONObject(new ReadTextFromFile().read(path + profileName + File.separator + "profile.json"));
		
		return profile.getString("mcVersion");
		
	}
	
}
