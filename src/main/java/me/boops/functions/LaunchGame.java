package me.boops.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import org.json.JSONObject;

import me.boops.functions.file.CreateFolder;

public class LaunchGame {
	
	public LaunchGame(List<String> libs, String dirS, String version, String[] user, JSONObject versionMeta) {
		
		new CreateFolder(dirS + "base");
		
		String cleanLibs = cleanLibs(libs);
		String launchArgs = ("java -Djava.library.path=" + dirS + "natives" 
		+ File.separator + " -cp " + dirS + "versions" 
		+ File.separator + version + ".jar" + cleanLibs + " net.minecraft.client.main.Main" 
		+ " --username " + user[3]
		+ " --assetsDir " + dirS + "assets" + File.separator
		+ " --assetIndex " + versionMeta.getString("assets")
		+ " --uuid " + user[2]
		+ " --usertype {}"
		+ " --versionType " + versionMeta.getString("type")
		+ " --accessToken " + user[0] 
		+ " --version " + version
		+ " --gameDir " + dirS + "base");
		
		System.out.println(launchArgs);
		
		//System.exit(0);
		
		try {
			
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(launchArgs);
			
			BufferedReader brErr = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
			BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));

			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			String lineErr = null;
			while ((lineErr = brErr.readLine()) != null) {
				System.out.println(lineErr);
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private String cleanLibs(List<String> libs) {
		
		String ans = ":";
		
		for(int i = 0; i < libs.size(); i++) {
			
			ans += libs.get(i) + File.pathSeparator;
			
		}
		
		return ans.substring(0, ans.lastIndexOf(":"));
		
	}
}
