package nor.player;

import javafx.util.*;
import javafx.scene.control.*;

/**
 * Equips cells with CSS class
 *
 * @author Philipp Radler
 * special thanks to Bernhard Bodenstorfer for helping me design this class
 */
public class StyledCellFactory<T> implements Callback<TableColumn<LineItem, T>, TableCell<LineItem, T>> {
    private int playingIndex = 0;
    
    @Override
    public TableCell<LineItem, T> call(final TableColumn<LineItem, T> column) {
        StyledCell<T> sc = new StyledCell<T>();
        sc.setIndex(playingIndex);
        return sc;
    }
    
    public void setIndex(int playingIndex){
        this.playingIndex = playingIndex;
    }
}
