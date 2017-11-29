package me.boops.functions;

import org.json.JSONArray;
import org.json.JSONObject;

import me.boops.base.Cache;
import me.boops.net.DownloadFile;

public class FetchLibraries {
	
	public FetchLibraries(JSONObject details, String pathFirst) throws Exception {
		
		JSONArray libs = details.getJSONArray("libraries");
		
		for(int i = 0; i < libs.length(); i++) {
			if(libs.getJSONObject(i).has("downloads")) {
				if(libs.getJSONObject(i).getJSONObject("downloads").has("artifact")) {
					download(libs.getJSONObject(i).getJSONObject("downloads").getJSONObject("artifact"), pathFirst, libs.getJSONObject(i).has("extract"));
				}
				if(libs.getJSONObject(i).getJSONObject("downloads").has("classifiers")) {
					if(libs.getJSONObject(i).getJSONObject("downloads").getJSONObject("classifiers").has("natives-" + Cache.OS)) {
						download(libs.getJSONObject(i).getJSONObject("downloads").getJSONObject("classifiers").getJSONObject("natives-" + Cache.OS), pathFirst, libs.getJSONObject(i).has("extract"));
					}
				}
			}
		}
	}
	
	private void download(JSONObject json, String pathFirst, boolean shouldExtract) throws Exception {
		String URL = json.getString("url");
		String path = pathFirst + (json.getString("path").substring(0, json.getString("path").lastIndexOf("/") + 1));
		String fileName = URL.substring(URL.lastIndexOf("/") + 1, URL.length());
		Cache.libPaths.add(path + fileName);
		if(shouldExtract) {
			Cache.extractPaths.add(path + fileName);
		}
		System.out.println("Downloading Lib: " + fileName);
		new DownloadFile().Download(path, URL, fileName);
	}
	
}
