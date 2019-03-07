package app.TreeViewWatchService.contextMenu;

import app.TreeViewWatchService.PathItem;
import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import app.interfaces.ILockDir;
import javafx.beans.binding.Bindings;
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
	
	private SimpleBooleanProperty propDisBoolOpen;
	private SimpleBooleanProperty propDisBoolNewFile;
	private SimpleBooleanProperty propDisBoolNewDirectory;
	private SimpleBooleanProperty propDisBoolRename;
	private SimpleBooleanProperty propDisBoolDeleteItem;
	private SimpleBooleanProperty propDisBoolRefreshTree;
	
	public CellContextMenu(PathTreeCell pathTreeCell, Stage primaryStage, CTree cTree, ObservableList<String> listAllLockedFiles) {
		this.cTree = cTree;
		this.propDisBoolOpen = cTree.getPropDisBoolOpen();
		this.propDisBoolNewFile = cTree.getPropDisBoolNewFile();
		this.propDisBoolNewDirectory = cTree.getPropDisBoolNewDirectory();
		this.propDisBoolRename = cTree.getPropDisBoolRename();
		this.propDisBoolDeleteItem = cTree.getPropDisBoolDeleteItem();
		this.propDisBoolRefreshTree = cTree.getPropDisBoolRefreshTree();
		
		showingProperty().addListener((ov, oldVal, newVal) -> {
			// Alle Selected Items Speichern
			cTree.saveSelectedItems();
			
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
		menuItemOpen.disableProperty().bind(propDisBoolOpen);
		menuItemNewFile.disableProperty().bind(propDisBoolNewFile);
		menuItemNewDirectory.disableProperty().bind(propDisBoolNewDirectory);
		menuItemRename.disableProperty().bind(propDisBoolRename);
		menuItemDeleteItem.disableProperty().bind(propDisBoolDeleteItem);
		menuItemRefreshTree.disableProperty().bind(propDisBoolRefreshTree);
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
