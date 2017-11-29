package me.boops;

import me.boops.base.ArgsParser;
import me.boops.base.Config;
import me.boops.functions.CheckRelease;
import me.boops.functions.CheckSnapShot;
import me.boops.functions.CheckStatus;
import me.boops.functions.ListAllVersions;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		// Parse the cmd args
		new ArgsParser().parse(args);
		
		if(Config.checkStatus) {
			new CheckStatus();
		}
		
		if(Config.checkRelease) {
			new CheckRelease();
		}
		
		if(Config.checkSnapshot) {
			new CheckSnapShot();
		}
		
		if(Config.listAllVersions) {
			new ListAllVersions();
		}
		
		if(!Config.runVersion.isEmpty() && !Config.userName.isEmpty() && !Config.password.isEmpty()) {
			new RunGame();
		}
		
		System.out.println("Run --help for help");
		
	}
}
