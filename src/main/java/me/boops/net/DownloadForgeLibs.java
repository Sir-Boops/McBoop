package me.boops.net;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import me.boops.Cache;
import me.boops.functions.ReadForgeVersion;

public class DownloadForgeLibs {
	public DownloadForgeLibs(JSONObject versionMeta) throws Exception {

		JSONObject version = new ReadForgeVersion().read();
		JSONArray libs = version.getJSONArray("libraries");
		String baseMojang = "https://libraries.minecraft.net/";

		for (int i = 0; i < libs.length(); i++) {
			String[] dep = libs.getJSONObject(i).getString("name").split(":");
			String path = dep[0].replace(".", "/");
			String name = dep[1];
			String depVersion = dep[2];
			String MojangURL = baseMojang + path + "/" + name + "/" + depVersion + "/" + name + "-" + depVersion + ".jar";
			String filePath = Cache.cacheDir + "libraries/" + dep[0].replace(".", File.separator) + File.separator + name + File.separator + depVersion + File.separator;
			String fullName = name + "-" + depVersion + ".jar";

			if (!path.contains("net/minecraft")) {
				if (!checkForLib(filePath + fullName)) {
					String URL = jarLink(dep[0] + "/" + name + "/" + depVersion, fullName);
					System.out.println(URL);
					new DownloadFile().Download(filePath, URL, fullName);
					Cache.libPaths.add(filePath + fullName);
				}
			} else {
				if (!checkForLib(filePath + fullName) && !path.contains("forge")) {
					System.out.println("Downloading Lib: " + fullName);
					new DownloadFile().Download(filePath, MojangURL, fullName);
					Cache.libPaths.add(filePath + fullName);
				}
			}
		}
	}

	private boolean checkForLib(String loc) {
		boolean ans = false;
		for (int i = 0; i < Cache.libPaths.size(); i++) {
			if (Cache.libPaths.get(i).equalsIgnoreCase(loc)) {
				ans = true;
			}
		}
		return ans;
	}
	
	private String jarLink(String toSearch, String libName) throws Exception {
		Document doc = Jsoup.connect("https://mvnrepository.com/artifact/" + toSearch).get();
		String link = doc.getElementsContainingOwnText("View All").get(0).attr("href") + "/" + libName;
		return link;
	}
}
