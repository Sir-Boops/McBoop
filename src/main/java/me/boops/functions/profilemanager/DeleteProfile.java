package me.boops.functions.profilemanager;

import java.io.File;

import org.apache.commons.io.FileUtils;

import me.boops.Main;

public class DeleteProfile {

	public DeleteProfile(String profileName) {

		String path = (Main.homeDir + "profiles" + File.separator + profileName);

		if (new File(path).exists()) {
			try {
				FileUtils.deleteDirectory(new File(path));
			} catch (Exception e) {
				System.out.println("Error deleteing profile!");
				e.printStackTrace();
				System.exit(1);
			}
			
			System.out.println("Deleted profile: " + profileName);
			
		} else {

			System.out.println("");
			System.out.println("The profile: " + profileName + " Cannot be found!");
			System.out.println("Perhaps try --list-profiles ?");
			System.out.println("");

		}

	}
}
