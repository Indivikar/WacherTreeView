package app.interfaces;

import app.StartWacherDemo;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public interface IWindowEigenschaften {

	public default double[] setOpenWindowInWindowCenter(Stage stageMain, Stage stage) {
		double[] array = new double[2];
		
		Platform.runLater(() -> {	
		
			double stageMainPosX = stageMain.getX();
			double stageMainPosY = stageMain.getY();
			double stageMainWidth = stageMain.getWidth();
			double stageMainHeight = stageMain.getHeight();
			
			Stage stageChild = (Stage) stage.getScene().getWindow();
			double stageChildWidth = stageChild.getWidth();
			double stageChildHeight = stageChild.getHeight();
			
	
			double posX = (stageMainWidth / 2) - (stageChildWidth / 2) +  stageMainPosX;
			double posY = (stageMainHeight / 2) - (stageChildHeight / 2) +  stageMainPosY;
			System.out.println(stageMainWidth / 2 + " + " + stageChildWidth / 2 + " + " + stageMainPosX + " = " + posX);
			System.out.println(stageMainHeight / 2 + " + " + stageChildHeight / 2 + " + " + stageMainPosY + " = " + posY);

			stage.setX(posX);
			stage.setY(posY);
			
			// PosX
			array[0] = posX;
			// PosY
			array[1] = posY;
		});
		
		return array;		
		
	}

	public default void setSceneEigenschaften(Scene scene) {
//		scene.getStylesheets().add(StartMain.class.getResource("/app/view/css/MainStage.css").toExternalForm());
		scene.getStylesheets().add(StartWacherDemo.class.getResource("/app/style/MainStyle.css").toExternalForm());
		scene.getStylesheets().add(StartWacherDemo.class.getResource("/app/font/lucist__.ttf").toExternalForm());
		scene.setFill(Color.TRANSPARENT); // Fill our scene with nothing
	}
	
}
