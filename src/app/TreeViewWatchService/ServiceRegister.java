package app.TreeViewWatchService;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import app.controller.CTree;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ServiceRegister extends Task<Void>{
	
	private TreeView<PathItem> tree;
	private HashMap<Path, TreeItem<PathItem>> listeAlleOrdner;
	private File directory;
		
	public ServiceRegister(TreeView<PathItem> tree, HashMap<Path, TreeItem<PathItem>> listeAlleOrdner, File directory) {
		this.tree = tree;
		this.listeAlleOrdner = listeAlleOrdner;
		this.directory = directory;
	}

	@Override
	protected void succeeded() {
		System.out.println("task ende: " + directory);
		cancel();
	}
	
	@Override
	protected void cancelled() {

	}
	
	@Override
	protected void failed() {

	}
	



	@Override
	protected Void call() throws Exception {		
        try {
			registerAll(directory.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
	
    private void register(Path dir) throws IOException {
		TreeItem<PathItem> pathTreeItem = PathTreeItem.createNode(new PathItem(dir));
		if (!isItemExist(pathTreeItem, CTree.treeItem)) {
			addNewNode(pathTreeItem, CTree.treeItem);
		}
    }

    
	
	private void addNewNode(TreeItem<PathItem> pathTreeItem, TreeItem<PathItem> rootTreeItem) {
		String pathNewItem = pathTreeItem.getValue().getPath().toString();
		String rootItem = rootTreeItem.getValue().getPath().toString();
			
		System.out.println("pathNewItem: " + pathNewItem + " == rootItem: " + rootItem);


//		listeAlleOrdner.clear();
//		populateMap(rootTreeItem);
//		
//		// gibt es im Tree, das Item schon, wenn ja -> abbrechen
//		for(Entry<Path, TreeItem<PathItem>> entry: listeAlleOrdner.entrySet()) {	
//			
//			  if (entry.getKey().toString().equalsIgnoreCase(pathNewItem)) {
//				  System.err.println("Item gibt es schon: " + entry.getKey() + " -> " + entry.getValue());
//				  return;
//			  }
//		}
				
		ObservableList<TreeItem<PathItem>> newList = FXCollections.observableArrayList();
		
		try (Stream<TreeItem<PathItem>> stream = rootTreeItem.getChildren().stream()){
			
			newList = stream.filter(x -> x.getValue().getPath().toString().equalsIgnoreCase(pathNewItem))
		          .collect(Collectors.toCollection(FXCollections::observableArrayList));
			stream.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		System.out.println("newList.size() 1: " + newList.size());
		
		if (newList.size() == 0) {
			addTreeItems(rootTreeItem);
		}
		
		for (TreeItem<PathItem> item : newList) {
			System.out.println("gefundenes item: " + item.getValue().getPath().toFile());	
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
			System.out.println("addTreeItems in Item: " + item + " -> " + treeItem);
			item.getChildren().addAll(treeItem.getChildren());
			item.setExpanded(true);
			tree.getSelectionModel().select(0);
			tree.getFocusModel().focus(0);
//			tree.refresh();			
		}

	}
	
	private boolean isItemExist(TreeItem<PathItem> pathTreeItem, TreeItem<PathItem> rootTreeItem) {
		String pathNewItem = pathTreeItem.getValue().getPath().toString();
		
		
		populateMap(rootTreeItem);
		
		// gibt es im Tree, das Item schon, wenn ja -> abbrechen
		for(Entry<Path, TreeItem<PathItem>> entry: listeAlleOrdner.entrySet()) {	
			
			  if (entry.getKey().toString().equalsIgnoreCase(pathNewItem)) {
				  System.err.println("Item gibt es schon: " + entry.getKey() + " -> " + entry.getValue());
				  return true;
			  }
		}
		return false;
	}
	
    private void populateMap(TreeItem<PathItem> item){
    	listeAlleOrdner.clear();
        if(item.getChildren().size() > 0){
            for(TreeItem<PathItem> subItem : item.getChildren()){
            	if (subItem.getValue().getPath().toFile().isDirectory()) {
            		listeAlleOrdner.put(subItem.getValue().getPath(), subItem);
            		populateMap(subItem);
				}
                
            }
        }
//        else {
//        	PathItem node = (PathItem) item.getValue();     
//            if(!node.isHeader()){
//                map.put(node.getTitle(), node);
//            }
//        }
    }
	
    private void removeFromRoot(Path path) {
    	ObservableList<TreeItem<PathItem>> foundedChild = null;
    				
    		try (Stream<TreeItem<PathItem>> stream = tree.getRoot().getChildren().stream()){
				
    			foundedChild = stream
	    			.filter(x -> x.getValue().getPath().toString().equalsIgnoreCase(path.toString()))			            
		            .collect(Collectors.toCollection(FXCollections::observableArrayList));
    			stream.close();
    			
			} catch (Exception e) {
				// TODO: handle exception
			}

    	for (TreeItem<PathItem> treeItem : foundedChild) {
    		treeItem.getParent().getChildren().remove(treeItem);
		}

	}
	
}
