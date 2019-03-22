package app.view.functions;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

//created by Alexander Berg
public class ResizeHelper {

    public static void addResizeListener(Stage stage, AnchorPane anchorPane) {
        ResizeListener resizeListener = new ResizeListener(stage);
        anchorPane.addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
        anchorPane.addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
        anchorPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
        anchorPane.addEventHandler(MouseEvent.MOUSE_EXITED, resizeListener);
        anchorPane.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);
        ObservableList<Node> children = anchorPane.getChildrenUnmodifiable();
        for (Node child : children) {
            addListenerDeeply(child, resizeListener);
        }
    }
    
    public static void removeResizeListener(Stage stage, AnchorPane anchorPane) {
        ResizeListener resizeListener = new ResizeListener(stage);
        anchorPane.removeEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
        anchorPane.removeEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
        anchorPane.removeEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
        anchorPane.removeEventHandler(MouseEvent.MOUSE_EXITED, resizeListener);
        anchorPane.removeEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);
        ObservableList<Node> children = anchorPane.getChildrenUnmodifiable();
        for (Node child : children) {
            addListenerDeeply(child, resizeListener);
        }
    }

    public static void addListenerDeeply(Node node, EventHandler<MouseEvent> listener) {
        node.addEventHandler(MouseEvent.MOUSE_MOVED, listener);
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, listener);
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, listener);
        node.addEventHandler(MouseEvent.MOUSE_EXITED, listener);
        node.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, listener);
        if (node instanceof Parent) {
            Parent parent = (Parent) node;
            ObservableList<Node> children = parent.getChildrenUnmodifiable();
            for (Node child : children) {
                addListenerDeeply(child, listener);
            }
        }
    }

    static class ResizeListener implements EventHandler<MouseEvent> {
        private Stage stage;
        private Cursor cursorEvent = Cursor.DEFAULT;
        public static int border = 10;
        private double startParameterX = 0; // Wenn eine unsichtbare Pane für den Schlagschatten, hinter der Content-Pane liegt, dann muss die Breite der unsichtbaren Pane korrigiert
        private double startParameterY = 0; // werden, sonst springt das Fenster auf den Startpunkt, der unsichtbaren Pane, beim MouseEvent.MOUSE_DRAGGED
        private double startX = 0;
        private double startY = 0;

        public ResizeListener(Stage stage) {
            this.stage = stage;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
            Scene scene = stage.getScene();

            double mouseEventX = mouseEvent.getSceneX(),
                   mouseEventY = mouseEvent.getSceneY(),
                   sceneWidth = scene.getWidth(),
                   sceneHeight = scene.getHeight();

            if (MouseEvent.MOUSE_MOVED.equals(mouseEventType) == true && !stage.isMaximized()) {
                if (mouseEventX < border && mouseEventY < border) {
                    cursorEvent = Cursor.NW_RESIZE;
                } else if (mouseEventX < border && mouseEventY > sceneHeight - border) {
                    cursorEvent = Cursor.SW_RESIZE;
                } else if (mouseEventX > sceneWidth - border && mouseEventY < border) {
                    cursorEvent = Cursor.NE_RESIZE;
                } else if (mouseEventX > sceneWidth - border && mouseEventY > sceneHeight - border) {
                    cursorEvent = Cursor.SE_RESIZE;
                } else if (mouseEventX < border) {
                    cursorEvent = Cursor.W_RESIZE;
                } else if (mouseEventX > sceneWidth - border) {
                    cursorEvent = Cursor.E_RESIZE;
                } else if (mouseEventY < border) {
                    cursorEvent = Cursor.N_RESIZE;
                } else if (mouseEventY > sceneHeight - border) {
                    cursorEvent = Cursor.S_RESIZE;
                } else {
                    cursorEvent = Cursor.DEFAULT;
                }
                scene.setCursor(cursorEvent);
            } else if(MouseEvent.MOUSE_EXITED.equals(mouseEventType) || MouseEvent.MOUSE_EXITED_TARGET.equals(mouseEventType)){
                scene.setCursor(Cursor.DEFAULT);
            } else if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType) == true) {

            	System.out.println("MOUSE_PRESSED: " + mouseEvent.getScreenY() + " | stage: " + stage.getY());
                startX = stage.getWidth() - mouseEventX;
                startY = stage.getHeight() - mouseEventY;
            } else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType) == true) {
                if (Cursor.DEFAULT.equals(cursorEvent) == false) {
                    if (Cursor.W_RESIZE.equals(cursorEvent) == false && Cursor.E_RESIZE.equals(cursorEvent) == false) {
                        double minHeight = stage.getMinHeight() > (border*2) ? stage.getMinHeight() : (border*2);
                        if (Cursor.NW_RESIZE.equals(cursorEvent) == true || Cursor.N_RESIZE.equals(cursorEvent) == true || Cursor.NE_RESIZE.equals(cursorEvent) == true) {
                            if (stage.getHeight() > minHeight || mouseEventY < 0) {
                            	System.out.println("MOUSE_DRAGGED: " + mouseEvent.getScreenY() + " | stage: " + stage.getY());
                            	double saveY = stage.getY();
                                stage.setY(mouseEvent.getScreenY() - startParameterY);
                                stage.setHeight((saveY + startParameterY) - mouseEvent.getScreenY() + stage.getHeight());
                                System.out.println("Y oben");
                            }
                        } else {
                            if (stage.getHeight() > minHeight || mouseEventY + startY - stage.getHeight() > 0) {
                                stage.setHeight(mouseEventY + startY);
                                System.out.println("Y unten");
                            }
                        }
                    }

                    if (Cursor.N_RESIZE.equals(cursorEvent) == false && Cursor.S_RESIZE.equals(cursorEvent) == false) {
                        double minWidth = stage.getMinWidth() > (border*2) ? stage.getMinWidth() : (border*2);
                        if (Cursor.NW_RESIZE.equals(cursorEvent) == true || Cursor.W_RESIZE.equals(cursorEvent) == true || Cursor.SW_RESIZE.equals(cursorEvent) == true) {
                            if (stage.getWidth() > minWidth || mouseEventX < 0) {
                            	double saveX = stage.getX();
                                stage.setX(mouseEvent.getScreenX() - startParameterX);
                                stage.setWidth((saveX + startParameterX) - mouseEvent.getScreenX() + stage.getWidth());
                                System.out.println("X links");
                            }
                        } else {
                            if (stage.getWidth() > minWidth || mouseEventX + startX - stage.getWidth() > 0) {
                                stage.setWidth(mouseEventX + startX);
                                System.out.println("X rechts");
                            }
                        }
                    }
                }

            }
        }
    }
}
