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
		
		System.out.println("Attempting to load profile");
		new CreateFolder(Main.home_dir + "profiles" + File.separator);
		
		for(int i = 0; i < Main.args.length; i++) {
			
			if(Main.args[i].equalsIgnoreCase("--create-profile")) {
				
				if((i + 2) >= Main.args.length) {
					System.out.println("");
					System.out.println("Missing argument!");
					System.out.println("--help for help!");
					System.out.println("");
					System.exit(1);
				}
				
				new CreateProfile(Main.args[i + 1], Main.args[i + 2]);
				System.exit(0);
			}
			
			if(Main.args[i].equalsIgnoreCase("--update-profile")) {
				
				if((i + 2) < Main.args.length) {
					System.out.println("");
					System.out.println("Missing argument!");
					System.out.println("--help for help!");
					System.out.println("");
				}
				
				new CreateProfile(Main.args[i + 1], Main.args[i + 2]);
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
		ProfileManager.path = (Main.home_dir + "profiles" + File.separator + ProfileManager.name + File.separator);
		new CreateFolder(ProfileManager.path);
		System.out.println("Loaded profile: " + ProfileManager.name);
	}
}
