package app.test.ShowDialogOnException;

import java.lang.Thread.UncaughtExceptionHandler;
import java.security.Permission;
import java.util.concurrent.FutureTask;

import app.StartWacherDemo;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ShowDialogOnException  {

    public static final UncaughtExceptionHandler ALERT_EXCEPTION_HANDLER = (thread, cause) -> {
        try {
            cause.printStackTrace();
            final Runnable showDialog = () -> {
               Alert alert = new Alert(AlertType.ERROR);
               alert.setContentText("An unknown error occurred");
               alert.showAndWait();
            };
            if (Platform.isFxApplicationThread()) {
               showDialog.run();
            } else {
               FutureTask<Void> showDialogTask = new FutureTask<Void>(showDialog, null);
               Platform.runLater(showDialogTask);
               showDialogTask.get();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            System.exit(-1);
        }
    };



    public static void main(String[] args) {
        System.setSecurityManager(new SecurityManager(){
            @Override
            public void checkPermission(Permission perm) {}
        });
        Thread.setDefaultUncaughtExceptionHandler(ALERT_EXCEPTION_HANDLER);
        Application.launch(StartWacherDemo.class, args);
    }
}