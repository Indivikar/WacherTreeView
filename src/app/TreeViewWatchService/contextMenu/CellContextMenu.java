package app.TreeViewWatchService.contextMenu;

import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import app.interfaces.ILockDir;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

public class CellContextMenu extends ContextMenu {
	
	private Stage primaryStage;
	private CTree cTree;
	private PathTreeCell pathTreeCell;
	private ObservableList<String> listAllLockedFiles;
	
	private MenuItemOpen menuItemOpen;
	private SeparatorMenuItem separatorMenuItem_1;
	private MenuItemNewFile menuItemNewFile;
	private MenuItemNewDirectory menuItemNewDirectory;
	private MenuItemRename menuItemRename;
	private MenuItemDeleteItem menuItemDeleteItem;
	private SeparatorMenuItem separatorMenuItem_2;
	private MenuItemRefreshTree menuItemRefreshTree;
	
	private SimpleBooleanProperty serviceReload = new SimpleBooleanProperty(false);
	
	private SimpleBooleanProperty propDisBoolOpen = new SimpleBooleanProperty(false);
	private SimpleBooleanProperty propDisBoolNewFile = new SimpleBooleanProperty(false);
	private SimpleBooleanProperty propDisBoolNewDirectory = new SimpleBooleanProperty(false);
	private SimpleBooleanProperty propDisBoolRename = new SimpleBooleanProperty(false);
	private SimpleBooleanProperty propDisBoolDeleteItem = new SimpleBooleanProperty(false);
	private SimpleBooleanProperty propDisBoolRefreshTree = new SimpleBooleanProperty(false);
	
	public CellContextMenu(PathTreeCell pathTreeCell, Stage primaryStage, CTree cTree, ObservableList<String> listAllLockedFiles) {
//		System.out.println("init CellContextMenu");
		this.cTree = cTree;
//		this.propDisBoolOpen = cTree.getPropDisBoolOpen();
//		this.propDisBoolNewFile = cTree.getPropDisBoolNewFile();
//		this.propDisBoolNewDirectory = cTree.getPropDisBoolNewDirectory();
//		this.propDisBoolRename = cTree.getPropDisBoolRename();
//		this.propDisBoolDeleteItem = cTree.getPropDisBoolDeleteItem();
//		this.propDisBoolRefreshTree = cTree.getPropDisBoolRefreshTree();
		
		
		

				
		showingProperty().addListener((ov, oldVal, newVal) -> {
			// Alle Selected Items Speichern
			cTree.saveSelectedItems();
			
			
			
			if (cTree.getPropBoolRefresh().get()) {
				setMenuItemsReload(true);
			}
			
			// wurde eingebaut, damit die celle selected wird, wo ein rechts-Klick auf den Pfeil vom Node gemacht wird
			TreeItem<PathItem> selItem = cTree.getTree().getSelectionModel().getSelectedItem();
			if (selItem != null) {
				String selItemString = selItem.getValue().getPath().toString();
				String cellItem = pathTreeCell.getTreeItem().getValue().getPath().toString();
				
				int row = pathTreeCell.getTreeItem().getValue().getRow();
				if (!selItemString.equals(cellItem)) {
					cTree.getTree().getSelectionModel().clearSelection();
					cTree.getTree().getSelectionModel().select(row);
				}
			}
		});
		
		pathTreeCell.set(this);
		this.primaryStage = primaryStage;
		this.pathTreeCell = pathTreeCell;
		this.listAllLockedFiles = listAllLockedFiles;
		
		this.menuItemOpen = new MenuItemOpen(primaryStage, pathTreeCell);
		this.separatorMenuItem_1 = new SeparatorMenuItem();
		this.menuItemNewFile = new MenuItemNewFile(pathTreeCell);
		this.menuItemNewDirectory = new MenuItemNewDirectory(primaryStage, cTree, pathTreeCell);
		this.menuItemRename = new MenuItemRename(cTree, primaryStage, pathTreeCell);
		this.menuItemDeleteItem = new MenuItemDeleteItem(primaryStage, cTree, pathTreeCell, listAllLockedFiles);
		this.separatorMenuItem_2 = new SeparatorMenuItem();
		this.menuItemRefreshTree = new MenuItemRefreshTree(pathTreeCell, cTree);
		
		bindings();
		
		getItems().addAll(	menuItemOpen,
							separatorMenuItem_1,
							menuItemNewFile, 
							menuItemNewDirectory, 
							menuItemRename, 
							menuItemDeleteItem,
							separatorMenuItem_2,
							menuItemRefreshTree);	
		
	}

