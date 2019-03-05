package app.controller;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Stack;

import app.functions.LockFileHandler;
import app.StartWacherDemo;
import app.TreeViewWatchService.CreateTree;
import app.TreeViewWatchService.FileMonitor;
import app.TreeViewWatchService.ModelFileChanges;
import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.TreeViewWatchService.ScrollingByDragNDrop;
import app.db.PathList;
import app.interfaces.ICursor;
import app.interfaces.ILockDir;
import app.interfaces.ISaveExpandedItems;
import app.interfaces.ISuffix;
import app.interfaces.ISystemIcon;
import app.interfaces.ITreeUpdateHandler;
import app.listeners.ChildrenChangedListener;
import app.loadTime.LoadTime.LoadTimeOperation;
import app.models.ItemsDB;
import app.sort.WindowsExplorerComparator;
import app.threads.LoadDBService;
import app.threads.NewDirectoryTask;
import app.threads.SortWinExplorerTask;
import app.threads.TreeLoaderTask;
import app.watcher.watchService.LockWatcher;
import app.watcher.watchService.PAWatcher;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

public class CTree implements Initializable, ISuffix, ISystemIcon, ISaveExpandedItems, ICursor, ILockDir, ITreeUpdateHandler {

	public static ObservableList<LoadTimeOperation> listLoadTime = FXCollections.observableArrayList();

//	String mainDirectory = "Y:\\test";
	public static String lockFileName = "folder.lock";
	public static String refreshFileName = ".startRefesh";
	private static String drive = "V:\\";
	private static String mainDirectory = drive + "test";
	private static String DirectoryNameDB = "___DB___";
	private static String fileNameDB = "test.txt";	
	private String DirectoryDB = mainDirectory + File.separator + DirectoryNameDB;
	private static String pathFileDB = mainDirectory + File.separator + DirectoryNameDB + File.separator + fileNameDB;
	
	// Stages
	private StartWacherDemo startWacherDemo;
	private Stage primaryStage;

	// Services
	private static LoadDBService loadDBService;
	
	
	// Klassen
//	private static PathList pathList;
	private LockFileHandler lockFileHandler = new LockFileHandler();
	private ScrollingByDragNDrop scrollingByDragNDrop;
	private CreateTree createTree;
	private final Comparator<PathItem> NATURAL_SORT = new WindowsExplorerComparator();
	private PathTreeCell cell;
	
	// Listeners
	private ChildrenChangedListener ChildrenChangedListener = new ChildrenChangedListener(this);
	
	public static boolean isInternalChange = false;
	
	private Path rootPath;
	public static TreeItem<PathItem> treeItem;

	private static ObservableList<TreeItem<PathItem>> listFiles = FXCollections.observableArrayList();
	private static ObservableList<ItemsDB> pathsPA = FXCollections.observableArrayList();
	
	private ObservableList<TreeItem<PathItem>> selectedItems = FXCollections.observableArrayList();
	
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
	@FXML private Button buttonClearTree;
	@FXML private Button buttonReloadTree;
	@FXML private Button buttonSortList;
	
	public CTree() {

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		System.out.println("CTree()");	
//		pathList = new PathList(pathFileDB);
		scrollingByDragNDrop = new ScrollingByDragNDrop(tree);
//		service = Executors.newFixedThreadPool(1);
//		textFieldRootDirectory.setText("H:\\Test");
		textFieldRootDirectory.setText(mainDirectory);

		setButtonAction();
		setPropUpdateMessage();
	}

	private void setPropUpdateMessage() {
		vBoxMessage.setVisible(false);
	}
	
	private static void loadPathsPA() {
		loadDBService.setOnSucceeded(e -> {
			pathsPA = loadDBService.getValue();
		});
		loadDBService.start();
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
		
		buttonClearTree.setOnAction(event -> {
			tree.getRoot().getChildren().clear();
		});
		
		buttonReloadTree.setOnAction(event -> {		
//			this.createTree.addAllExpandedItems();
//			tree.getRoot().getChildren().clear();
			refreshServerPathList(this);
//			refreshTree();
			
			
			
		});

		buttonSortList.setOnAction(event -> {
	        SortWinExplorerTask task = new SortWinExplorerTask(this, tree.getRoot());
	        new Thread(task).start();
		});
		
	}
	
	public void refreshTree() {
		System.out.println("----- refreshTree -----");	
//		cell.getCellContextMenu().serviceReloadBinding(true);
//		lockDir(this, getTree().getRoot());
		this.createTree.updatePathListFormDB(treeItem, true, true);
//		this.createTree.startCreateTree(treeItem, true, true);
	}

	public void refreshTree(boolean cursorWait) {
			System.out.println("----- refreshTree -----");
//			cell.getCellContextMenu().serviceReloadBinding(true);
//			lockDir(this, getTree().getRoot());
			this.createTree.updatePathListFormDB(treeItem, true, true, true);
//			this.createTree.startCreateTree(treeItem, true, true);
	}
	
