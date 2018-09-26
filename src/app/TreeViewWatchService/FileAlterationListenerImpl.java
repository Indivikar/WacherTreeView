package app.TreeViewWatchService;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import app.controller.CTree;
import app.interfaces.ISuffix;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author eddie
 * @source http://examples.javacodegeeks.com/core-java/apache/commons/io-commons/monitor/filealterationmonitor/org-apache-commons-io-monitor-filealterationmonitor-example/
 *
 */
public class FileAlterationListenerImpl implements FileAlterationListener, ISuffix {

	private CTree cTree;
	private TreeView<PathItem> tree;
	private HashMap<Path, TreeItem<PathItem>> listAllItems = new HashMap<>();
	
	private ExecutorService executorService;
	private ServiceRegister serviceRegister;
//	private ServiceWaitUpdate serviceWaitUpdate;	
	
	
	private Map<File, String> map = new HashMap<File, String>();
	 
    // Now add observability by wrapping it with ObservableList.
	private ObservableList<ModelFileChanges> data = FXCollections.observableArrayList();
	private ObservableList<ModelFileChanges> listSaveChanges = FXCollections.observableArrayList();
	
	public static boolean isInternalChange = false;
	private boolean nowShowUpdateMessage = false;

	private VBox vBoxMessage;
	
	private TableView<ModelFileChanges> tableView;
	private TableColumn<ModelFileChanges, String> columnAction;
	private TableColumn<ModelFileChanges, String> columnFile;
	private TableColumn<ModelFileChanges, String> columnTime;
	private Button buttonNow;
		
	public FileAlterationListenerImpl(CTree cTree) {
		this.cTree = cTree;
		this.tree = cTree.getTree();
		this.vBoxMessage = cTree.getvBoxMessage();
		this.buttonNow = cTree.getButtonNow();
		this.tableView = cTree.getTableView();
		this.columnAction = cTree.getColumnAction();
		this.columnFile = cTree.getColumnFile();
		this.columnTime = cTree.getColumnTime();

		executorService = Executors.newFixedThreadPool(1);
//		serviceWaitUpdate = new ServiceWaitUpdate(this);
		
		setButtonAction();
		addTableViewProperties();
//		serviceWaitUpdate.setOnSucceeded(e -> {
//			System.out.println("Wait Service Finish");
//			nowShowUpdateMessage = true;
//		});
				
	}

	private void addTableViewProperties() {
		columnAction.setCellValueFactory(new PropertyValueFactory<>("action"));
		columnFile.setCellValueFactory(new PropertyValueFactory<>("fileString"));
		columnTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        
		tableView.setItems(listSaveChanges);
	}
	
	
	private void setButtonAction() {
		buttonNow.setOnAction(e -> {
			actionChange();
		});		
	}
	
	@Override
	public void onStart(final FileAlterationObserver observer) {
//		System.out.println("The FileListener has started on " + observer.getDirectory().getAbsolutePath());
		data.clear();	
	}

	@Override
	public void onDirectoryCreate(final File directory) {
//		System.out.println(directory.getAbsolutePath() + " was created.");
		data.add(new ModelFileChanges("create", directory, getTimeStamp()));
		nowShowUpdateMessage = true;
	}

	@Override
	public void onDirectoryChange(final File directory) {
//		System.out.println(directory.getAbsolutePath() + " wa modified");
		data.add(new ModelFileChanges("change", directory, getTimeStamp()));
		nowShowUpdateMessage = true;
	}

	@Override
	public void onDirectoryDelete(final File directory) {		
		System.out.println(directory.getAbsolutePath() + " was deleted.");
		data.add(new ModelFileChanges("delete", directory, getTimeStamp()));
		nowShowUpdateMessage = true;
	}

	@Override
	public void onFileCreate(final File file) {
//		System.out.println(file.getAbsoluteFile() + " was created.");
		data.add(new ModelFileChanges("create", file, getTimeStamp()));
		nowShowUpdateMessage = true;
	}

