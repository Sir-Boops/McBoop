package me.boops.functions.commands;

public class CommandParser {
	
	public CommandParser(String[] args) {
		
		for(int i = 0; i < args.length; i++) {
			
			if(args[i].equalsIgnoreCase("--help")) {
				new CommandHelp();
			}
			
			if(args[i].equalsIgnoreCase("--list-all-versions")) {
				new CommandListAllVersions();
			}
			
		}
	}
}
