package me.boops.functions.forgehandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.network.FetchRemoteText;
import me.boops.functions.profilemanager.ProfileManager;

public class ForgeHandler {
	
	public static JSONObject jsonMaster = new JSONObject();
	public static String versionID = "";
	public static JSONObject versionMeta = new JSONObject();
	public static List<String> libs = new ArrayList<String>();
	
	public ForgeHandler() {
		
		for(int i = 0; i < Main.args.length; i++) {
			
			if(Main.args[i].equalsIgnoreCase("--with-forge")) {
				ForgeHandler.jsonMaster = new JSONObject(new FetchRemoteText().fetch("https://files.minecraftforge.net/maven/net/minecraftforge/forge/json"));
				ForgeHandler.versionID = Main.args[i + 1];
				new ForgeFileName();
				ForgeHandler.versionMeta = new GetVersionMeta().meta();
				ForgeHandler.libs = new FetchForgeLibs().fetch();
				libs.add(Main.homeDir + "forge" + File.separator + ForgeFileName.fileName);
			}
			
			if(Main.args[i].equalsIgnoreCase("--profile") && !ProfileManager.forgeVersion.isEmpty()) {
				System.out.println("Attempting to setup forge");
				System.out.println("Loading forge meta json");
				ForgeHandler.jsonMaster = new JSONObject(new FetchRemoteText().fetch("https://files.minecraftforge.net/maven/net/minecraftforge/forge/json"));
				ForgeHandler.versionID = ProfileManager.forgeVersion;
				new ForgeFileName();
				System.out.println("Setting up envirment for forge");
				ForgeHandler.versionMeta = new GetVersionMeta().meta();
				ForgeHandler.libs = new FetchForgeLibs().fetch();
				libs.add(Main.homeDir + "forge" + File.separator + ForgeFileName.fileName);
			}
		}
	}
}
