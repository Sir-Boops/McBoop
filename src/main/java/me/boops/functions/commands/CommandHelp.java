package me.boops.functions.commands;

public class CommandHelp {
	
	public CommandHelp() {
		
		System.out.println("========== McBoop Help ==========");
		System.out.println("");
		System.out.println("McBoop --help => Shows this screen");
		System.out.println("");
		System.out.println("McBoop --add-account <email> <password> => Adds a new account that you can use to play ( First account added will become the default )");
		System.out.println("");
		System.out.println("McBoop --set-default <user name> => Sets default account to use if `--account` is not speficed");
		System.out.println("");
		System.out.println("McBoop --account <user name> => Used with `--run` to run MC with a non-default account");
		System.out.println("");
		System.out.println("McBoop --remove-account <username> => Remove the account <username>");
		System.out.println("");
		System.out.println("McBoop --run <MC version ID> => launches the desired MC version ( You can find the MC version IDs by running --list-all-versions )");
		System.out.println("");
		System.out.println("McBoop --list-all-versions => Lists all the MC version IDs you can run");
		System.out.println("");
		System.out.println("McBoop --create-profile <profile name> <version> => Create a profile named <profile name> running <version> of minecraft. <version> is a version ID");
		System.out.println("");
		System.out.println("McBoop --update-profile <profile name> <version> => Update a profile to use a requested MC version ID");
		System.out.println("");
		System.out.println("McBoop --set-profile-forge <profile name> <version> => Set a forge version ID for a profile");
		System.out.println("");
		System.out.println("McBoop --update-profile-forge <profile name> <version> => Update profile to use a new forge version ID");
		System.out.println("");
		System.out.println("McBoop --delete-profile <profile name> => Deletes a profile folder");
		System.out.println("");
		System.out.println("McBoop --profile <profile name> => Launch requested profile");
		System.out.println("");
		System.out.println("McBoop --list-profiles => List all profiles");
		System.out.println("");
		System.out.println("McBoop --list-forge-versions [MC version] => [MC Version is optional] Without this it will list all versions of MC that forge can on on");
		System.out.println(" With the argument it will list only forge versions for the requested version ID!");
		System.out.println("");
		System.out.println("McBoop --with-forge <version id> => Used with --run to launch MC with the requested forge version ID");
		System.out.println("");
		System.out.println("McBoop --max-ram <amount> => Set the max amount of ram to use Default: 2G");
		System.out.println("");
		System.out.println("McBoop --update => Update McBoop");
		
		System.out.println("");
		System.out.println("");
		
	}
	
}
