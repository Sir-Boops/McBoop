package me.boops.functions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

public class FetchVersionMeta {

	public JSONObject fetch(String URL) {

		JSONObject ans = new JSONObject();

		try {

			URL url = new URL(URL);

			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10 * 1000);
			conn.setConnectTimeout(10 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux X11; x64; rv:59.0) Gecko/20100101 Firefox/59.0");

			conn.connect();

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
