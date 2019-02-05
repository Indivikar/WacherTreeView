package app.threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import app.TreeViewWatchService.CreateTree;
import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import app.dialog.CopyDialogProgress;
import app.functions.LoadTime;
import app.interfaces.ICursor;
import app.interfaces.ILockDir;
import app.interfaces.ISearchLockedFiles;
import app.loadTime.LoadTime.LoadTimeOperation;
import app.sort.WindowsExplorerComparator;
import app.view.alerts.AlertFilesLocked;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Alert.AlertType;

public class DeleteItemTask extends Task<Void> implements ISearchLockedFiles, ICursor, ILockDir {
	
	private DeleteItemTask deleteItemTask;
	private CTree cTree;
	private PathTreeCell pathTreeCell;
	private CopyDialogProgress pForm;
	
	private ObservableList<String> listAllLockedFiles;
	private List<TreeItem<PathItem>> selectedItems = new ArrayList<TreeItem<PathItem>>();
	private List<Integer> rows = new ArrayList<Integer>();
	
	private int pathsCounter = 0;
	private int countDeletedDir = 0;
	
	private boolean cancelTask = false;
	private boolean skip = false;
	
	public DeleteItemTask(CTree cTree, PathTreeCell pathTreeCell, ObservableList<String> listAllLockedFiles) {
		  System.out.println("new Task");		  
		  this.deleteItemTask = this;		  
		  this.cTree = cTree;
		  this.pathTreeCell = pathTreeCell;
		  this.listAllLockedFiles = listAllLockedFiles;
		  
    	  pForm = new CopyDialogProgress(deleteItemTask);
    	  pForm.activateProgressBar(this);
    	  
    	  bindUIandService(pForm.getDialogStage(), this);
    	  
	}

	@Override
	protected void cancelled() {
		System.out.println("Del cancelled");	
		this.cancelTask = true;
		unlockDir(cTree.getLockFileHandler(), pathTreeCell.getTreeItem().getValue().getLevelOneItem());
		updateMessage("Cancelled");
		// TODO - wenn cancelled dann komplett refresh, der refresh soll von der DB angestossen werden
		// 		- und danach Dialog Close
	}
	
	@Override
	protected void failed() {
		System.err.println("Failed");
		unlockDir(cTree.getLockFileHandler(), pathTreeCell.getTreeItem().getValue().getLevelOneItem());
		updateMessage("Failed");
		// TODO - wenn cancelled dann komplett refresh, der refresh soll von der DB angestossen werden
		// 		- und danach Dialog Close
	}
	
	@Override
	protected void succeeded() {
		removeItem();
		unlockDir(cTree.getLockFileHandler(), pathTreeCell.getTreeItem().getValue().getLevelOneItem());
		CreateTree.wantUpdateTree = false;
		sleep(1000);
		pForm.close();
	}
	
	
	
	@Override
	protected Void call() throws Exception {

		LoadTime.Start();
		
		
		
		for (TreeItem<PathItem> item : pathTreeCell.getTreeView().getSelectionModel().getSelectedItems()) {
			System.out.println("add item: " + item.getValue().getPath() +  " (" + item.getValue().getRow() + ")");
			
			unlockLockFile(cTree.getLockFileHandler(), pathTreeCell);
			
			selectedItems.add(item);
			rows.add(item.getValue().getRow());
			
			pathsCounter++;
			countPaths(item);
		}

		for (TreeItem<PathItem> treeItem : selectedItems) {			
			File file = treeItem.getValue().getPath().toFile();
			deleteFileOrDirectory(file);
		}
		
		LoadTime.Stop("DeleteItemTask()", "");
	
		
		return null;
	}

