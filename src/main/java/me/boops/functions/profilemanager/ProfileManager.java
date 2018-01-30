package me.boops.functions.profilemanager;

import java.io.File;

import me.boops.Main;
import me.boops.functions.file.CreateFolder;

public class ProfileManager {
	
	public static String version = "";
	public static String name = "";
	public static String path = "";
	public static String forgeVersion = "";
	
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
				String[] data = new ReadProfileData().read(Main.args[i + 1]);
				ProfileManager.version = data[0];
				ProfileManager.forgeVersion = data[1];
				ProfileManager.name = Main.args[i + 1];
			}
			
			if(Main.args[i].equalsIgnoreCase("--list-profiles")) {
				new ListProfiles();
				System.exit(0);
			}
			
			if(Main.args[i].equalsIgnoreCase("--set-profile-forge")) {
				new AppendForgeVersion(Main.args[i + 1], Main.args[i + 2]);
				System.exit(0);
			}
			
			if(Main.args[i].equalsIgnoreCase("--update-profile-forge")) {
				new AppendForgeVersion(Main.args[i + 1], Main.args[i + 2]);
				System.exit(0);
			}
			
			if(Main.args[i].equalsIgnoreCase("--delete-profile")) {
				new DeleteProfile(Main.args[i + 1]);
				System.exit(0);
			}
		}
		
		// If no profile is defined use the
		// `default` profile
		if(ProfileManager.name.isEmpty()) {
			ProfileManager.name = "default";
		}
		
		// Set the profile path
		ProfileManager.path = (Main.homeDir + "profiles" + File.separator + ProfileManager.name + File.separator);
		new CreateFolder(ProfileManager.path);
	}
}
