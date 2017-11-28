package me.boops.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;

import me.boops.Config;
import me.boops.net.GetURL;

public class RunGame {
	public RunGame() throws Exception {

		// Get version details
		JSONObject details = new GetLauncherMeta().get();
		JSONObject versionAssets = new JSONObject(
				new GetURL().get(details.getJSONObject("assetIndex").getString("url")));

		// Make sure the root-dir is there
		if (!new File(Config.rootDir).exists()) {
			// Make the root-dir
			new File(Config.rootDir).mkdirs();
		}

		// Make sure that assets folder is there
		if (!new File(Config.rootDir + "assets/").exists()) {
			new File(Config.rootDir + "assets/").mkdir();
		}

		// Fetch all the assets
		new SaveTextFile().Save(Config.rootDir + "assets/indexes/", versionAssets.toString(),
				details.getJSONObject("assetIndex").getString("id") + ".json");
		if(details.has("logging")) {
			new FetchLoggingConfig(details.getJSONObject("logging"), Config.rootDir + "assets/");
		}
		new FetchObjects().Download(versionAssets);

		// Make sure that libraries folder is there
		if (!new File(Config.rootDir + "libraries/").exists()) {
			new File(Config.rootDir + "libraries/").mkdir();
		}

		// Fetch all the libs
		new FetchLibraries(details, Config.rootDir + "libraries/");

		// Extract required libs
		// Make sure that libraries folder is there
		if (!new File(Config.rootDir + "natives/").exists()) {
			new File(Config.rootDir + "natives/").mkdir();
		} else {
			ArrayList<File> files = new ArrayList<File>(Arrays.asList(new File(Config.rootDir + "natives/").listFiles()));
			for(int i = 0; i < files.size(); i++) {
				files.get(i).delete();
			}
		}
		
		System.out.println("Extracting natives");
		new ExtractLibs();

		// Make sure that versions folder is there
		if (!new File(Config.rootDir + "versions/").exists()) {
			new File(Config.rootDir + "versions/").mkdir();
		}

		System.out.println("Downloading client: " + details.getString("id") + ".jar");
		new DownloadFile().Download(Config.rootDir + "versions/",
				details.getJSONObject("downloads").getJSONObject("client").getString("url"),
				details.getString("id") + ".jar");

		String startArgs = "java -Djava.library.path=" + (Config.rootDir + "natives/") + " -cp \"";
		String serp = "";
		if(Config.OS.equalsIgnoreCase("linux")) {
			serp = ":";
		} else {
			serp = ";";
		}
		for (int i = 0; i < Config.libPaths.size(); i++) {
			startArgs += Config.libPaths.get(i) + serp;
		}
		startArgs += Config.rootDir + "versions/" + details.getString("id") + ".jar"
				+ "\" " + details.getString("mainClass");

		// Now Login to MC
		JSONObject authTicket = new LoginToMojang().login();

		startArgs = new FinishStartArgs().Finish(startArgs, authTicket, details);

		System.out.println(startArgs);

		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(startArgs);
		BufferedReader brErr = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
		BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line = null;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
		String lineErr = null;
		while ((lineErr = brErr.readLine()) != null) {
			System.out.println(lineErr);
		}
		System.exit(0);

	}
}
