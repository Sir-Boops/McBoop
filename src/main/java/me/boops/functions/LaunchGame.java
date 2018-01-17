package me.boops.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import me.boops.functions.file.CreateFolder;

public class LaunchGame {

	public LaunchGame(List<String> libs, String dirS, String version, String[] user, JSONObject versionMeta) {

		new CreateFolder(dirS + "base");

		String cleanLibs = cleanLibsPaths(libs);
		String launchArgs = ("java -Xms256M -Xmx1G -Djava.library.path=" + dirS + "natives" + File.separator + " -cp "
				+ cleanLibs + dirS + "versions" + File.separator + version + ".jar" + " net.minecraft.client.main.Main "
				+ genMCArgs(dirS, version, user, versionMeta));

		System.out.println(launchArgs);

		//System.exit(0);

		try {

			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(launchArgs);

			BufferedReader brErr = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
			BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));

			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			String lineErr = null;
			while ((lineErr = brErr.readLine()) != null) {
				System.out.println(lineErr);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String cleanLibsPaths(List<String> libs) {

		String ans = "";
		for (int i = 0; i < libs.size(); i++) {
			ans += libs.get(i) + File.pathSeparator;
		}
		return ans;
	}

	private String genMCArgs(String dirS, String version, String[] user, JSONObject versionMeta) {
		String ans = "";

		// Newer versions use a JSONArray older versions use a string
		// Just run through both and keep appending to ans!

		// If newer
		if (versionMeta.has("arguments")) {
			JSONArray arr = versionMeta.getJSONObject("arguments").getJSONArray("game");
			for (int i = 0; i < arr.length(); i++) {
				if (arr.get(i) instanceof String) {
					if (arr.getString(i).indexOf("--") == 0) {
						ans += arr.getString(i) + " ";
					} else {
						ans += getVar(arr.getString(i), dirS, version, user, versionMeta) + " ";
					}
				}
			}
		}
		
		// If the version is older
		if(versionMeta.has("minecraftArguments")) {
			String[] arr = versionMeta.getString("minecraftArguments").split(" ");
			for(int i = 0; i < arr.length; i++) {
				if(arr[i].indexOf("--") == 0) {
					ans += arr[i] + " ";
				} else {
					ans += getVar(arr[i], dirS, version, user, versionMeta) + " ";
				}
			}
		}

		return ans.substring(0, ans.lastIndexOf(" "));
	}

	private String getVar(String var, String dirS, String version, String[] user, JSONObject versionMeta) {
		String ans = "";

		if (var.equalsIgnoreCase("${auth_player_name}")) {
			ans = user[3];
		}

		if (var.equalsIgnoreCase("{version_name}")) {
			ans = version;
		}

		if (var.equalsIgnoreCase("${game_directory}")) {
			ans = (dirS + "base" + File.separator);
		}

		if (var.equalsIgnoreCase("${assets_root}") || var.equalsIgnoreCase("${game_assets}")) {
			ans = (dirS + "assets" + File.separator);
		}

		if (var.equalsIgnoreCase("${assets_index_name}")) {
			ans = versionMeta.getString("assets");
		}

		if (var.equalsIgnoreCase("${auth_uuid}")) {
			ans = user[2];
		}

		if (var.equalsIgnoreCase("${auth_access_token}") || var.equalsIgnoreCase("${auth_session}")) {
			ans = user[0];
		}

		if (var.equalsIgnoreCase("${user_type}")) {
			ans = "{}";
		}

		if (var.equalsIgnoreCase("${version_type}")) {
			ans = versionMeta.getString("type");
		}

		return ans;
	}

}
