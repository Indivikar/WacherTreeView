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
import app.interfaces.ISaveExpandedItems;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

public class MenuItemNewDirectory extends MenuItem implements ISaveExpandedItems{

	public MenuItemNewDirectory(PathTreeCell pathTreeCell) {
		  setText("new directory");
	      setOnAction(new EventHandler<ActionEvent>() {
	          @Override
	          public void handle(ActionEvent t) {
	        	  
	        	  addAllExpandedItems(pathTreeCell.getTreeView().getRoot());
	        	  
	        	  FileAlterationListenerImpl.isInternalChange = true;
	              Path newDir = createNewDirectory();
	              if (newDir != null) {            	  
	            	  TreeItem<PathItem> newItem = new TreeItem<PathItem>(new PathItem(newDir));
	            	  CTree.createTree(newItem, false);
	              }
	              
	          }
	          
	          private Path createNewDirectory() {
	              Path newDir = null;
	              
	              while (true) {
	                  Path path = pathTreeCell.getTreeItem().getValue().getPath();
	                  newDir = Paths.get(path.toAbsolutePath().toString(), "new directory " + String.valueOf(pathTreeCell.getItem().getCountNewDir()));
	                  try {
	                      Files.createDirectory(newDir);
	                      break;
	                  } catch (FileAlreadyExistsException ex) {
	                      continue;
	                  } catch (IOException ex) {
	                	  pathTreeCell.cancelEdit();
//	                      messageProp.setValue(String.format("Creating directory(%s) failed", newDir.getFileName()));
	                      break;
	                  }
	              }
	                  return newDir;
	          }
	      });
	}

}
