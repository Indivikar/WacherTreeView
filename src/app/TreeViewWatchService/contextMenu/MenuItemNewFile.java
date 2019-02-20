package app.TreeViewWatchService.contextMenu;

import app.TreeViewWatchService.FileAlterationListenerImpl;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import app.interfaces.ITreeItemMethods;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

public class MenuItemNewFile extends MenuItem implements ITreeItemMethods{

	public MenuItemNewFile(PathTreeCell pathTreeCell) {
		setText("new File");
	    setOnAction(new EventHandler<ActionEvent>() {
	          @Override
	          public void handle(ActionEvent t) {
	        	  if (isOnlyOneItemSelected(pathTreeCell)) {
		        	  CTree.isInternalChange = true;
		        	  System.out.println("new File");	
	        	  }
	          }	         
	    });
	}

}
