package me.boops.functions.threads;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SaveCompressedFileThread implements Runnable {
    
    private ZipFile file;
    private ZipEntry entry;
    private String dist;
    
    public SaveCompressedFileThread(ZipFile zip_file, ZipEntry comp_file, String dist_dir) {
        this.file = zip_file;
        this.entry = comp_file;
        this.dist = dist_dir;
    }

    @Override
    public void run() {
        
        try {
            
            InputStream is = this.file.getInputStream(this.entry);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            FileOutputStream fos = new FileOutputStream(this.dist + this.entry.getName());
            

            int inByte;
            while ((inByte = is.read()) != -1) {
                bos.write(inByte);
            }

            fos.write(bos.toByteArray());

            fos.close();
            bos.close();
            is.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
