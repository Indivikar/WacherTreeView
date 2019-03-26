package app.view.functions.notification;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.controlsfx.control.Notifications;
import org.controlsfx.tools.Utils;

import app.StartWacherDemo;
import app.controller.CTree;
import app.interfaces.IStringBreaker;
import app.view.alerts.AlertExpandableContent;

public class Notification implements IStringBreaker{

	public enum NotificationType {
		NULL, PEN, GEAR, REFRESH, INFO, WARNING, ERROR
	}
	
	private CTree cTree;
	private NotificationSender notificationSender;
	
	// General Properties 
	private Stage primaryStage;
    private Window owner;
    private Screen screen = Screen.getPrimary();
	private String title;
	private String text;
	private String klasse;
	private String fehlerCode;
	
	// Notification Properties 
	private NotificationType type;
	private Pos position = Pos.BOTTOM_RIGHT;
	private int hideAfterSeconds = 10;
	private int delayZwischenShowNotes = 1000;
	
	// Alert Properties
	private boolean showAlert;
	private AlertType alertType;
	
	// Log Properties
//	private boolean writeLog;
	private String exceptionText;
	
	
	
	private Image PEN_GRAPHIC = new Image(StartWacherDemo.class.getResourceAsStream("view/images/edit_32px.png")); 
	private Image GEAR_GRAPHIC = new Image(StartWacherDemo.class.getResourceAsStream("view/images/edit_32px.png")); 
	private Image REFRESH_GRAPHIC = new Image(StartWacherDemo.class.getResourceAsStream("view/images/refresh_32px.png")); 
	private Image INFO_GRAPHIC = new Image(StartWacherDemo.class.getResourceAsStream("view/images/info_32px.png")); 
	private Image WARNING_GRAPHIC = new Image(StartWacherDemo.class.getResourceAsStream("view/images/warning_32px.png")); 
	private Image ERROR_GRAPHIC = new Image(StartWacherDemo.class.getResourceAsStream("view/images/error_32px.png"));
	private Exception e; 
	
	
	public Notification() {
		
		// TODO Auto-generated constructor stub
	}
	
    public static Notification create() {
        return new Notification();
    }
	
    public Notification setNotificationSender(CTree cTree) {
    	this.cTree = cTree;
        this.notificationSender = cTree.getNotificationSender();
        return this;
    }
    
    public Notification type(NotificationType type) {
        this.type = type;
        return this;
    }
    
    public Notification setTitle(String title) {
        this.title = title;
        return this;
    }

    public Notification setText(String text) {
        this.text = text;
        return this;
    }
    
    public Notification position(Pos position) {
        this.position = position;
        return this;
    }
    
    public Notification hideAfter(int hideAfterSeconds) {
        this.hideAfterSeconds = hideAfterSeconds;
        return this;
    }
	
    public Notification owner(Object owner) {
        if (owner instanceof Screen) {
            this.screen = (Screen) owner;
        } else {
            this.owner = Utils.getWindow(owner);
        }
        return this;
    }
    
//    public Notification setLog() {
//        this.writeLog = true;
//        return this;
//    }
    
    public Notification setAlert() {
        this.showAlert = true;
        return this;
    }
    
    public Notification setClass(String klasse) {
        this.klasse = klasse;
        return this;
    }
    
    public Notification fehlerCode(String fehlerCode) {
    	fehlerCode = "Code: " + fehlerCode;
        this.fehlerCode = fehlerCode;
        return this;
    }
    
    public Notification setException(Exception e) {
   	 	StringWriter errors = new StringWriter();
   	 	e.printStackTrace(new PrintWriter(errors));
        this.exceptionText = errors.toString();
        this.e = e;
        return this;
    }
    
    public Notification setIOException(IOException e) {
   	 	StringWriter errors = new StringWriter();
   	 	e.printStackTrace(new PrintWriter(errors));
        this.exceptionText = errors.toString();
        return this;
    }

//    public Notification setInterruptedException(InterruptedException e) {
//   	 	StringWriter errors = new StringWriter();
//   	 	e.printStackTrace(new PrintWriter(errors));
//        this.exceptionText = errors.toString();
//        return this;
//    }
    
