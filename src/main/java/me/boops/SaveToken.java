package me.boops;

import java.io.File;

import org.json.JSONObject;

import me.boops.base.Cache;
import me.boops.base.SaveTextFile;
import me.boops.net.LoginToMojang;

public class SaveToken {
	
	public SaveToken() throws Exception {
		
		JSONObject login = new LoginToMojang().login();
		JSONObject toSave = new JSONObject();
		
		toSave.put("accessToken", login.getString("accessToken"));
		toSave.put("clientToken", login.getString("clientToken"));
		toSave.put("userName", login.getJSONObject("selectedProfile").getString("name"));
		
		if(!new File(Cache.cacheDir + File.separator).exists()) {
			new File(Cache.cacheDir + File.separator).mkdirs();
		}
		
		new SaveTextFile().Save(Cache.cacheDir + File.separator, toSave.toString(), "auth.json");
		
		System.out.println("Cached your MC account!");
		System.exit(0);
		
	}
}
