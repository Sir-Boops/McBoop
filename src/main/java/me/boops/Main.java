package me.boops;

import java.io.File;
import java.util.List;

import org.json.JSONObject;

import me.boops.functions.DownloadClient;
import me.boops.functions.InstallAssets;
import me.boops.functions.InstallLibs;
import me.boops.functions.LaunchGame;
import me.boops.functions.UserHander;
import me.boops.functions.VersionVerifyMeta;
import me.boops.functions.file.CreateFolder;
import me.boops.functions.network.FetchRemoteText;

public class Main {
	
	// Home dir is the base dir that mcboop works in
	static private String homeDir = System.getProperty("user.home") + File.separator + ".mcboop" + File.separator;
	static public String version = "2.0.0";

	public static void main(String[] args) throws Exception {

		// List of things to do
		// Check/Create a hidden dir to use
		// Login/Reauth
		// Verify game files are present (checking sums?)
		// Extract natives (Unless the current version is the same as the last launched version ?)
		// Run game ( Reading from json? or hard coded launch args ?)
		
		
		// Check for a base dir
		// If it dosn't exist create it
		new CreateFolder(Main.homeDir);
		
		// Try to login or refresh auth for the requested user
		// 0 => accessToken
		// 1 => clientToken
		// 2 => UUID
		// 3 => username
		String[] user = new UserHander().getUser(Main.homeDir, args); // Look into verfiying over refreshing every time! http://wiki.vg/Authentication
		
		System.out.println("Running MC using user: " + user[3]);
		//String[] user = {"i","am","very","gay"};
		
		// Grab the version index
		// This checks to see if the version the user
		// Is trying to run and if it is returns the
		// launcher meta URL
		String versionMetaURL = new VersionVerifyMeta().getMeta(args);
		JSONObject versionMeta = new JSONObject(new FetchRemoteText().fetch(versionMetaURL));
		
		System.out.println(versionMeta);
		
		// Install/check the assets for
		// This version of MC
		new InstallAssets(versionMeta.getJSONObject("assetIndex"), Main.homeDir);
		
		// Install libs / natives
		List<String> libs = new InstallLibs().install(Main.homeDir, versionMeta.getJSONArray("libraries"));
		
		// Download the client
		new DownloadClient(versionMeta.getJSONObject("downloads"), Main.homeDir, versionMeta.getString("id"));
		
		// Launch the game!
		new LaunchGame(libs, Main.homeDir, versionMeta.getString("id"), user, versionMeta);
		
		System.exit(0);
		System.out.println("Run --help for help");

	}
}
