package app.TreeViewWatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchKey;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


import app.controller.CTree;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ServiceAddItems extends Service<Void>  {

	private String klasse = "\n(app.service.admin.ServiceAllesSpeichern)";
	private TreeItem<PathItem> pathTreeItem;
	private TreeItem<PathItem> rootTreeItem;
	
	public ServiceAddItems() {
		
	}


	@Override
      protected void succeeded() {
		  reset();
      }

      @Override
      protected void failed() {
    	  reset();
      }

      @Override
      protected void cancelled() {
    	  reset();
      }

      @Override
      protected Task<Void> createTask() {
          return new Task<Void>() {
              @Override
              protected Void call() throws Exception {

            	  System.out.println("Start Service and Wait");
            	  Thread.sleep(1000);
            	  System.out.println("End Service");
            	  
            	  this.cancel();
            	  return null;
              };
          };
     }

      public void startAdd(TreeItem<PathItem> pathTreeItem, TreeItem<PathItem> rootTreeItem) {
    	  this.pathTreeItem = pathTreeItem;
    	  this.rootTreeItem = rootTreeItem;    	  
    	  this.restart();
      }
      
  	private void addNewNode(TreeItem<PathItem> pathTreeItem, TreeItem<PathItem> rootTreeItem) {
		String pathNewItem = pathTreeItem.getValue().getPath().toString();
		String rootItem = rootTreeItem.getValue().getPath().toString();
			
		System.out.println("pathNewItem: " + pathNewItem + " == rootItem: " + rootItem);


//		listeAlleOrdner.clear();
//		populateMap(rootTreeItem);
//		
//		// gibt es im Tree, das Item schon, wenn ja -> abbrechen
//		for(Entry<Path, TreeItem<PathItem>> entry: listeAlleOrdner.entrySet()) {	
//			
//			  if (entry.getKey().toString().equalsIgnoreCase(pathNewItem)) {
//				  System.err.println("Item gibt es schon: " + entry.getKey() + " -> " + entry.getValue());
//				  return;
//			  }
//		}
		
		
		
		
		ObservableList<TreeItem<PathItem>> newList = 
				rootTreeItem.getChildren().stream()
		            .filter(x -> x.getValue().getPath().toString().equalsIgnoreCase(pathNewItem))
		            .collect(Collectors.toCollection(FXCollections::observableArrayList));

		System.out.println("newList.size() 1: " + newList.size());
		
		if (newList.size() == 0) {
			addTreeItems(rootTreeItem);
		}
		
		for (TreeItem<PathItem> item : newList) {
			System.out.println("gefundenes item: " + item.getValue().getPath().toFile());	
			addTreeItems(item);
//			item.getChildren().add(e);
		}

	}

	private void addTreeItems(TreeItem<PathItem> item) {

        Path rootPath = item.getValue().getPath();
        PathItem pathItem = new PathItem(rootPath);
        TreeItem<PathItem> treeItem = new TreeItem<PathItem>(pathItem);
        
        File treeItemFile =  treeItem.getValue().getPath().toFile();
        
        if (treeItemFile.exists()) {
			CTree.createTree(treeItem, true);    
			
			item.getChildren().clear();
			System.out.println("addTreeItems in Item: " + item + " -> " + treeItem);
			item.getChildren().addAll(treeItem.getChildren());
//			tree.refresh();			
		}

	}
  	
     // Getter
      
      
}
