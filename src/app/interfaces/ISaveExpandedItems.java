package app.interfaces;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map.Entry;

import app.TreeViewWatchService.PathItem;
import javafx.scene.control.TreeItem;

public interface ISaveExpandedItems {

	public HashMap<Path, TreeItem<PathItem>> saveExpandedItems = new HashMap<>();
	
	public void addAllExpandedItems();
	
    public default HashMap<Path, TreeItem<PathItem>> addAllExpandedItems(TreeItem<PathItem> item){
//    	PathTreeCell.saveExpandedItems.clear();
    	if (item != null) {
	    	saveExpandedItems.clear();
	    	searchAllExpandedItems(item); 	
    	}
    	
    	return saveExpandedItems;
    }
	
    public default void searchAllExpandedItems(TreeItem<PathItem> item){
//    	System.out.println("expanded Item: " + item.getValue().getPath());
        if(item.getChildren().size() > 0){
            for(TreeItem<PathItem> subItem : item.getChildren()){
            	if (subItem.isExpanded()) {
//            		PathTreeCell.saveExpandedItems.put(subItem.getValue().getPath(), subItem);
            		saveExpandedItems.put(subItem.getValue().getPath(), subItem);
//	            	System.out.println("expanded Item: " + subItem.getValue().getPath());
	            	searchAllExpandedItems(subItem);
				}                   	
            }
        }
    } 
    
    public default boolean expandAllSavedItems(TreeItem<PathItem> newItem, HashMap<Path, TreeItem<PathItem>> expandedItems){
    	if (expandedItems != null) {
			for (Entry<Path, TreeItem<PathItem>> item : expandedItems.entrySet()) {	
				if (item.getKey().toString().equals(newItem.getValue().getPath().toString())) {
					System.err.println("rootItem expand: " + newItem);
					newItem.setExpanded(true);
					return true;
				} 
			}
		}

		return false;
    } 
    
    
}
