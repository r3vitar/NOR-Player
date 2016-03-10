package nor.player;

import java.io.File;
import java.util.List;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *
 * @author Philipp Radler
 */
public class DataManager {
    final private ExtensionFilter fileExtentions;
    final private FileChooser fileChooser = new FileChooser();

    public DataManager() {
        fileExtentions = new ExtensionFilter("Alle","*.mp3", "*.wav");
        fileChooser.getExtensionFilters().add(fileExtentions);
    }

    public List chooseMultipleFiles() {
        List selectedFiles = fileChooser.showOpenMultipleDialog(new Stage());
        if(selectedFiles != null)
            return selectedFiles;
        else
            return null;
    }
    public File chooseSingleFile(){
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if(selectedFile != null)
            return selectedFile;
        else
            return null;
    }
    
}