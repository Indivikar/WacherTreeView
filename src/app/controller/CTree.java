package app.controller;

import java.io.File;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Stack;

import app.StartWacherDemo;
import app.TreeViewWatchService.CreateTree;
import app.TreeViewWatchService.FileMonitor;
import app.TreeViewWatchService.ModelFileChanges;
import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.TreeViewWatchService.ScrollingByDragNDrop;
import app.db.PathList;
import app.interfaces.ICursor;
import app.interfaces.ISaveExpandedItems;
import app.interfaces.ISuffix;
import app.interfaces.ISystemIcon;
import app.loadTime.LoadTime.LoadTimeOperation;
import app.sort.WindowsExplorerComparator;
import app.threads.AddTreeItems;
import app.threads.LoadDBService;
import app.threads.SortWinExplorerTask;
import app.watcher.watchService.PAWatcher;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

public class CTree implements Initializable, ISuffix, ISystemIcon, ISaveExpandedItems, ICursor {

	public static ObservableList<LoadTimeOperation> listLoadTime = FXCollections.observableArrayList();

//	String mainDirectory = "Y:\\test";
	private static String mainDirectory = "D:\\test";
	private static String DirectoryNameDB = "___DB___";
	private static String fileNameDB = "test.txt";
	private String DirectoryDB = mainDirectory + File.separator + DirectoryNameDB;
	private static String pathFileDB = mainDirectory + File.separator + DirectoryNameDB + File.separator + fileNameDB;
	
	private StartWacherDemo startWacherDemo;
	private Stage primaryStage;
	
	// Services
	private static LoadDBService loadDBService;
	
	
	// Klassen
//	private static PathList pathList;
	private ScrollingByDragNDrop scrollingByDragNDrop;
	private CreateTree createTree;
	private final Comparator<PathItem> NATURAL_SORT = new WindowsExplorerComparator();
	
//    private ExecutorService service;
	private PathTreeCell cell;
	private Path rootPath;
	public static TreeItem<PathItem> treeItem;

	private static ObservableList<TreeItem<PathItem>> listFiles = FXCollections.observableArrayList();
	private static ObservableList<Path> pathsPA = FXCollections.observableArrayList();
	
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
			refreshTree();
		});

		buttonSortList.setOnAction(event -> {
	        SortWinExplorerTask task = new SortWinExplorerTask(tree.getRoot());
	        new Thread(task).start();
		});
		
	}
	
	public void refreshTree() {
			this.createTree.updatePathListFormDB(treeItem, true, true);
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
        PathItem pathItem = new PathItem(rootPath);
        treeItem = new TreeItem<PathItem>(pathItem);
        treeItem.setExpanded(false);

//        createTree(treeItem, false);
        
//        AddTreeItems threadAddItems = new AddTreeItems(pathFileDB, this);
//        new Thread(threadAddItems).start();
        createTree = new CreateTree(pathFileDB, this);
        createTree.updatePathListFormDB(treeItem, false, false);
//        createTree.startCreateTree(treeItem, false, false);
//        createTree2(treeItem, tree);
        tree.setRoot(treeItem);
        
        
        
//        long start1 = new Date().getTime();
//        sortTreeItems(tree.getRoot());
//        sortTree(tree.getRoot());
        SortWinExplorerTask task = new SortWinExplorerTask(tree.getRoot());
        new Thread(task).start();
//		long runningTime1 = new Date().getTime() - start1;			
//		CTree.listLoadTime.add(new LoadTimeOperation("sortTreeItems()", runningTime1 + "", pathFileDB));
        
        tree.getRoot().setExpanded(true);    
        tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        tree.getRoot().expandedProperty().addListener((o, oldVal, newVal) -> {
        	System.out.println(newVal + " - " + o.getValue());
        });
        
        
        
		tree.setFixedCellSize(35);
        tree.setCellFactory((TreeView<PathItem> p) -> {
            cell = new PathTreeCell(this, primaryStage);           
            return cell;
        });
        
        // Apache Watcher
////        FileMonitor fileMonitor = new FileMonitor(this, cell, "H:\\Test", 1000);
//        FileMonitor fileMonitor = new FileMonitor(this, cell, DirectoryDB, 1000);
//        fileMonitor.startFileMonitor();    

        // java.nio.file.WatchService
        PAWatcher paWatcher = new PAWatcher(this);
        
        
	}
	
