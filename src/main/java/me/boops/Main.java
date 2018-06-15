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

    // Home dir is the base dir that mcboop works in
    static public String home_dir = System.getProperty("user.home") + File.separator + ".mcboop" + File.separator;
    static public String http_user = "Mozilla/5.0 (X11; Linux x86_64; rv:60.0) Gecko/20100101 Firefox/60.0";
    static public String rand_string = "";
    static public String base_os_name = (System.getProperty("os.name").split(" ")[0].toLowerCase());

    public static void main(String[] args) throws Exception {

        Main.rand_string = new BigInteger(32, Main.random).toString();

        // List of things to do
        // Check/Create a hidden dir to use
        // Login/Reauth
        // Setup profile
        // Verify game files are present
        // Extract natives
        // Run game

        // Run non-mc realted commands
        new CommandParser(args);

        // Check for a base dir
        // If it dosn't exist create it
        new CreateFolder(Main.home_dir);

        // Init the profile manager
        // All a profile is, is a folder
        // that launches a spefic version!
        System.out.println("");
        String[] profile = new ProfileManager().check(args);
        // 0 = MC Version
        // 1 = Profile Name
        // 2 = Profile Path
        // 3 = Forge Version
        System.out.println("");

        // Try to login or refresh auth for the requested user
        System.out.println("");
        new UserHandler(args);
        System.out.println("");

        // Grab the version index
        // This checks to see if the version the user
        // Is trying to run and if it is returns the
        // launcher meta URL
        System.out.println("");
        new VersionMeta(args, profile[0]);
        System.out.println("");

        // Install/check the assets for
        // This version of MC
        System.out.println("");
        new InstallAssets(profile[2]);
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
        new ForgeHandler(args, profile[3]);
        System.out.println("");

        // Launch the game!
        System.out.println("");
        new LaunchGame(args, profile[2]);
        System.out.println("");

        System.out.println("Run --help for help");

    }
}
