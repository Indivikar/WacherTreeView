package app.TreeViewWatchService;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;

import app.StartWacherDemo;
import app.controller.CTree;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
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
import sun.awt.shell.ShellFolder;

public class PathTreeCell extends TreeCell<PathItem>{
    private TextField textField;
    private Path editingPath;
    private StringProperty messageProp;
    private ContextMenu dirMenu = new ContextMenu();
    private ContextMenu fileMenu = new ContextMenu();
    

    public PathTreeCell() {
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
    	  Path filePath = this.getItem().getPath();
    	  deleteFileOrDirectory(filePath.toFile());
      });
      
      fileMenu.getItems().addAll(addFile, addFolder, deleteMenu);		
	}

    private void deleteFileOrDirectory(File file) {
    	System.out.println("Del 1 fertig: " + this.getItem().getPath());
    	try {
			  if (file.isDirectory()) {

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
    
    
	@Override
    protected void updateItem(PathItem item, boolean empty) {
        super.updateItem(item, empty);
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
                setText(getString());
                setGraphic(null);
                setGraphic(getImage(this.getTreeItem()));
                setContextMenu(fileMenu);
                
                
                
//              if (getString().equalsIgnoreCase("test")) {
//					this.setDisable(true);
//				}
                
//            }
        }
    }

	private ImageView getImage(TreeItem<PathItem> treeItem) {
    	ImageView imageView = new ImageView();

    	File file = treeItem.getValue().getPath().toFile();
    	if (file.isDirectory()) {
			if (treeItem.isExpanded() && treeItem.getChildren().size() != 0) {
				imageView.setImage(setOpenIcon());
			} else {
				imageView.setImage(setCloseIcon());
			}
		} else {
//			imageView.setImage(setDocumentIcon());
			imageView.setImage(getSystemIcon(file));
		}
    	    	
		return imageView;
	}
	
	private Image getSystemIcon(File file)  {

        ShellFolder sf = null;
		try {
			sf = ShellFolder.getShellFolder(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Get large icon
		ImageIcon ico = new ImageIcon(sf.getIcon(true), sf.getFolderType());
		java.awt.Image awtImage = ico.getImage();
		
		BufferedImage bImg ;
		if (awtImage instanceof BufferedImage) {
		    bImg = (BufferedImage) awtImage ;
		} else {
		    bImg = new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		    Graphics2D graphics = bImg.createGraphics();
		    graphics.drawImage(awtImage, 0, 0, null);
		    graphics.dispose();
		}
		
		Image fxImage = SwingFXUtils.toFXImage(bImg, null);
   			
//		System.out.println("Icon-Breite: " + fxImage.getWidth() + "  -  Icon-Höhe: " + fxImage.getHeight());
		
		return fxImage;

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
