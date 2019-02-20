package app.view.Stages;

import java.awt.MouseInfo;
import java.awt.Point;

import app.TreeViewWatchService.DragNDropInternal;
import app.controller.CTree;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StageMoveOrCopy {

	public StageMoveOrCopy(CTree cTree, DragNDropInternal dragNDropInternal) {
		Point p = MouseInfo.getPointerInfo().getLocation();
		
		Stage stage = new Stage();
		
		AnchorPane root = new AnchorPane();
		
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initStyle(StageStyle.UTILITY);
		stage.resizableProperty().setValue(Boolean.FALSE);
//		stage.initStyle(StageStyle.UNDECORATED);
		stage.initOwner(cTree.getPrimaryStage());
		
		Scene scene = new Scene(root, 100, 80);
		
		VBox vBox = new VBox();
		AnchorPane.setTopAnchor(vBox, 10.0);
		AnchorPane.setLeftAnchor(vBox, 10.0);
		AnchorPane.setRightAnchor(vBox, 10.0);
		AnchorPane.setBottomAnchor(vBox, 10.0);
		vBox.setAlignment(Pos.CENTER);
		vBox.setSpacing(10);
		
		Button buttonCopy = new Button("Copy");
		buttonCopy.setOnAction(e -> {
			dragNDropInternal.getCopyOrMoveTask().setMove(false);
			stage.close();
		});

		Button buttonMove = new Button("Move");
		buttonMove.setOnAction(e -> {
			dragNDropInternal.getCopyOrMoveTask().setMove(true);
			stage.close();
		});
		
		stage.setOnCloseRequest(e -> {
			stage.close();
			System.out.println("Close");
		});
		
		// Fügen den Button zu unserem StackPane (Fenster) hinzu
		vBox.getChildren().addAll(buttonCopy, buttonMove);
		root.getChildren().addAll(vBox);

		// nun Setzen wir die Scene zu unserem Stage und zeigen ihn an
		stage.setScene(scene);
		stage.setX(p.getX());
		stage.setY(p.getY() + 30.0);
		stage.showAndWait();
		
		
	}

}
