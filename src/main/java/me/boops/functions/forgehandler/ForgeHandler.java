package me.boops.functions.forgehandler;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.network.FetchRemoteText;

public class ForgeHandler {
	
	public static JSONObject jsonMaster = new JSONObject();
	public static String versionID = "";
	
	public ForgeHandler() {
		
		for(int i = 0; i < Main.args.length; i++) {
			
			if(Main.args[i].equalsIgnoreCase("--with-forge")) {
				ForgeHandler.jsonMaster = new JSONObject(new FetchRemoteText().fetch("https://files.minecraftforge.net/maven/net/minecraftforge/forge/json"));
				ForgeHandler.versionID = Main.args[i + 1];
				new GetVersionMeta().meta();
			}
		}
	}
}
