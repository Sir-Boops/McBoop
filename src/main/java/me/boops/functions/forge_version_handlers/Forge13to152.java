package me.boops.functions.forge_version_handlers;

import java.io.File;

import me.boops.Main;
import me.boops.functions.VersionMeta;
import me.boops.functions.file.CreateFolder;
import me.boops.functions.file.ExtractZip;
import me.boops.functions.file.MergeTwoJars;
import me.boops.functions.forgehandler.ForgeHandler;
import me.boops.functions.network.DownloadClient;
import me.boops.functions.network.FetchRemoteContent;

public class Forge13to152 {
    public Forge13to152(String file_name, String forge_id) {

        String forge_path = (Main.home_dir + "forge" + File.separator);
        String new_jar = (VersionMeta.ID + "-forge-" + forge_id + ".jar");
        String dot_mc = get_dot_minecraft_path();
        String lib_path = (dot_mc + "lib" + File.separator);

        String url_base = "https://files.minecraftforge.net/fmllibs/";

        if (!new File(forge_path + new_jar).exists()) {
            new MergeTwoJars(DownloadClient.jarPath, (forge_path + file_name), (forge_path + new_jar));
        }

        // Try create .minecraft/lib
        new CreateFolder(dot_mc);
        new CreateFolder(lib_path);

        // Download 1.5.X Libs
        if (ForgeHandler.forge_version >= 15 && ForgeHandler.forge_version <= 15.2) {
            // Download the Libs zip for 1.5.X
            if (!new File(forge_path + "fml_libs15.zip").exists()) {
                System.out.println("Downloading: fml_libs15.zip");
                new FetchRemoteContent().file((url_base + "fml_libs15.zip"), forge_path, "");
                System.out.println("");
            }

            // Extract the fml_libs15.zip contents
            System.out.println("Checking if we need to extract contents from: fml_libs15.zip");
            new ExtractZip(forge_path + "fml_libs15.zip", lib_path);
            System.out.println("");

            // Finally grab the deobvs zip
            if (!new File(lib_path + "deobfuscation_data_" + VersionMeta.ID + ".zip").exists()) {
                System.out.println("Downloading: deobfuscation_data_" + VersionMeta.ID + ".zip");
                new FetchRemoteContent().file((url_base + "deobfuscation_data_" + VersionMeta.ID + ".zip"), lib_path, "");
                System.out.println("");
            }
        }

        // Download 1.4.X & 1.3.X Libs
        if (ForgeHandler.forge_version >= 13 && ForgeHandler.forge_version <= 14.7) {
            // Download the needed libs for 1.4.X
            if (!new File(forge_path + "fml_libs.zip").exists()) {
                System.out.println("Downloading: fml_libs.zip");
                new FetchRemoteContent().file((url_base + "fml_libs.zip"), forge_path, "");
                System.out.println("");
            }

            // Extract the fml_libs15.zip contents
            System.out.println("Checking if we need to extract contents from: fml_libs.zip");
            new ExtractZip(forge_path + "fml_libs.zip", lib_path);
            System.out.println("");
        }

        ForgeHandler.custom_runtime_jar = (forge_path + new_jar);
    }

    private String get_dot_minecraft_path() {
        String ans = System.getProperty("user.home") + File.separator + ".minecraft" + File.separator;
        if (Main.base_os_name.equals("windows")) {
            ans = (System.getenv("APPDATA") + File.separator + ".minecraft" + File.separator);
        }
        return ans;
    }
}
