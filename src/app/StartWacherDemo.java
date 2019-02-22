package app;

import java.io.File;

import app.controller.CTree;
import app.loadTime.LoadTime;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class StartWacherDemo  extends Application {

	// Config
	boolean startLoadTimeList = true;
	
	private CTree controller;
	
	public StartWacherDemo() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		if (startLoadTimeList) {
			new LoadTime().start(new Stage());
		}
		
		
		FXMLLoader loader = new FXMLLoader(StartWacherDemo.class.getResource("view/fxml/tree.fxml"));			
		AnchorPane root = loader.load();
		
		Scene scene = new Scene(root);
		
		controller = loader.getController();
		controller.set(this, primaryStage);
			
//		controller.getScrollingByDragNDrop().stopScrolling(root);
		
		primaryStage.setTitle("Watch Dir Demo");
		primaryStage.setScene(scene);
		File f = new File("C:\\Users\\DH\\AppData\\Roaming\\IndivikarAG\\dev");
		if (f.exists()) {
			primaryStage.setX(7050);
		}		
		primaryStage.setY(10);
		primaryStage.show();

	}

    @Override
    public void stop() throws Exception {
    	Platform.exit();
    	System.exit(0);
//    	controller.getService().shutdownNow();
    }


	public static void main(String[] args) {
        launch(args);
    }
	
}
