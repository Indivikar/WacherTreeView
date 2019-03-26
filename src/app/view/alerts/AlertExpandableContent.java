package app.view.alerts;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import app.StartWacherDemo;
import app.interfaces.IWindowEigenschaften;
import app.view.alerts.controller.CAlertExpandableContent;
import app.view.functions.StageVerschiebenMitAnchorPane;
import app.view.functions.notification.Notification;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AlertExpandableContent implements IWindowEigenschaften {

	private CAlertExpandableContent controller;
	
	private void buttonStyle(Node b) {
		// in der Button-Bar befindet sich auch der ExpandableContent-Button, der nicht verändert werden soll.
		// es werden nur die Button geändert
		try {
			Button button = (Button) b;
			button.getStyleClass().add("myButton");
		} catch (Exception e) {
			
		}
	}
	
	
	public AlertExpandableContent(Notification notification, AlertType alertType, String title, String text, String klasse, String fehlerCode, String exceptionText) {
	    FXMLLoader loader = new FXMLLoader(StartWacherDemo.class.getResource("view/fxml/dialog.fxml"));
        DialogPane dialogPane = null;
        try {
        	dialogPane = (DialogPane) loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        
		Alert alert = new Alert(alertType);
		alert.setDialogPane(dialogPane);
//		DialogPane dialogPane = alert.getDialogPane();
//		dialogPane.getStylesheets().add(StartWacherDemo.class.getResource("view/css/alert.css").toExternalForm());
//		dialogPane.getStyleClass().add("myAlert");

		dialogPane.getButtonTypes().addAll(
                ButtonType.OK
        );

		ButtonBar buttonBar = (ButtonBar)dialogPane.lookup(".button-bar");
		buttonBar.getButtons().forEach(b->buttonStyle(b));
		
		
		Stage alertStage = (Stage) dialogPane.getScene().getWindow();
		alertStage.initStyle(StageStyle.UNDECORATED);
		alertStage.setAlwaysOnTop(true);

		new StageVerschiebenMitAnchorPane(null, dialogPane, null, alertStage, false);
		setOpenWindowInWindowCenter(alertStage);
		
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(text);
		
//		StringWriter sw = new StringWriter();
//		PrintWriter pw = new PrintWriter(sw);

		Label label = new Label("Exception:");

		String ausgabe = "Class: " + klasse + "\n" + fehlerCode + "\n\n" + exceptionText;
		
		TextArea textArea = new TextArea();				
		textArea.setText(ausgabe);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		alert.getDialogPane().setExpandableContent(expContent);

		controller = loader.getController();
		controller.set(notification, alertStage, alertType, title, text);
		
		alert.showAndWait();

	}
	
}
