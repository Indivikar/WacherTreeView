package app.models;

import java.nio.file.Path;

public class SourceTarget {

//	private Path file;
	private Path source;
	private Path target;
	
	public SourceTarget(Path source, Path target) {
//		this.file = file;
		this.source = source;
		this.target = target;
	}

	// Getter
	public Path getSource() {return source;}
	public Path getTarget() {return target;}
//	public Path getFile() {return file;}

	// Setter
	public void setSource(Path source) {this.source = source;}
	public void setTarget(Path target) {this.target = target;}
//	public void setFile(Path file) {this.file = file;}	

}
