package nor.player;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.media.AudioClip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Philipp Radler
 */
public class DataManager {

    private Desktop desktop = Desktop.getDesktop();

    private FileChooser fileChooser = new FileChooser();

    public DataManager() {
    }

    public List chooseMultipleFiles() {
        return fileChooser.showOpenMultipleDialog(new Stage());

    }
}