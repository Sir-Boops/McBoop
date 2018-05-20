package me.boops.functions.mojangauth;

import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import me.boops.Main;

public class AuthValidate {

	public boolean validate(String access, String client) {
		boolean ans = false;

		try {

			URL url = new URL("https://authserver.mojang.com/validate");
			JSONObject payload = new JSONObject().put("accessToken", access).put("clientToken", client);

			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10 * 1000);
			conn.setConnectTimeout(10 * 1000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("User-Agent", Main.http_user);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			conn.connect();

			// Send the post data
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(payload.toString());
			wr.close();
			
			if(conn.getResponseCode() != 204) {
				ans = true;
			}

		} catch (Exception e) {
			ans = true;
		}

		return ans;
	}

}
