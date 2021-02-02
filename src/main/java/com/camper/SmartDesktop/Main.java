package com.camper.SmartDesktop;



import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application implements Initializable
{
    public static void main(String[] args) { launch(args); }

    @FXML private MediaView videoViewer;
    private static final int DEFAULT_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static final int DEFAULT_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    private ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void start(Stage stage) throws IOException
    {
        var loader = Main.class.getClassLoader();
        Parent root = FXMLLoader.load(Objects.requireNonNull(loader.getResource("FXMLs/StartScreen.fxml")));
        var scene = new Scene(root,DEFAULT_WIDTH,DEFAULT_HEIGHT-66);
        stage.setScene(scene);
        stage.setTitle("SmartDesktop");
        stage.show();


    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        String VUrl = "file:/E:/Programming/IdeaProjects/SmartDesktop/src/main/resources/test.mp4";
        var media = new Media(VUrl);
        var mediaPlayer = new MediaPlayer(media);
        videoViewer.setFitHeight(DEFAULT_HEIGHT);
        videoViewer.setFitWidth(DEFAULT_WIDTH);
        videoViewer.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
        mediaPlayer.setOnEndOfMedia(()->
                {
                    mediaPlayer.stop();
                    videoViewer.setMediaPlayer(mediaPlayer);
                    mediaPlayer.play();
                });

    }
}