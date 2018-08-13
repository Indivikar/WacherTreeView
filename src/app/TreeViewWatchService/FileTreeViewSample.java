package app.TreeViewWatchService;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class FileTreeViewSample extends Application {
	
	private PathTreeCell cell;
	public static final Set<PathTreeCell> myTreeCells = Collections.newSetFromMap(new WeakHashMap<>());
	public static TreeItem<PathItem> treeItem;
	
    private ExecutorService service;
//    private Task watchTask;
    private TextField rootDirText;
    private Path rootPath;
    private Button dispBtn;
    private Text messageText;
    private StringProperty messageProp= new SimpleStringProperty();
    private TreeView<PathItem> fileTreeView;
    private CheckBox watchChkbox;
    private TextArea watchText;
    private TextField patternText;
    private Button searchBtn;
    private ListView<String> searchListView;
    private ObservableList<String> searchListItem;
    private StringProperty searchProp = new SimpleStringProperty();
    private List<String> searchList = new ArrayList<>();
    private Label searchCountLabel;
    public static ArrayList<PathTreeCell> children = new ArrayList<>();
    
    
    public FileTreeViewSample() {
        fileTreeView = new TreeView<>();
        fileTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        service = Executors.newFixedThreadPool(3);
    }

    @Override
    public void start(final Stage stage) {
        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);
        // root Directory
        HBox rootHbox = getRootHbox(stage);
        TitledPane titledPane = new TitledPane("File Tree View", fileTreeView);
        titledPane.setPrefHeight(300);

        setEventHandler(stage);
        root.getChildren().addAll(rootHbox, titledPane);
//        root.getChildren().addAll(rootHbox, titledPane, messageText, watchChkbox, watchText, searchHbox, searchListView);
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("File Tree View Sample");
        stage.setScene(scene);
        stage.setX(6700);
        stage.show();
        Platform.runLater(() -> rootDirText.requestFocus());
    }

    private HBox getRootHbox(final Stage stage) {
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        Label label = new Label("root Directory:");
        rootDirText = new TextField();
        rootDirText.setPrefWidth(300);
        final DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("select Directory");
        Image image = new Image(getClass().getResourceAsStream("OpenDirectory.png"));
        Button chooserBtn = new Button("", new ImageView(image));
        chooser.setTitle("Select Root Directory");
        chooserBtn.setOnAction(event -> {
            File selDir = chooser.showDialog(stage);
            if (selDir != null) {
                rootDirText.setText(selDir.getAbsolutePath());
            }
        });
        dispBtn = new Button("Display File Tree");
        hbox.getChildren().addAll(label, rootDirText, chooserBtn, dispBtn);
        return hbox;
    }

    private void setEventHandler(final Stage stage) {

        // Display File Tree Button


//            if (watchTask != null && watchTask.isRunning()) {
//                watchTask.cancel();
//            }

//            rootPath = Paths.get("F:\\Test");
//            PathItem pathItem = new PathItem(rootPath);
//            fileTreeView.setRoot(createNode(pathItem));
//            fileTreeView.setEditable(true);
            rootPath = Paths.get("D:\\Test");
            PathItem pathItem = new PathItem(rootPath);
            treeItem = new TreeItem<PathItem>(pathItem);
            treeItem.setExpanded(false);

            // create tree structure
            try {
				createTree(treeItem, false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            fileTreeView.setRoot(treeItem);
            

            
            fileTreeView.setCellFactory((TreeView<PathItem> p) -> {
                cell = new PathTreeCell(stage, messageProp);
                Platform.runLater(() -> myTreeCells.add(cell));
                new DragNDropInternal(stage, service, cell);
//                setDragDropEvent(stage, cell);
                return cell;
            });
            
            System.out.println("vor myTreeCells: " + myTreeCells.size());
            for (PathTreeCell item : myTreeCells) {
				System.out.println("myTreeCells: " + item.getItem().getPath());
			}
            
            
            boolean recursive = true;
            try {
            	WatchTask3 watchTask = new WatchTask3(rootPath, recursive, cell, fileTreeView);
            	service.submit(watchTask);

            	
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
    }
            

    public static void createTree(TreeItem<PathItem> rootItem, boolean expand) throws IOException {

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rootItem.getValue().getPath())) {

            for (Path path : directoryStream) {

                TreeItem<PathItem> newItem = new TreeItem<PathItem>( new PathItem( path));
//                newItem.setExpanded(expand);

                rootItem.getChildren().add(newItem);

                if (Files.isDirectory(path)) {
                    createTree(newItem, expand);
                }
            }
        }
        // catch exceptions, e. g. java.nio.file.AccessDeniedException: c:\System Volume Information, c:\$RECYCLE.BIN
        catch( Exception ex) {
            ex.printStackTrace();
        }
    }
    
    
    
    private TreeItem<PathItem> createNode(PathItem pathItem) {
        return PathTreeItem.createNode(pathItem);
    }
    
    public PathTreeCell getCell() {return cell;}

	public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        service.shutdownNow();
    }
}
