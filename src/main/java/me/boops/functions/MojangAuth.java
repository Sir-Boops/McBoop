package me.boops.functions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

public class MojangAuth {

	public JSONObject auth(String user, String pass) {

		JSONObject ans = new JSONObject();

		JSONObject agent = new JSONObject().put("name", "Minecraft").put("version", 1);
		JSONObject payload = new JSONObject().put("username", user).put("password", pass).put("agent", agent);

		try {

			URL url = new URL("https://authserver.mojang.com/authenticate");

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
			wr.write(payload.toString());
			wr.close();

			// Recive the answer
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
	
	public JSONObject refresh(String access, String client) {
		
		JSONObject ans = new JSONObject();
		
		JSONObject payload = new JSONObject().put("accessToken", access).put("clientToken", client);
		
		try {

			URL url = new URL("https://authserver.mojang.com/refresh");

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
			wr.write(payload.toString());
			wr.close();

			// Recive the answer
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
