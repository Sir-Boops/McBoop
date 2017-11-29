package me.boops.functions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import me.boops.base.Config;

public class ExtractLibs {
	public ExtractLibs() throws Exception {

		for (int i = 0; i < Config.extractPaths.size(); i++) {

			JarFile jar = new JarFile(Config.extractPaths.get(i));
			Enumeration<JarEntry> files = jar.entries();

			while (files.hasMoreElements()) {
				JarEntry file = (JarEntry) files.nextElement();
				if (file.getName().contains(".so") || file.getName().contains(".dll")) {
					InputStream is = jar.getInputStream(file);
					FileOutputStream fos = new FileOutputStream(Config.rootDir + "natives" + File.separator + file.getName());
					int inByte;
					while ((inByte = is.read()) != -1) {
						fos.write(inByte);
					}
					is.close();
					fos.close();
				}
			}
			jar.close();

		}
	}
}
