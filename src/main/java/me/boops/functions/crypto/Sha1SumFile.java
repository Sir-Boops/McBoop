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
			ans = getMessageDigest(digest.digest());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ans;
	}
	
	private String getMessageDigest(byte[] bytes) {
		String ans = "";
		for(int i = 0; i < bytes.length; i++) {
			ans += Integer.toString( ( bytes[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return ans;
	}
	
}
