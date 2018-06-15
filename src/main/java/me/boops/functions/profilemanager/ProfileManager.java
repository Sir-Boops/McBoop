package me.boops.functions.profilemanager;

import java.io.File;

import me.boops.Main;
import me.boops.functions.file.CreateFolder;

public class ProfileManager {
	
	public String[] check(String[] launcher_args) {
	    
	    String version = "";
	    String name = "";
	    String path = "";
	    String forge_version = "";
		
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
				version = data[0];
				forge_version = data[1];
				name = launcher_args[i + 1];
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
		if(name.isEmpty()) {
			name = "default";
		}
		
		// Set the profile path
		path = (Main.home_dir + "profiles" + File.separator + name + File.separator);
		new CreateFolder(path);
		System.out.println("Loaded profile: " + name);
		return new String[] {version, name, path, forge_version};
	}
}
