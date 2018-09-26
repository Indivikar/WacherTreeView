package app.view.Stages;

import java.nio.file.Path;

import app.TreeViewWatchService.CopyDirectory;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StageFileIsExist {

	public StageFileIsExist(CopyDirectory copyDirectory, Path dir) {
		
	 	Platform.runLater(() -> {
	  			Stage primaryStage = new Stage();
	  		  
				primaryStage.setTitle("Info");
	
				AnchorPane root = new AnchorPane();
	
				Label label = new Label("This file \"" + dir + "\" already exists.\n\nDo you want to replace this file?");
				label.setWrapText(true);
				HBox hBoxText = new HBox(label);
			    AnchorPane.setTopAnchor(hBoxText, 10.0);
			    AnchorPane.setLeftAnchor(hBoxText, 10.0);
			    AnchorPane.setRightAnchor(hBoxText, 10.0);
				
				CheckBox checkBox = new CheckBox("same for all files");
				checkBox.selectedProperty().addListener ( (arg, oldVal, newVal) -> {
					copyDirectory.setSameForAll(newVal);
				});
				HBox HBoxCheck = new HBox(checkBox);
			    AnchorPane.setBottomAnchor(HBoxCheck, 10.0);
			    AnchorPane.setLeftAnchor(HBoxCheck, 10.0);


			    
				Button buttonYes = new Button("Yes");
				buttonYes.setOnAction(e -> {
					copyDirectory.setReplaceYes(true);
					copyDirectory.next();
					primaryStage.close();
				});
				
				Button buttonNo = new Button("No");
				buttonNo.setOnAction(e -> {
					copyDirectory.setReplaceYes(false);
					copyDirectory.next();
					primaryStage.close();
				});
				
				HBox hBoxButtons = new HBox(buttonYes, buttonNo);
				hBoxButtons.setSpacing(10);
			    AnchorPane.setBottomAnchor(hBoxButtons, 10.0);
			    AnchorPane.setRightAnchor(hBoxButtons, 10.0);
				
				
				root.getChildren().addAll(hBoxText, HBoxCheck, hBoxButtons);
	
				Scene scene = new Scene(root,300,150);
				
				primaryStage.setScene(scene);
				primaryStage.show();
	  	}); 
		
	}

}
