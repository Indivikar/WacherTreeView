package app.view.Stages;

import java.io.IOException;
import java.nio.file.Path;

import app.StartWacherDemo;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CDelete;
import app.controller.CRename;
import app.controller.CTree;
import app.interfaces.ILockDir;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StageDelete implements ILockDir {

	private CDelete controller;
	
	public StageDelete(Stage primaryStage, CTree cTree, PathTreeCell pathTreeCell, ObservableList<String> listAllLockedFiles) {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader loader = new FXMLLoader(StartWacherDemo.class.getResource("view/fxml/delete.fxml"));			
		AnchorPane root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Scene scene = new Scene(root);
		
		
		controller = loader.getController();
		controller.set(cTree, this, primaryStage, stage, pathTreeCell, listAllLockedFiles);
		
		stage.setOnCloseRequest(e -> {
			unlockDir(cTree.getLockFileHandler(), pathTreeCell.getTreeItem().getValue().getLevelOneItem());
		});
		
		stage.setTitle("Delete");
		stage.setScene(scene);
		stage.setX(6800);
		stage.showAndWait();
	}

}
