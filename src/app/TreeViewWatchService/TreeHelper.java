package app.TreeViewWatchService;

import javafx.event.Event;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;

public class TreeHelper {
    /** Trigger a redraw of a tree item
     *
     *  <p>Call when the model item's representation changed,
     *  so tree item with existing value object needs to be
     *  redrawn.
     *
     *  @param item {@link TreeItem}
     */
    public static <PathItem> void triggerTreeItemRefresh(final TreeItem<PathItem> item) {
        // TreeView or TreeItem has no 'refresh()', update or redraw method.
        // 'setValue' only triggers a refresh of the item if value is different
        //
         final PathItem value = item.getValue();
         item.setValue(null);
         item.setValue(value);

        // The API does expose the valueChangedEvent(), so send that
        Event.fireEvent(item, new TreeModificationEvent<PathItem>(TreeItem.<PathItem>valueChangedEvent(), item, item.getValue()));
    }
}
