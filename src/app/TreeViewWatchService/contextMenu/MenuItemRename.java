package app.TreeViewWatchService.contextMenu;

import java.nio.file.Path;

import app.TreeViewWatchService.FileAlterationListenerImpl;
import app.TreeViewWatchService.PathTreeCell;
import app.view.Stages.StageRename;
import javafx.scene.control.MenuItem;

public class MenuItemRename extends MenuItem{

	public MenuItemRename(PathTreeCell pathTreeCell) {
		  setText("Rename");
	      setOnAction((event) -> {
	    	  FileAlterationListenerImpl.isInternalChange = true;
	    	  Path filePath = pathTreeCell.getItem().getPath();
	    	  new StageRename(pathTreeCell);
	      });
	}

}
