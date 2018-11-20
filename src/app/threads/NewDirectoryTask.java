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
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

public class NewDirectoryTask extends Task<Void> {

	private CTree cTree;
	private PathTreeCell pathTreeCell;
	
	public NewDirectoryTask(CTree cTree, PathTreeCell pathTreeCell) {
		this.pathTreeCell = pathTreeCell;
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
    	CreateTree createTree = cTree.getCreateTree();
    	Path path = pathTreeCell.getTreeItem().getValue().getPath();
    	createTree.updatePathListFormItem(path);
    	createTree.startCreateTree(pathTreeCell.getTreeView().getRoot(), false, false);
	}
	
	@Override
	protected Void call() throws Exception {
		
		LoadTime.Start();
		
        Path newDir = createNewDirectory();
        if (newDir != null) {            	  
      	  TreeItem<PathItem> newItem = new TreeItem<PathItem>(new PathItem(newDir));
      	  CTree.createTree(newItem, false);
        }
		
        LoadTime.Stop("NewDirectoryTask()", "");
		return null;
	}

	 private Path createNewDirectory() {
         Path newDir = null;
         
         while (true) {
             Path path = pathTreeCell.getTreeItem().getValue().getPath();
             newDir = Paths.get(path.toAbsolutePath().toString(), "new directory " + String.valueOf(pathTreeCell.getItem().getCountNewDir()));
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
