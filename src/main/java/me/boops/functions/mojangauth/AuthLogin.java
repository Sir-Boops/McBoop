package me.boops.functions.mojangauth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import me.boops.Main;

public class AuthLogin {

	public String[] login(String username, String password) {

		// 0 => accessToken
		// 1 => clientToken
		// 2 => UUID
		// 3 => UserName
		String[] ans = new String[] {};
		
		try {

			JSONObject agent = new JSONObject().put("name", "Minecraft").put("version", 1);
			JSONObject payload = new JSONObject().put("username", username).put("password", password).put("agent", agent);
			URL url = new URL("https://authserver.mojang.com/authenticate");

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
			ans = new String[] {refAns.getString("accessToken"), refAns.getString("clientToken"),
					refAns.getJSONObject("selectedProfile").getString("id"), refAns.getJSONObject("selectedProfile").getString("name")};

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error logging in!");
			System.out.println("Is your password wrong?");
			System.exit(1);
		}
		
		return ans;
	}
}
