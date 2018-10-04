package app.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import app.TreeViewWatchService.PathTreeCell;
import app.interfaces.ISuffix;
import app.view.Stages.StageRename;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CRename implements Initializable, ISuffix{

	private StageRename stageRename;
	private Stage stage;
	private PathTreeCell pathTreeCell;
	private File cellFile;
	private String suffix;
	private String name;
	
	@FXML private TextField textFieldName;
	@FXML private Label labelMassage;
	@FXML private Button buttonOK;
	@FXML private Button buttonCancel;
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		buttonAction();

		labelMassage.setText("");
		buttonOK.setDisable(true);
	}

	private void textFieldListener() {
		textFieldName.textProperty().addListener((arg, oldVal, newVal) -> {
			File file = null;
			if (cellFile.isDirectory()) {
				file = new File(cellFile.getParentFile() + File.separator + newVal);
			} else {
				file = new File(cellFile.getParentFile() + File.separator + newVal + "." + suffix);
			}
			
			if (file.exists()) {
				labelMassage.setText("Name is exists!");
				buttonOK.setDisable(true);
			} else {
				labelMassage.setText("");
				buttonOK.setDisable(false);
			}
			
		});
		
	}

	private void buttonAction() {
		buttonOK.setOnAction(e -> {
			
			boolean isRenameSuccessful = false;
			if (cellFile.isDirectory()) {
				isRenameSuccessful = rename(cellFile.getParentFile(), cellFile.getName(), textFieldName.getText());
			} else {
				isRenameSuccessful = rename(cellFile.getParentFile(), cellFile.getName(), textFieldName.getText() + "." + suffix);
			}
			
			if (isRenameSuccessful) {
				stage.close();
			} 			
		});

		buttonCancel.setOnAction(e -> {
			stage.close();
		});
	}
	
	
	private boolean rename(File path, String oldName, String newName) {
		
		File oldFile = new File(path + File.separator + oldName);
		File newFile = new File(path + File.separator + newName);	
				
		boolean isRenameSuccessful = oldFile.renameTo(newFile);
		
	    if (isRenameSuccessful) {
	        System.out.println("renamed");
	        labelMassage.setText("");
	      } else {
	        System.out.println("Error");
	        labelMassage.setText("error when rename");
	      }
	    
		return isRenameSuccessful;
	}
	
	
    // Getter
	
   
	public void set(StageRename stageRename, Stage stage, PathTreeCell pathTreeCell) {
		this.stageRename = stageRename;
		this.stage = stage;	
		this.pathTreeCell = pathTreeCell;
		this.cellFile = pathTreeCell.getItem().getPath().toFile();
		this.name = suffixRemove(cellFile.getName());
		this.suffix = ISuffix.getSuffix(cellFile.getName());

		if (cellFile.isDirectory()) {
			textFieldName.setText(cellFile.getName());
		} else {
			textFieldName.setText(name);
		}
		
		textFieldListener();
		
	}

}
