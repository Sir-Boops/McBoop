package me.boops.functions.threads;

import java.io.File;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.crypto.Sha1SumFile;
import me.boops.functions.file.CreateFolder;
import me.boops.functions.file.ReadTextFromFile;
import me.boops.functions.file.WriteTextToFile;
import me.boops.functions.network.FetchRemoteContent;
import me.boops.functions.string_utls.ReplaceChars;

public class DownloadForgeLibs implements Runnable {

	private String file_name;
	private String file_path;
	private String full_path;
	private String[] urls;
	
	public DownloadForgeLibs(String file_name, String file_path) {
		this.file_name = file_name;
		this.file_path = file_path;
		this.full_path = (Main.home_dir + "libraries" + File.separator + this.file_path + this.file_name);
		this.urls = new String[] {
				"https://repo1.maven.org/maven2/" + new ReplaceChars().replace(this.file_path, File.separator, "/") + this.file_name,
				"https://repo.spongepowered.org/maven/" + new ReplaceChars().replace(this.file_path, File.separator, "/") + this.file_name,
				"https://libraries.minecraft.net/" + new ReplaceChars().replace(this.file_path, File.separator, "/") + this.file_name};
	}
	
	@Override
	public void run() {
		new CreateFolder(Main.home_dir + "libraries" + File.separator + this.file_path);
		
		// Check to see if we have the lib already!
		if(!new File(this.full_path).exists()) {
			
			// If we don't have the file already!
			
			System.out.println("Downloading: " + this.file_name);
			
			boolean got_file = false;
			int attempts = 0;
			
			// While we have tried less then 3 times and still have not found the file
			while(!got_file && attempts < 3) {
				boolean did_fail = new FetchRemoteContent().fileStatus(this.urls[attempts], Main.home_dir + "libraries" + File.separator + this.file_path, "");
				if(!did_fail) {
					got_file = true;
				}
				attempts++;
			}
			
			// At this point we should have the file
			// Get the sha1sum
			String sum = new FetchRemoteContent().text(this.urls[attempts - 1] + ".sha1");
			
			// Put the sum and the url it was downloaded from into a JSON object to be
			// saved next to the file incase of need to redownload
			JSONObject json_file = new JSONObject();
			json_file.put("url", this.urls[attempts - 1] );
			json_file.put("sum", sum);
			
			// Write the json file
			new WriteTextToFile(this.full_path + ".json", json_file.toString());
			
			// Check the file again!
			checkFile();
			
		} else {
			
			// The file is already there so just verify it
			checkFile();
			
		}
	}
	
	private void checkFile() {
		// Make sure there is a file here!
		if(new File(this.full_path + ".json").exists()) {
			// Load the file json and generate a file sum
			JSONObject json_file = new JSONObject(new ReadTextFromFile().read(this.full_path + ".json"));
			String sum = new Sha1SumFile().sum(this.full_path);
			
			if(!sum.equals(json_file.get("sum"))){
				// The sum does not match
				System.out.println("Found bad lib: " + this.file_name + " Redownloading...");
				reDownload();
			}
		}
	}
	
	private void reDownload() {
		// Load json_file
		JSONObject json_file = new JSONObject(new ReadTextFromFile().read(this.full_path + ".json"));
		
		// Download the file again!
		new FetchRemoteContent().file(json_file.getString("url"), Main.home_dir + "libraries" + File.separator + this.file_path, "");
		System.out.println(json_file.getString("url"));
		
		// Now sum and check the sum
		String sum = new Sha1SumFile().sum(this.full_path);
		if(!sum.equals(json_file.get("sum"))) {
			// Failed again try again
			reDownload();
		}
		
	}
	
}
