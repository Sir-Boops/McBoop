package me.boops;

import org.json.JSONArray;

import me.boops.functions.ListForgeVersions;
import me.boops.net.CheckStatus;
import me.boops.net.GetVersions;
import me.boops.net.MojangAuth;

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

		if (Cache.listForgeVersions && !Cache.runVersion.isEmpty()) {
			new ListForgeVersions().listVersions();
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
			new MojangAuth().login(Cache.userName, Cache.password);
			System.exit(0);
		}

		if (!Cache.runVersion.isEmpty() && !Cache.userName.isEmpty()) {
			new RunGame();
			System.exit(0);
		}

		System.out.println("Run --help for help");

	}
}
