package app.original;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

/**
 * @author eddie
 * @source http://examples.javacodegeeks.com/core-java/apache/commons/io-commons/monitor/filealterationmonitor/org-apache-commons-io-monitor-filealterationmonitor-example/
 *
 */
public class FileAlterationListenerImpl implements FileAlterationListener {

	private long start;
	
	@Override
	public void onStart(final FileAlterationObserver observer) {
		start = new Date().getTime();
		System.out.println("The FileListener has started on "
				+ observer.getDirectory().getAbsolutePath());
	}

	@Override
	public void onDirectoryCreate(final File directory) {
		System.out.println(directory.getAbsolutePath() + " was created.");
	}

	@Override
	public void onDirectoryChange(final File directory) {
		System.out.println(directory.getAbsolutePath() + " wa modified");
	}

	@Override
	public void onDirectoryDelete(final File directory) {
		System.out.println(directory.getAbsolutePath() + " was deleted.");
	}

	@Override
	public void onFileCreate(final File file) {
		System.out.println(file.getAbsoluteFile() + " was created.");
		System.out.println("1. length: " + file.length());
		System.out
				.println("2. last modified: " + new Date(file.lastModified()));
		System.out.println("3. readable: " + file.canRead());
		System.out.println("4. writable: " + file.canWrite());
		System.out.println("5. executable: " + file.canExecute());
	}

	@Override
	public void onFileChange(final File file) {
		System.out.println(file.getAbsoluteFile() + " was modified.");
		System.out.println("1. length: " + file.length());
		System.out
				.println("2. last modified: " + new Date(file.lastModified()));
		System.out.println("3. readable: " + file.canRead());
		System.out.println("4. writable: " + file.canWrite());
		System.out.println("5. executable: " + file.canExecute());
	}

	@Override
	public void onFileDelete(final File file) {
		System.out.println(file.getAbsoluteFile() + " was deleted.");
	}

	@Override
	public void onStop(final FileAlterationObserver observer) {
		long runningTime = new Date().getTime() - start;
		System.out.println("The FileListener has stopped after " + runningTime + "(ms) on "
				+ observer.getDirectory().getAbsolutePath());
	}

}
