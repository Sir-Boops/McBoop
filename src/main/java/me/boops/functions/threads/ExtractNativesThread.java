package me.boops.functions.threads;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ExtractNativesThread implements Runnable {
	
	private String jar_file;
	private String native_path;
	
	public ExtractNativesThread(String jar_file, String native_path) {
		this.jar_file = jar_file;
		this.native_path = native_path;
	}
	
	@Override
	public void run() {
		
		try {
			
			JarFile jar = new JarFile(this.jar_file);
			Enumeration<JarEntry> jar_files = jar.entries();
			
			while(jar_files.hasMoreElements()) {
				
				JarEntry file = (JarEntry) jar_files.nextElement();
				
				// Check if the file is a .so file
				if(file.getName().substring(file.getName().length() - 3, file.getName().length()).equals(".so")) {
					// We found a .so file!
				    ExtractFile(jar, file);
				}
				
				// Check if a .dll file
				if(file.getName().substring(file.getName().length() - 4, file.getName().length()).equals(".dll")) {
					// We found a .dll file!
				    ExtractFile(jar, file);
				}
			}
			
			jar.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void ExtractFile(JarFile jar, JarEntry file) throws Exception {
		
		// Make sure we havn't extracted this already!
		if(!new File(this.native_path + file.getName()).exists()) {
			
			// We have not extracted this file yet!
			System.out.println("Extracting: " + file.getName());
			
			InputStream is = jar.getInputStream(file);
			FileOutputStream fos = new FileOutputStream(this.native_path + file.getName());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			int inByte;
			while((inByte = is.read()) != -1) {
				bos.write(inByte);
			}
			
			fos.write(bos.toByteArray());

			fos.close();
			is.close();
			
		}
	}
}
