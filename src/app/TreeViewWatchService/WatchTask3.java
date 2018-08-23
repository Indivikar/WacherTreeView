package app.TreeViewWatchService;

import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import app.controller.CTree;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * This refers to this site http://docs.oracle.com/javase/tutorial/essential/io/notification.html
 * I just wanted to bind this result to JavaFX UI control(TextArea)
 * 
 * @author tomo
 */
public class WatchTask3 extends Task<Void>{
    private Path path;
    private PathTreeCell cell;
    private TreeView<PathItem> tree;
    private StringBuilder message = new StringBuilder();
    private WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private ServiceAddItems serviceAddItems;
    private Thread thread;
    
    
    private final boolean recursive;
    private boolean trace = true;
    
//    private ObservableSet<TreeItem<PathItem>> listeAlleOrdner = FXCollections.observableSet();
    private HashMap<Path, TreeItem<PathItem>> listeAlleOrdner = new HashMap<>();
    private ObservableList<Path> delItem = FXCollections.observableArrayList();
    
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }
    
    public WatchTask3(Path path, boolean recursive, PathTreeCell cell, TreeView<PathItem> tree) throws IOException {
        this.path = path;
        this.tree = tree;
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.recursive = recursive;
        this.serviceAddItems = new ServiceAddItems();
        
        this.thread = new Thread(serviceAddItems.createTask());
        thread.setDaemon(true);
        
        if (recursive) {
            System.out.format("Scanning %s ...\n", path);
            registerAll(path);
            System.out.println("Done.");
        } else {
            register(path);
        }
        
//        this.cell = cell;
        
    }
    
    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {

	        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
	        if (trace) {
	            Path prev = keys.get(key);
	            if (prev == null) {
	                System.err.format("register: %s - %s\n", dir, CTree.treeItem);
		              TreeItem<PathItem> pathTreeItem = PathTreeItem.createNode(new PathItem(dir));
		              
//		             pathTreeItem.getChildren().stream()
////		    		.filter(s -> s.contains("B"))
//		    		.forEach(System.out::println);
		              
		              
		              if (!isItemExist(pathTreeItem, CTree.treeItem)) {
//		            	  System.out.println("vor");
//		            	  serviceAddItems.startAdd(pathTreeItem, CTree.treeItem);
//		            	  System.out.println("nach");
						addNewNode(pathTreeItem, CTree.treeItem);
		              }
		              
	            } else {
	                if (!dir.equals(prev)) {
	                    System.out.format("update: %s -> %s\n", prev, dir);
	                }
	            }
	        }
	        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
//	              TreeItem<PathItem> pathTreeItem = PathTreeItem.createNode(new PathItem(dir));
//	              addNewNode(pathTreeItem, CTree.treeItem);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    @Override
    protected Void call() throws Exception {
//        watcher = FileSystems.getDefault().newWatchService();
//        path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        for (; ; ) {

            // wait for key to be signalled
            WatchKey key;
            try {
//            	serviceAddItems.start();
            	
//            	thread.start();
            	System.out.println("wait for key to be signalled");
//            	
                key = watcher.take();
//                thread.interrupt();
//                thread.resume();
//                serviceAddItems.cancel();
                System.out.println("key detected ");
            } catch (InterruptedException x) {
                return null;
            }

            
                       
            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    System.err.println("OVERFLOW !");
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                System.err.format("%s: %s\n", event.kind().name(), child);
//                System.err.println("isRecursive: " + recursive + " " +event.kind().name() + " " + child);

//                TreeItem<PathItem> pathTreeItem = PathTreeItem.createNode(new PathItem(child));
//                addNewNode(pathTreeItem, CTree.treeItem);
                
                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
//                	System.out.println("im ENTRY_CREATE");
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {                            	
                            registerAll(child);                           
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                    
                    System.out.println("Refresh Tree");
//                    tree.getTreeItem(0).setExpanded(false);
//                    tree.getTreeItem(0).setExpanded(true);
                    
                    for (TreeItem<PathItem> items : tree.getRoot().getChildren()) {                    	
                    	TreeHelper.triggerTreeItemRefresh(items);
					}
                    
                    tree.refresh();
                    
                } 
                
                
                
                if (recursive && (kind == ENTRY_DELETE)) {
//                	removeItem(child, CTree.treeItem);
//                    keys.remove(key);
                	
//                	keys.forEach((k,v) -> System.out.println("remove: " + v));
                	

                	System.out.println("delete Child: " + child);
//                	delItem.add(child);
                	removeFromRoot(child);
                	if (child.toFile().isDirectory()) {
                		System.out.println("key removed: " + child);
//						removeItem(child, tree.getRoot()); 
					} 
                	               	
//                    key.cancel();
                    
                    
                }
                
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
//                System.out.println("key remove: " + key.);
                
                // all directories are inaccessible
                if (keys.isEmpty()) {
                	
                	System.out.println("key break");
                    break;
                }
            }
        }
        
//        System.out.println("starte removeFromRoot()");
//        removeFromRoot();
        
        return null;
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
    	
//				 foundedChild = 
//					tree.getRoot().getChildren().stream()
//			            .filter(x -> x.getValue().getPath().toString().equalsIgnoreCase(path.toString()))			            
////			            .peek(System.out::println)
//			            .collect(Collectors.toCollection(FXCollections::observableArrayList));
		
    	
    	
    	for (TreeItem<PathItem> treeItem : foundedChild) {
    		treeItem.getParent().getChildren().remove(treeItem);
		}
    	


