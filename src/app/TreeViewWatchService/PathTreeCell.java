package app.TreeViewWatchService;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;

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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class PathTreeCell extends TreeCell<PathItem>{
    private TextField textField;
    private Path editingPath;
    private StringProperty messageProp;
    private ContextMenu dirMenu = new ContextMenu();
    private ContextMenu fileMenu = new ContextMenu();
    

    public PathTreeCell(final Stage owner, final StringProperty messageProp) {
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
    	  
    	  	try {
				Files.walk(filePath)
				    .sorted(Comparator.reverseOrder())
				    .map(Path::toFile)
				    .peek(System.out::println)
				    .forEach(File::delete);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	  
    		
    		try {
				Files.walkFileTree(filePath, new SimpleFileVisitor<Path>() {
    
					@Override
					public FileVisitResult visitFile(Path file,
							BasicFileAttributes attrs) throws IOException {
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}
    
					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc)
							throws IOException {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

      });
      fileMenu.getItems().addAll(addFile, addFolder, deleteMenu);
		
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
            	FileTreeViewSample.children.add(this);
                setText(getString());
                setGraphic(null);
                setContextMenu(fileMenu);

            }
        }
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