	private void bindings() {
		
		// wenn der Cursor auf WAIT gesetzt wird, dann context-Items deaktivieren
//		BooleanBinding booleanBindingCursor = cTree.getTree().getScene().getRoot().cursorProperty().isEqualTo(Cursor.WAIT); // Scene binding
		BooleanBinding booleanBindingCursor = cTree.getTree().cursorProperty().isEqualTo(Cursor.WAIT); // TreeView binding
		
		menuItemOpen.disableProperty().bind(propDisBoolOpen.or(booleanBindingCursor));
		menuItemNewFile.disableProperty().bind(propDisBoolNewFile.or(booleanBindingCursor));
		menuItemNewDirectory.disableProperty().bind(propDisBoolNewDirectory.or(booleanBindingCursor));
		menuItemRename.disableProperty().bind(propDisBoolRename.or(booleanBindingCursor));
		menuItemDeleteItem.disableProperty().bind(propDisBoolDeleteItem.or(booleanBindingCursor));
		menuItemRefreshTree.disableProperty().bind(propDisBoolRefreshTree.or(booleanBindingCursor));
		
		
//		menuItemOpen.disableProperty().bind(propDisBoolOpen);
//		menuItemNewFile.disableProperty().bind(propDisBoolNewFile);
//		menuItemNewDirectory.disableProperty().bind(propDisBoolNewDirectory);
//		menuItemRename.disableProperty().bind(propDisBoolRename);
//		menuItemDeleteItem.disableProperty().bind(propDisBoolDeleteItem);
//		menuItemRefreshTree.disableProperty().bind(propDisBoolRefreshTree);
	}
	
	

	
	public void setRootMenuItems() {
		propDisBoolOpen.setValue(false);
		propDisBoolNewFile.setValue(false);
		propDisBoolNewDirectory.setValue(false);
		propDisBoolRename.setValue(true);
		propDisBoolDeleteItem.setValue(true);
		propDisBoolRefreshTree.setValue(false);
		
//		menuItemOpen.setDisable(false);
//		menuItemNewFile.setDisable(true);
//		menuItemNewDirectory.setDisable(false);
//		menuItemRename.setDisable(true);
//		menuItemDeleteItem.setDisable(true);
	}
	
	public void setMenuItemsReload(boolean wert) {
		System.out.println("     setMenuItemsReload -> " + wert);		
		propDisBoolOpen.setValue(wert);
		propDisBoolNewFile.setValue(wert);
		propDisBoolNewDirectory.setValue(wert);
		propDisBoolRename.setValue(wert);
		propDisBoolDeleteItem.setValue(wert);
		propDisBoolRefreshTree.setValue(wert);
	}
	
//	public boolean setMenuItemsReload(boolean wert) {
//		// Diese Methode muss ein boolean zurück geben, weil die Methode (in dem Interface "CreateTree")
//        // 		Bindings.when(service.runningProperty())
//        //    		.then(setMenuItemsReload(true))
//        //    		.otherwise(setMenuItemsReload(false));
//		// ein boolean wert erwartet, was für ein wert es ist, spielt keine Rolle
//		
//		cTree.getPropDisBoolOpen().setValue(wert);
//		cTree.getPropDisBoolNewFile().setValue(wert);
//		cTree.getPropDisBoolNewDirectory().setValue(wert);
//		cTree.getPropDisBoolRename().setValue(wert);
//		cTree.getPropDisBoolDeleteItem().setValue(wert);
//		cTree.getPropDisBoolRefreshTree().setValue(wert);
//		return false;
//	}
	
	public void setMenuItemsDir() {
		propDisBoolOpen.setValue(false);
		propDisBoolNewFile.setValue(false);
		propDisBoolNewDirectory.setValue(false);
		propDisBoolRename.setValue(false);
		propDisBoolDeleteItem.setValue(false);
		propDisBoolRefreshTree.setValue(false);
		
//		menuItemOpen.setDisable(false);
//		menuItemNewFile.setDisable(false);
//		menuItemNewDirectory.setDisable(false);
//		menuItemRename.setDisable(false);
//		menuItemDeleteItem.setDisable(false);		
	}
	
	public void setMenuItemsFile() {
		propDisBoolOpen.setValue(false);
		propDisBoolNewFile.setValue(true);
		propDisBoolNewDirectory.setValue(true);
		propDisBoolRename.setValue(false);
		propDisBoolDeleteItem.setValue(false);
		propDisBoolRefreshTree.setValue(false);
		
//		menuItemOpen.setDisable(false);
//		menuItemNewFile.setDisable(true);
//		menuItemNewDirectory.setDisable(true);
//		menuItemRename.setDisable(false);
//		menuItemDeleteItem.setDisable(false);		
	}
	
	public void setLockedDir(boolean isLocked) {	
		System.out.println(pathTreeCell.getTreeItem().getValue().getPath() + " -> " + isLocked);
		propDisBoolOpen.setValue(isLocked);
		propDisBoolNewFile.setValue(isLocked);
		propDisBoolNewDirectory.setValue(isLocked);
		propDisBoolRename.setValue(isLocked);
		propDisBoolDeleteItem.setValue(isLocked);
		propDisBoolRefreshTree.setValue(false);
		
//		menuItemOpen.setDisable(isLocked);
//		menuItemNewFile.setDisable(isLocked);
//		menuItemNewDirectory.setDisable(isLocked);
//		menuItemRename.setDisable(isLocked);
//		menuItemDeleteItem.setDisable(isLocked);
	}
	
	public void setLockedFile(boolean isLocked) {
		propDisBoolOpen.setValue(isLocked);
		propDisBoolNewFile.setValue(true);
		propDisBoolNewDirectory.setValue(true);
		propDisBoolRename.setValue(isLocked);
		propDisBoolDeleteItem.setValue(isLocked);
		propDisBoolRefreshTree.setValue(false);
		
//		menuItemOpen.setDisable(isLocked);
//		menuItemNewFile.setDisable(true);
//		menuItemNewDirectory.setDisable(true);
//		menuItemRename.setDisable(isLocked);
//		menuItemDeleteItem.setDisable(isLocked);
	}
	
//	public void serviceReloadBinding(boolean wert) {		
//		System.out.println("serviceReloadBinding(): " + wert);
//		menuItemNewFile.setDisable(wert);
//		System.out.println("is menuItemNewFile disabled: " + menuItemNewFile.isDisable());
//	}
	
}
