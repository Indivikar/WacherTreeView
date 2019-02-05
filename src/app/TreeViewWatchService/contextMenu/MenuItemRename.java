package app.TreeViewWatchService.contextMenu;

import java.nio.file.Path;

import app.TreeViewWatchService.FileAlterationListenerImpl;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import app.interfaces.ILockDir;
import app.view.Stages.StageRename;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class MenuItemRename extends MenuItem {

	public MenuItemRename(CTree cTree, Stage primaryStage, PathTreeCell pathTreeCell) {
		  setText("Rename");
	      setOnAction((event) -> {	    
	    	  CTree.isInternalChange = true;	
	    	  Path filePath = pathTreeCell.getItem().getPath();
	    	  StageRename stageRename = new StageRename(cTree, primaryStage, pathTreeCell);
	      });
	}

}
