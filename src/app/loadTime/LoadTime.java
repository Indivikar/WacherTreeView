package app.loadTime;

import app.controller.CTree;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
 
public class LoadTime extends Application {
 
    private TableView<LoadTimeOperation> table = new TableView<LoadTimeOperation>();
    private final ObservableList<LoadTimeOperation> data =
        FXCollections.observableArrayList(
            new LoadTimeOperation("Jacob", "Smith", "jacob.smith@example.com"),
            new LoadTimeOperation("Isabella", "Johnson", "isabella.johnson@example.com"),
            new LoadTimeOperation("Ethan", "Williams", "ethan.williams@example.com"),
            new LoadTimeOperation("Emma", "Jones", "emma.jones@example.com"),
            new LoadTimeOperation("Michael", "Brown", "michael.brown@example.com")
        );
   
    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle("Table View - LoadTime");
        stage.setWidth(450);
        stage.setHeight(550);
 
        final Label label = new Label("Load Time");
        label.setFont(new Font("Arial", 20));
 
        Button buttonClear = new Button("Clear");
        buttonClear.setOnAction((e) -> {
        	CTree.listLoadTime.clear();
        });
        
        table.setEditable(true);
 
        TableColumn firstNameCol = new TableColumn("Operation");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<LoadTimeOperation, String>("operation"));
 
        TableColumn lastNameCol = new TableColumn("Time (ms)");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<LoadTimeOperation, String>("time"));
 
        TableColumn emailCol = new TableColumn("File");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(
                new PropertyValueFactory<LoadTimeOperation, String>("file"));
 
        table.setItems(CTree.listLoadTime);
        table.getColumns().addAll(firstNameCol, lastNameCol, emailCol);
 
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table, buttonClear);
 
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        
        stage.setX(6600);
        stage.setScene(scene);
        stage.show();
    }
 
    public static class LoadTimeOperation {
 
        private final SimpleStringProperty operation;
        private final SimpleStringProperty time;
        private final SimpleStringProperty file;
 
        public LoadTimeOperation(String operation, String time, String file) {
            this.operation = new SimpleStringProperty(operation);
            this.time = new SimpleStringProperty(time);
            this.file = new SimpleStringProperty(file);
        }
 
        public String getOperation() {return operation.get();}
        public String getTime() {return time.get();}
        public String getFile() {return file.get();}
 
        public void setOperation(String fName) {operation.set(fName);}
        public void setTime(String fName) {time.set(fName);}
        public void setFile(String fName) {file.set(fName);}
    }
} 
