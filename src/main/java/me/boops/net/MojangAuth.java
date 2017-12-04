package me.boops.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import me.boops.base.Cache;
import me.boops.files.ReadFile;
import me.boops.files.SaveTextFile;

public class MojangAuth {

	public void login(String username, String password) throws Exception {
		JSONObject payload = new JSONObject();
		JSONObject agent = new JSONObject();
		agent.put("name", "Minecraft");
		agent.put("version", 1);
		payload.put("agent", agent);
		payload.put("username", username);
		payload.put("password", password);

		JSONObject ans = new JSONObject(postURL(new URL("https://authserver.mojang.com/authenticate"), payload.toString()));
		JSONObject userData = new JSONObject();
		
		System.out.println(ans);

		userData.put("accessToken", ans.getString("accessToken"));
		userData.put("clientToken", ans.getString("clientToken"));
		userData.put("userName", ans.getJSONObject("selectedProfile").getString("name"));
		userData.put("UUID", ans.getJSONObject("selectedProfile").getString("id"));

		saveToAuth(userData);
	}

	public void refresh(String username) throws Exception {
		JSONObject userData = getUser(username);

		JSONObject payload = new JSONObject();
		payload.put("accessToken", userData.getString("accessToken"));
		payload.put("clientToken", userData.getString("clientToken"));

		JSONObject renew = new JSONObject(postURL(new URL("https://authserver.mojang.com/refresh"), payload.toString()));
		JSONObject toSave = new JSONObject();

		toSave.put("accessToken", renew.getString("accessToken"));
		toSave.put("clientToken", renew.getString("clientToken"));
		toSave.put("userName", renew.getJSONObject("selectedProfile").getString("name"));
		toSave.put("UUID", renew.getJSONObject("selectedProfile").getString("id"));
		
		saveToAuth(toSave);
	}

	public void validate(String username) throws Exception {
		JSONObject userData = getUser(username);
		
		JSONObject payload = new JSONObject();
		payload.put("accessToken", userData.getString("accessToken"));
		payload.put("clientToken", userData.getString("clientToken"));
		
		try {
			new JSONObject(postURL(new URL("https://authserver.mojang.com/validate"), payload.toString()));
		} catch (Exception e) {
			System.out.println("Error with your user please login again!");
			System.exit(0);
		}
	}
	
	public JSONObject getUser(String username) throws Exception {
		
		JSONArray arr = new JSONArray(new ReadFile().Read(Cache.cacheDir + File.separator + "auth.json"));
		JSONObject ans = null;
		
		for(int i = 0; i < arr.length(); i++) {
			if(arr.getJSONObject(i).getString("userName").equalsIgnoreCase(username)) {
				ans = arr.getJSONObject(i);
			}
		}
		return ans;
	}

	private void saveToAuth(JSONObject userData) throws Exception {
		if (new File(Cache.cacheDir + File.separator + "auth.json").exists()) {
			// Load the current auth file
			JSONArray currentFile = new JSONArray(new ReadFile().Read(Cache.cacheDir + File.separator + "auth.json"));
			JSONArray newFile = new JSONArray();
			
			newFile.put(userData);

			// See if the current user is in the auth file
			// And if they are remove them and set the new one
			for (int i = 0; i < currentFile.length(); i++) {
				if (!currentFile.getJSONObject(i).getString("userName").equalsIgnoreCase(userData.getString("userName"))) {
					newFile.put(currentFile.get(i));
				}
			}
			
			new File(Cache.cacheDir + File.separator + "auth.json").delete();
			new SaveTextFile(Cache.cacheDir + File.separator, newFile.toString(), "auth.json");
			
		} else {
			// Try to make the folder just in case
			if(!new File(Cache.cacheDir + File.separator).exists()) {
				new File(Cache.cacheDir + File.separator).mkdirs();
			}
			
			// Put the userdata in a jsonarray and write to the file!
			JSONArray authFile = new JSONArray();
			authFile.put(userData);
			new SaveTextFile(Cache.cacheDir + File.separator, authFile.toString(), "auth.json");
		}
	}

	private String postURL(URL url, String payload) throws Exception {

		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setReadTimeout(10 * 1000);
		conn.setConnectTimeout(10 * 1000);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("User-Agent", "McBoop/" + Cache.mcBoopVersion);
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
