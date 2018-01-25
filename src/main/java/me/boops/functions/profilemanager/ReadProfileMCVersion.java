package me.boops.functions.profilemanager;

import java.io.File;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.file.ReadTextFromFile;

public class ReadProfileMCVersion {

	public String read(String profileName) {
		String path = Main.homeDir + "profiles" + File.separator;
		
		JSONObject profile = new JSONObject(new ReadTextFromFile().read(path + profileName + File.separator + "profile.json"));
		return profile.getString("mcVersion");
	}
}
