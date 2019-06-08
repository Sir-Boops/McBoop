package me.boops.functions.launchgame;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import me.boops.Main;
import me.boops.functions.GetCLIArg;
import me.boops.functions.InstallLibs;
import me.boops.functions.file.WriteTextToFile;
import me.boops.functions.network.DownloadClient;

public class LaunchGame {
    
    private String[] mc_meta;

	public LaunchGame(String[] launcher_args, String profile_path, String[] user, String[] meta) {
	    
	    this.mc_meta = meta;
		
		List<String> launchArr = new ArrayList<String>();

		String cleanLibs = cleanLibsPaths(InstallLibs.libs);
		launchArr.add("-Xms256M");
		launchArr.add(getMaxRAM(launcher_args));
		launchArr.add("-Djava.library.path=" + InstallLibs.nativesPath);
		launchArr.add("-cp");
		launchArr.add(cleanLibs + DownloadClient.jarPath);
		launchArr.add(new JSONObject(this.mc_meta[1]).getString("mainClass"));
		launchArr.addAll(getMCArgs(profile_path, user));
		
		// Pass the args out the the golang launcher
		System.out.println("Using launch args:");
		System.out.println("");
		String FinalArr = "";
		for (int i = 0; i < launchArr.size(); i++) {
			if ((i + 1) >= launchArr.size()) {
				FinalArr += launchArr.get(i);
			} else {
				FinalArr += launchArr.get(i) + " ";
			}
		}
		System.out.println(FinalArr);
		new WriteTextToFile(Main.home_dir + ".launch", FinalArr);
		System.exit(0);

	}

	private String cleanLibsPaths(List<String> basicLibs) {
		
		String ans = "";
		for (int i = 0; i < basicLibs.size(); i++) {
			ans += basicLibs.get(i) + File.pathSeparator;
		}
		return ans;
	}
	
	private List<String> getMCArgs(String profile_path, String[] user){
		List<String> ans = new ArrayList<String>();
		ans.addAll(new GenerateMCArgs().gen(this.mc_meta, profile_path, user));
		return ans;
	}
	
	private String getMaxRAM(String[] launcher_args) {
		String ans = ("-Xmx" + new GetCLIArg().get(launcher_args, "--max-ram"));
		if((new GetCLIArg().get(launcher_args, "--max-ram")).isEmpty()) {
			ans = "-Xmx2G";
		}
		return ans;
	}
}
