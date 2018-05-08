package me.boops.functions.crypto;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class Sha1SumFile {
	
	public String sum(String file_path) {
		
		String ans = "";
		
		try {
			
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			InputStream fis = new FileInputStream(file_path);
			int n = 0;
			
			byte[] buffer = new byte[8192];
			while(n != -1) {
				n = fis.read(buffer);
				if(n > 0) {
					digest.update(buffer, 0, n);
				}
			}
			
			fis.close();
			ans = digest.digest().toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ans;
	}
}
