package me.boops;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import org.json.JSONObject;

import me.boops.functions.DownloadClient;
import me.boops.functions.InstallAssets;
import me.boops.functions.InstallLibs;
import me.boops.functions.LaunchGame;
import me.boops.functions.ProfileManager;
import me.boops.functions.VersionVerifyMeta;
import me.boops.functions.commands.CommandParser;
import me.boops.functions.file.CreateFolder;
import me.boops.functions.network.FetchRemoteText;
import me.boops.functions.userhandler.UserHandler;

public class Main {
	
	// Get A Random String for natives
	private static SecureRandom random = new SecureRandom();
	
	// Home dir is the base dir that mcboop works in
	static public String homeDir = System.getProperty("user.home") + File.separator + ".mcboop" + File.separator;
	static public String version = "2.0.0";
	static public String HttpUser = "Mozilla/5.0 (X11; Linux x86_64; rv:59.0) Gecko/20100101 Firefox/59.0";
	static public String randString = "";

	public static void main(String[] args) throws Exception {
		
		Main.randString = new BigInteger(32, Main.random).toString();

		// List of things to do
		// Check/Create a hidden dir to use
		// Login/Reauth
		// Setup profile
		// Verify game files are present (checking sums?)
		// Extract natives (Unless the current version is the same as the last launched version ?)
		// Run game ( Reading from json? or hard coded launch args ?)
		
		// Run non-launcher related commands
		new CommandParser(args);
		
		
		// Check for a base dir
		// If it dosn't exist create it
		new CreateFolder(Main.homeDir);
		
		// Try to login or refresh auth for the requested user
		new UserHandler(args);
		
		//Setup the profile
		// All a profile is, is a folder
		// that launches a spefic version!
		String profileVersion = new ProfileManager().getProfile(Main.homeDir, args);
		String profileName = new ProfileManager().getProfileName(args);
		
		// Grab the version index
		// This checks to see if the version the user
		// Is trying to run and if it is returns the
		// launcher meta URL
		String versionMetaURL = new VersionVerifyMeta().getMeta(args, profileVersion);
		JSONObject versionMeta = new JSONObject(new FetchRemoteText().fetch(versionMetaURL));
		
		//System.out.println(versionMeta);
		
		// Install/check the assets for
		// This version of MC
		new InstallAssets(versionMeta.getJSONObject("assetIndex"), Main.homeDir);
		
		// Install libs / natives
		List<String> libs = new InstallLibs().install(Main.homeDir, versionMeta.getJSONArray("libraries"));
		
		// Download the client
		new DownloadClient(versionMeta.getJSONObject("downloads"), Main.homeDir, versionMeta.getString("id"));
		
		// Launch the game!
		new LaunchGame(libs, Main.homeDir, versionMeta.getString("id"), versionMeta, profileName);
		
		System.out.println("Run --help for help");

	}
}
