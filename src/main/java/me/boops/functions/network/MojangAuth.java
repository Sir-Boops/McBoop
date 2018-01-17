package me.boops.functions.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

public class MojangAuth {

	public JSONObject auth(String user, String pass) {

		JSONObject agent = new JSONObject().put("name", "Minecraft").put("version", 1);
		JSONObject payload = new JSONObject().put("username", user).put("password", pass).put("agent", agent);
		return new JSONObject(postURL("https://authserver.mojang.com/authenticate", payload.toString()));

	}
	
	public JSONObject refresh(String access, String client) {
		
		JSONObject payload = new JSONObject().put("accessToken", access).put("clientToken", client);
		return new JSONObject(postURL("https://authserver.mojang.com/refresh", payload.toString()));
		
	}
	
	private String postURL(String URL, String postbody) {
		
		String ans = "";
		
		try {

			URL url = new URL(URL);

			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10 * 1000);
			conn.setConnectTimeout(10 * 1000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:59.0) Gecko/20100101 Firefox/59.0");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			conn.connect();

			// Send the post data
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(postbody);
			wr.close();

			// Recive the answer
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String inByte;
			while ((inByte = in.readLine()) != null) {
				sb.append(inByte);
			}

			ans = sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ans;
		
	}

}
