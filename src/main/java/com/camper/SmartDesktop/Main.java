package com.camper.SmartDesktop;



import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.crypto.Data;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.ResourceBundle;

public class Main extends Application implements Initializable
{
    public static void main(String[] args) { launch(args); }

    @FXML private ImageView imageViewer;
    @FXML private MediaView videoViewer;
    @FXML private Button imageFileChooserButton;
    @FXML private Button videoFileChooserButton;
    private static final int DEFAULT_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static final int DEFAULT_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    private static final ClassLoader LOADER = Main.class.getClassLoader();
    private static Stage Stage;

    @Override
    public void start(Stage stage) throws IOException
    {
        Parent root = FXMLLoader.load(Objects.requireNonNull(LOADER.getResource("FXMLs/StartScreenRu.fxml")));
        var scene = new Scene(root,DEFAULT_WIDTH,DEFAULT_HEIGHT-66);
        Stage = stage;
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
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setOnEndOfMedia(()->
                {
                    mediaPlayer.stop();
                    videoViewer.setMediaPlayer(mediaPlayer);
                    mediaPlayer.play();
                });

        imageFileChooserButton.setOnAction(event ->
        {
            var fileChooser = new FileChooser();
            var imageFilters = new FileChooser.ExtensionFilter("image filters", "*.jpg","*.png","*.gif");
            fileChooser.getExtensionFilters().add(imageFilters);
            var result = fileChooser.showOpenDialog(Stage);
            try
            {
                if(result.getName().contains(".jpg"))
                {
                    //Files.copy(Paths.get("file:/"+result.getPath()), Paths.get("VideosAndImages/image.jpg"));
                    if (mediaPlayer.isAutoPlay())
                    {
                        mediaPlayer.dispose();
                    }
                    //var image = new Image("file:/" + result.getAbsolutePath());
                    Files.copy(Paths.get(result.getAbsolutePath()),Paths.get("resources/VideosAndImages/image.jpg"), StandardCopyOption.REPLACE_EXISTING);
                    imageViewer.setImage(new Image("file:/"+"./../../../image.jpg"));
                }





                if(result.getName().contains(".png"))
                {
                    Files.copy(new DataInputStream(new FileInputStream(result)), Paths.get("VideosAndImages/image.png"));
                    if (mediaPlayer.isAutoPlay())
                    {
                        mediaPlayer.stop();
                    }
                    imageViewer.setImage(new Image(getClass().getResourceAsStream("VideosAndImages/image.png")));
                }
                if(result.getName().contains(".gif"))
                {
                    Files.copy(new DataInputStream(new FileInputStream(result)), Paths.get("VideosAndImages/image.gif"));
                    if (mediaPlayer.isAutoPlay())
                    {
                        mediaPlayer.stop();
                    }
                    imageViewer.setImage(new Image(getClass().getResourceAsStream("VideosAndImages/image.gif")));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });
    }
}