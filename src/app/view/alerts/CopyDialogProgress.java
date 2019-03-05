package app.view.alerts;

import java.io.File;

import javax.swing.event.ChangeEvent;

import app.threads.DeleteItemTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CopyDialogProgress {
	private DeleteItemTask deleteItemTask;
    private final Stage dialogStage;
    private Task<?> task;
    private final ProgressBar progressBar = new ProgressBar();
    private final Label labelMassage = new Label();
    private final Label labelCounter = new Label();

    private Cursor saveOldCursor;
    
    private ObservableList<String> listAllLockedFiles = FXCollections.observableArrayList();
    
//    private final ProgressIndicator pin = new ProgressIndicator();

    public CopyDialogProgress(DeleteItemTask deleteItemTask) {
    	this.deleteItemTask = deleteItemTask;
    	
        dialogStage = new Stage();
        dialogStage.setX(7050);
        dialogStage.setY(400);
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setOnCloseRequest(e -> {
        	task.cancel();
        });
        

        final Button buttonTryAgain = new Button("try again");
        mouseHoverCursor(buttonTryAgain);
        buttonTryAgain.setVisible(false);
        buttonTryAgain.setOnAction(e -> {
	        	synchronized (task) {	  
	        		listAllLockedFiles.clear();
		    		task.notify();
		    	}
        });
        
        final Button buttonSkip = new Button("skip");
        mouseHoverCursor(buttonSkip);
        buttonSkip.setVisible(false);
        buttonSkip.setOnAction(e -> {
	        	synchronized (task) {	  
	        		listAllLockedFiles.clear();
	        		deleteItemTask.setSkip(true);
		    		task.notify();
		    		
		    	}
        });
        
        final Button buttonCancel = new Button("cancel");
        mouseHoverCursor(buttonCancel);
        buttonCancel.setOnAction(e -> {
//        	task.cancel(false);
        	task.cancel();
        	sleep(1000);
        	dialogStage.close();
        });
        
        // PROGRESS BAR    
        final HBox hBoxCounter = new HBox();     
        hBoxCounter.setAlignment(Pos.CENTER);
        
        hBoxCounter.getChildren().addAll(labelCounter);
        
        progressBar.setPrefWidth(5000.0);
        progressBar.setProgress(-1F);
//        pin.setProgress(-1F);

        AnchorPane root = new AnchorPane();
           
        VBox vBox = new VBox();
        vBox.setSpacing(10.0);
        vBox.setAlignment(Pos.CENTER);
        AnchorPane.setTopAnchor(vBox, 10.0);
        AnchorPane.setLeftAnchor(vBox, 10.0);
        AnchorPane.setRightAnchor(vBox, 10.0);
        AnchorPane.setBottomAnchor(vBox, 10.0);
        
        final HBox hb = new HBox();
        hb.setSpacing(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(progressBar);
//        hb.getChildren().addAll(pb, pin);

        TitledPane expContent = titledPane();
        expContent.setVisible(false);
        
        listAllLockedFiles.addListener((ListChangeListener<String>) e -> {
        	if (e.getList().size() !=0) {
        		buttonTryAgain.setVisible(true);
        		buttonSkip.setVisible(true);
        		expContent.setVisible(true);
        		
			} else {
        		buttonTryAgain.setVisible(false);
        		buttonSkip.setVisible(false);
        		expContent.setVisible(false);
			} 
        });

        final HBox hBoxButton = new HBox();
        hBoxButton.setAlignment(Pos.CENTER_RIGHT);
        hBoxButton.setSpacing(10.0);
        hBoxButton.getChildren().addAll(buttonTryAgain, buttonSkip, buttonCancel);

        vBox.getChildren().addAll(hBoxCounter, hb, labelMassage, expContent, hBoxButton);
        root.getChildren().addAll(vBox);
        
        Scene scene = new Scene(root, 500, 250);          
        dialogStage.setScene(scene);
    }
    
    private TitledPane titledPane() {
        TitledPane expandable = new TitledPane();
        expandable.setText("Details");

        Label label = new Label("Locked Files:");
    	ListView<String> listLockedFiles = new ListView<>(listAllLockedFiles);
    	GridPane expContent = new GridPane();
    	expContent.setMaxWidth(Double.MAX_VALUE);
    	expContent.add(label, 0, 0);
    	expContent.add(listLockedFiles, 0, 1);
    	
        ScrollPane scroller = new ScrollPane();
        scroller.setContent(expContent);
        expandable.setContent(scroller);
        expandable.setExpanded(false);
        return expandable ;
    }
    
    public void activateProgressBar(final Task<?> task)  {
    	this.task = task;
    	labelMassage.textProperty().bind(task.titleProperty());
        progressBar.progressProperty().bind(task.progressProperty());
        labelCounter.textProperty().bind(task.messageProperty());
//        pin.progressProperty().bind(task.progressProperty());
        dialogStage.show();
    }

    public void close() {
    	task.cancel();
        dialogStage.close();
    }
    
    private void mouseHoverCursor(Node node) {
    	   	
    	node.addEventHandler(MouseEvent.MOUSE_ENTERED, event ->  {
    		saveOldCursor = node.getCursor();
    		node.setCursor(Cursor.DEFAULT);
    	});
    	
    	node.addEventHandler(MouseEvent.MOUSE_EXITED, event ->  {
    		node.setCursor(saveOldCursor);
    	});

	}
    
    private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public void addLockedFile(File f) {
    	Platform.runLater(() -> {
    		listAllLockedFiles.add(f.getAbsolutePath());
    	});
    }
    
    public ObservableList<String> getListAllLockedFiles() {
		return listAllLockedFiles;
	}

	public Stage getDialogStage() {
        return dialogStage;
    } 
}
