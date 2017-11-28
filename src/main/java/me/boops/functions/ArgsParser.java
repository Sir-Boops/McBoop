package me.boops.functions;

import java.io.File;

import me.boops.Config;

public class ArgsParser {
	
	public void parse(String[] args) throws Exception {
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase("--check-status")) {
				Config.checkStatus = true;
			}
			if(args[i].equalsIgnoreCase("--check-release")) {
				Config.checkRelease = true;
			}
			if(args[i].equalsIgnoreCase("--check-snapshot")) {
				Config.checkSnapshot = true;
			}
			if(args[i].equalsIgnoreCase("--list-all-versions")) {
				Config.listAllVersions = true;
			}
			if(args[i].equalsIgnoreCase("--run")) {
				Config.runVersion = args[i + 1];
			}
			if(args[i].equalsIgnoreCase("--root-dir")) {
				Config.rootDir = new File(args[i + 1]).getCanonicalPath().toString() + "/";
			}
			if(args[i].equalsIgnoreCase("--username")) {
				Config.userName = args[i + 1];
			}
			if(args[i].equalsIgnoreCase("--password")) {
				Config.password = args[i + 1];
			}
		}
	}
}
