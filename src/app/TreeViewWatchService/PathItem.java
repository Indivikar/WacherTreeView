package app.TreeViewWatchService;

import java.nio.file.Path;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TreeItem;

public class PathItem {
	
    private Path path;
    private boolean isDirectoryItem;
    private TreeItem<PathItem> levelOneItem;
    private int countNewDir = 0;
    private int level;    
    private int row;
    private boolean isLocked;
    private boolean isRefreshTree;
    private SimpleIntegerProperty rowProp = new SimpleIntegerProperty();
    private SimpleBooleanProperty isLockedProp = new SimpleBooleanProperty();
    private SimpleBooleanProperty isRefreshTreeProp = new SimpleBooleanProperty();
    
    public PathItem(Path path, boolean isDirectoryItem) {
    	this.path = path;
    	this.isDirectoryItem = isDirectoryItem;
    	setLocked(false);
    }
    
    // Getter
    public Path getPath() {return path;}
    public boolean isDirectoryItem() {return isDirectoryItem;}
    public TreeItem<PathItem> getLevelOneItem() {return levelOneItem;}
	public int getLevel() {return level;}    
    public int getRow() {return row;}
	public SimpleIntegerProperty getRowProp() {return rowProp;}	
	public boolean isLocked() {return isLocked;}
	public boolean isRefreshTree() {return isRefreshTree;}
	public SimpleBooleanProperty getIsLockedProp() {return isLockedProp;}
	public SimpleBooleanProperty getIsRefreshTreeProp() {return isRefreshTreeProp;}

	// Setter
	public void setPath(Path path) {this.path = path;}
	public void setDirectoryItem(boolean isDirectoryItem) {this.isDirectoryItem = isDirectoryItem;}
	public void setLevelOneItem(TreeItem<PathItem> levelOneItem) {this.levelOneItem = levelOneItem;}
	public void setLevel(int level) {this.level = level;}	
	public void setRow(int row) {
		this.row = row;
		this.rowProp.setValue(row);
	}
	
    public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
		this.isLockedProp.setValue(isLocked);
	}

    public void setRefreshTree(boolean isRefreshTree) {
		this.isRefreshTree = isRefreshTree;
		this.isRefreshTreeProp.setValue(isRefreshTree);
	}
    
	@Override
    public String toString() {
        if (path.getFileName() == null) {
            return path.toString();
        } else {
            return path.getFileName().toString();
        }
    }
	

	public String toString2() {
		return "PathItem [path=" + path + ", isDirectoryItem=" + isDirectoryItem + ", levelOneItem=" + levelOneItem
				+ ", countNewDir=" + countNewDir + ", level=" + level + ", row=" + row + ", isLocked=" + isLocked
				+ ", rowProp=" + rowProp + ", isLockedProp=" + isLockedProp + "]";
	}
	
    public int getCountNewDir() {
        return ++this.countNewDir;
    }
}
