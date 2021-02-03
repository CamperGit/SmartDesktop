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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.naming.Context;
import javax.print.attribute.AttributeSet;
import java.awt.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private static final ClassLoader LOADER = Main.class.getClassLoader();
    private static MediaPlayer mediaPlayer;
    public static final int DEFAULT_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    public static final int DEFAULT_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    public static Stage Stage;
    public static final String DIRPATH = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();



    @Override
    public void start(Stage stage) throws IOException
    {
        Parent root = FXMLLoader.load(Objects.requireNonNull(LOADER.getResource("FXMLs/StartScreenRu.fxml")));
        var scene = new Scene(root,DEFAULT_WIDTH,DEFAULT_HEIGHT-66);
        if (!(Files.isDirectory(Paths.get(DIRPATH+"\\Resources"))&&Files.exists(Paths.get(DIRPATH+"\\Resources"))))
        { Files.createDirectory(Paths.get(DIRPATH+"\\Resources")); }
        Stage = stage;
        stage.setScene(scene);
        stage.setTitle("SmartDesktop");
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        imageViewer.setFitWidth(DEFAULT_WIDTH);
        imageViewer.setFitHeight(DEFAULT_HEIGHT);
        String extensionOnReloading=null;
        if (Files.exists(Paths.get(DIRPATH+"\\Resources\\Images\\image.jpg"))) { extensionOnReloading=".jpg";}
        else if (Files.exists(Paths.get(DIRPATH+"\\Resources\\Images\\image.png"))) { extensionOnReloading=".png";}
        else if (Files.exists(Paths.get(DIRPATH+"\\Resources\\Images\\image.gif"))) {extensionOnReloading=".gif";}
        if (extensionOnReloading!=null)
        {
            imageViewer.setImage(new Image("file:/"+DIRPATH + "\\Resources\\Images\\image"+extensionOnReloading,DEFAULT_WIDTH,DEFAULT_HEIGHT,false,false));
        }


        videoViewer.setFitWidth(DEFAULT_WIDTH);
        videoViewer.setFitHeight(DEFAULT_HEIGHT);
        String VUrl = null;
        if(Files.exists(Paths.get(DIRPATH+"\\Resources\\Videos\\video.mp4")))
        { VUrl ="file:/"+DIRPATH.replace("\\","/") + "/Resources/Videos/video.mp4";}
        if (VUrl != null)
        {
            var mediaFromLoadLaunch = new Media(VUrl);
            mediaPlayer = new MediaPlayer(mediaFromLoadLaunch);
            videoViewer.setMediaPlayer(mediaPlayer);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setCycleCount(Integer.MAX_VALUE);
        }


        videoFileChooserButton.setOnAction(event ->
        {
            var fileChooser = new FileChooser();
            var imageFilters = new FileChooser.ExtensionFilter("Video filters", "*.mp4");
            fileChooser.getExtensionFilters().add(imageFilters);
            var result = fileChooser.showOpenDialog(Stage);
            if (result!=null)
            {
                try
                {
                    if (mediaPlayer != null && mediaPlayer.isAutoPlay())
                    {
                        mediaPlayer.stop();
                        mediaPlayer.dispose();
                    }
                    if (Files.isDirectory(Paths.get(DIRPATH+"\\Resources\\Videos"))&&Files.exists(Paths.get(DIRPATH+"\\Resources\\Videos")))
                    {
                        var folderWithVideo = new File(DIRPATH + "\\Resources\\Videos");
                        File[] contents = folderWithVideo.listFiles();
                        if (contents != null)
                        { for (File f : contents) { boolean deleted = f.delete(); } }
                        String s = result.getPath();
                        String s2 = result.getAbsolutePath();
                        String s3= DIRPATH+"\\Resources\\Videos\\video.mp4";
                        Files.copy(Paths.get(result.getPath()), Paths.get(DIRPATH+"\\Resources\\Videos\\video.mp4"), StandardCopyOption.REPLACE_EXISTING);
                    }
                    else
                    {
                        Files.createDirectory(Paths.get(DIRPATH+"\\Resources\\Videos"));
                        Files.copy(Paths.get(result.getPath()), Paths.get(DIRPATH+"\\Resources\\Videos\\video.mp4"), StandardCopyOption.REPLACE_EXISTING);
                    }
                    var mediaFromFirstLaunch = new Media("file:/" + result.getAbsolutePath().replace("\\","/"));
                    mediaPlayer = new MediaPlayer(mediaFromFirstLaunch);
                    videoViewer.setMediaPlayer(mediaPlayer);
                    mediaPlayer.setAutoPlay(true);
                    mediaPlayer.setCycleCount(Integer.MAX_VALUE);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });


        /*mediaPlayer.setOnReady(()->
        {
            int mediaWidth = media.getWidth();
            int mediaHeight = media.getHeight();
            if (mediaWidth !=DEFAULT_WIDTH && mediaHeight!=DEFAULT_HEIGHT)
            {
                videoViewer.setX(DEFAULT_WIDTH/2-mediaWidth/2);
                videoViewer.setY(DEFAULT_HEIGHT/2-mediaHeight/2);
            }
        });*/

        imageFileChooserButton.setOnAction(event ->
        {
            var fileChooser = new FileChooser();
            var imageFilters = new FileChooser.ExtensionFilter("Image filters", "*.jpg","*.png","*.gif");
            fileChooser.getExtensionFilters().add(imageFilters);
            var result = fileChooser.showOpenDialog(Stage);
            String extensionOnFirstLaunch;
            if (result!=null)
            {
                if (result.getName().endsWith(".jpg")) { extensionOnFirstLaunch=".jpg";}
                else if (result.getName().endsWith(".png")) { extensionOnFirstLaunch=".png";}
                else { extensionOnFirstLaunch=".gif";}
                try
                {
                    if (Files.isDirectory(Paths.get(DIRPATH+"\\Resources\\Images"))&&Files.exists(Paths.get(DIRPATH+"\\Resources\\Images")))
                    {
                        /**
                         * Чтобы иметь только один файл фона и не хранить несколько изображений в разном расширении, что
                         * будет мешать в выборе фона рабочего стола, ведь выбираться он будет по первому вхождению
                         * нужного расширения при первом запуске(смотри выше). Чтобы этого избежать, удаляется изображение,
                         * которое лежит в папке
                         */
                        var folderWithImages = new File(DIRPATH + "\\Resources\\Images");
                        File[] contents = folderWithImages.listFiles();
                        if (contents != null)
                        { for (File f : contents) { f.delete(); } }
                        Files.copy(Paths.get(result.getAbsolutePath()), Paths.get(DIRPATH+"\\Resources\\Images\\image"+extensionOnFirstLaunch), StandardCopyOption.REPLACE_EXISTING);
                    }
                    else
                    {
                        Files.createDirectory(Paths.get(DIRPATH+"\\Resources\\Images"));
                        Files.copy(Paths.get(result.getPath()), Paths.get(DIRPATH+"\\Resources\\Images\\image"+extensionOnFirstLaunch), StandardCopyOption.REPLACE_EXISTING);
                    }
                    if (mediaPlayer != null && mediaPlayer.isAutoPlay())
                    {
                        mediaPlayer.dispose();
                    }
                    /**
                     * Привыборе фотки, очищаем папку с видео, чтобы при последующем запуске у нас открывалась именно фотка
                     */
                    if (Files.isDirectory(Paths.get(DIRPATH+"\\Resources\\Videos"))&&Files.exists(Paths.get(DIRPATH+"\\Resources\\Videos")))
                    {
                        var folderWithVideo = new File(DIRPATH + "\\Resources\\Videos");
                        File[] contents = folderWithVideo.listFiles();
                        if (contents != null)
                        { for (File f : contents) { f.delete(); } }
                    }

                    var image = new Image("file:/" + result.getAbsolutePath(),DEFAULT_WIDTH,DEFAULT_HEIGHT,false,false);
                    imageViewer.setImage(image);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}

