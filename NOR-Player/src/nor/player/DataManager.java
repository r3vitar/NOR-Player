package nor.player;

import java.io.File;
import java.util.List;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Philipp Radler
 */
public class DataManager {

    final private FileChooser fileChooser = new FileChooser();

    public DataManager() {
    }

    public List chooseMultipleFiles() {
        return fileChooser.showOpenMultipleDialog(new Stage());

    }
    public File chooseSingleFile(){
        return fileChooser.showOpenDialog(new Stage());
    }
}