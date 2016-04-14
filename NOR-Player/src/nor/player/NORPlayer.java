package nor.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
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
import javafx.scene.image.Image;
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
 * Dies ist die MainKlasse.
 *
 * @author Kacper Olszanski, Philipp Radler, Julian Nenning
 */
public class NORPlayer extends Application implements MediaChangeListener {

    MediaChangeListener listener = this;

    StringProperty displayName = new SimpleStringProperty("metadata");
    StringProperty displayTitle = new SimpleStringProperty("NOR Player");
    boolean interruptT = false;
    Thread ls;
    private int refreshRate = 750;
    private Scanner sc = new Scanner(System.in);
    private static final String settingPath = "settings.norc";
    private boolean listenerSet = false;
    private Scene playlistScene;
    private Duration duration;
    private MediaView view = new MediaView();
    private MediaPlayer mp;
    private BorderPane root = new BorderPane();
    private Scene scene = new Scene(root, 350, 175);

    private Slider slide = new Slider();
    private Slider vol = new Slider();
    private Slider balanceSlider = new Slider();
    private Slider speedSlider = new Slider();

    private MenuItem addB = new MenuItem("Add Media"),
            addAndPlayB = new MenuItem("Add & Play"),
            addsB = new MenuItem("Add More Media"),
            delB = new MenuItem("Delete Current Media"),
            addLink = new MenuItem("Open Media From URL"),
            loadPlaylistButton = new MenuItem("Load Playlist"),
            savePlaylistButton = new MenuItem("Save Playlist"),
            clearB = new MenuItem("Clear Playlist"),
            shuffleB = new MenuItem("Shuffle"),
            sortFileAscB = new MenuItem("FileName asc"),
            sortFileDescB = new MenuItem("FileName desc"),
            sortTitleAscB = new MenuItem("Title asc"),
            sortTitleDescB = new MenuItem("Title desc"),
            sortArtistAscB = new MenuItem("Artist asc"),
            sortArtistDescB = new MenuItem("Artist desc"),
            sortAlbumAscB = new MenuItem("Album asc"),
            sortAlbumDescB = new MenuItem("Album desc");

    private Menu fileMenu = new Menu("File", null, addB, addAndPlayB, addsB, addLink, delB);
    private Menu playlistMenu = new Menu("Playlist", null, loadPlaylistButton, savePlaylistButton, clearB);
    private Menu sortMenu = new Menu("Sort", null, shuffleB, sortFileAscB, sortFileDescB, sortTitleAscB, sortTitleDescB, sortArtistAscB, sortArtistDescB, sortAlbumAscB, sortAlbumDescB);
    private MenuBar playlistMenuBar = new MenuBar(fileMenu, playlistMenu, sortMenu);

    private Button playB = new Button();
    private Button pauseB = new Button();
    private Button stopB = new Button();
    //Button addB = new Button("Add");
    private Button openB = new Button();
    //Button clearB = new Button("Clear");

    private Button nextB = new Button();
    private Button prevB = new Button();
   // Button shuffleB = new Button("Shuffle");

    //Button savePlaylistButton = new Button("savePlaylist");
    // Button loadPlaylistButton = new Button("loadPlaylist");
    //Label l1 = new Label("test");
    private Button playlistStageB = new Button();
    private NORMediaPlayer norMediaPlayer = new NORMediaPlayer(this);
    private DataManager manager = new DataManager();
    private Label name = new Label(displayName.getValue());
    private Label mytime = new Label("00:00:00");
    private Stage playlistStage = new Stage();
    private String[] requiredData = {"artist=", "title=", "album="};
    private TableView playlistTable = new TableView();
    private Stage primaryStage;

    private ObservableList<LineItem> playlistData = FXCollections.observableArrayList();

    private boolean playInit = false;

    //Tests für audio per link abspielen
    private Button linkB = new Button("playByLink");
    private TextField linkTf = new TextField();

