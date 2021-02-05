package com.camper.SmartDesktop;


import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;

public class Main extends Application implements Initializable
{
    public static void main(String[] args) { launch(args); }

    @FXML private ImageView imageViewer;
    @FXML private MediaView videoViewer;
    @FXML private Button imageFileChooserButton;
    @FXML private Button videoFileChooserButton;
    @FXML private Button note;
    private static MediaPlayer mediaPlayer;
    private static Pane root;
    private static Stage Stage;
    public static final int DEFAULT_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    public static final int DEFAULT_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    public static final ClassLoader mainCL = Main.class.getClassLoader();
    public static final String DIRPATH = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();

    public static void addChild(Parent node) { root.getChildren().add(node); }

    public static void saveAll() throws ParserConfigurationException, TransformerException, IOException
    {
        var factory = DocumentBuilderFactory.newInstance();
        final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
        final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
        factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
        var builder = factory.newDocumentBuilder();
        var doc = builder.newDocument();

        var rootDocument = doc.createElement("save");
        doc.appendChild(rootDocument);

        Note.addNotesToXML(doc);

        var t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT,"yes"); //Отступ
        t.setOutputProperty(OutputKeys.METHOD,"xml");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        int id = 1;
        while(Files.exists(Path.of(DIRPATH + "\\Resources\\Saves\\save" + id + ".xml")))
        {
            id++;
        }
        t.transform(new DOMSource(doc), new StreamResult(Files.newOutputStream(Paths.get(DIRPATH + "\\Resources\\Saves\\save" +id+".xml"))));
    }

    @Override
    public void start(Stage stage) throws IOException
    {
        stage.setOnCloseRequest((event)->
        {
            try
            {
                saveAll();
            }
            catch (ParserConfigurationException | TransformerException | IOException e)
            {
                e.printStackTrace();
            }
            /*try
            {
                SavingElements.saveAll();
            } catch (ParserConfigurationException e)
            {
                e.printStackTrace();
            } catch (TransformerException e)
            {
                e.printStackTrace();
            } catch (SAXException e)
            {
                e.printStackTrace();
            } catch (XPathExpressionException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }*/
            //saveAll()
            /*var list = root.getChildren();
            for(Node node : list)
            {
                if (node instanceof AnchorPane)
                {
                    var listLvl2 = ((AnchorPane) node).getChildren();
                    for(Node nodeLvl2 : listLvl2)
                    {
                        if (nodeLvl2 instanceof TextArea)
                        {
                            String text = ((TextArea) nodeLvl2).getText();
                            if (Files.isDirectory(Paths.get(DIRPATH + "\\Resources\\Saves")) && Files.exists(Paths.get(DIRPATH + "\\Resources\\Saves")))
                            {
                                try(var out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(DIRPATH + "\\Resources\\Saves\\test.txt"), StandardCharsets.UTF_8),true);)
                                {
                                    out.println(text);
                                }
                                catch (FileNotFoundException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }*/
        });
        root = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/StartScreenRu.fxml")));
        var scene = new Scene(root,DEFAULT_WIDTH,DEFAULT_HEIGHT-66);
        if (!(Files.isDirectory(Paths.get(DIRPATH+"\\Resources"))&&Files.exists(Paths.get(DIRPATH+"\\Resources"))))
        { Files.createDirectory(Paths.get(DIRPATH+"\\Resources")); }
        if (!(Files.isDirectory(Paths.get(DIRPATH+"\\Resources\\Saves"))&&Files.exists(Paths.get(DIRPATH+"\\Resources\\Saves"))))
        { Files.createDirectory(Paths.get(DIRPATH+"\\Resources\\Saves")); }
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
                    if (Files.isDirectory(Paths.get(DIRPATH+"\\Resources\\Images"))&&Files.exists(Paths.get(DIRPATH+"\\Resources\\Images")))
                    {
                        var folderWithImages = new File(DIRPATH + "\\Resources\\Images");
                        File[] contents = folderWithImages.listFiles();
                        if (contents != null)
                        { for (File f : contents) { f.delete(); } }
                    }
                    if (Files.isDirectory(Paths.get(DIRPATH+"\\Resources\\Videos"))&&Files.exists(Paths.get(DIRPATH+"\\Resources\\Videos")))
                    {
                        var folderWithVideo = new File(DIRPATH + "\\Resources\\Videos");
                        File[] contents = folderWithVideo.listFiles();
                        if (contents != null)
                        { for (File f : contents) { f.delete(); } }
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

        note.setOnAction((event)->
        {
            try
            {
                new Note().start(Stage);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        /*var list = selected.getChildren();
        for (Node node: list)
        {

        }*/

    }
}

