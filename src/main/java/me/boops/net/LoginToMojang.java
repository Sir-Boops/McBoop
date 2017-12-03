package me.boops.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import me.boops.base.Cache;
import me.boops.base.SaveTextFile;
import me.boops.functions.ReadFile;

public class LoginToMojang {

	public JSONObject login() throws Exception {
		JSONObject payload = new JSONObject();
		JSONObject agent = new JSONObject();
		agent.put("name", "Minecraft");
		agent.put("version", 1);
		payload.put("agent", agent);
		payload.put("username", Cache.userName);
		payload.put("password", Cache.password);

		return new JSONObject(postURL(new URL("https://authserver.mojang.com/authenticate"), payload.toString()));
	}
	
	public JSONObject refresh() throws Exception {
		
		JSONObject authFile = new ReadFile().Read(Cache.cacheDir + File.separator + "auth.json");
		JSONObject payload = new JSONObject();
		payload.put("accessToken", authFile.getString("accessToken"));
		payload.put("clientToken", authFile.getString("clientToken"));
		
		JSONObject renew = new JSONObject(postURL(new URL("https://authserver.mojang.com/refresh"), payload.toString()));
		JSONObject toSave = new JSONObject();
		
		toSave.put("accessToken", renew.getString("accessToken"));
		toSave.put("clientToken", renew.getString("clientToken"));
		toSave.put("userName", renew.getJSONObject("selectedProfile").getString("name"));
		new SaveTextFile().Save(Cache.cacheDir + File.separator, toSave.toString(), "auth.json");
		
		return renew;	
	}
	
	private String postURL(URL url, String payload) throws Exception {
		
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setReadTimeout(10 * 1000);
		conn.setConnectTimeout(10 * 1000);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux X11; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
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
		
		return sb.toString();
		
	}
	
}
