package app.TreeViewWatchService;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.FileUtils;

import app.view.Stages.StageFileIsExist;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class CopyDirectory extends SimpleFileVisitor<Path> {
	  private Object lock = new Object();
	
	  private Path source;
	  private Path target;
	  private FileCopyTask fileCopyTask;
	  
	  private boolean isSameForAll = false;
	  private boolean replaceYes = false;
	  
	  public CopyDirectory(Path source, Path target, FileCopyTask fileCopyTask) {
		    this.source = source;
		    this.target = target;
		    this.fileCopyTask = fileCopyTask;
		    	    
		    System.out.println("CopyDirectory: target -> " + target);
	  }

	  
	    public void next(){
	        synchronized(lock){
	            lock.notify();
	        }   
	    }
	  
	  
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) 
              throws IOException {
                 
          Path fileTarget = target.resolve(source.relativize(dir));
          System.out.format("preVisitDirectory: %s  ->  ", dir);   
          System.out.format("target: %s\n", fileTarget);
          
          if (fileTarget.toFile().exists()) {
  			System.err.println("    exists -> " + fileTarget);
          } else {
        	  fileTarget.toFile().mkdir();
          }
         
          return super.preVisitDirectory(dir, attrs);
      }

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) 
              throws IOException {
          Path fileTarget = target.resolve(source.relativize(file));
          System.out.format("visitFile: %s  ->  ", file);   
          System.out.format("target: %s\n", fileTarget);
          
          if (fileTarget.toFile().exists()) {
  			System.err.println("    exists -> " + fileTarget);
  			
		  	if (isSameForAll) {
		  		  if (replaceYes) {
		  			copyAndDeleteOldFile(file, fileTarget);
		  		  }        		  
		  	  }  else {		 		
		  		  	new StageFileIsExist(this, file);
		  		
					synchronized(lock){
			            try {
			                lock.wait();
			            } catch (InterruptedException e1) {
			                e1.printStackTrace();
			            }
					}
				
				    if (replaceYes) {
			    		System.out.println("Source: " + this.source + "  ->  target: " + this.target);
			    		copyAndDeleteOldFile(file, fileTarget);
				    }		
		  	  }
  			
          } else {
//        	  System.err.println("    Copy File -> " + fileTarget);
        	  copyAndDeleteOldFile(file, fileTarget);
          }        
          return super.visitFile(file, attrs);
      }
	  
      @Override
      public FileVisitResult visitFileFailed(Path file, IOException exc) 
              throws IOException {
    	  // TODO - Fehlermeldung einbauen, wird ausgelöst, wenn eine Datei nicht gelesen werden kann
          Path fileTarget = target.resolve(source.relativize(file));
          System.out.format("visitFileFailed: %s  ->  ", file);   
          System.out.format("target: %s\n", fileTarget);
          
          if (fileTarget.toFile().exists()) {
  			System.err.println("    exists -> " + fileTarget);
          }   
          
          return super.visitFileFailed(file, exc);
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) 
              throws IOException {
//          System.out.format("postVisitDirectory: %s\n", dir);
    	  
    	  if (isDirectoryEmpty(dir)) {
    		  dir.toFile().delete();
    	  }
    	  
          return super.postVisitDirectory(dir, exc);
      }
       
      
      private void copyAndDeleteOldFile(Path file, Path fileTarget) {
	    	try {
				Files.copy(file, fileTarget, StandardCopyOption.REPLACE_EXISTING);
				if (fileTarget.toFile().exists()) {
		    		file.toFile().delete();
		    	}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	  
      }
      
      private boolean isDirectoryEmpty(final Path directory) throws IOException {
    	    try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
    	        return !dirStream.iterator().hasNext();
    	    }
    	}
      
//	  @Override
//	  public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
//	      throws IOException {
//	    System.out.println("Copying 1: " + source.relativize(file) + " -> " + target.resolve(source.relativize(file)));
//	    Files.copy(file, target.resolve(source.relativize(file)));
//	    return FileVisitResult.CONTINUE;
//	  }
//
//	  @Override
//	  public FileVisitResult preVisitDirectory(Path directory,
//	      BasicFileAttributes attributes) throws IOException {
//	    Path targetDirectory = target.resolve(source.relativize(directory));
//	    try {
//	      System.out.println("Copying 2: " + source.relativize(directory) + " -> " + targetDirectory);
//	      Files.copy(directory, targetDirectory);
//	    } catch (FileAlreadyExistsException e) {
//	      if (!Files.isDirectory(targetDirectory)) {
//	        throw e;
//	      }
//	    }
//	    return FileVisitResult.CONTINUE;
//	  }
	  
     
      
//	  private void alertExists(Path path) {
//		    Alert alert = createAlertWithOptOut(AlertType.CONFIRMATION, "Exit", null, 
//	                  "This file \"" +  + "\" is exists. Are you replace this file", "same for all files" , ButtonType.YES, ButtonType.NO);
//		    if (alert.showAndWait().filter(t -> t == ButtonType.YES).isPresent()) {
//	
//		    }
//	  }
      
      
	  public Alert createAlertWithOptOut(AlertType type, String title, String headerText, 
		              String message, String optOutMessage,
		              ButtonType... buttonTypes) {
		  Alert alert = new Alert(AlertType.CONFIRMATION);
		  // Need to force the alert to layout in order to grab the graphic,
		   // as we are replacing the dialog pane with a custom pane
		   alert.getDialogPane().applyCss();
		   Node graphic = alert.getDialogPane().getGraphic();
		   // Create a new dialog pane that has a checkbox instead of the hide/show details button
		   // Use the supplied callback for the action of the checkbox
		   alert.setDialogPane(new DialogPane() {
		     @Override
		     protected Node createDetailsButton() {
		       CheckBox optOut = new CheckBox();
		       optOut.setText(optOutMessage);
		       optOut.setOnAction(e -> isSameForAll = optOut.isSelected());
		       return optOut;
		     }
		   });
		   alert.getDialogPane().getButtonTypes().addAll(buttonTypes);
		   alert.getDialogPane().setContentText(message);
		   // Fool the dialog into thinking there is some expandable content
		   // a Group won't take up any space if it has no children
		   alert.getDialogPane().setExpandableContent(new Group());
		   alert.getDialogPane().setExpanded(true);
		   // Reset the dialog graphic using the default style
		   alert.getDialogPane().setGraphic(graphic);
		   alert.setTitle(title);
		   alert.setHeaderText(headerText);
		   
		   return alert;
	}


	public boolean isSameForAll() {return isSameForAll;}
	public boolean isReplaceYes() {return replaceYes;}
	
	
	public void setReplaceYes(boolean replaceYes) {this.replaceYes = replaceYes;}
	public void setSameForAll(boolean isSameForAll) {this.isSameForAll = isSameForAll;}  
	  
	  
}
