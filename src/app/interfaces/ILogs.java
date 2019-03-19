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

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;



public interface ILogs {

	public Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public default void logInfo(IOException e) {
   	 	StringWriter errors = new StringWriter();
   	 	e.printStackTrace(new PrintWriter(errors));
   	 	logInfo(errors.toString());
	}
	
	public default void logWarning(IOException e) {
   	 	StringWriter errors = new StringWriter();
   	 	e.printStackTrace(new PrintWriter(errors));
   	 	logWarning(errors.toString());
	}
	
	public default void logSevere(IOException e) {
   	 	StringWriter errors = new StringWriter();
   	 	e.printStackTrace(new PrintWriter(errors));
   	 	logSevere(errors.toString());
	}
	
	public default void logInfo(String msg) {
		log();
		log.info(msg);
	}
	
	public default void logWarning(String msg) {
		log();
		log.warning(msg);
	}
	
	public default void logSevere(String msg) {
		log();
		log.severe(msg);
	}
	
	public default void logInfoAndAlert(String msg) {
		logAndAlert();
		log.info(msg);
	}
	
	public default void logWarningAndAlert(String msg) {
		logAndAlert();
		log.warning(msg);
	}
	
	public default void logSevereAndAlert(String msg) {
		logAndAlert();
		log.severe(msg);
	}
	
	public default void log() {
		logAndAlert(false);
	}
	
	public default void logAndAlert() {
		logAndAlert(true);
	}
	
	public default void logAndAlert(boolean isAlert) {
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
			
		root.setLevel(Level.INFO); // welchem Level soll eine Meldung in die Datei geschrieben werden
		txt.setFormatter(new Formatter() {
			@Override
			public String format(LogRecord record)
			{
				String ret = "";

				
				SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
				Date d = new Date(record.getMillis());
				
				ret += df.format(d) + "\r\n";
				ret += record.getLevel() + ": ";
				ret += " " + this.formatMessage(record);
				ret += "\r\n";
				
				if(isAlert && record.getLevel().intValue() == Level.INFO.intValue()) { // bei welchem Level soll was ausgegeben werden, z.B.: ein Alert
					alert(AlertType.INFORMATION, ret);
				}
				
				if(isAlert && record.getLevel().intValue() == Level.WARNING.intValue()) { // bei welchem Level soll was ausgegeben werden, z.B.: ein Alert
					alert(AlertType.WARNING, ret);
				}
				
				if(isAlert && record.getLevel().intValue() == Level.SEVERE.intValue()) { // bei welchem Level soll was ausgegeben werden, z.B.: ein Alert
					alert(AlertType.ERROR, ret);
				}
				
				return ret;
			}
		});
		
		root.addHandler(txt);		
	}
	
	public default void alert(AlertType alertType, String ret) {
		
		String title = "";
		
		switch (alertType) {
	        case INFORMATION:
	        	title = "Information";
	            break;	                
	        case WARNING:
	        	title = "Warnung";
	            break;	                     
	        case ERROR:
	        	title = "Fehler";
	            break;                   
	        default:
	            break;
	    }
		
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(ret);

		alert.showAndWait();
	}
	
}
