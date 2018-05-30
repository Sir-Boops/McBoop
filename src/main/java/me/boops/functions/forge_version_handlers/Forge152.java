package me.boops.functions.forge_version_handlers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;

import me.boops.Main;
import me.boops.functions.VersionMeta;
import me.boops.functions.file.MergeTwoJars;
import me.boops.functions.forgehandler.ForgeHandler;
import me.boops.functions.network.DownloadClient;

public class Forge152 {
    public Forge152(String file_name, String forge_id) {

        String forge_path = (Main.home_dir + "forge" + File.separator);
        String new_jar = (VersionMeta.ID + "-forge-" + forge_id + ".jar");

        // First thing to do is extract the universal jar from
        // The installer jar
        if (!new File(forge_path + file_name.replace("installer", "universal")).exists()) {
            save_universal((forge_path + file_name), (forge_path + file_name.replace("installer", "universal")), forge_id);
        }
        
        new MergeTwoJars(DownloadClient.jarPath, (forge_path + file_name.replace("installer", "universal")), (forge_path + new_jar));
        
        ForgeHandler.custom_runtime_jar = (forge_path + new_jar);
    }

    private void save_universal(String full_file, String new_file, String forge_id) {
        try {

            JarFile installer_jar = new JarFile(full_file);
            InputStream is = installer_jar.getInputStream(installer_jar.getEntry("minecraftforge-universal-" + VersionMeta.ID + "-" + forge_id + ".jar"));
            FileOutputStream fos = new FileOutputStream(new File(new_file));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            int inByte;
            while ((inByte = is.read()) != -1) {
                bos.write(inByte);
            }

            fos.write(bos.toByteArray());

            bos.close();
            fos.close();
            is.close();
            installer_jar.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
