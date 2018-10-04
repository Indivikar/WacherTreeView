package app.TreeViewWatchService.contextMenu;

import app.TreeViewWatchService.FileAlterationListenerImpl;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

public class MenuItemNewFile extends MenuItem{

	public MenuItemNewFile() {
		setText("new File");
	    setOnAction(new EventHandler<ActionEvent>() {
	          @Override
	          public void handle(ActionEvent t) {
	        	  FileAlterationListenerImpl.isInternalChange = true;
	        	  System.out.println("new File");	
	          }	         
	    });
	}

}
