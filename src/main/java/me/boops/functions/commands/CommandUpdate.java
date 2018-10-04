package me.boops.functions.commands;

import java.io.File;

import org.apache.commons.io.FileUtils;

import me.boops.functions.crypto.Sha1SumFile;
import me.boops.functions.network.FetchRemoteContent;

public class CommandUpdate {
	
	public CommandUpdate( ) {
		try {
			
			// Get Jar Path
			File jar_path = new File(CommandUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			
			// Get sha1sum for local file
			String sha1sum_local_og = new Sha1SumFile().sum(jar_path.toString());
			
			// Fetch remote sha1sum
			String sha1sum_remote = new FetchRemoteContent().text("https://s3.amazonaws.com/boops-deploy/McBoop/McBoop.jar.sha1");
			
			// Check if we need to update
			if(!sha1sum_local_og.equalsIgnoreCase(sha1sum_remote)) {
				
				// Sums do not match found an update!
				System.out.println("Found an update!");
				
				// Create a new filename based on the old one
				String temp_filename = (jar_path.getName() + ".tmp");
				
				// Download the new version
				new FetchRemoteContent().file("https://s3.amazonaws.com/boops-deploy/McBoop/McBoop.jar", jar_path.toString().replace(jar_path.getName(), ""), temp_filename);
				
				// Verify the new version
				String sha1sum_new = new Sha1SumFile().sum(jar_path.toString().replace(jar_path.getName(), temp_filename));
				
				if(sha1sum_remote.equalsIgnoreCase(sha1sum_new)) {
					System.out.println("SHA1 sums matched");
					
					// Now that the new version has been verfied
					FileUtils.copyFile(new File(jar_path.toString().replace(jar_path.getName(), temp_filename)), jar_path);
					System.out.println("McBoop has been updated!");
					
				} else {
					System.out.println("SHA1 sums failed to match please try again!");
				}
				FileUtils.forceDelete(new File(jar_path.toString().replace(jar_path.getName(), temp_filename)));
			} else {
				System.out.println("No update found");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
