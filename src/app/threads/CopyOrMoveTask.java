package app.threads;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import app.TreeViewWatchService.CreateTree;
import app.TreeViewWatchService.DragNDropInternal;
import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import app.interfaces.IBindings;
import app.interfaces.ILockDir;
import app.interfaces.ITreeItemMethods;
import app.models.SourceTarget;
import app.view.Stages.StageFileIsExist;
import app.view.alerts.CopyDialogProgress;
import app.view.alerts.DefaultAlert;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Alert.AlertType;

public class CopyOrMoveTask extends Task<Void> implements IBindings, ITreeItemMethods, ILockDir {

	private CTree cTree;
	private CreateTree createTree;
	private DragNDropInternal dragNDropInternal;
	private CopyDialogProgress copyDialogProgress;
	
	private PathTreeCell cell;
	private TreeItem<PathItem> treeItem;
	private Set<Path> inputSelectedFiles;
	private Set<SourceTarget> selectedFiles;
	private Path sourceDir = null;
	private Path targetDir = null;
	
	private boolean isMove;	
	private boolean isSameForAll = false;
	private boolean replaceYes = false;
	
	private Object lock = new Object();
	
	private int currentCounter;

	private int DeleteVersuche = 10;
	
//	private ObservableList<TreeItem<PathItem>> selItems;
	private ObservableList<String> saveLockFiles;
	
	public CopyOrMoveTask(CTree cTree, DragNDropInternal dragNDropInternal, PathTreeCell cell, TreeItem<PathItem> treeItem, Set<Path> inputSelectedFiles, Set<SourceTarget> selectedFiles,
			Path sourceDir, Path targetDir) {

		System.out.println("init CopyOrMoveTask 1");
		this.cTree = cTree;
		this.createTree = cTree.getCreateTree();
		this.dragNDropInternal = dragNDropInternal;
		this.cell = cell;
		this.treeItem = treeItem;
		this.inputSelectedFiles = inputSelectedFiles;
		this.selectedFiles = selectedFiles;
		this.sourceDir = sourceDir;
		this.targetDir = targetDir;
//		this.isMove = isMove;

		this.copyDialogProgress = new CopyDialogProgress(null);
		bindUIandService(copyDialogProgress.getDialogStage(), this);
		System.out.println("init CopyOrMoveTask 2");
		
	}

	public void next(){
        synchronized(lock){
            lock.notify();
        }   
    }
	
	@Override
	protected void failed() {
			// TODO - Fehlermeldung �ndern und Layout anpassen
	        new DefaultAlert(cTree.getPrimaryStage(), AlertType.ERROR, "ERROR", "There was an error during the copy process", "");
	        sortUnlockUpdate();
			// TODO - wenn cancelled dann komplett refresh, der refresh soll von der DB angestossen werden
	        createTree.startCreateTree(cell.getTreeView().getRoot(), false, false);
			// 		- und danach Dialog Close
//	        sleep(1000);
	        copyDialogProgress.close();
	}
	
	@Override
	protected void cancelled() {
		sortUnlockUpdate();
		// TODO - wenn cancelled dann komplett refresh, der refresh soll von der DB angestossen werden
		createTree.startCreateTree(cell.getTreeView().getRoot(), false, false);
		// 		- und danach Dialog Close
//		sleep(1000);
		copyDialogProgress.close();
	}
	
	@Override
	protected void scheduled() {
		copyDialogProgress.activateProgressBar(this);
	}
	
	@Override
    protected void succeeded() {
		if (isMove) {
			DeleteItemTask deleteItemTask = new DeleteItemTask(cTree, cell, treeItem, saveLockFiles, false);
			new Thread(deleteItemTask).start();
		} else {
			copyDialogProgress.close();
		}
		
		sortUnlockUpdate(); 
//		sleep(1000);
		
		System.out.println("______Copy or Move -> Succeeded______");
    }
	
	private void sortUnlockUpdate() {
		Platform.runLater(() -> {
				sortItems(cell, copyDialogProgress);
				unlockDir(cTree.getLockFileHandler(), treeItem.getValue().getLevelOneItem());
				unlockDir(cTree.getLockFileHandler(), cTree.getSelectedItems().get(0).getValue().getLevelOneItem());
	//			unlockDir(cTree.getLockFileHandler(), cell.getTreeItem().getValue().getLevelOneItem());
//				updateTreeIfLevelOneNodeEmpty(cTree, treeItem.getValue().getLevelOneItem());
//				updateTreeIfLevelOneNodeEmpty(cTree, cTree.getSelectedItems().get(0).getValue().getLevelOneItem());
				cell.getTreeView().refresh();
		});	
	}
	
