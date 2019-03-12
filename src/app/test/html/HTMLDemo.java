package app.test.html;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import app.StartWacherDemo;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.styles.jmetro8.JMetro;
import jfxtras.styles.jmetro8.JMetro.Style;
 
public class HTMLDemo extends Application {
    
	private double progress = 0.1;
	
    @Override
    public void start(Stage primaryStage) {

        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
    	
        browser.setMaxSize(150, 150);
        
    	addHTML(webEngine);
    	
        TreeItem<String> rootItem = new TreeItem<>("Root");
        rootItem.setExpanded(true);

        TreeItem<String> item = new TreeItem<>("A");
        rootItem.getChildren().add(item);
        
        item = new TreeItem<>("B");
        rootItem.getChildren().add(item);

        item = new TreeItem<>("B");
        rootItem.getChildren().add(item);
        
        item = new TreeItem<>("BBBBBBBBBBBBBBBBBBBBBBBBBBBB");
        rootItem.getChildren().add(item);
        
        TreeView<String> tree = new TreeView<>(rootItem);
        
        StackPane root = new StackPane();
        root.getChildren().addAll(tree, browser);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
        
    	
    	

    	

    }

    private void addHTML(WebEngine webEngine) {
        URL url = StartWacherDemo.class.getResource("view/html/loading/load_animation.htm");
		System.out.println("Local URL: " + url.toString());
           
		webEngine.load(url.toString());
	}
    
 public static void main(String[] args) {
        launch(args);
    }
}
