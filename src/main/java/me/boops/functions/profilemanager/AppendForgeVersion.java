package me.boops.functions.profilemanager;

import java.io.File;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.file.WriteTextToFile;

public class AppendForgeVersion {
	
	public AppendForgeVersion(String name, String version) {
		
		String path = Main.homeDir + "profiles" + File.separator + name + File.separator;
		
		String MCVersion = new ReadProfileData().read(name)[0];
		
		if(!MCVersion.isEmpty()) {
			JSONObject profile = new JSONObject().put("mcVersion", MCVersion).put("forgeVersion", version);
			new WriteTextToFile(path + "profile.json", profile.toString());
			System.out.println("Set profile: " + name + " To use forge version " + version );
		} else {
			System.out.println("Could not find profile!");
			System.out.println("Perhaps try creating the profile first?");
			System.exit(1);
		}
	}
}
