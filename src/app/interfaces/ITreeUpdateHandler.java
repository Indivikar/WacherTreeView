package app.interfaces;

import java.io.File;
import java.io.IOException;

import app.TreeViewWatchService.CreateTree;
import app.controller.CTree;

public interface ITreeUpdateHandler {

	public default void wantUpdateTree (boolean b) {
		CreateTree.wantUpdateTree = b;
	}
	
//	public default void deleteServerRefreshFile(CTree cTree) {
//		File f = new File(cTree.getMainDirectory() + File.separator + CTree.refreshFileName);
//		if (f.exists()) {
//			f.delete();
//		}
//	}
	
	public default void refreshServerPathList(CTree cTree) {
		File f = new File(cTree.getMainDirectory() + File.separator + CTree.refreshFileName);
//		System.out.println(f);
		try {		
			if (!f.exists()) {
				f.createNewFile();
			}			
			Thread.sleep(500);
			if (f.exists()) {
				f.delete();
			}			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
