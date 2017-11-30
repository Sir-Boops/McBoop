package me.boops.base;

import java.io.File;

import org.json.JSONArray;
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
				getVersionID(args[i + 1]);
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
	
	private void getVersionID(String requestedVersion) throws Exception {
		JSONObject versions = new GetVersions().getAllVersionsRaw();
		
		if(requestedVersion.equalsIgnoreCase("stable")) {
			Cache.runVersion = versions.getJSONObject("latest").getString("release");
			Cache.versionMetaURL = getVersionMeta(versions.getJSONArray("versions"));
			return;
		}
		
		if(requestedVersion.equalsIgnoreCase("snapshot")) {
			Cache.runVersion = versions.getJSONObject("latest").getString("snapshot");
			Cache.versionMetaURL = getVersionMeta(versions.getJSONArray("versions"));
			return;
		}
		
		for(int i2 = 0; i2 < versions.getJSONArray("versions").length(); i2++) {
			if(versions.getJSONArray("versions").getJSONObject(i2).getString("id").equalsIgnoreCase(requestedVersion)) {
				Cache.runVersion = versions.getJSONArray("versions").getJSONObject(i2).getString("id");
				Cache.versionMetaURL = getVersionMeta(versions.getJSONArray("versions"));
				return;
			}
		}
		
		System.out.println("`" + requestedVersion + "` Is not a valid version string please run `--list-all-versions` to see vaild version names");
		System.out.println("Or use `--help` for help");
		System.exit(0);
	}
	
	private String getVersionMeta(JSONArray versions) {
		String ans = "";
		for(int i = 0; i < versions.length(); i++) {
			if(versions.getJSONObject(i).getString("id").equalsIgnoreCase(Cache.runVersion)) {
				ans = versions.getJSONObject(i).getString("url");
			}
		}
		return ans;
	}
	
}
