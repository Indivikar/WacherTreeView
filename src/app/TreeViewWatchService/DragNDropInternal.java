package app.TreeViewWatchService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

public class DragNDropInternal {

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
            if (item != null && item.isLeaf()) {
                Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                List<File> files = Arrays.asList(cell.getTreeItem().getValue().getPath().toFile());
                content.putFiles(files);
                db.setContent(content);
                event.consume();
            }
        });
	}
	
	private void setDragOver(PathTreeCell cell) {
        cell.setOnDragOver(event -> {
            TreeItem<PathItem> item = cell.getTreeItem();
            if ((item != null && !item.isLeaf()) &&
                    event.getGestureSource() != cell &&
                    event.getDragboard().hasFiles()) {
                Path targetPath = cell.getTreeItem().getValue().getPath();
                PathTreeCell sourceCell = (PathTreeCell) event.getGestureSource();
                final Path sourceParentPath = sourceCell.getTreeItem().getValue().getPath().getParent();
                if (sourceParentPath.compareTo(targetPath) != 0) {
                    event.acceptTransferModes(TransferMode.COPY);
                }
            }
            event.consume();
        });
	}
	
	private void setDragEntered(PathTreeCell cell) {
        cell.setOnDragEntered(event -> {
            TreeItem<PathItem> item = cell.getTreeItem();
            if ((item != null && !item.isLeaf()) &&
                    event.getGestureSource() != cell &&
                    event.getDragboard().hasFiles()) {
                Path targetPath = cell.getTreeItem().getValue().getPath();
                PathTreeCell sourceCell = (PathTreeCell) event.getGestureSource();
//                final Path sourceParentPath = sourceCell.getTreeItem().getValue().getPath().getParent();
//                if (sourceParentPath.compareTo(targetPath) != 0) {
//                    cell.setStyle("-fx-background-color: powderblue;");
//                }                
            }
            event.consume();
        });
	}
	
	private void setDragExited(PathTreeCell cell) {
        cell.setOnDragExited(event -> {
//            cell.setStyle("-fx-background-color: white");
//            event.consume();
        });
	}
	
	private void setDragDropped(Stage stage, ExecutorService service, PathTreeCell cell) {
		 cell.setOnDragDropped(event -> {
	            Dragboard db = event.getDragboard();
	            boolean success = false;
	            if (db.hasFiles()) {
	                final Path source = db.getFiles().get(0).toPath();
	                final Path target = Paths.get(cell.getTreeItem().getValue().getPath().toAbsolutePath().toString(), source.getFileName().toString());
	                if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
	                    Platform.runLater(() -> {
	                        BooleanProperty replaceProp = new SimpleBooleanProperty();
	                        CopyModalDialog dialog = new CopyModalDialog(stage, replaceProp);
	                        replaceProp.addListener((ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) -> {
	                            if (newValue) {
	                                FileCopyTask task = new FileCopyTask(source, target);
	                                service.submit(task);
	                            }
	                        });
	                    });
	                } else {
	                    FileCopyTask task = new FileCopyTask(source, target);
	                    service.submit(task);
	                    task.setOnSucceeded(value -> {
	                        Platform.runLater(() -> {
//	                            TreeItem<PathItem> item = PathTreeItem.createNode(new PathItem(target));
//	                            cell.getTreeItem().getChildren().add(item);
	                        });
	                    });
	                }
	                success = true;
	            }
	            event.setDropCompleted(success);
	            event.consume();
	        });
	}
	
	private void setDragDone(PathTreeCell cell) {
        cell.setOnDragDone(event -> {
            
        });

	}
	
}
