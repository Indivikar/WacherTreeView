package app.interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import app.functions.LockFileHandler;
import javafx.scene.control.TreeItem;

public interface ILockDir {

	
	   public default boolean lockDir(LockFileHandler lockFileHandler, TreeItem<PathItem> levelOneItem) {
		   try {
			   File f = new File(levelOneItem.getValue().getPath() + File.separator + CTree.lockFileName);
			   if (!f.exists()) {
				   boolean b =  f.createNewFile();
//				   	BufferedReader br = new BufferedReader(new FileReader(f));
//				   	FileInputStream inputStream = new FileInputStream(f);
				   lockFileHandler.lockFile(f);
			        //set hidden attribute
//			        Files.setAttribute(f.toPath(), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
			        return b;
			   }			   
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    return false;
	   }
	
	   public default boolean unlockDir(LockFileHandler lockFileHandler, TreeItem<PathItem> levelOneItem) {
		   File f = new File(levelOneItem.getValue().getPath() + File.separator + CTree.lockFileName);
		   if (f.exists()) {
			   try {
				unlockLockFile(lockFileHandler, f);
				Files.deleteIfExists(f.toPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			   return f.delete();
		   }		   
		   return false;
	   }
	   
	   public default void unlockLockFile(LockFileHandler lockFileHandler, PathTreeCell pathTreeCell) {
		   File levelOneDir = pathTreeCell.getTreeItem().getValue().getLevelOneItem().getValue().getPath().toFile();
		   unlockLockFile(lockFileHandler, levelOneDir);
	   }
	   
	   public default void unlockLockFile(LockFileHandler lockFileHandler, File levelOneDirOrLockFilePath) {
		   lockFileHandler.unlockLockfile(levelOneDirOrLockFilePath);
	   }
	   
	   public default boolean setThisItemLocked(TreeItem<PathItem> rootItem, Path path, boolean isLocked) {

		   for (TreeItem<PathItem> item : rootItem.getChildren()) {
			   PathItem val = item.getValue();
			   if (val.getPath().equals(path)) {
				   
				   val.setLocked(isLocked);
				   
//				   if (CTree.isInternalChange) {		
//					   val.setLocked(isLocked);
//				   } else {
//					   	// beim Client den unlock verzögern, damit der Ordner erst frei gegeben wird, wenn der Tree refreshed wurde
//						if(item.getValue().isLocked() && !isLocked) {
//							System.out.println(5);
////							    try {
////								   Thread.sleep(3000);
////								} catch (InterruptedException e) {
////									e.printStackTrace();
////								}
//							   val.setLocked(isLocked);
//						} else {
//							val.setLocked(isLocked);
//						}
//				   }
			   }
		   }

			return false;
		}

	   
	   public default boolean isDirLocked(TreeItem<PathItem> rootItem, File dir) {

			File[] files = dir.listFiles();
			if (files != null) { // Erforderliche Berechtigungen etc. sind vorhanden
				for (int i = 0; i < files.length; i++) {				
					if (!files[i].isDirectory() && files[i].getName().equalsIgnoreCase(CTree.lockFileName)) {
						setThisItemLocked(rootItem, dir.toPath(), true);
						return true;
//						System.out.println("locked -> " + dir);
					} else {					
//						if(CTree.isInternalChange) {
							setThisItemLocked(rootItem, dir.toPath(), false);
//						}			
//						System.out.println("unlocked -> " + dir);
					}
				}				
			}
			return false;
		}
	
	   public default void changeIconWhenFolderLocked(TreeItem<PathItem> rootItem) {
		   System.out.println("changeIconWhenFolderLocked: " + rootItem.getChildren().size());
		   for (TreeItem<PathItem> item : rootItem.getChildren()) {
			   PathItem val = item.getValue();
			   System.out.println("changeIconWhenFolderLocked: " + val.getPath());
			   if (searchLockFile(val.getPath().toFile())) {
				   val.setLocked(true);
			   }
		   }
	   };
	   
	   
	   public default void childrenLockerAndUnlocker(TreeItem<PathItem> mainItem, boolean isLocked) {
		   for (TreeItem<PathItem> child : mainItem.getChildren()) {
			   PathItem val = child.getValue();
			   val.setLocked(isLocked);
			   childrenLockerAndUnlocker(child, isLocked);
		   }
	   };
	   
	   public default boolean searchLockFile(File file) {
		    File[] files = file.listFiles();
			if (files != null) { // Erforderliche Berechtigungen etc. sind vorhanden
				for (int i = 0; i < files.length; i++) {				
					if (!files[i].isDirectory() && files[i].getName().equalsIgnoreCase(CTree.lockFileName)) {
						return true;
					}				
				}
			}
			return false;
	   };
	   
}