	public void loadTree() {
		listSaveChanges.clear();
//		addAllExpandedItems(tree.getRoot());

		
		long start = new Date().getTime();
		loadTree(primaryStage, mainDirectory);
//		loadTree(primaryStage, mainDirectory);
		long runningTime = new Date().getTime() - start;
		
		listLoadTime.add(new LoadTimeOperation("loadTree()", runningTime + "", mainDirectory));
		System.out.println("loadTree() LadeZeit: " + runningTime);

	}
	
	private void loadTree(Stage primaryStage, String rootDirectory) {
		System.out.println("loadTree()");
        rootPath = Paths.get(rootDirectory);
        PathItem pathItem = new PathItem(rootPath, true);
        treeItem = new TreeItem<PathItem>(pathItem);
        treeItem.setExpanded(false);

//        createTree(treeItem, false);
        
//        AddTreeItems threadAddItems = new AddTreeItems(pathFileDB, this);
//        new Thread(threadAddItems).start();
        createTree = new CreateTree(pathFileDB, this);
        
        createTree.updatePathListFormDB(treeItem, false, false);
//        updateTree();

//        createTree.startCreateTree(treeItem, false, false);
//        createTree2(treeItem, tree);
        tree.setRoot(treeItem);
        
        
        
//        long start1 = new Date().getTime();
//        sortTreeItems(tree.getRoot());
//        sortTree(tree.getRoot());
//        SortWinExplorerTask task = new SortWinExplorerTask(this, tree.getRoot());
//        new Thread(task).start();
//		long runningTime1 = new Date().getTime() - start1;			
//		CTree.listLoadTime.add(new LoadTimeOperation("sortTreeItems()", runningTime1 + "", pathFileDB));
        
        tree.getRoot().setExpanded(true);    
        tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tree.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle); 
        tree.getRoot().expandedProperty().addListener((o, oldVal, newVal) -> {
        	System.out.println(newVal + " - " + o.getValue());
        });
        
        tree.getRoot().getChildren().addListener(ChildrenChangedListener.listener);
        
        
		tree.setFixedCellSize(35);
        tree.setCellFactory((TreeView<PathItem> p) -> {
            cell = new PathTreeCell(this, primaryStage);           
            return cell;
        });
        

        
        changeIconWhenFolderLocked(tree.getRoot());
        
//        SortWinExplorerTask task = new SortWinExplorerTask(this, tree.getRoot());
//        new Thread(task).start();
        
        // Apache Watcher
////        FileMonitor fileMonitor = new FileMonitor(this, cell, "H:\\Test", 1000);
//        FileMonitor fileMonitor = new FileMonitor(this, cell, DirectoryDB, 1000);
//        fileMonitor.startFileMonitor();    

