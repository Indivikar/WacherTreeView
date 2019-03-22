package app.interfaces;

import java.io.File;
import java.io.IOException;

import app.TreeViewWatchService.CreateTree;
import app.controller.CTree;
import app.view.functions.notification.Notification;
import app.view.functions.notification.Notification.NotificationType;
import javafx.application.Platform;
import javafx.geometry.Pos;

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
		// Server-Tool anstossen, so das die DB neu erstellt wird
		File f = new File(cTree.getMainDirectory() + File.separator + CTree.refreshFileName);
//		System.out.println(f);
		try {		
			if (!f.exists()) {
				boolean b = f.createNewFile();
				if (b) {
				
				}
			}			
			Thread.sleep(500);
			if (f.exists()) {
				f.delete();
			}			
		} catch (IOException | InterruptedException e) {
			String text = "Die Datei \n\"" + f.getAbsolutePath() + "\"\n konnte nicht gefunden werden.";
			Notification.create().setTitle("Fehler").owner(cTree.getPrimaryStage()).setClass(getClass().getCanonicalName())
				.fehlerCode("000").type(NotificationType.ERROR).setLog().setAlert().setException(e).setText(text).start();			
			e.printStackTrace();
		}
	}
	
	public default void refreshTree(CTree cTree) {
				// nur von DB updaten
				Platform.runLater(() -> {
					cTree.refreshTree(true);
				});
	}
	
}
