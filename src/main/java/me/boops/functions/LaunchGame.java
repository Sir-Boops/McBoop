package me.boops.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import me.boops.functions.file.CreateFolder;

public class LaunchGame {

	public LaunchGame(List<String> libs, String dirS, String version, String[] user, JSONObject versionMeta) {

		new CreateFolder(dirS + "base");
		
		List<String> launchArr = new ArrayList<String>();

		String cleanLibs = cleanLibsPaths(libs);
		launchArr.add("java");
		launchArr.add("-Xms256M");
		launchArr.add("-Xmx1G");
		launchArr.add("-Djava.library.path=" + dirS + "natives" + File.separator);
		launchArr.add("-cp");
		launchArr.add(cleanLibs + dirS + "versions" + File.separator + version + ".jar");
		launchArr.add(versionMeta.getString("mainClass"));
		launchArr.addAll(genMCArgs(dirS, version, user, versionMeta));

		System.out.println(launchArr);
		//System.exit(0);

		try {

			Process pr = null;
			ProcessBuilder pb = new ProcessBuilder(launchArr);
			pb.directory(new File(dirS + "base"));
			pr = pb.start();

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
		
		System.exit(0);

	}

	private String cleanLibsPaths(List<String> libs) {

		String ans = "";
		for (int i = 0; i < libs.size(); i++) {
			ans += libs.get(i) + File.pathSeparator;
		}
		return ans;
	}

	private List<String> genMCArgs(String dirS, String version, String[] user, JSONObject versionMeta) {
		List<String> ans = new ArrayList<String>();

		// Newer versions use a JSONArray older versions use a string
		// Just run through both and keep appending to ans!

		// If newer
		if (versionMeta.has("arguments")) {
			JSONArray arr = versionMeta.getJSONObject("arguments").getJSONArray("game");
			for (int i = 0; i < arr.length(); i++) {
				if (arr.get(i) instanceof String) {
					if (arr.getString(i).indexOf("${") == 0) {
						ans.add(getVar(arr.getString(i), dirS, version, user, versionMeta));
					} else {
						ans.add(arr.getString(i));
					}
				}
			}
		}
		
		// If the version is older
		if(versionMeta.has("minecraftArguments")) {
			String[] arr = versionMeta.getString("minecraftArguments").split(" ");
			for(int i = 0; i < arr.length; i++) {
				if(arr[i].indexOf("${") == 0) {
					ans.add(getVar(arr[i], dirS, version, user, versionMeta));
				} else {
					ans.add(arr[i]);
				}
			}
		}

		return ans;
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

		if (var.equalsIgnoreCase("${user_type}") || var.equalsIgnoreCase("${user_properties}")) {
			ans = "{}";
		}

		if (var.equalsIgnoreCase("${version_type}")) {
			ans = versionMeta.getString("type");
		}

		return ans;
	}

}
