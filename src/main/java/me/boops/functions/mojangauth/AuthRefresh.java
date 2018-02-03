package me.boops.functions.mojangauth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import me.boops.Main;

public class AuthRefresh {
	
	public String[] refresh(String access, String client) {
		
		// 0 => accessToken
		// 1 => clientToken
		String[] ans = new String[] {};
		
		try {

			JSONObject payload = new JSONObject().put("accessToken", access).put("clientToken", client);
			URL url = new URL("https://authserver.mojang.com/refresh");

			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10 * 1000);
			conn.setConnectTimeout(10 * 1000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("User-Agent", Main.HttpUser);
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
			
			JSONObject refAns = new JSONObject(sb.toString());
			ans = new String[] {refAns.getString("accessToken"), refAns.getString("clientToken")};

		} catch (Exception e) {
			System.out.println("Error refreshing your auth key!");
			System.out.println("Perhaps try removing your account and re-adding it?");
			System.exit(1);
		}
		
		return ans;
	}
}
