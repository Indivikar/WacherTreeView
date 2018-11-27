package app.TreeViewWatchService;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import app.controller.CTree;
import app.db.PathList;
import app.functions.LoadTime;
import app.interfaces.ICursor;
import app.interfaces.ISaveExpandedItems;
import app.interfaces.ISaveSelectedItems;
import app.interfaces.ITreeItemMethods;
import app.loadTime.LoadTime.LoadTimeOperation;
import app.sort.WinExplorerComparatorPath;
import app.sort.WindowsExplorerComparator;
import app.threads.CreateTreeTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Pair;

public class CreateTree implements ITreeItemMethods, ISaveExpandedItems, ISaveSelectedItems, ICursor {
	
	private CTree cTree;
	
	private PathList pathList;
	private String pathFileDB;
	
	public static boolean wantUpdateTree = true;
	
	private final Comparator<Path> NATURAL_SORT = new WinExplorerComparatorPath();
	private ObservableList<Path> paths = FXCollections.observableArrayList();
	private HashMap<Path, TreeItem<PathItem>> expandedItemList;
	private ObservableList<TreeItem<PathItem>> selectedItems = FXCollections.observableArrayList();
	
	
	
	public CreateTree(String pathFileDB, CTree cTree) {
		System.out.println("Load -> CreateTree");
		this.cTree = cTree;
		this.pathFileDB = pathFileDB;
		this.pathList = new PathList(pathFileDB);
	}

	
	
	public void updatePathListFormDB(TreeItem<PathItem> mainItem, boolean clearTree, boolean saveSelections) {		
		
		if (!wantUpdateTree) {
			wantUpdateTree = true;
			return;
		}
		
		CTree.getLoadDBService().setOnSucceeded(e -> {
			System.out.println("updatePathListFormDB");
			this.paths = CTree.getLoadDBService().getValue();

			startCreateTree(mainItem, clearTree, saveSelections);
		});
		
		if (!CTree.getLoadDBService().isRunning()) {
			System.out.println("---- updatePathListFormDB ----");
			CTree.getLoadDBService().start();
		}
		
//		long start = new Date().getTime();
//		this.paths = pathList.loadDB();
//		long runningTime = new Date().getTime() - start;		
//		CTree.listLoadTime.add(new LoadTimeOperation("updatePathList()", runningTime + "", pathFileDB));
	}
	
