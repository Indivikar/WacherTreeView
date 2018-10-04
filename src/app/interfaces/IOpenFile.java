package app.interfaces;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public interface IOpenFile {

	public default void open(File file) {
		try {
		      Desktop desktop = null;
		      if (Desktop.isDesktopSupported()) {
		    	  	desktop = Desktop.getDesktop();
		      } else {
		    	  Alert alert = new Alert(AlertType.ERROR);
		    	  alert.setTitle("ERROR");
		    	  alert.setHeaderText(null);
		    	  alert.setContentText("java.awt.Desktop isn’t Supported!");

		    	  alert.showAndWait();
		      }
		      
		      desktop.open(file);
		      
		    } catch (IOException ioe) {
		      ioe.printStackTrace();
		    }
	}
}

