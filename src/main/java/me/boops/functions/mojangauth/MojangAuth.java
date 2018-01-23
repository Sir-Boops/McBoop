package me.boops.functions.mojangauth;

public class MojangAuth {
	
	public String[] getSession(String access, String client) {
		
		String[] ans = new String[] {};
		// 0 => accessToken
		// 1 => clientToken
		// 2 => is refresh
		
		boolean shouldRefresh = new AuthValidate().validate(access, client);
		
		if(shouldRefresh) {
			String[] refAns = new AuthRefresh().refresh(access, client);
			ans = new String[] {refAns[0], refAns[1], "true"};
		} else {
			ans = new String[] {access, client, "false"};
		}
		return ans;
	}
}
