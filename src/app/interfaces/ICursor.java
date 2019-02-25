package app.interfaces;

import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.stage.Stage;

public interface ICursor {

    public default void bindUIandService(Stage node, Service<?> service) {
    	node.getScene()
                .getRoot()
                .cursorProperty()
                .bind(
                        Bindings
                            .when(service.runningProperty())
                                .then(Cursor.WAIT)
                                .otherwise(Cursor.DEFAULT)
                );
    }
	
    public default void bindUIandService(Node node, Service<?> service) {
    	node.getScene()
                .getRoot()
                .cursorProperty()
                .bind(
                        Bindings
                            .when(service.runningProperty())
                                .then(Cursor.WAIT)
                                .otherwise(Cursor.DEFAULT)
                );
    }
    
    public default void bindUIandService(Stage node, Task<?> task) {
    	node.getScene()
                .getRoot()
                .cursorProperty()
                .bind(
                        Bindings
                            .when(task.runningProperty())
                                .then(Cursor.WAIT)
                                .otherwise(Cursor.DEFAULT)
                );
    }
	
    public default void bindUIandService(Node node, Task<?> task) {
    	node.getScene()
                .getRoot()
                .cursorProperty()
                .bind(
                        Bindings
                            .when(task.runningProperty())
                                .then(Cursor.WAIT)
                                .otherwise(Cursor.DEFAULT)
                );
    }
	
}
