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
		System.out.println("McBoop --run <MC version ID> => launches the disred MC version ( You can get a MC id using --list-all-versions )");
		System.out.println("");
		System.out.println("McBoop --list-all-versions => Lists all the MC versions you can run");
		
		System.out.println("");
		System.out.println("");
		System.exit(0);
		
	}
	
}
