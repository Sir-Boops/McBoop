package me.boops.functions.forgehandler;

import java.io.File;

import me.boops.functions.VersionMeta;

public class ForgeFileName {
	
	public static String fileName = "";
	public static String filePath = "";
	
	public ForgeFileName() {
		
		int forgeVersion = Integer.valueOf(VersionMeta.ID.replaceAll("\\.", ""));
		
		if(forgeVersion > 190 && forgeVersion != 1710) {
			String path = ("%mcID%-%forgeID%/forge-%mcID%-%forgeID%-universal.jar".replaceAll("(%mcID%)", VersionMeta.ID).replaceAll("(%forgeID%)", ForgeHandler.versionID));
			ForgeFileName.fileName = (path.substring(path.lastIndexOf("/") + 1, path.length()));
			ForgeFileName.filePath = (path.substring(0, path.lastIndexOf("/")) + File.separator);
		}
		
		if(forgeVersion <= 190 || forgeVersion == 1710) {
			String path = ("%mcID%-%forgeID%-%mcID%/forge-%mcID%-%forgeID%-%mcID%-universal.jar".replaceAll("(%mcID%)", VersionMeta.ID).replaceAll("(%forgeID%)", ForgeHandler.versionID));
			ForgeFileName.fileName = (path.substring(path.lastIndexOf("/") + 1, path.length()));
			ForgeFileName.filePath = (path.substring(0, path.lastIndexOf("/")) + File.separator);
		}
	}
}
