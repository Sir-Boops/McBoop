package me.boops.functions.file;

import java.io.File;

public class CreateFolder {
    
    // This will only create the folder if it dosn't already exist

    public CreateFolder(String dir_path) {

        File dir = new File(dir_path);
        boolean ans = false;

        if (!dir.exists() && !dir.isDirectory()) {
            // Create the requested dir
            ans = dir.mkdirs();
        } else {
            ans = true;
        }

        if (!ans) {
            System.out.println("Error creating folder: " + dir_path);
            System.exit(1);
        }
    }
}
