package me.boops.functions.forgehandler;

import java.io.File;

import me.boops.functions.VersionMeta;

public class ForgeFileName {
    public String[] getInfo(String version_id) {

        // Take the first three numbers from the MC version
        String[] forge_id_split = VersionMeta.ID.split("\\.");
        double forge_version = Integer.valueOf(forge_id_split[0] + forge_id_split[1]);
        if (forge_id_split.length >= 3) {
            forge_version = (forge_version + ((double) Integer.valueOf(forge_id_split[2]) / 10));
        }

        // Fix forgeID if it needs it!
        String forgeID = (version_id);
        if (version_id.contains("-")) {
            forgeID = (version_id.split("-")[1]);
        }

        ForgeHandler.forge_version = forge_version;

        String file_name = "";
        String file_path = "";

        // If starting a forge version newer then 1.9
        if (forge_version > 19) {
            String path = ("%mcID%-%forgeID%/forge-%mcID%-%forgeID%-universal.jar".replaceAll("(%mcID%)", VersionMeta.ID).replaceAll("(%forgeID%)", forgeID));
            file_name = (path.substring(path.lastIndexOf("/") + 1, path.length()));
            file_path = (path.substring(0, path.lastIndexOf("/")) + File.separator);
        }

        // If starting a forge version older then 1.9 but newer then 1.6
        if (forge_version <= 19 && forge_version > 16.4) {
            String path = ("%mcID%-%forgeID%-%mcID%/forge-%mcID%-%forgeID%-%mcID%-universal.jar".replaceAll("(%mcID%)", VersionMeta.ID).replaceAll("(%forgeID%)", forgeID));
            file_name = (path.substring(path.lastIndexOf("/") + 1, path.length()));
            file_path = (path.substring(0, path.lastIndexOf("/")) + File.separator);
        }

        if (forge_version <= 16.4 && forge_version > 15.2) {
            String path = ("%mcID%-%forgeID%/forge-%mcID%-%forgeID%-universal.jar".replaceAll("(%mcID%)", VersionMeta.ID).replaceAll("(%forgeID%)", forgeID));
            file_name = (path.substring(path.lastIndexOf("/") + 1, path.length()));
            file_path = (path.substring(0, path.lastIndexOf("/")) + File.separator);
        }

        if (forge_version == 15.2) {
            String path = ("%mcID%-%forgeID%/forge-%mcID%-%forgeID%-universal.zip".replaceAll("(%mcID%)", VersionMeta.ID).replaceAll("(%forgeID%)", forgeID));
            file_name = (path.substring(path.lastIndexOf("/") + 1, path.length()));
            file_path = (path.substring(0, path.lastIndexOf("/")) + File.separator);
        }

        if (forge_version < 15.2) {
            System.out.println("Sorry, Forge for Minecraft older then 1.6.4 are currently unsupported");
            System.out.println("If you wish to help add support for this please open an issue here -> https://git.sergal.org/Sir-Boops/McBoop/issues");
            System.exit(1);
        }
        return new String[] { file_name, file_path };
    }
}
