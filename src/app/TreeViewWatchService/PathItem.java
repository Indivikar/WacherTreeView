package app.TreeViewWatchService;

import java.nio.file.Path;

import javafx.beans.property.SimpleIntegerProperty;

public class PathItem {
	
    private Path path;
    private int countNewDir = 0;
    private int row;
    private SimpleIntegerProperty rowProp = new SimpleIntegerProperty();
    
    public PathItem(Path path) {
    	this.path = path;
    }
    
    // Getter
    public Path getPath() {return path;}
    public int getRow() {return row;}
	public SimpleIntegerProperty getRowProp() {return rowProp;}

	// Setter
	public void setPath(Path path) {this.path = path;}
	public void setRow(int row) {
		this.row = row;
		this.rowProp.setValue(row);
	}
    

	@Override
    public String toString() {
        if (path.getFileName() == null) {
            return path.toString();
        } else {
            return path.getFileName().toString();
        }
    }
    public int getCountNewDir() {
        return ++this.countNewDir;
    }
}
