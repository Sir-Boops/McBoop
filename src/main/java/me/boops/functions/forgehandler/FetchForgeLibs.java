package me.boops.functions.forgehandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import me.boops.Main;
import me.boops.functions.VersionMeta;
import me.boops.functions.file.CreateFolder;

public class FetchForgeLibs {
	
	public List<String> fetch(){
		List<String> ans = new ArrayList<String>();
		
		JSONArray libList = ForgeHandler.versionMeta.getJSONArray("libraries");
		
		// Download forge libs and add them to the final response!
		for(int i = 0; i < libList.length(); i++) {
			String rawName = libList.getJSONObject(i).getString("name");
			if(!rawName.toLowerCase().contains("minecraftforge")) {
				String fileName = (rawName.split(":")[1] + "-" + rawName.split(":")[2] + ".jar");
				String filePath = (rawName.split(":")[0].replaceAll("\\.", File.separator) + File.separator + rawName.split(":")[1]
						+ File.separator + rawName.split(":")[2] + File.separator);
				String fullPath = (Main.homeDir + "libraries" + File.separator + filePath + fileName);
				downloadLib(fileName, filePath);
				ans.add(fullPath);
			}
		}
		
		// Add all the default libs that forge dosn't override
		JSONArray defaultArr = VersionMeta.Meta.getJSONArray("libraries");
		for(int i = 0; i < defaultArr.length(); i++) {
			String rawName = defaultArr.getJSONObject(i).getString("name");
			String name = (rawName.split(":")[1]);
			if(shouldAdd(name)) {
				String fileName = (rawName.split(":")[1] + "-" + rawName.split(":")[2] + ".jar");
				String filePath = (rawName.split(":")[0].replaceAll("\\.", File.separator) + File.separator + rawName.split(":")[1]
						+ File.separator + rawName.split(":")[2] + File.separator);
				String fullPath = (Main.homeDir + "libraries" + File.separator + filePath + fileName);
				ans.add(fullPath);
			}
		}
		
		return ans;
	}
	
	private boolean shouldAdd(String term) {
		boolean ans = true;
		JSONArray forgeLibs = ForgeHandler.versionMeta.getJSONArray("libraries");
		
		for(int i = 0; i < forgeLibs.length(); i++) {
			String rawName = forgeLibs.getJSONObject(i).getString("name");
			String name = (rawName.split(":")[1]);
			
			if(term.equalsIgnoreCase(name)) {
				ans = false;
			}
		}
		
		return ans;
	}
	
	private void downloadLib(String fileName, String filePath) {
		
		String fullPath = (Main.homeDir + "libraries" + File.separator + filePath);
		new CreateFolder(fullPath);
		
		if(!new File(fullPath + fileName).exists()) {
			
			System.out.println("Downloading: " + fileName);
			
			String[] urls = new String[] {
					"https://libraries.minecraft.net/" + filePath + fileName,
					"https://repo.spongepowered.org/maven/" + filePath + fileName,
					"http://central.maven.org/maven2/" + filePath + fileName
			};
			
			boolean gotFile = false;
			int attempts = 0;
			while(!gotFile && attempts < 3) {
				gotFile = tryDownload(urls[attempts], (fullPath + fileName));
				attempts++;
			}
			
		}
	}
	
	private boolean tryDownload(String URL, String fullPath) {
		boolean ans = true;
		
		try {

			URL url = new URL(URL);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10 * 1000);
			conn.setConnectTimeout(10 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", Main.HttpUser);

			conn.connect();
			
			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(new File(fullPath));
			int inByte;

			while ((inByte = is.read()) != -1) {
				fos.write(inByte);
			}
			

			is.close();
			fos.close();

		} catch (Exception e) {
			ans = false;
		}
		
		return ans;
	}
}
