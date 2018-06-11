package me.boops.functions.threads;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import me.boops.functions.profilemanager.ProfileManager;

public class LaunchGameThread implements Runnable {
    
    private List<String> launch_arr = new ArrayList<String>();
    
    public LaunchGameThread(List<String> arr) {
        this.launch_arr = arr;
    }

    @Override
    public void run() {
        
        try {
            
            Process pr = new ProcessBuilder(this.launch_arr).directory(new File(ProfileManager.path)).start();
            
            BufferedReader brErr = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
            BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            
            String lineErr = null;
            while ((lineErr = brErr.readLine()) != null) {
                System.out.println(lineErr);
            }
            
            while(pr.isAlive()) {
                pr.destroy();
            }
            
            pr.waitFor();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
