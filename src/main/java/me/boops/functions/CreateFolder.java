package me.boops.functions;

import java.io.File;

public class CreateFolder {

	public CreateFolder(String dirString) {

		File dir = new File(dirString);
		boolean ans = false;

		if (!dir.exists() && !dir.isDirectory()) {
			// Create the requested dir
			ans = dir.mkdirs();
		} else {
			ans = true;
		}

		if (!ans) {
			System.out.println("Error working with folder: " + dirString);
			System.exit(1);
		}
	}
}
