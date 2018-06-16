package me.boops.functions;

import org.json.JSONObject;

import me.boops.functions.network.FetchRemoteContent;

public class VersionMeta {

	//public static String ID = "";
	//public static JSONObject Meta = new JSONObject();
	
	public String[] get(String[] launcher_args, String profile_mc_version) {
		
		System.out.println("Attempting to fetch version meta");
		
		// Grab the base meta file
		JSONObject metaFile = new JSONObject(new FetchRemoteContent().text("https://launchermeta.mojang.com/mc/game/version_manifest.json"));

		String mcVersion = "";
		
		// Check if we should use profile version
		// Or if we should use the --run <version>
		if (profile_mc_version.isEmpty()) {

			for (int i = 0; i < launcher_args.length; i++) {
				if (launcher_args[i].equalsIgnoreCase("--run")) {
					mcVersion = launcher_args[i + 1];
					i = (launcher_args.length + 1);
				}
			}

		} else {
			mcVersion = profile_mc_version;
		}

		if (mcVersion.equalsIgnoreCase("stable") || mcVersion.equalsIgnoreCase("snapshot")) {
			if (mcVersion.equalsIgnoreCase("stable")) {
				mcVersion = metaFile.getJSONObject("latest").getString("release");
			}
			if (mcVersion.equalsIgnoreCase("snapshot")) {
				mcVersion = metaFile.getJSONObject("latest").getString("snapshot");
			}
		}
		
		// At this point we have a version ID
		// That we want to be sure we can run!
		// So try and find the requested version
		// inside the base meta file to see if it is
		// a real version!
		
		String metaURL = "";

		for (int i = 0; i < metaFile.getJSONArray("versions").length(); i++) {
			if (metaFile.getJSONArray("versions").getJSONObject(i).getString("id").equalsIgnoreCase(mcVersion)) {
				metaURL = metaFile.getJSONArray("versions").getJSONObject(i).getString("url");
			}
		}

		if (metaURL.isEmpty()) {
			System.out.println("Could not find requested version!");
			System.exit(1);
		}
		
		// From this point we have the meta URL
		// So we know it's a proper version so just
		// Fetch the version META and we are good to go!
		
		JSONObject Meta = new JSONObject(new FetchRemoteContent().text(metaURL));
		String ID = Meta.getString("id");
		System.out.println("Got the version meta for: " + ID);
		return new String[] {ID, Meta.toString()};
	}
}
