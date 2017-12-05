package me.boops;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;

import me.boops.files.ReadFile;
import me.boops.functions.AddJVMArgs;
import me.boops.functions.ExtractLibs;
import me.boops.functions.FetchLibraries;
import me.boops.functions.FinishStartArgs;
import me.boops.net.DownloadFile;
import me.boops.net.DownloadForgeLibs;
import me.boops.net.FetchObjects;
import me.boops.net.MojangAuth;

public class RunGame {
	public RunGame() throws Exception {

		// Start Login

		// Make sure the key is vaild
		// new MojangAuth().validate(Cache.userName);

		// Refresh the key
		new MojangAuth().refresh(Cache.userName);

		// End login

		// Begin required files
		JSONObject versionMeta = getVersionMeta();
		JSONObject assetIndex = getAssetIndex(versionMeta);
		// End required files

		// Start assets hander
		grabAssets(assetIndex, versionMeta);
		// End assets handler

		// Start libs handler
		grabLibs(versionMeta);
		// End libs handler

		// Start forge handler
		setupForge(versionMeta);
		// end forge handler

		// Start natives handler
		setupNatives();
		// End natives handler

		// Download client
		downloadClient(versionMeta);
		// End Download client

		// Start gen launch args
		String launchArgs = launchArgs(versionMeta);
		// End gen launch args

		// Start launch game
		launchGame(launchArgs);
		// End launch game

	}

	private void launchGame(String launchArgs) throws Exception {

		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(launchArgs);

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
	}

	private String launchArgs(JSONObject versionMeta) throws Exception {
		String startArgs = new AddJVMArgs().add(versionMeta);
		startArgs += new FinishStartArgs().Finish(new MojangAuth().getUser(Cache.userName), versionMeta);
		return startArgs + "--tweakClass net.minecraftforge.fml.common.launcher.FMLTweaker";
	}

	private void downloadClient(JSONObject versionMeta) throws Exception {
		// Make sure that versions folder is there
		if (!new File(Cache.cacheDir + "versions" + File.separator).exists()) {
			new File(Cache.cacheDir + "versions" + File.separator).mkdir();
		}

		new DownloadFile().Download(Cache.cacheDir + "versions" + File.separator, versionMeta.getJSONObject("downloads").getJSONObject("client").getString("url"), versionMeta.getString("id") + ".jar");

	}

	private void setupNatives() throws Exception {

		// Setup the natives folder or just clean it
		if (!new File(Cache.rootDir + "natives" + File.separator).exists()) {
			new File(Cache.rootDir + "natives" + File.separator).mkdir();
		} else {
			ArrayList<File> files = new ArrayList<File>(Arrays.asList(new File(Cache.rootDir + "natives" + File.separator).listFiles()));
			for (int i = 0; i < files.size(); i++) {
				files.get(i).delete();
			}
		}

		// Extract the natives
		new ExtractLibs();
	}

	private void setupForge(JSONObject versionMeta) throws Exception {

		if (!Cache.forgeVersion.isEmpty()) {

			// Make sure the folder is there
			String path = Cache.cacheDir + "libraries" + File.separator + "net" + File.separator + "minecraftforge" + File.separator + "forge" + File.separator;
			path += Cache.runVersion + "-" + Cache.forgeVersion + File.separator;
			if (!new File(path).exists()) {
				new File(path).mkdirs();
			}

			String URL = "https://files.minecraftforge.net/maven/net/minecraftforge/forge/" + Cache.runVersion + "-" + Cache.forgeVersion + "/forge-" + Cache.runVersion + "-" + Cache.forgeVersion + "-universal.jar";
			String fileName = (URL.substring(URL.lastIndexOf("/") + 1, URL.length()));

			// Download the forge jar
			new DownloadFile().Download(path, URL, fileName);

			// Add the forge jar to libs
			Cache.libPaths.add(path + fileName);

			// Grab all the required forge libs
			new DownloadForgeLibs(versionMeta);
		}
	}

	private void grabLibs(JSONObject versionMeta) throws Exception {
		// Make sure that libraries folder is there
		if (!new File(Cache.cacheDir + "libraries" + File.separator).exists()) {
			new File(Cache.cacheDir + "libraries" + File.separator).mkdir();
		}

		new FetchLibraries(versionMeta, Cache.cacheDir + "libraries" + File.separator);

	}

	private void grabAssets(JSONObject assetIndex, JSONObject versionMeta) throws Exception {

		// Make sure that assets folder is there
		if (!new File(Cache.cacheDir + "assets" + File.separator).exists()) {
			new File(Cache.cacheDir + "assets" + File.separator).mkdirs();
		}

		// Fetch assets/objects
		new FetchObjects(assetIndex);

		// Fetch logging config (If there)
		if (versionMeta.has("logging")) {
			new DownloadFile().Download(Cache.cacheDir + "assets" + File.separator + "log_configs" + File.separator, versionMeta.getJSONObject("logging").getJSONObject("client").getJSONObject("file").getString("url"), versionMeta.getJSONObject("logging").getJSONObject("client").getJSONObject("file").getString("id"));
		}

	}

	private JSONObject getVersionMeta() throws Exception {
		JSONObject ans = new JSONObject();
		if (new File(Cache.cacheDir + "versions" + File.separator + Cache.runVersion + ".json").exists()) {
			ans = new JSONObject(new ReadFile().Read(Cache.cacheDir + "versions" + File.separator + Cache.runVersion + ".json"));
		} else {
			new File(Cache.cacheDir + "versions" + File.separator).mkdirs();
			new DownloadFile().Download(Cache.cacheDir + "versions" + File.separator, Cache.versionMetaURL, Cache.runVersion + ".json");
			ans = new JSONObject(new ReadFile().Read(Cache.cacheDir + "versions" + File.separator + Cache.runVersion + ".json"));
		}
		return ans;
	}

	private JSONObject getAssetIndex(JSONObject details) throws Exception {

		JSONObject ans = new JSONObject();
		String indexDir = Cache.cacheDir + "assets" + File.separator + "indexes" + File.separator;

		if (new File(indexDir + Cache.runVersion + ".json").exists()) {
			ans = new JSONObject(new ReadFile().Read(indexDir + Cache.runVersion + ".json"));
		} else {
			new File(indexDir).mkdirs();
			new DownloadFile().Download(indexDir, details.getJSONObject("assetIndex").getString("url"), Cache.runVersion + ".json");
			ans = new JSONObject(new ReadFile().Read(indexDir + Cache.runVersion + ".json"));
		}
		return ans;
	}
}
