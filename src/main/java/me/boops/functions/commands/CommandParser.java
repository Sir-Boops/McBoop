package me.boops.functions.commands;

import me.boops.Main;

public class CommandParser {
	
	public static String maxRAM = "";
	
	public CommandParser() {
		
		for(int i = 0; i < Main.args.length; i++) {
			
			if(Main.args[i].equalsIgnoreCase("--help")) {
				new CommandHelp();
				System.exit(0);
			}
			
			if(Main.args[i].equalsIgnoreCase("--list-all-versions")) {
				new CommandListAllVersions();
				System.exit(0);
			}
			
			if(Main.args[i].equalsIgnoreCase("--list-forge-versions")) {
				new ListForgeVersions(Main.args);
				System.exit(0);
			}
			
			if(Main.args[i].equalsIgnoreCase("--max-ram")) {
				CommandParser.maxRAM = Main.args[i + 1];
			}
			
		}
	}
}