    public void start() { 

    	// Notification syncronisieren und eine kleine Pause zwischen den Notifications einlegen,
    	// damit es etwas besser dagestellt wird
    	NotificationThread notificationThread = new NotificationThread(cTree, this);
    	notificationThread.start();

    }
       
	public void createAndShow() {

    	// kurze Pause einlegen, sonst gibt es Überlagerungen bei der Darstellung der Notes
    	

			Node image = null;
		    switch (type) {
		        case PEN:  image = new ImageView(PEN_GRAPHIC); break;
		        case GEAR: image = new ImageView(GEAR_GRAPHIC); break;
		        case REFRESH: image = new ImageView(REFRESH_GRAPHIC); break;
		        case INFO:  image = new ImageView(INFO_GRAPHIC); this.alertType = alertType.INFORMATION; break;
		        case WARNING: image = new ImageView(WARNING_GRAPHIC); this.alertType = alertType.WARNING; break;
		        case ERROR: image = new ImageView(ERROR_GRAPHIC); this.alertType = alertType.ERROR; break;
		        default: image = null; 
		    }
			  		 	    
	        Notifications notificationBuilder = Notifications.create()
//	                .title(isTitleExists(title) ? title : "")
	                .title(title())
//	                .text(text)
	                .text(splitString(text, 50)) // nach 50 Zeichen ein Zeilenumbruch machen
	                .graphic(image)
	                .hideAfter(Duration.seconds(hideAfterSeconds))
	                .position(position)
	                .onAction(e -> clickOnAction());
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
	
	private String title() {
		String newTitle = "";
		
		if (title != null || !title.isEmpty()) {
			newTitle = title + " ";
		}
		
		if (alertType != null && showAlert) {
			newTitle += "(klicken für mehr Info's)";
		}
		return newTitle;
	}
	
	private void clickOnAction() {
		showAlert();		
	}
	
	private void showAlert() {
		if (alertType != null && showAlert) {
			alertExpandableContent();
		}		
	}
	
//	private void writeLog() {
//		if (writeLog) {			
//			switch (type) {
//		        case INFO:
//		        	logInfo(klasse, text, exceptionText);
//		            break;	                
//		        case WARNING:
//		        	logWarning(klasse, text, exceptionText);
//		            break;	                     
//		        case ERROR:	        	
//		        	logSevere(klasse, text, exceptionText, e);
//		            break;                   
//		        default:
//		            break;
//		    }			
//		}	
//	}
	
	private void printTextLayoutBounds(Parent p) {
	    for(Node n : p.getChildrenUnmodifiable()) {
	      if (n instanceof VBox) {
	    	  VBox l = (VBox) n;
//	        Text t = (Text) l.getChildrenUnmodifiable().get(0);
	 
	        Bounds b = l.localToScene(l.getLayoutBounds());
	        System.out.println("Node : " + l);
	        System.out.println("MinX : " + b.getMinX());
	        System.out.println("MaxX : " + b.getMaxX());
	        System.out.println("MinY : " + b.getMinY());
	        System.out.println("MaxY : " + b.getMaxY());
	        System.out.println("Width : " + b.getWidth());
	        System.out.println("Height : " + b.getHeight());
	        System.out.println("--------------------------------------------------------" + n);
	      }
	    }
	  }
	
	
	private void alertExpandableContent() {	
		new AlertExpandableContent(this, alertType, title, text, klasse, fehlerCode, exceptionText);
	}
	
	// Getter
	public Image getPEN_GRAPHIC() {return PEN_GRAPHIC;}
	public Image getGEAR_GRAPHIC() {return GEAR_GRAPHIC;}
	public Image getREFRESH_GRAPHIC() {return REFRESH_GRAPHIC;}
	public Image getINFO_GRAPHIC() {return INFO_GRAPHIC;}
	public Image getWARNING_GRAPHIC() {return WARNING_GRAPHIC;}
	public Image getERROR_GRAPHIC() {return ERROR_GRAPHIC;}
	
	public int getDelayZwischenShowNotes() {return delayZwischenShowNotes;}



	private void sleep(int milliSec) {
        try {
			Thread.sleep(milliSec);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private boolean isTitleExists(String title) {		
		if (title == null || title.isEmpty()) {
			return false;
		}
		return true;
	}
	
}
