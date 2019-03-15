package app.view.functions;


import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.controlsfx.control.Notifications;

import app.StartWacherDemo;

public class Notification {

	public enum NotificationGraphic {
		NULL, PEN, GEAR, REFRESH
	}
	
	private Image PEN_GRAPHIC = new Image(StartWacherDemo.class.getResourceAsStream("view/images/edit_32px.png")); 
	private Image GEAR_GRAPHIC = new Image(StartWacherDemo.class.getResourceAsStream("view/images/edit_32px.png")); 
	private Image REFRESH_GRAPHIC = new Image(StartWacherDemo.class.getResourceAsStream("view/images/refresh_32px.png")); 
	
	public void create(Pos pos, Stage owner, NotificationGraphic graphic, boolean showTitle, String title, String text) {
	        
			Node image = null;
		    switch (graphic) {
		        case PEN:  image = new ImageView(PEN_GRAPHIC); break;
		        case GEAR: image = new ImageView(GEAR_GRAPHIC); break;
		        case REFRESH: image = new ImageView(REFRESH_GRAPHIC); break;
		        default: image = null; 
		    }
		
	        Notifications notificationBuilder = Notifications.create()
	                .title(showTitle ? title : "")
	                .text(text)
	                .graphic(image)
	                .hideAfter(Duration.seconds(5))
	                .position(pos)
	                .onAction(e -> System.out.println("Notification clicked on!"));
//	                .threshold((int) 5, Notifications.create().title("Threshold Notification"));

	        if (owner != null) {
	            notificationBuilder.owner(owner);
	        }
	        
//	        notificationBuilder.darkStyle();
//	        notificationBuilder.showWarning();
//          notificationBuilder.showInformation();
//          notificationBuilder.showConfirm();
//          notificationBuilder.showError();
//	        notificationBuilder.hideCloseButton();
	        
//	        if (!showCloseButtonChkBox.isSelected()) {
//	            
//	        }
//	        
//	        if (darkStyleChkBox.isSelected()) {
//	            notificationBuilder.darkStyle();
//	        }
	        
	        notificationBuilder.show(); 

	}
	
}
