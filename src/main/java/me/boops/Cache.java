package me.boops;

import java.util.ArrayList;
import java.util.List;

public class Cache {
	
	// Store the strings
	public static String OS = System.getProperty("os.name").split(" ")[0].toLowerCase();
	public static String mcBoopVersion = "1.0.0";
	public static List<String> libPaths = new ArrayList<String>();
	public static List<String> extractPaths = new ArrayList<String>();
	
	// Store Args
	public static boolean checkStatus = false;
	public static boolean printReleaseVersion = false;
	public static boolean printSnapshotVersion = false;
	public static boolean listAllVersions = false;
	public static boolean saveLogin = false;
	public static boolean listForgeVersions = false;
	
	public static String runVersion = "";
	public static String forgeVersion = "";
	public static String rootDir = "";
	public static String cacheDir = "";
	public static String versionMetaURL = "";
	
	public static String userName = "";
	public static String password = "";
	
}
