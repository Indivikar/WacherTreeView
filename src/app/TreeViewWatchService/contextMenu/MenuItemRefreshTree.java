package app.TreeViewWatchService.contextMenu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.apache.commons.io.FileUtils;

import app.TreeViewWatchService.FileAlterationListenerImpl;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import app.interfaces.IOpenFile;
import app.view.alerts.AlertFilesLocked;
import javafx.scene.control.MenuItem;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;

public class MenuItemRefreshTree extends MenuItem implements IOpenFile{

	private PathTreeCell pathTreeCell;
	private ObservableList<String> listAllLockedFiles;
	
	public MenuItemRefreshTree(PathTreeCell pathTreeCell, CTree cTree) {
		  this.pathTreeCell = pathTreeCell;
		  this.listAllLockedFiles = listAllLockedFiles;
		
		  setText("refresh Tree");
	      setOnAction((event) -> {	    	  	    	  
	    	  cTree.refreshTree();
	      });
	} 
}
