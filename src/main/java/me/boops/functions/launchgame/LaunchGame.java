package me.boops.functions.launchgame;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import me.boops.functions.DownloadClient;
import me.boops.functions.InstallLibs;
import me.boops.functions.VersionMeta;
import me.boops.functions.profilemanager.ProfileManager;

public class LaunchGame {

	public LaunchGame() {
		
		List<String> launchArr = new ArrayList<String>();

		String cleanLibs = cleanLibsPaths(InstallLibs.libs);
		launchArr.add("java");
		launchArr.add("-Xms256M");
		launchArr.add("-Xmx2G");
		launchArr.add("-Djava.library.path=" + InstallLibs.nativesPath);
		launchArr.add("-cp");
		launchArr.add(cleanLibs + DownloadClient.jarPath);
		launchArr.add(VersionMeta.Meta.getString("mainClass"));
		launchArr.addAll(new GenerateMCArgs().gen());

		System.out.println(launchArr);

		try {

			Process pr = null;
			ProcessBuilder pb = new ProcessBuilder(launchArr);
			pb.directory(new File(ProfileManager.path));
			pr = pb.start();

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
		
		// Delete the natives dir on exit
		try {
			FileUtils.deleteDirectory(new File(InstallLibs.nativesPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);

	}

	private String cleanLibsPaths(List<String> libs) {

		String ans = "";
		for (int i = 0; i < libs.size(); i++) {
			ans += libs.get(i) + File.pathSeparator;
		}
		return ans;
	}
}
