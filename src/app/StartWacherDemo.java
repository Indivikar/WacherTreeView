package app;

import java.io.File;

import app.controller.CTree;
import app.interfaces.IBindings;
import app.interfaces.ILockDir;
import app.interfaces.IPrimaryStage;
import app.loadTime.LoadTime;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;



public class StartWacherDemo extends Application implements ILockDir, IBindings, IPrimaryStage {

	// Config
	boolean startLoadTimeList = false;
	
	private StartWacherDemo startWacherDemo;
	private CTree controller;
	
	public static ObservableList<File> myLockFiles = FXCollections.observableArrayList(); // für Interface ILockDir
	
	public StartWacherDemo() {
		this.startWacherDemo = this;
	}

	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		if (startLoadTimeList) {
			new LoadTime().start(new Stage());
		}
			
		FXMLLoader loader = new FXMLLoader(StartWacherDemo.class.getResource("view/fxml/tree.fxml"));			
		AnchorPane root = loader.load();
				
		Scene scene = new Scene(root);
		
		addController(primaryStage, loader);		
		setLocationListener(primaryStage);	
		setSizeListener(primaryStage);
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
    	System.out.println("Stop");
    	delAllLockFiles(controller);
 
//    	controller.getService().shutdownNow();
    }

    public void addController(Stage primaryStage, FXMLLoader loader) {
    	Task<Void> task = new Task<Void>() {
		    @Override public Void call() {
		    	Platform.runLater(() -> {
		    				controller = loader.getController();
		    				controller.set(startWacherDemo, primaryStage);
		    	});
		        return null;
		    }
		};
		
		Platform.runLater(() -> {
			bindSceneAndService(primaryStage, task);
			new Thread(task).start();
		});
	}
    

	public static void main(String[] args) {
        launch(args);
        Platform.exit();
        System.exit(0);
    }



	@Override
	public void setLocationListener(Stage primaryStage) {
		primaryStage.xProperty().addListener((obs, oldVal, newVal) -> CTree.primaryStageLocationX = newVal.doubleValue());
		primaryStage.yProperty().addListener((obs, oldVal, newVal) -> CTree.primaryStageLocationY = newVal.doubleValue());	
	}

	@Override
	public void setSizeListener(Stage primaryStage) {
		primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> CTree.primaryStageWidth = newVal.doubleValue());
		primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> CTree.primaryStageHeight = newVal.doubleValue());	
	}

}
