package me.boops.functions.threads;

import java.io.File;

import me.boops.functions.InstallAssets;
import me.boops.functions.crypto.Sha1SumFile;
import me.boops.functions.network.FetchRemoteContent;
import me.boops.functions.string_utls.ReplaceChars;

public class DownloadLegacyAssetsThread implements Runnable {

    private String key;
    private String hash;
    private String dist_dir;
    private String first_two;
    private String url;
    private String file_name;

    public DownloadLegacyAssetsThread(String key, String hash, String dirS) {
        this.key = key;
        this.hash = hash;
        this.first_two = hash.substring(0, 2);
        this.file_name = this.key.substring(this.key.lastIndexOf("/") + 1, this.key.length());
        this.dist_dir = (new ReplaceChars().replace(dirS + this.key.replace(file_name, ""), "/", File.separator));
        this.url = ("https://resources.download.minecraft.net/" + this.first_two + "/" + hash);
    }

    @Override
    public void run() {

        // If we have the file verify the checksum, else download it
        // and then verify the checksum
        if (new File(this.dist_dir + this.file_name).exists()) {

            // We have this file so verify checksum
            String checksum = new Sha1SumFile().sum(this.dist_dir + this.file_name);
            if (!checksum.equals(this.hash)) {

                // the file is there but it has a bad hash so delete and redownload this asset
                System.out.println("Found bad asset: " + this.file_name + " Redownloading");
                downloadRemote();
            }
        } else {
            // We don't have this file yet so off to fetch it!
            System.out.println("Downloaded: " + this.file_name);
            downloadRemote();
        }
        InstallAssets.processed_assets++;
    }

    private void downloadRemote() {
        // Try to download
        new FetchRemoteContent().file(this.url, this.dist_dir, this.file_name);

        // Calc the sum
        String sum = new Sha1SumFile().sum(this.dist_dir + this.file_name);

        // Check if the sum is empty or not
        if (sum.isEmpty()) {
            // Retry the download again!
            downloadRemote();
        } else {
            if (!this.hash.equals(sum)) {
                // Bad download retry again!
                downloadRemote();
            }
        }

    }
}
