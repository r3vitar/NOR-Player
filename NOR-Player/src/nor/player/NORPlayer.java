package nor.player;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 *
 * @author Kacper Olszanski, Philipp Radler, Julian Nenning
 */
public class NORPlayer extends Application implements MediaChangeListener {
    
    Scanner sc = new Scanner(System.in);
    
    private Duration duration;
    MediaView view = new MediaView();
    MediaPlayer mp;
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 700, 300);
    Slider slide = new Slider();
    Slider vol = new Slider();
    Slider balanceSlider = new Slider();
    Slider speedSlider = new Slider();
    
    Button playB = new Button("Start");
    Button pauseB = new Button("Play");
    Button stopB = new Button("Stop");
    Button addB = new Button("Add");
    Button openB = new Button("Open");
    Button clearB = new Button("Clear");
    
    Button nextB = new Button("Next");
    Button prevB = new Button("Prev");
    Button shuffleB = new Button("Shuffle");
    
    Button savePlaylistButton = new Button("savePlaylist");
    Button loadPlaylistButton = new Button("loadPlaylist");
    Label l1 = new Label("test");
    
    Button playlistStageB = new Button("Playlist");
    NORMediaPlayer norMediaPlayer = new NORMediaPlayer(this);
    DataManager manager = new DataManager();
    Label name = new Label("metadata");
    Label mytime = new Label("00:00:00");
    Stage playlistStage = new Stage();
    
    boolean playInit = false;
    
    @Override
    public void start(Stage primaryStage) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    mytime.setId("font");
                    mp = new MediaPlayer(norMediaPlayer.createMedia(new File("NOR.wav")));
                    
                    mp.play();
                    
                } catch (Exception e) {
                    System.err.println(e);
                } finally {
                }
            }
        });
        
        initSliders();
        
        playlistStageB.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                showActivePlaylist();
            }
        });
        
        addB.setOnAction((ActionEvent event) -> {
            
            try {
                Thread t = new Thread(new Runnable() {
                    List dataList = manager.chooseMultipleFiles("all");
                    
                    ArrayList<File> data = new ArrayList<File>(dataList);
                    
                    @Override
                    public void run() {
                        if (data != null && !data.isEmpty()) {
                            norMediaPlayer.addMedia(data);
                            
                        }
                    }
                });
                t.start();
                
                t.join();
                if (!norMediaPlayer.isPlaying()) {
                    norMediaPlayer.play();
                    pauseB.setText("Pause");
                } else {
                    pauseB.setText("Play");
                }
                
            } catch (Exception e) {
                System.err.println(e);
            }
        });
        
        openB.setOnAction((ActionEvent event) -> {
            boolean b = norMediaPlayer.isPlaying();
            
            try {
                
                List dataList = manager.chooseMultipleFiles("media");
                ArrayList<File> data = new ArrayList<File>(dataList);
                
                if (data != null && !data.isEmpty()) {
                    
                    norMediaPlayer.clearPlaylist();
                    
                    norMediaPlayer.addMedia(data);
                    if (b) {
                        norMediaPlayer.play();
                        pauseB.setText("Pause");
                    } else {
                        pauseB.setText("Play");
                    }
                }
                
            } catch (Exception e) {
                System.err.println(e);
            }
        });
        
        playB.setOnAction((ActionEvent event) -> {
            if (norMediaPlayer.isPlaying()) {
                norMediaPlayer.stop();
                norMediaPlayer.play();
                
            } else {
                norMediaPlayer.play();
                norMediaPlayer.stop();
                norMediaPlayer.play();
            }
            
            pauseB.setText("Pause");
            
        });
        pauseB.setOnAction((ActionEvent event) -> {
            if (norMediaPlayer.isPlaying()) {
                pauseB.setText("Play");
            } else {
                pauseB.setText("Pause");
            }
            norMediaPlayer.playOrPause();
        });
        
        nextB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.nextClip();
            
        });
        
        prevB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.prevClip();
            
        });
        
        stopB.setOnAction((ActionEvent event) -> {
            if (norMediaPlayer.isPlaying()) {
                norMediaPlayer.stop();
            } else {
                norMediaPlayer.play();
                norMediaPlayer.stop();
            }
            pauseB.setText("Play");
            
        });
        
        savePlaylistButton.setOnAction((ActionEvent event) -> {
            
            File f = manager.savePlaylist();
            norMediaPlayer.savePlaylist(f.getAbsolutePath());
        });
        loadPlaylistButton.setOnAction((ActionEvent event) -> {
            
            try {
                File f = manager.chooseSingleFile("playlist");
                norMediaPlayer.loadPlaylist(f, true);
                
            } catch (Exception ex) {
                
            }
        });
        shuffleB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.shuffle();
        });
        clearB.setOnAction((ActionEvent event) -> {
            pauseB.setText("Play");
            norMediaPlayer.clearPlaylist();
        });
        
        HBox chooseFile = new HBox();
        
        chooseFile.getChildren().add(addB);
        chooseFile.getChildren().add(openB);
        chooseFile.getChildren().addAll(l1);
        HBox playStop = new HBox(playB, pauseB, stopB, prevB, nextB, savePlaylistButton, loadPlaylistButton, shuffleB);
        
        VBox bottomB;
        bottomB = new VBox(chooseFile, playStop, slide, playlistStageB);
        BorderPane bp1 = new BorderPane(bottomB);
        vol.setOnScroll(new EventHandler<ScrollEvent>() {
            
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    vol.setValue(vol.getValue() + 5);
                } else {
                    vol.setValue(vol.getValue() - 5);
                }
                
            }
        });
        balanceSlider.setOnScroll(new EventHandler<ScrollEvent>() {
            
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    balanceSlider.setValue(balanceSlider.getValue() + 25);
                } else {
                    balanceSlider.setValue(balanceSlider.getValue() - 25);
                }
                
            }
        });
        speedSlider.setOnScroll(new EventHandler<ScrollEvent>() {
            
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    speedSlider.setValue(speedSlider.getValue() + 12.5);
                } else {
                    speedSlider.setValue(speedSlider.getValue() - 12.5);
                }
                
            }
        });
        
        slide.setOnScroll(new EventHandler<ScrollEvent>() {
            
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    norMediaPlayer.getNorPlayer().seek(Duration.millis(slide.getValue() + 5000));
                } else {
                    norMediaPlayer.getNorPlayer().seek(Duration.millis(slide.getValue() - 5000));
                }
                
            }
        });
        
        File lastSession = new File("lastSession.npl");
        if (lastSession.isFile()) {
            try {
                norMediaPlayer.loadPlaylist(lastSession, true);
            } catch (IOException ex) {
            }
        }
        
        bp1.setRight(vol);
        root.setTop(new VBox(mytime, name, balanceSlider, speedSlider, clearB));
        root.setCenter(view);
        root.setBottom(bp1);
        
        primaryStage.setTitle("NOR-Player");
        scene.getStylesheets().add("styles.css");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            
            @Override
            public void handle(WindowEvent event) {
                if (!norMediaPlayer.isEmpty()) {
                    norMediaPlayer.savePlaylist("lastSession.npl");
                }
                playlistStage.close();
            }
        });
        
        primaryStage.show();
    }
    
    private void showActivePlaylist() {
        if (!this.playInit) {
            initPlaylist(this.norMediaPlayer.getPlaylistName());
        }
        if (playlistStage.isShowing()) {
            playlistStage.hide();
        } else {
            
            playlistStage.show();
        }
    }
    
    private void initPlaylist(String playlistTitle) {
        ArrayList<Media> playlistMedia = norMediaPlayer.getPlaylist();
        final ObservableList<LineItem> data = FXCollections.observableArrayList();
        String[] requiredData = {"artist=", "title=", "album="};
        for (int i = 0; i < playlistMedia.size(); i++) {
            String[] temp = readMetadata(requiredData, playlistMedia.get(i).getMetadata().toString());
            data.add(new LineItem(i + 1, temp[1], temp[0], temp[2]));
        }
        
        TableView playlistTable = new TableView();
        TableColumn titleColumn = new TableColumn("Name"),
                interpretColumn = new TableColumn("Interpret"),
                albumColumn = new TableColumn("Album"),
                indexColumn = new TableColumn("Nr");
        
        indexColumn.setCellValueFactory(
                new PropertyValueFactory<LineItem, Integer>("index"));
        titleColumn.setCellValueFactory(
                new PropertyValueFactory<LineItem, String>("name"));
        interpretColumn.setCellValueFactory(
                new PropertyValueFactory<LineItem, String>("interpret"));
        albumColumn.setCellValueFactory(
                new PropertyValueFactory<LineItem, String>("album"));
        
        
        playlistTable.getColumns()
                .addAll(indexColumn, titleColumn, interpretColumn, albumColumn);
        playlistTable.setItems(data);
        playlistTable.setPrefWidth(357);
        
        
        Pane root = new Pane();
        root.getChildren().add(playlistTable);
        Scene playlistScene = new Scene(root, playlistTable.getPrefWidth(), 200);
        playlistStage.setScene(playlistScene);
        playlistStage.setTitle(playlistTitle);
        playlistStage.setOnHiding(new EventHandler<WindowEvent>() {
            
            @Override
            public void handle(WindowEvent event) {
                double x = playlistStage.getX();
                double y = playlistStage.getY();
                double h = playlistStage.getHeight();
                double w = playlistStage.getWidth();
                
                playlistStage.setOnShowing(new EventHandler<WindowEvent>() {
                    
                    @Override
                    public void handle(WindowEvent event) {
                        playlistStage.setX(x);
                        playlistStage.setY(y);
                        playlistStage.setHeight(h);
                        playlistStage.setWidth(w);
                    }
                });
                
            }
        });
        
    }
    
    private String[] readMetadata(String[] requiredData, String meta) {
        String[] data = new String[requiredData.length];
        // 0 -> artist; 1 --> title; 2 --> album
        // searching for requiredData
        
        for (int x = 0; x < requiredData.length; x++) {
            for (int i = 0; i < meta.length() && (meta.length() - i) >= requiredData[x].length(); i++) {
                if (meta.substring(i, i + requiredData[x].length()).equalsIgnoreCase(requiredData[x])) {
                    i += requiredData[x].length();
                    String temp = "";
                    for (; meta.charAt(i) != ',' && meta.charAt(i) != '}'; i++) {
                        temp += meta.charAt(i);
                    }
                    data[x] = temp;
                    i--;
                }
            }
        }
        
        return data;
    }
    
    public void chName() {
        ObservableMap<String, Object> metadata = this.norMediaPlayer.getCurrentMedia().getMetadata();
        
        this.name.setText(metadata.toString());
    }
    
    @Override
    public void mediaChanged() {
        
        this.norMediaPlayer.getNorPlayer().setOnReady(new Runnable() {
            
            @Override
            public void run() {
                if (norMediaPlayer.isVideo()) {
                    view.setMediaPlayer(norMediaPlayer.getNorPlayer());
                }
                chName();
                
                System.out.println(norMediaPlayer.getNorPlayer().getMedia().getSource());
                norMediaPlayer.getNorPlayer().volumeProperty().bind(vol.valueProperty().divide(100.0));
                norMediaPlayer.getNorPlayer().balanceProperty().bind(balanceSlider.valueProperty().divide(100.0));
                norMediaPlayer.getNorPlayer().rateProperty().bind(speedSlider.valueProperty().divide(100.0));
                
                norMediaPlayer.getNorPlayer().currentTimeProperty().addListener((Observable observable) -> {
                    int min = (int) norMediaPlayer.getNorPlayer().getCurrentTime().toMinutes();
                    int sec = (int) norMediaPlayer.getNorPlayer().getCurrentTime().toSeconds() % 60;
                    int mili = (int) ((norMediaPlayer.getNorPlayer().getCurrentTime().toMillis() % 1000));
                    mili /= 100;
                    DecimalFormat df = new DecimalFormat("00");
                    
                    mytime.setText(df.format(min) + ':' + df.format(sec) + ':' + df.format(mili));
                });
                double dur = Double.NaN;
                
                do {
                    try {
                        dur = norMediaPlayer.getNorPlayer().getTotalDuration().toMillis();
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                } while (dur == Double.NaN);
                slide.setMax(dur);
                slide.setMin(0);
                
                InvalidationListener Ili = (Observable observable) -> {
                    
                    slide.setValue(norMediaPlayer.getNorPlayer().getCurrentTime().toMillis());
                };
                
                norMediaPlayer.getNorPlayer().currentTimeProperty().addListener(Ili);
                slide.setOnMousePressed((MouseEvent event) -> {
                    norMediaPlayer.getNorPlayer().currentTimeProperty().removeListener(Ili);
                    
                });
                
                slide.setOnMouseReleased((MouseEvent event) -> {
                    norMediaPlayer.getNorPlayer().seek(Duration.millis(slide.getValue()));
                    norMediaPlayer.getNorPlayer().currentTimeProperty().addListener(Ili);
                });
                
            }
        });
        
    }
    
    private void initSliders() {
        vol.setOrientation(Orientation.VERTICAL);
        vol.setMax(100);
        vol.setMin(0);
        vol.setValue(100);
        vol.setShowTickLabels(true);
        vol.setShowTickMarks(true);
        vol.setMajorTickUnit(25);
        vol.setMinorTickCount(5);
        vol.setSnapToTicks(true);
        balanceSlider.setMax(100);
        balanceSlider.setMin(-100);
        balanceSlider.setValue(0);
        balanceSlider.setMaxWidth(100);
        balanceSlider.setMajorTickUnit(100);
        balanceSlider.setMinorTickCount(3);
        balanceSlider.setShowTickMarks(true);
        balanceSlider.setSnapToTicks(true);
        speedSlider.setMax(150);
        speedSlider.setMin(50);
        speedSlider.setValue(100);
        speedSlider.setMajorTickUnit(50);
        speedSlider.setMinorTickCount(3);
        speedSlider.setShowTickMarks(true);
        speedSlider.setSnapToTicks(true);
        speedSlider.setMaxWidth(100);
        
    }
    
    @Override
    public void playlistChanged() {
//        new Thread(new Task() {
//
//            @Override
//            protected Object call() throws Exception {
//                initPlaylist(norMediaPlayer.getPlaylistName());
//                return true;
//            }
//
//        }).start();
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                initPlaylist(norMediaPlayer.getPlaylistName());
            }
        });
    }
}
