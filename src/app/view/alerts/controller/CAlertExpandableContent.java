package app.view.alerts.controller;

import java.net.URL;
import java.util.ResourceBundle;

import app.interfaces.IStringBreaker;
import app.view.functions.notification.Notification;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class CAlertExpandableContent implements Initializable, IStringBreaker {

	private Stage alertStage;
	
	@FXML private Button buttonClose;
	@FXML private Label labelHeader;
	@FXML private Label labelText;
	@FXML private ImageView dialogImageView;
		
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buttonClose.setOnAction(e -> {
			alertStage.close();
		});
		
	}

	
	private void setAlertTypeImage(Notification notification, AlertType alertType) {
		Image image = null;
			
		switch (alertType) {
	        case INFORMATION:
	        	image = notification.getERROR_GRAPHIC();
	            break;	                
	        case WARNING:
	        	image = notification.getERROR_GRAPHIC();
	            break;	                     
	        case ERROR:
	        	image = notification.getERROR_GRAPHIC();
	            break;                   
	        default:
	            break;
	    }
		
		dialogImageView.setImage(image);

	}

	public void set(Notification notification, Stage alertStage, AlertType alertType, String title, String text) {
		this.alertStage = alertStage;
		setAlertTypeImage(notification, alertType);
		labelHeader.setText(title);
		labelText.setText(splitString(text, 70));	
//		labelText.setText(text);	
	}
	
}
