package app.threads;

import java.io.File;
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
import app.interfaces.ILockDir;
import app.interfaces.ITreeItemMethods;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

public class WaitUnlockTask extends Task<Void> implements ILockDir {

	private TreeItem<PathItem> treeItem;
	private File file;
	
	public WaitUnlockTask(TreeItem<PathItem> treeItem, File file) {
		this.treeItem = treeItem;
		this.file = file;
	}

	@Override
	protected void cancelled() {

	}
	
	@Override
	protected void failed() {

	}
	
	@Override
	protected void succeeded() {   	
		isDirLocked(treeItem, file);
	}
	
	@Override
	protected Void call() throws Exception {
		
		Thread.sleep(3000);
		return null;
	}



	
}