	@Override
	protected Void call() throws Exception {
			System.out.println("______Copy or Move -> Start");
			System.out.println("Row: " + treeItem.getValue().getRow() + " - " + treeItem.getValue().getPath());
        


//            logger.info("Copying files.");
//            Platform.runLater(() -> {
//                copyBtn.setDisable(true);
//                closeBtn.setDisable(true);
//                cancelBtn.setDisable(false);
//                filtersBtn.setDisable(true);
//                zipCheckBox.setDisable(true);
//                selectTargetBtn.setDisable(true);
//            });
            
        	
        	
//            Set<Path> filteredFiles = applyFileFilters(inputSelectedFiles, sourceDir);    
            Set<Path> filteredFiles = inputSelectedFiles;   
//            System.out.println(0 + " -> " + filteredFiles.size());
            Map<Boolean, List<Path>> countsMap = filteredFiles.stream()
                .collect(Collectors.partitioningBy(p -> Files.isDirectory(p)));
//            int dirsCount = countsMap.get(true).size() - 1; // minus root dir
//            int filesCount = countsMap.get(false).size();
//            logger.info("Filters applied. " +
//                "Directories [" + ((dirsCount < 0) ? 0 : dirsCount) + "], " +
//                "Files [" + filesCount + "].");

            Thread.sleep(100); // pause for n milliseconds
//            logger.info("Copy in progress...");

            /*
             * Walks the source file tree and copies the filtered source
             * files to the target directory. The directories and files are
             * copied. In case of any existing directories or files in the
             * target, they are replaced.
             */
            
            for (SourceTarget item : selectedFiles) {
//				walker(item, dirsCount, filesCount, currentCounter);

     		   sourceDir = item.getSource();
    		   targetDir = item.getTarget();
            	
    		   System.err.println("sourceDir: " + sourceDir + "  ->  targetDir: " + targetDir);
    		   
    		// is DragNDrop internal
//    		   TreeItem<PathItem> c = (TreeItem<PathItem>) cell.getTreeView().getSelectionModel().getSelectedItem();
//    		   
//    		   String rootPath = cell.getTreeView().getRoot().getValue().getPath().toString();
//    		   System.out.println("target: " + getRootDirectory(sourceDir) + "     cell.getTreeItem(): " + rootPath);
//    		   if (getRootDirectory(sourceDir).toString().equalsIgnoreCase(rootPath) && c != null) {	  
//    			   isMove = true;
////    			   boolean isRemoved = c.getParent().getChildren().remove(c);
//    		   } else {
//    			   isMove = false;
//    		   }
    		   
    		   
				Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {

                    /*
                     * Copy the directories.
                     */
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir,
                            BasicFileAttributes attrs)
                            throws IOException {
//                    	System.out.println(1);

                        if (isCancelled()) {
                        
                            // Task's isCancelled() method returns true
                            // when its cancel() is executed; in this app
                            // when the Cancel copy button is clicked.
                            // Here, the files copy is terminated.
                            return FileVisitResult.TERMINATE;
                        }
                        
                        
//                        try {
//							Thread.sleep(2000);
//						} catch (InterruptedException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
//	                        System.out.println(2 + " -> " + filteredFiles.size());
//	                        
//	                        for (SourceTarget path : filteredFiles) {
//								System.out.println(path.getFile() + " == " + dir);
//							}
                        
//	                        if (! filteredFiles.contains(dir)) {
//	                
//	                            return FileVisitResult.SKIP_SUBTREE;
//	                        }
//                        System.out.println(3);
                        Path target = targetDir.resolve(sourceDir.relativize(dir));
                        
                        // TODO - Text anpassen
                        if (isMove) {
							updateTitle("Move: " + dir.toString());
						} else {
							updateTitle("Copy: " + dir.toString());
						}
                        
						if (target.toFile().exists()) {
							System.err.println("    exists -> " + target);
						  } else {
//                            	  Files.copy(dir, target);
							  System.out.println("Copy or Move -> Source: " + dir + "  ->  Target: " + target + "  -> treeItem: " + treeItem.getValue().getPath());
							  copyOrMove(dir, target, isMove);
						  }

//                            copiedDirsCount++;
						
						int percent = (int)((++currentCounter * 100.0f) / inputSelectedFiles.size());		                            
						double progress = percent / 100.0;
						updateProgress(progress, 1.0); 		
//                            updateProgress(currentCounter, dirsCount+filesCount); 		                            
						updateMessage(percent + "%");
//                        System.out.println(4);
                        return FileVisitResult.CONTINUE;
                    }
            
                    /*
                     * Copy the files.
                     */
                    @Override
                    public FileVisitResult visitFile(Path file,
                            BasicFileAttributes attrs)
                            throws IOException {
                            
                        if (isCancelled()) {
                        
                            // Task's isCancelled() method
                            // terminates the files copy.
                            return FileVisitResult.TERMINATE;
                        }

                        if (file.toFile().getName().equalsIgnoreCase(CTree.lockFileName)) {
                        	return FileVisitResult.CONTINUE;
						}
                        
                        
                        if (filteredFiles.contains(file)) {
                            // TODO - Text anpassen
                            if (isMove) {
    							updateTitle("Move: " + file.toString());
    						} else {
    							updateTitle("Copy: " + file.toString());
    						}
                        	
                        	Path fileTarget = targetDir.resolve(sourceDir.relativize(file));
                        	
                        	if (fileTarget.toFile().exists()) {
								existFile(file, fileTarget);
							} else {
//								Files.copy(file, fileTarget, StandardCopyOption.REPLACE_EXISTING);
								copyOrMove(file, fileTarget, isMove);
							}
                       		                           
//                            copiedFilesCount++;
                            
                            int percent = (int)((++currentCounter * 100.0f) / inputSelectedFiles.size());		                            
                            double progress = percent / 100.0;
                            
                            updateProgress(progress, 1.0); 		

                            
//                            updateProgress(++currentCounter, dirsCount+filesCount);
//                            int percent = (int)((currentCounter * 100.0f) / inputSelectedFiles.size());
                            updateMessage(percent + "%");		
	                            
                        }

                        return FileVisitResult.CONTINUE;
                    }

                    
                    private void copyOrMove(Path fileSource, Path fileTarget, boolean isMove) {
            	    	try {
            	    		System.out.println("Copy or Move 1");
            				Files.copy(fileSource, fileTarget, StandardCopyOption.REPLACE_EXISTING); 
            				System.out.println("Copy or Move 2");
            				if (fileTarget.toFile().exists()) {
            					System.out.println("Copy or Move 3");
            					if (isMove) {
            						System.out.println("Copy or Move 4");
            						File lockFile = getLockFilePath(fileSource);
            				        List<String> list = new ArrayList<String>();
            				        list.add(lockFile.getAbsolutePath());
//            				        ObservableList<String> obList = FXCollections.observableList(list);
            				        System.out.println("Copy or Move 5");
//            				        saveSourceItems.clear();
            				        System.out.println("Copy or Move 6");
            				        saveLockFiles = FXCollections.observableList(list);

            				        System.out.println("move Delete treeItem: " + treeItem.getValue().getPath());
//            				        System.out.println("move Delete cell: " + cell.getTreeItem().getValue().getPath());
            				        
//            				        DeleteItemTask deleteItemTask = new DeleteItemTask(cTree, cell, treeItem, obList, false);
            				        
            				        
//            				        deleteItemTask.setOnSucceeded(e -> {
//	            						if (!fileSource.toFile().exists()) {
//	        								removeMovedItems();	
//										} else {
//											
//											
//											while (fileSource.toFile().exists()) {
//		            							try {
//													Thread.sleep(500);
//												} catch (InterruptedException ee) {
//													// TODO Auto-generated catch block
//													ee.printStackTrace();
//												}
//
//		            							new Thread(deleteItemTask).start();
//		 
//												if (--DeleteVersuche == 0) {
//													break;
//												}
//		
//											}
//										}
//	            						
//	            						
//            						});
            				         
//            						new Thread(deleteItemTask).start();

            						

//            						boolean isDeleted = Files.deleteIfExists(fileSource);
            				        
//            				        cTree.getLockFileHandler().printList();
            				        
            				              
//            						boolean isDeleted = false;
//            						
//            						int tries = 10;
//            						
//            						while (!isDeleted) {
//            							try {
//											Thread.sleep(500);
//										} catch (InterruptedException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}
//            							
//            							cTree.getLockFileHandler().printList();
//            							
//            							isDeleted = fileSource.toFile().delete();
// 
//										if (--tries == 0) {
//											break;
//										}
//
//									}
//            						
//            						System.err.println("Delete: " + fileSource + " -> " + isDeleted);
//            						if (isDeleted) {
//            							removeMovedItems();	
//									}
            					}		    		
            		    	}
            			} catch (IOException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			}                	  
                  }
                    
					private void existFile(Path file, Path fileTarget) {
						if (isSameForAll) {
					  		  if (replaceYes) {
					  			copyOrMove(file, fileTarget, isMove);
					  		  }        		  
					  	  }  else {		 		
					  		  	new StageFileIsExist(cTree.getPrimaryStage(), dragNDropInternal.getDragNDropInternal(), fileTarget);
					  		
								synchronized(lock){
						            try {
						                lock.wait();
						            } catch (InterruptedException e1) {
						                e1.printStackTrace();
						            }
								}
							
							    if (replaceYes) {
						    		System.out.println("Source: " + file + "  ->  target: " + fileTarget);
						    		copyOrMove(file, fileTarget, isMove);
							    }		
					  	  }
						
					}
                });
				
				
//            	TreeItem<PathItem> c = (TreeItem<PathItem>) cell.getTreeView().getSelectionModel().getSelectedItem();
            	
            	
            	// create the new Item
//            	CreateTree createTree = cTree.getCreateTree();
            	System.out.println("targetDir: " + targetDir);
            	createTree.updatePathListFormItem(targetDir);
            	createTree.startCreateTree(cell.getTreeView().getRoot(), false, false);
            	
            	
//            	System.out.println("cell.getTreeItem(): " + cell.getTreeItem().getValue().getPath() + "  ->  " + cell.getTreeItem().getChildren().get(0).getChildren().size());
//            	TreeItem<PathItem> mainCell = null;
//            	System.out.println("   98: " + cell.getTreeItem().getValue().getPath());
//            	if (cell.getTreeItem().getParent() == null) {
//            		System.out.println("   null");
//            		mainCell = cell.getTreeItem();
//				} else {
//					System.out.println("   nicht null");
//					mainCell = cell.getTreeItem().getParent();
//				}
//            	createTree.startCreateTree(mainCell);
            	
            	
            	// is DragNDrop internal, remove this Item
            	
//            	String rootPath = cell.getTreeView().getRoot().getValue().getPath().toString();
//            	System.out.println("target: " + getRootDirectory(sourceDir) + "     cell.getTreeItem(): " + rootPath);
//            	if (getRootDirectory(sourceDir).toString().equalsIgnoreCase(rootPath) && c != null) {	                                
//                    boolean isRemoved = c.getParent().getChildren().remove(c);
//				}
				
				
				
			}
            
            
            
            

            
