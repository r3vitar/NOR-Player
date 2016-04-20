package nor.player;

import javafx.util.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.event.*;

/**
 * A cell with CSS class chosen by value.
 *
 *
 * @author Philipp Radler
 * special thanks to Bernhard Bodenstorfer for helping me design this class
 */
public class StyledCell<T> extends TableCell<LineItem, T> {
    private int playingIndex = 0;
    @Override
    public void updateItem(final T item, final boolean empty) {
        super.updateItem(item, empty);
        if (empty || null == item) {
            setText(null);
            setGraphic(null);
        } else {
            TableRow<LineItem> tr = getTableRow();
            if (item.equals(playingIndex+1)) {
                tr.getStyleClass().add("emphasisedRow");
                
                
            } else {
                tr.getStyleClass().remove("emphasisedRow");
            }
            setText(item.toString());
        }
    }
    
    public void setIndex(int playingIndex){
        this.playingIndex = playingIndex;
    }
}
