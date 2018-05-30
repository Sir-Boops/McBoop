package me.boops.functions.forge_version_handlers;

import java.io.File;

import me.boops.Main;
import me.boops.functions.VersionMeta;
import me.boops.functions.file.MergeTwoJars;
import me.boops.functions.forgehandler.ForgeHandler;
import me.boops.functions.network.DownloadClient;

public class Forge152 {
    public Forge152(String file_name, String forge_id) {

        String forge_path = (Main.home_dir + "forge" + File.separator);
        String new_jar = (VersionMeta.ID + "-forge-" + forge_id + ".jar");
        
        new MergeTwoJars(DownloadClient.jarPath, (forge_path + file_name.replace("installer", "universal")), (forge_path + new_jar));
        
        ForgeHandler.custom_runtime_jar = (forge_path + new_jar);
    }
}
