package app.threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CRename;
import app.controller.CTree;
import app.functions.LoadTime;
import app.interfaces.IBindings;
import app.interfaces.ILockDir;
import app.interfaces.ITreeItemMethods;
import app.loadTime.LoadTime.LoadTimeOperation;
import app.sort.WindowsExplorerComparator;
import app.view.alerts.AlertFilesLocked;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Alert.AlertType;

public class RenameTask extends Task<Boolean> implements ITreeItemMethods, IBindings, ILockDir {

	private CRename cRename;
	private CTree cTree;
	private PathTreeCell pathTreeCell;
	private TreeItem<PathItem> cellTreeItem;
	private File cellFile;
	private File newFile;
	private ObservableList<String> listAllLockedFiles;
	
	
	public RenameTask(CRename cRename, CTree cTree, PathTreeCell pathTreeCell, TreeItem<PathItem> cellTreeItem) {
		this.cRename = cRename;
		this.cTree = cTree;
		this.pathTreeCell = pathTreeCell;	
		this.cellTreeItem = cellTreeItem;
		this.cellFile = cellTreeItem.getValue().getPath().toFile();
		System.err.println("Rename70: " + cellFile);
	}

	@Override
	protected void cancelled() {
//		unlockDir(cTree.getLockFileHandler(), pathTreeCell.getTreeItem().getValue().getLevelOneItem());
		unlockDir(cTree.getLockFileHandler(), cellTreeItem.getValue().getLevelOneItem());
	}
	
	@Override
	protected void failed() {
		// TODO - ein Alert einbauen
		cRename.getLabelMassage().setText("Fail");		
//		unlockDir(cTree.getLockFileHandler(), pathTreeCell.getTreeItem().getValue().getLevelOneItem());
		unlockDir(cTree.getLockFileHandler(), cellTreeItem.getValue().getLevelOneItem());
	}
	
	@Override
	protected void succeeded() {			
//		unlockDir(cTree.getLockFileHandler(), pathTreeCell.getTreeItem().getValue().getLevelOneItem());
		unlockDir(cTree.getLockFileHandler(), cellTreeItem.getValue().getLevelOneItem());
		cRename.getStage().close();

		pathTreeCell.getTreeView().getSelectionModel().clearSelection();
		
//        SortWinExplorerTask task = new SortWinExplorerTask(pathTreeCell.getTreeItem().getParent());
//        bindUIandService(cRename.getStage(), task);
//        new Thread(task).start();
//        task.setOnSucceeded(e -> {
//        	selectItemSearchInTreeView(pathTreeCell.getTreeView(), pathTreeCell.getTreeItem(), newFile.getAbsolutePath());
//        });
        
	}
	
	@Override
	protected Boolean call() throws Exception {

		LoadTime.Start();
		
		// unlock lockFile to change the name
		unlockLockFile(cTree.getLockFileHandler(), cellTreeItem);
        
		boolean isRenameSuccessful = false;
		
		String newName = cRename.getTextFieldName().getText().trim();
		if (cellFile.isDirectory()) {
			System.err.println("Rename80: " + cellFile.getParentFile() + " > " + cellFile.getName());
			isRenameSuccessful = rename(cellFile.getParentFile(), cellFile.getName(), newName);
		} else {
			isRenameSuccessful = rename(cellFile.getParentFile(), cellFile.getName(), newName + "." + cRename.getSuffix());
		}

		LoadTime.Stop("RenameTask()", "");

		
		return isRenameSuccessful;
	}

	private boolean rename(File path, String oldName, String newName) {
		
		File oldFile = new File(path + File.separator + oldName);
		newFile = new File(path + File.separator + newName);	
				
		System.err.println("Rename90: " + oldFile + " > " + newFile);
		
		boolean isRenameSuccessful = oldFile.renameTo(newFile);
		
	    if (isRenameSuccessful) {
	        System.out.println("renamed");
//	        pathTreeCell.getTreeItem().getValue().setPath(newFile.toPath());
	        cellTreeItem.getValue().setPath(newFile.toPath());
	        
	        if (newFile.isDirectory()) {
	        	LoadTime.Start();
//	        	setNewPathsForChildrens(pathTreeCell.getTreeItem(), oldFile, newFile);
	        	setNewPathsForChildrens(cellTreeItem, oldFile, newFile);
	        	LoadTime.Stop("setNewPathsForChildrens()", "");
			}

	        
	        pathTreeCell.getTreeView().refresh();
	        	   
//	        System.out.println(pathTreeCell.getTreeItem());
	        
	      } else {
	        System.out.println("Error");
	        Platform.runLater(()->{
	        	cRename.getLabelMassage().setText("error when rename");
	        });	        
	      }
	    
		return isRenameSuccessful;
	}

	private void setNewPathsForChildrens(TreeItem<PathItem> renamedItem, File oldFile, File newFile) {
		System.out.println(1);
		String oldFileToString = oldFile.getAbsolutePath();
		String newFileToString = newFile.getAbsolutePath();
		
        for (TreeItem<PathItem> item : renamedItem.getChildren()) {
        	
        	if(!item.isLeaf()) {
        		setNewPathsForChildrens(item, oldFile, newFile);
        	}
      
        	String childToString = item.getValue().getPath().toString();       	
        	String newChild = childToString.replace(oldFileToString, newFileToString);       	
        	item.getValue().setPath(Paths.get(newChild));
        	
			System.out.println(newChild);
			
			
		}

	}
	
	
	// Getter
	public File getNewFile() {return newFile;}

	
}
