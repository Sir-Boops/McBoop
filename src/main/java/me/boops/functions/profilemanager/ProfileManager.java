package me.boops.functions.profilemanager;

import java.io.File;

import me.boops.Main;
import me.boops.functions.file.CreateFolder;

public class ProfileManager {
	
	public static String version = "";
	public static String name = "";
	public static String path = "";
	
	public ProfileManager() {
		new CreateFolder(Main.homeDir + "profiles" + File.separator);
		
		for(int i = 0; i < Main.args.length; i++) {
			
			if(Main.args[i].equalsIgnoreCase("--create-profile")) {
				new CreateProfile();
				System.exit(0);
			}
			
			if(Main.args[i].equalsIgnoreCase("--update-profile")) {
				new CreateProfile();
				System.exit(0);
			}
			
			if(Main.args[i].equalsIgnoreCase("--profile")) {
				String version = new ReadProfile().read();
				ProfileManager.version = version;
				ProfileManager.name = Main.args[i + 1];
			}	
		}
		
		// If no profile is defined use the
		// `default` profile
		if(ProfileManager.name.isEmpty()) {
			ProfileManager.name = "default";
		}
		
		// Set the profile path
		ProfileManager.path = (Main.homeDir + "profiles" + File.separator + ProfileManager.name + File.separator);
		
	}
}
