package me.boops.functions.threads;

import java.io.File;

import me.boops.functions.crypto.Sha1SumFile;
import me.boops.functions.network.FetchRemoteContent;

public class DownloadLibsThread implements Runnable {

	private String dist_dir;
	private String url;
	private String hash;
	private String file_name;
	
	public DownloadLibsThread(String dist_dir, String url, String hash) {
		this.dist_dir = dist_dir;
		this.url = url;
		this.hash = hash;
		this.file_name = url.substring(this.url.lastIndexOf("/") + 1, this.url.length());
	}
	
	@Override
	public void run() {
		
		// Check if lib is already downloaded
		if(new File(this.dist_dir + this.file_name).exists()) {
			// The file is there!
			// Check the hash
			String sum = new Sha1SumFile().sum(this.dist_dir + this.file_name);
			if (!this.hash.equals(sum)) {
				// Sum does not match the file hash redownload!
				System.out.println("Found a bad lib: " + this.file_name + " Redownloading...");
				downloadRemote();
			}
			
		} else {
			// File is not there
			// Download then check the hash
			System.out.println("Downloading: " + this.file_name);
			downloadRemote();
		}
	}
	
	private void downloadRemote() {
		
		// Download the file!
		new FetchRemoteContent().file(this.url, this.dist_dir, "");
		
		// Calc the sum
		String sum = new Sha1SumFile().sum(this.dist_dir + this.file_name);
		
		// Check if the sum is empy
		if(sum.isEmpty()) {
			// Try to download again!
			downloadRemote();
		} else {
			if(!this.hash.equals(sum)) {
				// Bad download try again!
				downloadRemote();
			}
		}
	}
}
