package me.boops.functions;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;

import me.boops.base.Cache;

public class AddJVMArgs {
	public String add(JSONObject details) {

		String ans = "java ";

		String nativesDir = Cache.rootDir + "natives" + File.separator; //

		if (details.has("arguments")) {
			JSONArray json = details.getJSONObject("arguments").getJSONArray("jvm");
			for (int i = 0; i < json.length(); i++) {
				if (json.get(i) instanceof String) {
					String[] lines = json.getString(i).split("=");
					if (lines[0].equals("-Djava.library.path")) {
						ans += (lines[0] + "=" + Cache.rootDir + "natives" + File.separator + " ");
					}
					if (lines[0].equals("-Dminecraft.launcher.brand")) {
						ans += (lines[0] + "=" + "McBoop ");
					}
					if (lines[0].equals("-Dminecraft.launcher.version")) {
						ans += (lines[0] + "=" + Cache.mcBoopVersion + " ");
					}
				}
				if (json.get(i) instanceof JSONObject) {
					ans += parseJSONObject(json.getJSONObject(i));
				}
			}
			
			ans += loggingConfig(details);
			ans += "-cp " + classPath(details) + " ";
			ans += details.getString("mainClass");
			
		} else {
			ans += "-Djava.library.path=" + nativesDir + " ";
			ans += loggingConfig(details);
			ans += "-cp " + classPath(details) + " ";
			ans += details.getString("mainClass");
		}

		return ans;
	}

	private String classPath(JSONObject details) {
		String ans = "\"";
		for (int i = 0; i < Cache.libPaths.size(); i++) {
			ans += Cache.libPaths.get(i) + File.pathSeparator;
		}
		ans += Cache.rootDir + "versions" + File.separator + details.getString("id") + ".jar";
		ans += "\"";
		return ans;
	}

	private String loggingConfig(JSONObject details) {
		String ans = "";
		if (details.has("logging")) {
			String path = (Cache.rootDir + "assets" + File.separator + "log_configs" + File.separator
					+ details.getJSONObject("logging").getJSONObject("client").getJSONObject("file").getString("id"));
			ans = details.getJSONObject("logging").getJSONObject("client").getString("argument")
					.replaceFirst("\\$\\{path\\}", "") + path + " ";
		}
		return ans;
	}

	private String parseJSONObject(JSONObject object) {
		String ans = "";
		JSONObject OS = object.getJSONArray("rules").getJSONObject(0).getJSONObject("os");
		if (OS.getString("name").equalsIgnoreCase(Cache.OS)) {
			if (OS.has("version")) {
				if (OS.getString("version").replaceAll("[^a-zA-Z0-9]", "").contains(System.getProperty("os.version")
						.substring(0, System.getProperty("os.version").lastIndexOf(".")))) {
					// For OS and for this Version of said OS
					if (object.get("value") instanceof String) {
						ans += object.getString("value") + " ";
					} else {
						for (int i = 0; i < object.getJSONArray("value").length(); i++) {
							String[] split = object.getJSONArray("value").getString(i).split("=");
							if(split[1].contains(" ")) {
								ans += (split[0] + "=" + "\"" + split[1] + "\" ");
							} else {
								ans += object.getJSONArray("value").getString(i) + " ";
							}
						}
					}
				}
			} else {
				// For this OS and does not have a version
				if (object.get("value") instanceof String) {
					ans += object.getString("value") + " ";
				} else {
					for (int i = 0; i < object.getJSONArray("value").length(); i++) {
						String[] split = object.getJSONArray("value").getString(i).split("=");
						if(split[1].contains(" ")) {
							ans += (split[0] + "=" + "\"" + split[1] + "\" ");
						} else {
							ans += object.getJSONArray("value").getString(i) + " ";
						}
					}
				}
			}
		}
		return ans;
	}
}
