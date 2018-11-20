package app.test.CopyFilesApp;

import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.io.IOException;

/*
 * Handler to write log messages to a log file.
 */
public class LogFileHandler extends FileHandler {


    public LogFileHandler(String pattern)
            throws IOException {
        
        super(pattern);
        setFormatter(new SimpleFormatter()); // overrides the default xml formatter
    }
}