package me.boops.functions.profilemanager;

import java.io.File;

import me.boops.Main;

public class ListProfiles {
	
	public ListProfiles() {
		
		String[] profiles = new File(Main.homeDir + "profiles" + File.separator).list();
		
		System.out.println("Here is a list of profiles:");
		
		for(int i = 0; i < profiles.length; i++) {
			System.out.println(profiles[i] + " - " + new ReadProfileMCVersion().read(profiles[i]));
		}
		
		System.out.println("");
	}
}
