package me.boops.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ReadTextFromFile {

	public String read(String fileS) {

		File file = new File(fileS);
		String ans = "";

		try {
			
			StringBuilder sb = new StringBuilder();
			
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			
			br.close();
			ans = sb.toString();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ans;
		
	}
}
