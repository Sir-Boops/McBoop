package me.boops.functions.forgehandler;

import java.io.File;

import me.boops.functions.VersionMeta;

public class ForgeFileName {
	
	public static String fileName = "";
	public static String filePath = "";
	
	public ForgeFileName() {
		
		int forgeVersion = Integer.valueOf(VersionMeta.ID.replaceAll("\\.", ""));
		String forgeID = (ForgeHandler.versionID);
		
		//Fix forgeID if it needs it!
		if(ForgeHandler.versionID.contains("-")) {
			forgeID = (ForgeHandler.versionID.split("-")[1]);
		}
		
		if(forgeVersion > 190 && forgeVersion != 1710) {
			String path = ("%mcID%-%forgeID%/forge-%mcID%-%forgeID%-universal.jar".replaceAll("(%mcID%)", VersionMeta.ID).replaceAll("(%forgeID%)", forgeID));
			ForgeFileName.fileName = (path.substring(path.lastIndexOf("/") + 1, path.length()));
			ForgeFileName.filePath = (path.substring(0, path.lastIndexOf("/")) + File.separator);
		}
		
		if(forgeVersion <= 190 || forgeVersion == 1710) {
			String path = ("%mcID%-%forgeID%-%mcID%/forge-%mcID%-%forgeID%-%mcID%-universal.jar".replaceAll("(%mcID%)", VersionMeta.ID).replaceAll("(%forgeID%)", forgeID));
			ForgeFileName.fileName = (path.substring(path.lastIndexOf("/") + 1, path.length()));
			ForgeFileName.filePath = (path.substring(0, path.lastIndexOf("/")) + File.separator);
		}
		
		if(forgeVersion <= 164) {
			System.out.println("");
			System.out.println("Sorry, forge versions 1.6.4 and older are not supported!");
			System.out.println("If you wish to attempt to add support for them open an issue");
			System.out.println("and I'll explan what needs to be done!");
			System.out.println("");
			System.exit(1);
		}
	}
}
