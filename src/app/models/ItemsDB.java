package app.models;

import java.nio.file.Path;

public class ItemsDB {

	private Path path;
	private boolean isDirectoryPath;
	
	public ItemsDB(Path path, boolean isDirectoryPath) {
		this.path = path;
		this.isDirectoryPath = isDirectoryPath;
	}

	public Path getPath() {return path;}
	public boolean isDirectoryPath() {return isDirectoryPath;}
	
	public void setPath(Path path) {this.path = path;}
	public void setDirectoryPath(boolean isDirectoryPath) {this.isDirectoryPath = isDirectoryPath;}

}
