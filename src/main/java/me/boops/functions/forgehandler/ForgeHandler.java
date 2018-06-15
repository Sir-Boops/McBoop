package me.boops.functions.forgehandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.forge_version_handlers.Forge11to124;
import me.boops.functions.forge_version_handlers.Forge125to152;

public class ForgeHandler {

    // These are also used in launching the game
    public static JSONObject versionMeta = new JSONObject();
    public static List<String> libs = new ArrayList<String>();
    public static double forge_version;
    public static String custom_runtime_jar = "";

    public ForgeHandler(String[] launcher_args, String profile_forge_version) {

        System.out.println("Checking if we should use forge");

        for (int i = 0; i < launcher_args.length; i++) {

            if (launcher_args[i].equalsIgnoreCase("--with-forge")) {
                runList(launcher_args[i + 1]);
            }

            if (launcher_args[i].equalsIgnoreCase("--profile") && !profile_forge_version.isEmpty()) {
                runList(profile_forge_version);
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

        System.out.println("Getting forge version meta");
        ForgeHandler.versionMeta = new GetVersionMeta().meta(file_info[0], file_info[1]);
        System.out.println("");

        if (ForgeHandler.forge_version > 15.2) {
            System.out.println("Installing forge libs");
            ForgeHandler.libs = new FetchForgeLibs().fetch();
            libs.add(Main.home_dir + "forge" + File.separator + file_info[0]);
            System.out.println("");
        }

        // And here we check to see if
        // The user requested older versions
        // As they require...a bit of extra work

        if (ForgeHandler.forge_version <= 15.2 && ForgeHandler.forge_version >= 12.5) {
            new Forge125to152(file_info[0], version_id);
        }
        
        if(ForgeHandler.forge_version <= 12.4) {
            new Forge11to124(file_info[0], version_id);
        }
        
        System.out.println("Forge hs been installed!");
    }
}
