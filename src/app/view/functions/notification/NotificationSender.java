package app.view.functions.notification;

import javafx.application.Platform;

public class NotificationSender {
	
	public void send(Notification notification) {
		System.out.println("Sent Notification Pause" ); 
	    try
	    { 
	        Thread.sleep(notification.getDelayZwischenShowNotes()); 
	    } 
	    catch (Exception e) 
	    { 
	        System.out.println("Thread  interrupted."); 
	    } 
	    
	    Platform.runLater(() -> {
	    	notification.createAndShow();
	    });
	    
	    System.out.println("Sent Notification"); 
	}

}
