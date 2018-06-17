package me.boops.functions.launchgame;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import me.boops.functions.InstallAssets;

public class GenerateMCArgs {
	
    private String mc_version_id;
    private JSONObject mc_meta;
    private String profile_path;
    private String[] user;
    
	public List<String> gen(String[] meta, String profile_path, String[] user, JSONObject forge_meta) {
		List<String> ans = new ArrayList<String>();
		this.mc_version_id = meta[0];
		this.mc_meta = new JSONObject(meta[1]);
		this.profile_path = profile_path;
		this.user = user;
		
		if(forge_meta.has("minecraftArguments")) {
		    ans.addAll(parse_meta(forge_meta));
		} else {
		    ans.addAll(parse_meta(this.mc_meta));
		}
		
		return ans;
	}

	private String getVar(String var) {
		String ans = "";

		if (var.equalsIgnoreCase("${auth_player_name}")) {
			ans = this.user[3];
		}

		if (var.equalsIgnoreCase("${version_name}")) {
			ans = this.mc_version_id;
		}

		if (var.equalsIgnoreCase("${game_directory}")) {
			ans = (this.profile_path);
		}

		if (var.equalsIgnoreCase("${assets_root}") || var.equalsIgnoreCase("${game_assets}")) {
			ans = InstallAssets.assets_path;
		}

		if (var.equalsIgnoreCase("${assets_index_name}")) {
			ans = this.mc_meta.getString("assets");
		}

		if (var.equalsIgnoreCase("${auth_uuid}")) {
			ans = this.user[2];
		}

		if (var.equalsIgnoreCase("${auth_access_token}") || var.equalsIgnoreCase("${auth_session}")) {
			ans = this.user[0];
		}

		if (var.equalsIgnoreCase("${user_type}") || var.equalsIgnoreCase("${user_properties}")) {
			ans = "{}";
		}

		if (var.equalsIgnoreCase("${version_type}")) {
			ans = this.mc_meta.getString("type");
		}

		return ans;
	}
	
	private List<String> parse_meta(JSONObject meta) {
	       List<String> ans = new ArrayList<String>();

	        // Newer versions use a JSONArray older versions use a string
	        // Just run through both and keep appending to ans!

	        // If newer
	        if (meta.has("arguments")) {
	            JSONArray arr = meta.getJSONObject("arguments").getJSONArray("game");
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
	        if(meta.has("minecraftArguments")) {
	            String[] arr = meta.getString("minecraftArguments").split(" ");
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
	
}
