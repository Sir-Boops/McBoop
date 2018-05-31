package me.boops.functions.forge_version_handlers;

import java.io.File;

import me.boops.Main;
import me.boops.functions.VersionMeta;
import me.boops.functions.file.MergeTwoJars;
import me.boops.functions.forgehandler.ForgeHandler;
import me.boops.functions.network.DownloadClient;
import me.boops.functions.network.FetchRemoteContent;

public class Forge11to124 {
    public Forge11to124(String file_name, String forge_id) {
        
        String forge_path = (Main.home_dir + "forge" + File.separator);
        String ml_jar = (VersionMeta.ID + "-ML.jar");
        String new_jar = (VersionMeta.ID + "-forge-" + forge_id + ".jar");
        
        String url_base = "https://git.sergal.org/Sir-Boops/McBoop-Support-Files/raw/branch/master/mod_loader/";
        
        // Make sure we already have the ML version
        if(!new File(forge_path + "ModLoader-" + VersionMeta.ID + ".zip").exists()) {
            new FetchRemoteContent().file((url_base + "ModLoader-" + VersionMeta.ID + ".zip"), forge_path, "");
        }
        
        // Check if the ML jar is already there or not
        if(!new File(forge_path + ml_jar).exists()) {
            new MergeTwoJars(DownloadClient.jarPath, (forge_path + "ModLoader-" + VersionMeta.ID + ".zip"), (forge_path + ml_jar));
        }
        
        // Finally create a forge jar
        if(!new File(forge_path + new_jar).exists()) {
            new MergeTwoJars((forge_path + ml_jar), (forge_path + file_name), (forge_path + new_jar));
        }
        
        ForgeHandler.custom_runtime_jar = (forge_path + new_jar);
    }
}
