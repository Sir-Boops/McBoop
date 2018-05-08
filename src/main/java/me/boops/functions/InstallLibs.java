package me.boops.functions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import me.boops.Main;
import me.boops.functions.file.CreateFolder;
import me.boops.functions.network.FetchRemoteFile;
import me.boops.functions.threads.ExtractNativesThread;

public class InstallLibs {
	
	public static List<String> libs = new ArrayList<String>();
	public static String nativesPath = "";
	public static List<String> already_extracted = new ArrayList<String>();
	
	private List<String> libURLS = new ArrayList<String>();
	private List<String> libSUMS = new ArrayList<String>();
	
	public InstallLibs() {
		
		System.out.println("Attempting to download/verify libraries");
		// Grab all the URLs and sums
		grabData(VersionMeta.Meta.getJSONArray("libraries"));
		
		startDownload(libURLS, Main.homeDir + "libraries" + File.separator);
		extractNatives(Main.homeDir, libURLS);
		
		for(int i = 0; i < libURLS.size(); i++) {
			
			if(!libURLS.get(i).contains("natives")) {
				InstallLibs.libs.add(Main.homeDir + "libraries" + File.separator + libURLS.get(i).substring(libURLS.get(i).indexOf(".net/") + 5, libURLS.get(i).length()));
			}
		}
		
		System.out.println("Libraries have been verifyed/downloaded");
	}
	
	private void grabData(JSONArray libs){
		for(int i = 0; i < libs.length(); i++) {
			// Do we have to DL it?
			if(libs.getJSONObject(i).has("downloads")) {
				// Does this lib have a universal version?
				if(libs.getJSONObject(i).getJSONObject("downloads").has("artifact")) {
					// Save the universal version URL along with the sum
					this.libURLS.add(libs.getJSONObject(i).getJSONObject("downloads").getJSONObject("artifact").getString("url"));
					this.libSUMS.add(libs.getJSONObject(i).getJSONObject("downloads").getJSONObject("artifact").getString("sha1"));
				}
				
				// Does this lib have a platform spefic version?
				if(libs.getJSONObject(i).getJSONObject("downloads").has("classifiers")) {
					// Are we running a platfrom supported by this lib?
					if(libs.getJSONObject(i).getJSONObject("downloads").getJSONObject("classifiers").has("natives-" + System.getProperty("os.name").toLowerCase())) {
						// Get the platform spefic version URL and sum!
						this.libURLS.add(libs.getJSONObject(i).getJSONObject("downloads").getJSONObject("classifiers").getJSONObject("natives-" + System.getProperty("os.name").toLowerCase()).getString("url"));
						this.libSUMS.add(libs.getJSONObject(i).getJSONObject("downloads").getJSONObject("classifiers").getJSONObject("natives-" + System.getProperty("os.name").toLowerCase()).getString("sha1"));
					}
				}
			}
		}
	}
	
	private void extractNatives(String dirS, List<String> libURLS) {
		
		System.out.println("Extracting native libraries");
		
		String natives_path = (dirS + "natives-" + Main.randString + File.separator);
		new CreateFolder(natives_path);
		InstallLibs.nativesPath = natives_path;
		
		List<String> natives = new ArrayList<String>();
		
		// Generate a list of native jar files
		for(int i = 0; i < libURLS.size(); i++) {
			if(libURLS.get(i).contains("natives")) {
				natives.add(dirS + "libraries" + File.separator + libURLS.get(i).substring(libURLS.get(i).indexOf(".net/") + 5, libURLS.get(i).length()));
			}
		}
		
		// Extract all the natives
		ThreadGroup ExtractGroup = new ThreadGroup("ExtractGroup");
		for(int i = 0; i < natives.size(); i++) {
			
			// Limit extraction thread count
			while(ExtractGroup.activeCount() > 9) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// Start a new extraction thread
			Thread thread = new Thread(ExtractGroup, new ExtractNativesThread(natives.get(i), natives_path));
			thread.start();
		}
		
		// Wait for all natives to be extracted!
		while(ExtractGroup.activeCount() > 0) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Native libraries extracted!");
	}
	
	private void startDownload(List<String> libURLS, String dirS) {
		
		for(int i = 0; i < libURLS.size(); i++) {
			
			String distDir = (dirS + libURLS.get(i).substring(libURLS.get(i).indexOf(".net/") + 5, libURLS.get(i).lastIndexOf("/") + 1).replaceAll("/", File.separator));
			
			if(!new File(distDir + libURLS.get(i).substring(libURLS.get(i).lastIndexOf("/") + 1, libURLS.get(i).length())).exists()) {
				System.out.println("Downloading: " + libURLS.get(i).substring(libURLS.get(i).lastIndexOf("/") + 1, libURLS.get(i).length()));
				new FetchRemoteFile(libURLS.get(i), distDir, "");
			}
			
		}
	}
}
