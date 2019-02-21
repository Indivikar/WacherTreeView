package app.TreeViewWatchService.contextMenu;

import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import app.interfaces.ILockDir;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;

public class CellContextMenu extends ContextMenu {
	
	private Stage primaryStage;
	private PathTreeCell pathTreeCell;
	private ObservableList<String> listAllLockedFiles;
	
	MenuItemOpen menuItemOpen;
	SeparatorMenuItem separatorMenuItem_1;
	MenuItemNewFile menuItemNewFile;
	MenuItemNewDirectory menuItemNewDirectory;
	MenuItemRename menuItemRename;
	MenuItemDeleteItem menuItemDeleteItem;
	SeparatorMenuItem separatorMenuItem_2;
	MenuItemRefreshTree menuItemRefreshTree;
	
	public CellContextMenu(PathTreeCell pathTreeCell, Stage primaryStage, CTree cTree, ObservableList<String> listAllLockedFiles) {
		
		pathTreeCell.set(this);
		this.primaryStage = primaryStage;
		this.pathTreeCell = pathTreeCell;
		this.listAllLockedFiles = listAllLockedFiles;
		
		this.menuItemOpen = new MenuItemOpen(pathTreeCell);
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
	
}
