package me.boops.functions.profilemanager;

import java.io.File;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.file.ReadTextFromFile;

public class ReadProfileMCVersion {

	public String read(String profileName) {
		String path = Main.homeDir + "profiles" + File.separator;
		
		String json = new ReadTextFromFile().read(path + profileName + File.separator + "profile.json");
		
		if(!json.isEmpty()) {
			JSONObject profile = new JSONObject();
			return profile.getString("mcVersion");
		} else {
			return "";
		}
	}
}
