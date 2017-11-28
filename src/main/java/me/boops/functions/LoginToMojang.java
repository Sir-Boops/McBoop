package me.boops.functions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import me.boops.Config;

public class LoginToMojang {

	public JSONObject login() throws Exception {

		URL url = new URL("https://authserver.mojang.com/authenticate");
		
		
		JSONObject payload = new JSONObject();
		JSONObject agent = new JSONObject();
		agent.put("name", "Minecraft");
		agent.put("version", 1);
		payload.put("agent", agent);
		payload.put("username", Config.userName);
		payload.put("password", Config.password);

		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setReadTimeout(10 * 1000);
		conn.setConnectTimeout(10 * 1000);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);

		conn.connect();
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(payload.toString());
		wr.close();

		// Read/Parse the input
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		StringBuilder sb = new StringBuilder();
		String inByte;
		while ((inByte = in.readLine()) != null) {
			sb.append(inByte);
		}
		
		System.out.println(sb.toString());

		return new JSONObject(sb.toString());
	}
}
