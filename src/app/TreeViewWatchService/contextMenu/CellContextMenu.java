package app.TreeViewWatchService.contextMenu;

import app.TreeViewWatchService.PathTreeCell;
import app.controller.CTree;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;

public class CellContextMenu extends ContextMenu{

	private Stage primaryStage;
	private PathTreeCell pathTreeCell;
	private ObservableList<String> listAllLockedFiles;
	
	public CellContextMenu(PathTreeCell pathTreeCell, Stage primaryStage, CTree cTree, ObservableList<String> listAllLockedFiles) {
		this.primaryStage = primaryStage;
		this.pathTreeCell = pathTreeCell;
		this.listAllLockedFiles = listAllLockedFiles;
		
		getItems().addAll(	new MenuItemOpen(pathTreeCell),
							new SeparatorMenuItem(),
							new MenuItemNewFile(), 
							new MenuItemNewDirectory(primaryStage, cTree, pathTreeCell), 
							new MenuItemRename(primaryStage, pathTreeCell), 
							new MenuItemDeleteItem(primaryStage, cTree, pathTreeCell, listAllLockedFiles),
							new SeparatorMenuItem(),
							new MenuItemRefreshTree(pathTreeCell, cTree));	
	}

}
