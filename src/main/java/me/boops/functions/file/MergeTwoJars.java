package me.boops.functions.file;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class MergeTwoJars {
    @SuppressWarnings("unchecked")
    public MergeTwoJars(String base_jar, String merge_jar, String output_jar) {
        try {

            // Load the ZipFiles
            ZipFile base_zip = new ZipFile(base_jar);
            ZipFile merge_zip = new ZipFile(merge_jar);

            // Open the new zipfile
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output_jar));

            // Create lists from the first two
            List<ZipEntry> base_list = (List<ZipEntry>) Collections.list(base_zip.entries());
            List<ZipEntry> merge_list = (List<ZipEntry>) Collections.list(merge_zip.entries());

            // Take all the files from base and
            // Put them in output unless
            // The same file name is present
            // in merge_jar then just skip
            for (int i = 0; i < base_list.size(); i++) {
                if (!base_list.get(i).getName().contains("META-INF")) {
                    if (!check_merge(merge_list, base_list.get(i).getName())) {
                        copy_file_to_zip(base_zip, base_list.get(i), zos);
                    }
                }
            }

            for (int i = 0; i < merge_list.size(); i++) {
                if(!merge_list.get(i).getName().contains("META-INF")) {
                    copy_file_to_zip(merge_zip, merge_list.get(i), zos);
                }
            }
            
            zos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copy_file_to_zip(ZipFile src_zip, ZipEntry file_to_copy, ZipOutputStream zos) {
        try {

            InputStream is = src_zip.getInputStream(file_to_copy);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            int inByte;
            while ((inByte = is.read()) != -1) {
                bos.write(inByte);
            }

            zos.putNextEntry(file_to_copy);
            zos.write(bos.toByteArray());
            zos.closeEntry();

            bos.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean check_merge(List<ZipEntry> list, String term) {
        boolean ans = false;
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).getName().equalsIgnoreCase(term.toLowerCase())) {
                ans = true;
            }
        }
        return ans;
    }
}
