package app.watcher.watchService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.controller.CTree;
import app.interfaces.ILockDir;
import app.threads.WaitUnlockTask;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class LockWatcher extends Thread implements ILockDir {
	
   private CTree cTree;
   private Path rootDir;
   private final WatchService watcher;
   private final Map<WatchKey, Path> keys;
   private List<String> filterList = new ArrayList<>(Arrays.asList(CTree.refreshFileName));
   
//   private WatchService watchService;
//   private Path dirToWatch;

   public LockWatcher(CTree cTree, WatchService watchService, Path dirToWatch) throws IOException {
	   	this.cTree = cTree;
	  	this.rootDir = dirToWatch;
	    this.watcher = FileSystems.getDefault().newWatchService();
	    this.keys = new HashMap<WatchKey, Path>();
    
	    walkLevelOneAndRegisterDirectories(dirToWatch);
	    
//      this.watchService = watchService;
//      this.dirToWatch = dirToWatch;
   }


   private void registerDirectory(Path dir) throws IOException {
	   
	   	if (!isForbiddenFile(dir)) {
			System.out.println("Registrier: " + dir);
	   		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
	   		keys.put(key, dir);
		}	  
   }
   
   private boolean isForbiddenFile(Path p) {
	   for (String string : filterList) {
		   if (p.getFileName().toString().equalsIgnoreCase(string)) {
			   return true;
		   }
	   }
	   return false;
   }
   
   private void walkLevelOneAndRegisterDirectories(final Path start) throws IOException {
   	
	   	registerDirectory(start);
	   	
	   	File[] files = start.toFile().listFiles();
	   	if (files != null) { // Erforderliche Berechtigungen etc. sind vorhanden
	   		for (int i = 0; i < files.length; i++) {
	   			if (files[i].isDirectory()) {
	   				registerDirectory(files[i].toPath());
	   			}
	   		}
	   	}

   }
   
   @Override
   public void run() {
      System.out.println("LockWatcher() -> is running");
      for(;;) {
    	  
          // wait for key to be signalled
          WatchKey key;
          try {
              key = watcher.take();
          } catch (InterruptedException x) {
              return;
          }

          Path dir = keys.get(key);
          if (dir == null) {
              System.err.println("WatchKey not recognized!!");
              continue;
          }

          for (WatchEvent<?> event : key.pollEvents()) {
              @SuppressWarnings("rawtypes")
              WatchEvent.Kind kind = event.kind();

              // Context for directory entry event is the file name of entry
              @SuppressWarnings("unchecked")
              Path name = ((WatchEvent<Path>)event).context();
              Path child = dir.resolve(name);

              // print out event
//              System.out.format("%s: %s\n", event.kind().name(), child.toFile().getName());
              
              boolean isLockFile = child.toFile().getName().equalsIgnoreCase(CTree.lockFileName);
              
              if(kind == ENTRY_CREATE && isLockFile) {
            	  System.out.println("isDirLocked()" + child.toFile());
            	  isDirLocked(cTree.getTree().getRoot(), child.toFile().getParentFile());
              } else if (kind == ENTRY_DELETE && isLockFile) {
            	  
            	  if (CTree.isInternalChange) {
            		  	isDirLocked(cTree.getTree().getRoot(), child.toFile().getParentFile());
            		  	CTree.isInternalChange = false;
            	  } else {
		            	WaitUnlockTask WaitUnlockTask = new WaitUnlockTask(cTree.getTree().getRoot(), child.toFile().getParentFile());
		            	new Thread(WaitUnlockTask).start();
		            	
//		            	isDirLocked(cTree.getTree().getRoot(), child.toFile().getParentFile());
            	  }

              }

              
              
              // if directory is created, and watching recursively, then register it and its sub-directories
              if (kind == ENTRY_CREATE && child.getParent().toString().equalsIgnoreCase(rootDir.toString())) {
              	try {
						registerDirectory(child);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
              }
          }

          // reset key and remove from set if directory no longer accessible
          boolean valid = key.reset();
          if (!valid) {
              keys.remove(key);

              // all directories are inaccessible
              if (keys.isEmpty()) {
                  break;
              }
          }
    	  
    	  
//    	  System.out.println("LockWatcher " + 1);
//         try {
//        	 System.out.println("LockWatcher " + 2);
//            WatchKey key = watchService.take(); // blockiert
//            System.out.println("LockWatcher " + 3);
//            List<WatchEvent<?>> eventList = key.pollEvents();
//            System.out.println("size = " + eventList.size());
//            for(WatchEvent<?> e : eventList) {
////               System.out.println("LockWatcher -> " + e.kind() );
//               Path name = (Path)e.context();
//               System.out.println("LockWatcher " + e.kind() + " -> " + name);
//               //System.out.print(name.getParent());
//               // context liefert nur den Dateinamen, parent ist null !
//               Path path = dirToWatch.resolve(name);              
//               if (Files.isDirectory(path)) {
//            	   isDirLocked(cTree.getTree().getRoot(), path.toFile());
//            	   
//            	 
////            	  if () {
////            		  System.out.println("locked -> " + path);
////            	  } else {
////            		  System.out.println("unlocked -> " + path);
////            	  }
//               }
//            }
//            boolean valid = key.reset();
//            if (!valid) {
//               break;
//            }
//         }
//         catch(InterruptedException ex) {
//            ex.printStackTrace();
//            return;
//         }
      } 

   } 


   
} 


