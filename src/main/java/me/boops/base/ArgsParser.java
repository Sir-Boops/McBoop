package me.boops.base;

import java.io.File;

import org.json.JSONObject;

import me.boops.checks.GetVersions;

public class ArgsParser {
	
	public void parse(String[] args) throws Exception {
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("--check-status")) {
				Cache.checkStatus = true;
			}
			if(args[i].equalsIgnoreCase("--get-release-version")) {
				Cache.printReleaseVersion = true;
			}
			if(args[i].equalsIgnoreCase("--get-snapshot-version")) {
				Cache.printSnapshotVersion = true;
			}
			if(args[i].equalsIgnoreCase("--list-all-versions")) {
				Cache.listAllVersions = true;
			}
			if(args[i].equalsIgnoreCase("--run")) {
				String versionID = getVersionID(args[i + 1]);
				if(versionID.isEmpty()) {
					System.exit(0);
				}
				Cache.runVersion = versionID;
			}
			if(args[i].equalsIgnoreCase("--root-dir")) {
				Cache.rootDir = new File(args[i + 1]).getCanonicalPath().toString() + File.separator;
			}
			if(args[i].equalsIgnoreCase("--username")) {
				Cache.userName = args[i + 1];
			}
			if(args[i].equalsIgnoreCase("--password")) {
				Cache.password = args[i + 1];
			}
		}
	}
	
	private String getVersionID(String requestedVersion) throws Exception {
		JSONObject versions = new GetVersions().getAllVersionsRaw();
		
		if(requestedVersion.equalsIgnoreCase("stable")) {
			return versions.getJSONObject("latest").getString("release");
		}
		
		if(requestedVersion.equalsIgnoreCase("snapshot")) {
			return versions.getJSONObject("latest").getString("snapshot");
		}
		
		for(int i2 = 0; i2 < versions.getJSONArray("versions").length(); i2++) {
			if(versions.getJSONArray("versions").getJSONObject(i2).getString("id").equalsIgnoreCase(requestedVersion)) {
				return versions.getJSONArray("versions").getJSONObject(i2).getString("id");
			}
		}
		
		System.out.println("`" + requestedVersion + "` Is not a valid version string please run `--list-all-versions` to see vaild version names");
		System.out.println("Or use `--help` for help");
		return "";
	}
	
}
