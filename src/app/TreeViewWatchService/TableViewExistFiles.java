package app.TreeViewWatchService;


import app.models.ExistFiles;
import app.models.ReplaceOrIgnore;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

public class TableViewExistFiles extends TableView<ExistFiles>{

	public TableViewExistFiles() {

        // Editable
		setEditable(true);
 
        TableColumn<ExistFiles, String> fileNameCol //
                = new TableColumn<ExistFiles, String>("File Name");
    
        TableColumn<ExistFiles, ReplaceOrIgnore> replaceOrIgnoreCol//
                = new TableColumn<ExistFiles, ReplaceOrIgnore>("replace or ignore");
        
        TableColumn<ExistFiles, String> filePathCol //
        = new TableColumn<ExistFiles, String>("Path");
 
        TableColumn<ExistFiles, Boolean> sizeCol//
                = new TableColumn<ExistFiles, Boolean>("Size");
 
        fileNameCol.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        filePathCol.setCellValueFactory(new PropertyValueFactory<>("filePath"));
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        
//        fileNameCol.setCellFactory(TextFieldTableCell.<ExistFiles> forTableColumn());
 
        fileNameCol.setMinWidth(200);

 
        // ==== ReplaceOrIgnore (COMBO BOX) ===
 
        ObservableList<ReplaceOrIgnore> replaceOrIgnoreList = FXCollections.observableArrayList(//
        		ReplaceOrIgnore.values());
 
        replaceOrIgnoreCol.setCellValueFactory(new Callback<CellDataFeatures<ExistFiles, ReplaceOrIgnore>, ObservableValue<ReplaceOrIgnore>>() {
 
            @Override
            public ObservableValue<ReplaceOrIgnore> call(CellDataFeatures<ExistFiles, ReplaceOrIgnore> param) {
            	ExistFiles existFiles = param.getValue();
                // F,M
                String replaceOrIgnoreCode = existFiles.getReplaceOrIgnore();
                ReplaceOrIgnore replaceOrIgnore = ReplaceOrIgnore.getByCode(replaceOrIgnoreCode);
                return new SimpleObjectProperty<ReplaceOrIgnore>(replaceOrIgnore);
            }
        });
 
        replaceOrIgnoreCol.setCellFactory(ComboBoxTableCell.forTableColumn(replaceOrIgnoreList));
 
        replaceOrIgnoreCol.setOnEditCommit((CellEditEvent<ExistFiles, ReplaceOrIgnore> event) -> {
            TablePosition<ExistFiles, ReplaceOrIgnore> pos = event.getTablePosition();
 
            ReplaceOrIgnore newReplaceOrIgnore = event.getNewValue();
 
            int row = pos.getRow();
            ExistFiles existFiles = event.getTableView().getItems().get(row);
 
            existFiles.setReplaceOrIgnore(newReplaceOrIgnore.getCode());
        });
 
        replaceOrIgnoreCol.setMinWidth(120);
 

 

 
        ObservableList<ExistFiles> list = getPersonList();
        setItems(list);
        
        getColumns().addAll(fileNameCol, replaceOrIgnoreCol, filePathCol, sizeCol);
		
	}

    private ObservableList<ExistFiles> getPersonList() {
    	 
    	ExistFiles existFiles1 = new ExistFiles("Susan Smith", ReplaceOrIgnore.Ignore.getCode(), "1",  "2");
    	ExistFiles existFiles2 = new ExistFiles("Anne McNeil", ReplaceOrIgnore.Ignore.getCode(), "1",  "2");
    	ExistFiles existFiles3 = new ExistFiles("Kenvin White", ReplaceOrIgnore.Replace.getCode(), "1",  "2");
 
        ObservableList<ExistFiles> list = FXCollections.observableArrayList(existFiles1, existFiles2, existFiles3);
        return list;
    }
	
}
