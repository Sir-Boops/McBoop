package me.boops.functions.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ExtractZip {
    public ExtractZip(String zip_file_path, String dist_dir) {
        try {

            ZipFile zip_file = new ZipFile(zip_file_path);
            @SuppressWarnings("unchecked")
            List<ZipEntry> zip_list = (List<ZipEntry>) Collections.list(zip_file.entries());

            for (int i = 0; i < zip_list.size(); i++) {
                if (!new File(dist_dir + zip_list.get(i).getName()).exists()) {
                    if (!zip_list.get(i).getName().endsWith("/")) {
                        System.out.println("Extracting Lib: " + zip_list.get(i).getName());

                        InputStream is = zip_file.getInputStream(zip_list.get(i));
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        FileOutputStream fos = new FileOutputStream(dist_dir + zip_list.get(i).getName());

                        int inByte;
                        while ((inByte = is.read()) != -1) {
                            bos.write(inByte);
                        }

                        fos.write(bos.toByteArray());

                        fos.close();
                        bos.close();
                        is.close();
                    } else {
                        new CreateFolder(dist_dir + zip_list.get(i).getName());
                    }
                }
            }

            zip_file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