	@Override
	public void onFileChange(final File file) {		
//		System.out.println(file.getAbsoluteFile() + " was modified.");
		data.add(new ModelFileChanges("change", file, getTimeStamp()));
		nowShowUpdateMessage = true;
	}

	@Override
	public void onFileDelete(final File file) {
//		System.out.println(file.getAbsoluteFile() + " was deleted.");
		data.add(new ModelFileChanges("delete", file, getTimeStamp()));
		nowShowUpdateMessage = true;
	}

	@Override
	public void onStop(final FileAlterationObserver observer) {
//		System.out.println("The FileListener has stopped on " + observer.getDirectory().getAbsolutePath());

		
		
		Platform.runLater(() -> {
			if (data.size() != 0 && nowShowUpdateMessage) {			
					
				
				getMainDirectory();
				nowShowUpdateMessage = false;
				
				
				
				if (isInternalChange) {
					actionChange();
				} else {
//					System.out.println("show List");
					vBoxMessage.setVisible(true);
				}
				
//				for (ModelFileChanges items : listSaveChanges) {
//					System.out.println("items in List: " + items.getFileString());
//				}
			} 								
		});		
	}

	private Timestamp getTimeStamp() {
		return new Timestamp(System.currentTimeMillis());
	}
	
	private void getMainDirectory() {
				
//		System.out.println("data.size(): " + data.size());
		
//			ObservableList<ModelFileChanges> filter = data.stream()		
//				.filter(p -> p.getFile().isDirectory())
//				.collect(Collectors.toCollection(FXCollections::observableArrayList)); 
//		
//			System.out.println("filter.size(): " + filter.size());
//			if (filter.size() <= 1) {
//				return;
//			}
			
			ModelFileChanges res = null;
			Optional<ModelFileChanges> resDirectory = data.stream()		
				.filter(p -> IsPathDirectory( p.getFileString()))	
				.min((p1, p2) -> Integer.compare(p1.getFileString().length(), p2.getFileString().length()));
			
			if (!data.isEmpty()) {
				if (resDirectory.isPresent()) {
					res = resDirectory.get();
					
				} else {
//					System.err.println("ergOptional not present");
					Optional<ModelFileChanges> resFiles = data.stream()		
							.filter(p -> !IsPathDirectory( p.getFileString()))
							.findFirst();	
					res = resFiles.get();
				}
			} else {
//				System.err.println("list data is empty");
				return;
			}


//			System.err.println("der kleinste: " + res.getFileString());
			removeItem(res);
			
			if (!existItem(res) && !existMainDirectoryFromItem(res)) {
				listSaveChanges.add(res);	
				sortList(listSaveChanges);				
			}
			data.remove(res);

			getMainDirectory();
	}
	
	
	private boolean existMainDirectoryFromItem(ModelFileChanges res) {

		for (ModelFileChanges item : listSaveChanges) {
			if (item.getAction().equalsIgnoreCase(res.getAction())) {
				for (String parents : getAllParents(res.getFile())) {
					if (parents.equalsIgnoreCase(item.getFileString())) {
						return true;
					}
				}	
			}			
		}
		
		return false;
	}

	private ObservableList<String> getAllParents(File file) {
		ObservableList<String> parents = FXCollections.observableArrayList();
		File parentFile = file;

		while (parentFile != null) {
			parents.add(parentFile.getAbsolutePath());
			parentFile = parentFile.getParentFile();
		}
		
		return parents;
	}
	
	
	private boolean IsPathDirectory(String path) {
	    File file = new File(path);

	    // check if the file/directory is already there
	    if (!file.exists()) {
	        // see if the file portion it doesn't have an extension
	        return file.getName().lastIndexOf('.') == -1;
	    } else {
	        // see if the path that's already in place is a file or directory
	        return true;
	    }
	}
	
	private boolean existItem(ModelFileChanges erg) {
		for (ModelFileChanges item : listSaveChanges) {
			if (item.equals(erg)) {
				return true;
			}
		}
		return false;		
	}
	
