package app.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import app.TreeViewWatchService.CreateTree;
import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.interfaces.IBindings;
import app.interfaces.ILockDir;
import app.interfaces.ISuffix;
import app.interfaces.ITreeItemMethods;
import app.interfaces.IWindowEigenschaften;
import app.threads.RenameTask;
import app.threads.SortWinExplorerTask;
import app.view.Stages.StageRename;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

public class CRename implements Initializable, ISuffix, IBindings, ITreeItemMethods, ILockDir, IWindowEigenschaften{

	
	private StageRename stageRename;
	private CTree cTree;
	private Stage mainStage;
	private Stage stage;
	private PathTreeCell pathTreeCell;
	private TreeItem<PathItem> cellTreeItem;
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
			
//			boolean isRenameSuccessful = false;
//			if (cellFile.isDirectory()) {
//				isRenameSuccessful = rename(cellFile.getParentFile(), cellFile.getName(), textFieldName.getText());
//			} else {
//				isRenameSuccessful = rename(cellFile.getParentFile(), cellFile.getName(), textFieldName.getText() + "." + suffix);
//			}

			// unlock lockFile to change the name
//	        Task<Void> unLockTaskask = new Task<Void>() {
//	            @Override
//	            protected Void call() throws Exception {
//	                File levelOneDir = pathTreeCell.getTreeItem().getValue().getLevelOneItem().getValue().getPath().toFile();
//	                cTree.getLockFileHandler().unlockLockfile(levelOneDir);
//	                return null;
//	            }
//	        };
//			
//	        
//	        unLockTaskask.setOnSucceeded(evt -> {
			System.err.println("Rename61: " + cellTreeItem.getValue().getPath());
		        RenameTask renameTask = new RenameTask(this, cTree, pathTreeCell, cellTreeItem);		
				bindUIandService(mainStage, renameTask);
				
				renameTask.setOnSucceeded(ev -> {
					
			        SortWinExplorerTask task = new SortWinExplorerTask(pathTreeCell.getcTree(), pathTreeCell.getTreeItem().getParent());
			        bindUIandService(mainStage, task);
			        new Thread(task).start();
			        task.setOnSucceeded(event -> {
			        	selectItemSearchInTreeView(pathTreeCell.getTreeView(), pathTreeCell.getTreeItem(), renameTask.getNewFile().getAbsolutePath());		   		        	
			        });
				});
				
				new Thread(renameTask).start();
//	        });
//	        
//	        Thread t = new Thread(unLockTaskask);
//			t.setDaemon(true);
//			t.start();
//			if (RenameTask) {
//				stage.close();
//			} 			
		});

//		buttonCancel.setOnAction(e -> {		
//			System.out.println("pathTreeCell: " + pathTreeCell);
//			unlockDir(cTree.getLockFileHandler(), 
//					pathTreeCell.getTreeItem().getValue().getLevelOneItem());
//			stage.close();
//
//		});
	}
	
	
	private boolean rename(File path, String oldName, String newName) {
		
		File oldFile = new File(path + File.separator + oldName);
		File newFile = new File(path + File.separator + newName);	
				
		boolean isRenameSuccessful = oldFile.renameTo(newFile);
		
	    if (isRenameSuccessful) {
	        System.out.println("renamed");
	        pathTreeCell.getTreeItem().getValue().setPath(newFile.toPath());
	        pathTreeCell.getTreeView().refresh();
	        	     
	      } else {
	        System.out.println("Error");
	        labelMassage.setText("error when rename");
	      }
	    
		return isRenameSuccessful;
	}
	
	
    // Getter
	public Stage getStage() {return stage;}
	public TextField getTextFieldName() {return textFieldName;}
	public Label getLabelMassage() {return labelMassage;}
	public String getSuffix() {return suffix;}

	// Setter
	public void setTextFieldName(TextField textFieldName) {this.textFieldName = textFieldName;}	
	
	public void set(StageRename stageRename, CTree cTree, Stage mainStage, Stage stage, PathTreeCell pathTreeCell, TreeItem<PathItem> treeItem) {
		this.stageRename = stageRename;
		this.cTree = cTree;
		this.mainStage = mainStage;
		this.stage = stage;	
		this.pathTreeCell = pathTreeCell;
		this.cellTreeItem = treeItem;
		this.cellFile = pathTreeCell.getItem().getPath().toFile();
		this.name = suffixRemove(cellFile.getName());
		this.suffix = ISuffix.getSuffix(cellFile.getName());

		System.err.println("Rename60: " + treeItem.getValue().getPath());
		
		if (cellFile.isDirectory()) {
			textFieldName.setText(cellFile.getName());
		} else {
			textFieldName.setText(name);
		}
		
//		setOpenWindowInWindowCenter(mainStage, stage);
		
		textFieldListener();
		lockDir(cTree, treeItem);
		
		
		buttonCancel.setOnAction(e -> {		
			System.out.println("pathTreeCell: " + this.pathTreeCell);
//			unlockDir(cTree.getLockFileHandler(), this.pathTreeCell.getTreeItem().getValue().getLevelOneItem());
			unlockDir(cTree.getLockFileHandler(), treeItem.getValue().getLevelOneItem());
			
			// Tree nur updaten, wenn das LevelOne Node leer ist, weil sonst das Lock-Icon nicht auf unlock wechselt, wenn das LevelOne Node leer ist
			int levelOneSize = treeItem.getValue().getLevelOneItem().getChildren().size();
			if(levelOneSize == 0) {
				cTree.refreshTree(true);
			}

			stage.close();

		});
		
	}





}
