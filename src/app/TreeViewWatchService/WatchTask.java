package app.TreeViewWatchService;

import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.javafx.scene.control.skin.VirtualContainerBase;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.sun.javafx.scene.control.skin.VirtualFlow.ArrayLinkedList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * This refers to this site http://docs.oracle.com/javase/tutorial/essential/io/notification.html
 * I just wanted to bind this result to JavaFX UI control(TextArea)
 * 
 * @author tomo
 */
public class WatchTask extends Task<Void>{
    private Path path;
    private PathTreeCell cell;
    private TreeView<PathItem> fileTreeView;
    private StringBuilder message = new StringBuilder();

    public WatchTask(Path path, PathTreeCell cell, TreeView<PathItem> fileTreeView) {
        this.path = path;
        this.fileTreeView = fileTreeView;
//        this.cell = cell;
        
    }
    
    @Override
    protected Void call() throws Exception {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        while (true) {
            WatchKey key;
            System.out.println("in While");
            try {
                key = watcher.take();
            } catch (InterruptedException e) {
                break;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == OVERFLOW) {
                    continue;
                }
                Path context = (Path) event.context();
				Path child = path.resolve(context);

                TreeItem<PathItem> pathTreeItem = PathTreeItem.createNode(new PathItem(child));

//                System.out.println("size(): " + FileTreeViewSample.myTreeCells.size());
                
                
                
//                for (int i = 0; i < FileTreeViewSample.myTreeCells.size(); i++) {
//                	System.out.println(FileTreeViewSample.myTreeCells..getPath());
//				}
//                for (PathTreeCell path : FileTreeViewSample.myTreeCells.iterator()) {
//					System.out.println(path.getItem().getPath());
//				}
                
                addNewNode(pathTreeItem, FileTreeViewSample.treeItem);
                
//                System.out.println("root: " + FileTreeViewSample.treeItem);
//                for (TreeItem<PathItem> item : FileTreeViewSample.treeItem.getChildren()) {
//    				System.out.println("treeItem.getChildren(): " + item.getValue().getPath());
//    			}
                
                
                PathNode p = PathNode.getTree(child);
                System.out.println("pathTreeItem: " + pathTreeItem);
                System.out.println("p.getPath(): " + p.getPath());
//                for (Path path : p.getPath()) {
//					System.out.println("Root-Ordner: " + path.getRoot() + " -> datei: " + path.getFileName() + "" );
//				}

                message.append("hallo: " + child.toAbsolutePath());
                message.append(getKindToMessage(event.kind()));
                message.append(System.getProperty("line.separator"));
                updateMessage(message.toString()); // to bind to the TextArea
            }
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
        return null;
    }
    

	private void addNewNode(TreeItem<PathItem> pathTreeItem, TreeItem<PathItem> treeItem) {
		String pathNewItem = pathTreeItem.getValue().getPath().toString();
		String rootItem = treeItem.getValue().getPath().toString();
			
		System.out.println("pathNewItem: " + pathNewItem + " == rootItem: " + rootItem);
		
		ObservableList<TreeItem<PathItem>> newList = 
				treeItem.getChildren().stream()
		            .filter(x -> x.getValue().getPath().toString().equalsIgnoreCase(pathNewItem))
		            .collect(Collectors.toCollection(FXCollections::observableArrayList));
		

		System.out.println("newList.size(): " + newList.size());
		
		if (newList.size() == 0) {
			getTreeItem(treeItem);
		}
		
		for (TreeItem<PathItem> item : newList) {
			System.out.println("gefundenes item: " + item.getValue().getPath());	
			getTreeItem(item);
//			item.getChildren().add(e);
		}

	}
    
	private void getTreeItem(TreeItem<PathItem> item) {
		
		item.getChildren().clear();
		
        Path rootPath = item.getValue().getPath();
        PathItem pathItem = new PathItem(rootPath);
        TreeItem<PathItem> treeItem = new TreeItem<PathItem>(pathItem);
        File treeItemFile =  treeItem.getValue().getPath().toFile();
        
        if (treeItemFile.exists()) {
			try {
				FileTreeViewSample.createTree( treeItem);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}       
			item.getChildren().addAll(treeItem.getChildren());
		}
        

        
//        System.out.println("treeItem.getChildren().size(): " + treeItem.getChildren().size());
//        
//        for (TreeItem<PathItem> items : treeItem.getChildren()) {
//        	items.setExpanded(false);
//	        try {
//				FileTreeViewSample.createTree( items);			
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//        	item.getChildren().add(items);
//        }

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
