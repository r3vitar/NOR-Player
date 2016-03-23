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
        
            return selectedFiles;
        
    }
    public File chooseSingleFile(){
        return fileChooser.showOpenDialog(new Stage());
     
    }
    
}