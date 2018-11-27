package app.view.alerts;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DefaultAlert {

	public DefaultAlert(AlertType type, String title, String content) {
		new DefaultAlert(type, title, null, content);
	}
	
	public DefaultAlert(AlertType type, String title, String Header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(Header);
		alert.setContentText(content);

		alert.showAndWait();
	}

}
