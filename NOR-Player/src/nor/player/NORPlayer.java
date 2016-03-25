package nor.player;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableNumberValue;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import static javafx.scene.input.KeyCode.T;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import nor.player.NORMediaPlayer.*;

/**
 *
 * @author Kacper Olszanski, Philipp Radler, Julian Nenning
 */
public class NORPlayer extends Application implements MediaChangeListener {

    Scanner sc = new Scanner(System.in);

    private Duration duration;
    MediaView view = new MediaView();
    MediaPlayer mp ;
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 700, 300);
    Slider slide = new Slider();
    Slider vol = new Slider();
    Slider balanceSlider = new Slider();
    Slider speedSlider = new Slider();
    NORMediaPlayer norMediaPlayer = new NORMediaPlayer(this);
    DataManager manager = new DataManager();
    Label name = new Label("metadata");
    Label time = new Label("00:00:00");
    

    @Override
    public void start(Stage primaryStage) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                try {
                    mp = new MediaPlayer(norMediaPlayer.createMedia(new File("NOR.wav")));
                    mp.play();
                    
                } catch (Exception e) {
                    System.err.println(e);
                }finally{
                }
            }
        });

        Button playB = new Button("Start");
        Button pauseB = new Button("Pause");
        Button stopB = new Button("Stop");
        Button addB = new Button("Add");
        Button openB = new Button("Open");

        Button nextB = new Button("Next");
        Button prevB = new Button("Prev");
        Button shuffleB = new Button("Shuffle");

        Button savePlaylistButton = new Button("savePlaylist");
        Button loadPlaylistButton = new Button("loadPlaylist");
        Label l1 = new Label("test");

        initSliders();

        addB.setOnAction((ActionEvent event) -> {

            try {
                new Thread(new Runnable() {
                    List dataList = manager.chooseMultipleFiles("all");

                    ArrayList<File> data = new ArrayList<File>(dataList);

                    @Override
                    public void run() {

                        norMediaPlayer.addMedia(data);
                        if (!norMediaPlayer.isPlaying()) {
                            norMediaPlayer.play();
                        }
                    }
                }).start();

            } catch (Exception e) {
                name.setText("ERROR");
            }
        });

        openB.setOnAction((ActionEvent event) -> {
            boolean b = norMediaPlayer.isPlaying();
           
            try {
                new Thread(new Runnable() {
                    List dataList = manager.chooseMultipleFiles("all");

                    ArrayList<File> data = new ArrayList<File>(dataList);

                    
                    
                    @Override
                    public void run() {
                   
                        norMediaPlayer.clearPlaylist();

                        

                        norMediaPlayer.addMedia(data);
                        if (!b) {
                            norMediaPlayer.play();
                        }
                    }
                }).start();

            } catch (Exception e) {
                name.setText("ERROR");
            }
        });

        playB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.play();

        });
        pauseB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.pause();
        });

        nextB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.nextClip();

        });

        prevB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.prevClip();

        });

        stopB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.stop();
        });

        savePlaylistButton.setOnAction((ActionEvent event) -> {

            File f = manager.savePlaylist();
            norMediaPlayer.savePlaylist(f.getAbsolutePath());
        });
        loadPlaylistButton.setOnAction((ActionEvent event) -> {
            File f = manager.chooseSingleFile("playlist");
            try {
                norMediaPlayer.loadPlaylist(f, true);
            } catch (IOException ex) {

            }
        });
        shuffleB.setOnAction((ActionEvent event) -> {
            norMediaPlayer.shuffle();
        });

        HBox chooseFile = new HBox();

        chooseFile.getChildren().add(addB);
        chooseFile.getChildren().add(openB);
        chooseFile.getChildren().addAll(l1);
        HBox playStop = new HBox(playB, pauseB, stopB, prevB, nextB, savePlaylistButton, loadPlaylistButton, shuffleB);

        VBox bottomB;
        bottomB = new VBox(chooseFile, playStop, slide);
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
        root.setTop(new VBox(time, name, balanceSlider, speedSlider));
        root.setCenter(view);
        root.setBottom(bp1);

        primaryStage.setTitle("NOR-Player");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                norMediaPlayer.savePlaylist("lastSession.npl");

            }
        });

        primaryStage.show();
    }

    public void chName() {
        ObservableMap<String, Object> metadata = this.norMediaPlayer.getCurrentMedia().getMetadata();

        this.name.setText(metadata.toString());
    }

    public static void main(String[] args) {
        launch(args);
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

                    time.setText(df.format(min) + ':' + df.format(sec) + ':' + df.format(mili));
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

}
