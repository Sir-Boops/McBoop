package me.boops.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import me.boops.Cache;

public class DownloadFile {
	public void Download(String destFolder, String URL, String fileName) throws Exception {

		if (!new File(destFolder + fileName).exists()) {
			
			if(!new File(destFolder).exists()) {
				new File(destFolder).mkdirs();
			}
			
			URL url = new URL(URL);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10 * 1000);
			conn.setConnectTimeout(10 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "McBoop/" + Cache.mcBoopVersion);

			conn.connect();

			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(new File(destFolder + fileName));

			int inByte;
			while ((inByte = is.read()) != -1) {
				fos.write(inByte);
			}

			is.close();
			fos.close();
		}
	}
}
