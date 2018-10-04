package app.interfaces;

import java.nio.file.Path;
import java.util.HashMap;


import app.TreeViewWatchService.PathItem;
import javafx.scene.control.TreeItem;

public interface ISaveExpandedItems {

	public HashMap<Path, TreeItem<PathItem>> saveExpandedItems = new HashMap<>();
	
    public default void addAllExpandedItems(TreeItem<PathItem> item){
//    	PathTreeCell.saveExpandedItems.clear();
    	if (item != null) {
	    	saveExpandedItems.clear();
	    	searchAllExpandedItems(item); 	
    	}
    }
	
    public default void searchAllExpandedItems(TreeItem<PathItem> item){
    	System.out.println("expanded Item: " + item.getValue().getPath());
        if(item.getChildren().size() > 0){
            for(TreeItem<PathItem> subItem : item.getChildren()){
            	if (subItem.isExpanded()) {
//            		PathTreeCell.saveExpandedItems.put(subItem.getValue().getPath(), subItem);
            		saveExpandedItems.put(subItem.getValue().getPath(), subItem);
	            	System.out.println("expanded Item: " + subItem.getValue().getPath());
	            	searchAllExpandedItems(subItem);
				}                   	
            }
        }
    } 
    
//    public static boolean expandAllSavedItems(TreeItem<PathItem> newItem){
//		for (Entry<Path, TreeItem<PathItem>> item : saveExpandedItems.entrySet()) {	
//			if (item.getKey().toString().equals(newItem.getValue().getPath().toString())) {
//				System.err.println("rootItem expand: " + newItem);
//				return true;
//			} 
//		}
//		return false;
//    } 
    
    
}
