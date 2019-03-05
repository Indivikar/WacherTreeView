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
import app.functions.LoadTime;
import app.interfaces.ICursor;
import app.interfaces.ILockDir;
import app.interfaces.ISearchLockedFiles;
import app.loadTime.LoadTime.LoadTimeOperation;
import app.sort.WindowsExplorerComparator;
import app.view.alerts.AlertFilesLocked;
import app.view.alerts.CopyDialogProgress;
import app.view.alerts.DefaultAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Alert.AlertType;

public class DeleteItemTask extends Task<Void> implements ISearchLockedFiles, ICursor, ILockDir {
	
	private DeleteItemTask deleteItemTask;
	private CTree cTree;
	private CreateTree createTree;
	private PathTreeCell pathTreeCell;
	private TreeItem<PathItem> treeItem;
	private CopyDialogProgress copyDialogProgress;
	
	private ObservableList<String> listAllLockedFiles;
//	private ObservableList<TreeItem<PathItem>> selectedItems;
//	private List<TreeItem<PathItem>> selectedItems = new ArrayList<TreeItem<PathItem>>();
	private List<TreeItem<PathItem>> itemsToRemove = new ArrayList<TreeItem<PathItem>>();
	
	private int pathsCounter = 0; // für die ProgressBar
	private int countDeletedDir = 0;
	
	private boolean showDialog = false;
	private boolean cancelTask = false;
	private boolean skip = false;
	
//	public DeleteItemTask(CTree cTree, PathTreeCell pathTreeCell, TreeItem<PathItem> treeItem, ObservableList<String> listAllLockedFiles) {
//		new DeleteItemTask(cTree, pathTreeCell, treeItem, listAllLockedFiles, true);
//	}

	public DeleteItemTask(CTree cTree, PathTreeCell pathTreeCell, TreeItem<PathItem> treeItem, 
			ObservableList<String> listAllLockedFiles, boolean showDialog) {
		  System.out.println("new Task");		  
		  this.deleteItemTask = this;		  
		  this.cTree = cTree;
		  this.createTree = cTree.getCreateTree();
		  this.pathTreeCell = pathTreeCell;
		  this.treeItem = treeItem;
		  this.listAllLockedFiles = listAllLockedFiles;
//		  this.selectedItems = selItems;
		  this.showDialog = showDialog;
		  
		  if (showDialog) {
			  copyDialogProgress = new CopyDialogProgress(deleteItemTask);
			  copyDialogProgress.activateProgressBar(this);
    	  
			  bindUIandService(copyDialogProgress.getDialogStage(), this);
		  }

	}

	@Override
	protected void cancelled() {
		System.out.println("Del cancelled");	
		this.cancelTask = true;
//		unlockDir(cTree.getLockFileHandler(), pathTreeCell.getTreeItem().getValue().getLevelOneItem());
		unlockDir(cTree.getLockFileHandler(), treeItem.getValue().getLevelOneItem());
		updateMessage("Cancelled");
		// TODO - wenn cancelled dann komplett refresh, der refresh soll von der DB angestossen werden
		createTree.startCreateTree(cTree.getTree().getRoot(), false, false);
		// 		- und danach Dialog Close
		if (showDialog) {
			copyDialogProgress.close();
		}
	}
	
	@Override
	protected void failed() {
		// TODO - Alert mit fehlermeldung einbauen
		System.err.println("Failed");		
//		unlockDir(cTree.getLockFileHandler(), pathTreeCell.getTreeItem().getValue().getLevelOneItem());
		unlockDir(cTree.getLockFileHandler(), treeItem.getValue().getLevelOneItem());
		updateMessage("Failed");
		// TODO - wenn cancelled dann komplett refresh, der refresh soll von der DB angestossen werden
		createTree.startCreateTree(cTree.getTree().getRoot(), false, false);
		// 		- und danach Dialog Close
		if (showDialog) {
			copyDialogProgress.close();
		}
	}
	
	@Override
	protected void succeeded() {
		removeItem();
//		unlockDir(cTree.getLockFileHandler(), pathTreeCell.getTreeItem().getValue().getLevelOneItem());		
		unlockDir(cTree.getLockFileHandler(), treeItem.getValue().getLevelOneItem());

		// Tree nur updaten, wenn das LevelOne Node leer ist, weil sonst das Lock-Icon nicht auf unlock wechselt, wenn das LevelOne Node leer ist
//		int levelOneSize = treeItem.getValue().getLevelOneItem().getChildren().size();
//		if(levelOneSize == 0) {
//			cTree.refreshTree(true);
//		} else {
//			CreateTree.wantUpdateTree = false;
//		}
		
		sleep(1000);
		

//		cTree.refreshTree();
//		if (b) {
//			treeItem.getValue().getLevelOneItem().getValue().setLocked(false);
//		}
//		System.out.println("succeeded: " + treeItem.getValue().getLevelOneItem().getValue().isLocked());
		if (copyDialogProgress != null) {
			System.out.println("______Delete -> copyDialogProgress close______");
			copyDialogProgress.close();
		}
		System.out.println("______Delete -> succeeded______");
		
	}
	
	
	
