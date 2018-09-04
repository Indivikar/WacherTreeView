package app.TreeViewWatchService;

import java.io.File;
import java.nio.file.SimpleFileVisitor;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javafx.beans.property.SimpleStringProperty;

public class ModelFileChanges {

    private SimpleStringProperty action;    
    private File file;
    private SimpleStringProperty fileString;
    private Timestamp timestamp;
    private SimpleStringProperty time;
    private long timeInMilli;
        
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    
	public ModelFileChanges(String action, File file, Timestamp timestamp) {
		this.action = new SimpleStringProperty(action);		
		this.file = file;
		this.fileString = new SimpleStringProperty(file.getAbsolutePath());
		this.timestamp = timestamp;
		this.time = new SimpleStringProperty(sdf.format(timestamp) + "");
		this.timeInMilli = timestamp.getTime();
		
	}

	// Getter
	public String getAction() {return action.get();}
	public File getFile() {return file;}
	public String getFileString() {return fileString.get();}	
	public Timestamp getTimestamp() {return timestamp;}
	public long getTimeInMilli() {return timeInMilli;}
	public String getTime() {return time.get();}	
	
	// Setter
	public void setAction(String action) {this.action.set(action);}
	public void setFile(File file) {this.file = file;}
	public void setFileString(String fileString) {this.fileString.set(fileString);}
	public void setTimestamp(Timestamp timestamp) {this.timestamp = timestamp;}
	public void setTimeInMilli(long timeInMilli) {this.timeInMilli = timeInMilli;}
	public void setTime(String time) {this.time.set(time);}


	
	
	
}
