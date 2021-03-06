package nor.player;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * Diese  Klasse ist für das einlesen der Dateien zuständig.
 * 
 * @author Philipp Radler
 */
public class DataManager {
    
    private HashMap<String, FileChooser> fileChooser = new HashMap<String, FileChooser>();
    
    /**
     * Der Kunstruktor beinhaltet alle fileChooser für die verschiedenen Datentypen in einer HashMap.
     */
    public DataManager() {
        

        fileChooser.put("audio", new FileChooser());
        fileChooser.get("audio").getExtensionFilters().add(new ExtensionFilter("All Supported Audio", "*.mp3", "*.mp2", "*.mp1", "*.aac", "*.vlb", "*.wav", "*.flac", "*.alac"));
        fileChooser.get("audio").getExtensionFilters().add(new ExtensionFilter("MPEG", "*.mp3", "*.mp2", "*.mp1", "*.aac", "*.vlb"));
        fileChooser.get("audio").getExtensionFilters().add(new ExtensionFilter("LossLess", "*.wav", "*.flac", "*.alac"));
        fileChooser.get("audio").setTitle("Audio");

        fileChooser.put("playlist", new FileChooser());
        fileChooser.get("playlist").getExtensionFilters().add(new ExtensionFilter("NPL", "*.npl"));
        fileChooser.get("playlist").getExtensionFilters().add(new ExtensionFilter("M3U8 / M3U", "*.m3u", "*.m3u8"));
       // fileChooser.get("playlist").getExtensionFilters().add(new ExtensionFilter("PLS", "*.pls"));
        fileChooser.get("playlist").getExtensionFilters().add(new ExtensionFilter("All Supported Playlistfiles", "*.npl", "*.m3u", "*.m3u8"/*, "*.pls"*/));
        fileChooser.get("playlist").setTitle("Playlist");

        fileChooser.put("npl", new FileChooser());
        fileChooser.get("npl").getExtensionFilters().add(new ExtensionFilter("Playlist", "*.npl"));
        fileChooser.get("npl").setTitle("NPL");

        fileChooser.put("media", new FileChooser());
        fileChooser.get("media").getExtensionFilters().add(new ExtensionFilter("All Supported Audio", "*.mp3", "*.aac", "*.vlb", "*.wav", "*.flac", "*.alac"));
        fileChooser.get("media").getExtensionFilters().add(new ExtensionFilter("MPEG", "*.mp3", "*.aac", "*.vlb"));
        fileChooser.get("media").getExtensionFilters().add(new ExtensionFilter("LossLess", "*.wav", "*.flac", "*.alac"));
        fileChooser.get("media").getExtensionFilters().add(new ExtensionFilter("Video", "*.mp4", "*.avi", "*.mkv"));
        fileChooser.get("media").setTitle("Media");

        fileChooser.put("all", new FileChooser());
        fileChooser.get("all").getExtensionFilters().add(new ExtensionFilter("All Supported Files", "*.mp3", "*.aac", "*.vlb", "*.wav", "*.flac", "*.alac", "*.mp4", "*.avi", "*.mkv", "*.npl","*.m3u8"));
        fileChooser.get("all").getExtensionFilters().add(new ExtensionFilter("All Supported Audio", "*.mp3", "*.aac", "*.vlb", "*.wav", "*.flac", "*.alac"));
        fileChooser.get("all").getExtensionFilters().add(new ExtensionFilter("MPEG", "*.mp3",  "*.aac", "*.vlb"));
        fileChooser.get("all").getExtensionFilters().add(new ExtensionFilter("LossLess", "*.wav", "*.flac", "*.alac"));
        fileChooser.get("all").getExtensionFilters().add(new ExtensionFilter("NPL", "*.npl"));
        fileChooser.get("all").getExtensionFilters().add(new ExtensionFilter("M3U8 / M3U", /*"*.m3u",*/ "*.m3u8"));
        //fileChooser.get("all").getExtensionFilters().add(new ExtensionFilter("PLS", "*.pls"));
        fileChooser.get("all").getExtensionFilters().add(new ExtensionFilter("All Supported Playlistfiles", "*.npl", "*.m3u", "*.m3u8"/*,"*.pls"*/));
        fileChooser.get("all").getExtensionFilters().add(new ExtensionFilter("Video", "*.mp4", "*.avi", "*.mkv"));

        //fileChooser.get("all").getExtensionFilters().add(new ExtensionFilter("All", "*.*"));
        fileChooser.get("all").setTitle("NOR-Player");

        //fileChooser.getExtensionFilters().add(new ExtensionFilter("All", "*.*"));
    }
    
    /**
     * 
     * @param key Der Schlüssel, der angibt, welche Dateitypen man haben soll.
     * @return Man bekommt eine Liste zurück, welche alle ausgewählten Files beinhaltet.
     */
    public List chooseMultipleFiles(String key) {
        List selectedFiles = fileChooser.get(key).showOpenMultipleDialog(new Stage());

        return selectedFiles;

    }
    
    /**
     * 
     * @param key Der Schlüssel, der angibt, welche Dateitypen man haben soll.
     * @return File, der alle passenden Dateien zurückgibt.
     */
    public File chooseSingleFile(String key) {
        return fileChooser.get(key).showOpenDialog(new Stage());

    }
    
    /**
     * 
     * @return Gibt die aktuelle Playlist als File zurück.
     */
    public File savePlaylist() {
        return fileChooser.get("npl").showSaveDialog(new Stage());
    }

}
