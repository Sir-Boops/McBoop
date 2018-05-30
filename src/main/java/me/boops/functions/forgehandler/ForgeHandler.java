package me.boops.functions.forgehandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.forge_version_handlers.Forge152;
import me.boops.functions.profilemanager.ProfileManager;

public class ForgeHandler {

    // These are also used in launching the game
    public static JSONObject versionMeta = new JSONObject();
    public static List<String> libs = new ArrayList<String>();
    public static double forge_version;
    public static String custom_runtime_jar = "";

    public ForgeHandler() {

        System.out.println("Checking if we should use forge");

        for (int i = 0; i < Main.args.length; i++) {

            if (Main.args[i].equalsIgnoreCase("--with-forge")) {
                runList(Main.args[i + 1]);
            }

            if (Main.args[i].equalsIgnoreCase("--profile") && !ProfileManager.forgeVersion.isEmpty()) {
                runList(ProfileManager.forgeVersion);
            }
        }
    }

    private void runList(String version_id) {

        System.out.println("Attempting to setup forge");
        System.out.println("");
        System.out.println("Attempting to figure out the forge file name");
        System.out.println("");
        // file_info[0] = file_name
        // file_info[1] = file_path
        String[] file_info = new ForgeFileName().getInfo(version_id);

        if (ForgeHandler.forge_version >= 15.2) {
            System.out.println("Getting forge version meta");
            System.out.println("");
            ForgeHandler.versionMeta = new GetVersionMeta().meta(file_info[0], file_info[1]);
            System.out.println("");
        }

        if (ForgeHandler.forge_version > 15.2) {
            System.out.println("Installing forge libs");
            ForgeHandler.libs = new FetchForgeLibs().fetch();
            libs.add(Main.home_dir + "forge" + File.separator + file_info[0]);
            System.out.println("");
        }

        // And here we check to see if
        // The user requested older versions
        // As they require...a bit of extra work

        if (ForgeHandler.forge_version == 15.2) {
            new Forge152(file_info[0], version_id);
        }
        
        System.out.println("Forge hs been installed!");
    }
}
