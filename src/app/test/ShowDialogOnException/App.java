package app.test.ShowDialogOnException;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        throw new Exception("An exception");
    }

    @Override
    public void stop() {
        System.out.println("Stop");
    }

}
