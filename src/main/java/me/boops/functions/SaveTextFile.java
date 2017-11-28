package me.boops.functions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class SaveTextFile {
	public void Save(String path, String text, String fileName) throws Exception {

		// Make sure that folder is there
		if (!new File(path).exists()) {
			new File(path).mkdir();
		}

		// Save the file unless it is already present
		if(!new File(path + fileName).exists()) {
			BufferedWriter out = new BufferedWriter(new FileWriter(path + fileName));
			out.write(text);
			out.close();
		}
	}
}
