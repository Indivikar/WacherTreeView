package app.interfaces;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map.Entry;

import app.TreeViewWatchService.PathItem;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public interface ISaveSelectedItems {

	public void saveAllSelectedItems();
	
    public default ObservableList<TreeItem<PathItem>> addAllSelectedItems(TreeView<PathItem> tree){
    	ObservableList<TreeItem<PathItem>> selectedItems = FXCollections.observableArrayList();
    	selectedItems.addAll(tree.getSelectionModel().getSelectedItems());
    	return selectedItems;
    }

    public default void select(ObservableList<TreeItem<PathItem>> selectedItems, TreeItem<PathItem> mainItem, TreeView<PathItem> tree) {
//    	System.out.println("selectedItems: " + selectedItems.size());
    	Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				
//				tree.getSelectionModel().clearSelection();
		    	for (TreeItem<PathItem> treeItem : mainItem.getChildren()) {
					for (int i = 0; i < selectedItems.size(); i++) {
						
						String selectedPath = selectedItems.get(i).getValue().getPath().toString();
						String newPath = treeItem.getValue().getPath().toString();
//						System.out.println(selectedPath + " == " + newPath);
						if (selectedPath.equalsIgnoreCase(newPath)) {
							MultipleSelectionModel<TreeItem<PathItem>> msm = tree.getSelectionModel();
							System.err.println("Select Item: " + treeItem.getValue().getPath());
							int row = tree.getRow(treeItem);
							msm.select(row);
							selectedItems.remove(i);
							System.err.println("Select Item: " + treeItem);
//							return true;
						}
					}
					
					
					if (!treeItem.isLeaf() && !selectedItems.isEmpty()) {
						select(selectedItems, treeItem, tree);
					}
					
					if (selectedItems.isEmpty()) {
						break;
					}
				}
			}
		});
    	
    }
    
    
    public default boolean selectSavedItem(TreeItem<PathItem> newItem, ObservableList<TreeItem<PathItem>> selectedItems, TreeView<PathItem> tree){
    	
//    	System.out.println(selectedItems.size() + " -> " + newItem.getValue().getPath());
    	if (selectedItems != null) {
			for (TreeItem<PathItem> item : selectedItems) {	
				String selectedPath = item.getValue().getPath().toString();
				String newPath = newItem.getValue().getPath().toString();

				if (selectedPath.equalsIgnoreCase(newPath)) {
					MultipleSelectionModel<TreeItem<PathItem>> msm = tree.getSelectionModel();
					int row = tree.getRow(newItem);
					msm.select(row);
					System.err.println("Select Item: " + newItem);
//					return true;
				} 
			}
		}

		return false;
    } 
	
}