	private void countPaths(TreeItem<PathItem> item) {
		if (!item.isLeaf()) {			
			for (TreeItem<PathItem> child : item.getChildren()) {
				if (!item.isLeaf()) {
					pathsCounter++;
					countPaths(child);
				}
			}
		}	
	}
	
	
	private void removeItem() {

		rows.sort(Comparator.naturalOrder());
		rows.sort(Comparator.reverseOrder());
		
		for (Integer row : rows) {
			TreeItem<PathItem> item = pathTreeCell.getTreeView().getTreeItem(row);
			if (!item.getValue().getPath().toFile().exists()) {
				boolean isRemoved = item.getParent().getChildren().remove(item);
	            if (isRemoved) {
					System.err.println("remove Item: " + item.getValue().getPath());
				}
			}
		}
	}
	
    private void deleteFileOrDirectory(File file) {
    	System.out.println("Del 1 fertig: " + pathTreeCell.getItem().getPath());
    	skip = false;
    	
		listAllLockedFiles.clear();
		boolean isAccessFileFounded = recursiveSearch(file, pForm);
	    if (isAccessFileFounded) {
	    	System.err.println("Locked File Founded");
	    	updateMessage("Locked File Founded");
//				    	return;
	    	
	    	synchronized (this) {
	    		try {
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	
		    if (skip) {
		    	System.err.println("Skip 1");
				return;
			} else {
				deleteFileOrDirectory(file);
			}						
		}
    	
    	try {
			  if (file.isDirectory()) {

				  	int count = 0;
					while (file.exists() && count < 10) {
						
						if (cancelTask) {										
							return;
						}
//						
						// Count Files and update massages
						countAllPaths(file);
						
						// Delete
						Files.walk(file.toPath())
							.filter(f -> f.toFile().isDirectory())
							.sorted(Comparator.reverseOrder())
							.map(Path::toFile)
							.peek(System.out::println)
							.forEach(ordner -> {
								try {
									if (cancelTask || skip) {												
										return;
									}
									Thread.sleep(50);
									if (ordner.exists() && !cancelTask) {
										FileUtils.cleanDirectory(ordner);
										updater(ordner);
									}									
								} catch (IOException | InterruptedException e) {									
				                     if (isCancelled()) {
				                         updateMessage("Cancelled");
				                         this.cancelTask = true;
				                         return;
				                     } else {
				                    	 e.printStackTrace();
				                     }
								}
							});

						Thread.sleep(50);
						if (file.exists() && !cancelTask) {
							if (skip) {
								break;
							} 
							FileUtils.deleteDirectory(file);
						}						
			    		count++;
			    	}
					
					
			  } else {	   					
				  if (cancelTask) {										
					  return;
				  }

				  if (file.exists() && !cancelTask && !skip) {
						FileUtils.forceDelete(file);	
						updater(file);
						Thread.sleep(1000);
				  }
			  }
		
		} catch (IOException | InterruptedException e) {
				// TODO - refresh the Tree and file is Open;
                if (isCancelled()) {
                    updateMessage("Cancelled");
                    this.cancelTask = true;
                    return;
                } else {
               	   e.printStackTrace();
                }
		}
	}
    
    private void countAllPaths(File file) throws IOException {		
		Files.walk(file.toPath())
		.filter(f -> !f.toFile().isDirectory())
		.map(Path::toFile)
		.forEach(ordner -> {updater(ordner);});
	}

    
    private void updater(File file) {
		countDeletedDir++;
		updateTitle(file.getAbsolutePath());
		updateProgress(countDeletedDir, pathsCounter); 
		int percent = (int)((countDeletedDir * 100.0f) / pathsCounter);
		updateMessage(percent + "%");
	}
    
    public void cancelTask() {
    	this.cancelTask = true;
	}
    
//    private void recursiveSearch(File file) {
//      	 File[] filesList = file.listFiles();
//      	    for (File f : filesList) {
//      	        if (f.isDirectory() && !f.isHidden()) {
//      	            recursiveSearch(f);
//      	        }
//      	        if( f.isFile() ){
//      	        	if (accessFile(f)) {
//      	        		listAllLockedFiles.add(f.getAbsolutePath());
//   					}    	        	
//      	        }
//      	    }
//      	}
	
	private boolean accessFile(File name) {
//			System.out.println("is File Locked: " + name);
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

	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	
	// Setter
	public void setSkip(boolean skip) {this.skip = skip;}

	
}
