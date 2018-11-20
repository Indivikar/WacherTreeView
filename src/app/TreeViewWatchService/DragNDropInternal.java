package app.TreeViewWatchService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import app.controller.CTree;
import app.interfaces.ICursor;
import app.interfaces.ISaveExpandedItems;
import app.interfaces.ITreeItemMethods;
import app.models.ExistFiles;
import app.models.ReplaceOrIgnore;
import app.test.dragNdropMitAnimation.TaskNode;
import app.threads.SortWinExplorerTask;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DragNDropInternal implements ISaveExpandedItems, ITreeItemMethods, ICursor{

	private CTree cTree;
	private ScrollingByDragNDrop scrollingByDragNDrop;
	
	private boolean isMove;
	private TreeCell<PathItem> dropZone;
	private TreeItem<TaskNode> draggedItem;
	private static final String DROP_HINT_STYLE = "-fx-border-color: #eea82f; -fx-border-width: 0 0 2 0; -fx-padding: 3 3 1 3";
	private ObservableList<ExistFiles> listExistFiles = FXCollections.observableArrayList();
	
	
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

	private void setDragDetected(PathTreeCell cell) {
        cell.setOnDragDetected(event -> {
            TreeItem<PathItem> item = cell.getTreeItem();
//            if (item != null && item.isLeaf()) {
            if (item != null) {
                Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
//                System.out.println(cell.getTreeItem().getValue().getPath().toFile());
                List<File> files = Arrays.asList(cell.getTreeItem().getValue().getPath().toFile());
                content.putFiles(files);
                db.setContent(content);
                db.setDragView(cell.snapshot(null, null));
                event.consume();
            }
        });
	}
	
	private void setDragOver(PathTreeCell cell) {
        cell.setOnDragOver(event -> {
        	Dragboard db = event.getDragboard();
        	
            TreeItem<PathItem> item = cell.getTreeItem();
            if (item != null && event.getGestureSource() != cell && event.getDragboard().hasFiles()) {
                Path targetPath = cell.getTreeItem().getValue().getPath();                
                PathTreeCell sourceCell = (PathTreeCell) event.getGestureSource();
                Path source = db.getFiles().get(0).toPath();
//                System.out.println(source);
                if (sourceCell == null) {
					// external Drag N Drop
					event.acceptTransferModes(TransferMode.COPY);          
					isMove = false;
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
		                    isMove = true;
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
			 	
//			 List<CompletableFuture<Void>> serviceList = new ArrayList<>();
			 
			 System.out.println(2);
//			 	addAllExpandedItems(cell.getTreeView().getRoot());
	            Dragboard db = event.getDragboard();
	            boolean success = false;	            
	            if (db.hasFiles()) {
	            	
	            	
	            	
//	            	for (File sourceFile : db.getFiles()) {
//						System.out.println("db.getFiles(): " + sourceFile);
//						serviceList.add(dropItems(stage, service, sourceFile, cell));
//					}
	            	
	            	
//	            	CompletableFuture<Void> finished = CompletableFuture
//	            	        .allOf(serviceList.toArray(new CompletableFuture[serviceList.size()]));
	            	
//	                CompletableFuture<Void> finished = CompletableFuture.allOf(s);
//	                finished.thenRun(() -> {
//	                    Platform.runLater(() -> {
//	                    	System.err.println("setDragDropped Ende");
//	                    });
//	                });
//	            	
	            	
	            	System.err.println("setDragDropped Ende");
//	                final Path source = db.getFiles().get(0).toPath();
//	                final Path target = Paths.get(cell.getTreeItem().getValue().getPath().toAbsolutePath().toString(), source.getFileName().toString());
//
//	                
//	                	System.out.println(3);
//	                	FileAlterationListenerImpl.isInternalChange = true;
//	                    FileCopyTask task = new FileCopyTask(source, target, isMove);
//	                    bindUIandService(stage, task);
////	                    new Thread(task).start();
//	                    service.submit(task);
//	                    task.setOnSucceeded(value -> {
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
	
	
	
	private void setDragDone(PathTreeCell cell) {
        cell.setOnDragDone(event -> {
            scrollingByDragNDrop.stopScrolling();
            System.out.println("setDragDone(PathTreeCell cell)");
//            cell.indexProperty().addListener(e -> {
//            	System.out.println("--- Set Index " +  cell.getIndex());
//            	cell.selectCell();
//            });
        });
        
        
	}
	
	private CompletableFuture<Void> dropItems(Stage stage, ExecutorService service, File sourceFile , PathTreeCell cell) {
		
        final Path source = sourceFile.toPath();
        final Path target = Paths.get(cell.getTreeItem().getValue().getPath().toAbsolutePath().toString(), source.getFileName().toString());

        
        	System.out.println(3);
        	FileAlterationListenerImpl.isInternalChange = true;
            FileCopyTask task = new FileCopyTask(source, target, isMove);
            bindUIandService(stage, task);
//            new Thread(task).start();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> service.submit(task));
//            service.submit(task);
            
 
            task.setOnSucceeded(value -> {
                Platform.runLater(() -> {
//                    TreeItem<PathItem> item = PathTreeItem.createNode(new PathItem(target));
//                    cell.getTreeItem().getChildren().add(item);

                	TreeItem<PathItem> c = (TreeItem<PathItem>) cell.getTreeView().getSelectionModel().getSelectedItem();
                	
                	// create the new Item
                	CreateTree createTree = cTree.getCreateTree();
                	createTree.updatePathListFormItem(target);
//                	System.out.println("cell.getTreeItem(): " + cell.getTreeItem().getValue().getPath() + "  ->  " + cell.getTreeItem().getChildren().get(0).getChildren().size());
                	TreeItem<PathItem> mainCell = cell.getTreeItem().getParent();
                	if (mainCell == null) {
                		mainCell = cell.getTreeItem();
					}
                	
//                	createTree.startCreateTree(mainCell);
                	createTree.startCreateTree(cell.getTreeView().getRoot(), false, false);
                	
                	// is DragNDrop internal, remove this Item
                	String rootPath = cell.getTreeView().getRoot().getValue().getPath().toString();
                	System.out.println("target: " + getRootDirectory(source) + "     cell.getTreeItem(): " + rootPath);
                	if (getRootDirectory(source).toString().equalsIgnoreCase(rootPath) && c != null) {	                                
                        boolean isRemoved = c.getParent().getChildren().remove(c);
					}
                	
//                	System.err.println(source.toString() + " == " + rootPath);
                	
                	// sort Items
                    SortWinExplorerTask taskSort = new SortWinExplorerTask(cell.getTreeItem());
                    new Thread(taskSort).start();
                    
                    scrollingByDragNDrop.stopScrolling();
                    cTree.getTree().refresh();
                    
                    System.err.println("=== get Index " +  cell.getTreeItem().getValue().getRow());
                    	                           
                    // Select new Item
                    try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    selectItemSearchInTreeView(cTree.getTree(), cell.getTreeItem(), target.toString());
                });
            });
			return future;
	}
	
	
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
	
}
