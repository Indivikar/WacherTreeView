package app.threads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

import app.TreeViewWatchService.PathTreeCell;
import app.TreeViewWatchService.contextMenu.CellContextMenu;
import app.controller.CTree;
import app.functions.LoadTime;
import app.models.ItemsDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.stage.Stage;

public class LoadDBService extends Service<ObservableList<ItemsDB>>{

	private File pathFileDB;
	private CTree cTree;
	
	private ObservableList<ItemsDB> fileList = FXCollections.observableArrayList();	
	
	public LoadDBService(CTree cTree, String pathFileDB) {
		this.cTree = cTree;
		this.pathFileDB = new File(pathFileDB);
	}

	@Override
	protected void cancelled() {
		reset();
//		cellContextMenuBinding(false);
	}
	
	@Override
	protected void failed() {
		reset();
//		cellContextMenuBinding(false);
	}
	
	@Override
	protected void scheduled() {
		System.out.println("scheduled()");
		cTree.getTree().getScene().setCursor(Cursor.WAIT);
//		cellContextMenuBinding(true);
	}
	
	@Override
	protected void succeeded() {
		System.out.println("succeeded()");
		cTree.getTree().getScene().setCursor(Cursor.DEFAULT);	
//		cellContextMenuBinding(false);
		reset();
		
//        SortWinExplorerTask task = new SortWinExplorerTask(cTree, cTree.getTree().getRoot());
//        new Thread(task).start();
		
		
	}
	
	
	
	@Override
	protected Task<ObservableList<ItemsDB>> createTask() {
		
		 return new Task<ObservableList<ItemsDB>>() {
             @Override
             protected ObservableList<ItemsDB> call() throws Exception {
                 
            	 System.out.println("Start LoadDBService");
            	 
            	LoadTime.Start();
            	 
            	Thread.sleep(2000);
            	
            	
            	fileList.clear();
//         		try (BufferedReader br = new BufferedReader(new FileReader(pathFileDB)))
         		try (BufferedReader br = new BufferedReader(new InputStreamReader(
         			    new FileInputStream(pathFileDB), "UTF-8")))
                 {

                     String line;

                     while ((line = br.readLine()) != null) {
                    	String path = cTree.getMainDirectory() + File.separator + line;
                    	boolean isDirectoryPath = false;
                    	
                    	String[] parts = path.split("; ");
                    	String part1 = parts[0]; // Path
                    	String part2 = parts[1]; // is Dir or File
                    	
                    	
                    	if (part2.equalsIgnoreCase("dir")) {
                    		isDirectoryPath = true;
						} else {
							isDirectoryPath = false;
						}
                    	
                     	fileList.add(new ItemsDB(new File(part1).toPath(), isDirectoryPath));
                     }

                     br.close();
                 } catch (IOException e) {
                	 // TODO - V:\test\___DB___\test.txt (Das System kann den angegebenen Pfad nicht finden)
                     e.printStackTrace();
                 }
         		LoadTime.Stop("updatePathList()", "");
         		
            	 return fileList;
             }
         };
	}
	
//	private void cellContextMenuBinding(boolean wert) {
//		PathTreeCell cell = cTree.getCell();
//		if (cell != null) {
//			CellContextMenu contextMenu = cell.getCellContextMenu();
//			if (contextMenu != null) {
//				contextMenu.serviceReloadBinding(wert);
//			}
//		}
//	}
	

}
