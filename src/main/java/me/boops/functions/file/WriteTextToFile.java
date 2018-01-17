package me.boops.functions.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteTextToFile {
	
	public WriteTextToFile(String fileS, String data) {
		
		File file = new File(fileS);
		
		// Make sure that the path is present for the file!
		new CreateFolder(fileS.substring(0, fileS.lastIndexOf(File.separator)));
		
		try {
			
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(data);
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
