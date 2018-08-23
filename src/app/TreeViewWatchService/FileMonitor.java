package app.TreeViewWatchService;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import javafx.scene.control.TreeView;



public class FileMonitor {

	private TreeView<PathItem> tree;
	private final FileAlterationMonitor monitor;
    
	
	public FileMonitor(TreeView<PathItem> tree, String root, long intervalInMilliSec) {
		this.tree = tree;

		final File directory = new File(root);

		// Create a new FileAlterationObserver on the given directory
		FileAlterationObserver fao = new FileAlterationObserver(directory);
		
		// Create a new FileAlterationListenerImpl and pass it the previously created FileAlterationObserver
		fao.addListener(new FileAlterationListenerImpl(tree));

		// Create a new FileAlterationMonitor with the given pollingInterval period
		monitor = new FileAlterationMonitor(
				intervalInMilliSec);

		// Add the previously created FileAlterationObserver to FileAlterationMonitor
		monitor.addObserver(fao);

	}

	
	public void startFileMonitor() {
		try {
			// Start the FileAlterationMonitor
			monitor.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopFileMonitor() {
		try {
			// Start the FileAlterationMonitor			
			monitor.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
