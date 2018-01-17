package me.boops.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

public class InstallAssets {

	public InstallAssets(JSONObject assetIndex, String dirS) {
		
		JSONObject assetList = grabIndexList(assetIndex.getString("url"));
		
		new CreateFolder(dirS + "versions");
		new WriteTextToFile(dirS + "assets" + File.separator + "indexes" + File.separatorChar + assetIndex.getString("id") + ".json", assetList.toString());
		
		setUpDownloads(assetList.getJSONObject("objects"), (dirS + "assets" + File.separator + "objects" + File.separator));

	}

	private JSONObject grabIndexList(String URL) {

		JSONObject ans = new JSONObject();

		try {

			URL url = new URL(URL);

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
	
	private void setUpDownloads(JSONObject list, String dirS) {
		
		Iterator<?> keys = list.keys();
		
		while(keys.hasNext()) {
			
			String key = (String) keys.next();
			String hash = list.getJSONObject(key).getString("hash");
			String firstTwo = hash.substring(0, 2);
			
			String URL = ("https://resources.download.minecraft.net/" + firstTwo + "/" + hash);
			String distDir = (dirS + firstTwo + File.separator);
			
			if(!new File(distDir + hash).exists()) {
				System.out.println("Downloading: " + key.substring(key.lastIndexOf("/") + 1, key.length()));
				downloadFile(URL, distDir);
			}
		}
	}
	
	private void downloadFile(String URL, String destDir) {
		
		new CreateFolder(destDir);
		
		String fileName = URL.substring(URL.lastIndexOf("/") + 1, URL.length());
		
		try {
			
			URL url = new URL(URL);
			
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10 * 1000);
			conn.setConnectTimeout(10 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux X11; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
			
			conn.connect();
			
			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(new File(destDir + fileName));
			int inByte;
			
			while ((inByte = is.read()) != -1) {
				fos.write(inByte);
			}
			
			is.close();
			fos.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
