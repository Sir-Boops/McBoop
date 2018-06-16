package me.boops.functions;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.file.CreateFolder;
import me.boops.functions.file.ExtractZip;
import me.boops.functions.file.GetDotMCPath;
import me.boops.functions.file.ReadTextFromFile;
import me.boops.functions.file.WriteTextToFile;
import me.boops.functions.network.FetchRemoteContent;
import me.boops.functions.threads.DownloadAssetsThread;
import me.boops.functions.threads.DownloadLegacyAssetsThread;

public class InstallAssets {

    public static String assets_path = "";
    public static double processed_assets = 0;

    private boolean is_legacy = false;
    private boolean is_alpha = false;

    public InstallAssets(String profile_path, JSONObject version_meta) {

        System.out.println("Starting asset download and verification");

        JSONObject asset_index = version_meta.getJSONObject("assetIndex");
        JSONObject asset_list = new JSONObject(new FetchRemoteContent().text(asset_index.getString("url")));

        if (asset_index.getString("id").equalsIgnoreCase("legacy")) {
            this.is_legacy = true;
        }

        if (version_meta.getString("type").equals("old_alpha")) {
            this.is_alpha = true;
        }

        // Create versions folder and write the version meta into it
        new CreateFolder(Main.home_dir + "versions");
        new WriteTextToFile(Main.home_dir + "assets" + File.separator + "indexes" + File.separatorChar + asset_index.getString("id") + ".json", asset_list.toString());

        // Downloading the libs also verfies them
        // If legacy use the resources folder
        if (this.is_legacy) {
            downloadLibs(asset_list.getJSONObject("objects"), (Main.home_dir + "assets" + File.separator + "legacy" + File.separator));
        } else {
            downloadLibs(asset_list.getJSONObject("objects"), (Main.home_dir + "assets" + File.separator + "objects" + File.separator));
        }

        // If legacy hack in the old sounds
        if (this.is_legacy) {
            // First install to the profile dir /resources
            // Then install to the .minecraft dir /resources

            // First download the old MC sounds and save them to
            // the assets dir
            if (!new File(Main.home_dir + "assets" + File.separator + "old_sounds.zip").exists()) {
                System.out.println("Downloading old_sounds.zip");
                new FetchRemoteContent().file("https://git.sergal.org/Sir-Boops/McBoop-Support-Files/raw/branch/master/mojang_files/old_sounds.zip", Main.home_dir + "assets" + File.separator, "");
            }
            if (this.is_alpha && !new File(Main.home_dir + "assets" + File.separator + "alpha_sounds.zip").exists()) {
                System.out.println("Downloading alpha_sounds.zip");
                new FetchRemoteContent().file("https://git.sergal.org/Sir-Boops/McBoop-Support-Files/raw/branch/master/mojang_files/alpha_sounds.zip", Main.home_dir + "assets" + File.separator, "");
            }

            // Check if last run was alpha or legacy
            // not found newer then 1.7.2

            if (new File(Main.home_dir + "assets" + File.separator + "resource_type.txt").exists()) {
                String last_type = new ReadTextFromFile().read(Main.home_dir + "assets" + File.separator + "resource_type.txt");
                if (last_type.equals("alpha") && !this.is_alpha) {
                    // Last ran alpha currently running legacy
                    clean_resource_dirs(profile_path);
                }

                if (last_type.equals("legacy") && this.is_alpha) {
                    // Last run is legacy and currently running alpha
                    clean_resource_dirs(profile_path);
                }
            } else {

                // User has never run older versions
                // Create and clean the dirs just to make sure
                new CreateFolder(profile_path + "resources" + File.separator);
                new CreateFolder(new GetDotMCPath().path());
                new CreateFolder(new GetDotMCPath().path() + "resources" + File.separator);
                clean_resource_dirs(profile_path);

            }

            System.out.println("Extracting and checking old MC sounds in the profile folder");
            if (this.is_alpha) {
                new ExtractZip(Main.home_dir + "assets" + File.separator + "alpha_sounds.zip", profile_path + "resources" + File.separator);
            }
            new ExtractZip(Main.home_dir + "assets" + File.separator + "old_sounds.zip", profile_path + "resources" + File.separator);
            System.out.println("");

            System.out.println("copying to the .minecraft folder");
            try {
                FileUtils.copyDirectory(new File(profile_path + "resources" + File.separator), new File(new GetDotMCPath().path() + "resources" + File.separator));
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("");

            // Save what we ran this time
            if (this.is_alpha) {
                new WriteTextToFile(Main.home_dir + "assets" + File.separator + "resource_type.txt", "alpha");
            } else {
                new WriteTextToFile(Main.home_dir + "assets" + File.separator + "resource_type.txt", "legacy");
            }
        }

        InstallAssets.assets_path = gen_asset_path(this.is_legacy);
        System.out.println("All assets verifyed/downloaded");

    }

    private void clean_resource_dirs(String profile_path) {
        try {
            FileUtils.cleanDirectory(new File(profile_path + "resources" + File.separator));
            FileUtils.cleanDirectory(new File(new GetDotMCPath().path() + "resources" + File.separator));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadLibs(JSONObject list, String dirS) {

        ThreadGroup DLGroup = new ThreadGroup("DLGroup");
        double total_assets = list_length(list);

        Iterator<?> keys = list.keys();
        while (keys.hasNext()) {
            System.out.print(" " + new DecimalFormat("#.00").format((float) (processed_assets * 100) / total_assets) + "% Complete               \r");
            // Cap out at 10 threads
            while (DLGroup.activeCount() > 9) {
                try {
                    System.out.print(" " + new DecimalFormat("#.00").format((float) (processed_assets * 100) / total_assets) + "% Complete               \r");
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Create and start the new thread
            String key = (String) keys.next();
            Thread thread = null;
            if (this.is_legacy) {
                thread = new Thread(DLGroup, new DownloadLegacyAssetsThread(key, list.getJSONObject(key).getString("hash"), dirS));
            } else {
                thread = new Thread(DLGroup, new DownloadAssetsThread(key, list.getJSONObject(key).getString("hash"), dirS));
            }
            thread.start();
        }

        System.out.println(" 100% Complete               \r");

        // Wait for all threads to finish!
        while (DLGroup.activeCount() > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int list_length(JSONObject key_list) {
        int ans = 0;
        Iterator<?> keys = key_list.keys();
        while (keys.hasNext()) {
            keys.next();
            ans++;
        }
        return ans;
    }

    private String gen_asset_path(boolean is_legacy) {
        String ans = (Main.home_dir + "assets" + File.separator);
        if (is_legacy) {
            ans = (Main.home_dir + "assets" + File.separator + "legacy" + File.separator);
        }
        return ans;
    }
}
