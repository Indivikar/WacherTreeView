package app.interfaces;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import app.functions.LoadTime;
import app.view.alerts.DefaultAlert;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import javafx.stage.Window;

public interface ITreeItemMethods {

    public default TreeItem<PathItem> getExistsItem(TreeItem<PathItem> root, TreeItem<PathItem> newItem){    
    	String newItemString = newItem.getValue().getPath().toString();
    	return getExistsItem(root, newItemString);
    }
    
    public default TreeItem<PathItem> getExistsItem(TreeItem<PathItem> root, Path path){
    	return getExistsItem(root, path.toString());
    }
	
    public default TreeItem<PathItem> getExistsItem(TreeItem<PathItem> root, String newItemString){

        for(TreeItem<PathItem> subItem : root.getChildren()){
        	if (subItem.getValue().getPath().toString().equalsIgnoreCase(newItemString)) {      		      		
        		return subItem;
			}        
        }
        return null;
    }

    public default TreeItem<PathItem> getItemSearchRecrusive(TreeItem<PathItem> root, String newItemString){

        for(TreeItem<PathItem> subItem : root.getChildren()){
        	if (subItem.getValue().getPath().toString().equalsIgnoreCase(newItemString)) {      		      		
        		return subItem;
			} else {
				getItemSearchRecrusive(subItem, newItemString);
			}      
        }
        return null;
    }
    
    public default void searchAndSelectItem(TreeView<PathItem> treeView, TreeItem<PathItem> root, String newItemString){

        for(TreeItem<PathItem> subItem : root.getChildren()){
        	
        	if (subItem.getValue().getPath().toString().equalsIgnoreCase(newItemString)) {  
        		System.out.println("foundedItem: " + subItem);
        		selectItem(treeView, subItem);   
//        		subItem.setExpanded(true);
//        		treeView.getSelectionModel().select(subItem);
        		return;
			} else {				
				searchAndSelectItem(treeView, subItem, newItemString);
			}      	
        }        
    }
    
    public default void selectItem(TreeView<PathItem> treeView, TreeItem<PathItem> item) {
    	if (item != null && treeView != null) {
    		System.err.println("selectedItem -> " + item.getValue().getPath());
    		expandPath(treeView, item);
			int row = treeView.getRow(item);	
//			System.err.println("selectedItem row -> " + row + " -> " + treeView.getTreeItem(row).getValue().getPath());
			treeView.getSelectionModel().clearSelection();
			treeView.getSelectionModel().select(row);
//			treeView.getSelectionModel().select(item);
			treeView.scrollTo(row);
			treeView.refresh();
		}
	}
    
    public default void expandPath(TreeView<PathItem> treeView, TreeItem<PathItem> item) {
    	if (item != null && treeView != null) {
    		Path path = item.getValue().getPath();
    		int countNames = path.getNameCount();
    		
    		List<TreeItem<PathItem>> parents = new ArrayList<TreeItem<PathItem>>();

    		for (int i = 0; i < countNames; i++) {
    			item = item.getParent();
    			if (item != null) {
					parents.add(item);   	
				}   					
			}
    		
    		Collections.reverse(parents);
    		
    		for (TreeItem<PathItem> treeItem : parents) {
				System.out.println(treeItem.getValue().getPath());
				treeItem.setExpanded(true);
			}

		}
	}
    
    public default void selectItemSearchInTreeView(TreeView<PathItem> treeView, TreeItem<PathItem> item, String newItemString) {
    	LoadTime.Start();
    	searchAndSelectItem(treeView, treeView.getRoot(), newItemString);
    	LoadTime.Stop("selectItemSearchInTreeView()", "");
	}
    
    public default boolean isOnlyOneItemSelected(Stage stageMain, PathTreeCell pathTreeCell) {
    	int i = pathTreeCell.getTreeView().getSelectionModel().getSelectedItems().size();
    	if (i == 1) {
    		return true; 
		} else {
			new DefaultAlert(stageMain, AlertType.WARNING, "Achtung", "Bitte nur eine Markierung setzen.");
			return false;
		}   	
	}
}
