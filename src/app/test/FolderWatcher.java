package app.test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;


import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;

public class FolderWatcher {

	private static volatile Boolean mStop = false;
//	static Logger log = Logger.getLogger(FolderWatcher.class);
	public static Integer mExecId = 0;
	
	private static final String inputDirPath = "D:\\Test\\___DB___";

	public static void main(String[] args) {

		// Registering Shutdown hook
		final Thread mainThread = Thread.currentThread();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
//				log.info("Shutdown hook invoked, Application will terminate...");
				System.out.println("Shutdown hook invoked, Application will terminate...");
				mStop = true;
				try {
					mainThread.join();
				} catch (InterruptedException e) {
//					log.error("Error in application shutdown", e);
				}
			}
		});

		// Get the directory we want to watch, using the Paths singleton class
		Path pathToFolder = null;

		try {
			pathToFolder = Paths.get(inputDirPath);
			// FileUtil.pathValidator("FILE_INPUT_DIRECTORY");
		} catch (Exception e) {
			String lLogMsg = "Error in reading Input directories, ensure directories " +
					"are configured and accessible, System will now exit";
//			log.error(lLogMsg, e);
			System.exit(1);
		}

		try {
			// Make a new watch service that we can register interest in
			// directories and files with.
			WatchService watchFolder = pathToFolder.getFileSystem().newWatchService();
			// start the file watcher thread below
			WatchQueueReader watchQueueReader = new WatchQueueReader(watchFolder);
			Thread th1 = new Thread(watchQueueReader, "FILE_INPUT_DIRECTORY");

			th1.start();

			// register a file event
			pathToFolder.register(watchFolder, ENTRY_CREATE, ENTRY_MODIFY);

			// Check for existing files which might have accumulated while this
			// application was not running
			// The following code will trigger the watch service for any
			// existing file
			File dir = new File(inputDirPath);
			triggerWatchService(dir);
		} catch (IOException ioex) {
			String lLogMsg = "IOException in File Watcher service";
//			log.error(lLogMsg, ioex);
			System.exit(1);
		}

	}

	/***
	 * Static method invoked for triggering watch service for existing files in
	 * the input directories
	 */
	private static void triggerWatchService(File dir) throws IOException {
		File[] dirContent = dir.listFiles();
		for (File f : dirContent) {
			if (!f.isDirectory()) {
//				log.debug("Processing existing file: " + f.getName());
				System.out.println("Processing existing file: " + f.getName());
				f.setLastModified(f.lastModified());
			}
		}
	}

	/**
	 * This Runnable is used to constantly attempt to take from the watch queue,
	 * and will receive all events that are registered with the fileWatcher it
	 * is associated.
	 */
	private static class WatchQueueReader implements Runnable {

		/** the watchService that is passed in from above */
		private WatchService watchService;

		public WatchQueueReader(WatchService watchService) {
			this.watchService = watchService;
		}

		/**
		 * In order to implement a file watcher, we loop forever ensuring
		 * requesting to take the next item from the file watchers queue.
		 */
		@Override
		public void run() {
			try {
				// get the first event before looping
				WatchKey key = watchService.take();
				Thread t = Thread.currentThread();

				while (key != null && !mStop) {
					// we have a polled event, now we traverse it and
					// receive all the states from it
					for (WatchEvent event : key.pollEvents()) {

//						log.debug(t.getName() + ": Received " + event.kind() + " event for file: " + event.context());
						System.out.println(t.getName() + ": Received " + event.kind() + " event for file: " + event.context());
						if (t.getName().equals("FILE_INPUT_DIRECTORY")) {
							// Process and parse the file
//							log.info("FILE_INPUT_DIRECTORY: " +  event.context());
							System.out.println("FILE_INPUT_DIRECTORY: " +  event.context());
							/**
							 *  do something here with the file
							 *  event.context() will give the file name
							 *  event.kind() will tell what kind of event occured
							 */
						}
						key.reset();
						key = watchService.take();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			log.info("All application threads stopped, Shutting down...");
		}
	}

}
