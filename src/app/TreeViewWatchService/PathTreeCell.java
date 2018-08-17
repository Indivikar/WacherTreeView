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
						 
						FileUtils.cleanDirectory(file);
						FileUtils.deleteDirectory(file);						
			    		count++;
			    		Thread.sleep(1000);
			    	}
			  } else {	   				  
					FileUtils.forceDelete(file);	
					Thread.sleep(1000);
			  }
		
		} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		

		if (!file.exists()) {
			TreeItem<PathItem> c = (TreeItem<PathItem>)this.getTreeView().getSelectionModel().getSelectedItem();
			boolean remove = c.getParent().getChildren().remove(c);
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
			if (treeItem.isExpanded() && treeItem.getChildren().size() != 0) {
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
