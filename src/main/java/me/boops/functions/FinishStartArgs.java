package me.boops.functions;

import java.io.File;

import org.json.JSONObject;

import me.boops.base.Config;

public class FinishStartArgs {
	
	public String Finish(String oldArgs, JSONObject ticket, JSONObject details) {
		
		//JSONArray arrArgs = details.getJSONObject("arguments").getJSONArray("game");
		String gameArgs = "";
		gameArgs += " --username " + ticket.getJSONObject("selectedProfile").getString("name") + " ";
		gameArgs += "--version " + details.getString("id") + " ";
		gameArgs += "--gameDir " + Config.rootDir + " ";
		gameArgs += "--assetsDir " + Config.rootDir + "assets" + File.separator + " ";
		gameArgs += "--assetIndex " + details.getString("assets") + " ";
		gameArgs += "--accessToken " + ticket.getString("accessToken") + " ";
		gameArgs += "--uuid " + ticket.getJSONObject("selectedProfile").getString("id") + " ";
		gameArgs += "--userProperties " + "{} ";
		gameArgs += "--versionType " + details.getString("type");
		
		return (oldArgs + gameArgs);
	}
}
