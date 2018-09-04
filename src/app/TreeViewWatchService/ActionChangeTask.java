package app.TreeViewWatchService;

import java.nio.file.Files;
import java.nio.file.Path;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

public class ActionChangeTask extends Task<Void>{

		private FileAlterationListenerImpl fileAlterationListenerImpl;
	
	    public ActionChangeTask(FileAlterationListenerImpl fileAlterationListenerImpl) {
	    	this.fileAlterationListenerImpl = fileAlterationListenerImpl;
	    }
	    
	    @Override
	    protected Void call() throws Exception {
	    	ObservableList<ModelFileChanges> listSaveChanges = fileAlterationListenerImpl.getListSaveChanges();
	    	TreeView<PathItem> tree = fileAlterationListenerImpl.getTree();
	    	
			for (ModelFileChanges item : fileAlterationListenerImpl.sortList(listSaveChanges)) {
				System.out.println("in for");
//				if (IsPathDirectory( item.getFileString())) {
				if (Files.isDirectory(item.getFile().toPath())) {
					System.out.println("its a Directory: " + item.getFileString());
					if (item.getAction().equalsIgnoreCase("create") && item.getFile().exists()) {
						fileAlterationListenerImpl.registerAll(item.getFile().toPath()); 
//						TreeItem<PathItem> pathTreeItem = new TreeItem<PathItem> (new PathItem(item.getFile().toPath()));
//				    	TreeItem<PathItem> foundedParent = getFoundedParent(pathTreeItem.getValue().getPath(), tree.getRoot());
//				    	TreeItem<PathItem> selectChild = selectChild(pathTreeItem.getValue().getPath(), foundedParent);
					}
					
					if (item.getAction().equalsIgnoreCase("change") && item.getFile().exists()) {
						fileAlterationListenerImpl.removeFromRoot(tree.getRoot(), item.getFile().toPath());
						fileAlterationListenerImpl.registerAll(item.getFile().toPath()); 
					}

					if (item.getAction().equalsIgnoreCase("delete") && !item.getFile().exists()) {
						fileAlterationListenerImpl.removeFromRoot(tree.getRoot(), item.getFile().toPath());
					}				
				} else {
					System.out.println("its a File");
					if (item.getAction().equalsIgnoreCase("create") && item.getFile().exists()) {
						System.out.println("create file: " + item.getFile().toPath());
						fileAlterationListenerImpl.register(item.getFile().toPath());
					}
					
					if (item.getAction().equalsIgnoreCase("change") && item.getFile().exists()) {
						fileAlterationListenerImpl.removeFromRoot(tree.getRoot(), item.getFile().toPath());
						fileAlterationListenerImpl.register(item.getFile().toPath()); 
					}
									
					if (item.getAction().equalsIgnoreCase("delete") && !item.getFile().exists()) {
						System.out.println("removeFromRoot: " + item.getFile().toPath());
						fileAlterationListenerImpl.removeFromRoot(tree.getRoot(), item.getFile().toPath());
					}
				}
			}		
			listSaveChanges.clear();
//			fileAlterationListenerImpl.getvBoxMessage().setVisible(false);
//			fileAlterationListenerImpl.isInternalChange = false;
	        return null;
	    }	


	    
}
