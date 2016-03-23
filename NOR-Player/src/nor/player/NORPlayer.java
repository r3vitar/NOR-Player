package nor.player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.value.ObservableNumberValue;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

/**
 *
 * @author Kacper Olszanski, Philipp Radler, Julian Nenning
 */
public class NORPlayer extends Application {

    Scanner sc = new Scanner(System.in);

    MediaView view;
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 300, 250);
    Slider slide = new Slider();
    Playlist playlist = new Playlist();
    DataManager manager = new DataManager();
    Label name = new Label("name");

    @Override
    public void start(Stage primaryStage) {
        Button startB = new Button("Start");
        Button pauseB = new Button("Pause");
        Button stopB = new Button("Stop");
        Button selectB = new Button("Add");
        Button nextB = new Button("Next");
        Button prevB = new Button("Prev");

        Button savePlaylistButton = new Button("savePlaylist");
        Button loadPlaylistButton = new Button("loadPlaylist");
        Label l1 = new Label("test");

        Label sFile = new Label("ERROR");

        view = new MediaView();

        selectB.setOnAction((ActionEvent event) -> {
            try {
//                    media = new Media(tf.getText());
//                    player = new MediaPlayer(media);
//
//                    view.setMediaPlayer(player);
//                    view.fitHeightProperty().bind(scene.heightProperty());
//                    view.fitWidthProperty().bind(scene.widthProperty());
//                    
//                    slide = new Slider(0, media.getDuration().toSeconds(), 0);
//                    slide.valueProperty().bind((ObservableNumberValue) player.currentTimeProperty().getValue());
//                    slide.onDragDroppedProperty();

                File data = manager.chooseSingleFile();
                l1.setText(data.getPath());
                playlist.addMedia(data);

            } catch (Exception e) {
                sFile.setText("ERROR");
            }
        });

        startB.setOnAction((ActionEvent event) -> {
            playlist.playCurrent();
            chName();
        });
        pauseB.setOnAction((ActionEvent event) -> {
            playlist.pauseCurrent();
        });

        nextB.setOnAction((ActionEvent event) -> {
            playlist.nextClip();
            chName();
        });

        prevB.setOnAction((ActionEvent event) -> {
            playlist.prevClip();
            chName();
        });

        stopB.setOnAction((ActionEvent event) -> {
            playlist.stopCurrent();
        });

        savePlaylistButton.setOnAction((ActionEvent event) -> {
            System.out.print("Name: ");
            playlist.savePlaylist(sc.nextLine());
        });
        loadPlaylistButton.setOnAction((ActionEvent event) -> {
            System.out.print("Name: ");
            try {
                playlist.loadPlaylist(sc.nextLine());
            } catch (IOException ex) {
                Logger.getLogger(NORPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        root.setCenter(view);

        HBox chooseFile = new HBox();

        chooseFile.getChildren().add(selectB);
        chooseFile.getChildren().addAll(l1);
        HBox playStop = new HBox(startB, pauseB, stopB, prevB, nextB, savePlaylistButton, loadPlaylistButton);

        VBox bottomB = new VBox(chooseFile, playStop, slide);
        root.setTop(name);
        root.setBottom(bottomB);

        primaryStage.setTitle("NOR-Player");
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public void chName() {
        ObservableMap<String, Object> metadata = this.playlist.getCurrentMedia().getMetadata();

        this.name.setText(metadata.toString());
    }

    public static void main(String[] args) {
        launch(args);
    }

}