//            if (zipCheckBox.isSelected()) {
//
//                if (copiedFilesCount > 0) {
//            
////                    logger.info("Creating ZIP file, wait... ");
//                    Thread.sleep(100);
//                    String zipFile = ZipFileCreater.zip(targetDir);
////                    logger.info("ZIP file created: " + zipFile);
//                }
//                else {
////                    logger.info("Cannot create ZIP file with files count = 0");
//                }
//            }



		return null;
	}

//	private void removeMovedItems() {
//		if (isMove) {
//			
//
//				System.out.println("remove: " +  treeItem.getValue().getPath() + " -> Row: " + treeItem.getValue().getRow());
//				selItems.get(0).getParent().getChildren().remove(treeItem);
//
////			for (TreeItem<PathItem> item : selItems) {
////				System.out.println("remove: " +  item.getValue().getPath() + " -> Row: " + item.getValue().getRow());
////				selItems.get(0).getParent().getChildren().remove(item);
////			}
//		}
//	}
	
	   private void sortItems(PathTreeCell cell, CopyDialogProgress pForm) {
           SortWinExplorerTask taskSort = new SortWinExplorerTask(cTree, pForm, cell.getTreeView().getRoot(), targetDir.toString());
           
           
//           taskSort.setOnSucceeded(evt -> {
//        	   cTree.getScrollingByDragNDrop().stopScrolling();
//               cTree.getTree().refresh();
//               
////               System.err.println("=== get Index " +  cell.getTreeItem().getValue().getRow());
//               	                           
//               // Select new Item
//               try {
//					Thread.sleep(500);
//				} catch (InterruptedException ex) {
//					// TODO Auto-generated catch block
//					ex.printStackTrace();
//				}
//               selectItemSearchInTreeView(cTree.getTree(), cell.getTreeItem(), targetDir.toString());
//               
//               System.out.println("   Ende Sort");
//               
//               pForm.getDialogStage().close();
////               doTaskEventCloseRoutine(copyTask);
//           });
           
           sleep(50);

           bindUIandService(pForm.getDialogStage(), taskSort);
           new Thread(taskSort).start();

	   }
	   
	   
//	   private void sleep(int milliSec) {
//           try {
//				Thread.sleep(milliSec);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//	   }
	   
	   // Getter
	   public boolean isSameForAll() {return isSameForAll;}
	   public boolean isReplaceYes() {return replaceYes;}
	   
	   // Setter
	   public void setReplaceYes(boolean replaceYes) {this.replaceYes = replaceYes;}
	   public void setSameForAll(boolean isSameForAll) {this.isSameForAll = isSameForAll;}
	   public void setMove(boolean isMove) {this.isMove = isMove;}
}
