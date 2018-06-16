package me.boops.functions.forgehandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.string_utls.ReplaceChars;
import me.boops.functions.threads.DownloadForgeLibs;

public class FetchForgeLibs {

    public List<String> fetch(JSONObject mc_version_meta) {
        List<String> ans = new ArrayList<String>();

        JSONArray libList = ForgeHandler.versionMeta.getJSONArray("libraries");
        ThreadGroup DLGroup = new ThreadGroup("DLGroup");

        // Download forge libs and add them to the final response!
        for (int i = 0; i < libList.length(); i++) {

            // Only spin up X threads
            while (DLGroup.activeCount() > 9) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Clean up the string to use
            String rawName = libList.getJSONObject(i).getString("name");
            if (!rawName.toLowerCase().contains("minecraftforge")) {
                String fileName = (rawName.split(":")[1] + "-" + rawName.split(":")[2] + ".jar");
                String filePath = (new ReplaceChars().replace(rawName.split(":")[0], ".", File.separator) + File.separator + rawName.split(":")[1] + File.separator + rawName.split(":")[2] + File.separator);
                String fullPath = (Main.home_dir + "libraries" + File.separator + filePath + fileName);
                Thread thread = new Thread(DLGroup, new DownloadForgeLibs(fileName, filePath));
                thread.start();
                ans.add(fullPath);
            }
        }

        // Wait for all threads to finish
        while (DLGroup.activeCount() > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Add all the default libs that forge dosn't override
        JSONArray defaultArr = mc_version_meta.getJSONArray("libraries");
        for (int i = 0; i < defaultArr.length(); i++) {
            String rawName = defaultArr.getJSONObject(i).getString("name");
            String name = (rawName.split(":")[1]);
            if (shouldAdd(name)) {
                String fileName = (rawName.split(":")[1] + "-" + rawName.split(":")[2] + ".jar");
                String filePath = (new ReplaceChars().replace(rawName.split(":")[0], ".", File.separator) + File.separator + rawName.split(":")[1] + File.separator + rawName.split(":")[2] + File.separator);
                String fullPath = (Main.home_dir + "libraries" + File.separator + filePath + fileName);
                ans.add(fullPath);
            }
        }

        return ans;
    }

    private boolean shouldAdd(String term) {
        boolean ans = true;
        JSONArray forgeLibs = ForgeHandler.versionMeta.getJSONArray("libraries");

        for (int i = 0; i < forgeLibs.length(); i++) {
            String rawName = forgeLibs.getJSONObject(i).getString("name");
            String name = (rawName.split(":")[1]);

            if (term.equalsIgnoreCase(name)) {
                ans = false;
            }
        }

        return ans;
    }
}
