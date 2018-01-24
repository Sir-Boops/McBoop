package me.boops;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import me.boops.functions.DownloadClient;
import me.boops.functions.InstallAssets;
import me.boops.functions.InstallLibs;
import me.boops.functions.LaunchGame;
import me.boops.functions.VersionMeta;
import me.boops.functions.commands.CommandParser;
import me.boops.functions.file.CreateFolder;
import me.boops.functions.profilemanager.ProfileManager;
import me.boops.functions.userhandler.UserHandler;

public class Main {
	
	// Get A Random String for natives
	private static SecureRandom random = new SecureRandom();
	
	// Set the args here so anywhere can access them!
	public static String[] args = {};
	
	// Home dir is the base dir that mcboop works in
	static public String homeDir = System.getProperty("user.home") + File.separator + ".mcboop" + File.separator;
	static public String version = "2.0.0";
	static public String HttpUser = "Mozilla/5.0 (X11; Linux x86_64; rv:59.0) Gecko/20100101 Firefox/59.0";
	static public String randString = "";

	public static void main(String[] args) throws Exception {
		
		Main.randString = new BigInteger(32, Main.random).toString();
		Main.args = args;

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
		
		// Init the profile manager
		// All a profile is, is a folder
		// that launches a spefic version!
		new ProfileManager();
		
		// Grab the version index
		// This checks to see if the version the user
		// Is trying to run and if it is returns the
		// launcher meta URL
		new VersionMeta();
		
		//System.out.println(versionMeta);
		
		// Install/check the assets for
		// This version of MC
		new InstallAssets();
		
		// Install libs / natives
		List<String> libs = new InstallLibs().install();
		
		// Download the client
		new DownloadClient();
		
		// Launch the game!
		new LaunchGame(libs);
		
		System.out.println("Run --help for help");

	}
}
