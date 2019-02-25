package app.view.Stages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import app.StartWacherDemo;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CRename;
import app.controller.CTree;
import app.interfaces.ILockDir;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class StageRename implements ILockDir {

	private CRename controller;
	
	
	
	public StageRename(CTree cTree, Stage primaryStage, PathTreeCell pathTreeCell) {
		Stage stage = new Stage();
		FXMLLoader loader = new FXMLLoader(StartWacherDemo.class.getResource("view/fxml/rename.fxml"));			
		AnchorPane root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Scene scene = new Scene(root);

		controller = loader.getController();
		System.err.println("Rename50: " + pathTreeCell.getTreeItem().getValue().getPath());
		controller.set(this, cTree, primaryStage, stage, pathTreeCell, pathTreeCell.getTreeItem());
		
		stage.setOnCloseRequest(e -> {
			unlockDir(cTree.getLockFileHandler(), pathTreeCell.getTreeItem().getValue().getLevelOneItem());
		});
		
		stage.setTitle("Rename");
		stage.setScene(scene);
		File f = new File("C:\\Users\\DH\\AppData\\Roaming\\IndivikarAG\\dev");
		if (f.exists()) {
			stage.setX(6800);
		}
		stage.show();
	}

}
