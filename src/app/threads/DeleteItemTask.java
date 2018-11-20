package app.threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import app.functions.LoadTime;
import app.loadTime.LoadTime.LoadTimeOperation;
import app.sort.WindowsExplorerComparator;
import app.view.alerts.AlertFilesLocked;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Alert.AlertType;

public class DeleteItemTask extends Task<Void> {

	private PathTreeCell pathTreeCell;
	private ObservableList<String> listAllLockedFiles;
	
	public DeleteItemTask(PathTreeCell pathTreeCell, ObservableList<String> listAllLockedFiles) {
		  this.pathTreeCell = pathTreeCell;
		  this.listAllLockedFiles = listAllLockedFiles;
	}

	@Override
	protected void cancelled() {

	}
	
	@Override
	protected void failed() {

	}
	
	@Override
	protected void succeeded() {
		removeItem(pathTreeCell);
	}
	
	@Override
	protected Void call() throws Exception {
		
//		long start1 = new Date().getTime();
		
		LoadTime.Start();
		
		Path filePath = pathTreeCell.getItem().getPath();
		deleteFileOrDirectory(filePath.toFile());

		LoadTime.Stop("DeleteItemTask()", "");
		
//		long runningTime1 = new Date().getTime() - start1;			
//		CTree.listLoadTime.add(new LoadTimeOperation("DeleteItemTask()", runningTime1 + "", ""));
		
		return null;
	}

	private void removeItem(PathTreeCell pathTreeCell) {
		File file = pathTreeCell.getItem().getPath().toFile();
		if (!file.exists()) {
            TreeItem<PathItem> c = (TreeItem<PathItem>) pathTreeCell.getTreeView().getSelectionModel().getSelectedItem();
            boolean isRemoved = c.getParent().getChildren().remove(c);
		}
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
