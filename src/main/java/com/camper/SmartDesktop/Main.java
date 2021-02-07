package com.camper.SmartDesktop;


import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;

import static com.camper.SmartDesktop.Loading.*;
import static com.camper.SmartDesktop.Saving.addNewSaveFile;
import static com.camper.SmartDesktop.Saving.saveAll;

public class Main extends Application implements Initializable
{
    public static void main(String[] args) { launch(args); }

    @FXML private ChoiceBox<String> savesChoiceBox;
    @FXML private TabPane mainTabPane;
    @FXML private Tab tab1;
    @FXML private Tab tab2;
    @FXML private Tab tab3;
    @FXML private Tab tab4;
    @FXML private Tab tab5;
    @FXML private ImageView imageViewer;
    @FXML private MediaView videoViewer;
    @FXML private Button addNewPresetButton;
    @FXML private Button imageFileChooserButton;
    @FXML private Button videoFileChooserButton;
    @FXML private Button note;
    private static MediaPlayer mediaPlayer;
    private static int numberOfImmutableElements;
    public static int idOfSelectedTab=1;
    public static Map<Integer, List<Node>> tabs = new HashMap<>();
    public static Pane root;
    public static String currencySaveName;
    public static Properties saveInfo = new Properties();
    public static Stage Stage;
    public static final int DEFAULT_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    public static final int DEFAULT_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    public static final ClassLoader mainCL = Main.class.getClassLoader();
    public static final String DIRPATH = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();

    public static void addChild(Parent node) { root.getChildren().add(node); }

    private static void deleteAllNewElements()
    {
        var list = root.getChildren();
        list.remove(numberOfImmutableElements,list.size());
        Note.clearSaveList();
    }

    private static void clearTab(TabPane tabPane) throws IOException
    {
        for (int i = 1;i<(tabs.size()+1);i++)
        {
            tabs.get(i).clear();
        }
        saveInfo.setProperty("lastSaveName",saveInfo.getProperty("lastSaveName"));
        saveInfo.store(new FileOutputStream(DIRPATH+"\\Resources\\Saves\\saveInfo.properties"),"Info of latest save");
    }


    @Override
    public void start(Stage stage) throws Exception
    {
        stage.setOnCloseRequest((event)->
        {
            try { saveAll(event); }
            catch (ParserConfigurationException | TransformerException | IOException e)
            { e.printStackTrace(); }
        });

        if (!(Files.isDirectory(Paths.get(DIRPATH+"\\Resources"))&&Files.exists(Paths.get(DIRPATH+"\\Resources"))))
        { Files.createDirectory(Paths.get(DIRPATH+"\\Resources")); }

        if (!(Files.isDirectory(Paths.get(DIRPATH+"\\Resources\\Saves"))&&Files.exists(Paths.get(DIRPATH+"\\Resources\\Saves"))))
        { Files.createDirectory(Paths.get(DIRPATH+"\\Resources\\Saves")); }

        if (!Files.exists(Paths.get(DIRPATH + "\\Resources\\Saves\\saveInfo.properties")))
        {
            saveInfo.setProperty("lastSaveName","");
            saveInfo.store(new FileOutputStream(DIRPATH+"\\Resources\\Saves\\saveInfo.properties"),"Info of latest save");
        }
        try(FileInputStream io = new FileInputStream(DIRPATH+"\\Resources\\Saves\\saveInfo.properties"))
        { saveInfo.load(io); }

        var elementsOfTab1 = new ArrayList<Node>();
        var elementsOfTab2 = new ArrayList<Node>();
        var elementsOfTab3 = new ArrayList<Node>();
        var elementsOfTab4 = new ArrayList<Node>();
        var elementsOfTab5 = new ArrayList<Node>();
        tabs.put(1,elementsOfTab1);
        tabs.put(2,elementsOfTab2);
        tabs.put(3,elementsOfTab3);
        tabs.put(4,elementsOfTab4);
        tabs.put(5,elementsOfTab5);

        root = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/StartScreenRu.fxml")));
        var scene = new Scene(root,DEFAULT_WIDTH,DEFAULT_HEIGHT-66);

        Stage = stage;
        numberOfImmutableElements = root.getChildren().size();

        currencySaveName = loadSave(null);
        //После загрузки находит таб с пресетами и устанавливает ему пресет равный числу в сохранённом файле
        for (Node node : root.getChildren())
        {
            if (node instanceof TabPane && node.getAccessibleHelp().equals("Presaves tab pane"))
            {
                var selectionModel = ((TabPane)node).getSelectionModel();
                selectionModel.select(idOfSelectedTab-1);
                break;
            }
        }

        stage.setScene(scene);
        stage.setTitle("SmartDesktop");
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        var selectionModel=mainTabPane.getSelectionModel();
        loadSavesToSavesList(savesChoiceBox);
        addNewPresetButton.setOnAction((event)->
        {
            try
            {
                String nameNewSave = addNewSaveFile();
                savesChoiceBox.getItems().add(nameNewSave);
                savesChoiceBox.setValue(nameNewSave);
                saveAll(null);
                deleteAllNewElements();
                clearTab(mainTabPane);
                loadSave(nameNewSave);

                selectionModel.select(idOfSelectedTab-1);

                loadSavesToSavesList(savesChoiceBox);
            } catch (Exception e)
            { e.printStackTrace(); }
        });
        savesChoiceBox.setOnAction(event->
        {
            try
            {
                saveAll(null);
                deleteAllNewElements();
                clearTab(mainTabPane);
                loadSave(savesChoiceBox.getValue());

                selectionModel.select(idOfSelectedTab-1);

            } catch (Exception e)
            { e.printStackTrace(); }
        });
        tab1.setOnSelectionChanged((event)-> { if(tab1.isSelected()) {updateElementsVisibility(1);} });
        tab2.setOnSelectionChanged((event)-> { if(tab2.isSelected()) {updateElementsVisibility(2);} });
        tab3.setOnSelectionChanged((event)-> { if(tab3.isSelected()) {updateElementsVisibility(3);} });
        tab4.setOnSelectionChanged((event)-> { if(tab4.isSelected()) {updateElementsVisibility(4);} });
        tab5.setOnSelectionChanged((event)-> { if(tab5.isSelected()) {updateElementsVisibility(5);} });

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
            try { new Note().start(Stage); /*saveAll(null);*/ }
            catch (Exception e)
            { e.printStackTrace(); }
        });
    }
}

