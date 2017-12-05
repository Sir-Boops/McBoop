package me.boops.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.json.JSONObject;

import me.boops.Cache;

public class ReadForgeVersion {
	public JSONObject read() throws Exception {
		
		JSONObject ans = new JSONObject();
		
		String path = Cache.cacheDir + "libraries" + File.separator + "net" + File.separator + "minecraftforge" + File.separator + "forge" + File.separator;
		path += Cache.runVersion + "-" + Cache.forgeVersion + File.separator;
		
		JarFile jar = new JarFile(path + "forge-" + Cache.runVersion + "-" + Cache.forgeVersion + "-universal.jar");
		Enumeration<JarEntry> files = jar.entries();

		while (files.hasMoreElements()) {
			JarEntry file = (JarEntry) files.nextElement();
			if (file.getName().equalsIgnoreCase("version.json")) {
				InputStream is = jar.getInputStream(file);
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();
				while (line != null) {
					sb.append(line);
					line = br.readLine();
				}
				br.close();
				ans = new JSONObject(sb.toString());
			}
		}
		jar.close();
		
		return ans;
	}
}
