package app.controller;

import java.io.File;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import app.StartWacherDemo;
import app.TreeViewWatchService.FileMonitor;
import app.TreeViewWatchService.ModelFileChanges;
import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.TreeViewWatchService.WindowsExplorerComparator;
import app.interfaces.ISaveExpandedItems;
import app.interfaces.ISuffix;
import app.interfaces.ISystemIcon;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class CTree implements Initializable, ISuffix, ISystemIcon, ISaveExpandedItems {

	String mainDirectory = "H:\\test";
	
	private StartWacherDemo startWacherDemo;
	private Stage primaryStage;
	
//    private ExecutorService service;
	private PathTreeCell cell;
	private Path rootPath;
	public static TreeItem<PathItem> treeItem;

	private static ObservableList<TreeItem<PathItem>> listFiles = FXCollections.observableArrayList();

	private ObservableList<ModelFileChanges> listSaveChanges = FXCollections.observableArrayList();
	public static HashMap<String, Image> suffixIcon = new HashMap<>();
	
	private StringProperty messageProp = new SimpleStringProperty();
	
	@FXML private VBox vBoxMessage;
	
//	@FXML private ListView<File> listViewChanges;
	@FXML private TableView<ModelFileChanges> tableView;
	@FXML private TableColumn<ModelFileChanges, String> columnAction;
	@FXML private TableColumn<ModelFileChanges, String> columnFile;
	@FXML private TableColumn<ModelFileChanges, String> columnTime;
	
	@FXML private Button buttonNow;
//	@FXML private Button buttonLater;
	
	@FXML private TextField textFieldRootDirectory;
	@FXML private Button buttonChooser;
	@FXML private Button buttonLoad;
	@FXML private TreeView<PathItem> tree;

	public CTree() {

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
//		service = Executors.newFixedThreadPool(1);
//		textFieldRootDirectory.setText("H:\\Test");
		textFieldRootDirectory.setText(mainDirectory);
		
		setButtonAction();
		setPropUpdateMessage();
	}

	private void setPropUpdateMessage() {
		vBoxMessage.setVisible(false);
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
	
	public void loadTree() {
		listSaveChanges.clear();
		addAllExpandedItems(tree.getRoot());
//		loadTree(primaryStage, "H:\\Test");
		loadTree(primaryStage, mainDirectory);
	}
	
	private void loadTree(Stage primaryStage, String rootDirectory) {
		
        rootPath = Paths.get(rootDirectory);
        PathItem pathItem = new PathItem(rootPath);
        treeItem = new TreeItem<PathItem>(pathItem);
        treeItem.setExpanded(false);

        createTree(treeItem, false);
        
        tree.setRoot(treeItem);
        tree.getRoot().setExpanded(true);    
        
        tree.getRoot().expandedProperty().addListener((o, oldVal, newVal) -> {
        	System.out.println(newVal + " - " + o.getValue());
        });
        
		tree.setFixedCellSize(35);
        tree.setCellFactory((TreeView<PathItem> p) -> {
            cell = new PathTreeCell(this, primaryStage);           
            return cell;
        });
        
//        FileMonitor fileMonitor = new FileMonitor(this, cell, "H:\\Test", 1000);
        FileMonitor fileMonitor = new FileMonitor(this, cell, mainDirectory, 1000);
        fileMonitor.startFileMonitor();    

	}
	
    public static void createTree(TreeItem<PathItem> oldItem, boolean expand) {
    	
    	if (oldItem.getValue().getPath().toFile().isDirectory()) {

			try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(oldItem.getValue().getPath())){
	
	            for (Path path : directoryStream) {
	                TreeItem<PathItem> newItem = new TreeItem<PathItem>( new PathItem( path));
  
	                // is File Hidden, not add in Tree
	                if (!newItem.getValue().getPath().toFile().isHidden()) {
	                	
						oldItem.getChildren().add(newItem);
		                sortMyList(oldItem.getChildren());
		                if (Files.isDirectory(path)) {
		                    createTree(newItem, expand);
		                } else {
		                	File file = newItem.getValue().getPath().toFile();
							addSuffixAndImage(file.getName(), ISystemIcon.getSystemImageView(file));
						}
					}
	                
	            }
	            directoryStream.close();
	            
	            // Expand saved items
				for (Entry<Path, TreeItem<PathItem>> item : saveExpandedItems.entrySet()) {	
					if (item.getKey().toString().equals(oldItem.getValue().getPath().toString())) {
						System.err.println("rootItem expand: " + oldItem);
						oldItem.setExpanded(true);
					} 
				}							
	        }
			
	        // catch exceptions, e. g. java.nio.file.AccessDeniedException: c:\System Volume Information, c:\$RECYCLE.BIN
	        catch( Exception ex) {
	            ex.printStackTrace();
	        }
		}  
//    	System.out.println("createTree Ende");
    }
    
	private static void addSuffixAndImage(String name, Image image) {
		suffixIcon.put(ISuffix.getSuffix(name), image);
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
    
    // Getter    
    public TreeView<PathItem> getTree() {return tree;}
	public PathTreeCell getCell() {return cell;}
	public Stage getPrimaryStage() {return primaryStage;}
	public VBox getvBoxMessage() {return vBoxMessage;}
	public Button getButtonNow() {return buttonNow;}	
	public ObservableList<ModelFileChanges> getListSaveChanges() {return listSaveChanges;}
	public static HashMap<String, Image> getSuffixIcon() {return suffixIcon;}
	public TableView<ModelFileChanges> getTableView() {return tableView;}
	public TableColumn<ModelFileChanges, String> getColumnAction() {return columnAction;}
	public TableColumn<ModelFileChanges, String> getColumnFile() {return columnFile;}
	public TableColumn<ModelFileChanges, String> getColumnTime() {return columnTime;}

	public void set(StartWacherDemo startWacherDemo, Stage primaryStage) {
		this.startWacherDemo = startWacherDemo;
		this.primaryStage = primaryStage;		
		
		loadTree();
	}


	
}
