package me.boops.functions.string_utls;

public class ReplaceChars {
	
	public String replace(String string, String to_replace, String replacement) {
		new String();
		String ans = "";
		char[] char_list = string.toCharArray();
		
		for(int i = 0; i < char_list.length; i++) {
			ans += (String.valueOf(char_list[i]).replace(to_replace, replacement));
		}
		return ans;
	}
}
