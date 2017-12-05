package me.boops.net;

import org.json.JSONArray;

public class CheckStatus {
	
	public CheckStatus() throws Exception {
		JSONArray status = new JSONArray(new GetURL().get("https://status.mojang.com/check"));
		System.out.println("Current status of services");
		for(int i = 0; i < status.length(); i++) {
			if(status.getJSONObject(i).has("minecraft.net")) {
				System.out.println("Minecraft.net status is: " + status.getJSONObject(i).getString("minecraft.net"));
			}
			if(status.getJSONObject(i).has("session.minecraft.net")) {
				System.out.println("Mojang Session server status is: " + status.getJSONObject(i).getString("session.minecraft.net"));
			}
			if(status.getJSONObject(i).has("account.mojang.com")) {
				System.out.println("Account status is: " + status.getJSONObject(i).getString("account.mojang.com"));
			}
			if(status.getJSONObject(i).has("auth.mojang.com")) {
				System.out.println("Auth status is: " + status.getJSONObject(i).getString("auth.mojang.com"));
			}
			if(status.getJSONObject(i).has("skins.minecraft.net")) {
				System.out.println("Skins status is: " + status.getJSONObject(i).getString("skins.minecraft.net"));
			}
			if(status.getJSONObject(i).has("authserver.mojang.com")) {
				System.out.println("AuthServer status is: " + status.getJSONObject(i).getString("authserver.mojang.com"));
			}
			if(status.getJSONObject(i).has("sessionserver.mojang.com")) {
				System.out.println("Session Server status is: " + status.getJSONObject(i).getString("sessionserver.mojang.com"));
			}
			if(status.getJSONObject(i).has("api.mojang.com")) {
				System.out.println("Mojang API status is: " + status.getJSONObject(i).getString("api.mojang.com"));
			}
			if(status.getJSONObject(i).has("textures.minecraft.net")) {
				System.out.println("Textures status is: " + status.getJSONObject(i).getString("textures.minecraft.net"));
			}
			if(status.getJSONObject(i).has("mojang.com")) {
				System.out.println("Mojang.com status is: " + status.getJSONObject(i).getString("mojang.com"));
			}
		}
		System.exit(0);
	}
}
