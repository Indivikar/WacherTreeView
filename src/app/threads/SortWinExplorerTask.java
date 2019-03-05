package app.threads;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import app.TreeViewWatchService.PathItem;
import app.controller.CTree;
import app.functions.LoadTime;
import app.loadTime.LoadTime.LoadTimeOperation;
import app.interfaces.ITreeItemMethods;
import app.sort.WindowsExplorerComparator;
import app.view.alerts.CopyDialogProgress;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

public class SortWinExplorerTask extends Task<Void> implements ITreeItemMethods {

	private CTree cTree;
	private CopyDialogProgress pForm;
	private TreeItem<PathItem> children;
	private String newItem;
	
	public SortWinExplorerTask(CTree cTree, TreeItem<PathItem> children) {
		this.cTree = cTree;
		this.children = children;
//		new SortWinExplorerTask(cTree, children, null);
	}
	
	public SortWinExplorerTask(CTree cTree, TreeItem<PathItem> children, String newItem) {
		System.out.println("1 -> Sort WinExplorer Task: " + children);
		this.cTree = cTree;
		this.children = children;
		this.newItem = newItem;
//		new SortWinExplorerTask(cTree, null, children, newItem);
	}

	public SortWinExplorerTask(CTree cTree, CopyDialogProgress pForm, TreeItem<PathItem> children, String newItem) {
		System.out.println("2 -> Sort WinExplorer Task: " + children);
		this.cTree = cTree;
		this.pForm = pForm;
		this.children = children;
		this.newItem = newItem;
	}
	
	@Override
	protected void succeeded() {
	   System.out.println("Succeeded -> Sort WinExplorer Task");
 	   cTree.getScrollingByDragNDrop().stopScrolling();
       cTree.getTree().refresh();
       
//       System.err.println("=== get Index " +  cell.getTreeItem().getValue().getRow());
       	                           
       // Select new Item
       try {
			Thread.sleep(500);
		} catch (InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
       
       if (newItem != null) {
    	   selectItemSearchInTreeView(cTree.getTree(), children, newItem);
       }
       
       
       System.out.println("   Ende Sort");
       if (pForm != null) {
    	   pForm.getDialogStage().close();
       }
       
//       doTaskEventCloseRoutine(copyTask);
	}
	
	
	@Override
	protected Void call() throws Exception {
	
		System.out.println("Start 1 -> Sort WinExplorer Task: " + this.children);
		LoadTime.Start();
//		long start1 = new Date().getTime();
		sortTreeItems(children);
//		long runningTime1 = new Date().getTime() - start1;			
		LoadTime.Stop("sortTreeItems()", children.getValue().getPath().toString());
//		CTree.listLoadTime.add(new LoadTimeOperation("sortTreeItems()", runningTime1 + "", children.getValue().getPath().toString()));
		
		return null;
	}

	public void sortTreeItems(TreeItem<PathItem> item) {

//		SortWinExplorerTask task = new SortWinExplorerTask(item.getChildren());
//		Thread thread = new Thread(task);
//		thread.start();
		
		sortMyListRecursive(item.getChildren());
		
		  for (TreeItem<PathItem> child : item.getChildren()) {
			  if (!child.isLeaf() && child.getValue().isDirectoryItem()) {
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