    /**
     * Dies ist die Start-Methode die immer am Anfang des Programmes ausgeführt
     * wird.
     *
     * @param ps Name der Stage.
     */
    @Override
    public void start(Stage ps) {
        try {

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

                    } catch (Exception e) {
                    }

                    return true;
                }

            }).start();
            initSliders();

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

            if (new File(settingPath).exists()) {
                new Thread(new Task() {

                    @Override
                    protected Object call() throws Exception {

                        try {
                            loadSettings();
                        } catch (Exception e) {
                            return false;
                        }
                        return true;
                    }
                }).start();

            }

            mytime.setId("font");

            //HBox chooseFile = new HBox();
            //chooseFile.getChildren().add(openB);
            //chooseFile.getChildren().addAll(l1);
            HBox playStop = new HBox(prevB, pauseB, stopB, nextB, openB, playlistStageB);
            
            VBox bottomB;
            //HBox linkBox = new HBox(linkB, linkTf);
            bottomB = new VBox(slide, playStop);
            BorderPane bp1 = new BorderPane();

            name.setId("name");
            mytime.setId("time");
            
            name.setMaxWidth((scene.getWidth() / 10 * 7));
            bp1.setBottom(bottomB);
            bp1.setRight(vol);
            bp1.setTranslateY(-55);
            bp1.setId("pane");

            root.setCenter(view);
            root.setBottom(bp1);
            VBox sliderBox = new VBox(balanceSlider, speedSlider);
            speedSlider.setTranslateY(5);
            sliderBox.setId("slider1");
            sliderBox.setTranslateX(-10);
            sliderBox.translateYProperty().bind(sliderBox.translateXProperty().negate());
            VBox displayBox = new VBox(mytime, name);
            
            displayBox.setTranslateX(17);
            displayBox.setTranslateY(7);
             BorderPane topPane = new BorderPane(null, null,sliderBox , null, displayBox);
            root.setTop(topPane);

            primaryStage.setTitle(displayTitle.toString());
            scene.getStylesheets().add("resources/styles.css");
            primaryStage.getIcons().add(new Image("resources/nor.png"));
            root.setId("bg");
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

                @Override
                public void handle(WindowEvent event) {
                    interruptT = true;
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException ex) {
                        //Logger.getLogger(NORPlayer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        saveSettings();
                    } catch (Exception e) {
                        System.err.println(e);
                    }
                    if (!norMediaPlayer.isEmpty()) {
                        norMediaPlayer.savePlaylist("lastSession.npl");
                    }
                    playlistStage.close();
                    Platform.exit();
                }
            });
            

            primaryStage.show();
        } catch (Exception e) {
            new File("lastSession.npl").delete();
            System.err.println(e);
            System.err.println("lastSession deleted!");
        }
    }

    /**
     * Diese Methode bewirkt, dass die Playlist in einem neuen Fenster geöffnet
     * wird.
     */
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

    /**
     * Hier wird die Playlist in ein TableView gegeben, welches später angezeigt
     * werden kann.
     */
    private void initPlaylistTable() {
        playlistTable = new TableView();

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

                playlistTable.setPrefHeight(playlistScene.getHeight() - playlistMenuBar.getHeight());
            }
        });
        playlistTable.setPrefWidth(playlistScene.getWidth());
        playlistTable.setPrefHeight(playlistScene.getHeight() - 25.0);
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

    /**
     * Hier wird die Playlist
     *
     * @param playlistTitle Name der Playlist.
     */
    private void initPlaylist(String playlistTitle) {
        Pane root = new Pane();

        playlistScene = new Scene(root, 750, 500);
        playlistScene.getStylesheets().add("resources/styles.css");

        // Init the PlaylistTable
        initPlaylistTable();

        BorderPane bp = new BorderPane();

        bp.setBottom(playlistMenuBar);
        bp.setCenter(playlistTable);
        root.getChildren().add(bp);
        
        playlistStage.getIcons().add(primaryStage.getIcons().get(0));
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
                    for (File file : db.getFiles()) {
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

    public void chName() {
        //interruptT = true;
        name.textProperty().unbind();
        
        String[] requiredDataName = {"artist=", "title="};

        String[] data = NORMediaPlayer.readMetadata(requiredDataName, this.norMediaPlayer.getCurrentMedia());
        displayName.setValue(String.format("%s - %s", data[0], data[1]).replace("null - ", "").replace("null", ""));
        if (displayName.getValue().length() > 25) {
            displayName.setValue(String.format("%s - %s --- ", data[0], data[1]).replace("null - ", "").replace("null", ""));
        } 
        this.name.setText(displayName.getValue());
        primaryStage.setTitle(data[1]);

        ls = new Thread(new Task() {

            @Override
            protected Object call() throws Exception { 
                try {
                    Thread.sleep(2000);
                    interruptT = false;
                    
                    name.setWrapText(false);
                    name.setEllipsisString("");
                    
                    
                    
                    for (; !interruptT; ) {
                        
                        Platform.runLater(new Task() {

                        @Override
                        protected Object call() throws Exception {

                            try {
                                    String tmp = "";
                                    for (int i = 1; i < displayName.getValue().length() ; i++) {
                                        tmp += displayName.getValue().charAt(i);

                                    }
                                    tmp += Character.toString(displayName.getValue().charAt(0));

                                    listener.changeText(tmp);

                                   
                                } catch (Exception ee) {
                                    System.err.println(ee);
                                    return false;
                                }
                            return true;
                        
                        }
                    });
                        
                        for(int i = 0; i < refreshRate/2 && !interruptT ; i++){
                            Thread.sleep(2);
                            if(interruptT)
                                return true;
                            
                        }
                        
                    }
                    interruptT = false;

                } catch (Exception e) {
                    return false;
                }

                return true;
            }
        });
         if (displayName.getValue().length() > 25) {
        ls.setDaemon(true);
        name.textProperty().bind(displayName);
        
         }

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
                    
                                interruptT = true;
                                Thread.sleep(5);
                                interruptT = false;
                                chName();
                                ls.start();
          

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
        vol.setTranslateY(56);
        
        vol.setTooltip(new Tooltip("Volume"));
        
        balanceSlider.setMax(100);
        balanceSlider.setMin(-100);
        balanceSlider.setValue(0);
        balanceSlider.setMaxWidth(100);
        balanceSlider.setMajorTickUnit(100);
        balanceSlider.setMinorTickCount(3);
        balanceSlider.setShowTickMarks(true);
        balanceSlider.setSnapToTicks(true);
        balanceSlider.setTooltip(new Tooltip("Balance"));
      
        speedSlider.setMax(150);
        speedSlider.setMin(50);
        speedSlider.setValue(100);
        speedSlider.setMajorTickUnit(50);
        speedSlider.setMinorTickCount(3);
        speedSlider.setShowTickMarks(true);
        speedSlider.setSnapToTicks(true);
        speedSlider.setMaxWidth(100);
        speedSlider.setTooltip(new Tooltip("SPEED!"));
        
        slide.setMaxWidth(scene.getWidth()/20 *18);

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
                    String[] temp = NORMediaPlayer.readMetadata(requiredData, playlistMedia.get(i));
                    LineItem li = new LineItem(i + 1, temp[1], temp[0], temp[2]);
                    playlistData.add(li);
                }
                playlistTable.setItems(playlistData);

            }
        });

    }

    private void initButtons() {

        pauseB.setMinSize(35, 35);
        pauseB.setId("playButton");
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
        openB.setTooltip(new Tooltip("Open"));
        playlistStageB.setMinSize(35, 35);
        playlistStageB.setId("playlistButton");
        playlistStageB.setTooltip(new Tooltip("Playlist"));

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

                File f = manager.chooseSingleFile("media");

                if (f != null) {
                    if (!norMediaPlayer.isEmpty()) {
                        norMediaPlayer.stop();
                        norMediaPlayer.clearPlaylist();
                    }

                    norMediaPlayer.addMedia(f);
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

            if (currDur.toSeconds() > 5) {
                norMediaPlayer.stop();
                norMediaPlayer.play();
            } else {

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
        sortFileAscB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.sortByNameAsc();
        });

        sortFileDescB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.sortByPathDesc();
        });
        sortTitleAscB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.sortByTitleAsc();
        });

        sortTitleDescB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.sortByTitleDesc();
        });
        sortArtistAscB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.sortByArtistAsc();
        });

        sortArtistDescB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.sortByArtistDesc();
        });
        sortAlbumAscB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.sortByAlbumAsc();
        });

        sortAlbumDescB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.sortByAlbumDesc();
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
                            pauseB.setId("pauseButton");
                        } catch (Exception e) {
                        }

                    }
                });

                norMediaPlayer.getNorPlayer().setOnStopped(new Runnable() {

                    @Override
                    public void run() {

                        try {
                            pauseB.setId("playButton");
                        } catch (Exception e) {
                        }

                    }
                });
                norMediaPlayer.getNorPlayer().setOnPaused(new Runnable() {

                    @Override
                    public void run() {

                        try {
                            pauseB.setId("playButton");
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

    private void loadSettings() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(settingPath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            HashMap<String, Double> input = (HashMap<String, Double>) ois.readObject();
            this.balanceSlider.setValue(input.get("balance"));
            this.vol.setValue(input.get("volume"));
            this.speedSlider.setValue(input.get("speed"));
            this.primaryStage.setX(input.get("x"));
            this.primaryStage.setY(input.get("y"));
            if (input.get("pOpen") > 0) {
                showActivePlaylist();
                this.playlistStage.setX(input.get("pX"));
                this.playlistStage.setY(input.get("pY"));
            }

        } catch (IOException e) {
            System.err.println(e);

        } catch (ClassNotFoundException ec) {
            System.err.println(ec);
        } finally {
            try {
                fis.close();
            } catch (Exception ee) {
                System.err.println(ee);

            }
        }
    }

    private void saveSettings() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(settingPath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            HashMap<String, Double> output = new HashMap<String, Double>();

            output.put("balance", this.balanceSlider.getValue());
            output.put("volume", this.vol.getValue());
            output.put("speed", this.speedSlider.getValue());
            output.put("x", this.primaryStage.getX());
            output.put("y", this.primaryStage.getY());
            output.put("pX", this.playlistStage.getX());
            output.put("pY", this.playlistStage.getY());
            output.put("pOpen", this.playlistStage.isShowing() ? -1.0 : 1.0);

            oos.writeObject(output);

            oos.flush();

        } catch (IOException e) {
            System.err.println(e);

        } finally {
            try {
                fos.close();
            } catch (Exception ee) {
                System.out.println(ee);

            }

        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void changeText(String s) {
        this.displayName.setValue(s);
    }

}
