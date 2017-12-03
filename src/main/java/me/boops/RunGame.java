package me.boops;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;

import me.boops.base.Cache;
import me.boops.base.SaveTextFile;
import me.boops.functions.AddJVMArgs;
import me.boops.functions.ExtractLibs;
import me.boops.functions.FetchLibraries;
import me.boops.functions.FetchLoggingConfig;
import me.boops.functions.FinishStartArgs;
import me.boops.functions.ReadFile;
import me.boops.net.DownloadFile;
import me.boops.net.FetchObjects;
import me.boops.net.LoginToMojang;

public class RunGame {
	public RunGame() throws Exception {

		// Get launcher meta
		JSONObject details = getVersionMeta();
		// Get assetIndex
		JSONObject assetIndex = getAssetIndex(details);

		// Make sure the root-dir is there
		if (!new File(Cache.cacheDir).exists()) {
			// Make the root-dir
			new File(Cache.cacheDir).mkdirs();
		}

		// Make sure that assets folder is there
		if (!new File(Cache.cacheDir + "assets" + File.separator).exists()) {
			new File(Cache.cacheDir + "assets" + File.separator).mkdir();
		}

		// Fetch all the assets
		new SaveTextFile().Save(Cache.cacheDir + "assets" + File.separator + "indexes" + File.separator,
				assetIndex.toString(), details.getJSONObject("assetIndex").getString("id") + ".json");
		if (details.has("logging")) {
			new FetchLoggingConfig(details.getJSONObject("logging"), Cache.cacheDir + "assets" + File.separator);
		}
		new FetchObjects().Download(assetIndex);

		// Make sure that libraries folder is there
		if (!new File(Cache.cacheDir + "libraries" + File.separator).exists()) {
			new File(Cache.cacheDir + "libraries" + File.separator).mkdir();
		}

		// Fetch all the libs
		new FetchLibraries(details, Cache.cacheDir + "libraries" + File.separator);

		// Setup the natives
		if (!new File(Cache.rootDir + "natives" + File.separator).exists()) {
			new File(Cache.rootDir + "natives" + File.separator).mkdir();
		} else {
			ArrayList<File> files = new ArrayList<File>(
					Arrays.asList(new File(Cache.rootDir + "natives" + File.separator).listFiles()));
			for (int i = 0; i < files.size(); i++) {
				files.get(i).delete();
			}
		}

		System.out.println("Extracting natives");
		new ExtractLibs();

		// Make sure that versions folder is there
		if (!new File(Cache.cacheDir + "versions" + File.separator).exists()) {
			new File(Cache.cacheDir + "versions" + File.separator).mkdir();
		}
		
		System.out.println(details);

		System.out.println("Downloading client: " + details.getString("id") + ".jar");
		new DownloadFile().Download(Cache.cacheDir + "versions" + File.separator,
				details.getJSONObject("downloads").getJSONObject("client").getString("url"),
				details.getString("id") + ".jar");

		String startArgs = new AddJVMArgs().add(details);

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

	private JSONObject getVersionMeta() throws Exception {
		JSONObject ans = new JSONObject();
		if (new File(Cache.cacheDir + "versions" + File.separator + Cache.runVersion + ".json").exists()) {
			ans = new ReadFile().Read(Cache.cacheDir + "versions" + File.separator + Cache.runVersion + ".json");
		} else {
			new File(Cache.cacheDir + "versions" + File.separator).mkdirs();
			new DownloadFile().Download(Cache.cacheDir + "versions" + File.separator, Cache.versionMetaURL,
					Cache.runVersion + ".json");
			ans = new ReadFile().Read(Cache.cacheDir + "versions" + File.separator + Cache.runVersion + ".json");
		}
		return ans;
	}

	private JSONObject getAssetIndex(JSONObject details) throws Exception {

		JSONObject ans = new JSONObject();
		String indexDir = Cache.cacheDir + "assets" + File.separator + "indexes" + File.separator;

		if (new File(indexDir + Cache.runVersion + ".json").exists()) {
			ans = new ReadFile().Read(indexDir + Cache.runVersion + ".json");
		} else {
			new File(indexDir).mkdirs();
			new DownloadFile().Download(indexDir, details.getJSONObject("assetIndex").getString("url"),
					Cache.runVersion + ".json");
			ans = new ReadFile().Read(indexDir + Cache.runVersion + ".json");
		}
		return ans;
	}

}
