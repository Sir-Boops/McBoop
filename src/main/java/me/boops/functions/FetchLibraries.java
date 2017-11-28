package me.boops.functions;

import org.json.JSONArray;
import org.json.JSONObject;

import me.boops.Config;

public class FetchLibraries {
	
	public FetchLibraries(JSONObject details, String pathFirst) throws Exception {
		
		JSONArray libs = details.getJSONArray("libraries");
		System.out.println(details.toString());
		
		for(int i = 0; i < libs.length(); i++) {
			if(libs.getJSONObject(i).has("downloads")) {
				if(libs.getJSONObject(i).getJSONObject("downloads").has("artifact")) {
					if(libs.getJSONObject(i).getJSONObject("downloads").has("classifiers") && details.getInt("minimumLauncherVersion") < 20) {
						//if(!libs.getJSONObject(i).getJSONObject("downloads").getJSONObject("classifiers").has("natives-" + Config.OS)) {
						//	download(libs.getJSONObject(i).getJSONObject("downloads").getJSONObject("artifact"), pathFirst, libs.getJSONObject(i).has("extract"));
						//}
						download(libs.getJSONObject(i).getJSONObject("downloads").getJSONObject("artifact"), pathFirst, libs.getJSONObject(i).has("extract"));
					} else {
						download(libs.getJSONObject(i).getJSONObject("downloads").getJSONObject("artifact"), pathFirst, libs.getJSONObject(i).has("extract"));
					}
				}
				if(libs.getJSONObject(i).getJSONObject("downloads").has("classifiers")) {
					if(libs.getJSONObject(i).getJSONObject("downloads").getJSONObject("classifiers").has("natives-" + Config.OS)) {
						download(libs.getJSONObject(i).getJSONObject("downloads").getJSONObject("classifiers").getJSONObject("natives-" + Config.OS), pathFirst, libs.getJSONObject(i).has("extract"));
					}
				}
			}
		}
	}
	
	private void download(JSONObject json, String pathFirst, boolean shouldExtract) throws Exception {
		String URL = json.getString("url");
		String path = pathFirst + (json.getString("path").substring(0, json.getString("path").lastIndexOf("/") + 1));
		String fileName = URL.substring(URL.lastIndexOf("/") + 1, URL.length());
		Config.libPaths.add(path + fileName);
		if(shouldExtract) {
			Config.extractPaths.add(path + fileName);
		}
		System.out.println("Downloading Lib: " + fileName);
		new DownloadFile().Download(path, URL, fileName);
	}
	
}
