package me.boops.functions.mojangauth;

import org.json.JSONArray;

import me.boops.Main;
import me.boops.functions.file.LoadAuthFile;
import me.boops.functions.file.WriteTextToFile;

public class MojangAuth {
	
	public String[] getSession(String access, String client) {
		
		String[] ans = new String[] {};
		// 0 => accessToken
		// 1 => clientToken
		// 2 => is refresh
		
		boolean shouldRefresh = new AuthValidate().validate(access, client);
		
		if(shouldRefresh) {
			String[] refAns = new AuthRefresh().refresh(access, client);
			ans = new String[] {refAns[0], refAns[1]};
			updateAuthFile(refAns);
		} else {
			ans = new String[] {access, client};
		}
		return ans;
	}
	
	private void updateAuthFile(String[] auth) {
		
		JSONArray authFile = new LoadAuthFile().load();
		
		for(int i = 0; i < authFile.length(); i++) {
			if(authFile.getJSONObject(i).getString("id").equalsIgnoreCase(auth[2])) {
				authFile.getJSONObject(i).put("accessToken", auth[0]);
				authFile.getJSONObject(i).put("clientToken", auth[1]);
				new WriteTextToFile(Main.homeDir + "auth.json", authFile.toString());
				i = (authFile.length() + 1);
			}
		}
	}
}
