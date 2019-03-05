package app.interfaces;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.controller.CTree;
import app.view.alerts.AlertFilesLocked;
import app.view.alerts.CopyDialogProgress;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;

public interface ISearchLockedFiles {
	
//	public static AlertFilesLocked alertFilesLocked = new AlertFilesLocked(AlertType.ERROR);
	
    public default boolean recursiveSearch(File file, CopyDialogProgress pForm) {
    	boolean isAccessFileFounded = false;
    	
    	if (!file.isDirectory()) {
    		pForm.addLockedFile(file);
    		return isFileLocked(file);
		}
    	
	 	 File[] filesList = file.listFiles();
	 	    for (File f : filesList) {
	 	        if (f.isDirectory() && !f.isHidden()) {
	 	            recursiveSearch(f, pForm);
	 	        }
	 	        if( f.isFile() ){
	 	        	if (!isFileOnIgnoreList(f.getName()) && isFileLocked(f)) {
	 	        		pForm.addLockedFile(f);
	 	        		isAccessFileFounded = true;
					}    	        	
	 	        }
	 	    }
			return isAccessFileFounded;
	 	}
	
    public default boolean isFileOnIgnoreList(String fileName) {
    	List<String> fileList = new ArrayList<String>();
    	fileList.add(CTree.lockFileName);
    	
    	for (String string : fileList) {
			if (string.equalsIgnoreCase(fileName)) {
		    	return true;
			}
		}    	
    	return false;
    }
    
    public default boolean isFileLocked(File name) {
//		System.out.println("is File Locked: " + name);
		System.out.println(name.canWrite()); // -> true
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream =  new FileOutputStream(name);
			fileOutputStream.close();
		} catch (IOException e) {			
				System.out.println("___" + e.getMessage());	

				
//				fileOutputStream.close();
				return true;						
		}
    
	return false;
}
}
