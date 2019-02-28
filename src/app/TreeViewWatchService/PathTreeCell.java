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
import app.interfaces.ILockDir;
import app.interfaces.IOpenFile;
import app.interfaces.ISuffix;
import app.interfaces.ISystemIcon;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class PathTreeCell extends TreeCell<PathItem> implements ISuffix, ISystemIcon, IOpenFile, ILockDir{
	private Stage primaryStage;
	private CTree cTree;
	private CellContextMenu cellContextMenu;
	
    private TextField textField;
    private Path editingPath;
    private StringProperty messageProp;
//    private ContextMenu dirMenu = new ContextMenu();
    private ContextMenu fileMenu;
    private ExecutorService service = Executors.newFixedThreadPool(1);
    private ObservableList<String> listAllLockedFiles = FXCollections.observableArrayList();

    private int index;
    private TreeItem<PathItem> levelOneItem;
    
    
    public PathTreeCell(CTree cTree, Stage primaryStage) {  
    	this.cTree = cTree;
    	this.fileMenu = new CellContextMenu(this, primaryStage, cTree, listAllLockedFiles);
        DragNDropInternal DragNDropInternal = new DragNDropInternal(primaryStage, service, this);                   
    }

    public void selectCell() {
    	System.err.println("CellIndex: " + getIndex());
//    	System.err.println("CellName: " + getTreeItem().getValue().getPath());
    	getTreeView().getSelectionModel().clearSelection();
    	getTreeView().getSelectionModel().select(getIndex());
    }
    
	@Override
    protected void updateItem(PathItem item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
            setContextMenu(null);           
        } else {

        		setLevelOneItem(getTreeItem());

        		item.setRow(getIndex());
        		item.setLevel(getTreeView().getTreeItemLevel(getTreeItem()));
        		
                setText(getString() + " (" + getIndex() + " - " + item.getLevel() + " -> " + item.isLocked() + ")");
                
				setContextMenu(fileMenu);       
				setListenerLockedContextMenu(getTreeItem());              
                setStartPropertiesContextMenu(item);

                setGraphic(getImage(this.getTreeItem()));
                
                mouseOver(this);                              
				mouseClick(this);
//				if (item.getPath().toString().contains("A0")) {
//					System.out.println(item.toString2());
//				}
				
				
        }
    }

	private void setListenerLockedContextMenu(TreeItem<PathItem> treeItem) {
		PathItem item = treeItem.getValue();
		boolean isLocked = item.isLocked();
		

        if (getTreeView().getTreeItemLevel(getTreeItem()) == 1) {
        	item.getIsLockedProp().addListener((var, oldVar, newVar) -> {
        		item.setLocked(newVar);

    			Platform.runLater(() -> {
    				// Image und ContextMenu wechsel
    				setGraphic(getImage(this.getTreeItem()));
    			});
   			
    		});
		} 
        
        if (getTreeView().getTreeItemLevel(getTreeItem()) > 1) {
			levelOneItem.getValue().getIsLockedProp().addListener((var, oldVar, newVar) -> {   		
//				System.out.println(item + " -> " + newVar);
				item.setLocked(newVar);			

    			Platform.runLater(() -> {   	
    				// Image und ContextMenu wechsel
    				setGraphic(getImage(this.getTreeItem()));
    			});

    		});
		}
        getTreeView().refresh();
	}

	private void setStartPropertiesContextMenu(PathItem item) {
        if (getTreeView().getTreeItemLevel(getTreeItem()) == 0) {               	
        	cellContextMenu.setRootMenuItems();
//        	setGraphic(getImage(this.getTreeItem()));
        } else {
        	if (item.isDirectoryItem()) {
				cellContextMenu.setMenuItemsDir();
			} else {
				cellContextMenu.setMenuItemsFile();
			}
        	
		}
        
        if (getTreeView().getTreeItemLevel(getTreeItem()) == 1) {
			boolean b = isDirLocked(getTreeView().getRoot(), getTreeItem().getValue().getPath().toFile());
			if (item.isDirectoryItem()) {
				cellContextMenu.setLockedDir(b);
			} else {
				cellContextMenu.setLockedFile(b);
			}
			
//			setGraphic(getImage(this.getTreeItem()));
		} 
        

        if (getTreeView().getTreeItemLevel(getTreeItem()) > 1) {
        	item.setLocked(levelOneItem.getValue().isLocked());
//        	setGraphic(getImage(this.getTreeItem()));
        }
	}

	public void setLevelOneItem(TreeItem<PathItem> treeItem) {

		// Root-Ordner oder LevelOne-Ordner
		if (getTreeView().getTreeItemLevel(getTreeItem()) <= 1) {
			getTreeItem().getValue().setLevelOneItem(treeItem);
		}
		
		if (getTreeView().getTreeItemLevel(getTreeItem()) > 1) {
			getLevelOneItem(getTreeItem());
			getTreeItem().getValue().setLevelOneItem(levelOneItem);
		}
	}
	
	private void getLevelOneItem(TreeItem<PathItem> treeItem) {
		
		TreeItem<PathItem> parentItem = treeItem.getParent();
        if (getTreeView().getTreeItemLevel(parentItem) == 1) {
//        	System.out.println("getLevelOneItem(): " + parentItem);
        	levelOneItem = parentItem;   
        	treeItem.getValue().setLevelOneItem(levelOneItem);
			return;
		} else {
			getLevelOneItem(parentItem);
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
	
	
	public ImageView getImage(TreeItem<PathItem> treeItem) {
    	ImageView imageView = new ImageView();

    	if (treeItem != null) {

	    	File file = treeItem
	    			.getValue()
	    			.getPath()
	    			.toFile();
	    	if (file.isDirectory()) {	    		
	    		imageView.setImage(getDirectoryItem(treeItem));	    		
			} else {
				if (file.exists()) {
					setContextMenuFileProperties(treeItem);
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
						setContextMenuFileProperties(treeItem);
						imageView.setImage(getDefaultDocumentIcon());	
					} else {
						imageView.setImage(getDirectoryItem(treeItem));
					}
				}			
			}
		}    	
    	
		return imageView;
	}
	
	private Image getDirectoryItem(TreeItem<PathItem> treeItem) {
		boolean isLocked = treeItem.getValue().isLocked();
		if (treeItem.isExpanded() && treeItem.getChildren().size() != 0) {
			if (isLocked) {
				cellContextMenu.setLockedDir(true);
				return getOpenKeyIcon();
			} else {
				cellContextMenu.setLockedDir(false);
				return getOpenIcon();
			}			
		} else {
			if (isLocked) {
				cellContextMenu.setLockedDir(true);
				return getCloseKeyIcon();
			} else {
				cellContextMenu.setLockedDir(false);
				return getCloseIcon();
			}							
		}
	}

	private void setContextMenuFileProperties(TreeItem<PathItem> treeItem) {
		boolean isLocked = treeItem.getValue().isLocked();
		System.out.println(treeItem + " -> " + isLocked);
		if (treeItem.isExpanded()) {
			if (isLocked) {
				cellContextMenu.setLockedFile(true);
			} else {
				cellContextMenu.setLockedFile(false);
			}			
		} else {
			if (isLocked) {
				cellContextMenu.setLockedFile(true);
			} else {
				cellContextMenu.setLockedFile(false);
			}							
		}

	}
	
	private Image getOpenIcon() {
		return new Image(StartWacherDemo.class.getResourceAsStream("view/images/folderOpen.png"));
	}
	
	private Image getCloseIcon() {
		return new Image(StartWacherDemo.class.getResourceAsStream("view/images/folderClose.png"));
	}
	
	private Image getOpenKeyIcon() {
		return new Image(StartWacherDemo.class.getResourceAsStream("view/images/folderOpen_key.png"));
	}
	
	private Image getCloseKeyIcon() {
		return new Image(StartWacherDemo.class.getResourceAsStream("view/images/folderClose_key.png"));
	}
	
	private Image getDefaultDocumentIcon() {
		return new Image(StartWacherDemo.class.getResourceAsStream("view/images/document.png"));
	}
	
	
//    @Override
//    public void startEdit() {
//        super.startEdit();
//        if (textField == null){
//            createTextField();
//        }
//        setText(null);
//        setGraphic(textField);
//        textField.selectAll();
//        if (getItem() == null) {
//            editingPath = null;
//        } else {
//            editingPath =getItem().getPath();
//        }
//    }

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

//    private void createTextField() {
//        textField = new TextField(getString());
//        textField.setOnKeyReleased((KeyEvent t) -> {
//            if (t.getCode() == KeyCode.ENTER){
//                Path path = Paths.get(getItem().getPath().getParent().toAbsolutePath().toString(), textField.getText());
//                commitEdit(new PathItem(path));
//            } else if (t.getCode() == KeyCode.ESCAPE) {
//                cancelEdit();
//            }
//        });
//    }

    // Getter
	public CTree getcTree() {return cTree;}

	public void set(CellContextMenu cellContextMenu) {
		this.cellContextMenu = cellContextMenu;
	}
    

    
}
