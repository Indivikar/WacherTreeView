package app.test;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.function.Function;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FolderTreeViewWithFilter extends Application {

    private static final String ROOT_FOLDER = "D:\\Test"; // TODO: change or make selectable

    TreeItem<FilePath> rootTreeItem;
    TreeView<FilePath> treeView;

    @Override
    public void start(Stage primaryStage) throws IOException {

        // root component
        VBox root = new VBox();

        // filter
        TextField filter = new TextField();
        filter.textProperty().addListener((observable, oldValue, newValue) -> filterChanged(newValue));

        // treeview
        treeView = new TreeView<FilePath>();
        VBox.setVgrow(treeView, Priority.ALWAYS);

        root.getChildren().addAll( filter, treeView);

        // stage
        primaryStage.setScene(new Scene(root, 1024, 768));
        primaryStage.setTitle("Folder Tree View With Filter Example");
        primaryStage.show();

        // create tree
        createTree();

        // show tree structure in tree view
        treeView.setRoot(rootTreeItem);
    }

    /**
     * Create original tree structure
     * @throws IOException
     */
    private void createTree() throws IOException {

        // create root
        rootTreeItem = createTreeRoot();

        // create tree structure recursively
        createTree( rootTreeItem);

        // sort tree structure by name
        rootTreeItem.getChildren().sort( Comparator.comparing( new Function<TreeItem<FilePath>, String>() {
            @Override
            public String apply(TreeItem<FilePath> t) {
                return t.getValue().toString().toLowerCase();
            }
        }));

    }

    /**
     * Iterate through the directory structure and create a file tree
     * @param rootItem
     * @throws IOException
     */
    public static void createTree(TreeItem<FilePath> rootItem) throws IOException {

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rootItem.getValue().getPath())) {

            for (Path path : directoryStream) {

                TreeItem<FilePath> newItem = new TreeItem<FilePath>( new FilePath( path));
                newItem.setExpanded(true);

                rootItem.getChildren().add(newItem);

                if (Files.isDirectory(path)) {
                    createTree(newItem);
                }
            }
        }
        // catch exceptions, e. g. java.nio.file.AccessDeniedException: c:\System Volume Information, c:\$RECYCLE.BIN
        catch( Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Create new filtered tree structure
     * @param root
     * @param filter
     * @param filteredRoot
     */
    private void filter(TreeItem<FilePath> root, String filter, TreeItem<FilePath> filteredRoot) {

        for (TreeItem<FilePath> child : root.getChildren()) {

            TreeItem<FilePath> filteredChild = new TreeItem<>( child.getValue());
            filteredChild.setExpanded(true);

            filter(child, filter, filteredChild );

            if (!filteredChild.getChildren().isEmpty() || isMatch(filteredChild.getValue(), filter)) {
                filteredRoot.getChildren().add(filteredChild);
            }

        }
    }

    /**
     * Comparator for tree filter
     * @param value
     * @param filter
     * @return
     */
    private boolean isMatch(FilePath value, String filter) {
        return value.toString().toLowerCase().contains( filter.toLowerCase()); // TODO: optimize or change (check file extension, etc)
    }

    /**
     * Show original tree or filtered tree depending on filter
     * @param filter
     */
    private void filterChanged(String filter) {
        if (filter.isEmpty()) {
            treeView.setRoot(rootTreeItem);
        }
        else {
            TreeItem<FilePath> filteredRoot = createTreeRoot();
            filter(rootTreeItem, filter, filteredRoot);
            treeView.setRoot(filteredRoot);
        }
    }

    /**
     * Create root node. Used for the original tree and the filtered tree.
     * Another option would be to clone the root.
     * @return
     */
    private TreeItem<FilePath> createTreeRoot() {
        TreeItem<FilePath> root = new TreeItem<FilePath>( new FilePath( Paths.get( ROOT_FOLDER)));
        root.setExpanded(true);
        return root;
    }

    /**
     * Wrapper for the path with overwritte toString method. We only want to see the last path part as tree node, not the entire path.
     */
    private static class FilePath {

        Path path;
        String text;

        public FilePath( Path path) {

            this.path = path;

            // display text: the last path part
            // consider root, e. g. c:\
            if( path.getNameCount() == 0) {
                this.text = path.toString();
            }
            // consider folder structure
            else {
                this.text = path.getName( path.getNameCount() - 1).toString();
            }

        }

        public Path getPath() {
            return path;
        }

        public String toString() {

            // hint: if you'd like to see the entire path, use this:
            // return path.toString();

            // show only last path part
            return text;

        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
