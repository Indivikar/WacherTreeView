package app.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.StartWacherDemo;
import app.TreeViewWatchService.DragNDropInternal;
import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.TreeViewWatchService.WatchTask3;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class CTree implements Initializable{

	private StartWacherDemo startWacherDemo;
	private Stage primaryStage;
	
    private ExecutorService service;
	private PathTreeCell cell;
	private Path rootPath;
	public static TreeItem<PathItem> treeItem;

	
	private StringProperty messageProp = new SimpleStringProperty();
	
	@FXML private TextField textFieldRootDirectory;
	@FXML private Button buttonChooser;
	@FXML private Button buttonLoad;
	@FXML private TreeView<PathItem> tree;
	
	public CTree() {

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		service = Executors.newFixedThreadPool(3);
		textFieldRootDirectory.setText("D:\\Test");
		
		setButtonAction();
		
		
	}

	
	private void setButtonAction() {
		buttonChooser.setOnAction(event -> {
			 DirectoryChooser directoryChooser = new DirectoryChooser();
             File selectedDirectory = 
                     directoryChooser.showDialog(new Stage());
              
             if(selectedDirectory == null){
            	 textFieldRootDirectory.setText("No Directory selected");
             }else{
            	 textFieldRootDirectory.setText(selectedDirectory.getAbsolutePath());
             }
		});
		
		buttonLoad.setOnAction(event -> {
			loadTree(primaryStage);
		});

	}
	
	private void loadTree(Stage primaryStage) {
		
        rootPath = Paths.get(textFieldRootDirectory.getText());
        PathItem pathItem = new PathItem(rootPath);
        treeItem = new TreeItem<PathItem>(pathItem);
        treeItem.setExpanded(false);

        // create tree structure
        try {
			createTree(treeItem, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        tree.setRoot(treeItem);
		
        tree.setCellFactory((TreeView<PathItem> p) -> {
            cell = new PathTreeCell();
            new DragNDropInternal(primaryStage, service, cell);
//            setDragDropEvent(stage, cell);
            return cell;
        });
                  
        try {
        	boolean recursive = true;
        	WatchTask3 watchTask = new WatchTask3(rootPath, recursive, cell, tree);       	
        	service.submit(watchTask);
        	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
    public static void createTree(TreeItem<PathItem> rootItem, boolean expand) throws IOException {

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rootItem.getValue().getPath())) {

            for (Path path : directoryStream) {

                TreeItem<PathItem> newItem = new TreeItem<PathItem>( new PathItem( path));
//                newItem.setExpanded(expand);

                rootItem.getChildren().add(newItem);

                if (Files.isDirectory(path)) {
                    createTree(newItem, expand);
                }
            }
        }
        // catch exceptions, e. g. java.nio.file.AccessDeniedException: c:\System Volume Information, c:\$RECYCLE.BIN
        catch( Exception ex) {
            ex.printStackTrace();
        }
    }
	
       
	public ExecutorService getService() {return service;}

	public void set(StartWacherDemo startWacherDemo, Stage primaryStage) {
		this.startWacherDemo = startWacherDemo;
		this.primaryStage = primaryStage;		
		
		loadTree(primaryStage);
	}
	
}
