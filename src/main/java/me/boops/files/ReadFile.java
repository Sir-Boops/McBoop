package me.boops.files;

import java.io.BufferedReader;
import java.io.FileReader;

public class ReadFile {
	public String Read(String file) throws Exception {
		
		StringBuilder sb = new StringBuilder();

		// Read The Config File
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		while (line != null) {
			sb.append(line);
			line = br.readLine();
		}
		br.close();
		
		return sb.toString();
	}
}
