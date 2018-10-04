package app.TreeViewWatchService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import app.interfaces.ISaveExpandedItems;
import app.models.ExistFiles;
import app.models.ReplaceOrIgnore;
import app.test.dragNdropMitAnimation.TaskNode;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

public class DragNDropInternal implements ISaveExpandedItems{

	private boolean isMove;
	private TreeCell<PathItem> dropZone;
	private TreeItem<TaskNode> draggedItem;
	private static final String DROP_HINT_STYLE = "-fx-border-color: #eea82f; -fx-border-width: 0 0 2 0; -fx-padding: 3 3 1 3";
	private ObservableList<ExistFiles> listExistFiles = FXCollections.observableArrayList();
	
	
	public DragNDropInternal(Stage stage, ExecutorService service, final PathTreeCell cell) {
		
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
            TreeItem<PathItem> item = cell.getTreeItem();
            if (item != null && event.getGestureSource() != cell && event.getDragboard().hasFiles()) {
                Path targetPath = cell.getTreeItem().getValue().getPath();                
                PathTreeCell sourceCell = (PathTreeCell) event.getGestureSource();

                if (sourceCell == null) {
					// external Drag N Drop
					event.acceptTransferModes(TransferMode.COPY);          
					isMove = false;
//					System.out.println("external");
				} else {
					// internal Drag N Drop
					final Path sourceParentPath = sourceCell.getTreeItem().getValue().getPath().getParent();	                
	                if (sourceParentPath.compareTo(targetPath) != 0) {
	                    event.acceptTransferModes(TransferMode.COPY);
	                    isMove = true;
//	                    System.out.println("internal");
	                }
				}
            }
            event.consume();
        });
	}
	
	private void setDragEntered(PathTreeCell cell) {
        cell.setOnDragEntered(event -> {
            TreeItem<PathItem> item = cell.getTreeItem();
            if (item != null &&
                    event.getGestureSource() != cell &&
                    event.getDragboard().hasFiles()) {
                Path targetPath = cell.getTreeItem().getValue().getPath();
                PathTreeCell sourceCell = (PathTreeCell) event.getGestureSource();               
            }
            event.consume();
        });
	}
	
	private void setDragExited(PathTreeCell cell) {
        cell.setOnDragExited(event -> {

        });
	}
	
	private void setDragDropped(Stage stage, ExecutorService service, PathTreeCell cell) {
		 cell.setOnDragDropped(event -> {
			 
			 	addAllExpandedItems(cell.getTreeView().getRoot());
	            Dragboard db = event.getDragboard();
	            boolean success = false;	            
	            if (db.hasFiles()) {
	                final Path source = db.getFiles().get(0).toPath();
	                final Path target = Paths.get(cell.getTreeItem().getValue().getPath().toAbsolutePath().toString(), source.getFileName().toString());

	                	System.out.println(3);
	                	FileAlterationListenerImpl.isInternalChange = true;
	                    FileCopyTask task = new FileCopyTask(source, target, isMove);
	                    bindUIandService(stage, task);
//	                    new Thread(task).start();
	                    service.submit(task);
	                    task.setOnSucceeded(value -> {
	                        Platform.runLater(() -> {
//	                            TreeItem<PathItem> item = PathTreeItem.createNode(new PathItem(target));
//	                            cell.getTreeItem().getChildren().add(item);
	                        });
	                    });
	                }
	                success = true;
//	            }
	            event.setDropCompleted(success);
	            event.consume();
	        });
	}
	
	private void setDragDone(PathTreeCell cell) {
        cell.setOnDragDone(event -> {
            
        });
	}
	
    private void bindUIandService(Stage stage, Task task) {
        stage.getScene()
                .getRoot()
                .cursorProperty()
                .bind(
                        Bindings
                            .when(task.runningProperty())
                                .then(Cursor.WAIT)
                                .otherwise(Cursor.DEFAULT)
                );
    }
	
}
