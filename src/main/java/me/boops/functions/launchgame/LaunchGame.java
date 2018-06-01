package me.boops.functions.launchgame;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import me.boops.functions.InstallLibs;
import me.boops.functions.VersionMeta;
import me.boops.functions.commands.CommandParser;
import me.boops.functions.forgehandler.ForgeHandler;
import me.boops.functions.network.DownloadClient;
import me.boops.functions.profilemanager.ProfileManager;

public class LaunchGame {

	public LaunchGame() {
		
		List<String> launchArr = new ArrayList<String>();

		String cleanLibs = cleanLibsPaths(InstallLibs.libs, ForgeHandler.libs);
		launchArr.add("java");
		launchArr.add("-Xms256M");
		launchArr.add(getMaxRAM());
		launchArr.add("-Djava.library.path=" + InstallLibs.nativesPath);
		launchArr.add("-cp");
		launchArr.add(cleanLibs + getRuntimeJar());
		launchArr.add(getLaunchClass());
		launchArr.addAll(getMCArgs());
		
		System.out.println("Using launch args:");
		System.out.println("");
		System.out.println(launchArr);
		System.out.println("");
		System.out.println("Minecraft output begins here!");
		System.out.println("");

		try {

			Process pr = new ProcessBuilder(launchArr).directory(new File(ProfileManager.path)).start();

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

			// Wait for the minecraft process to fully stop
			pr.destroy();
			pr.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Delete the natives dir on exit
		try {
			FileUtils.deleteDirectory(new File(InstallLibs.nativesPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);

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
			ans = VersionMeta.Meta.getString("mainClass");
		}
		
		return ans;
	}
	
	private List<String> getMCArgs(){
		List<String> ans = new ArrayList<String>();
		
		if(ForgeHandler.versionMeta.has("minecraftArguments")) {
			ans.addAll(new GenerateMCArgs().gen(ForgeHandler.versionMeta));
		} else {
			ans.addAll(new GenerateMCArgs().gen(VersionMeta.Meta));
		}
		
		return ans;
	}
	
	private String getMaxRAM() {
		String ans = "";
		if(CommandParser.maxRAM.isEmpty()) {
			ans = "-Xmx2G";
		} else {
			ans = ("-Xmx" + CommandParser.maxRAM);
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
