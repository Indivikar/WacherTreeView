package app.view.functions;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StageVerschiebenMitAnchorPane {

	private double xOffset;
	private double yOffset;

	private boolean verschieben;

	public StageVerschiebenMitAnchorPane(AnchorPane shadowPane, DialogPane mainAnchorPane, Button buttonWindowMax, Stage stage, boolean isStageWithBorder) {

		mainAnchorPane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
            public void handle(MouseEvent event) {
            	System.out.println("setOnMousePressed");

            	if (!istDasDerResizeBereich(stage, event)) {

            		verschieben = true;

					if(isStageWithBorder){
		            	xOffset = event.getSceneX() + 8; // + 8 ist der Rand von der Stage
		                yOffset = event.getSceneY() + 30; // + 30 ist der Rand von der Stage
	            	} else {
		            	xOffset = event.getSceneX();
		                yOffset = event.getSceneY();
	            	}
				} else {
					verschieben = false;
				}

            	System.out.println("xOffset: " + xOffset);
            }
        });

		mainAnchorPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
            public void handle(MouseEvent event) {
				if (verschieben) {
//					System.out.println("setOnMouseDragged");


					// wenn das Fenster verschoben wird und es noch max. ist, dann erst das Fenster min. und den Schatten wieder einblenden
	            	if(stage.isMaximized()){
		            	double x = event.getScreenX() - xOffset;
		            	double y = event.getScreenY() - yOffset;

		            	// Mouse Position
		            	Point p = MouseInfo.getPointerInfo().getLocation();
		            	int xMouse = p.x;
		            	int yMouse = p.y;

		            	// an welcher Y-Position im Fenster ist die Mouse (in %)
		            	double mousePosYInProzent = yMouse / mainAnchorPane.getHeight();

		            	// das Fenster wieder minimieren
//	            		setWindowMaxIcon(stage, buttonWindowMax, shadowPane, mainAnchorPane, Start.hasShadowPane);

	            		double neueFensterPosX = 0;
	            		double neueFensterPosY = 0;

	            		// wenn die Mouse auf der X-Achse, nach dem minimieren nicht mehr im Fenster ist, dann setze das Fenster auf die Mouse Pos. in X
	            		if(xMouse > (x + mainAnchorPane.getWidth())){

	            			neueFensterPosX = xMouse - mainAnchorPane.getWidth();
	            			stage.setX(neueFensterPosX);
	            			// setzte xOffset neu, ab da wird mit dem neuen wert weiter gerechnet
	            			xOffset = mainAnchorPane.getWidth() -100;
	            		}

	            		// wenn die Mouse auf der Y-Achse, nach dem minimieren nicht mehr im Fenster ist, dann setze das Fenster auf die Mouse Pos. in Y
	            		if(yMouse > (y + mainAnchorPane.getHeight())){

	            			neueFensterPosY = yMouse - (mainAnchorPane.getHeight() * mousePosYInProzent);
	            			stage.setY(neueFensterPosY);
	            			// setzte yOffset neu, ab da wird mit dem neuen wert weiter gerechnet
	            			yOffset = mainAnchorPane.getHeight() - (neueFensterPosY / 2);
	            		}

	            	} else {
		            	double x = event.getScreenX() - xOffset;
		            	double y = event.getScreenY() - yOffset;

		            	stage.setX(x);
		            	stage.setY(y);
	            	}



				}


            }
        });

		mainAnchorPane.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	if (verschieben) {
	            	if(event.getClickCount() == 1){
	            		System.out.println("setOnMouseReleased");
	                	double x = event.getScreenX() - xOffset;
	                	double y = event.getScreenY() - yOffset;

	            	}
            	}
            }
        });
	}

	private boolean istDasDerResizeBereich(Stage stage, MouseEvent event) {

	    System.out.println(event.getScreenX());
	    System.out.println(event.getScreenY());

        double 	mouseEventX = event.getSceneX(),
                mouseEventY = event.getSceneY(),
                sceneWidth = stage.getScene().getWidth(),
                sceneHeight = stage.getScene().getHeight();

        int border = ResizeHelper.ResizeListener.border;


        if (mouseEventX < border && mouseEventY < border) {
        	return true;
        } else if (mouseEventX < border && mouseEventY > sceneHeight - border) {
        	return true;
        } else if (mouseEventX > sceneWidth - border && mouseEventY < border) {
        	return true;
        } else if (mouseEventX > sceneWidth - border && mouseEventY > sceneHeight - border) {
        	return true;
        } else if (mouseEventX < border) {
        	return true;
        } else if (mouseEventX > sceneWidth - border) {
        	return true;
        } else if (mouseEventY < border) {
        	return true;
        } else if (mouseEventY > sceneHeight - border) {
        	return true;
        } else {
        	return false;
        }

	}

}
