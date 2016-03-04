package nor.player;

import javafx.application.Application;
import javafx.beans.value.ObservableNumberValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
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
    
     Media media;
    MediaPlayer player;
    MediaView view;
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 300, 250);
    Slider slide = new Slider();
    

    @Override
    public void start(Stage primaryStage) {
        Button startB = new Button("Start");
        Button pauseB = new Button("Stop");
        Button selectB = new Button("Load");
        

        Label sFile = new Label("ERROR");
        TextField tf = new TextField();

        view = new MediaView();

        selectB.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    media = new Media(tf.getText());
                    player = new MediaPlayer(media);

                    view.setMediaPlayer(player);
                    view.fitHeightProperty().bind(scene.heightProperty());
                    view.fitWidthProperty().bind(scene.widthProperty());
                    
                    slide = new Slider(0, media.getDuration().toSeconds(), 0);
                    slide.valueProperty().bind((ObservableNumberValue) player.currentTimeProperty().getValue());
                    slide.onDragDroppedProperty();
                    
                    
                } catch (Exception e) {
                    sFile.setText("ERROR");
                }

            }
        });

        startB.setOnAction(new EventHandler<ActionEvent>() {

            @Override

            public void handle(ActionEvent event) {
                if (startB.getText().contains("Start")) {
                    player.play();
                    startB.setText("Pause");
                } else if (startB.getText().contains("Pause")) {
                    player.pause();
                    startB.setText("Start!");
                }
            }
        });
        pauseB.setOnAction(new EventHandler<ActionEvent>() {

            @Override

            public void handle(ActionEvent event) {
                player.stop();
            }
        });

        root.setCenter(view);

        HBox chooseFile = new HBox();

        chooseFile.getChildren().add(selectB);
        chooseFile.getChildren().add(tf);
        HBox playStop = new HBox(startB, pauseB);

        VBox bottomB = new VBox(chooseFile, playStop, slide);

        root.setTop(bottomB);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
