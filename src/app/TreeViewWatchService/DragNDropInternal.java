package app.TreeViewWatchService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

import app.models.ExistFiles;
import app.models.ReplaceOrIgnore;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DragNDropInternal {

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
                event.consume();
            }
        });
	}
	
	private void setDragOver(PathTreeCell cell) {
        cell.setOnDragOver(event -> {
            TreeItem<PathItem> item = cell.getTreeItem();
            if (item != null &&
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
            if (item != null &&
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
//	                if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
//	                    Platform.runLater(() -> {
//	                    	System.out.println(1);
////	                    	getAllExistFiles(source.toFile(), target.toFile());
////	                    	alertExist();	                    	
//	                    	FileAlterationListenerImpl.isInternalChange = true;
//	                        BooleanProperty replaceProp = new SimpleBooleanProperty();
////	                        CopyModalDialog dialog = new CopyModalDialog(stage, replaceProp);
//	                        
//	                        
////	                        replaceProp.addListener((ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) -> {
////	                            if (newValue) {
//	                            	System.out.println(2);
//	                                FileCopyTask task = new FileCopyTask(source, target);
////	                                bindUIandService(stage, task);
//	                                service.submit(task);
////	                            }
////	                        });
//	                    });
//	                } else {
	                	System.out.println(3);
	                	FileAlterationListenerImpl.isInternalChange = true;
	                    FileCopyTask task = new FileCopyTask(source, target);
//	                    bindUIandService(stage, task);
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
	
	
	private void getExist() {
		for(Path entry: stream){
			  if(entry.toFile().exists()){
			    log.info("working on file " + entry.getFileName());
			  }
			}

	}
	
	private void alertExist() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Dialog");
		alert.setHeaderText("Files exist");
		alert.setContentText(null);


		VBox expContent = new VBox();

		Label label = new Label("The exception stacktrace was:");

		expContent.getChildren().addAll(label, new TableViewExistFiles());

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();

	}
	
	
    private void getAllExistFiles(File source, File target) {

        File[] filesList = source.listFiles();
        for(File f : filesList){
            if(f.isDirectory())
            	if (searchFile(f, target)) {
            		long fileSizeInMB  = f.length() / 1024 / 1024;
					listExistFiles.add(new ExistFiles(f.getName(), ReplaceOrIgnore.Ignore.getCode(), f.getAbsolutePath(), fileSizeInMB + ""));
				}
            	getAllExistFiles(f, target);
            if(f.isFile()){
                System.out.println(f.getName());
            }
        }
    }
	
    private boolean searchFile(File file, File dirTarget) {

        File[] filesList = dirTarget.listFiles();
        for(File f : filesList){
            if(f.isDirectory())
            	if (f.exists()) {
            		return true;
				}
            	searchFile(dirTarget, file);
            if(f.isFile()){
                System.out.println(f.getName());
            }
        }        
        return false;     
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
