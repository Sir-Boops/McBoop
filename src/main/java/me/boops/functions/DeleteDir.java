package me.boops.functions;

import java.io.File;

public class DeleteDir {
	
	public DeleteDir(String dirS) {
		
		File dir = new File(dirS);
		if(dir.exists()) {
			rm(dir);
		}
		
	}
	
	private void rm(File dir) {
		for(int i = 0; i < dir.list().length; i++) {
			
			if(new File(dir + File.separator + dir.list()[i]).isDirectory()) {
				
				rm(new File(dir + File.separator + dir.list()[i]));
				
			} else {
				
				new File(dir + File.separator + dir.list()[i]).delete();
				
			}
		}
		dir.delete();
	}
}
