package app.models;

import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class MLockFile {

	private FileChannel raf;
	private FileLock lock;
	

	public MLockFile(FileChannel raf, FileLock lock) {
		this.raf = raf;
		this.lock = lock;
	}

	public FileChannel getRaf() {return raf;}
	public FileLock getLock() {return lock;}	
	
	public void setRaf(FileChannel raf) {this.raf = raf;}
	public void setLock(FileLock lock) {this.lock = lock;}
	
	

}
