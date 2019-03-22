package app.view.alerts;

import app.interfaces.IWindowEigenschaften;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DefaultAlert implements IWindowEigenschaften {

	public DefaultAlert(Stage stageMain, AlertType type, String title, String content) {
		new DefaultAlert(stageMain, type, title, null, content);
	}
	
	public DefaultAlert(Stage stageMain, AlertType type, String title, String Header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(Header);
		alert.setContentText(content);

		Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
		setOpenWindowInWindowCenter(alertStage);
		
		alert.showAndWait();
	}

}
