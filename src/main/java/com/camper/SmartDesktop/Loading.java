package com.camper.SmartDesktop;

import com.camper.SmartDesktop.Info.*;
import com.camper.SmartDesktop.StandardElements.TableSD;
import com.camper.SmartDesktop.StandardElements.Weather;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.camper.SmartDesktop.Info.UpcomingEvent.runEventTask;
import static com.camper.SmartDesktop.Main.*;
import static com.camper.SmartDesktop.Main.saveInfo;
import static com.camper.SmartDesktop.Saving.addNewSaveFile;
import static com.camper.SmartDesktop.Saving.createEmptyXML;

public class Loading
{
    /**
     * @param saves - choicebox with saves
     * @return loading save name
     * @throws Exception
     */
    public static String loadSave(ChoiceBox<String> saves) throws Exception
    {
        logger.info("Loading: start loading save");
        String saveNameFromChoiceBox = null;
        if (saves != null)
        {
            saveNameFromChoiceBox = saves.getValue();
        }
        String filename = "";
        var folderWithSaves = new File(DIRPATH + "\\Resources\\Saves");

        File[] contents = folderWithSaves.listFiles();
        if (contents != null && contents.length != 0)
        {
            int countOfSaves = 0;
            for (File file : contents)
            {
                if (file.getAbsolutePath().endsWith(".xml"))
                {
                    countOfSaves++;
                    break;
                }
            }
            if (countOfSaves != 0)
            {
                var factory = DocumentBuilderFactory.newInstance();
                var builder = factory.newDocumentBuilder();
                var xPathFactory = XPathFactory.newInstance();
                var xPath = xPathFactory.newXPath();

                if (saveNameFromChoiceBox == null)
                {
                    if (Files.exists(Paths.get(DIRPATH + "\\Resources\\Saves\\" + saveInfo.getProperty("lastSaveName"))))
                    {
                        filename = saveInfo.getProperty("lastSaveName");
                        logger.info("Loading: the last save file exists");
                    }
                } else
                {
                    if (Files.exists(Paths.get(DIRPATH + "\\Resources\\Saves\\" + saveNameFromChoiceBox)))
                    {
                        filename = saveNameFromChoiceBox;
                        logger.info("Loading: save file from choice box exist");
                    }
                }

                Document doc;
                if (!filename.equals(""))
                {
                    doc = builder.parse(DIRPATH + "\\Resources\\Saves\\" + filename);
                }
                //ѕроверка проходит, если у нас при попытке загрузки из сейвлиста оказываетс€, что выбранного сохранени€ нет.
                //“огда мы загружаем старое сохранение, которое точно будет, потому что мы его только что создали
                else if (Files.exists(Paths.get(DIRPATH + "\\Resources\\Saves\\" + saveInfo.getProperty("lastSaveName"))))
                {
                    var alert = new Alert(Alert.AlertType.WARNING, languageBundle.getString("loadingSaveErrorAlert"), ButtonType.OK);
                    alert.showAndWait();

                    filename = saveInfo.getProperty("lastSaveName");
                    assert saves != null;
                    saves.getItems().remove(saveNameFromChoiceBox);

                    doc = builder.parse(DIRPATH + "\\Resources\\Saves\\" + saveInfo.getProperty("lastSaveName"));
                }
                //ѕроверка проходит, если у нас при первичной загрузке отсутствует файл последнего сохранени€.
                //“огда мы создаЄм новое пустое сохранение и загружаем его.
                else
                {
                    var alert = new Alert(Alert.AlertType.WARNING, languageBundle.getString("loadingSaveErrorAlert"), ButtonType.OK);
                    alert.showAndWait();

                    filename = addNewSaveFile();

                    doc = builder.parse(DIRPATH + "\\Resources\\Saves\\" + filename);
                    saveInfo.setProperty("lastSaveName", filename);
                    saveInfo.setProperty("language", "ru");
                    saveInfo.store(new FileOutputStream(DIRPATH + "\\Resources\\Saves\\saveInfo.properties"), "Info of latest save");
                    logger.info("Loading: create new saveInfo.properties");
                }

                logger.info("Loading: start elements loading");
                NoteSD.loadNotesFromXML(doc, xPath);
                ScheduleSD.loadSchedulesFromXML(doc, xPath);
                GoalSD.loadGoalsFromXML(doc, xPath);
                TableSD.loadTablesFromXML(doc, xPath);
                Weather.loadWeatherInfoFromXML(doc, xPath);
                UpcomingEvent.loadUpcomingEventInfoFromXML(doc, xPath);
                // алендарь всегда должен грузитьс€ последним!!!
                CalendarSD.loadCalendarFromXML(doc, xPath);
                logger.info("Loading: end elements loading");

                idOfSelectedTab = Integer.parseInt(xPath.evaluate("save/lastTab/@tab", doc));
            }
            //—оздание пустого файла сохранени€ при самом первом запуске или в папке есть файл properties, но нет сейвов вообще
            else
            {
                logger.info("Loading: saves folder are empty");
                filename = "save1.xml";
                createEmptyXML(filename);
                saveInfo.setProperty("lastSaveName", filename);
                saveInfo.setProperty("language", "ru");
                saveInfo.store(new FileOutputStream(DIRPATH + "\\Resources\\Saves\\saveInfo.properties"), "Info of latest save");
                runEventTask();
            }
            currencySaveName = filename;
            logger.info("Loading: end loading save");
            return filename;
        }
        logger.info("Loading: end loading save");
        return "";
    }

    public static void loadSavesToSavesList(ChoiceBox<String> saves)
    {
        saves.getItems().clear();
        var folderWithSaves = new File(DIRPATH + "\\Resources\\Saves");
        File[] contents = folderWithSaves.listFiles();
        if (contents != null && contents.length != 0)
        {
            for (File file : contents)
            {
                if (file.getAbsolutePath().endsWith(".xml"))
                {
                    saves.getItems().add(file.getName());
                }
            }
        }
    }

    public static void updateElementsVisibility(int numberOfTab)
    {
        var tabToDisableVisible = tabs.get(idOfSelectedTab);
        for (Node node : tabToDisableVisible)
        {
            node.setVisible(false);
        }
        var tabToEnableVisible = tabs.get(numberOfTab);
        for (Node node : tabToEnableVisible)
        {
            node.setVisible(true);
        }
        idOfSelectedTab = numberOfTab;
    }
}