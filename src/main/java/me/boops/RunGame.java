package me.boops;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;

import me.boops.base.Cache;
import me.boops.base.SaveTextFile;
import me.boops.functions.ExtractLibs;
import me.boops.functions.FetchLibraries;
import me.boops.functions.FetchLoggingConfig;
import me.boops.functions.FinishStartArgs;
import me.boops.functions.GetLauncherMeta;
import me.boops.net.DownloadFile;
import me.boops.net.FetchObjects;
import me.boops.net.GetURL;
import me.boops.net.LoginToMojang;

public class RunGame {
	public RunGame() throws Exception {

		// Get version details
		JSONObject details = new GetLauncherMeta().get();
		JSONObject versionAssets = new JSONObject(
				new GetURL().get(details.getJSONObject("assetIndex").getString("url")));

		// Make sure the root-dir is there
		if (!new File(Cache.rootDir).exists()) {
			// Make the root-dir
			new File(Cache.rootDir).mkdirs();
		}

		// Make sure that assets folder is there
		if (!new File(Cache.rootDir + "assets" + File.separator).exists()) {
			new File(Cache.rootDir + "assets" + File.separator).mkdir();
		}

		// Fetch all the assets
		new SaveTextFile().Save(Cache.rootDir + "assets" + File.separator + "indexes" + File.separator, versionAssets.toString(),
				details.getJSONObject("assetIndex").getString("id") + ".json");
		if(details.has("logging")) {
			new FetchLoggingConfig(details.getJSONObject("logging"), Cache.rootDir + "assets" + File.separator);
		}
		new FetchObjects().Download(versionAssets);

		// Make sure that libraries folder is there
		if (!new File(Cache.rootDir + "libraries" + File.separator).exists()) {
			new File(Cache.rootDir + "libraries" + File.separator).mkdir();
		}

		// Fetch all the libs
		new FetchLibraries(details, Cache.rootDir + "libraries" + File.separator);

		// Extract required libs
		// Make sure that libraries folder is there
		if (!new File(Cache.rootDir + "natives" + File.separator).exists()) {
			new File(Cache.rootDir + "natives" + File.separator).mkdir();
		} else {
			ArrayList<File> files = new ArrayList<File>(Arrays.asList(new File(Cache.rootDir + "natives" + File.separator).listFiles()));
			for(int i = 0; i < files.size(); i++) {
				files.get(i).delete();
			}
		}
		
		System.out.println("Extracting natives");
		new ExtractLibs();

		// Make sure that versions folder is there
		if (!new File(Cache.rootDir + "versions" + File.separator).exists()) {
			new File(Cache.rootDir + "versions" + File.separator).mkdir();
		}

		System.out.println("Downloading client: " + details.getString("id") + ".jar");
		new DownloadFile().Download(Cache.rootDir + "versions" + File.separator,
				details.getJSONObject("downloads").getJSONObject("client").getString("url"),
				details.getString("id") + ".jar");

		String startArgs = "java -Djava.library.path=" + (Cache.rootDir + "natives" + File.separator) + " -cp \"";
		for (int i = 0; i < Cache.libPaths.size(); i++) {
			startArgs += Cache.libPaths.get(i) + File.pathSeparator;
		}
		startArgs += Cache.rootDir + "versions" + File.separator + details.getString("id") + ".jar"
				+ "\" " + details.getString("mainClass");

		// Now Login to MC
		JSONObject authTicket = new LoginToMojang().login();

		startArgs = new FinishStartArgs().Finish(startArgs, authTicket, details);

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
