package app.interfaces;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import app.watcher.watchService.PAWatcher;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;



public interface ILogs extends IWindowEigenschaften {

	public Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
//	public default void logInfo(String myClass, boolean showAlert, String msg, Exception e) {
//   	 	StringWriter errors = new StringWriter();
//   	 	e.printStackTrace(new PrintWriter(errors));
//   	 	logInfo(myClass, showAlert, msg, errors.toString());
//	}
//	
//	public default void logWarning(String myClass, boolean showAlert, String msg, Exception e) {
//   	 	StringWriter errors = new StringWriter();
//   	 	e.printStackTrace(new PrintWriter(errors));
//   	 	logWarning(myClass, showAlert, msg, errors.toString());
//	}
//
//	public default void logSevere(String myClass, boolean showAlert, String msg, Exception e) {
//   	 	StringWriter errors = new StringWriter();
//   	 	e.printStackTrace(new PrintWriter(errors));
//   	 	logSevere(myClass, showAlert, msg, errors.toString());
//	}
//	
//	public default void logInfo(String myClass, boolean showAlert, String msg, IOException e) {
//   	 	StringWriter errors = new StringWriter();
//   	 	e.printStackTrace(new PrintWriter(errors));
//   	 	logInfo(myClass, showAlert, msg, errors.toString());
//	}
//	
//	public default void logWarning(String myClass, boolean showAlert, String msg, IOException e) {
//   	 	StringWriter errors = new StringWriter();
//   	 	e.printStackTrace(new PrintWriter(errors));
//   	 	logWarning(myClass, showAlert, msg, errors.toString());
//	}
//	
//	public default void logSevere(String myClass, boolean showAlert, String msg, IOException e) {
//		System.out.println("class " + myClass);
//   	 	StringWriter errors = new StringWriter();
//   	 	e.printStackTrace(new PrintWriter(errors));
//   	 	logSevere(myClass, showAlert, msg, errors.toString());
//	}
	
	public default void logInfo(String myClass, String msg, String exceptionText) {
		log(myClass, exceptionText);	
		log.info(msg);
	}
	
	public default void logWarning(String myClass, String msg, String exceptionText) {
		log(myClass, exceptionText);	
		log.warning(msg);
	}
	
	public default void logSevere(String myClass, String msg, String exceptionText) {
		log(myClass, exceptionText);			
		log.severe(msg);
	}
	
//	public default void logInfoAndAlert(boolean setAlert, String msg) {
//		
//		log.info(msg);
//	}
//	
//	public default void logWarningAndAlert(String msg) {
//		logAndAlert();
//		log.warning(msg);
//	}
//	
//	public default void logSevereAndAlert(String msg) {
//		logAndAlert();
//		log.severe(msg);
//	}
	
//	public default void log(String myClass, String exceptionText) {
//		logAndAlert(myClass, false, exceptionText);
//	}
//	
//	public default void logAndAlert(String myClass, String exceptionText) {
//		logAndAlert(myClass, true, exceptionText);
//	}
	
	public default void log(String myClass, String exceptionText) {
		Logger root = Logger.getLogger("");
		FileHandler txt = null;
		
		try {
			File logDir = new File("./logs/"); 
			if( !(logDir.exists()) )
				logDir.mkdir();
			
			txt = new FileHandler("logs/log.txt");
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		root.setLevel(Level.INFO); // ab welchem Level soll eine Meldung in die Datei geschrieben werden
		txt.setFormatter(new Formatter() {
			@Override
			public String format(LogRecord record)
			{
				String ret = "";

			
				SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
				Date d = new Date(record.getMillis());
				
				String msg = this.formatMessage(record);
//				String inKlasse = myClass.getCanonicalName();
				
				ret += df.format(d) + "\r\n";
				ret += "     Class: " + myClass;
				ret += "\r\n";
				ret += "     " + record.getLevel() + ": ";
				ret += " " + msg.replace("\n", "");
				ret += "\r\n";
				ret += "     Exception: " + exceptionText;
				ret += "\r\n";
				
//				if(isAlert && record.getLevel().intValue() == Level.INFO.intValue()) { // bei welchem Level soll was ausgegeben werden, z.B.: ein Alert
//					alert(AlertType.INFORMATION, msg, myClass, exceptionText);
//				}
//				
//				if(isAlert && record.getLevel().intValue() == Level.WARNING.intValue()) { // bei welchem Level soll was ausgegeben werden, z.B.: ein Alert
//					alert(AlertType.WARNING, msg, myClass, exceptionText);
//				}
//				
//				if(isAlert && record.getLevel().intValue() == Level.SEVERE.intValue()) { // bei welchem Level soll was ausgegeben werden, z.B.: ein Alert
//					alert(AlertType.ERROR, msg, myClass, exceptionText);
//				}
				System.out.println(ret);
				return ret;
			}
		});
		
		root.addHandler(txt);		
	}
	
//	public default void alert(AlertType alertType, String msg, String klasse, String exceptionText) {
//		
//		String title = "";
//		
//		switch (alertType) {
//	        case INFORMATION:
//	        	title = "Information";
//	            break;	                
//	        case WARNING:
//	        	title = "Warnung";
//	            break;	                     
//	        case ERROR:
//	        	title = "Fehler";
//	            break;                   
//	        default:
//	            break;
//	    }
//		
//		Alert alert = new Alert(alertType);
//		Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
//		setOpenWindowInWindowCenter(alertStage);
//		alert.setTitle(title);
//		alert.setHeaderText(null);
//		alert.setContentText(msg);
//		
////		if (exceptionText != null && !exceptionText.isEmpty()) {
//			// Create expandable Exception.
//			StringWriter sw = new StringWriter();
//			PrintWriter pw = new PrintWriter(sw);
//
//			Label label = new Label("Exception:");
//
//			String ausgabe = "Class: " + klasse + "\n\n" + exceptionText;
//			
//			TextArea textArea = new TextArea();				
//			textArea.setText(ausgabe);
//			textArea.setEditable(false);
//			textArea.setWrapText(true);
//
//			textArea.setMaxWidth(Double.MAX_VALUE);
//			textArea.setMaxHeight(Double.MAX_VALUE);
//			GridPane.setVgrow(textArea, Priority.ALWAYS);
//			GridPane.setHgrow(textArea, Priority.ALWAYS);
//
//			GridPane expContent = new GridPane();
//			expContent.setMaxWidth(Double.MAX_VALUE);
//			expContent.add(label, 0, 0);
//			expContent.add(textArea, 0, 1);
//
//			alert.getDialogPane().setExpandableContent(expContent);
////		}
//
//
//		
//		alert.showAndWait();
//	}
	
}
