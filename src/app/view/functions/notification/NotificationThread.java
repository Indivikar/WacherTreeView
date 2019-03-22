package app.view.functions.notification;

import app.controller.CTree;

public class NotificationThread extends Thread {

	private CTree cTree;
	private Notification notification;
	
	public NotificationThread(CTree cTree, Notification notification) {
		this.cTree = cTree;
		this.notification = notification;
	}
	
	public void run() 
    { 

        synchronized(cTree.getNotificationSender()) 
        { 
            // synchronizing the snd object 
        	cTree.getNotificationSender().send(notification); 
        } 
    } 
	
}
