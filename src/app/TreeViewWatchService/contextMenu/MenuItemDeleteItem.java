package app.TreeViewWatchService.contextMenu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.apache.commons.io.FileUtils;

import app.TreeViewWatchService.FileAlterationListenerImpl;
import app.TreeViewWatchService.PathTreeCell;
import app.view.alerts.AlertFilesLocked;
import javafx.scene.control.MenuItem;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;

public class MenuItemDeleteItem extends MenuItem{

	private PathTreeCell pathTreeCell;
	private ObservableList<String> listAllLockedFiles;
	
	public MenuItemDeleteItem(PathTreeCell pathTreeCell, ObservableList<String> listAllLockedFiles) {
		  this.pathTreeCell = pathTreeCell;
		  this.listAllLockedFiles = listAllLockedFiles;
		
		  setText("Delete");
	      setOnAction((event) -> {
	    	  FileAlterationListenerImpl.isInternalChange = true;
	    	  Path filePath = pathTreeCell.getItem().getPath();
	    	  deleteFileOrDirectory(filePath.toFile());
	    	  
	      });
	}

    private void deleteFileOrDirectory(File file) {
    	System.out.println("Del 1 fertig: " + pathTreeCell.getItem().getPath());
    	try {
			  if (file.isDirectory()) {
				    listAllLockedFiles.clear();
				    recursiveSearch(file);
				    if (listAllLockedFiles.size() > 0) {
				    	new AlertFilesLocked(AlertType.ERROR, listAllLockedFiles);
//						alertFilesLocked();
						return;
					}
				    				   
				  	int count = 0;
					while (file.exists() && count < 10) {
						
					Files.walk(file.toPath())
							.filter(f -> f.toFile().isDirectory())
							.sorted(Comparator.reverseOrder())
							.map(Path::toFile)
							.peek(System.out::println)
							.forEach(ordner -> {
								try {
									Thread.sleep(50);
									if (ordner.exists()) {
										FileUtils.cleanDirectory(ordner);
									}									
								} catch (IOException | InterruptedException e) {
									e.printStackTrace();
								}
							});
					
						Thread.sleep(50);
						if (file.exists()) {
							FileUtils.deleteDirectory(file);
						}						
			    		count++;
			    	}

			  } else {	   				  
					FileUtils.forceDelete(file);	
					Thread.sleep(1000);
			  }
		
		} catch (IOException | InterruptedException e) {
				// TODO - refresh the Tree and file is Open
				e.printStackTrace();
		}
	}
    
    private void recursiveSearch(File file) {
      	 File[] filesList = file.listFiles();
      	    for (File f : filesList) {
      	        if (f.isDirectory() && !f.isHidden()) {
      	            recursiveSearch(f);
      	        }
      	        if( f.isFile() ){
      	        	if (accessFile(f)) {
      	        		listAllLockedFiles.add(f.getAbsolutePath());
   					}    	        	
      	        }
      	    }
      	}
	
	private boolean accessFile(File name) {
			System.out.println("is File Locked: " + name);
			System.out.println(name.canWrite()); // -> true
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream =  new FileOutputStream(name);
				fileOutputStream.close();
			} catch (IOException e) {			
					System.out.println(e.getMessage());	
	//				fileOutputStream.close();
					return true;						
			}
	    
		return false;
	}
    
}
