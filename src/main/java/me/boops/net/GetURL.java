package me.boops.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetURL {

	public String get(String URL) throws Exception {

		// Create the URL
		URL url = new URL(URL);

		// Create the connection
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setReadTimeout(10 * 1000);
		conn.setConnectTimeout(10 * 1000);
		conn.setRequestMethod("GET");

		// Open the connection
		conn.connect();

		// Read & Parse the input
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		StringBuilder sb = new StringBuilder();
		String inByte;
		while ((inByte = in.readLine()) != null) {
			sb.append(inByte);
		}

		return sb.toString();

	}
}
