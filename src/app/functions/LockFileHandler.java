package app.functions;

import app.TreeViewWatchService.PathItem;
import app.controller.CTree;
import app.interfaces.ISearchLockedFiles;
import app.models.MLockFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map.Entry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.TreeItem;

public class LockFileHandler implements ISearchLockedFiles {

	private ObservableMap<String, MLockFile> inputStreamList = FXCollections.observableHashMap();
	
	public LockFileHandler() {
		// TODO Auto-generated constructor stub
	}

	public void unlockLockfile(File f) {
		for (Entry<String, MLockFile> inputStream : inputStreamList.entrySet()) {
			System.out.println("Key unlockLockfile: " + inputStreamList.size());
			
			if(f.getName().equalsIgnoreCase(CTree.lockFileName)) {
				f = f.getParentFile();
			}
			
			System.out.println("Key unlockLockfile: " + inputStream.getKey() + " == " + f);
			if (inputStream.getKey().equalsIgnoreCase(f.getAbsolutePath())) {
				try {
					
					inputStream.getValue().getLock().release();
					inputStream.getValue().getRaf().close();
					inputStreamList.remove(inputStream.getKey());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void lockFile(Path path) {
		lockFile(path.toFile());
	}
	
	public void lockFile(String path) {
		lockFile(new File(path));
	}
	
	public void lockFile(File f) {
//		lockFile(path.getAbsolutePath(), inputStream);
		   
		try {
			FileChannel raf = new RandomAccessFile(f, "rw").getChannel();
			FileLock lock = raf.tryLock();	
			System.out.println("Key lockFile: " + f);
			addLockFile(f.getAbsolutePath(), raf, lock);
		} catch (IOException e) {
			e.printStackTrace();
		}
		   
	}
	
	private void addLockFile(String path, FileChannel raf, FileLock lock) {
		String parent = new File(path).getParent();
		inputStreamList.put(parent, new MLockFile(raf, lock));
	}
	
	public void deleteAllLockfiles(TreeItem<PathItem> rootItem) {
//		System.out.println("childs " + rootItem.getChildren().size());
		for (TreeItem<PathItem> childs : rootItem.getChildren()) {
			File f = childs.getValue().getPath().toFile();
//			System.out.println("deleteAllLockfiles: " + f);
			File[] files = f.listFiles();
			if (files != null) { // Erforderliche Berechtigungen etc. sind vorhanden
				for (int i = 0; i < files.length; i++) {
					
					if (!files[i].isDirectory() && files[i].getName().equalsIgnoreCase(CTree.lockFileName)) {
//						System.out.println("Delete Lockfile: " + files[i]);
//						visFile(files[i]);
						if (!isFileLocked(files[i])) {
							if(!files[i].delete()) {
								
							}
						}
					}
				}
			}
		}
	}
	
	private void hideFile(File f) {
		try {
			Files.setAttribute(f.toPath(), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void visFile(File f) {
		try {
			Runtime.getRuntime().exec("attrib -H " + f.getAbsolutePath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}