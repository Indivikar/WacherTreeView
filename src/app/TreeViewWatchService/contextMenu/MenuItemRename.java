package app.TreeViewWatchService.contextMenu;

import java.nio.file.Path;

import app.TreeViewWatchService.FileAlterationListenerImpl;
import app.TreeViewWatchService.PathTreeCell;
import app.view.Stages.StageRename;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class MenuItemRename extends MenuItem{

	public MenuItemRename(Stage primaryStage, PathTreeCell pathTreeCell) {
		  setText("Rename");
	      setOnAction((event) -> {
	    	  FileAlterationListenerImpl.isInternalChange = true;
	    	  Path filePath = pathTreeCell.getItem().getPath();
	    	  StageRename stageRename = new StageRename(primaryStage, pathTreeCell);

	      });
	}

}
