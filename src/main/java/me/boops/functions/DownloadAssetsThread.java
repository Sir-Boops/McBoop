package me.boops.functions;

import java.io.File;

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
		
		if(!new File(this.dist_dir + this.hash).exists()) {
			System.out.println("Downloading: " + this.key.substring(this.key.lastIndexOf("/") + 1, this.key.length()));
			new FetchRemoteFile(this.url, this.dist_dir, "");
		}
		
	}
}
