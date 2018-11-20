package app.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PathList {

	private File pathFileDB;
	
	private ObservableList<Path> fileList = FXCollections.observableArrayList();

	public PathList(String pathFileDB) {
		this.pathFileDB = new File(pathFileDB);

	}
	
	public ObservableList<Path> loadDB() {
		fileList.clear();
//		try (BufferedReader br = new BufferedReader(new FileReader(pathFileDB)))
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
		
		return fileList; 

	}

}
