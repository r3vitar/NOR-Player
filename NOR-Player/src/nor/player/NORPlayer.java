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
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
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
    boolean listenerSet = false;
 Scene playlistScene;
    private Duration duration;
    MediaView view = new MediaView();
    MediaPlayer mp;
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 400, 200);
    Slider slide = new Slider();
    Slider vol = new Slider();
    Slider balanceSlider = new Slider();
    Slider speedSlider = new Slider();

    MenuItem addB = new MenuItem("Add Media"),
            addAndPlayB = new MenuItem("Add & Play"),
            addsB = new MenuItem("Add More Media"),
            delB = new MenuItem("Delete Current Media"),
            addLink = new MenuItem("Open Media From URL"),
            loadPlaylistButton = new MenuItem("Load Playlist"),
            savePlaylistButton = new MenuItem("Save Playlist"),
            clearB = new MenuItem("Clear Playlist"),
            shuffleB = new MenuItem("Shuffle"),
            sortAscB = new MenuItem("Sort ABC"),
            sortDescB = new MenuItem("Sort ZYX");

    Menu fileMenu = new Menu("File", null, addB, addAndPlayB, addsB, addLink, delB);
    Menu playlistMenu = new Menu("Playlist", null, loadPlaylistButton, savePlaylistButton, clearB);
    Menu sortMenu = new Menu("Sort", null, shuffleB, sortAscB, sortDescB);
    MenuBar playlistMenuBar = new MenuBar(fileMenu, playlistMenu, sortMenu);

    Button playB = new Button();
    Button pauseB = new Button();
    Button stopB = new Button();
    //Button addB = new Button("Add");
    Button openB = new Button();
    //Button clearB = new Button("Clear");

    Button nextB = new Button();
    Button prevB = new Button();
   // Button shuffleB = new Button("Shuffle");

    //Button savePlaylistButton = new Button("savePlaylist");
    // Button loadPlaylistButton = new Button("loadPlaylist");
    //Label l1 = new Label("test");

    Button playlistStageB = new Button("Playlist");
    NORMediaPlayer norMediaPlayer = new NORMediaPlayer(this);
    DataManager manager = new DataManager();
    Label name = new Label("metadata");
    Label mytime = new Label("00:00:00");
    Stage playlistStage = new Stage();
    String[] requiredData = {"artist=", "title=", "album="};
    TableView playlistTable = new TableView();
    Stage primaryStage;

    ObservableList<LineItem> playlistData = FXCollections.observableArrayList();

    boolean playInit = false;

    
    
    //Tests für audio per link abspielen
    Button linkB = new Button("playByLink");
    TextField linkTf = new TextField();
    
    
    
    @Override
    public void start(Stage ps) {
        try{
        
        primaryStage = ps;
        primaryStage.setResizable(false);
        new Thread(new Task() {
            @Override
            protected Object call() {
                try {

                    mp = new MediaPlayer(norMediaPlayer.createMedia(new File("NOR.wav")));

                    mp.play();

                } catch (Exception e) {
                    System.err.println(e);
                    return false;
                } finally {
                }

                try {
                    initListener();
                } catch (Exception e) {

                }
                try {
                    initButtons();
                } catch (Exception e) {
                }
                try {
                    initSliders();
                } catch (Exception e) {
                }

                return true;
            }

        }).start();

        mytime.setId("font");

        //HBox chooseFile = new HBox();

        //chooseFile.getChildren().add(openB);
        //chooseFile.getChildren().addAll(l1);
        HBox playStop = new HBox(playB, pauseB, stopB, prevB, nextB, openB);

        VBox bottomB;
        HBox linkBox = new HBox(linkB, linkTf);
        bottomB = new VBox(playStop, slide, playlistStageB, linkBox);
        BorderPane bp1 = new BorderPane(bottomB);

        File lastSession = new File("lastSession.npl");
        if (lastSession.exists()) {
            new Thread(new Task() {

                @Override
                protected Object call() throws Exception {

                    try {
                        norMediaPlayer.loadPlaylist(lastSession, true);
                        playlistChanged();
                    } catch (IOException ex) {
                        return false;
                    }
                    return true;
                }
            }).start();

        }

        bp1.setRight(vol);
        root.setTop(new VBox(mytime, name, balanceSlider, speedSlider));
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
                Platform.exit();
            }
        });

        primaryStage.show();
        }catch(Exception e){
            new File("lastSession.npl").delete();
            System.err.println(e);
            System.err.println("lastSession deleted!");
        }
    }

    private void showActivePlaylist() {
        if (!this.playInit) {
            this.initPlaylist(this.norMediaPlayer.getPlaylistName());
        }
        if (playlistStage.isShowing()) {
            playlistStage.hide();
        } else {

            playlistStage.show();
        }
    }

    private void initPlaylistTable() {
        playlistTable = new TableView();
        playlistTable.setPrefWidth(playlistScene.getWidth());

        playlistTable.setPrefHeight(playlistScene.getHeight()-playlistMenuBar.getHeight());
        playlistTable.setMaxHeight(playlistScene.getHeight()-playlistMenuBar.getHeight());
        
        playlistScene.widthProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                
                playlistTable.setPrefWidth(playlistScene.getWidth());
            }
        });
        playlistScene.heightProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                
                playlistTable.setMaxHeight(Double.MAX_VALUE);


                
                playlistTable.setPrefHeight(playlistScene.getHeight()-playlistMenuBar.getHeight());
            }
        });
        playlistTable.setPrefWidth(playlistScene.getWidth());
        playlistTable.setPrefHeight(playlistScene.getHeight()-25.0);
        playlistTable.setMaxHeight(playlistScene.getHeight());

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
        playlistTable.setItems(playlistData);
    }

    private void initPlaylist(String playlistTitle) {
         Pane root = new Pane();
         
         playlistScene= new Scene(root, 750,500);
        
        // Init the PlaylistTable
        initPlaylistTable();

       
        BorderPane bp = new BorderPane();
        
        bp.setBottom(playlistMenuBar);
        bp.setCenter(playlistTable);
        root.getChildren().add(bp);
        
        
       
        
        playlistStage.setScene(playlistScene);
        playlistStage.setTitle(playlistTitle);
       // playlistStage.setResizable(false);
        
        
        
        playlistScene.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.ANY);
                } else {
                    event.consume();
                }
            }
        });
        
        // Dropping over surface
        playlistScene.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    
                    ArrayList<File> data = new ArrayList<File>();
                    for (File file:db.getFiles()) {
                        data.add(file);
                        
                    }
                    
                    if (data != null && !data.isEmpty()) {
                            norMediaPlayer.addMedia(data);

                        }
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
        
        
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
        
        this.playInit = true;
    }

    private String[] readMetadata(String[] requiredData, Media m) {
        String meta = m.getMetadata().toString();
        String[] data = new String[requiredData.length];
        // 0 -> artist; 1 --> title; 2 --> album
        // searching for requiredData
        File f = new File(m.getSource());
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

        if (data[1] == null && data[0] == null) {
            if (f.getName().contains("-")) {
                data[0] = f.getName().split("-")[0].replace("%20", " ");
                data[1] = f.getName().split("-")[1].replace("%20", " ");
            } else {
                data[1] = f.getName().replace("%20", " ");
                data[0] = "";
            }

        } else if (data[1] == null) {
            if (f.getName().contains("-")) {
                data[1] = f.getName().split("-")[1].replace("%20", " ");
            } else {
                data[1] = f.getName().replace("%20", " ");

            }

        } else if (data[0] == null) {
            if (f.getName().contains("-")) {
                data[0] = f.getName().split("-")[0].replace("%20", " ");
            } else {
                data[0] = f.getName().replace("%20", " ");

            }
        }

        data[1] = data[1].trim();

        return data;
    }

    public void chName() {

        String[] requiredDataName = {"artist=", "title="};

        String[] data = readMetadata(requiredDataName, this.norMediaPlayer.getCurrentMedia());
        String s = String.format("%s - %s", data[0], data[1]).replace("null - ", "").replace("null", "");
        this.name.setText(s);
        primaryStage.setTitle(s);
    }

    @Override
    public void mediaChanged() {

        this.norMediaPlayer.getNorPlayer().setOnReady(new Task() {

            @Override
            protected Object call() throws Exception {
                try {
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

                    initListener();
                } catch (Exception e) {
                    return false;
                }
                return true;
            }
        });

    }

    private void initSliders() {
        vol.setOrientation(Orientation.VERTICAL);
        vol.setMax(100);
        vol.setMin(0);
        vol.setValue(100);
        vol.setMaxHeight(100);
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

    }

    @Override
    public void playlistChanged() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                playlistData = FXCollections.observableArrayList();
                ArrayList<Media> playlistMedia = norMediaPlayer.getPlaylist();
                for (int i = 0; i < playlistMedia.size(); i++) {
                    String[] temp = readMetadata(requiredData, playlistMedia.get(i));
                    LineItem li = new LineItem(i + 1, temp[1], temp[0], temp[2]);
                    playlistData.add(li);
                }
                playlistTable.setItems(playlistData);

            }
        });

    }

    private void initButtons() {
        
       playB.setMinSize(35, 35);
        playB.setId("playButton");
        playB.setTooltip(new Tooltip("Play"));
        pauseB.setMinSize(35, 35);
        pauseB.setId("pauseButton2");
        pauseB.setTooltip(new Tooltip("Pause"));
        nextB.setMinSize(35, 35);
        nextB.setId("nextButton");
        nextB.setTooltip(new Tooltip("Next"));
        prevB.setMinSize(35, 35);
        prevB.setId("prevButton");
        prevB.setTooltip(new Tooltip("Preview"));
        stopB.setMinSize(35, 35);
        stopB.setId("stopButton");
        stopB.setTooltip(new Tooltip("Stop"));
        openB.setMinSize(35, 35);
        openB.setId("loadButton");
        
        //Audio by link
        linkB.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                norMediaPlayer.addMedia(norMediaPlayer.createMediaByLink(linkTf.getText()));
            }
        });
        
         delB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.deleteMedia(norMediaPlayer.getPlayIndex());
        });
        
        playlistStageB.setOnAction((ActionEvent event) -> {
            showActivePlaylist();
        });

        addAndPlayB.setOnAction((ActionEvent event) -> {

            try {

                File f = manager.chooseSingleFile("media");

                if (f != null) {

                    norMediaPlayer.addMedia(f);
                    norMediaPlayer.setPlayIndexToLast();

                    norMediaPlayer.play();

                }

            } catch (Exception e) {
                System.err.println(e);
            }
        });

        addB.setOnAction((ActionEvent event) -> {
            boolean b = norMediaPlayer.isPlaying();

            try {

                File f = manager.chooseSingleFile("media");

                if (f != null) {

                    //norMediaPlayer.clearPlaylist();

                    norMediaPlayer.addMedia(f);
//                    if (b) {
//                        norMediaPlayer.play();
//                        //pauseB.setText("Pause");
//                    } else {
//                        //pauseB.setText("Play");
//                    }
                }

            } catch (Exception e) {
                System.err.println(e);
            }
        });

        addsB.setOnAction((ActionEvent event) -> {

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
                    //pauseB.setText("Pause");
                } else {
                    // pauseB.setText("Play");
                }

            } catch (Exception e) {
                System.err.println(e);
            }
        });
        
        addLink.setOnAction((ActionEvent event) -> {
            throw new UnsupportedOperationException();
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
                        //pauseB.setText("Pause");
                    } else {
                        //pauseB.setText("Play");
                    }
                }

            } catch (Exception e) {
                System.err.println(e);
            }
        });

        playB.setOnAction((ActionEvent event) -> {

            norMediaPlayer.play();

        });
        pauseB.setOnAction((ActionEvent event) -> {

            norMediaPlayer.playOrPause();
        });

        nextB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.nextClip();

        });

        prevB.setOnAction((ActionEvent event) -> {
            
            
                Duration currDur = norMediaPlayer.getNorPlayer().getCurrentTime();
                
            
                if(currDur.toSeconds() > 5 ){
                    norMediaPlayer.stop();
                    norMediaPlayer.play();
                }else{
            
                norMediaPlayer.prevClip();
                }

        });

        stopB.setOnAction((ActionEvent event) -> {

            norMediaPlayer.stop();

            //pauseB.setText("Play");
        });

        savePlaylistButton.setOnAction((ActionEvent event) -> {

            File f = manager.savePlaylist();
            norMediaPlayer.savePlaylist(f.getAbsolutePath());
        });
        loadPlaylistButton.setOnAction((ActionEvent event) -> {
            File f = manager.chooseSingleFile("playlist");
            Task worker = new Task() {

                @Override
                protected Object call() throws Exception {

                    try {

                        norMediaPlayer.loadPlaylist(f, true);

                    } catch (Exception ex) {
                        return false;
                    }
                    return true;
                }
            };

            new Thread(worker).start();

        });
        shuffleB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.shuffle();
        });
        sortAscB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.sortByNameAsc();
        });
        
        sortDescB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.sortByPathDesc();
        });
        
        clearB.setOnAction((ActionEvent event) -> {
            // pauseB.setText("Play");
            norMediaPlayer.stop();
            norMediaPlayer.clearPlaylist();
        });
    }

    private void initListener() {
        new Thread(new Task() {

            @Override
            protected Object call() throws Exception {
                norMediaPlayer.getNorPlayer().setOnPlaying(new Runnable() {

                    @Override
                    public void run() {

                        try {
                            pauseB.setId("pauseButton1");
                        } catch (Exception e) {
                        }

                    }
                });

                norMediaPlayer.getNorPlayer().setOnStopped(new Runnable() {

                    @Override
                    public void run() {

                        try {
                            pauseB.setId("pauseButton2");
                        } catch (Exception e) {
                        }

                    }
                });
                norMediaPlayer.getNorPlayer().setOnPaused(new Runnable() {

                    @Override
                    public void run() {

                        try {
                            pauseB.setId("pauseButton2");
                        } catch (Exception e) {
                        }

                    }
                });

                listenerSet = true;

                return true;
            }
        }
        ).start();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