        // java.nio.file.WatchService
        PAWatcher paWatcher = new PAWatcher(this);
        lockWacher(rootPath);

	}
	
	private void updateTree() {
		try {
	        TreeLoaderTask treeLoaderTask = new TreeLoaderTask(this, treeItem);
	  	  	bindUIandService(primaryStage, treeLoaderTask);
	  	  	new Thread(treeLoaderTask).start();        	
		} catch (Exception e) {
			// TODO: handle exception
			e.getStackTrace();
		}
	}
	
	EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> {
	    handleMouseClicked(event);
	};
	
	private void handleMouseClicked(MouseEvent event) {
//	    Node node = event.getPickResult().getIntersectedNode();
//	    System.out.println("click");
//	    // Accept clicks only on node cells, and not on empty spaces of the TreeView
//	    if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
//	        String name = tree.getSelectionModel().getSelectedItem().getValue().getPath().toString();
//	        System.out.println("Node click: " + name);
//	    }
	}
	
	
	private void lockWacher(Path path) {
		try {
			FileSystem fs = FileSystems.getDefault(); // sun.nio.fs.WindowsFileSystem			
	        WatchService watchService = fs.newWatchService(); // sun.nio.fs.WindowsWatchService
	        
			WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
				      StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
		
	        LockWatcher wh = new LockWatcher(this, watchService, path);
	        wh.start();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
	}


	 public static void createTree(TreeItem<PathItem> oldItem1, boolean expand) {
 			
			loadDBService.setOnSucceeded(e -> {
				pathsPA = loadDBService.getValue();
				for (ItemsDB item : pathsPA) {	      
	                TreeItem<PathItem> newItem = new TreeItem<PathItem>( new PathItem( item.getPath(), item.isDirectoryPath()));
	                handleSearch(oldItem1, newItem);
	            }
			});
			if (!loadDBService.isRunning()) {
				loadDBService.start();
			}
	 }
	
	 	private static void handleSearch(TreeItem<PathItem> rootItem, TreeItem<PathItem> newItem) {

	 		String root = rootItem.getValue().getPath().toString();
	 		String parent = newItem.getValue().getPath().getParent().toString();
	 		if (root.equalsIgnoreCase(parent)) {	 			
	 			rootItem.getChildren().add(newItem);
				return;
			}
	 		
	 		
		    if(rootItem != null && !rootItem.getChildren().isEmpty()){
		        for(TreeItem<PathItem> treeItem: rootItem.getChildren()){
		        	String main = treeItem.getValue().getPath().toString();
		        	if (main.equalsIgnoreCase(parent)) {
		        		treeItem.getChildren().add(newItem);

		        		break;
					} else {
						handleSearch(treeItem, newItem);
					}
		        }
		    }		    
		}

//	    private static TreeItem<PathItem> getExistsItem(TreeItem<PathItem> root, TreeItem<PathItem> newItem){
//	        
//	    	String newItemString = newItem.getValue().getPath().toString();
//	    	
//	        for(TreeItem<PathItem> subItem : root.getChildren()){
//	        	if (subItem.getValue().getPath().toString().equalsIgnoreCase(newItemString)) {      		      		
//	        		return subItem;
//				}        
//	        }
//	        return null;
//	    }

//    private static boolean isItemExists(TreeItem<PathItem> root, TreeItem<PathItem> newItem){
//        
//    	String newItemString = newItem.getValue().getPath().toString();
//    	
//        for(TreeItem<PathItem> subItem : root.getChildren()){
//        	if (subItem.getValue().getPath().toString().equalsIgnoreCase(newItemString)) {      		      		
//        		return true;
//			}        
//        }
//        return false;
//    }
    
//	private static void addSuffixAndImage(String name, Image image) {
//		suffixIcon.put(ISuffix.getSuffix(name), image);
//	}
    
	
	
//	public void sortTreeItems(TreeItem<PathItem> item) {
//
////		SortWinExplorerTask task = new SortWinExplorerTask(item.getChildren());
////		Thread thread = new Thread(task);
////		thread.start();
//		
//		sortMyListRecursive(item.getChildren());
//		
//		  for (TreeItem<PathItem> child : item.getChildren()) {
//			  if (!child.isLeaf() && child.getValue().isDirectoryItem()) {
//				  sortTreeItems(child);
//			  }
//		  }
//
//	}
//	
//    public static void sortMyListRecursive(ObservableList<TreeItem<PathItem>> children) {
//
//        Collections.sort(children, new Comparator<TreeItem<PathItem>>() {
//            private final Comparator<PathItem> NATURAL_SORT = new WindowsExplorerComparator();
//
//            @Override
//            public int compare(TreeItem<PathItem> o1, TreeItem<PathItem> o2) {;
//                return NATURAL_SORT.compare(o1.getValue(), o2.getValue());
//            }
//        });
//    }
	
    public void saveSelectedItems() {
    	ObservableList<TreeItem<PathItem>> selItems = tree.getSelectionModel().getSelectedItems();
    	selectedItems.clear();
    	selectedItems.addAll(selItems);
	}

    // Getter    
    
    // Services
    public static LoadDBService getLoadDBService() {return loadDBService;}
    
    
//    public static String getLockFileName() {return lockFileName;}
	public String getDrive() {return drive;}
    public String getMainDirectory() {return mainDirectory;}
	public String getDirectoryNameDB() {return DirectoryNameDB;}
	public String getFileNameDB() {return fileNameDB;}
	public String getDirectoryDB() {return DirectoryDB;}  
    public String getPathFileDB() {return pathFileDB;}

    public LockFileHandler getLockFileHandler() {return lockFileHandler;}
	public ScrollingByDragNDrop getScrollingByDragNDrop() {return scrollingByDragNDrop;}
	public CreateTree getCreateTree() {return createTree;}
	public ChildrenChangedListener getChildrenChangedListener() {return ChildrenChangedListener;}

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
	public Button getButtonReloadTree() {return buttonReloadTree;}
	
	public ObservableList<TreeItem<PathItem>> getSelectedItems() {return selectedItems;}

	// Setter
	public void setSelectedItems(ObservableList<TreeItem<PathItem>> selectedItems) {this.selectedItems = selectedItems;}

	public void set(StartWacherDemo startWacherDemo, Stage primaryStage) {
		this.startWacherDemo = startWacherDemo;
		this.primaryStage = primaryStage;		
	
		loadDBService = new LoadDBService(this, pathFileDB);

		loadTree();
		
//		try {
//		Platform.runLater(() -> {
//				bindUIandService(primaryStage, task);
//				new Thread(task).start();
//		});

//		} catch (Exception e) {
//			e.getStackTrace();
//		}
	}

	Task<Void> task = new Task<Void>() {
	    @Override public Void call() {
	    	Platform.runLater(() -> {
	    		loadTree();
	    	});
	        return null;
	    }
	};
	
	@Override
	public void addAllExpandedItems() {
		// TODO Auto-generated method stub
		
	}


	
}
