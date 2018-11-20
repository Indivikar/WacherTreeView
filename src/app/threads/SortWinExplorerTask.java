package app.threads;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import app.TreeViewWatchService.PathItem;
import app.controller.CTree;
import app.loadTime.LoadTime.LoadTimeOperation;
import app.sort.WindowsExplorerComparator;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

public class SortWinExplorerTask extends Task<Void> {

	private TreeItem<PathItem> children;
	
	public SortWinExplorerTask(TreeItem<PathItem> children) {
		this.children = children;	
	}

	@Override
	protected Void call() throws Exception {
		
		long start1 = new Date().getTime();
		sortTreeItems(children);
		long runningTime1 = new Date().getTime() - start1;			
		CTree.listLoadTime.add(new LoadTimeOperation("sortTreeItems()", runningTime1 + "", ""));
		
		return null;
	}

	public void sortTreeItems(TreeItem<PathItem> item) {

//		SortWinExplorerTask task = new SortWinExplorerTask(item.getChildren());
//		Thread thread = new Thread(task);
//		thread.start();
		
		sortMyListRecursive(item.getChildren());
		
		  for (TreeItem<PathItem> child : item.getChildren()) {
			  if (!child.isLeaf() && child.getValue().getPath().toFile().isDirectory()) {
				  sortTreeItems(child);
			  }
		  }
	}
	
	 public static void sortMyListRecursive(ObservableList<TreeItem<PathItem>> children) {

	        Collections.sort(children, new Comparator<TreeItem<PathItem>>() {
	            private final Comparator<PathItem> NATURAL_SORT = new WindowsExplorerComparator();

	            @Override
	            public int compare(TreeItem<PathItem> o1, TreeItem<PathItem> o2) {;
	                return NATURAL_SORT.compare(o1.getValue(), o2.getValue());
	            }
	        });
	    }
	
}
