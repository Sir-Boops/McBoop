package me.boops.functions;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Iterator;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.file.CreateFolder;
import me.boops.functions.file.WriteTextToFile;
import me.boops.functions.network.FetchRemoteContent;
import me.boops.functions.threads.DownloadAssetsThread;

public class InstallAssets {

    public static String assets_path = (Main.home_dir + "assets" + File.separator);
    public static double processed_assets = 0;

    public InstallAssets() {

        System.out.println("Starting asset download and verification");

        JSONObject asset_index = VersionMeta.Meta.getJSONObject("assetIndex");
        JSONObject asset_list = new JSONObject(new FetchRemoteContent().text(asset_index.getString("url")));

        // Create versions folder and write the version meta into it
        new CreateFolder(Main.home_dir + "versions");
        new WriteTextToFile(Main.home_dir + "assets" + File.separator + "indexes" + File.separatorChar + asset_index.getString("id") + ".json", asset_list.toString());

        // Downloading the libs also verfies them
        downloadLibs(asset_list.getJSONObject("objects"), (Main.home_dir + "assets" + File.separator + "objects" + File.separator));

        System.out.println("All assets verifyed/downloaded");

    }

    private void downloadLibs(JSONObject list, String dirS) {

        ThreadGroup DLGroup = new ThreadGroup("DLGroup");
        double total_assets = list_length(list);

        Iterator<?> keys = list.keys();
        while (keys.hasNext()) {
            System.out.print((float) (processed_assets * 100) / total_assets + "\r");
            // Cap out at 10 threads
            while (DLGroup.activeCount() > 9) {
                try {
                    Thread.sleep(10);
                    System.out.print(" " + new DecimalFormat("#.00").format((float) (processed_assets * 100) / total_assets) + "% Complete               \r");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Create and start the new thread
            String key = (String) keys.next();
            Thread thread = new Thread(DLGroup, new DownloadAssetsThread(key, list.getJSONObject(key).getString("hash"), dirS));
            thread.start();
        }

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
}
