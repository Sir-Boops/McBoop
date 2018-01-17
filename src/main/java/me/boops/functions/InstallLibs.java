package me.boops.functions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;

import me.boops.functions.file.CreateFolder;
import me.boops.functions.file.DeleteDir;

public class InstallLibs {
	
	public List<String> install(String dirS, JSONArray libs) {
		
		List<String> libURLS = fetchURLS(libs);
		
		List<String> ans = new ArrayList<String>();
		
		startDownload(libURLS, dirS + "libraries" + File.separator);
		
		extractNatives(dirS, libURLS);
		
		for(int i = 0; i < libURLS.size(); i++) {
			
			if(!libURLS.get(i).contains("natives")) {
				ans.add(dirS + "libraries" + File.separator + libURLS.get(i).substring(libURLS.get(i).indexOf(".net/") + 5, libURLS.get(i).length()));
			}
			
		}
		
		return ans;
		
	}
	
	private List<String> fetchURLS(JSONArray libs){
		List<String> ans = new ArrayList<String>();
		
		for(int i = 0; i < libs.length(); i++) {
			
			if(libs.getJSONObject(i).has("downloads")) {
				
				if(libs.getJSONObject(i).getJSONObject("downloads").has("artifact")) {
					
					ans.add(libs.getJSONObject(i).getJSONObject("downloads").getJSONObject("artifact").getString("url"));
				}
				
				if(libs.getJSONObject(i).getJSONObject("downloads").has("classifiers")) {
					if(libs.getJSONObject(i).getJSONObject("downloads").getJSONObject("classifiers").has("natives-" + System.getProperty("os.name").toLowerCase())) {
						
						ans.add(libs.getJSONObject(i).getJSONObject("downloads").getJSONObject("classifiers")
								.getJSONObject("natives-" + System.getProperty("os.name").toLowerCase()).getString("url"));
					}
				}
			}
		}
		return ans;
	}
	
	private void extractNatives(String dirS, List<String> libURLS) {
		
		new DeleteDir(dirS + "natives" + File.separator);
		new CreateFolder(dirS + "natives" + File.separator);
		
		List<String> natives = new ArrayList<String>();
		
		for(int i = 0; i < libURLS.size(); i++) {
			
			if(libURLS.get(i).contains("natives")) {
				natives.add(dirS + "libraries" + File.separator + libURLS.get(i).substring(libURLS.get(i).indexOf(".net/") + 5, libURLS.get(i).length()));
			}
			
		}
		
		for(int i = 0; i < natives.size(); i++) {
			
			try {
				
				JarFile jar = new JarFile(natives.get(i));
				Enumeration<JarEntry> files = jar.entries();
				
				while(files.hasMoreElements()) {
					
					JarEntry file = (JarEntry) files.nextElement();
					if(file.getName().contains(".")) {
						
						String ext = file.getName().substring(file.getName().lastIndexOf("."), file.getName().length());
						if(ext.equalsIgnoreCase(".so") || ext.equalsIgnoreCase(".dll")) {
							
							System.out.println("Extracting " + file.getName());
							
							InputStream is = jar.getInputStream(file);
							FileOutputStream fos = new FileOutputStream(dirS + "natives" + File.separator + file.getName());
							int inByte;
							
							while ((inByte = is.read()) != -1) {
								fos.write(inByte);
							}
							
							is.close();
							fos.close();
							
						}
					}
				}
				jar.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void startDownload(List<String> libURLS, String dirS) {
		
		for(int i = 0; i < libURLS.size(); i++) {
			
			String distDir = (dirS + libURLS.get(i).substring(libURLS.get(i).indexOf(".net/") + 5, libURLS.get(i).lastIndexOf("/") + 1).replaceAll("/", File.separator));
			
			if(!new File(distDir + libURLS.get(i).substring(libURLS.get(i).lastIndexOf("/") + 1, libURLS.get(i).length())).exists()) {
				System.out.println("Downloading: " + libURLS.get(i).substring(libURLS.get(i).lastIndexOf("/") + 1, libURLS.get(i).length()));
				downloadFile(libURLS.get(i), distDir);
			}
			
		}
	}
	
	private void downloadFile(String URL, String destDir) {
		
		new CreateFolder(destDir);
		
		String fileName = URL.substring(URL.lastIndexOf("/") + 1, URL.length());
		
		try {
			
			URL url = new URL(URL);
			
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10 * 1000);
			conn.setConnectTimeout(10 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux X11; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
			
			conn.connect();
			
			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(new File(destDir + fileName));
			int inByte;
			
			while ((inByte = is.read()) != -1) {
				fos.write(inByte);
			}
			
			is.close();
			fos.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
