package app.TreeViewWatchService.contextMenu;

import java.nio.file.Path;

import app.TreeViewWatchService.FileAlterationListenerImpl;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import app.interfaces.ILockDir;
import app.interfaces.ITreeItemMethods;
import app.view.Stages.StageRename;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class MenuItemRename extends MenuItem implements ITreeItemMethods {

	public MenuItemRename(CTree cTree, Stage primaryStage, PathTreeCell pathTreeCell) {
		  setText("Rename");
	      setOnAction((event) -> {	    
	    	  CTree.isInternalChange = true;	
	    	  if (isOnlyOneItemSelected(pathTreeCell)) {
	    		  Path filePath = pathTreeCell.getItem().getPath();
	    		  StageRename stageRename = new StageRename(cTree, primaryStage, pathTreeCell);
	    	  }
	      });
	}

}