//			System.out.println("foundedChild: " + foundedChild.get(0) + "  from: " + foundedParent);
//			if (!foundedChild.isEmpty()) {
//				return foundedChild.get(0);
//			}			
//		}

	}
    
    private void test() {
		// TODO Auto-generated method stub

	}
    
    private void removeItem(Path child, TreeItem<PathItem> rootTreeItem) {
    	System.out.println("remove item: " + child + "  root: " + rootTreeItem);

    	TreeItem<PathItem> foundedParent = getFoundedParent(child, rootTreeItem);
    	TreeItem<PathItem> foundedChild = getFoundedChild(child, foundedParent);
    	
    	System.out.println("foundedParent: " + foundedParent + "  root: " + rootTreeItem);
			
		System.out.println("foundedChild: " + foundedChild + "  from: " + foundedParent);
		foundedParent.getChildren().remove(foundedChild);
		System.out.println("item removed: " + foundedChild);
		

	}
    
    private TreeItem<PathItem> getFoundedChild(Path child, TreeItem<PathItem> foundedParent) {
    	String childItemString = child.toString();
    	
    	if (foundedParent != null) {
			ObservableList<TreeItem<PathItem>> foundedChild = 
					foundedParent.getChildren().stream()
			            .filter(x -> x.getValue().getPath().toString().equalsIgnoreCase(childItemString))
			            .collect(Collectors.toCollection(FXCollections::observableArrayList));

			System.out.println("foundedChild: " + foundedChild.get(0) + "  from: " + foundedParent);
			if (!foundedChild.isEmpty()) {
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
		
		
		
//		ObservableList<TreeItem<PathItem>> newList = 
//				rootTreeItem.getChildren().stream()
//					.filter(x -> x.getValue().getPath().toString().equalsIgnoreCase(pathNewItem))
//					.collect(Collectors.toCollection(FXCollections::observableArrayList));

		System.out.println("newList.size() 1: " + newList.size());
		
		if (newList.size() == 0) {
			addTreeItems(rootTreeItem);
		}
		
		for (TreeItem<PathItem> item : newList) {
			System.out.println("gefundenes item: " + item.getValue().getPath().toFile());	
			addTreeItems(item);
//			item.getChildren().add(e);
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
    
	private void addTreeItems(TreeItem<PathItem> item) {

        Path rootPath = item.getValue().getPath();
        PathItem pathItem = new PathItem(rootPath);
        TreeItem<PathItem> treeItem = new TreeItem<PathItem>(pathItem);
        
        File treeItemFile =  treeItem.getValue().getPath().toFile();
        
        if (treeItemFile.exists()) {
			CTree.createTree(treeItem, true);    
			
			item.getChildren().clear();
			System.out.println("addTreeItems in Item: " + item + " -> " + treeItem);
			item.getChildren().addAll(treeItem.getChildren());
			item.setExpanded(true);
			tree.getSelectionModel().select(0);
			tree.getFocusModel().focus(0);
//			tree.refresh();			
		}

	}
	

    @Override
    protected void cancelled() {
        updateMessage("Watch task was cancelled"); // to bind to the TextArea
    }

    private String getKindToMessage(WatchEvent.Kind<?> kind) {
        if (kind == ENTRY_CREATE) {
            return " is created";
        } else if (kind == ENTRY_DELETE) {
            return " is deleted";
        }
        return " is updated";
    }    
}

class PathNode {
    public static PathNode getTree(Path root) throws IOException {
        if(Files.isDirectory(root)) {
            Path[] childPaths;
            try(Stream<Path> dirStream = Files.list(root)) {
                childPaths = dirStream
                        .sorted(PATH_COMPARATOR)
                        .toArray(Path[]::new);
            }
            List<PathNode> children = new ArrayList<>(childPaths.length);
            for(Path p: childPaths) {
//            	System.out.println(p);
                children.add(getTree(p));
            }
            return directory(root, children);
        } else {
            return file(root, Files.getLastModifiedTime(root));
        }
    }

    private static final Comparator<Path> PATH_COMPARATOR = (p, q) -> {
        boolean pd = Files.isDirectory(p);
        boolean qd = Files.isDirectory(q);

        if(pd && !qd) {
            return -1;
        } else if(!pd && qd) {
            return 1;
        } else {
            return p.getFileName().toString().compareToIgnoreCase(q.getFileName().toString());
        }
    };

    static PathNode file(Path path, FileTime lastModified) {
        return new PathNode(path, false, Collections.emptyList(), lastModified);
    }

    static PathNode directory(Path path, List<PathNode> children) {
        return new PathNode(path, true, children, null);
    }

    private final Path path;
    private final boolean isDirectory;
    private final List<PathNode> children;
    private final FileTime lastModified;

    private PathNode(Path path, boolean isDirectory, List<PathNode> children, FileTime lastModified) {
        this.path = path;
        this.isDirectory = isDirectory;
        this.children = children;
        this.lastModified = lastModified;
    }

    public Path getPath() {
        return path;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public List<PathNode> getChildren() {
        return children;
    }

    public FileTime getLastModified() {
        return lastModified;
    }
    

}
