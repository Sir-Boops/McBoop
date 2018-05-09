package me.boops.functions;

public class ReplaceAChar {
	
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
