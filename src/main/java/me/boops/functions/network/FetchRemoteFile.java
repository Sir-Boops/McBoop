package me.boops.functions.network;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import me.boops.Main;
import me.boops.functions.file.CreateFolder;

public class FetchRemoteFile {

	public FetchRemoteFile(String URL, String destDir, String fileName) {

		if (fileName.isEmpty()) {
			fileName = URL.substring(URL.lastIndexOf("/") + 1, URL.length());
		}

		new CreateFolder(destDir);

		try {

			// Turn the URL into a URL
			URL url = new URL(URL);

			// Set the client options
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10 * 1000);
			conn.setConnectTimeout(10 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", Main.HttpUser);

			// Connect to the remote server
			conn.connect();

			// Open all the needed streams
			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(new File(destDir + fileName));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			// Copy the file from the remote server
			int inByte;
			while ((inByte = is.read()) != -1) {
				bos.write(inByte);
			}

			// Write the file
			fos.write(bos.toByteArray());

			is.close();
			fos.close();
			bos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
