package me.boops.functions.forgehandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.jar.JarFile;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.file.CreateFolder;
import me.boops.functions.network.FetchRemoteContent;
import me.boops.functions.string_utls.ReplaceChars;

public class GetVersionMeta {
	
	public JSONObject meta(String file_name, String file_path) {
		
		String URL = ("https://files.minecraftforge.net/maven/net/minecraftforge/forge/" + new ReplaceChars().replace(file_path, File.separator, "/") + file_name);
		
		String path = Main.homeDir + "forge" + File.separator;
		String filePath = (path + file_name);
		new CreateFolder(path);
		
		if(!new File(path + file_name).exists()) {
			System.out.println("Downloading forge jar");
			new FetchRemoteContent().file(URL, path, "");
		}
		
		return readVersionJSON(filePath);
	}
	
	private JSONObject readVersionJSON(String forgeFile) {
		
		JSONObject ans = new JSONObject();
		
		try {
			
			JarFile forgeJar = new JarFile(forgeFile);
			InputStream is = forgeJar.getInputStream(forgeJar.getEntry("version.json"));
			
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String inByte;
			
			while((inByte = in.readLine()) != null) {
				sb.append(inByte);
			}
			
			is.close();
			forgeJar.close();
			
			ans = new JSONObject(sb.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ans;
		
	}
}
