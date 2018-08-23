package app.TreeViewWatchService;

import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.util.Callback;

public class RecursiveTreeItem<PathItem> extends TreeItem<PathItem> {

    private Callback<PathItem, ObservableList<PathItem>> childrenFactory;

    private Callback<PathItem, Node> graphicsFactory;

    public RecursiveTreeItem(Callback<PathItem, ObservableList<PathItem>> childrenFactory){
        this(null, childrenFactory);
    }

    public RecursiveTreeItem(final PathItem value, Callback<PathItem, ObservableList<PathItem>> childrenFactory){
        this(value, (item) -> null, childrenFactory);
    }

    public RecursiveTreeItem(final PathItem value, Callback<PathItem, Node> graphicsFactory, Callback<PathItem, ObservableList<PathItem>> childrenFactory){
        super(value, graphicsFactory.call(value));

        this.graphicsFactory = graphicsFactory;
        this.childrenFactory = childrenFactory;

        if(value != null) {
            addChildrenListener(value);
        }

        valueProperty().addListener((obs, oldValue, newValue)->{
            if(newValue != null){
                addChildrenListener(newValue);
            }
        });

        this.setExpanded(true);
    }

    private void addChildrenListener(PathItem value){
        final ObservableList<PathItem> children = childrenFactory.call(value);

        children.forEach(child ->  RecursiveTreeItem.this.getChildren().add(
            new RecursiveTreeItem<>(child, this.graphicsFactory, childrenFactory)));

        children.addListener((ListChangeListener<PathItem>) change -> {
            while(change.next()){

                if(change.wasAdded()){
                    change.getAddedSubList().forEach(t-> RecursiveTreeItem.this.getChildren().add(
                        new RecursiveTreeItem<>(t, this.graphicsFactory, childrenFactory)));
                }

                if(change.wasRemoved()){
                    change.getRemoved().forEach(t->{
                        final List<TreeItem<PathItem>> itemsToRemove = RecursiveTreeItem.this
                                .getChildren()
                                .stream()
                                .filter(treeItem -> treeItem.getValue().equals(t))
                                .collect(Collectors.toList());

                        RecursiveTreeItem.this.getChildren().removeAll(itemsToRemove);
                    });
                }

            }
        });
    }
}

