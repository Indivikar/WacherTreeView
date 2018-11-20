package app.TreeViewWatchService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import app.controller.CTree;
import app.loadTime.LoadTime.LoadTimeOperation;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

public class ActionChangeTask extends Task<Void>{

		private CTree cTree;
		private FileAlterationListenerImpl fileAlterationListenerImpl;
		
	
	    public ActionChangeTask(FileAlterationListenerImpl fileAlterationListenerImpl, CTree cTree) {
	    	this.cTree = cTree;
	    	this.fileAlterationListenerImpl = fileAlterationListenerImpl;
	    }
	    
	    @Override
	    protected Void call() throws Exception {
	    	long start = new Date().getTime();
	    	System.out.println("ActionChangeTask Start ");
	    	ObservableList<ModelFileChanges> listSaveChanges = cTree.getListSaveChanges();
	    	TreeView<PathItem> tree = fileAlterationListenerImpl.getTree();
	    	
			for (ModelFileChanges item : fileAlterationListenerImpl.sortList(listSaveChanges)) {
				System.out.println("in for");
//				if (IsPathDirectory( item.getFileString())) {
				if (Files.isDirectory(item.getFile().toPath())) {
					System.out.println("its a Directory: " + item.getFileString());
					if (item.getAction().equalsIgnoreCase("create") && item.getFile().exists()) {
						Path itemParentNode = getParentNode(item.getFile().toPath());
						System.out.println("create " + itemParentNode);					
						fileAlterationListenerImpl.registerAll(item.getFile().toPath(), itemParentNode); 
						break;
					}
					
					if (item.getAction().equalsIgnoreCase("change") && item.getFile().exists()) {
						fileAlterationListenerImpl.removeFromRoot(tree.getRoot(), item.getFile().toPath());
						Path itemParentNode = getParentNode(item.getFile().toPath());
						System.out.println("change " + itemParentNode);	
						fileAlterationListenerImpl.registerAll(item.getFile().toPath(), itemParentNode); 
//						fileAlterationListenerImpl.registerAll(item.getFile().toPath()); 	
						break;
					}

					if (item.getAction().equalsIgnoreCase("delete") && !item.getFile().exists()) {
						System.out.println("delete");
						fileAlterationListenerImpl.removeFromRoot(tree.getRoot(), item.getFile().toPath());
					}				
				} else {
					System.out.println("its a File");
					if (item.getAction().equalsIgnoreCase("create") && item.getFile().exists()) {
						System.out.println("create file: " + item.getFile().toPath());
//						fileAlterationListenerImpl.register(item.getFile().toPath());
					}
					
					if (item.getAction().equalsIgnoreCase("change") && item.getFile().exists()) {
						fileAlterationListenerImpl.removeFromRoot(tree.getRoot(), item.getFile().toPath());
//						fileAlterationListenerImpl.register(item.getFile().toPath()); 
					}
									
					if (item.getAction().equalsIgnoreCase("delete") && !item.getFile().exists()) {
						System.out.println("removeFromRoot: " + item.getFile().toPath());
						fileAlterationListenerImpl.removeFromRoot(tree.getRoot(), item.getFile().toPath());
					}
				}
			}		
			listSaveChanges.clear();
			long runningTime = new Date().getTime() - start;
			cTree.listLoadTime.add(new LoadTimeOperation("ActionChangeTask()", runningTime + "", ""));
			System.out.println("ActionChangeTask Ende " + runningTime + "ms");
	        return null;
	    }	

	    private Path getParentNode(Path path) {	    	
	    	File file = path.toFile().getParentFile();
	    	if(isItRoot(path.toFile())) {
	    		return path;
	    	} else {
	    		return file.toPath();
	    	}
		}
	    
	    private boolean isItRoot(File file) {
	    	String root = cTree.getTree().getRoot().getValue().getPath().toFile().getAbsolutePath();
	    	String item = file.getAbsolutePath();
	    	if (root.equals(item)) {
				return true;
			}
			return false;
		}
	    
}
