package me.boops.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;

import me.boops.Main;
import me.boops.functions.file.CreateFolder;
import me.boops.functions.profilemanager.ProfileManager;
import me.boops.functions.userhandler.UserHandler;

public class LaunchGame {

	public LaunchGame() {
		
		String ProfilePath = Main.homeDir + "profiles" + File.separator + ProfileManager.name + File.separator;
		
		new CreateFolder(ProfilePath);
		
		List<String> launchArr = new ArrayList<String>();

		String cleanLibs = cleanLibsPaths(InstallLibs.libs);
		launchArr.add("java");
		launchArr.add("-Xms256M");
		launchArr.add("-Xmx2G");
		launchArr.add("-Djava.library.path=" + Main.homeDir + "natives-" + Main.randString + File.separator);
		launchArr.add("-cp");
		launchArr.add(cleanLibs + Main.homeDir + "versions" + File.separator + VersionMeta.ID + ".jar");
		launchArr.add(VersionMeta.Meta.getString("mainClass"));
		launchArr.addAll(genMCArgs());

		System.out.println(launchArr);
		//System.exit(0);

		try {

			Process pr = null;
			ProcessBuilder pb = new ProcessBuilder(launchArr);
			pb.directory(new File(ProfilePath));
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
		
		// Delete the natives dir on exit
		try {
			FileUtils.deleteDirectory(new File(Main.homeDir + "natives-" + Main.randString + File.separator));
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

	private List<String> genMCArgs() {
		List<String> ans = new ArrayList<String>();

		// Newer versions use a JSONArray older versions use a string
		// Just run through both and keep appending to ans!

		// If newer
		if (VersionMeta.Meta.has("arguments")) {
			JSONArray arr = VersionMeta.Meta.getJSONObject("arguments").getJSONArray("game");
			for (int i = 0; i < arr.length(); i++) {
				if (arr.get(i) instanceof String) {
					if (arr.getString(i).indexOf("${") == 0) {
						ans.add(getVar(arr.getString(i)));
					} else {
						ans.add(arr.getString(i));
					}
				}
			}
		}
		
		// If the version is older
		if(VersionMeta.Meta.has("minecraftArguments")) {
			String[] arr = VersionMeta.Meta.getString("minecraftArguments").split(" ");
			for(int i = 0; i < arr.length; i++) {
				if(arr[i].indexOf("${") == 0) {
					ans.add(getVar(arr[i]));
				} else {
					ans.add(arr[i]);
				}
			}
		}

		return ans;
	}

	private String getVar(String var) {
		String ans = "";

		if (var.equalsIgnoreCase("${auth_player_name}")) {
			ans = UserHandler.user[3];
		}

		if (var.equalsIgnoreCase("${version_name}")) {
			ans = VersionMeta.ID;
		}

		if (var.equalsIgnoreCase("${game_directory}")) {
			ans = (ProfileManager.path);
		}

		if (var.equalsIgnoreCase("${assets_root}") || var.equalsIgnoreCase("${game_assets}")) {
			ans = (Main.homeDir + "assets" + File.separator);
		}

		if (var.equalsIgnoreCase("${assets_index_name}")) {
			ans = VersionMeta.Meta.getString("assets");
		}

		if (var.equalsIgnoreCase("${auth_uuid}")) {
			ans = UserHandler.user[2];
		}

		if (var.equalsIgnoreCase("${auth_access_token}") || var.equalsIgnoreCase("${auth_session}")) {
			ans = UserHandler.user[0];
		}

		if (var.equalsIgnoreCase("${user_type}") || var.equalsIgnoreCase("${user_properties}")) {
			ans = "{}";
		}

		if (var.equalsIgnoreCase("${version_type}")) {
			ans = VersionMeta.Meta.getString("type");
		}

		return ans;
	}

}
