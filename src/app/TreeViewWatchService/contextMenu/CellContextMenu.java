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
	
	public CellContextMenu(PathTreeCell pathTreeCell, Stage primaryStage, CTree cTree, 
			ObservableList<String> listAllLockedFiles) {
		

		
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
		
		getItems().addAll(	menuItemOpen,
							separatorMenuItem_1,
							menuItemNewFile, 
							menuItemNewDirectory, 
							menuItemRename, 
							menuItemDeleteItem,
							separatorMenuItem_2,
							menuItemRefreshTree);	
	}

	public void bindMenuItemsReload(Service<?> service) {

                Bindings
                .when(service.runningProperty())
                    .then(test(true))
                    .otherwise(test(false));
				
	}
	
	private boolean test(boolean wert) {
		menuItemNewFile.setDisable(wert);
		return false;
	}
	
	public void setRootMenuItems() {
		menuItemOpen.setDisable(false);
		menuItemNewFile.setDisable(true);
		menuItemNewDirectory.setDisable(false);
		menuItemRename.setDisable(true);
		menuItemDeleteItem.setDisable(true);
	}
	
	public void setMenuItemsDir() {
		menuItemOpen.setDisable(false);
		menuItemNewFile.setDisable(false);
		menuItemNewDirectory.setDisable(false);
		menuItemRename.setDisable(false);
		menuItemDeleteItem.setDisable(false);		
	}
	
	public void setMenuItemsFile() {
		menuItemOpen.setDisable(false);
		menuItemNewFile.setDisable(true);
		menuItemNewDirectory.setDisable(true);
		menuItemRename.setDisable(false);
		menuItemDeleteItem.setDisable(false);		
	}
	
	public void setLockedDir(boolean isLocked) {
		menuItemOpen.setDisable(isLocked);
		menuItemNewFile.setDisable(isLocked);
		menuItemNewDirectory.setDisable(isLocked);
		menuItemRename.setDisable(isLocked);
		menuItemDeleteItem.setDisable(isLocked);
	}
	
	public void setLockedFile(boolean isLocked) {
		menuItemOpen.setDisable(isLocked);
		menuItemNewFile.setDisable(true);
		menuItemNewDirectory.setDisable(true);
		menuItemRename.setDisable(isLocked);
		menuItemDeleteItem.setDisable(isLocked);
	}
	
//	public void serviceReloadBinding(boolean wert) {		
//		System.out.println("serviceReloadBinding(): " + wert);
//		menuItemNewFile.setDisable(wert);
//		System.out.println("is menuItemNewFile disabled: " + menuItemNewFile.isDisable());
//	}
	
}
