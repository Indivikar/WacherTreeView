package app.interfaces;

import app.TreeViewWatchService.ModelFileChanges;
import app.TreeViewWatchService.PathItem;
import app.controller.CTree;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public interface IBindings {

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
    	Platform.runLater(() -> {
    		node.getScene()
                .getRoot()
                .cursorProperty()
                .bind(
                        Bindings
                            .when(service.runningProperty())
                                .then(Cursor.WAIT)
                                .otherwise(Cursor.DEFAULT)
                );
    	});   	
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
	
	public default void bindMenuItemsReload(CTree cTree, Service<?> service) {
		Platform.runLater(() -> {
			Bindings
	        .when(service.runningProperty())
	            .then(cTree.setProperiesRefreshTree(true))
	            .otherwise(cTree.setProperiesRefreshTree(false));
		});		
	}
    
	public default void bindTreeViewAndProgressBar(TreeView<PathItem> treeView, ProgressBar progressBar) {
		progressBar.visibleProperty().bind(treeView.disabledProperty());
	}
	
	public default void bindTreeViewAndWebView(TreeView<PathItem> treeView, WebView webView) {
		webView.visibleProperty().bind(treeView.disabledProperty());
	}
	
}
