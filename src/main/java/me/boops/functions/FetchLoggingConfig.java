package me.boops.functions;

import org.json.JSONObject;

import me.boops.net.DownloadFile;

public class FetchLoggingConfig {
	public FetchLoggingConfig(JSONObject json, String path) throws Exception {
		
		String pathLog = path + "log_configs/";
		String URL = json.getJSONObject("client").getJSONObject("file").getString("url");
		String fileName = json.getJSONObject("client").getJSONObject("file").getString("id");
		
		new DownloadFile().Download(pathLog, URL, fileName);
		
	}
}
