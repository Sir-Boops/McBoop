package me.boops.functions.launchgame;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.GetCLIArg;
import me.boops.functions.InstallLibs;
import me.boops.functions.forgehandler.ForgeHandler;
import me.boops.functions.network.DownloadClient;

public class LaunchGame {
    
    private String[] mc_meta;

	public LaunchGame(String[] launcher_args, String profile_path, String[] user, String[] meta) {
	    
	    this.mc_meta = meta;
		
		List<String> launchArr = new ArrayList<String>();

		String cleanLibs = cleanLibsPaths(InstallLibs.libs, ForgeHandler.libs);
		launchArr.addAll(get_system_launch_args());
		launchArr.add("java");
		launchArr.add("-Xms256M");
		launchArr.add(getMaxRAM(launcher_args));
		launchArr.add("-Djava.library.path=" + InstallLibs.nativesPath);
		launchArr.add("-cp");
		launchArr.add(cleanLibs + getRuntimeJar());
		launchArr.add(getLaunchClass());
		launchArr.addAll(getMCArgs(profile_path, user));
		
		System.out.println("Using launch args:");
		System.out.println("");
		System.out.println(launchArr);
		
		// Start the game
		try {
		    
		    
		    Process pr = new ProcessBuilder(launchArr).directory(new File(profile_path)).start();
		    
		    System.out.println("");
		    System.out.println("Starting Minecraft");
		    
		    while(pr.isAlive()) {
		        Thread.sleep(10);
		    }
		    
		    FileUtils.deleteDirectory(new File(InstallLibs.nativesPath));
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		System.exit(0);

	}
	
	private List<String> get_system_launch_args() {
	    List<String> ans = new ArrayList<String>();
	    if(Main.base_os_name.equalsIgnoreCase("windows")) {
	        ans.add("cmd.exe");
	        ans.add("/C");
	        ans.add("start");
	        ans.add("/wait");
	    }
	    if(Main.base_os_name.equalsIgnoreCase("linux")) {
	        ans.add("xterm");
	        ans.add("-e");
	    }
	    return ans;
	}

	private String cleanLibsPaths(List<String> basicLibs, List<String> forgeLibs) {
		
		List<String> libs = new ArrayList<String>();
		if(!forgeLibs.isEmpty()) {
			libs.addAll(forgeLibs);
		} else {
			libs.addAll(basicLibs);
		}
		
		String ans = "";
		for (int i = 0; i < libs.size(); i++) {
			ans += libs.get(i) + File.pathSeparator;
		}
		return ans;
	}
	
	private String getLaunchClass() {
		String ans = "";
		
		if(ForgeHandler.versionMeta.has("mainClass")) {
			ans = ForgeHandler.versionMeta.getString("mainClass");
		} else {
			ans = new JSONObject(this.mc_meta[1]).getString("mainClass");
		}
		
		return ans;
	}
	
	private List<String> getMCArgs(String profile_path, String[] user){
		List<String> ans = new ArrayList<String>();
		ans.addAll(new GenerateMCArgs().gen(this.mc_meta, profile_path, user, ForgeHandler.versionMeta));
		return ans;
	}
	
	private String getMaxRAM(String[] launcher_args) {
		String ans = ("-Xmx" + new GetCLIArg().get(launcher_args, "--max-ram"));
		if(ans.isEmpty()) {
			ans = "-Xmx2G";
		}
		return ans;
	}
	
	private String getRuntimeJar() {
	    String ans = DownloadClient.jarPath;
	    if(!ForgeHandler.custom_runtime_jar.isEmpty()) {
	        ans = ForgeHandler.custom_runtime_jar;
	    }
	    return ans;
	}
}
