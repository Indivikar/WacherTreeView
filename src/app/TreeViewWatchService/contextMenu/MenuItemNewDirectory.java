package app.TreeViewWatchService.contextMenu;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import app.TreeViewWatchService.FileAlterationListenerImpl;
import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import app.interfaces.ICursor;
import app.interfaces.ISaveExpandedItems;
import app.threads.NewDirectoryTask;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

public class MenuItemNewDirectory extends MenuItem implements ISaveExpandedItems, ICursor{

	public MenuItemNewDirectory(Stage primaryStage, CTree cTree, PathTreeCell pathTreeCell) {
		  setText("new directory");
	      setOnAction(new EventHandler<ActionEvent>() {
	          @Override
	          public void handle(ActionEvent t) {
	        	  
//	        	  addAllExpandedItems(pathTreeCell.getTreeView().getRoot());
	        	  
	        	  FileAlterationListenerImpl.isInternalChange = true;
	        	  NewDirectoryTask newDirectoryTask = new NewDirectoryTask(cTree, pathTreeCell);
	        	  bindUIandService(primaryStage, newDirectoryTask);
	        	  new Thread(newDirectoryTask).start();
	        	  	              
	          }	          
	      });
	}

	@Override
	public void addAllExpandedItems() {
		// TODO Auto-generated method stub
		
	}

}
