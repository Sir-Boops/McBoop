package me.boops.functions.commands;

public class CommandParser {
	
	public CommandParser(String[] launcher_args) {
		
		for(int i = 0; i < launcher_args.length; i++) {
			
			if(launcher_args[i].equalsIgnoreCase("--help")) {
				new CommandHelp();
				System.exit(0);
			}
			
			if(launcher_args[i].equalsIgnoreCase("--update")) {
				new CommandUpdate();
				System.exit(0);
			}
			
			if(launcher_args[i].equalsIgnoreCase("--list-all-versions")) {
				new CommandListAllVersions();
				System.exit(0);
			}
			
			if(launcher_args[i].equalsIgnoreCase("--list-forge-versions")) {
				new ListForgeVersions(launcher_args);
				System.exit(0);
			}
		}
	}
}
