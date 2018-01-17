package me.boops.functions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

public class VersionVerifyMeta {
	
	public String getMeta(String[] args) {
		
		// Grab the meta file
		JSONObject metaFile = fetchMeta();
		
		String mcVersion = "";
		
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("--run")) {
				mcVersion = args[i + 1];
				i =(args.length + 1);
			}
		}
		
		String versionMeta = "";
		
		for(int i = 0; i < metaFile.getJSONArray("versions").length(); i++) {
			if(metaFile.getJSONArray("versions").getJSONObject(i).getString("id").equalsIgnoreCase(mcVersion)) {
				versionMeta = metaFile.getJSONArray("versions").getJSONObject(i).getString("url");
			}
		}
		
		if(versionMeta.isEmpty()) {
			System.out.println("Could not find requested version!");
			System.exit(1);
		}
		
		
		return versionMeta;
	}
	
	private JSONObject fetchMeta() {
		JSONObject ans = new JSONObject();
		
		try {
			
			URL url = new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json");
			
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10 * 1000);
			conn.setConnectTimeout(10 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux X11; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
			
			conn.connect();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String inByte;
			
			while ((inByte = in.readLine()) != null) {
				sb.append(inByte);
			}
			
			ans = new JSONObject(sb.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ans;
	}
}
