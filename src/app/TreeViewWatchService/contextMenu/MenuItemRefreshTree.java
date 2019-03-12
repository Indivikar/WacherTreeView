package app.TreeViewWatchService.contextMenu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.apache.commons.io.FileUtils;

import app.StartWacherDemo;
import app.TreeViewWatchService.FileAlterationListenerImpl;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import app.interfaces.ILockDir;
import app.interfaces.IOpenFile;
import app.interfaces.ITreeUpdateHandler;
import app.view.alerts.AlertFilesLocked;
import javafx.scene.control.MenuItem;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;

public class MenuItemRefreshTree extends MenuItem implements IOpenFile, ILockDir, ITreeUpdateHandler{

	private PathTreeCell pathTreeCell;
	private ObservableList<String> listAllLockedFiles;
	
	public MenuItemRefreshTree(PathTreeCell pathTreeCell, CTree cTree) {
		  this.pathTreeCell = pathTreeCell;
		  this.listAllLockedFiles = listAllLockedFiles;
		
		  setText("refresh Tree");
	      setOnAction((event) -> {	 
	    	  	delAllLockFiles(cTree); // es werden nur die LockFiles gelöscht, die kein Besitzer mehr haben

	    	  	// Update
		    	if (isSomeDirLockedOnServer(cTree)) {
		    		// wenn Ordner gelockt sind, dann nur die vorhandene DB einlesen
		    		System.out.println("es sind noch files gelockt");
					cTree.refreshTree(false);
		    	} else {
		    		// wenn Ordner nicht gelockt sind, dann DB neu anlegen lassen und komplett-Refresh
		    		cTree.getTree().getRoot().getChildren().clear();
		    		cTree.showWebViewLoading();
		    		refreshServerPathList(cTree);	    		
		    	}   	  
	      });
	} 
}
