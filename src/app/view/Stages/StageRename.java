package app.view.Stages;

import java.io.IOException;
import java.nio.file.Path;

import app.StartWacherDemo;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CRename;
import app.controller.CTree;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class StageRename {

	private CRename controller;
	
	
	
	public StageRename(Stage primaryStage, PathTreeCell pathTreeCell) {
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
		controller.set(this, primaryStage, stage, pathTreeCell);
		
		
		stage.setTitle("Rename");
		stage.setScene(scene);
		stage.setX(6800);
		stage.show();
	}

}