	public void updatePathListFormItem(Path target) {

		long start = new Date().getTime();
		System.out.println("target: " + target);
		paths.clear();
		try {
			Files.walkFileTree(target, new SimpleFileVisitor<Path>() {
				    
				@Override
			        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						paths.add(file);
						return FileVisitResult.CONTINUE;
					}
				    
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//						Files.delete(dir);
						paths.add(dir);
						return FileVisitResult.CONTINUE;
					}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		this.paths = pathList.loadDB();
		long runningTime = new Date().getTime() - start;		
		CTree.listLoadTime.add(new LoadTimeOperation("updatePathList()", runningTime + "", pathFileDB));
	}
	
	 public void startCreateTree(TreeItem<PathItem> mainItem, boolean clearTree, boolean saveSelections) {

		 if (saveSelections) {
			saveAllSelectedItems();
		 }
		 
		 addAllExpandedItems();
		 
		 if (cTree.getTree().getRoot() != null && clearTree) {			 
			cTree.getTree().getRoot().getChildren().clear();
		 }
		 
//		 for (Path path : this.paths) {
//			 if (!path.toString().equals("")) {
//				System.out.println(path);
//			}
//		 }	
		 
		 long start = new Date().getTime();
		 
		 for (Path path : this.paths) {
			 if (!path.toString().equals("")) {
				 List<String> liste = getListParents(path);
				 if (!liste.isEmpty()) {
					isChild(mainItem, liste);					
				 }
			}
		 }	
		 

		 
		long runningTime = new Date().getTime() - start;			
		CTree.listLoadTime.add(new LoadTimeOperation("startCreateTree()", runningTime + "", mainItem.getValue().toString()));
		
		
		LoadTime.Start();
		if (cTree.getTree().getRoot() != null) {
			 cTree.getTree().getSelectionModel().clearSelection();
			 select(selectedItems, cTree.getTree().getRoot(), cTree.getTree());
		 }
		LoadTime.Stop("select Saved Items", selectedItems.size() + "");

		
		
//		long start1 = new Date().getTime();
//		cTree.sortTreeItems(mainItem);
//		long runningTime1 = new Date().getTime() - start1;			
//		CTree.listLoadTime.add(new LoadTimeOperation("sortTreeItems()", runningTime1 + "", pathFileDB));
		
	 }
	
	 private List<String> getListParents(Path path) {
		 List<String> list = new ArrayList<>(); 		 
		 int nameCount = path.getNameCount();		 
		 Path par = path;		 
		 list.add(par.toString());
		 for (int i = 0; i < nameCount; i++) {
			
			par = par.getParent();
			// TODO - hier den mainDirectory von CTree holen und den mainDirectory aus der PathListe entfernen
			if (par.toString().equalsIgnoreCase("D:\\") || par.toString().equalsIgnoreCase("D:\\test")) {
				continue;
			}
			list.add(par.toString());
		 }
		 
		 sortStringListByLength(list);
		 return list;
	}
	 
	private void sortStringListByLength(List<String> list) {
	        Collections.sort(list, Comparator.comparing(String::length));
	} 
	
	private boolean isChild(TreeItem<PathItem> mainItem, List<String> liste) {	
		
		 if (liste.size() == 0) {
			return true;			
		 } 

//		 if (mainItem == null) {
//			 mainItem = new TreeItem<PathItem>(new PathItem(new File(cTree.getMainDirectory()).toPath()));	
//		 }
		 
	 	if (mainItem.getChildren().isEmpty()) {
			TreeItem<PathItem> newItem = null;
			if (liste.size() != 0) {
				String newPath = liste.get(0);
			 	newItem = new TreeItem<PathItem>(new PathItem(new File(newPath).toPath()));					 	
//			 	System.out.println("add Empty: " + newPath + " ->  in: " + mainItem.getValue().getPath());			
//				if (mainItem.getChildren() == null) {
//					System.out.println("is added");
//				} else {
//					System.out.println("is not Added: " + mainItem.getChildren().size());
//					
//					for (TreeItem<PathItem> string : mainItem.getChildren()) {
//						System.out.println(string.getValue().getPath());
//					}
//					
//				}
			 	
				boolean b = mainItem.getChildren().add(newItem);
//				System.out.println("add Empty:" + selectedItems.size() + " -> " + newItem.getValue().getPath());
//				selectSavedItem(newItem, selectedItems, cTree.getTree());
				expandAllSavedItems(newItem, expandedItemList);
//				if (cTree.getTree().getRoot() != null) {
//					searchAndSelectItem(cTree.getTree(), cTree.getTree().getRoot(), newItem.getValue().getPath().toString());
//				}
				
//				selectItem(cTree.getTree(), newItem);
//				cTree.getTree().refresh();

				liste.remove(newPath);					
			 } 
			
			 isChild(newItem, liste);								
		} else {
//			System.out.println("Anzahl: " + mainItem.getChildren().size());
			Pair<Boolean, TreeItem<PathItem>> isChildExists = isChildExists(mainItem, liste);
			
			if (liste.isEmpty()) {
				return true;
			}
			
			if (!isChildExists.getKey()) {
				String newPath = liste.get(0);			
				TreeItem<PathItem> newItem = isChildExists.getValue();					
				if (!newItem.getValue().getPath().toString().equals(mainItem.getValue().getPath().toString())) {
//					System.out.println("add Child: " + newItem.getValue().getPath() + " ->  in: " + mainItem.getValue().getPath());								 
					mainItem.getChildren().add(newItem);
//					System.out.println("add Child:" + selectedItems.size() + " -> " + newItem.getValue().getPath());
//					selectSavedItem(newItem, selectedItems, cTree.getTree());
					expandAllSavedItems(newItem, expandedItemList);
//					if (cTree.getTree().getRoot() != null) {
//						searchAndSelectItem(cTree.getTree(), cTree.getTree().getRoot(), newItem.getValue().getPath().toString());
//					}
//					selectItem(cTree.getTree(), newItem);
				} else {
					newItem = mainItem;
				}
				liste.remove(newPath);
			
				isChild(newItem, liste);
				if(liste.isEmpty()) {
					return true;
				} 						
			} else {					
				TreeItem<PathItem> mainItem1 = isChildExists.getValue();
				isChild(mainItem1, liste);
			}

		}

		return true;
	}
	
	private Pair<Boolean, TreeItem<PathItem>> isChildExists(TreeItem<PathItem> mainItem, List<String> liste) {
		 TreeItem<PathItem> newItem = null;
		 if (liste.size() != 0) {
			 for (TreeItem<PathItem> child : mainItem.getChildren()) {
					String childPath = child.getValue().getPath().toString();
					String newPath = liste.get(0);
					newItem = new TreeItem<PathItem>(new PathItem(new File(newPath).toPath()));
						if(childPath.equalsIgnoreCase(newPath.toString())) {
							liste.remove(newPath);
							
//							isChild(child, liste);
							
							if(liste.isEmpty()) {
								return new Pair<Boolean, TreeItem<PathItem>>(false, mainItem);	
							} 
							return new Pair<Boolean, TreeItem<PathItem>>(true, child);	
							
						} else {	
							continue;
						}
			 }
		 }
		return new Pair<Boolean, TreeItem<PathItem>>(false, newItem);		
	}

	@Override
	public void addAllExpandedItems() {
		expandedItemList = addAllExpandedItems(cTree.getTree().getRoot());
	}

	@Override
	public void saveAllSelectedItems() {
		
		selectedItems = addAllSelectedItems(cTree.getTree());
		for (TreeItem<PathItem> path : selectedItems) {
			System.err.println("selectedItems: " + path);
		}
		System.err.println("saveAllSelectedItems: " + selectedItems.size());
	}
	
	
}