	private void removeItem(ModelFileChanges erg) {
		
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getFile().isDirectory() && data.get(i).getFileString().contains(erg.getFileString())) {				
				for (String parents : checkParents(data.get(i))) {
					if (erg.getFileString().equalsIgnoreCase(parents)) {
//						System.out.println("remove: " + data.get(i).getFile());
						data.remove(i);
					}
				}					
			}
		}
	}
	
	private ObservableList<String> checkParents(ModelFileChanges modelFileChanges) {
		ObservableList<String> list = FXCollections.observableArrayList();
		
		File file = modelFileChanges.getFile();
		
		while (true) {
			file = file.getParentFile();

			if (file == null) {
				break;
			} else {
				list.add(file.getAbsolutePath());
			}	
		}
		return list;
	}
	
	private void actionChange() {
		ActionChangeTask ActionChangeTask = new ActionChangeTask(this);
		bindUIandService(cTree.getPrimaryStage(), ActionChangeTask);
		executorService.submit(ActionChangeTask);
		new Thread(ActionChangeTask).start();
		
//		for (ModelFileChanges item : sortList(listSaveChanges)) {
//			System.out.println("in for");
////			if (IsPathDirectory( item.getFileString())) {
//			if (Files.isDirectory(item.getFile().toPath())) {
//				System.out.println("its a Directory: " + item.getFileString());
//				if (item.getAction().equalsIgnoreCase("create") && item.getFile().exists()) {
//					registerAll(item.getFile().toPath()); 
////					TreeItem<PathItem> pathTreeItem = new TreeItem<PathItem> (new PathItem(item.getFile().toPath()));
////			    	TreeItem<PathItem> foundedParent = getFoundedParent(pathTreeItem.getValue().getPath(), tree.getRoot());
////			    	TreeItem<PathItem> selectChild = selectChild(pathTreeItem.getValue().getPath(), foundedParent);
//				}
//				
//				if (item.getAction().equalsIgnoreCase("change") && item.getFile().exists()) {
//					removeFromRoot(tree.getRoot(), item.getFile().toPath());
//					registerAll(item.getFile().toPath()); 
//				}
//
//				if (item.getAction().equalsIgnoreCase("delete") && !item.getFile().exists()) {
//					removeFromRoot(tree.getRoot(), item.getFile().toPath());
//				}				
//			} else {
//				System.out.println("its a File");
//				if (item.getAction().equalsIgnoreCase("create") && item.getFile().exists()) {
//					System.out.println("create file: " + item.getFile().toPath());
//					register(item.getFile().toPath());
//				}
//				
//				if (item.getAction().equalsIgnoreCase("change") && item.getFile().exists()) {
//					removeFromRoot(tree.getRoot(), item.getFile().toPath());
//					register(item.getFile().toPath()); 
//				}
//								
//				if (item.getAction().equalsIgnoreCase("delete") && !item.getFile().exists()) {
//					System.out.println("removeFromRoot: " + item.getFile().toPath());
//					removeFromRoot(tree.getRoot(), item.getFile().toPath());
//				}
//			}
//		}		
//		listSaveChanges.clear();
		
		ActionChangeTask.setOnSucceeded(e -> {
			vBoxMessage.setVisible(false);
			isInternalChange = false;
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
	
//	private void actionWait() {		
//		nowShowUpdateMessage = false;
//		serviceWaitUpdate.reset();
//		serviceWaitUpdate.restart();
//	}
	
	
	public ObservableList<ModelFileChanges> sortList(ObservableList<ModelFileChanges> list) {
		list.sort((a, b) -> Long.compare(a.getTimeInMilli(), b.getTimeInMilli()));
		return list;
	}
	
    private TreeItem<PathItem> selectChild(Path child, TreeItem<PathItem> foundedParent) {
    	String childItemString = child.toString();
    	
    	if (foundedParent != null) {
			ObservableList<TreeItem<PathItem>> foundedChild = 
					foundedParent.getChildren().stream()
			            .filter(x -> x.getValue().getPath().toString().equalsIgnoreCase(childItemString))
			            .collect(Collectors.toCollection(FXCollections::observableArrayList));

//			System.out.println("foundedChild: " + foundedChild.get(0) + "  from: " + foundedParent);
			if (!foundedChild.isEmpty()) {
				foundedChild.get(0).setExpanded(true);
				return foundedChild.get(0);
			}			
		}
		return null;
	}
	
    private TreeItem<PathItem> getFoundedParent(Path child, TreeItem<PathItem> rootTreeItem) {
    	String childParentString = getChildParent(child).toString();
    	
    	// if childParent == rootTreeItem -> return rootTreeItem
        if (rootTreeItem.getValue().getPath().toString().equalsIgnoreCase(childParentString)) {
        	return rootTreeItem;
		} else {
			 ObservableList<TreeItem<PathItem>> foundedParent = 
				rootTreeItem.getChildren().stream()
		            .filter(x -> x.getValue().getPath().toString().equalsIgnoreCase(childParentString))
		            .collect(Collectors.toCollection(FXCollections::observableArrayList));	
			if (!foundedParent.isEmpty()) {
				return foundedParent.get(0);
			}			 
		}
		return null;
	}
    
    private Path getChildParent(Path child) {
		return child.toFile().getParentFile().toPath();
	}
    

	
	private void getPermissions(File file) {		
		System.out.println("1. length: " + file.length());
		System.out.println("2. last modified: " + new Date(file.lastModified()));
		System.out.println("3. readable: " + file.canRead());
		System.out.println("4. writable: " + file.canWrite());
		System.out.println("5. executable: " + file.canExecute());
	}
	
    public void registerAll(final Path start)  {
        // register directory and sub-directories   	
        try {
			Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			    @Override
			    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
			            throws IOException {
			        register(dir);
			        return FileVisitResult.CONTINUE;
			    }
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
    public void register(Path dir) {
		TreeItem<PathItem> pathTreeItem = PathTreeItem.createNode(new PathItem(dir));
		if (!isItemExist(pathTreeItem, CTree.treeItem)) {
			addNewNode(pathTreeItem, CTree.treeItem);
		}
    }

    	
	private void addNewNode(TreeItem<PathItem> pathTreeItem, TreeItem<PathItem> rootTreeItem) {
		String pathNewItem = pathTreeItem.getValue().getPath().toString();
		String rootItem = rootTreeItem.getValue().getPath().toString();
			
//		System.out.println("pathNewItem: " + pathNewItem + " == rootItem: " + rootItem);
			
		ObservableList<TreeItem<PathItem>> newList = FXCollections.observableArrayList();
		
		try (Stream<TreeItem<PathItem>> stream = rootTreeItem.getChildren().stream()){
			
			newList = stream.filter(x -> x.getValue().getPath().toString().equalsIgnoreCase(pathNewItem))
		          .collect(Collectors.toCollection(FXCollections::observableArrayList));
			stream.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
//		System.out.println("newList.size() 1: " + newList.size());
		
		if (newList.size() == 0) {
			addTreeItems(rootTreeItem);
		}
		
		for (TreeItem<PathItem> item : newList) {
//			System.out.println("gefundenes item: " + item.getValue().getPath().toFile());	
			addTreeItems(item);
		}
	}
    
	private void addTreeItems(TreeItem<PathItem> item) {

        Path rootPath = item.getValue().getPath();
        PathItem pathItem = new PathItem(rootPath);
        TreeItem<PathItem> treeItem = new TreeItem<PathItem>(pathItem);
        
        File treeItemFile =  treeItem.getValue().getPath().toFile();
        
        if (treeItemFile.exists()) {
			CTree.createTree(treeItem, false);    
			
			item.getChildren().clear();
//			System.out.println("addTreeItems in Item: " + item + " -> " + treeItem);
			item.getChildren().addAll(treeItem.getChildren());
			item.setExpanded(true);
			tree.getSelectionModel().select(0);
			tree.getFocusModel().focus(0);
//			tree.refresh();			
		}
	}
	
	private boolean isItemExist(TreeItem<PathItem> pathTreeItem, TreeItem<PathItem> rootTreeItem) {
		String pathNewItem = pathTreeItem.getValue().getPath().toString();
				
		getAllItems(rootTreeItem);
		
		// gibt es im Tree, das Item schon, wenn ja -> abbrechen
		for(Entry<Path, TreeItem<PathItem>> entry: listAllItems.entrySet()) {	
			
			  if (entry.getKey().toString().equalsIgnoreCase(pathNewItem)) {
//				  System.err.println("Item gibt es schon: " + entry.getKey() + " -> " + entry.getValue());
				  return true;
			  }
		}
		return false;
	}
	
    private void getAllItems(TreeItem<PathItem> item){
    	listAllItems.clear();
        if(item.getChildren().size() > 0){
            for(TreeItem<PathItem> subItem : item.getChildren()){
//            	if (subItem.getValue().getPath().toFile().isDirectory()) {
            		listAllItems.put(subItem.getValue().getPath(), subItem);
            		getAllItems(subItem);
//				}              
            }
        }
    }

    public TreeItem<PathItem> removeFromRoot(TreeItem<PathItem> root, Path path){
//    	listAllItems.clear();
    	TreeItem<PathItem> foundedItem = null;
        if(root.getChildren().size() > 0){
            for(TreeItem<PathItem> subItem : root.getChildren()){
//            	System.out.println(subItem.getValue().getPath().toString() + " == " + path.toString());
            	if (subItem.getValue().getPath().toString().equalsIgnoreCase(path.toString())) {       
            		System.err.println("return: " + subItem);
            		foundedItem = subItem;   
            		foundedItem.getParent().getChildren().remove(foundedItem);
            		break;
            	}
            	
            	if (IsPathDirectory(subItem.getValue().getPath().toString())) {
            		removeFromRoot(subItem, path);
            	}
            }           
        }
		return foundedItem;
    }

	public ObservableList<ModelFileChanges> getListSaveChanges() {return listSaveChanges;}
	public TreeView<PathItem> getTree() {return tree;}
	public VBox getvBoxMessage() {return vBoxMessage;}
	
	   
//    private void removeFromRoot(Path path) {
//    	Collection<Entry<Path, TreeItem<PathItem>>> foundedChild = null;
//    		
//    		TreeItem<PathItem> foundedItem = searchItem(tree.getRoot(), path);
//    		System.out.println("foundedItem: " + foundedItem.getValue().getPath());
//    		if (foundedItem != null) {
//    			System.out.println("treeItem.getParent(): " + foundedItem.getParent() + " -> remove: " + foundedItem);
//    			foundedItem.getParent().getChildren().remove(foundedItem);
//			}
//    		
//    		System.out.println("listAllItems: " + listAllItems.size());
//    		
//    		try (Stream<Entry<Path, TreeItem<PathItem>>> stream = listAllItems.entrySet().stream()){
//				
//    			foundedChild = stream
//	    			.filter(x -> x.getValue().getValue().getPath().toString().equalsIgnoreCase(path.toString()))			            
//		            .collect(Collectors.toCollection(FXCollections::observableArrayList));
//    			stream.close();
//    			
//    			System.out.println("try: " + foundedChild.size());
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//    		System.out.println("foundedChild: " + foundedChild.size());
//    		
//    	for (Entry<Path, TreeItem<PathItem>> treeItem : foundedChild) {
//    		System.out.println("treeItem.getParent(): " + treeItem.getValue().getParent() + " -> remove: " + treeItem);
//    		treeItem.getValue().getParent().getChildren().remove(treeItem);
//		}
//	}   
    
    
    
}