//	private void sortTree(TreeItem<PathItem> root) {
//		  if (!root.isLeaf()) {
//		    FXCollections.sort(root.getChildren(), new Comparator<TreeItem<PathItem>>() {
//	            
//
//	            @Override
//	            public int compare(TreeItem<PathItem> o1, TreeItem<PathItem> o2) {;
//	                return NATURAL_SORT.compare(o1.getValue(), o2.getValue());
//	            }
//	        });
//		    root.getChildren().forEach(this::sortTree);
//		  }
//		}
	
	
//	 public static void createTree2(TreeItem<PathItem> oldItem1, TreeView<PathItem> tree) {
//		 	 
//		 for (Path path : pathList.loadDB()) {
//			 if (!path.toString().equals("")) {
//				 List<String> liste = getListParents(path);
//				 if (!liste.isEmpty()) {
//					isChild(oldItem1, liste);
//				 }
//			}
//		 }
//	 }
	
//	 private static boolean isChild(TreeItem<PathItem> mainItem, List<String> liste) {	
//			 if (liste.size() == 0) {
//				return true;			
//			 } 
//
//		 	if (mainItem.getChildren().isEmpty()) {
//				TreeItem<PathItem> newItem = null;
//				if (liste.size() != 0) {
//					String newPath = liste.get(0);
//				 	newItem = new TreeItem<PathItem>(new PathItem(new File(newPath).toPath()));					 	
////				 	System.out.println("add Empty: " + newPath + " ->  in: " + mainItem.getValue().getPath());				 
//					mainItem.getChildren().add(newItem);
//					liste.remove(newPath);					
//				 } 
//
//				 isChild(newItem, liste);								
//			} else {
//
//				Pair<Boolean, TreeItem<PathItem>> isChildExists = isChildExists(mainItem, liste);
//				
//				if (!isChildExists.getKey()) {
//					String newPath = liste.get(0);			
//					TreeItem<PathItem> newItem = isChildExists.getValue();					
////					System.out.println("add Child: " + newPath + " ->  in: " + mainItem.getValue().getPath());								 
//					mainItem.getChildren().add(newItem);
//					liste.remove(newPath);
//				
//					isChild(newItem, liste);
//					if(liste.isEmpty()) {
//						return true;
//					} 						
//				} else {					
//					TreeItem<PathItem> mainItem1 = isChildExists.getValue();
//					isChild(mainItem1, liste);
//				}
//				
//			 if (liste.size() != 0) {
//				 for (TreeItem<PathItem> child : mainItem.getChildren()) {
//						String childPath = child.getValue().getPath().toString();
//						String newPath = liste.get(0);
//						TreeItem<PathItem> newItem = new TreeItem<PathItem>(new PathItem(new File(newPath).toPath()));
//							if(childPath.equalsIgnoreCase(newPath.toString())) {
//								liste.remove(newPath);
//								
//								isChild(child, liste);
//								
//								if(liste.isEmpty()) {
//									return true;
//								} 
//							} else {
//								System.out.println(4);
		
//								continue;
//								if (!newPath.toString().equals(mainItem.getValue().getPath().toString())) {									 					
//									System.out.println("add Child: " + newPath + " ->  in: " + mainItem.getValue().getPath());								 
//									mainItem.getChildren().add(newItem);
//									liste.remove(newPath);
//								
//									isChild(newItem, path, liste);
//									if(liste.isEmpty()) {
//										return true;
//									} 	
//								}
//							}
//								 if (newItem.getChildren().isEmpty()) {
//								 		TreeItem<PathItem> newItem1 = null;
////									 	TreeItem<PathItem> newItem = new TreeItem<PathItem>(new PathItem(new File("D:\\test\\ordner").toPath()));
//											 if (liste.size() != 0) {
//												Path newPath1 = liste.pop();
//											 	System.out.println("newPath: " + newPath);
//											 	newItem1 = new TreeItem<PathItem>(new PathItem(newPath));	
//											 	
//											 	System.out.println("add: " + newPath1 + " ->  in: " + mainItem.getValue().getPath());
//											 
//												mainItem.getChildren().add(newItem1);
//												
//											 } 
//											 
//
//											 isChild(newItem, path, liste);	
//								 }
								 
//								 if (liste.size() != 0) {
//									liste.pop();
//								 }
								 
//								 continue;
//							}
//					}
//			 } 
			
//			return true;
//	}
	 

//	 private static Pair<Boolean, TreeItem<PathItem>> isChildExists(TreeItem<PathItem> mainItem, List<String> liste) {
//		 TreeItem<PathItem> newItem = null;
//		 if (liste.size() != 0) {
//			 for (TreeItem<PathItem> child : mainItem.getChildren()) {
//					String childPath = child.getValue().getPath().toString();
//					String newPath = liste.get(0);
//					newItem = new TreeItem<PathItem>(new PathItem(new File(newPath).toPath()));
//						if(childPath.equalsIgnoreCase(newPath.toString())) {
//							liste.remove(newPath);
//							
////							isChild(child, liste);
//							
//							if(liste.isEmpty()) {
//								return new Pair<Boolean, TreeItem<PathItem>>(false, mainItem);	
//							} 
//							return new Pair<Boolean, TreeItem<PathItem>>(true, child);	
//							
//						} else {	
//							continue;
//						}
//			 }
//		 }
//		return new Pair<Boolean, TreeItem<PathItem>>(false, newItem);		
//	}
	 
	 
//	 private void addNewChild() {
//		 if (!newPath.toString().equals(oldItem1.getValue().getPath().toString())) {
//				 
//			 System.out.println("add: " + newPath + " ->  in: " + oldItem1.getValue().getPath());
//				oldItem1.getChildren().add(newItem);
//			 }
//			 
//			 stackPaths.pop();
//
//	}
	 
