package app.TreeViewWatchService;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;

import app.StartWacherDemo;
import app.controller.CTree;
import app.interfaces.ISuffix;
import app.interfaces.ISystemIcon;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import sun.awt.shell.ShellFolder;

public class PathTreeCell extends TreeCell<PathItem> implements ISuffix, ISystemIcon{
	private Stage primaryStage;
    private TextField textField;
    private Path editingPath;
    private StringProperty messageProp;
    private ContextMenu dirMenu = new ContextMenu();
    private ContextMenu fileMenu = new ContextMenu();
    private ExecutorService service = Executors.newFixedThreadPool(1);
    private ObservableList<String> listAllLockedFiles = FXCollections.observableArrayList();
   
    
    public PathTreeCell(Stage primaryStage) {
    	
        contextMenu();
        DragNDropInternal DragNDropInternal = new DragNDropInternal(primaryStage, service, this);
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
        	  FileAlterationListenerImpl.isInternalChange = true;
          }
         
      });
      MenuItem addFolder = new MenuItem("neuer Ordner");
      addFolder.setOnAction(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent t) {
        	  FileAlterationListenerImpl.isInternalChange = true;
              Path newDir = createNewDirectory();
              if (newDir != null) {            	  
            	  TreeItem<PathItem> newItem = new TreeItem<PathItem>(new PathItem(newDir));
            	  CTree.createTree(newItem, false);
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
      deleteMenu.setOnAction((event) -> {
    	  FileAlterationListenerImpl.isInternalChange = true;
    	  Path filePath = this.getItem().getPath();
    	  deleteFileOrDirectory(filePath.toFile());
    	  
      });
      
      fileMenu.getItems().addAll(addFile, addFolder, deleteMenu);		
	}

    private void deleteFileOrDirectory(File file) {
    	System.out.println("Del 1 fertig: " + this.getItem().getPath());
    	try {
			  if (file.isDirectory()) {
				    listAllLockedFiles.clear();
				    recursiveSearch(file);
				    if (listAllLockedFiles.size() > 0) {
						alertFilesLocked();
						return;
					}
				    				   
				  	int count = 0;
					while (file.exists() && count < 10) {
						
					Files.walk(file.toPath())
							.filter(f -> f.toFile().isDirectory())
							.sorted(Comparator.reverseOrder())
							.map(Path::toFile)
							.peek(System.out::println)
							.forEach(ordner -> {
								try {
									Thread.sleep(50);
									if (ordner.exists()) {
										FileUtils.cleanDirectory(ordner);
									}									
								} catch (IOException | InterruptedException e) {
									e.printStackTrace();
								}
							});
					
						Thread.sleep(50);
						if (file.exists()) {
							FileUtils.deleteDirectory(file);
						}						
			    		count++;
			    	}

			  } else {	   				  
					FileUtils.forceDelete(file);	
					Thread.sleep(1000);
			  }
		
		} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
    	
	}
    
    
    private void alertFilesLocked() {
    	Alert alert = new Alert(AlertType.ERROR);
    	alert.setTitle("Exception Dialog");
    	alert.setHeaderText("can not be deleted, it is still being edited");
    	alert.setContentText(null);

    	Label label = new Label("Locked Files:");

    	ListView<String> listLockedFiles = new ListView<>(listAllLockedFiles);

    	listLockedFiles.setMaxWidth(Double.MAX_VALUE);
    	listLockedFiles.setMaxHeight(Double.MAX_VALUE);
    	GridPane.setVgrow(listLockedFiles, Priority.ALWAYS);
    	GridPane.setHgrow(listLockedFiles, Priority.ALWAYS);

    	GridPane expContent = new GridPane();
    	expContent.setMaxWidth(Double.MAX_VALUE);
    	expContent.add(label, 0, 0);
    	expContent.add(listLockedFiles, 0, 1);

    	// Set expandable Exception into the dialog pane.
    	alert.getDialogPane().setExpandableContent(expContent);

    	alert.showAndWait();

	}
    
    private void recursiveSearch(File file) {
    	 File[] filesList = file.listFiles();
    	    for (File f : filesList) {
    	        if (f.isDirectory() && !f.isHidden()) {
    	            recursiveSearch(f);
    	        }
    	        if( f.isFile() ){
    	        	if (accessFile(f)) {
    	        		listAllLockedFiles.add(f.getAbsolutePath());
					}    	        	
    	        }
    	    }
    	}
    
    
    private boolean accessFile(File name) {
    		System.out.println("is File Locked: " + name);
    		System.out.println(name.canWrite()); // -> true
    		FileOutputStream fileOutputStream = null;
    		try {
    			fileOutputStream =  new FileOutputStream(name);
    			fileOutputStream.close();
			} catch (IOException e) {			
					System.out.println(e.getMessage());	
//					fileOutputStream.close();
					return true;						
			}
        
		return false;
    }
    
    
	@Override
    protected void updateItem(PathItem item, boolean empty) {
        super.updateItem(item, empty);
//         ImageView iconView = new ImageView();
        if (empty) {
            setText(null);
            setGraphic(null);
            setContextMenu(null);
        } else {
//            if (isEditing()) {
//                if (textField != null) {
//                    textField.setText(getString());
//                }
//                setText(null);
//                setGraphic(textField);
//                setContextMenu(null);
//            } else {
//            	System.out.println("updateItem: " + this.getItem().getPath());
//            	FileTreeViewSample.children.add(this);
//        	ImageView iconView = new ImageView();
        		ImageView iconView = getImage(this.getTreeItem());
//        		iconView.setImage(getImage(this.getTreeItem()).getImage());
        		String name = getString();
        		
                setText(name);
//                setGraphic(null);
                setGraphic(getImage(this.getTreeItem()));
//                setGraphic(iconView);
                setContextMenu(fileMenu);
//                iconView.setImage(getImage(this.getTreeItem()).getImage());
//                addSuffixAndImage(name, image);
                
//              if (getString().equalsIgnoreCase("test")) {
//					this.setDisable(true);
//				}
                
//            }
        }
    }


	
//	private String getSuffix(String dateiName){
//		
//		String suffix = null;
//		if ( dateiName.lastIndexOf( '.' ) > 0 ) // das > ist pure Absicht, damit versteckte Dateien nicht als Dateiendung interpretiert werden!
//		{
//		  suffix = dateiName.substring(dateiName.lastIndexOf('.'));
////		  System.out.println("DateiEndung: " + suffix);
//		}
//		else
//		{
//		  suffix = "";
//		}
//
//		return suffix;
//	}
	
	private ImageView getImage(TreeItem<PathItem> treeItem) {
    	ImageView imageView = new ImageView();

    	File file = treeItem.getValue().getPath().toFile();
    	if (file.isDirectory()) {
//    	if (IsPathDirectory(file.getAbsolutePath())) {
    		imageView.setImage(getDirectoryItem(treeItem));
		} else {
//			imageView.setImage(setDocumentIcon());
			if (file.exists()) {
				imageView.setImage(ISystemIcon.getSystemImage(file));
			} else {								
				String itemSuffix = ISuffix.getSuffix(file.getName());
				
//				System.out.println("suffix: " + file + " -> " + itemSuffix);
				
				if (!itemSuffix.equals("")) {
					for (Entry<String, Image> item : CTree.getSuffixIcon().entrySet()) {	
//						System.out.println("      suffix: " + item.getKey() + " == " + itemSuffix);
						if (item.getKey().equalsIgnoreCase(itemSuffix)) {
							
							imageView.setImage(item.getValue());
//							System.out.println("      suffix found: " + item.getKey() + " == " + itemSuffix + " -> " + imageView);
							return imageView;
						}					
					}
					// Default File-Icon
					imageView.setImage(getDefaultDocumentIcon());	
				} else {
					imageView.setImage(getDirectoryItem(treeItem));
				}
			}			
		}
    	    	
		return imageView;
	}
	
	private Image getDirectoryItem(TreeItem<PathItem> treeItem) {
		if (treeItem.isExpanded() && treeItem.getChildren().size() != 0) {
			return getOpenIcon();
		} else {
			return getCloseIcon();
		}
	}
	
	private boolean IsPathDirectory(String path) {
	    File file = new File(path);

	    // check if the file/directory is already there
	    if (!file.exists()) {
	        // see if the file portion it doesn't have an extension
	        return file.getName().lastIndexOf('.') == -1;
	    } else {
	        // see if the path that's already in place is a file or directory
	        return true;
	    }
	}
	
//	private Image getSystemIcon(File file)  {
//
//        ShellFolder sf = null;
//		try {
//			sf = ShellFolder.getShellFolder(file);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//
//		// Get large icon
//		ImageIcon ico = new ImageIcon(sf.getIcon(true), sf.getFolderType());
//		java.awt.Image awtImage = ico.getImage();
//		
//		BufferedImage bImg ;
//		if (awtImage instanceof BufferedImage) {
//		    bImg = (BufferedImage) awtImage ;
//		} else {
//		    bImg = new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
//		    Graphics2D graphics = bImg.createGraphics();
//		    graphics.drawImage(awtImage, 0, 0, null);
//		    graphics.dispose();
//		}
//		
//		Image fxImage = SwingFXUtils.toFXImage(bImg, null);
//   			
////		System.out.println("Icon-Breite: " + fxImage.getWidth() + "  -  Icon-Höhe: " + fxImage.getHeight());
//		
//		return fxImage;
//
//	}
	
	
	private Image getOpenIcon() {
		return new Image(StartWacherDemo.class.getResourceAsStream("view/images/folderOpen.png"));
	}
	
	private Image getCloseIcon() {
		return new Image(StartWacherDemo.class.getResourceAsStream("view/images/folderClose.png"));
	}
	
	private Image getDefaultDocumentIcon() {
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
