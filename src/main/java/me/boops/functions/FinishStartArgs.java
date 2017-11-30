package me.boops.functions;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;

import me.boops.base.Cache;

public class FinishStartArgs {
	
	// Launch Args
	private String playerName = "";
	private String version = "";
	private String gameDir = "";
	private String addetsDir = "";
	private String assetName = "";
	private String uuid = "";
	private String accessToken = "";
	private String userProperties= "";
	private String versionType = "";
	private String userType = "";
	
	public String Finish(String oldArgs, JSONObject ticket, JSONObject details) {
		
		//JSONArray arrArgs = details.getJSONObject("arguments").getJSONArray("game");
		String gameArgs = " ";
		
		// Launch Args
		playerName = ticket.getJSONObject("selectedProfile").getString("name"); // ${auth_player_name}
		version = details.getString("id"); // ${version_name}
		gameDir = Cache.rootDir; // ${game_directory}
		addetsDir = Cache.rootDir + "assets" + File.separator; // ${assets_root}
		assetName = details.getString("assets"); // ${assets_index_name}
		uuid = ticket.getJSONObject("selectedProfile").getString("id"); // ${auth_uuid}
		accessToken = ticket.getString("accessToken"); // ${auth_access_token}
		userProperties = "{}"; // ${user_properties}
		versionType = details.getString("type"); // ${version_type}
		userType = "mojang"; // ${user_type}
		
		if(details.has("minecraftArguments")) {
			String[] args = details.getString("minecraftArguments").split(" ");
			for(int i = 0; i < args.length; i++) {
				if(args[i].contains("${")) {
					gameArgs += replace(args[i]) + " ";
				} else {
					gameArgs += args[i] + " ";
				}
			}
		}
		
		if(details.has("arguments")) {
			if(details.getJSONObject("arguments").has("game")) {
				JSONArray arr = details.getJSONObject("arguments").getJSONArray("game");
				for(int i = 0; i < arr.length(); i++) {
					if(arr.get(i) instanceof String) {
						if(arr.getString(i).contains("${")) {
							gameArgs += replace(arr.getString(i)) + " ";
						} else {
							gameArgs += arr.getString(i) + " ";
						}
					}
				}
			}
		}
		
		return (oldArgs + gameArgs);
	}
	
	// Replace args with the proper args
	private String replace(String old) {
		
		if(old.equalsIgnoreCase("${auth_player_name}")) {
			return this.playerName;
		}
		if(old.equalsIgnoreCase("${version_name}")) {
			return this.version;
		}
		if(old.equalsIgnoreCase("${game_directory}")) {
			return this.gameDir;
		}
		if(old.equalsIgnoreCase("${assets_root}") || old.equalsIgnoreCase("${game_assets}")) {
			return this.addetsDir;
		}
		if(old.equalsIgnoreCase("${assets_index_name}")) {
			return this.assetName;
		}
		if(old.equalsIgnoreCase("${auth_uuid}")) {
			return this.uuid;
		}
		if(old.equalsIgnoreCase("${auth_access_token}") || old.equalsIgnoreCase("${auth_session}")) {
			return this.accessToken;
		}
		if(old.equalsIgnoreCase("${user_properties}")) {
			return this.userProperties;
		}
		if(old.equalsIgnoreCase("${version_type}")) {
			return this.versionType;
		}
		if(old.equalsIgnoreCase("${user_type}")) {
			return this.userType;
		}
		
		return " ";
		
	}
	
}
