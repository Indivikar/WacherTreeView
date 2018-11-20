package app.threads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

import app.controller.CTree;
import app.functions.LoadTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.stage.Stage;

public class LoadDBService extends Service<ObservableList<Path>>{

	private File pathFileDB;
	private CTree cTree;
	
	private ObservableList<Path> fileList = FXCollections.observableArrayList();	
	
	public LoadDBService(CTree cTree, String pathFileDB) {
		this.cTree = cTree;
		this.pathFileDB = new File(pathFileDB);
	}

	@Override
	protected void cancelled() {
		reset();
	}
	
	@Override
	protected void failed() {
		reset();
	}
	
	@Override
	protected void scheduled() {
		System.out.println("scheduled()");
		cTree.getTree().getScene().setCursor(Cursor.WAIT);
	}
	
	@Override
	protected void succeeded() {
		System.out.println("succeeded()");
		cTree.getTree().getScene().setCursor(Cursor.DEFAULT);
		reset();
	}
	
	
	@Override
	protected Task<ObservableList<Path>> createTask() {
		
		 return new Task<ObservableList<Path>>() {
             @Override
             protected ObservableList<Path> call() throws Exception {
                 
            	LoadTime.Start();
            	 
            	fileList.clear();
//         		try (BufferedReader br = new BufferedReader(new FileReader(pathFileDB)))
         		try (BufferedReader br = new BufferedReader(new InputStreamReader(
         			    new FileInputStream(pathFileDB), "UTF-8")))
                 {

                     String line;

                     while ((line = br.readLine()) != null) {
                     	fileList.add(new File(line).toPath());
                     }

                     br.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
         		LoadTime.Stop("updatePathList()", "");
         		
            	 return fileList;
             }
         };
	}

}
