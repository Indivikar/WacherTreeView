package app.test.JMetroProgressbar;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.styles.jmetro8.JMetro;
import jfxtras.styles.jmetro8.JMetro.Style;
 
public class JMetroProgressbar extends Application {
    
	private double progress = 0.1;
	
    @Override
    public void start(Stage primaryStage) {


    	
    	ProgressBar bar = new ProgressBar();
    	bar.setProgress(-1);

         
    	VBox vBox = new VBox();
    	

         
        StackPane root = new StackPane();
        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.applyTheme(root);
        
        vBox.getChildren().add(bar);
        root.getChildren().add(vBox);

        Scene scene = new Scene(root, 300, 250);
//        scene.getStylesheets().add
//        (JMetroProgressbar.class.getResource("JMetroProgressbar.css").toExternalForm());
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
 public static void main(String[] args) {
        launch(args);
    }
}
