package app.threads;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import app.TreeViewWatchService.CreateTree;
import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import app.functions.LoadTime;
import app.interfaces.ICursor;
import app.interfaces.ITreeItemMethods;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

public class TreeLoaderTask extends Task<Void> implements ICursor, ITreeItemMethods {

	private CTree cTree;
	private PathTreeCell pathTreeCell;
	private TreeItem<PathItem> treeItem;
	private TreeItem<PathItem> newItem;
	
	public TreeLoaderTask(CTree cTree, TreeItem<PathItem> treeItem) {
		this.pathTreeCell = pathTreeCell;
		this.treeItem = treeItem;
		this.cTree = cTree;
	}

	@Override
	protected void cancelled() {

	}
	
	@Override
	protected void failed() {

	}
	
	@Override
	protected void succeeded() {   	
	
	}
	
	@Override
	protected Void call() throws Exception {
		cTree.getCreateTree().updatePathListFormDB(treeItem, false, false);
		return null;
	}


}
