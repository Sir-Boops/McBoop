package me.boops;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;

import me.boops.functions.InstallAssets;
import me.boops.functions.InstallLibs;
import me.boops.functions.VersionMeta;
import me.boops.functions.commands.CommandParser;
import me.boops.functions.file.CreateFolder;
import me.boops.functions.forgehandler.ForgeHandler;
import me.boops.functions.launchgame.LaunchGame;
import me.boops.functions.network.DownloadClient;
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
	static public String HttpUser = "Mozilla/5.0 (X11; Linux x86_64; rv:60.0) Gecko/20100101 Firefox/60.0";
	static public String randString = "";
	static public String base_os_name = (System.getProperty("os.name").split(" ")[0].toLowerCase());

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
		new CommandParser();
		
		
		// Check for a base dir
		// If it dosn't exist create it
		new CreateFolder(Main.homeDir);
		
		// Init the profile manager
		// All a profile is, is a folder
		// that launches a spefic version!
		System.out.println("");
		new ProfileManager();
		System.out.println("");
		
		// Try to login or refresh auth for the requested user
		System.out.println("");
		new UserHandler();
		System.out.println("");
		
		// Grab the version index
		// This checks to see if the version the user
		// Is trying to run and if it is returns the
		// launcher meta URL
		System.out.println("");
		new VersionMeta();
		System.out.println("");
		
		// Install/check the assets for
		// This version of MC
		System.out.println("");
		new InstallAssets();
		System.out.println("");
		
		// Install libs / natives
		System.out.println("");
		new InstallLibs();
		System.out.println("");
		
		// Download the client
		System.out.println("");
		new DownloadClient();
		System.out.println("");
		
		// Try to setup forge!
		System.out.println("");
		new ForgeHandler();
		System.out.println("");
		
		// Launch the game!
		System.out.println("");
		new LaunchGame();
		System.out.println("");
		
		System.out.println("Run --help for help");

	}
}
