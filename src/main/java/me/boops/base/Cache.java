package me.boops.base;

import java.util.ArrayList;
import java.util.List;

public class Cache {
	
	// Store the strings
	public static String OS = System.getProperty("os.name").split(" ")[0].toLowerCase();
	public static List<String> libPaths = new ArrayList<String>();
	public static List<String> extractPaths = new ArrayList<String>();
	
	// Store Args
	public static boolean checkStatus = false;
	public static boolean printReleaseVersion = false;
	public static boolean printSnapshotVersion = false;
	public static boolean listAllVersions = false;
	
	public static String runVersion = "";
	public static String rootDir = "";
	
	public static String userName = "";
	public static String password = "";
	
}
