package app.TreeViewWatchService;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import app.StartWacherDemo;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class PathTreeCell extends TreeCell<PathItem>{
    private TextField textField;
    private Path editingPath;
    private StringProperty messageProp;
    private ContextMenu dirMenu = new ContextMenu();
    private ContextMenu fileMenu = new ContextMenu();
    

    public PathTreeCell() {
        this.messageProp = messageProp;       
        contextMenu();
    }

    private void contextMenu() {
      MenuItem expandMenu = new MenuItem("Expand");
      expandMenu.setOnAction((ActionEvent event) -> {
          getTreeItem().setExpanded(true);
      });
      MenuItem addFile = new MenuItem("neue Datei");
      addFile.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent t) {
              
          }
         
      });
      MenuItem addFolder = new MenuItem("neuer Ordner");
      addFolder.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent t) {
              Path newDir = createNewDirectory();
              if (newDir != null) {
                  TreeItem<PathItem> addItem = PathTreeItem.createNode(new PathItem(newDir));
                  getTreeItem().getChildren().add(addItem);
              }
          }
          private Path createNewDirectory() {
              Path newDir = null;
              while (true) {
                  Path path = getTreeItem().getValue().getPath();
                  newDir = Paths.get(path.toAbsolutePath().toString(), "neuer Ordner " + String.valueOf(getItem().getCountNewDir()));
                  try {
                      Files.createDirectory(newDir);
                      break;
                  } catch (FileAlreadyExistsException ex) {
                      continue;
                  } catch (IOException ex) {
                      cancelEdit();
                      messageProp.setValue(String.format("Creating directory(%s) failed", newDir.getFileName()));
                      break;
                  }
              }
                  return newDir;
          }
      });
      MenuItem deleteMenu = new MenuItem("Delete");
      deleteMenu.setOnAction((ActionEvent event) -> {
    	  System.out.println("Del 1: " + this.getItem().getPath());
    	  Path filePath = this.getItem().getPath();
//   	  setPermission(filePath);
    	  
//    	  deleteTree(filePath.toFile());
    	  
    	  
    	  
    	  try {
//    		  	FileUtils.cleanDirectory(filePath.toFile());
    		  	FileUtils.deleteDirectory(filePath.toFile());
//				Files.walk(filePath)
//				  .map(Path::toFile)
//				  .sorted((o1, o2) -> -o1.compareTo(o2))
//				  .forEach(File::delete);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	  
//			FileUtils.deleteDirectory(filePath.toFile());
//		FileUtils.deleteQuietly(filePath.toFile());
    	  
//		removeDir(filePath.toFile());
		
//    	  if (filePath.toFile().isDirectory()) {
//				try {
//					Files.walk(filePath)
//					    .sorted(Comparator.reverseOrder())
//					    .map(Path::toFile)
//					    .peek(System.out::println)
//					    .forEach(File::delete);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//    	  }
//    	  deleteTree(filePath.toFile());
//    	  deleteFiles(filePath);
    	  
    	  System.out.println("Del 1 fertig: " + this.getItem().getPath());
    	  
    		if (filePath.toFile().exists()) {
    			
//    			try {
//					Files.delete(filePath);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

    			
//				try {
//					Files.walkFileTree(filePath, new SimpleFileVisitor<Path>() {
//	    
//						@Override
//						public FileVisitResult visitFile(Path file,
//								BasicFileAttributes attrs) throws IOException {
//							Files.delete(file);
//							return FileVisitResult.CONTINUE;
//						}
//	    
//						@Override
//						public FileVisitResult postVisitDirectory(Path dir, IOException exc)
//								throws IOException {
//							Files.delete(dir);
//							return FileVisitResult.CONTINUE;
//						}
//					});
//				} catch (IOException e) {
//
//				
//					try {
//			            System.err.println("Executable: " + filePath.toFile().canExecute());
//			            System.err.println("Readable: " + filePath.toFile().canRead());
//			            System.err.println("Writable: "+ filePath.toFile().canWrite());
//						System.err.println("File Owner: " + Files.getOwner(filePath));
//						FileOwnerAttributeView view = Files.getFileAttributeView(filePath, FileOwnerAttributeView.class);	
//						UserPrincipal userPrincipal = view.getOwner();
//						System.err.println("File Owner: " + userPrincipal);   
//					} catch (IOException ex) {
//						// TODO Auto-generated catch block
//						ex.printStackTrace();
//					}
//				
//					e.printStackTrace();
//				}
			}


    		if (!filePath.toFile().exists()) {
				TreeItem c = (TreeItem)this.getTreeView().getSelectionModel().getSelectedItem();
	            boolean remove = c.getParent().getChildren().remove(c);
			}

    		
      });
      fileMenu.getItems().addAll(addFile, addFolder, deleteMenu);
		
	}
    


	public boolean removeDir(File dir) {
		
		
		
		try {
			FileUtils.cleanDirectory(dir);
			
		   //destFile = new File((System.getProperty("user.dir")+"/FileName"))
		   // checks if the directory has any file
		    if (dir.isDirectory())
		    {
		        File[] files = dir.listFiles();
		        if (files != null && files.length > 0)
		        {
		            for (File aFile : files) 
		            {
		                System.gc();
		                Thread.sleep(200);
		                FileDeleteStrategy.FORCE.delete(aFile);
		                System.out.println("delet file" +aFile);
		            }
		        }
		        dir.delete();
		        System.out.println("delet" +dir);
		    } 
		    else 
		    {
		        dir.delete();
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}


    
    private static void dir(File file) {
    	
    	System.err.println("kein zugriff -> " + file);
        System.out.println("	Executable: " + file.canExecute());
        System.out.println("	Readable: " + file.canRead());
        System.out.println("	Writable: "+ file.canWrite());
		try {
			System.out.println("	File Owner: " + Files.getOwner(file.toPath()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        FileSystem fileSystem = file.getParentFile().toPath().getFileSystem();
        UserPrincipalLookupService service = fileSystem.getUserPrincipalLookupService();
		UserPrincipal userPrincipal;
		try {
			userPrincipal = service.lookupPrincipalByName("DH");
			Files.setOwner(file.toPath(), userPrincipal);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		file.setExecutable(true);
		file.setReadable(true);
		file.setWritable(true);
		
    	System.err.println("\nkein zugriff -> " + file);
        System.out.println("	Executable: " + file.canExecute());
        System.out.println("	Readable: " + file.canRead());
        System.out.println("	Writable: "+ file.canWrite());
		try {
			System.out.println("	File Owner: " + Files.getOwner(file.toPath()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
    	System.err.println("\nkein zugriff Parent -> " + file.getParent());
        System.out.println("	Parent Executable: " + file.getParentFile().canExecute());
        System.out.println("	Parent Readable: " + file.getParentFile().canRead());
        System.out.println("	Parent Writable: "+ file.getParentFile().canWrite());
		try {
			System.out.println("	Parent File Owner: " + Files.getOwner(file.getParentFile().toPath()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "cd \"D:\\Test\" && dir");
            builder.redirectErrorStream(true);
            try {
				Process p = builder.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
    
    private void deleteFiles(Path filePath) {
    	
    	int count = 0;
    	
    	while (filePath.toFile().exists() && count < 10) {
			
					try {
						Files.walk(filePath)
						    .sorted(Comparator.reverseOrder())
						    .map(Path::toFile)
						    .peek(System.out::println)						    
						    .forEach(File::delete);
					} catch (IOException e) {
						count = 10;
						e.printStackTrace();
					}
	  	  	
			
			count++;
		}

	}
    
    public static void deleteTree( File path ) {

		      for ( File file : path.listFiles() ) {
		    	  try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        if ( file.isDirectory() ) {
		        	deleteTree( file );
		        } else {
			        if ( ! file.delete() ) {
			            System.err.println( file + " could not be deleted!" );
			        } else {
			        	System.out.println("refresh folder");
						dir(file);		        	
					}
		        }
		        
	      
      }

      if ( ! path.delete() )
        System.err.println( path + " could not be deleted!" );
    }
    
    
    public static void deleteTreeFile( File path ) {
    	
    	for ( File file : path.listFiles() ) {
    		try {
    			Thread.sleep(50);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		if ( file.isDirectory() ) {
    			deleteTree( file );
    		} else {
    			if ( ! file.delete() ) {
    				System.err.println( file + " could not be deleted!" );
    			} else {
    				System.out.println("refresh folder");
    				dir(file);		        	
    			}
    		}
    		
    		
    	}
    	
    	if ( ! path.delete() )
    		System.err.println( path + " could not be deleted!" );
    }

    
    public static void removeDirectory(Path directory) throws IOException
    {
        // does nothing if non-existent
        if (Files.exists(directory))
        {
            // prefer OS-dependent directory removal tool
			if (SystemUtils.IS_OS_WINDOWS)
//                    Processes.execute("%ComSpec%", "/C", "RD /S /Q \"" + directory + '"');
				Runtime.getRuntime().exec("%ComSpec%" + " /C" + " RD /S /Q \"" + directory + "\"");
//                else if (SystemUtils.IS_OS_UNIX)
//                    Processes.execute("/bin/rm", "-rf", directory.toString());

            if (Files.exists(directory))
                removeRecursive(directory);
        }
    }
    
    public static void removeRecursive(Path path) throws IOException
    {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException
            {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
            {
                // try to delete the file anyway, even if its attributes
                // could not be read, since delete-only access is
                // theoretically possible
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
            {
                if (exc == null)
                {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
                else
                {
                    // directory iteration failed; propagate exception
                    throw exc;
                }
            }
        });
    }
    
    private void setPermission(Path path) {
    	   File file = new File("/Users/pankaj/temp.txt");
           
//           //set application user permissions to 455
//           file.setExecutable(false);
//           file.setReadable(false);
//           file.setWritable(true);
//           
//           //change permission to 777 for all the users
//           //no option for group and others
//           file.setExecutable(true, false);
//           file.setReadable(true, false);
//           file.setWritable(true, false);
           
           //using PosixFilePermission to set file permissions 777
           Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
           //add owners permission
           perms.add(PosixFilePermission.OWNER_READ);
           perms.add(PosixFilePermission.OWNER_WRITE);
           perms.add(PosixFilePermission.OWNER_EXECUTE);
           //add group permissions
           perms.add(PosixFilePermission.GROUP_READ);
           perms.add(PosixFilePermission.GROUP_WRITE);
           perms.add(PosixFilePermission.GROUP_EXECUTE);
           //add others permissions
           perms.add(PosixFilePermission.OTHERS_READ);
           perms.add(PosixFilePermission.OTHERS_WRITE);
           perms.add(PosixFilePermission.OTHERS_EXECUTE);
           
           try {
//			Files.setPosixFilePermissions(Paths.get(path.toString()), perms);
			Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rw-r--r--"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
    protected void updateItem(PathItem item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
//            	System.out.println("updateItem: " + this.getItem().getPath());
//            	FileTreeViewSample.children.add(this);
                setText(getString());
                setGraphic(getImage(this.getTreeItem()));
                setContextMenu(fileMenu);
                
            }
        }
    }

	private ImageView getImage(TreeItem<PathItem> treeItem) {
    	ImageView imageView = new ImageView();
    	
    	File file = treeItem.getValue().getPath().toFile();
    	if (file.isDirectory()) {
			if (treeItem.isExpanded()) {
				imageView.setImage(setOpenIcon());
			} else {
				imageView.setImage(setCloseIcon());
			}
		} else {
			imageView.setImage(setDocumentIcon());
		}
    	
    	
		return imageView;
	}
	
	
	private Image setOpenIcon() {
		return new Image(StartWacherDemo.class.getResourceAsStream("view/images/folderOpen.png"));
	}
	
	private Image setCloseIcon() {
		return new Image(StartWacherDemo.class.getResourceAsStream("view/images/folderClose.png"));
	}
	
	private Image setDocumentIcon() {
		return new Image(StartWacherDemo.class.getResourceAsStream("view/images/document.png"));
	}
	
	
	
    @Override
    public void startEdit() {
        super.startEdit();
        if (textField == null){
            createTextField();
        }
        setText(null);
        setGraphic(textField);
        textField.selectAll();
        if (getItem() == null) {
            editingPath = null;
        } else {
            editingPath =getItem().getPath();
        }
    }

    @Override
    public void commitEdit(PathItem pathItem) {
        // rename the file or directory
        if (editingPath != null) {
            try {
                Files.move(editingPath, pathItem.getPath());
            } catch (IOException ex) {
                cancelEdit();
                messageProp.setValue(String.format("Renaming %s filed", editingPath.getFileName()));
            }
        }
        super.commitEdit(pathItem);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getString());
        setGraphic(null);
    }

    private String getString() {
        return getItem().toString();
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setOnKeyReleased((KeyEvent t) -> {
            if (t.getCode() == KeyCode.ENTER){
                Path path = Paths.get(getItem().getPath().getParent().toAbsolutePath().toString(), textField.getText());
                commitEdit(new PathItem(path));
            } else if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });
    }
    
    
}
