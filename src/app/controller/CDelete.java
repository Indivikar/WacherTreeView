package app.controller;

import java.net.URL;
import java.util.ResourceBundle;

import app.TreeViewWatchService.PathTreeCell;
import app.interfaces.ICursor;
import app.interfaces.ILockDir;
import app.threads.DeleteItemTask;
import app.view.Stages.StageDelete;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CDelete implements Initializable, ICursor, ILockDir{

	private CTree cTree;
	private StageDelete stageDelete;
	private Stage primaryStage;
	private Stage stage;
	private PathTreeCell pathTreeCell;
	
	private ObservableList<String> listAllLockedFiles;
	
	private @FXML Button buttonOK;
	private @FXML Button buttonCancel;
	
	public CDelete() {

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		setAction();
	}

	private void setAction() {
		buttonOK.setOnAction(e -> {
    	    DeleteItemTask DeleteItemTask = new DeleteItemTask(cTree, pathTreeCell, listAllLockedFiles);
    	    bindUIandService(primaryStage, DeleteItemTask);
    	    new Thread(DeleteItemTask).start();
			stage.close();
		});
		
		buttonCancel.setOnAction(e -> {
			unlockDir(cTree.getLockFileHandler(), pathTreeCell.getTreeItem().getValue().getLevelOneItem());
			stage.close();
		});
		
	}

	public void set(CTree cTree, StageDelete stageDelete, Stage primaryStage, Stage stage, PathTreeCell pathTreeCell, ObservableList<String> listAllLockedFiles) {
		this.cTree = cTree;
		this.stageDelete = stageDelete;
		this.primaryStage = primaryStage;
		this.stage = stage;
		this.pathTreeCell = pathTreeCell;
		this.listAllLockedFiles = listAllLockedFiles;
		
		lockDir(cTree.getLockFileHandler(), 
				pathTreeCell.getTreeItem().getValue().getLevelOneItem());
	}

	
	
}
