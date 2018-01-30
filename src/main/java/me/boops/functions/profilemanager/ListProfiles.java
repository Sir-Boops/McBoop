package me.boops.functions.profilemanager;

import java.io.File;

import me.boops.Main;

public class ListProfiles {
	
	public ListProfiles() {
		
		String[] profiles = new File(Main.homeDir + "profiles" + File.separator).list();
		
		System.out.println("Here is a list of profiles:");
		System.out.println("");
		
		for(int i = 0; i < profiles.length; i++) {
			if(new ReadProfileData().read(profiles[i])[1].isEmpty()) {
				System.out.println("Profile Name: " + profiles[i] + ", MCVersion: " + new ReadProfileData().read(profiles[i])[0]);
			} else {
				System.out.println("Profile Name: " + profiles[i] + ", MCVersion: " + new ReadProfileData().read(profiles[i])[0]
						+ ", ForgeVersion: " + new ReadProfileData().read(profiles[i])[1]);
			}
			System.out.println("");
		}
		
		System.out.println("");
	}
}
