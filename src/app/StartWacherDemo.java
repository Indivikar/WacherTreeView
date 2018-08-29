package app;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import app.controller.CTree;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class StartWacherDemo  extends Application {

	private CTree controller;
	
	public StartWacherDemo() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(StartWacherDemo.class.getResource("view/fxml/tree.fxml"));			
		AnchorPane root = loader.load();

		
		Scene scene = new Scene(root);
		
		controller = loader.getController();
		controller.set(this, primaryStage);
		
		
		primaryStage.setTitle("Watch Dir Demo");
		primaryStage.setScene(scene);
		primaryStage.setX(6800);
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
