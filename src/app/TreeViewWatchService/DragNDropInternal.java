package app.TreeViewWatchService;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import app.controller.CTree;
import app.dialog.CopyDialogProgress;
import app.interfaces.ICursor;
import app.interfaces.ISaveExpandedItems;
import app.interfaces.ITreeItemMethods;
import app.models.ExistFiles;
import app.models.ReplaceOrIgnore;
import app.models.SourceTarget;
import app.test.CopyFilesApp.FileFilterApplication;
import app.test.CopyFilesApp.FileFilters;
import app.test.CopyFilesApp.ZipFileCreater;
import app.test.ProgressDialogExample.ProgressForm;
import app.test.dragNdropMitAnimation.TaskNode;
import app.threads.CopyOrMoveTask;
import app.threads.SortWinExplorerTask;
import app.view.Stages.StageFileIsExist;
import app.view.Stages.StageMoveOrCopy;
import app.view.alerts.DefaultAlert;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DragNDropInternal implements ISaveExpandedItems, ITreeItemMethods, ICursor{

	private CTree cTree;
	private ScrollingByDragNDrop scrollingByDragNDrop;
	
//	private boolean isMove;
//	private boolean isSameForAll = false;
	private boolean replaceYes = false;
//	private Object lock = new Object();
	
	private TreeCell<PathItem> dropZone;
	private TreeItem<TaskNode> draggedItem;
	private static final String DROP_HINT_STYLE = "-fx-border-color: #eea82f; -fx-border-width: 0 0 2 0; -fx-padding: 3 3 1 3";
	private ObservableList<ExistFiles> listExistFiles = FXCollections.observableArrayList();
	private int copiedFilesCount;
	private int copiedDirsCount;
	private Task<Void> copyTask;
	private FileFilters fileFilters;
	private Set<Path> filesCounter = new HashSet<Path>();
	private Path sourceDir = null;
	private Path targetDir = null;
	private CopyOrMoveTask copyOrMoveTask;
	
	
	public DragNDropInternal(Stage stage, ExecutorService service, final PathTreeCell cell) {
		this.cTree = cell.getcTree();
		this.scrollingByDragNDrop = cTree.getScrollingByDragNDrop();
//		scrollingByDragNDrop = new ScrollingByDragNDrop(cTree.getTree());
		
		cTree.getTree().addEventFilter(MouseEvent.MOUSE_ENTERED, eventStopScrolling);
		cTree.getPrimaryStage().getScene().addEventFilter(MouseEvent.MOUSE_EXITED_TARGET, eventStopScrolling);
				
		setDragDetected(cell);
		setDragOver(cell);
		setDragEntered(cell);
		setDragExited(cell);
		setDragDropped(stage, service, cell);
		setDragDone(cell);
	}

//	public void next(){
//        synchronized(lock){
//            lock.notify();
//        }   
//    }
	
	
	private void setDragDetected(PathTreeCell cell) {
        cell.setOnDragDetected(event -> {
//        	System.out.println("setDragDetected");
        	ObservableList<TreeItem<PathItem>> selItems = cell.getTreeView().getSelectionModel().getSelectedItems();

            TreeItem<PathItem> item = cell.getTreeItem();
//            if (item != null && item.isLeaf()) {
            
            if (selItems != null) {
                Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();

                List<File> allFiles = new ArrayList<>();
                
                for (TreeItem<PathItem> treeItem : selItems) {
                	List<File> files = Arrays.asList(treeItem.getValue().getPath().toFile());
                	allFiles.addAll(files);
				}
                
                content.putFiles(allFiles);
                db.setContent(content);
                db.setDragView(cell.snapshot(null, null));
                event.consume();
            }
            
//            if (item != null) {
//                Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
//                ClipboardContent content = new ClipboardContent();
////                System.out.println(cell.getTreeItem().getValue().getPath().toFile());
//                List<File> files = Arrays.asList(cell.getTreeItem().getValue().getPath().toFile());
//                content.putFiles(files);
//                db.setContent(content);
//                db.setDragView(cell.snapshot(null, null));
//                event.consume();
//            }
        });
	}
	
	private void setDragOver(PathTreeCell cell) {
        cell.setOnDragOver(event -> {
        	Dragboard db = event.getDragboard();
//        	System.out.println("setDragOver");
            TreeItem<PathItem> item = cell.getTreeItem();
            if (item != null && event.getGestureSource() != cell && event.getDragboard().hasFiles()) {
                Path targetPath = cell.getTreeItem().getValue().getPath();                
                PathTreeCell sourceCell = (PathTreeCell) event.getGestureSource();
                Path source = db.getFiles().get(0).toPath();
//                System.out.println(source);
                if (sourceCell == null) {
					// external Drag N Drop
					event.acceptTransferModes(TransferMode.COPY);          
//					isMove = false;
//					System.out.println("external");
//					System.out.println(cell.getTreeItem().getValue().getPath());
				} else {
					// internal Drag N Drop
//					System.out.println("internal");
					Path sourceParentPath = sourceCell.getTreeItem().getValue().getPath();
//					System.out.println(targetPath.toString() + " == " +  sourceParentPath.toString());
					if (!targetPath.equals(source.getParent()) && !targetPath.equals(source)) {
	//	                if (sourceParentPath.compareTo(targetPath) != 0) {
		                    event.acceptTransferModes(TransferMode.COPY);
//		                    isMove = true;
	//	                    System.out.println(sourceCell.getTreeItem().getValue().getPath());
	//	                    System.out.println("internal");
	//	                }
					}

				}
                

                
            }
            event.consume();
        });
	}
	

	
	private void setDragEntered(PathTreeCell cell) {
        cell.setOnDragEntered(event -> {
//        	System.out.println("setDragEntered");
        	scrollingByDragNDrop.stopScrolling();
            TreeItem<PathItem> item = cell.getTreeItem();
            if (item != null &&
                    event.getGestureSource() != cell &&
                    event.getDragboard().hasFiles()) {
                Path targetPath = cell.getTreeItem().getValue().getPath();
                PathTreeCell sourceCell = (PathTreeCell) event.getGestureSource();   
                System.out.println(sourceCell);
            }
            event.consume();
        });
	}
	
	private void setDragExited(PathTreeCell cell) {
        cell.setOnDragExited(event -> {
//        	scrollingByDragNDrop.startScrolling(event);
        });
	}
	
	private void setDragDropped(Stage stage, ExecutorService service, PathTreeCell cell) {
		 cell.setOnDragDropped(event -> {
			 scrollingByDragNDrop.stopScrolling();

//			 System.out.println(2);
//			 	addAllExpandedItems(cell.getTreeView().getRoot());
	        Dragboard db = event.getDragboard();
	            
	        // is DragNDrop internal, remove this Item
	        boolean isInternal = isDragNDropInternal(db, cell);

         	

	            
	            boolean success = false;	            
	            if (db.hasFiles()) {
	            	filesCounter.clear();
	            	Set<SourceTarget> selectedFiles = new HashSet<SourceTarget>();
	            	
	            	copyOrMoveTask = new CopyOrMoveTask(cTree, this, cell, filesCounter, selectedFiles, sourceDir, targetDir);
	            	
	             	if (isInternal) {	             		
	             		new StageMoveOrCopy(cTree, this);
//	    				openContextMenu(); 
	    			}
	            		            	
	            	for (File existFiles : db.getFiles()) {
						System.out.println("db.getFiles(): " + existFiles);
//						selectedFiles.add(existFiles.toPath());
		                final Path source = existFiles.toPath();
		                final Path target = Paths.get(cell.getTreeItem().getValue().getPath().toAbsolutePath().toString(), source.getFileName().toString());

		                System.out.println("source: " + source);
		                System.out.println("target: " + target);
						
		                selectedFiles.add(new SourceTarget(source, target));
		                
						addAllPaths(existFiles.toPath(), target);
					}
	            	
	            	
	            	
//	                final Path source = db.getFiles().get(0).toPath();
//	                final Path target = Paths.get(cell.getTreeItem().getValue().getPath().toAbsolutePath().toString(), source.getFileName().toString());
//
//	                System.out.println("source: " + source);
//	                System.out.println("target: " + target);
//	                
	            	
	            	
	                copyRoutine(cell, filesCounter, selectedFiles);
	                
//	                	System.out.println(3);
//	                	FileAlterationListenerImpl.isInternalChange = true;
//	                    FileCopyTask task = new FileCopyTask(source, target, isMove);
//	                    bindUIandService(stage, task);
////	                    new Thread(task).start();
//	                    service.submit(task);
//	                copyTask.setOnSucceeded(value -> {
//	                        Platform.runLater(() -> {
////	                            TreeItem<PathItem> item = PathTreeItem.createNode(new PathItem(target));
////	                            cell.getTreeItem().getChildren().add(item);
//
//	                        	TreeItem<PathItem> c = (TreeItem<PathItem>) cell.getTreeView().getSelectionModel().getSelectedItem();
//	                        	
//	                        	// create the new Item
//	                        	CreateTree createTree = cTree.getCreateTree();
//	                        	createTree.updatePathListFormItem(target);
////	                        	System.out.println("cell.getTreeItem(): " + cell.getTreeItem().getValue().getPath() + "  ->  " + cell.getTreeItem().getChildren().get(0).getChildren().size());
//	                        	TreeItem<PathItem> mainCell = cell.getTreeItem().getParent();
//	                        	if (mainCell == null) {
//	                        		mainCell = cell.getTreeItem();
//								}
//	                        	
////	                        	createTree.startCreateTree(mainCell);
//	                        	createTree.startCreateTree(cell.getTreeView().getRoot(), false, false);
//	                        	
//	                        	// is DragNDrop internal, remove this Item
//	                        	String rootPath = cell.getTreeView().getRoot().getValue().getPath().toString();
//	                        	System.out.println("target: " + getRootDirectory(source) + "     cell.getTreeItem(): " + rootPath);
//	                        	if (getRootDirectory(source).toString().equalsIgnoreCase(rootPath) && c != null) {	                                
//	                                boolean isRemoved = c.getParent().getChildren().remove(c);
//								}
//	                        	
////	                        	System.err.println(source.toString() + " == " + rootPath);
//	                        	
//	                        	// sort Items
//	                            SortWinExplorerTask taskSort = new SortWinExplorerTask(cell.getTreeItem());
//	                            new Thread(taskSort).start();
//	                            
//	                            scrollingByDragNDrop.stopScrolling();
//	                            cTree.getTree().refresh();
//	                            
//	                            System.err.println("=== get Index " +  cell.getTreeItem().getValue().getRow());
//	                            	                           
//	                            // Select new Item
//	                            try {
//									Thread.sleep(500);
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//	                            selectItemSearchInTreeView(cTree.getTree(), cell.getTreeItem(), target.toString());
//	                        });
//	                    });
	                }
	                success = true;
//	            }
	            event.setDropCompleted(success);
	            event.consume();
	        });
	}
	
	
	private boolean isDragNDropInternal(Dragboard db, PathTreeCell cell) {	
		final Path sourcePath = db.getFiles().get(0).toPath();
		TreeItem<PathItem> c = (TreeItem<PathItem>) cell.getTreeView().getSelectionModel().getSelectedItem();
     	String rootPath = cell.getTreeView().getRoot().getValue().getPath().toString();
     	System.out.println("target: " + getRootDirectory(sourcePath) + "     cell.getTreeItem(): " + rootPath);
     	if (getRootDirectory(sourcePath).toString().equalsIgnoreCase(rootPath) && c != null) {	                                
     		return true;
		}
		
		return false;		
	}
	
//	private void openContextMenu() {
//		Point p = MouseInfo.getPointerInfo().getLocation();
//		
//		Stage stage = new Stage();
//		
//		AnchorPane root = new AnchorPane();
//		
//		stage.initModality(Modality.WINDOW_MODAL);
//		stage.initStyle(StageStyle.UTILITY);
//		stage.resizableProperty().setValue(Boolean.FALSE);
////		stage.initStyle(StageStyle.UNDECORATED);
//		stage.initOwner(cTree.getPrimaryStage());
//		
//		Scene scene = new Scene(root, 100, 80);
//		
//		VBox vBox = new VBox();
//		AnchorPane.setTopAnchor(vBox, 10.0);
//		AnchorPane.setLeftAnchor(vBox, 10.0);
//		AnchorPane.setRightAnchor(vBox, 10.0);
//		AnchorPane.setBottomAnchor(vBox, 10.0);
//		vBox.setAlignment(Pos.CENTER);
//		vBox.setSpacing(10);
//		
//		Button buttonCopy = new Button("Copy");
//		buttonCopy.setOnAction(e -> {
//			stage.close();
//		});
//
//		Button buttonMove = new Button("Move");
//		buttonCopy.setOnAction(e -> {
//			
//			stage.close();
//		});
//		
//		stage.setOnCloseRequest(e -> {
//			System.out.println("Close");
//		});
//		
//		// Fügen den Button zu unserem StackPane (Fenster) hinzu
//		vBox.getChildren().addAll(buttonCopy, buttonMove);
//		root.getChildren().addAll(vBox);
//
//		// nun Setzen wir die Scene zu unserem Stage und zeigen ihn an
//		stage.setScene(scene);
//		stage.setX(p.getX());
//		stage.setY(p.getY() + 30.0);
//		stage.showAndWait();
//	}

	private void addAllPaths(Path sourceDir, Path target) {
		try {
			Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					filesCounter.add(dir);
					return FileVisitResult.CONTINUE;
					
				}
				
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					filesCounter.add(file);
					return FileVisitResult.CONTINUE;
				}
				
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	   private void copyRoutine(PathTreeCell cell, Set<Path> inputSelectedFiles, Set<SourceTarget> selectedFiles) {
		   CreateTree.wantUpdateTree = false;
		   System.out.println("copyRoutine");

	        copiedFilesCount = 0;
	        copiedDirsCount = 0;

	        new Thread(copyOrMoveTask).start();

	    }
	
//	   private void sortItems(PathTreeCell cell, CopyDialogProgress pForm) {
//           SortWinExplorerTask taskSort = new SortWinExplorerTask(cTree, cell.getTreeView().getRoot());
//           
//           
//           taskSort.setOnSucceeded(evt -> {
//               scrollingByDragNDrop.stopScrolling();
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
//               doTaskEventCloseRoutine(copyTask);
//           });
//           
//           
//           try {
//				Thread.sleep(50);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//           bindUIandService(pForm.getDialogStage(), taskSort);
//           new Thread(taskSort).start();
//
//	   }
	   

//	   private void walker(SourceTarget item, int dirsCount, int filesCount, int currentCounter) throws IOException {
//		   
//		   Path sourceDir = item.getSource();
//		   Path targetDir = item.getTarget();
//		   
//		   Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
//
//               /*
//                * Copy the directories.
//                */
//               @Override
//               public FileVisitResult preVisitDirectory(Path dir,
//                       BasicFileAttributes attrs)
//                       throws IOException {
//               	System.out.println(1);
////                   if (isCancelled()) {
////                   
////                       // Task's isCancelled() method returns true
////                       // when its cancel() is executed; in this app
////                       // when the Cancel copy button is clicked.
////                       // Here, the files copy is terminated.
////                       return FileVisitResult.TERMINATE;
////                   }
////                   System.out.println(2 + " -> " + filteredFiles.size());
////                   
////                   for (SourceTarget path : filteredFiles) {
////						System.out.println(path.getFile() + " == " + dir);
////					}
//                   
////                   if (! filteredFiles.contains(dir)) {
////           
////                       return FileVisitResult.SKIP_SUBTREE;
////                   }
//                   System.out.println(3);
//                   Path target = targetDir.resolve(sourceDir.relativize(dir));
//
//                   try {
//                       Files.copy(dir, target);
//                       copiedDirsCount++;
//                       // Updates the Progess bar using the Task's
//                       // updateProgress(workDone, max) method.
//                       updateProgress(++currentCounter, dirsCount+filesCount);
//                   }
//                   catch (FileAlreadyExistsException e) {
//           
//                       if (! Files.isDirectory(target)) {
//               
//                           throw e;
//                       }
//                   }
//                   System.out.println(4);
//                   return FileVisitResult.CONTINUE;
//               }
//       
//               /*
//                * Copy the files.
//                */
//               @Override
//               public FileVisitResult visitFile(Path file,
//                       BasicFileAttributes attrs)
//                       throws IOException {
//                       
////                   if (isCancelled()) {
////                   
////                       // Task's isCancelled() method
////                       // terminates the files copy.
////                       return FileVisitResult.TERMINATE;
////                   }
//
//                   if (filteredFiles.contains(file)) {
//           
//                       Files.copy(file,
//                           targetDir.resolve(sourceDir.relativize(file)),
//                           StandardCopyOption.REPLACE_EXISTING);
//                       copiedFilesCount++;
//                       // Updates the Progess bar using the Task's
//                       // updateProgress(workDone, max) method.
//                       updateProgress(++currentCounter, dirsCount+filesCount);
//                   }
//
//                   return FileVisitResult.CONTINUE;
//               }
//           });
//
//	   }
//	   
//	    private Set<Path> applyFileFilters(Set<Path> selectedFiles, Path sourceDir)
//	            throws IOException {
//	    
//	        if (fileFilters == null) {
//
//	            fileFilters = FileFilters.getDefault();
////	            logger.info("File filters: " + fileFilters.toString());
//	        }
//	        
//	        return new FileFilterApplication().apply(sourceDir, 
//	                                                    selectedFiles,
//	                                                    fileFilters);
//	    }
	   
	   
//	    private void doTaskEventCloseRoutine(Task copyTask) {
//	        
////	        logger.info("Status: " + copyTask.getState() + "\n");
////	        logger.info("Select a target directory, apply file filters and copy.");
//	        Platform.runLater(() -> {
////	            selectTargetBtn.setDisable(false);
////	            closeBtn.setDisable(false);
////	            cancelBtn.setDisable(true);
//	        });    
//	    }
	    
	    
	private void setDragDone(PathTreeCell cell) {
        cell.setOnDragDone(event -> {
            scrollingByDragNDrop.stopScrolling();
//            cell.indexProperty().addListener(e -> {
//            	System.out.println("--- Set Index " +  cell.getIndex());
//            	cell.selectCell();
//            });
        });
        
        
	}

	
//	 private void sleep(int ms) {
//			try {
//				Thread.sleep(ms);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	
	
	private Path getRootDirectory(Path path) {
		int pathNames = path.getNameCount();
		
		for (int i = 0; i < pathNames - 1; i++) {
			path = path.getParent();
		}
		
		return path;
	}

    EventHandler<MouseEvent> eventStopScrolling = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
//			System.out.println("Stop Scrolling");
			scrollingByDragNDrop.stopScrolling();
		}
    };
	
    
    
	@Override
	public void addAllExpandedItems() {
		// TODO Auto-generated method stub
		
	}
	
//	public boolean isSameForAll() {return isSameForAll;}
//	public boolean isReplaceYes() {return replaceYes;}
	
	public DragNDropInternal getDragNDropInternal() {return this;};
	public ScrollingByDragNDrop getScrollingByDragNDrop() {return scrollingByDragNDrop;}
	public CopyOrMoveTask getCopyOrMoveTask() {return copyOrMoveTask;}

	//	public void setReplaceYes(boolean replaceYes) {this.replaceYes = replaceYes;}
//	public void setSameForAll(boolean isSameForAll) {this.isSameForAll = isSameForAll;}
//	public void setMove(boolean isMove) {this.isMove = isMove;}  
	
	
}
