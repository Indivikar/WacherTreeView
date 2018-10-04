package app.TreeViewWatchService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.StartWacherDemo;
import app.TreeViewWatchService.contextMenu.CellContextMenu;
import app.controller.CTree;
import app.interfaces.IOpenFile;
import app.interfaces.ISuffix;
import app.interfaces.ISystemIcon;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class PathTreeCell extends TreeCell<PathItem> implements ISuffix, ISystemIcon, IOpenFile{
	private Stage primaryStage;
    private TextField textField;
    private Path editingPath;
    private StringProperty messageProp;
    private ContextMenu dirMenu = new ContextMenu();
    private ContextMenu fileMenu;
    private ExecutorService service = Executors.newFixedThreadPool(1);
    private ObservableList<String> listAllLockedFiles = FXCollections.observableArrayList();

    
    public PathTreeCell(CTree cTree, Stage primaryStage) {    	
    	this.fileMenu = new CellContextMenu(this, cTree, listAllLockedFiles);
        DragNDropInternal DragNDropInternal = new DragNDropInternal(primaryStage, service, this);
    }

	@Override
    protected void updateItem(PathItem item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
            setContextMenu(null);
        } else {

        		String name = getString();
        		
                setText(name);
                setGraphic(getImage(this.getTreeItem()));
                setContextMenu(fileMenu);

                mouseOver(this);                              
				mouseClick(this);
        }
    }

	private void mouseClick(PathTreeCell pathTreeCell) {
		pathTreeCell.setOnMouseClicked(event -> {
			File file = pathTreeCell.getTreeItem().getValue().getPath().toFile();
        	if (event.getClickCount() == 2 && !pathTreeCell.isEmpty() && !file.isDirectory()) {				
                open(file);
			}               
        });
	}
	
	private void mouseOver(PathTreeCell pathTreeCell) {	
		pathTreeCell.setOnDragEntered(event -> {
			pathTreeCell.setStyle(
					"-fx-background: -fx-selection-bar-non-focused;\r\n" + 
					"-fx-table-cell-border-color: derive(-fx-selection-bar-non-focused, 20%);");
    	});
		pathTreeCell.setOnDragExited(event -> {	
			pathTreeCell.setStyle("");
    	});
	}
	
	
	private ImageView getImage(TreeItem<PathItem> treeItem) {
    	ImageView imageView = new ImageView();

    	File file = treeItem.getValue().getPath().toFile();
    	if (file.isDirectory()) {
    		imageView.setImage(getDirectoryItem(treeItem));
		} else {
			if (file.exists()) {
				imageView.setImage(ISystemIcon.getSystemImage(file));
			} else {								
				String itemSuffix = ISuffix.getSuffix(file.getName());
				if (!itemSuffix.equals("")) {
					for (Entry<String, Image> item : CTree.getSuffixIcon().entrySet()) {	
						if (item.getKey().equalsIgnoreCase(itemSuffix)) {							
							imageView.setImage(item.getValue());
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
