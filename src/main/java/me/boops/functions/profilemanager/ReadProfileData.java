package me.boops.functions.profilemanager;

import java.io.File;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.file.ReadTextFromFile;

public class ReadProfileData {

	public String[] read(String profileName) {
		// 0 => mcVersion ID
		// 1 => forge version ID
		String[] ans = new String[] {};
		
		String path = Main.homeDir + "profiles" + File.separator;
		
		String json = new ReadTextFromFile().read(path + profileName + File.separator + "profile.json");
		
		if(!json.isEmpty()) {
			JSONObject profile = new JSONObject(json);
			if(profile.has("forgeVersion")) {
				ans = new String[] {profile.getString("mcVersion"), profile.getString("forgeVersion")};
			} else {
				ans = new String[] {profile.getString("mcVersion"), ""};
			}
		}
		return ans;
	}
}
