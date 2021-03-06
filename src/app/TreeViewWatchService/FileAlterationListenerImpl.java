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
import app.interfaces.ISaveExpandedItems;
import app.interfaces.ISuffix;
import app.loadTime.LoadTime.LoadTimeOperation;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Service;
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
public class FileAlterationListenerImpl implements FileAlterationListener, ISuffix, ISaveExpandedItems {

	
	
	private CTree cTree;
	private TreeView<PathItem> tree;
	private PathTreeCell cell;
	
	private HashMap<Path, TreeItem<PathItem>> listAllItems = new HashMap<>();
	
	private ExecutorService executorService;
	private ServiceRegister serviceRegister;
	private ServiceWaitForUpdate serviceWaitForUpdate;	
	
	
	
	private Map<File, String> map = new HashMap<File, String>();
	 
    // Now add observability by wrapping it with ObservableList.
	private ObservableList<ModelFileChanges> data = FXCollections.observableArrayList();
//	private ObservableList<ModelFileChanges> listSaveChanges = FXCollections.observableArrayList();
	
//	public static boolean isInternalChange = false;
	private boolean startUpdate = false;

	private VBox vBoxMessage;
	
	private TableView<ModelFileChanges> tableView;
	private TableColumn<ModelFileChanges, String> columnAction;
	private TableColumn<ModelFileChanges, String> columnFile;
	private TableColumn<ModelFileChanges, String> columnTime;
	private Button buttonNow;
		
	public FileAlterationListenerImpl(CTree cTree, PathTreeCell cell) {
		this.cTree = cTree;
		this.tree = cTree.getTree();
		this.cell = cell;
		this.vBoxMessage = cTree.getvBoxMessage();
		this.buttonNow = cTree.getButtonNow();
		this.tableView = cTree.getTableView();
		this.columnAction = cTree.getColumnAction();
		this.columnFile = cTree.getColumnFile();
		this.columnTime = cTree.getColumnTime();

		
		
		serviceWaitForUpdate = new ServiceWaitForUpdate(this, cTree);
		
		executorService = Executors.newFixedThreadPool(1);
//		serviceWaitUpdate = new ServiceWaitUpdate(this);
		
		setButtonAction();
		addTableViewProperties();
//		serviceWaitUpdate.setOnSucceeded(e -> {
//			System.out.println("Wait Service Finish");
//			nowShowUpdateMessage = true;
//		});
				
		
//		data.addListener(new ListChangeListener<ModelFileChanges>() {
//				@Override
//				public void onChanged(Change<? extends ModelFileChanges> arg0) {
//					System.out.println("data.size(): " + data.size());
//				}
//		    });		
//		bindings();
	}


	ListChangeListener<ModelFileChanges> saveChangesListener = new ListChangeListener<ModelFileChanges>() {

		@Override
		public void onChanged(Change<? extends ModelFileChanges> c) {
			System.out.println("change listSaveChanges: " + cTree.getListSaveChanges().size());
			System.out.println("change data: " + data.size());
			if (c.getList().size() == 0) {
				vBoxMessage.setVisible(false);
			}
			
			
		}
    };
	
	private void addTableViewProperties() {
		columnAction.setCellValueFactory(new PropertyValueFactory<>("action"));
		columnFile.setCellValueFactory(new PropertyValueFactory<>("fileString"));
		columnTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        
		cTree.getListSaveChanges().addListener(saveChangesListener);
		tableView.setItems(cTree.getListSaveChanges());
	}
	
	
	private void setButtonAction() {
		buttonNow.setOnAction(e -> {
			Platform.runLater(() -> {
//				addAllExpandedItems(cTree.getTree().getRoot());
			});
			actionChange();
		});		
	}
	
	@Override
	public void onStart(final FileAlterationObserver observer) {
//		System.out.println("The FileListener has started on " + observer.getDirectory().getAbsolutePath());
//		data.clear();	
	}

	@Override
	public void onDirectoryCreate(final File directory) {
//		System.out.println(directory.getAbsolutePath() + " was created.");
//		data.add(new ModelFileChanges("create", directory, getTimeStamp()));
		addData("create", directory, getTimeStamp());
		startUpdate = true;
	}

	@Override
	public void onDirectoryChange(final File directory) {
//		System.out.println(directory.getAbsolutePath() + " wa modified");
//		data.add(new ModelFileChanges("change", directory, getTimeStamp()));
		addData("change", directory, getTimeStamp());
		startUpdate = true;
	}

