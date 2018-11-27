package app.view.alerts;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class AlertFilesLocked extends Alert {

	private ObservableList<String> listAllLockedFiles = FXCollections.observableArrayList();
	
	public AlertFilesLocked(AlertType arg0) {
		super(arg0);
		listAllLockedFiles.clear();
    	setTitle("Exception Dialog");
    	setHeaderText("can not be deleted, it is still being edited");
    	setContentText(null);

    	Label label = new Label("Locked Files:");

    	ListView<String> listLockedFiles = new ListView<>(listAllLockedFiles);

    	listLockedFiles.setMaxWidth(Double.MAX_VALUE);
    	listLockedFiles.setMaxHeight(Double.MAX_VALUE);
    	GridPane.setVgrow(listLockedFiles, Priority.ALWAYS);
    	GridPane.setHgrow(listLockedFiles, Priority.ALWAYS);

    	GridPane expContent = new GridPane();
    	expContent.setMaxWidth(Double.MAX_VALUE);
    	expContent.add(label, 0, 0);
    	expContent.add(listLockedFiles, 0, 1);

    	// Set expandable Exception into the dialog pane.
    	getDialogPane().setExpandableContent(expContent);

//    	showAndWait();
		
		
	}

	public void start() {
		show();
//		showAndWait();
	}
	
	public void addLockedFile(File lockedFile) {
		listAllLockedFiles.add(lockedFile.getAbsolutePath());
	}
	
}
