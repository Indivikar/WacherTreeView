package app;

import javafx.application.Application;

/**
 * Created 2016 by Jordan Martinez
 *
 * The author dedicates this to the public domain
 */




import javafx.scene.Scene;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import app.funktionen.LiveDirs;

public class NormalLiveDirs extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TreeView<Path> view = new TreeView<>();
        view.setShowRoot(false);

        try {
            // create a LiveDirs instance for use on the JavaFX Application Thread
            LiveDirs<ChangeSource, Path> dirs = LiveDirs.getInstance(ChangeSource.EXTERNAL);

            // set directory to watch
            dirs.addTopLevelDirectory(new File("F:\\Test").toPath());
            view.setRoot(dirs.model().getRoot());

            // stop DirWatcher's thread
            primaryStage.setOnCloseRequest(val -> dirs.dispose());
        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.setScene(new Scene(view, 500, 500));
        primaryStage.show();
    }
}