//	 private static List<String> getListParents(Path path) {
//		 List<String> list = new ArrayList<>(); 		 
//		 int nameCount = path.getNameCount();		 
//		 Path par = path;		 
//		 list.add(par.toString());
//		 for (int i = 0; i < nameCount; i++) {
//			
//			par = par.getParent();
//			if (par.toString().equalsIgnoreCase("D:\\") || par.toString().equalsIgnoreCase("D:\\test")) {
//				continue;
//			}
//			list.add(par.toString());
//		 }
//		 
//		 sortStringListByLength(list);
//		 return list;
//	}
	 
//	 private static void sortStringListByLength(List<String> list) {
//        Collections.sort(list, Comparator.comparing(String::length));
//	 }

	 public static void createTree(TreeItem<PathItem> oldItem1, boolean expand) {
 			
			loadDBService.setOnSucceeded(e -> {
				pathsPA = loadDBService.getValue();
				for (Path path : pathsPA) {	      
	                TreeItem<PathItem> newItem = new TreeItem<PathItem>( new PathItem( path));
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
//	 		System.out.println("1 " + root + " == " + parent);
	 		if (root.equalsIgnoreCase(parent)) {
	 			
	 			rootItem.getChildren().add(newItem);
//            	File file = newItem.getValue().getPath().toFile();
//				addSuffixAndImage(file.getName(), ISystemIcon.getSystemImageView(file));
				return;
			}
	 		
	 		
		    if(rootItem != null && !rootItem.getChildren().isEmpty()){
		        for(TreeItem<PathItem> treeItem: rootItem.getChildren()){
		        	String main = treeItem.getValue().getPath().toString();
//		        	System.out.println("2 " + main + " == " + parent);
		        	if (main.equalsIgnoreCase(parent)) {
		        		treeItem.getChildren().add(newItem);
//	                	File file = newItem.getValue().getPath().toFile();
//						addSuffixAndImage(file.getName(), ISystemIcon.getSystemImageView(file));
		        		break;
					} else {
						handleSearch(treeItem, newItem);
					}
		            

		        }
		    }		    
		}
	 
//	 	private static void handleSearch(TreeItem<PathItem> rootItem, TreeItem<PathItem> newItem) {
//
//	 		String root = rootItem.getValue().getPath().toString();
//	 		String parent = newItem.getValue().getPath().getParent().toString();
////	 		System.out.println("1 " + root + " == " + parent);
//	 		if (root.equalsIgnoreCase(parent)) {
//	 			
//	 			rootItem.getChildren().add(newItem);
////            	File file = newItem.getValue().getPath().toFile();
////				addSuffixAndImage(file.getName(), ISystemIcon.getSystemImageView(file));
//				return;
//			}
//	 		
//	 		
//		    if(rootItem != null && !rootItem.getChildren().isEmpty()){
//		        for(TreeItem<PathItem> treeItem: rootItem.getChildren()){
//		        	String main = treeItem.getValue().getPath().toString();
////		        	System.out.println("2 " + main + " == " + parent);
//		        	if (main.equalsIgnoreCase(parent)) {
//		        		treeItem.getChildren().add(newItem);
////	                	File file = newItem.getValue().getPath().toFile();
////						addSuffixAndImage(file.getName(), ISystemIcon.getSystemImageView(file));
//		        		break;
//					} else {
//						handleSearch(treeItem, newItem);
//					}
//		            
//
//		        }
//		    }		    
//		}
	 
	 
	    private static TreeItem<PathItem> getExistsItem(TreeItem<PathItem> root, TreeItem<PathItem> newItem){
	        
	    	String newItemString = newItem.getValue().getPath().toString();
	    	
	        for(TreeItem<PathItem> subItem : root.getChildren()){
	        	if (subItem.getValue().getPath().toString().equalsIgnoreCase(newItemString)) {      		      		
	        		return subItem;
				}        
	        }
	        return null;
	    }
	 
	 
//    public static void createTree(TreeItem<PathItem> oldItem, boolean expand) {
//
////    	System.out.println("createTree Star: " + oldItem);
//    	
//    	if (oldItem.getValue().getPath().toFile().isDirectory()) {
//
//			try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(oldItem.getValue().getPath())){
//	
//	            for (Path path : directoryStream) {
//	                TreeItem<PathItem> newItem = new TreeItem<PathItem>( new PathItem( path));
//  
//	                // is File Hidden, add not in Tree
//	                if (!newItem.getValue().getPath().toFile().isHidden() && !isItemExists(oldItem, newItem)) {
//	                	
//						oldItem.getChildren().add(newItem);
//						System.out.println("createTree Star: " + oldItem.getValue().getPath() + " add -> " + newItem.getValue().getPath());
//		                sortMyList(oldItem.getChildren());
//		                if (Files.isDirectory(path)) {
//		                    createTree(newItem, expand);
//		                } else {
//		                	File file = newItem.getValue().getPath().toFile();
//							addSuffixAndImage(file.getName(), ISystemIcon.getSystemImageView(file));
//						}
//					}
//	                
//	            }
//	            directoryStream.close();
//	            
//	            
//	            
//	            // Expand saved items
//				for (Entry<Path, TreeItem<PathItem>> item : saveExpandedItems.entrySet()) {	
//					if (item.getKey().toString().equals(oldItem.getValue().getPath().toString())) {
//						System.err.println("rootItem expand: " + oldItem);
//						oldItem.setExpanded(true);
//					} 
//				}	
//	        }
//	        // catch exceptions, e. g. java.nio.file.AccessDeniedException: c:\System Volume Information, c:\$RECYCLE.BIN
//	        catch( Exception ex) {
//	            ex.printStackTrace();
//	        }
//		}  
//    	
////    	System.out.println("createTree Ende ");
//    }
    
    private static boolean isItemExists(TreeItem<PathItem> root, TreeItem<PathItem> newItem){
        
    	String newItemString = newItem.getValue().getPath().toString();
    	
        for(TreeItem<PathItem> subItem : root.getChildren()){
        	if (subItem.getValue().getPath().toString().equalsIgnoreCase(newItemString)) {      		      		
        		return true;
			}        
        }
        return false;
    }
    
	private static void addSuffixAndImage(String name, Image image) {
		suffixIcon.put(ISuffix.getSuffix(name), image);
	}
    
	
	
	public void sortTreeItems(TreeItem<PathItem> item) {

//		SortWinExplorerTask task = new SortWinExplorerTask(item.getChildren());
//		Thread thread = new Thread(task);
//		thread.start();
		
		sortMyListRecursive(item.getChildren());
		
		  for (TreeItem<PathItem> child : item.getChildren()) {
			  if (!child.isLeaf() && child.getValue().getPath().toFile().isDirectory()) {
				  sortTreeItems(child);
			  }
		  }

	}
	
    public static void sortMyListRecursive(ObservableList<TreeItem<PathItem>> children) {

        Collections.sort(children, new Comparator<TreeItem<PathItem>>() {
            private final Comparator<PathItem> NATURAL_SORT = new WindowsExplorerComparator();

            @Override
            public int compare(TreeItem<PathItem> o1, TreeItem<PathItem> o2) {;
                return NATURAL_SORT.compare(o1.getValue(), o2.getValue());
            }
        });
    }
	
//    public static void sortMyList(ObservableList<TreeItem<PathItem>> children) {
//        Collections.sort(children, new Comparator<TreeItem<PathItem>>() {
//            private final Comparator<PathItem> NATURAL_SORT = new WindowsExplorerComparator();
//
//            @Override
//            public int compare(TreeItem<PathItem> o1, TreeItem<PathItem> o2) {;
//                return NATURAL_SORT.compare(o1.getValue(), o2.getValue());
//            }
//        });
//    }
    
    // Getter    
    
    // Services
    public static LoadDBService getLoadDBService() {return loadDBService;}
    
    public String getMainDirectory() {return mainDirectory;}
	public String getDirectoryNameDB() {return DirectoryNameDB;}
	public String getFileNameDB() {return fileNameDB;}
	public String getDirectoryDB() {return DirectoryDB;}  
    public String getPathFileDB() {return pathFileDB;}

	public ScrollingByDragNDrop getScrollingByDragNDrop() {return scrollingByDragNDrop;}
	public CreateTree getCreateTree() {return createTree;}
    
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

	public void set(StartWacherDemo startWacherDemo, Stage primaryStage) {
		this.startWacherDemo = startWacherDemo;
		this.primaryStage = primaryStage;		
	
		loadDBService = new LoadDBService(this, pathFileDB);

		loadTree();
		
	}

	@Override
	public void addAllExpandedItems() {
		// TODO Auto-generated method stub
		
	}


	
}
