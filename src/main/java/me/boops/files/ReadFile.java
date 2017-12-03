package me.boops.files;

import java.io.BufferedReader;
import java.io.FileReader;

import org.json.JSONObject;

public class ReadFile {
	public JSONObject Read(String file) throws Exception {
		
		StringBuilder sb = new StringBuilder();

		// Read The Config File
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		while (line != null) {
			sb.append(line);
			line = br.readLine();
		}
		br.close();
		
		return new JSONObject(sb.toString());
	}
}
