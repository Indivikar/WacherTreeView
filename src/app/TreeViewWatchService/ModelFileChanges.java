package app.TreeViewWatchService;

import java.io.File;
import java.nio.file.SimpleFileVisitor;

import javafx.beans.property.SimpleStringProperty;

public class ModelFileChanges {

    private SimpleStringProperty action;    
    private File file;
    private SimpleStringProperty fileString;
    private SimpleStringProperty time;
    
	public ModelFileChanges(String action, File file, String time) {
		this.action = new SimpleStringProperty(action);		
		this.file = file;
		this.fileString = new SimpleStringProperty(file.getAbsolutePath());
		this.time = new SimpleStringProperty(time);
	}

	// Getter
	public String getAction() {return action.get();}
	public File getFile() {return file;}
	public String getFileString() {return fileString.get();}	
	public String getTime() {return time.get();}	
	
	// Setter
	public void setAction(String action) {this.action.set(action);}
	public void setFile(File file) {this.file = file;}
	public void setFileString(String fileString) {this.fileString.set(fileString);}
	public void setTime(String time) {this.time.set(time);}


	
	
	
}
