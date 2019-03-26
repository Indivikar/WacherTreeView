package app.interfaces;

import java.io.File;

import app.controller.CTree;

public interface IAppData {

	// nur Ordner verwenden, die Datei nicht mit übergeben
	public static File getAppDataFile(String path) {
						
		String appDataFolder = System.getenv("APPDATA");		
		File indivikarFolder = new File(appDataFolder + File.separator + CTree.firmenName);		
		if (!indivikarFolder.exists()) {
			indivikarFolder.mkdir();
		} 
		
		File appFolder = new File(indivikarFolder + File.separator + CTree.programmName);		
		if (!appFolder.exists()) {
			appFolder.mkdir();
		} 
		
		File folder = new File(appFolder + File.separator + path);		
		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		return folder; 
		
	}
	
}
