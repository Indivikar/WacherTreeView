package app.view.Stages;

import java.awt.MouseInfo;
import java.awt.Point;

import app.TreeViewWatchService.CreateTree;
import app.TreeViewWatchService.DragNDropInternal;
import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import app.interfaces.ILockDir;
import app.interfaces.ITreeItemMethods;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StageMoveOrCopy implements ILockDir, ITreeItemMethods{

	private Stage stage;
	private CTree cTree;
	private PathTreeCell cell;
//	private DragNDropInternal dragNDropInternal;
	private TreeItem<PathItem> treeItem;
	private TreeItem<PathItem> levelOneItem;
	
	
	public StageMoveOrCopy(CTree cTree, DragNDropInternal dragNDropInternal) {
		this.cTree = cTree;
//		this.cell = cell;
//		this.treeItem = cell.getTreeItem();
//		Point p = MouseInfo.getPointerInfo().getLocation();
		
		stage = new Stage();
		
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
		
		
		
		Button buttonCopy = new Button("Copy 1");
		buttonCopy.setOnAction(e -> {
			dragNDropInternal.getCopyOrMoveTask().setMove(false);

			// Tree nur updaten, wenn das LevelOne Node leer ist, weil sonst das Lock-Icon nicht auf unlock wechselt, wenn das LevelOne Node leer ist
			updateTreeIfLevelOneNodeEmpty(cTree, levelOneItem);
			
//			stage.close();
			stage.hide();
		});

		Button buttonMove = new Button("Move 1");
		buttonMove.setOnAction(e -> {
			dragNDropInternal.getCopyOrMoveTask().setMove(true);
			
			// Tree nur updaten, wenn das LevelOne Node leer ist, weil sonst das Lock-Icon nicht auf unlock wechselt, wenn das LevelOne Node leer ist
			updateTreeIfLevelOneNodeEmpty(cTree, levelOneItem);
			
//			stage.close();
			stage.hide();
		});
		
		stage.setOnCloseRequest(e -> {
			dragNDropInternal.setCancelCopyRoutine(true);

			unlockUpdate();
			
//			unlockDir(cTree.getLockFileHandler(), levelOneItem);
//			
//			// Tree nur updaten, wenn das LevelOne Node leer ist, weil sonst das Lock-Icon nicht auf unlock wechselt, wenn das LevelOne Node leer ist
//			updateTreeIfLevelOneNodeEmpty(cTree, levelOneItem);
			
//			stage.close();
			stage.hide();
			System.out.println("Close");
		});
		
		
		
		// Fügen den Button zu unserem StackPane (Fenster) hinzu
		vBox.getChildren().addAll(buttonCopy, buttonMove);
		root.getChildren().addAll(vBox);

		// nun Setzen wir die Scene zu unserem Stage und zeigen ihn an
		stage.setScene(scene);
//		stage.setX(p.getX());
//		stage.setY(p.getY() + 30.0);
//		stage.showAndWait();
		
	}
		
	public void show(PathTreeCell cell) {
//		this.cTree = cTree;
		this.cell = cell;
		this.treeItem = cell.getTreeItem();		
		this.levelOneItem = cell.getTreeItem().getValue().getLevelOneItem();
		
		Point p = MouseInfo.getPointerInfo().getLocation();
		stage.setX(p.getX());
		stage.setY(p.getY() + 30.0);
		stage.showAndWait();
	}
	
	public void hide() {	
		stage.hide();
	}
		
	private void unlockUpdate() {
		Platform.runLater(() -> {

				unlockDir(cTree.getLockFileHandler(), treeItem.getValue().getLevelOneItem());
				unlockDir(cTree.getLockFileHandler(), cTree.getSelectedItems().get(0).getValue().getLevelOneItem());
				// Tree nur updaten, wenn das LevelOne Node leer ist, weil sonst das Lock-Icon nicht auf unlock wechselt, wenn das LevelOne Node leer ist
//				updateTreeIfLevelOneNodeEmpty(cTree, treeItem.getValue().getLevelOneItem());
//				updateTreeIfLevelOneNodeEmpty(cTree, cTree.getSelectedItems().get(0).getValue().getLevelOneItem());
				cell.getTreeView().refresh();
		});	
	}
		
	// Getter 
	public Stage getStage() {return stage;}	
	
//	private void updateTree(CTree cTree, TreeItem<PathItem> levelOneItem) {
//		// Tree nur updaten, wenn das LevelOne Node leer ist, weil sonst das Lock-Icon nicht auf unlock wechselt, wenn das LevelOne Node leer ist
//		int levelOneSize = levelOneItem.getChildren().size();			
//		if(levelOneSize == 0) {
//			cTree.refreshTree(true);
//		}
//	
//	}
	
	
}
