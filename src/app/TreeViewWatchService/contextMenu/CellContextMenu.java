package app.TreeViewWatchService.contextMenu;

import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;

public class CellContextMenu extends ContextMenu{

	private PathTreeCell pathTreeCell;
	private ObservableList<String> listAllLockedFiles;
	
	public CellContextMenu(PathTreeCell pathTreeCell, CTree cTree, ObservableList<String> listAllLockedFiles) {
		this.pathTreeCell = pathTreeCell;
		this.listAllLockedFiles = listAllLockedFiles;
		
		getItems().addAll(	new MenuItemOpen(pathTreeCell),
							new SeparatorMenuItem(),
							new MenuItemNewFile(), 
							new MenuItemNewDirectory(pathTreeCell), 
							new MenuItemRename(pathTreeCell), 
							new MenuItemDeleteItem(pathTreeCell, listAllLockedFiles),
							new SeparatorMenuItem(),
							new MenuItemRefreshTree(pathTreeCell, cTree));	
	}

}
