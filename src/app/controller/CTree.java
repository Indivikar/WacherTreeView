package app.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import app.StartWacherDemo;
import app.TreeViewWatchService.FileMonitor;
import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.TreeViewWatchService.WatchTask3;
import app.TreeViewWatchService.WindowsExplorerComparator;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	
//    private ExecutorService service;
	private PathTreeCell cell;
	private Path rootPath;
	public static TreeItem<PathItem> treeItem;

	private static ObservableList<TreeItem<PathItem>> listFiles = FXCollections.observableArrayList();
	
	private StringProperty messageProp = new SimpleStringProperty();
	
	@FXML private TextField textFieldRootDirectory;
	@FXML private Button buttonChooser;
	@FXML private Button buttonLoad;
	@FXML private TreeView<PathItem> tree;

	public CTree() {

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
//		service = Executors.newFixedThreadPool(1);
		textFieldRootDirectory.setText("H:\\Test");
		
		setButtonAction();
//		loadTree(primaryStage);
		
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
			loadTree(primaryStage, textFieldRootDirectory.getText());
		});

	}
	
	private void loadTree(Stage primaryStage) {
		loadTree(primaryStage, "H:\\Test");
	}
	
	private void loadTree(Stage primaryStage, String rootDirectory) {
		
        rootPath = Paths.get(rootDirectory);
        PathItem pathItem = new PathItem(rootPath);
        treeItem = new TreeItem<PathItem>(pathItem);
        treeItem.setExpanded(false);

        createTree(treeItem, false);
        
        tree.setRoot(treeItem);
        tree.getRoot().setExpanded(true);      
		tree.setFixedCellSize(35);
        tree.setCellFactory((TreeView<PathItem> p) -> {
            cell = new PathTreeCell();
            
//            new DragNDropInternal(primaryStage, service, cell);
//            setDragDropEvent(stage, cell);
            return cell;
        });
        
        FileMonitor fileMonitor = new FileMonitor(tree, "H:\\Test", 1000);
        fileMonitor.startFileMonitor();    
//        try {
//        	boolean recursive = true;
//        	WatchTask3 watchTask = new WatchTask3(rootPath, recursive, cell, tree);       	
//        	service.submit(watchTask);
//        	
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}
	
    public static void createTree(TreeItem<PathItem> rootItem, boolean expand) {
    	
    	if (rootItem.getValue().getPath().toFile().isDirectory()) {
			try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rootItem.getValue().getPath())){
	
	            for (Path path : directoryStream) {
	                TreeItem<PathItem> newItem = new TreeItem<PathItem>( new PathItem( path));
	                newItem.setExpanded(true);
	                newItem.setExpanded(false);
	                newItem.setExpanded(expand);
	
	                rootItem.getChildren().add(newItem);
	                sortMyList(rootItem.getChildren());
	                if (Files.isDirectory(path)) {
	                    createTree(newItem, expand);
	                }
	            }
	            directoryStream.close();
	        }
	        // catch exceptions, e. g. java.nio.file.AccessDeniedException: c:\System Volume Information, c:\$RECYCLE.BIN
	        catch( Exception ex) {
	            ex.printStackTrace();
	        }
		}    	
    }
    
    
    private static void sortMyList(ObservableList<TreeItem<PathItem>> children) {
        Collections.sort(children, new Comparator<TreeItem<PathItem>>() {
            private final Comparator<PathItem> NATURAL_SORT = new WindowsExplorerComparator();

            @Override
            public int compare(TreeItem<PathItem> o1, TreeItem<PathItem> o2) {;
                return NATURAL_SORT.compare(o1.getValue(), o2.getValue());
            }
        });
    }
    

    
//	public ExecutorService getService() {return service;}

	public void set(StartWacherDemo startWacherDemo, Stage primaryStage) {
		this.startWacherDemo = startWacherDemo;
		this.primaryStage = primaryStage;		
		
		loadTree(primaryStage);
	}
	
}