	@Override
	public void onDirectoryDelete(final File directory) {		
		System.out.println(directory.getAbsolutePath() + " was deleted.");
//		data.add(new ModelFileChanges("delete", directory, getTimeStamp()));
		addData("delete", directory, getTimeStamp());
		startUpdate = true;
	}

	@Override
	public void onFileCreate(final File file) {
//		System.out.println(file.getAbsoluteFile() + " was created.");
//		data.add(new ModelFileChanges("create", file, getTimeStamp()));
		addData("create", file, getTimeStamp());
		startUpdate = true;
	}

	@Override
	public void onFileChange(final File file) {		
		System.out.println(file.getAbsoluteFile() + " was modified.");
//		data.add(new ModelFileChanges("change", file, getTimeStamp()));
		addData("change", file, getTimeStamp());
		startUpdate = true;
	}

	@Override
	public void onFileDelete(final File file) {
//		System.out.println(file.getAbsoluteFile() + " was deleted.");
//		data.add(new ModelFileChanges("delete", file, getTimeStamp()));
		addData("delete", file, getTimeStamp());
		startUpdate = true;
	}

	@Override
	public void onStop(final FileAlterationObserver observer) {
//		System.out.println("The FileListener has stopped on " + observer.getDirectory().getAbsolutePath());

//		System.err.println("onStop: " + data.size());
		
		Platform.runLater(() -> {
			if (data.size() != 0 && !serviceWaitForUpdate.isRunning()) {	
//					System.err.println("   Starte Service: " + data.size());
					serviceWaitForUpdate.start();
			} 								
		});		
		
		
//		Platform.runLater(() -> {
//			if (data.size() != 0 && startUpdate) {			
//					
//				
//				if (!serviceWaitForUpdate.isRunning()) {
//					serviceWaitForUpdate.start();
//				}
//				
//				getMainDirectory();
//				
//
//				if (isInternalChange) {
//					actionChange();
//				} else {
////					System.out.println("show List");
//					vBoxMessage.setVisible(true);
//					startUpdate = false;
//				}
//				
////				for (ModelFileChanges items : listSaveChanges) {
////					System.out.println("items in List: " + items.getFileString());
////				}
//			} 								
//		});		
	}

	private Timestamp getTimeStamp() {
		return new Timestamp(System.currentTimeMillis());
	}
	
	private void addData(String action, File file, Timestamp timestamp) {
		if (!isFileHidden(file)) {
			data.add(new ModelFileChanges(action, file, getTimeStamp()));
		}
		
	}
	
    public boolean isFileHidden(File file) {
        return file.isHidden();
    }
	
//	private boolean isShadowFile(File file) {
//		String name = file.getName()
//		ObservableList<String> suffix = FXCollections.observableArrayList("tmp");
//		ObservableList<String> startWith = FXCollections.observableArrayList("~");
//		
//		for (String item : startWith) {
//			if (item.equals(anObject)) {
//				
//			}
//		}
//		
//		
//		
//		return startUpdate;
//	}
	
//	public void getMainDirectory() {
//				
//		System.out.println("data.size(): " + data.size());
//		
////			ObservableList<ModelFileChanges> filter = data.stream()		
////				.filter(p -> p.getFile().isDirectory())
////				.collect(Collectors.toCollection(FXCollections::observableArrayList)); 
////		
////			System.out.println("filter.size(): " + filter.size());
////			if (filter.size() <= 1) {
////				return;
////			}
//			
//			ModelFileChanges res = null;
//			Optional<ModelFileChanges> resDirectory = data.stream()		
//				.filter(p -> IsPathDirectory( p.getFileString()))	
//				.min((p1, p2) -> Integer.compare(p1.getFileString().length(), p2.getFileString().length()));
//			
//			if (!data.isEmpty()) {
//				if (resDirectory.isPresent()) {
//					res = resDirectory.get();
//					
//				} else {
//					System.err.println("ergOptional not present");
//					Optional<ModelFileChanges> resFiles = data.stream()		
//							.filter(p -> !IsPathDirectory( p.getFileString()))
//							.findFirst();	
//					res = resFiles.get();
//				}
//			} else {
//				System.err.println("list data is empty");
//				return;
//			}
//
//
//			System.err.println("der kleinste: " + res.getFileString());
//			removeItem(res);
//			
//			if (!existItem(res) && !existMainDirectoryFromItem(res)) {
//				listSaveChanges.add(res);	
//				sortList(listSaveChanges);				
//			}
//			data.remove(res);
//
//			getMainDirectory();
//	}
	
	
//	private boolean existMainDirectoryFromItem(ModelFileChanges res) {
//
//		for (ModelFileChanges item : listSaveChanges) {
//			if (item.getAction().equalsIgnoreCase(res.getAction())) {
//				for (String parents : getAllParents(res.getFile())) {
//					if (parents.equalsIgnoreCase(item.getFileString())) {
//						return true;
//					}
//				}	
//			}			
//		}
//		
//		return false;
//	}

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
	
//	private boolean existItem(ModelFileChanges erg) {
//		for (ModelFileChanges item : listSaveChanges) {
//			if (item.equals(erg)) {
//				return true;
//			}
//		}
//		return false;		
//	}
	
//	private void removeItem(ModelFileChanges erg) {
//		
//		for (int i = 0; i < data.size(); i++) {
//			if (data.get(i).getFile().isDirectory() && data.get(i).getFileString().contains(erg.getFileString())) {				
//				for (String parents : checkParents(data.get(i))) {
//					if (erg.getFileString().equalsIgnoreCase(parents)) {
////						System.out.println("remove: " + data.get(i).getFile());
//						data.remove(i);
//					}
//				}					
//			}
//		}
//	}
	
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
	
