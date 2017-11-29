package me.boops;

import org.json.JSONArray;

import me.boops.base.ArgsParser;
import me.boops.base.Config;
import me.boops.checks.CheckStatus;
import me.boops.checks.GetVersions;

public class Main {

	public static void main(String[] args) throws Exception {

		// Parse the cmd args
		new ArgsParser().parse(args);

		if (Config.checkStatus) {
			new CheckStatus();
		}

		if (Config.printReleaseVersion) {
			System.out.println("Current release version is: " + new GetVersions().getReleaseVersion());
			System.exit(0);
		}

		if (Config.printSnapshotVersion) {
			System.out.println("Current release version is: " + new GetVersions().getSnapShotVersion());
			System.exit(0);
		}

		if (Config.listAllVersions) {
			System.out.println("Here is a list of versions you can play:");
			JSONArray versions = new GetVersions().getAllVersions();
			for (int i = 0; i < versions.length(); i++) {
				System.out.println(versions.getJSONObject(i).getString("id"));
			}
			System.exit(0);
		}

		if (!Config.runVersion.isEmpty() && !Config.userName.isEmpty() && !Config.password.isEmpty()) {
			new RunGame();
		}

		System.out.println("Run --help for help");

	}
}
