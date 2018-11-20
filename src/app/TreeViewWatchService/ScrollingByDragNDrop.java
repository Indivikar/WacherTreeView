package app.TreeViewWatchService;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class ScrollingByDragNDrop {

	private TreeView<PathItem> tree;
	
    private Timeline scrolltimeline = new Timeline();
    private double scrollDirection = 0;
	
	public ScrollingByDragNDrop(TreeView<PathItem> tree) {
		this.tree = tree;
		setupScrolling(tree);
//		stopScrolling(tree.getScene());
		
	}

	  private void setupScrolling(Node node) {
	        scrolltimeline.setCycleCount(Timeline.INDEFINITE);
	        scrolltimeline.getKeyFrames().add(new KeyFrame(Duration.millis(20), "Scoll", (ActionEvent) -> { dragScroll();}));
	        node.setOnDragExited(event -> {
	        	System.out.println("startScrolling");
	            if (event.getY() > 0) {
	                scrollDirection = 1.0 / tree.getExpandedItemCount();
	            }
	            else {
	                scrollDirection = -1.0 / tree.getExpandedItemCount();
	            }
	            scrolltimeline.play();
	        });
	        node.setOnDragEntered(event -> {
	        	System.out.println("Stop setOnDragEntered");
	            scrolltimeline.stop();
	        });
	        node.setOnDragDone(event -> {
	        	System.out.println("Stop setOnDragDone");
	        	scrolltimeline.stop();
	        });         
	    }
		
	  public void stopScrolling(Node node) {

		  node.setOnDragEntered(event -> {
	        	System.out.println("Stop setOnDragEntered");
	            scrolltimeline.stop();
	        });
		  node.setOnDragDone(event -> {
	        	System.out.println("Stop setOnDragDone");
	        	scrolltimeline.stop();
	        });         
	    }
	  
	  	public void startScrolling(DragEvent event) {
	  		System.out.println("startScrolling");
            if (event.getY() > 0) {
                scrollDirection = 1.0 / tree.getExpandedItemCount();
            }
            else {
                scrollDirection = -1.0 / tree.getExpandedItemCount();
            }
            scrolltimeline.play();
		}
	  
		public void stopScrolling() {
			scrolltimeline.stop();
		}
	  
	  
	    private void dragScroll() {
	        ScrollBar sb = getVerticalScrollbar();
	        if (sb != null) {
	            double newValue = sb.getValue() + scrollDirection;
	            newValue = Math.min(newValue, 1.0);
	            newValue = Math.max(newValue, 0.0);
	            sb.setValue(newValue);
	        }
	    }
	    
	    private ScrollBar getVerticalScrollbar() {
	        ScrollBar result = null;
	        for (Node n : tree.lookupAll(".scroll-bar")) {
	            if (n instanceof ScrollBar) {
	                ScrollBar bar = (ScrollBar) n;
	                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
	                    result = bar;
	                }
	            }
	        }       
	        return result;
	    }
	
}