	public void actionChange() {
		ActionChangeTask ActionChangeTask = new ActionChangeTask(this, cTree);
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
		
//		ActionChangeTask.setOnSucceeded(e -> {
			vBoxMessage.setVisible(false);
////			isInternalChange = false;
//		});
		System.out.println("actionChange Ende");

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
	
    public void registerAll(final Path item, Path itemParent)  {
        // register directory and sub-directories   	
    	
    	register(item, itemParent);
//        try {
//			Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
//			    @Override
//			    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
//			            throws IOException {
//			    	System.out.println("register 1: " + dir);
//			    	
//			        register(dir);
//			        return FileVisitResult.CONTINUE;
//			    }
//			});
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }
	
    public void register(Path dir, Path itemParent) {
		TreeItem<PathItem> pathTreeItem = PathTreeItem.createNode(new PathItem(dir));
		if (!isItemExist(pathTreeItem, CTree.treeItem)) {
//			addNewNode(pathTreeItem, CTree.treeItem);
			addNewNode(pathTreeItem, itemParent);
		}
    }
	private void addNewNode(TreeItem<PathItem> pathTreeItem, Path itemParent) {
		TreeItem<PathItem> rootTreeItem = cTree.getTree().getRoot();
		String pathNewItem = pathTreeItem.getValue().getPath().toString();
		String pathItemParent = itemParent.toString();
			
		System.out.println("pathNewItem: " + pathNewItem + " == itemParent: " + pathItemParent);
			
		ObservableList<TreeItem<PathItem>> newList = FXCollections.observableArrayList();
		
//		try (Stream<TreeItem<PathItem>> stream = rootTreeItem.getChildren().stream()){
//			
//			newList = stream.filter(x -> x.getValue().getPath().toString().equalsIgnoreCase(pathItemParent))
//		          .collect(Collectors.toCollection(FXCollections::observableArrayList));
//			stream.close();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		
		
		searchItemAndAdd(cTree.getTree().getRoot(), pathItemParent);
		
//		for (Entry<Path, TreeItem<PathItem>> item : listAllItems.entrySet()) {
//			if (item.getKey().toString().equalsIgnoreCase(pathItemParent)) {
//				System.out.println("newList.size() 1: " + item.getValue());
//				addTreeItems(item.getValue());
//			}
//		}

//		if (newList.size() == 0) {
//			System.out.println("newList.size() 0: ");
//			addTreeItems(rootTreeItem);
//		}
//		
//		for (TreeItem<PathItem> item : newList) {
//			System.out.println("gefundenes item: " + item.getValue().getPath().toFile());	
////			addTreeItems(item);
//		}
	}
	
    private void searchItemAndAdd(TreeItem<PathItem> root, String itemParent){
    	
			String rootString = root.getValue().getPath().toString();

			// is it the root directory
    		if (rootString.equalsIgnoreCase(itemParent)) {
    			CTree.createTree(root, false);
    			return;
			}
        
            for(TreeItem<PathItem> subItem : root.getChildren()){
            	if (subItem.getValue().getPath().toString().equalsIgnoreCase(itemParent)) {           		
            		CTree.createTree(subItem, false);
            		break;
				} else {
					searchItemAndAdd(subItem, itemParent);
				}          
            }
    }
    
//	private void addNewNode(TreeItem<PathItem> pathTreeItem, TreeItem<PathItem> rootTreeItem) {
//		String pathNewItem = pathTreeItem.getValue().getPath().toString();
//		String rootItem = rootTreeItem.getValue().getPath().toString();
//			
//		System.out.println("pathNewItem: " + pathNewItem + " == rootItem: " + rootItem);
//			
//		ObservableList<TreeItem<PathItem>> newList = FXCollections.observableArrayList();
//		
//		try (Stream<TreeItem<PathItem>> stream = rootTreeItem.getChildren().stream()){
//			
//			newList = stream.filter(x -> x.getValue().getPath().toString().equalsIgnoreCase(pathNewItem))
//		          .collect(Collectors.toCollection(FXCollections::observableArrayList));
//			stream.close();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		
//		System.out.println("newList.size() 1: " + newList.size());
//		
//		if (newList.size() == 0) {
//			System.out.println("newList.size() 0: ");
//			addTreeItems(rootTreeItem);
//		}
//		
//		for (TreeItem<PathItem> item : newList) {
//			System.out.println("gefundenes item: " + item.getValue().getPath().toFile());	
//			addTreeItems(item);
//		}
//	}
    
	private void addTreeItems(TreeItem<PathItem> item) {
		System.err.println("treeItem 1: " + item);
        Path rootPath = item.getValue().getPath();
        PathItem pathItem = new PathItem(rootPath);
        TreeItem<PathItem> treeItem = new TreeItem<PathItem>(pathItem);
        
        File treeItemFile =  treeItem.getValue().getPath().toFile();
        
        System.err.println("treeItem 2: " + treeItem);
        
        if (treeItemFile.exists()) {
        	
        	long start = new Date().getTime();
			CTree.createTree(treeItem, false);    
			long runningTime = new Date().getTime() - start;
			cTree.listLoadTime.add(new LoadTimeOperation("createTree in addTreeItems()", runningTime + "", item.getValue().getPath().toString()));
			System.out.println("createTree in addTreeItems() Ende " + runningTime);
			
			item.getChildren().clear();
			System.out.println("addTreeItems in Item: " + item + " -> " + treeItem);
			item.getChildren().addAll(treeItem.getChildren());			
			item.setExpanded(true);
			tree.getSelectionModel().select(0);
			tree.getFocusModel().focus(0);			
//			tree.refresh();					
		}
        System.err.println("addTreeItems Ende");
	}
	
	private boolean isItemExist(TreeItem<PathItem> pathTreeItem, TreeItem<PathItem> rootTreeItem) {
		String pathNewItem = pathTreeItem.getValue().getPath().toString();
				
		
//		getAllItems(rootTreeItem);		
		long start = new Date().getTime();
		getAllItems(CTree.treeItem);
		long runningTime = new Date().getTime() - start;
		cTree.listLoadTime.add(new LoadTimeOperation("getAllItems()", runningTime + "", ""));
		
		// gibt es im Tree, das Item schon, wenn ja -> abbrechen
		for(Entry<Path, TreeItem<PathItem>> entry: listAllItems.entrySet()) {	
			
			  if (entry.getKey().toString().equalsIgnoreCase(pathNewItem)) {
//				  System.err.println("Item gibt es schon: " + entry.getKey() + " -> " + entry.getValue());
				  return true;
			  }
		}
		return false;
	}
	
	

	
	// TODO - hier kann vielleicht noch optimert werden, muss immer im RootNode gesucht werden oder reicht es auch nur im ParentNode?
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

    
	public ObservableList<ModelFileChanges> getData() {return data;}
//	public ObservableList<ModelFileChanges> getListSaveChanges() {return listSaveChanges;}
	public TreeView<PathItem> getTree() {return tree;}
	public VBox getvBoxMessage() {return vBoxMessage;}


	@Override
	public void addAllExpandedItems() {
		// TODO Auto-generated method stub
		
	}
	
	
	   
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