	@Override
	protected Void call() throws Exception {
		System.out.println("______Delete -> Start______");
		
		
		LoadTime.Start();
		
		// zähle die Pfade für die Progressbar
		pathsCounter = pathsCounter + cTree.getSelectedItems().size();
		
//		for (TreeItem<PathItem> item : pathTreeCell.getTreeView().getSelectionModel().getSelectedItems()) {
//			System.out.println("add item: " + item.getValue().getPath() +  " (" + item.getValue().getRow() + ")  unlock after Work -> " + treeItem.getValue().getPath());
//			
////			unlockLockFile(cTree.getLockFileHandler(), pathTreeCell);
////			unlockLockFile(cTree.getLockFileHandler(), treeItem);
//			unlockLockFile(cTree.getLockFileHandler(), item);
//			
//			selectedItems.add(item);
//			itemsToRemove.add(item);
//			
//			pathsCounter++;
//			countPaths(item);
//		}

		for (TreeItem<PathItem> treeItem : cTree.getSelectedItems()) {	
			// zähle die Pfade der Unter-Ordner für die Progressbar
			countPaths(treeItem);
			
			unlockLockFile(cTree.getLockFileHandler(), treeItem);
			
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
		System.out.println("______removeItem -> Start______");
//		itemsToRemove.get(index).sort(Comparator.naturalOrder());
		itemsToRemove.sort((a, b) -> Integer.compare(b.getValue().getRow(), a.getValue().getRow()));
//		itemsToRemove.sort(Comparator.reverseOrder());
		System.out.println("Liste SelectedItems: " + cTree.getSelectedItems().size());
		for (TreeItem<PathItem> item : cTree.getSelectedItems()) {
//			TreeItem<PathItem> item = pathTreeCell.getTreeView().getTreeItem(row);	
			System.err.println("vor remove Item: " + item.getValue().getPath() + "  -> item.getParent(): " + item.getParent().getValue().getPath() 
					+ "  -> exists Item: " + item.getValue().getPath().toFile().exists());
			if (!item.getValue().getPath().toFile().exists()) {
				
				System.out.println("Parent Item" + item.getParent());
				
				if (item.getParent() == null) {
					// TODO - reload tree
					
				} else {
					boolean isRemoved = item
						.getParent()
						.getChildren()
						.remove(item);
					if (isRemoved) {
						System.err.println("remove Item: " + item.getValue().getPath());
						cTree.getTree().refresh();
					}
				}
				


			}
		}
		
		System.out.println("______removeItem -> Ende______");
		
//		for (Integer row : rows) {
//			TreeItem<PathItem> item = pathTreeCell.getTreeView().getTreeItem(row);	
//			System.err.println("vor remove Item: " + item.getValue().getPath() + "  -> exists Item: " + item.getValue().getPath().toFile().exists());
//			if (!item.getValue().getPath().toFile().exists()) {
//				boolean isRemoved = item.getParent().getChildren().remove(item);
//	            if (isRemoved) {
//					System.err.println("remove Item: " + item.getValue().getPath());
//				}
//			}
//		}
	}
	
    private void deleteFileOrDirectory(File file) {
    	System.out.println("Del 1 fertig: " + pathTreeCell.getItem().getPath());
    	skip = false;
    	
		listAllLockedFiles.clear();
		boolean isAccessFileFounded = recursiveSearch(file, copyDialogProgress);
	    if (isAccessFileFounded) {
	    	System.err.println("Locked File Founded");
	    	// TODO - Message ändern
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
						
						System.err.println("Delete File: " + file.getAbsolutePath());
						
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
				                         // TODO - den namen "folder.lock" zentralisieren
				                         File lockFile = new File(ordner + File.separator + CTree.lockFileName);
				                         if (lockFile.exists()) {
				                        	 // TODO - Alert Layout ändern
											new DefaultAlert(cTree.getPrimaryStage(), 
													AlertType.ERROR, "Fehler", "Die Lock-Datei im Ordner \"" + ordner + "\" konnte nicht gelöscht werden.");
										 }
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
		updateTitle("Delete: " + file.getAbsolutePath());
		updateProgress(countDeletedDir, pathsCounter); 
		int percent = (int)((countDeletedDir * 100.0f) / pathsCounter);
		if (percent <= 100) {
			updateMessage(percent + "%");
		}
		
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

//	private void sleep(int ms) {
//		try {
//			Thread.sleep(ms);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	
//	}
	
	
	// Setter
	public void setSkip(boolean skip) {this.skip = skip;}

	
}
