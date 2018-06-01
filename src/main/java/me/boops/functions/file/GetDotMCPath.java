package me.boops.functions.file;

import java.io.File;

import me.boops.Main;

public class GetDotMCPath {
    public String path() {
        String ans = System.getProperty("user.home") + File.separator + ".minecraft" + File.separator;
        if (Main.base_os_name.equals("windows")) {
            ans = (System.getenv("APPDATA") + File.separator + ".minecraft" + File.separator);
        }
        return ans;
    }
}
