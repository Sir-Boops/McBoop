package me.boops.functions.file;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.boops.functions.threads.SaveCompressedFileThread;

public class ExtractZip {
    public ExtractZip(String zip_file_path, String dist_dir) {
        try {

            ZipFile zip_file = new ZipFile(zip_file_path);
            @SuppressWarnings("unchecked")
            List<ZipEntry> zip_list = (List<ZipEntry>) Collections.list(zip_file.entries());
            
            ThreadGroup ExtractGroup = new ThreadGroup("ExtractGroup");

            for (int i = 0; i < zip_list.size(); i++) {
                if (!new File(dist_dir + zip_list.get(i).getName()).exists()) {
                    if (!zip_list.get(i).getName().endsWith("/")) {
                        System.out.println("Extracting Lib: " + zip_list.get(i).getName());
                        
                        while(ExtractGroup.activeCount() > 9) {
                            Thread.sleep(200);
                        }
                        
                        Thread thread = new Thread(ExtractGroup, new SaveCompressedFileThread(zip_file, zip_list.get(i), dist_dir));
                        thread.start();
                        
                    } else {
                        new CreateFolder(dist_dir + zip_list.get(i).getName());
                    }
                }
            }
            
            while(ExtractGroup.activeCount() > 0) {
                Thread.sleep(200);
            }

            zip_file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
