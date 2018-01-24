package me.boops.functions.launchgame;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import me.boops.functions.InstallAssets;
import me.boops.functions.VersionMeta;
import me.boops.functions.profilemanager.ProfileManager;
import me.boops.functions.userhandler.UserHandler;

public class GenerateMCArgs {
	
	public List<String> gen() {
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
			ans = InstallAssets.path;
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
