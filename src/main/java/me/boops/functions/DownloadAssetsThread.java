package me.boops.functions;

import java.io.File;

import me.boops.functions.crypto.Sha1SumFile;
import me.boops.functions.network.FetchRemoteFile;

public class DownloadAssetsThread implements Runnable {
	
	private String key;
	private String hash;
	private String dist_dir;
	private String first_two;
	private String url;
	
	public DownloadAssetsThread(String key, String hash, String dirS) {
		this.key = key;
		this.hash = hash;
		this.first_two = hash.substring(0, 2);
		this.dist_dir = (dirS + this.first_two + File.separator);
		this.url = ("https://resources.download.minecraft.net/" + this.first_two + "/" + hash);
	}

	@Override
	public void run() {
		
		String file_name = this.key.substring(this.key.lastIndexOf("/") + 1, this.key.length());
		
		// If we have the file verify the checksum, else download it
		// and then verify the checksum
		if(new File(this.dist_dir + this.hash).exists()) {
			
			// We have this file so verify checksum
			String checksum = new Sha1SumFile().sum(this.dist_dir + this.hash);
			if(!checksum.equals(this.hash)) {
				
				// the file is there but it has a bad hash so delete and redownload this asset
				System.out.println("Found bad asset: " + file_name + " Redownloading...");
				downloadRemote();
			}
		} else {
			// We don't have this file yet so off to fetch it!
			System.out.println("Downloading: " + file_name);
			downloadRemote();
		}
		
	}
	
	private void downloadRemote() {
		// Try to download
		new FetchRemoteFile(this.url, this.dist_dir, "");
		
		// Calc the sum
		String sum = new Sha1SumFile().sum(this.dist_dir + this.hash);
		
		// Check if the sum is empty or not
		if(sum.isEmpty()) {
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
