package me.boops.functions.profilemanager;

import java.io.File;

import me.boops.Main;
import me.boops.functions.file.CreateFolder;

public class ProfileManager {
	
	public static String version = "";
	public static String name = "";
	public static String path = "";
	public static String forgeVersion = "";
	
	public ProfileManager(String[] launcher_args) {
		
		System.out.println("Attempting to load profile");
		new CreateFolder(Main.home_dir + "profiles" + File.separator);
		
		for(int i = 0; i < launcher_args.length; i++) {
			
			if(launcher_args[i].equalsIgnoreCase("--create-profile")) {
				
				if((i + 2) >= launcher_args.length) {
					System.out.println("");
					System.out.println("Missing argument!");
					System.out.println("--help for help!");
					System.out.println("");
					System.exit(1);
				}
				
				new CreateProfile(launcher_args[i + 1], launcher_args[i + 2]);
				System.exit(0);
			}
			
			if(launcher_args[i].equalsIgnoreCase("--update-profile")) {
				
				if((i + 2) < launcher_args.length) {
					System.out.println("");
					System.out.println("Missing argument!");
					System.out.println("--help for help!");
					System.out.println("");
				}
				
				new CreateProfile(launcher_args[i + 1], launcher_args[i + 2]);
				System.exit(0);
			}
			
			if(launcher_args[i].equalsIgnoreCase("--profile")) {
				String[] data = new ReadProfileData().read(launcher_args[i + 1]);
				ProfileManager.version = data[0];
				ProfileManager.forgeVersion = data[1];
				ProfileManager.name = launcher_args[i + 1];
			}
			
			if(launcher_args[i].equalsIgnoreCase("--list-profiles")) {
				new ListProfiles();
				System.exit(0);
			}
			
			if(launcher_args[i].equalsIgnoreCase("--set-profile-forge")) {
				new AppendForgeVersion(launcher_args[i + 1], launcher_args[i + 2]);
				System.exit(0);
			}
			
			if(launcher_args[i].equalsIgnoreCase("--update-profile-forge")) {
				new AppendForgeVersion(launcher_args[i + 1], launcher_args[i + 2]);
				System.exit(0);
			}
			
			if(launcher_args[i].equalsIgnoreCase("--delete-profile")) {
				new DeleteProfile(launcher_args[i + 1]);
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
