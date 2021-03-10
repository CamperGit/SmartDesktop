package com.camper.SmartDesktop;


import com.camper.SmartDesktop.Info.*;
import com.camper.SmartDesktop.StandardElements.TableSD;
import com.camper.SmartDesktop.StandardElements.Weather;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.*;

import static com.camper.SmartDesktop.Loading.*;
import static com.camper.SmartDesktop.Saving.addNewSaveFile;
import static com.camper.SmartDesktop.Saving.saveAll;

public class Main extends Application implements Initializable
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @FXML
    private ChoiceBox<String> savesChoiceBox;
    @FXML
    private TabPane toolBarTabPane;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab tab1, tab2, tab3, tab4, tab5;
    @FXML
    private ImageView imageViewer;
    @FXML
    private MediaView videoViewer;
    @FXML
    private Button addNewPresetButton;
    @FXML
    private Button imageFileChooserButton;
    @FXML
    private Button videoFileChooserButton;
    @FXML
    private Button note;
    @FXML
    private Button schedule;
    @FXML
    private Button goal;
    @FXML
    private Button notification;
    @FXML
    private Button upcomingEventInfo;
    @FXML
    private Button table;
    @FXML
    private Button weather;
    @FXML
    private Button calendar;
    @FXML
    private Button autorizeButton;
    @FXML
    private Button checkDeprecatedEventsButton;
    @FXML
    private ImageView noteIV;
    @FXML
    private ImageView scheduleIV;
    @FXML
    private ImageView goalIV;
    @FXML
    private ImageView notificationIV;
    @FXML
    private ImageView upcomingEventInfoIV;
    @FXML
    private ImageView weatherIV;
    @FXML
    private ImageView tableIV;
    @FXML
    private ImageView imagePlayerIV;
    @FXML
    private ImageView mediaPlayerIV;
    @FXML
    private ImageView calendarIV;
    @FXML
    private ImageView deprecatedEventsIV;
    @FXML
    private Menu helpMenu;
    @FXML
    private Menu aboutTheProgramMenu;
    @FXML
    private MenuItem desktopPhotoSelectorMenuItem;
    @FXML
    private MenuItem desktopVideoSelectorMenuItem;
    @FXML
    private CheckMenuItem hideLeftTabPaneMenuItem;
    @FXML
    private CheckMenuItem hideRightTabPaneMenuItem;

    private static MediaPlayer mediaPlayer;
    private static int numberOfImmutableElements;
    public static int idOfSelectedTab = 1;
    public static Map<Integer, List<Node>> tabs = new HashMap<>();
    public static Pane root;
    public static String currencySaveName;
    public static Properties saveInfo = new Properties();
    public static Stage Stage;
    public static final int DEFAULT_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    public static final int DEFAULT_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    public static final ClassLoader mainCL = Main.class.getClassLoader();
    public static final String DIRPATH = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();

    public static void addChild(Parent node)
    {
        root.getChildren().add(node);
    }

    public static void setRegion(Region node, int width, int height)
    {
        node.setMinHeight(height);
        node.setPrefHeight(height);
        node.setMaxHeight(height);
        node.setMinWidth(width);
        node.setPrefWidth(width);
        node.setMaxWidth(width);
    }

    private static void deleteAllNewElements()
    {
        var list = root.getChildren();
        list.remove(numberOfImmutableElements, list.size());
        NoteSD.clearSaveList();
        NotificationSD.clearSaveList();
        ScheduleSD.clearSaveList();
        GoalSD.clearSaveList();
        PrenotificationSD.clearSaveList();
        TableSD.clearSaveList();
        DeprecatedEvents.clearLastInfo();
        UpcomingEvent.clearLastInfo();
        Weather.clearLastInfo();
        CalendarSD.clearLastInfo();
    }

    public static int returnAnchorId(Node parent)
    {
        while (!(parent instanceof AnchorPane))
        {
            parent = parent.getParent();
        }
        var pane = (AnchorPane) parent;
        return Integer.parseInt(pane.getAccessibleHelp());
    }

    private static void clearTab()
    {
        for (int i = 1; i < (tabs.size() + 1); i++)
        {
            tabs.get(i).clear();
        }
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        stage.setOnCloseRequest((event) ->
        {
            try
            {
                saveAll(event);
            } catch (ParserConfigurationException | TransformerException | IOException | InterruptedException e)
            {
                e.printStackTrace();
            }
        });

        if (!(Files.isDirectory(Paths.get(DIRPATH + "\\Resources")) && Files.exists(Paths.get(DIRPATH + "\\Resources"))))
        {
            Files.createDirectory(Paths.get(DIRPATH + "\\Resources"));
        }

        if (!(Files.isDirectory(Paths.get(DIRPATH + "\\Resources\\Saves")) && Files.exists(Paths.get(DIRPATH + "\\Resources\\Saves"))))
        {
            Files.createDirectory(Paths.get(DIRPATH + "\\Resources\\Saves"));
        }

        if (!Files.exists(Paths.get(DIRPATH + "\\Resources\\Saves\\saveInfo.properties")))
        {
            saveInfo.setProperty("lastSaveName", "save1.xml");
            saveInfo.store(new FileOutputStream(DIRPATH + "\\Resources\\Saves\\saveInfo.properties"), "Info of latest save");
        }
        try (FileInputStream io = new FileInputStream(DIRPATH + "\\Resources\\Saves\\saveInfo.properties"))
        {
            saveInfo.load(io);
        }

        var elementsOfTab1 = new ArrayList<Node>();
        var elementsOfTab2 = new ArrayList<Node>();
        var elementsOfTab3 = new ArrayList<Node>();
        var elementsOfTab4 = new ArrayList<Node>();
        var elementsOfTab5 = new ArrayList<Node>();
        tabs.put(1, elementsOfTab1);
        tabs.put(2, elementsOfTab2);
        tabs.put(3, elementsOfTab3);
        tabs.put(4, elementsOfTab4);
        tabs.put(5, elementsOfTab5);

        Stage = stage;

        root = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/StartScreenRu.fxml")));
        var scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT - 66);

        numberOfImmutableElements = root.getChildren().size();

        currencySaveName = loadSave(null);

        if (CalendarSD.getRoot() == null)
        {
            try
            {
                new CalendarSD().start(stage);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        //После загрузки находит таб с пресетами и устанавливает ему пресет равный числу в сохранённом файле
        for (Node node : root.getChildren())
        {
            if (node instanceof TabPane && node.getAccessibleHelp().equals("Presaves tab pane"))
            {
                var selectionModel = ((TabPane) node).getSelectionModel();
                selectionModel.select(idOfSelectedTab - 1);
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
        noteIV.setImage(new Image("Images/note35.png"));
        scheduleIV.setImage(new Image("Images/schedule35.png"));
        goalIV.setImage(new Image("Images/goal42.png"));
        notificationIV.setImage(new Image("Images/notification35.png"));
        upcomingEventInfoIV.setImage(new Image("Images/upcomingEvent35.png"));
        tableIV.setImage(new Image("Images/table35.png"));
        weatherIV.setImage(new Image("Images/weather35.png"));
        imagePlayerIV.setImage(new Image("Images/imageViewer35.png"));
        mediaPlayerIV.setImage(new Image("Images/videoPlayer35.png"));
        calendarIV.setImage(new Image("Images/calendar35.png"));
        deprecatedEventsIV.setImage(new Image("Images/bell25.png"));

        autorizeButton.setLayoutX(DEFAULT_WIDTH - 120);
        savesChoiceBox.setLayoutX(DEFAULT_WIDTH - 320);
        addNewPresetButton.setLayoutX(DEFAULT_WIDTH - 345);
        checkDeprecatedEventsButton.setLayoutX(DEFAULT_WIDTH - 512);

        hideLeftTabPaneMenuItem.setOnAction(event -> toolBarTabPane.setVisible(!hideLeftTabPaneMenuItem.isSelected()));
        hideRightTabPaneMenuItem.setOnAction(event -> mainTabPane.setVisible(!hideRightTabPaneMenuItem.isSelected()));

        checkDeprecatedEventsButton.setOnMouseClicked(event ->
        {
            try
            {
                new DeprecatedEvents().start(Stage);
                DeprecatedEvents.updateBellIcon(false);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        var selectionModel = mainTabPane.getSelectionModel();
        loadSavesToSavesList(savesChoiceBox);
        //Устанавливаем значение пресета в загрузочном списке
        savesChoiceBox.setValue(saveInfo.getProperty("lastSaveName"));
        addNewPresetButton.setOnAction((event) ->
        {
            try
            {
                loadSavesToSavesList(savesChoiceBox);
                String nameNewSave = addNewSaveFile();
                savesChoiceBox.getItems().add(nameNewSave);
                //Даже при устанавлении значения в выплывающем списке программно срабатывает обработчик событий для него.
                savesChoiceBox.setValue(nameNewSave);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });
        savesChoiceBox.setOnAction(event ->
        {
            try
            {
                //Вызывается и при создании нового пресета и при загрузке уже существующего
                saveAll(null);
                deleteAllNewElements();
                clearTab();
                loadSave(savesChoiceBox);

                selectionModel.select(idOfSelectedTab - 1);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });
        tab1.setOnSelectionChanged((event) ->
        {
            if (tab1.isSelected())
            {
                updateElementsVisibility(1);
            }
        });
        tab2.setOnSelectionChanged((event) ->
        {
            if (tab2.isSelected())
            {
                updateElementsVisibility(2);
            }
        });
        tab3.setOnSelectionChanged((event) ->
        {
            if (tab3.isSelected())
            {
                updateElementsVisibility(3);
            }
        });
        tab4.setOnSelectionChanged((event) ->
        {
            if (tab4.isSelected())
            {
                updateElementsVisibility(4);
            }
        });
        tab5.setOnSelectionChanged((event) ->
        {
            if (tab5.isSelected())
            {
                updateElementsVisibility(5);
            }
        });

        imageViewer.setFitWidth(DEFAULT_WIDTH);
        imageViewer.setFitHeight(DEFAULT_HEIGHT);
        String extensionOnReloading = null;
        if (Files.exists(Paths.get(DIRPATH + "\\Resources\\Images\\image.jpg")))
        {
            extensionOnReloading = ".jpg";
        } else if (Files.exists(Paths.get(DIRPATH + "\\Resources\\Images\\image.png")))
        {
            extensionOnReloading = ".png";
        } else if (Files.exists(Paths.get(DIRPATH + "\\Resources\\Images\\image.gif")))
        {
            extensionOnReloading = ".gif";
        }
        if (extensionOnReloading != null)
        {
            imageViewer.setImage(new Image("file:/" + DIRPATH + "\\Resources\\Images\\image" + extensionOnReloading, DEFAULT_WIDTH, DEFAULT_HEIGHT, false, false));
        }

        videoViewer.setFitWidth(DEFAULT_WIDTH);
        videoViewer.setFitHeight(DEFAULT_HEIGHT);
        String VUrl = null;
        if (Files.exists(Paths.get(DIRPATH + "\\Resources\\Videos\\video.mp4")))
        {
            VUrl = "file:/" + DIRPATH.replace("\\", "/") + "/Resources/Videos/video.mp4";
        }
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
            if (result != null)
            {
                try
                {
                    if (mediaPlayer != null && mediaPlayer.isAutoPlay())
                    {
                        mediaPlayer.stop();
                        mediaPlayer.dispose();
                    }
                    if (Files.isDirectory(Paths.get(DIRPATH + "\\Resources\\Images")) && Files.exists(Paths.get(DIRPATH + "\\Resources\\Images")))
                    {
                        var folderWithImages = new File(DIRPATH + "\\Resources\\Images");
                        File[] contents = folderWithImages.listFiles();
                        if (contents != null)
                        {
                            for (File f : contents)
                            {
                                if (f.delete())
                                {
                                    //Лог что удалилось
                                }
                            }
                        }
                    }
                    if (Files.isDirectory(Paths.get(DIRPATH + "\\Resources\\Videos")) && Files.exists(Paths.get(DIRPATH + "\\Resources\\Videos")))
                    {
                        var folderWithVideo = new File(DIRPATH + "\\Resources\\Videos");
                        File[] contents = folderWithVideo.listFiles();
                        if (contents != null)
                        {
                            for (File f : contents)
                            {
                                if (f.delete())
                                {
                                    //Лог что удалилось
                                }
                            }
                        }
                    } else
                    {
                        Files.createDirectory(Paths.get(DIRPATH + "\\Resources\\Videos"));
                    }
                    Files.copy(Paths.get(result.getPath()), Paths.get(DIRPATH + "\\Resources\\Videos\\video.mp4"), StandardCopyOption.REPLACE_EXISTING);
                    var mediaFromFirstLaunch = new Media("file:/" + result.getAbsolutePath().replace("\\", "/"));
                    mediaPlayer = new MediaPlayer(mediaFromFirstLaunch);
                    videoViewer.setMediaPlayer(mediaPlayer);
                    mediaPlayer.setAutoPlay(true);
                    mediaPlayer.setCycleCount(Integer.MAX_VALUE);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        desktopVideoSelectorMenuItem.setOnAction(videoFileChooserButton.getOnAction());

        imageFileChooserButton.setOnAction(event ->
        {
            var fileChooser = new FileChooser();
            var imageFilters = new FileChooser.ExtensionFilter("Image filters", "*.jpg", "*.png", "*.gif");
            fileChooser.getExtensionFilters().add(imageFilters);
            var result = fileChooser.showOpenDialog(Stage);
            String extensionOnFirstLaunch;
            if (result != null)
            {
                if (result.getName().endsWith(".jpg"))
                {
                    extensionOnFirstLaunch = ".jpg";
                } else if (result.getName().endsWith(".png"))
                {
                    extensionOnFirstLaunch = ".png";
                } else
                {
                    extensionOnFirstLaunch = ".gif";
                }
                try
                {
                    if (Files.isDirectory(Paths.get(DIRPATH + "\\Resources\\Images")) && Files.exists(Paths.get(DIRPATH + "\\Resources\\Images")))
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
                        {
                            for (File f : contents)
                            {
                                if (f.delete())
                                {
                                    //Лог что удалилось
                                }
                            }
                        }
                        Files.copy(Paths.get(result.getAbsolutePath()), Paths.get(DIRPATH + "\\Resources\\Images\\image" + extensionOnFirstLaunch), StandardCopyOption.REPLACE_EXISTING);
                    } else
                    {
                        Files.createDirectory(Paths.get(DIRPATH + "\\Resources\\Images"));
                        Files.copy(Paths.get(result.getPath()), Paths.get(DIRPATH + "\\Resources\\Images\\image" + extensionOnFirstLaunch), StandardCopyOption.REPLACE_EXISTING);
                    }
                    if (mediaPlayer != null && mediaPlayer.isAutoPlay())
                    {
                        mediaPlayer.dispose();
                    }
                    /**
                     * Привыборе фотки, очищаем папку с видео, чтобы при последующем запуске у нас открывалась именно фотка
                     */

                    if (Files.isDirectory(Paths.get(DIRPATH + "\\Resources\\Videos")) && Files.exists(Paths.get(DIRPATH + "\\Resources\\Videos")))
                    {
                        var folderWithVideo = new File(DIRPATH + "\\Resources\\Videos");
                        File[] contents = folderWithVideo.listFiles();
                        if (contents != null)
                        {
                            for (File f : contents)
                            {
                                if (f.delete())
                                {
                                    //Лог что удалилось
                                }
                            }
                        }
                    }

                    var image = new Image("file:/" + result.getAbsolutePath(), DEFAULT_WIDTH, DEFAULT_HEIGHT, false, false);
                    imageViewer.setImage(image);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        desktopPhotoSelectorMenuItem.setOnAction(imageFileChooserButton.getOnAction());

        note.setOnAction(event ->
        {
            try
            {
                new NoteSD().start(Stage);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        schedule.setOnAction(event ->
        {
            try
            {
                new ScheduleSD().start(Stage);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        goal.setOnAction(event ->
        {
            try
            {
                new GoalSD().start(Stage);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        notification.setOnAction(event ->
        {
            try
            {
                new NotificationSD().start(Stage);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        upcomingEventInfo.setOnAction(event ->
        {
            var upcomingEventInfo = UpcomingEvent.getUpcomingEventInfoRoot();
            if (upcomingEventInfo!=null)
            {
                upcomingEventInfo.setVisible(true);
                tabs.get(Integer.parseInt(upcomingEventInfo.getAccessibleText())).remove(upcomingEventInfo);
                tabs.get(idOfSelectedTab).add(upcomingEventInfo);
                upcomingEventInfo.setAccessibleText(String.valueOf(idOfSelectedTab));
            }
            else
            {
                try
                {
                    new UpcomingEvent().start(Stage);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        table.setOnAction(event ->
        {
            try
            {
                new TableSD().start(Stage);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        weather.setOnAction(event ->
        {
            var weather = Weather.getWeatherRoot();
            if (weather!=null)
            {
                weather.setVisible(true);
                tabs.get(Integer.parseInt(weather.getAccessibleText())).remove(weather);
                tabs.get(idOfSelectedTab).add(weather);
                weather.setAccessibleText(String.valueOf(idOfSelectedTab));
            }
            else
            {
                try
                {
                    new Weather().start(Stage);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        calendar.setOnAction((event ->
        {
            var calendar = CalendarSD.getRoot();
            calendar.setVisible(true);

            int tabNumber = Integer.parseInt(calendar.getAccessibleText());
            if (tabNumber != -1)
            {
                var oldTab = tabs.get(tabNumber);
                oldTab.remove(calendar);
            }

            calendar.setAccessibleText(String.valueOf(idOfSelectedTab));
            var elementsOfSelectedTab = tabs.get(idOfSelectedTab);
            elementsOfSelectedTab.add(calendar);
        }));
    }
}

