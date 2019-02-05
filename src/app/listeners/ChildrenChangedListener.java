package app.listeners;

import app.TreeViewWatchService.PathItem;
import app.controller.CTree;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;

public class ChildrenChangedListener {

	private CTree cTree;
	
	private boolean selectNewDirectoy = false; // Select a new directory when it is added from the context menu
	
	public ChildrenChangedListener(CTree cTree) {
		this.cTree = cTree;
	}
	
	
	public final ListChangeListener<TreeItem<PathItem>> listener = new ListChangeListener<TreeItem<PathItem>>() {
		@Override
		public void onChanged(ListChangeListener.Change<? extends TreeItem<PathItem>> change) {
	            while (change.next()) {
	                if (change.wasAdded() || change.wasRemoved()) {
	//	                    count.invalidate();
	
	                }
	                if (change.wasAdded()) {
	                    for (TreeItem<PathItem> item : change.getAddedSubList()) {
	                    	                  	
	                        item.getChildren().addListener(listener);
//	                        System.out.println("childrenChanged: " + item.getValue().getPath() + " (" + selectNewDirectoy + ")"); 
		                        if (selectNewDirectoy) {
		                        	System.out.println("childrenChanged: " + item.getValue().getPath() + " (" + item.getValue().getRow() + ")");  
									cTree.getTree().getSelectionModel().select(item);
									selectNewDirectoy = false;
								}	                        
	                    }
	                } else if (change.wasRemoved()) {
	                    for (TreeItem<PathItem> item : change.getRemoved()) {
	                        item.getChildren().removeListener(listener);
	                    }
	                }
	            }		 
		}
	};
	
	public boolean isSelectNewDirectoy() {return selectNewDirectoy;}

	public void setSelectNewDirectoy(boolean selectNewDirectoy) {this.selectNewDirectoy = selectNewDirectoy;}
	
	
}
