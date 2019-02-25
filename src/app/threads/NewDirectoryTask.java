package app.threads;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import app.TreeViewWatchService.CreateTree;
import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import app.functions.LoadTime;
import app.interfaces.ICursor;
import app.interfaces.ITreeItemMethods;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

public class NewDirectoryTask extends Task<Void> implements ICursor, ITreeItemMethods {

	private CTree cTree;
	private PathTreeCell pathTreeCell;
	private TreeItem<PathItem> treeItem;
	private TreeItem<PathItem> newItem;
	
	public NewDirectoryTask(CTree cTree, PathTreeCell pathTreeCell, TreeItem<PathItem> treeItem) {
		this.pathTreeCell = pathTreeCell;
		this.treeItem = treeItem;
		this.cTree = cTree;
	}

	@Override
	protected void cancelled() {

	}
	
	@Override
	protected void failed() {

	}
	
	@Override
	protected void succeeded() {   	
		Path newDir = newItem.getValue().getPath();
//    	Path parentPath = pathTreeCell.getTreeItem().getValue().getPath();
    	Path parentPath = treeItem.getValue().getPath();
    	
    	TreeItem<PathItem> parentItem = getItemSearchRecrusive(pathTreeCell.getTreeView().getRoot(), parentPath.toString());
//    	TreeItem<PathItem> parentItem = new TreeItem<PathItem>(new PathItem(parentPath));

    	
    	
    	if (newDir.toFile().exists()) {

    		cTree.getChildrenChangedListener().setSelectNewDirectoy(true);
    		
	    	CreateTree createTree = cTree.getCreateTree();   	
//	    	hier weiter, methode "updatePathListFormItem()" muss überarbeitet werden	  
	    	createTree.updatePathListFormItem(newDir);
//	    	createTree.updatePathListFormItem(parentPath);
	    	createTree.startCreateTree(pathTreeCell.getTreeView().getRoot(), false, false);
	    	
//	    	TreeItem<PathItem> item = new TreeItem<PathItem>(new PathItem(newDir));
//	    	pathTreeCell.getTreeView().getSelectionModel().select(item);
	    	
	    	System.out.println("path___: " + pathTreeCell.getTreeView().getRoot().getValue().getPath());
	    	SortWinExplorerTask sortWinExplorerTask = new SortWinExplorerTask(cTree, null, parentItem, newDir.toString());
//	    	SortWinExplorerTask sortWinExplorerTask = new SortWinExplorerTask(cTree, null, pathTreeCell.getTreeView().getRoot(), newDir.toString());
	    	bindUIandService(cTree.getPrimaryStage(), sortWinExplorerTask);
	    	new Thread(sortWinExplorerTask).start();
    	}
    	
	}
	
	@Override
	protected Void call() throws Exception {
		
		LoadTime.Start();
		
        Path newDir = createNewDirectory();
        System.out.println(1);
        if (newDir != null) {   
        	System.err.println("NewDirectory: " + newDir);
      	  	this.newItem = new TreeItem<PathItem>(new PathItem(newDir, true));
      	  	System.out.println(3);
//      	  CTree.createTree(newItem, false);
      	  	System.out.println(99);
        }
		
        LoadTime.Stop("NewDirectoryTask()", "");
		return null;
	}

	 private Path createNewDirectory() {
         Path newDir = null;
         
         while (true) {
//             Path path = pathTreeCell.getTreeItem().getValue().getPath();
             Path path = treeItem.getValue().getPath();
//             newDir = Paths.get(path.toAbsolutePath().toString(), "new directory " + String.valueOf(pathTreeCell.getItem().getCountNewDir()));
             newDir = Paths.get(path.toAbsolutePath().toString(), "new directory " + String.valueOf(treeItem.getValue().getCountNewDir()));
             try {
                 Files.createDirectory(newDir);
                 break;
             } catch (FileAlreadyExistsException ex) {
                 continue;
             } catch (IOException ex) {
           	  pathTreeCell.cancelEdit();
//                 messageProp.setValue(String.format("Creating directory(%s) failed", newDir.getFileName()));
                 break;
             }
         }
             return newDir;
     }

	
}
