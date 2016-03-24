package nor.player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *
 * @author Philipp Radler
 */
public class DataManager {
    
    final private FileChooser fileChooser = new FileChooser();

    public DataManager() {
       
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Audio", "*.mp3", "*.wav", "*.aac", "*.flac"));
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Video", "*.mp4", "*.avi", "*.mkv"));
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Playlist", "*.npl", "*.m3u", "*.m3u8", "*.pls"));
        //fileChooser.getExtensionFilters().add(new ExtensionFilter("All", "*.*"));
        
    }

    public List chooseMultipleFiles() {
        List selectedFiles = fileChooser.showOpenMultipleDialog(new Stage());
     
        
        
            return selectedFiles;
        
    }
    public File chooseSingleFile(){
        return fileChooser.showOpenDialog(new Stage());
     
    }
    
}