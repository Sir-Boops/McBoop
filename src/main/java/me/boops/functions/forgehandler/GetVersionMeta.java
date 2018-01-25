package me.boops.functions.forgehandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.jar.JarFile;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.VersionMeta;
import me.boops.functions.file.CreateFolder;
import me.boops.functions.network.FetchRemoteFile;

public class GetVersionMeta {
	
	public JSONObject meta() {
		JSONObject ans = new JSONObject();
		
		String URL = ("https://files.minecraftforge.net/maven/net/minecraftforge/forge/%mcID%-%forgeID%-%mcID%/forge-%mcID%-%forgeID%-%mcID%-universal.jar");
		URL = URL.replaceAll("(%mcID%)", VersionMeta.ID);
		URL = URL.replaceAll("(%forgeID%)", ForgeHandler.versionID);
		
		String path = Main.homeDir + "forge" + File.separator;
		String filePath = (path + URL.substring(URL.lastIndexOf("/") + 1, URL.length()));
		new CreateFolder(path);
		
		if(!new File(path + URL.substring(URL.lastIndexOf("/") + 1, URL.length())).exists()) {
			new FetchRemoteFile(URL, path, "");
		}
		
		ans = readVersionJSON(filePath);
		
		System.out.println(ans);
		System.exit(0);
		
		return ans;
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
