package me.boops;

import java.io.File;

import org.json.JSONArray;

import me.boops.base.ArgsParser;
import me.boops.base.Cache;
import me.boops.checks.CheckStatus;
import me.boops.checks.GetVersions;

public class Main {

	public static void main(String[] args) throws Exception {

		// Parse the cmd args
		new ArgsParser().parse(args);

		if (Cache.checkStatus) {
			new CheckStatus();
		}

		if (Cache.printReleaseVersion) {
			System.out.println("Current release version is: " + new GetVersions().getReleaseVersion());
			System.exit(0);
		}

		if (Cache.printSnapshotVersion) {
			System.out.println("Current release version is: " + new GetVersions().getSnapShotVersion());
			System.exit(0);
		}

		if (Cache.listAllVersions) {
			System.out.println("Here is a list of versions you can play:");
			JSONArray versions = new GetVersions().getAllVersions();
			for (int i = 0; i < versions.length(); i++) {
				System.out.println(versions.getJSONObject(i).getString("id"));
			}
			System.exit(0);
		}
		
		if (Cache.saveLogin && !Cache.userName.isEmpty() && !Cache.password.isEmpty()) {
			new SaveToken();
		}

		if (!Cache.runVersion.isEmpty()) {
			if((!Cache.userName.isEmpty() && !Cache.password.isEmpty()) || new File(Cache.cacheDir + File.separator + "auth.json").exists()) {
				new RunGame();
			}
		}

		System.out.println("Run --help for help");

	}
}
