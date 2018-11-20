package app.threads;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import app.TreeViewWatchService.CreateTree;
import app.TreeViewWatchService.PathItem;
import app.controller.CTree;
import app.interfaces.ISaveSelectedItems;
import app.loadTime.LoadTime.LoadTimeOperation;
import app.sort.WindowsExplorerComparator;
import app.watcher.watchService.PAWatcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

public class AddTreeItems extends Task<Void> implements ISaveSelectedItems {
	private String pathFileDB;
	private CTree cTree;
	private PAWatcher paWatcher;
	private CreateTree createTree;

	private ObservableList<TreeItem<PathItem>> selectedItems = FXCollections.observableArrayList();

	public AddTreeItems(String pathFileDB, CTree cTree, PAWatcher paWatcher) {
		this.pathFileDB = pathFileDB;
		this.cTree = cTree;
		this.paWatcher = paWatcher;
		this.createTree = cTree.getCreateTree();
	}

	@Override
	protected void cancelled() {
		paWatcher.setAddTreeItems(new AddTreeItems(pathFileDB, cTree, paWatcher));
	}

	@Override
	protected void failed() {
		paWatcher.setAddTreeItems(new AddTreeItems(pathFileDB, cTree, paWatcher));
	}
	
	@Override
	protected void scheduled() {
		paWatcher.setAddTreeItems(new AddTreeItems(pathFileDB, cTree, paWatcher));
	}
	
	@Override
	protected void succeeded() {
		paWatcher.setAddTreeItems(new AddTreeItems(pathFileDB, cTree, paWatcher));
//		if (cTree.getTree().getRoot() != null) {
//			 select(selectedItems, cTree.getTree().getRoot(), cTree.getTree());
//		 }
	}
	
	@Override
	protected Void call() throws Exception {

//		saveAllSelectedItems();
		
		long start1 = new Date().getTime();
        createTree = new CreateTree(pathFileDB, cTree);
        createTree.updatePathListFormDB(CTree.treeItem, false, true);
//        createTree.startCreateTree(CTree.treeItem, false, true);
		long runningTime1 = new Date().getTime() - start1;			
		CTree.listLoadTime.add(new LoadTimeOperation("AddTreeItems()", runningTime1 + "", ""));
		
		// sort Items
        SortWinExplorerTask taskSort = new SortWinExplorerTask(cTree.getTree().getRoot());
        new Thread(taskSort).start();
		

        
		cTree.getTree().refresh();
		
		return null;
	}

	@Override
	public void saveAllSelectedItems() {
		selectedItems = addAllSelectedItems(cTree.getTree());
		for (TreeItem<PathItem> path : selectedItems) {
			System.err.println("selectedItems: " + path);
		}
		System.err.println("saveAllSelectedItems: " + selectedItems.size());
		
	}

}
