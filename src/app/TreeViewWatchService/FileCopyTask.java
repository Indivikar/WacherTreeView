package app.TreeViewWatchService;

import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.EnumSet;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * File copy Task
 * 
 * @author tomo
 */
public class FileCopyTask extends Task<Void> {
    private Path source;
    private Path target;

    public FileCopyTask(Path source, Path target) {
        this.source = source;
        this.target = target; 
    }
    
    @Override
    protected Void call() throws Exception {
//        Files.move(this.source, this.target);
        System.out.println("Start FileCopyTask");
        Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                Integer.MAX_VALUE, new CopyDirectory(source, target, this));
        wait();
        System.out.println("Ende FileCopyTask");
        
        return null;
    }
}
