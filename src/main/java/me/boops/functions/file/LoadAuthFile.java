package me.boops.functions.file;

import java.io.File;

import org.json.JSONArray;

import me.boops.Main;

public class LoadAuthFile {

	public JSONArray load() {

		File authFile = new File(Main.home_dir + "auth.json");
		JSONArray ans = new JSONArray();

		// Make sure the auth file is there!
		// Else create it and set it to an
		// empty jsonobject
		if (!authFile.exists() || !authFile.isFile()) {
			
			// Write to file to create the file!
			new WriteTextToFile(authFile.toString(), ans.toString());
			
		} else {
			
			ans = new JSONArray(new ReadTextFromFile().read(authFile.toString()));
			
		}
		
		return ans;

	}
}
