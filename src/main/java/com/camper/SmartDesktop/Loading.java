package com.camper.SmartDesktop;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.camper.SmartDesktop.Main.*;
import static com.camper.SmartDesktop.Main.saveInfo;
import static com.camper.SmartDesktop.Saving.addNewSaveFile;
import static com.camper.SmartDesktop.Saving.createEmptyXML;

public class Loading
{
    /**
     *
     * @param saves - choicebox with saves
     * @return loading save name
     * @throws Exception
     */
    public static String loadSave(ChoiceBox<String> saves) throws Exception
    {
        String saveNameFromChoiceBox=null;
        if (saves!=null) { saveNameFromChoiceBox = saves.getValue(); }
        String filename="";
        var folderWithSaves = new File(DIRPATH + "\\Resources\\Saves");

        File[] contents = folderWithSaves.listFiles();
        if (contents!=null && contents.length!=0 )
        {
            int countOfSaves=0;
            for (File file : contents)
            {
                if (file.getAbsolutePath().endsWith(".xml"))
                {
                    countOfSaves++;
                    break;
                }
            }
            if (countOfSaves!=0)
            {
                var factory = DocumentBuilderFactory.newInstance();
                var builder = factory.newDocumentBuilder();
                var xPathFactory = XPathFactory.newInstance();
                var xPath = xPathFactory.newXPath();

                if (saveNameFromChoiceBox==null)
                {
                    if (Files.exists(Paths.get(DIRPATH + "\\Resources\\Saves\\"+saveInfo.getProperty("lastSaveName"))))
                    {
                        filename = saveInfo.getProperty("lastSaveName");
                    }
                }
                else
                {
                    if (Files.exists(Paths.get(DIRPATH + "\\Resources\\Saves\\"+saveNameFromChoiceBox)))
                    {
                        filename = saveNameFromChoiceBox;
                    }
                }

                Document doc;
                if (!filename.equals(""))
                {
                    doc = builder.parse(DIRPATH + "\\Resources\\Saves\\" + filename);
                }
                //Проверка проходит, если у нас при попытке загрузки из сейвлиста оказывается, что выбранного сохранения нет.
                //Тогда мы загружаем старое сохранение, которое точно будет, потому что мы его только что создали
                else if (Files.exists(Paths.get(DIRPATH + "\\Resources\\Saves\\"+saveInfo.getProperty("lastSaveName"))))
                {
                    var alert = new Alert(Alert.AlertType.WARNING, "Выбранное сохранение было удалено или переименовано. Загрузка прервана", ButtonType.OK);
                    alert.showAndWait();

                    filename=saveInfo.getProperty("lastSaveName");
                    saves.getItems().remove(saveNameFromChoiceBox);

                    doc = builder.parse(DIRPATH + "\\Resources\\Saves\\" + saveInfo.getProperty("lastSaveName"));
                }
                //Проверка проходит, если у нас при перивычной загрузке отсутствует файл последнего сохранения.
                //Тогда мы создаём новое пустое сохранение и загружаем его.
                else
                {
                    var alert = new Alert(Alert.AlertType.WARNING, "Выбранное сохранение было удалено или переименовано. Загрузка прервана", ButtonType.OK);
                    alert.showAndWait();

                    filename=addNewSaveFile();

                    doc = builder.parse(DIRPATH + "\\Resources\\Saves\\" + filename);
                    saveInfo.setProperty("lastSaveName",filename);
                    saveInfo.store(new FileOutputStream(DIRPATH+"\\Resources\\Saves\\saveInfo.properties"),"Info of latest save");
                }

                Note.loadNotesFromXML(doc,xPath);

                idOfSelectedTab = Integer.parseInt(xPath.evaluate("save/lastTab/@tab",doc));
            }
            //Создание пустого файла сохранения при самом первом запуске или в папке есть файл properties, но нет сейвов вообще
            else
            {
                filename="save1.xml";
                createEmptyXML(filename);
                saveInfo.setProperty("lastSaveName",filename);
                saveInfo.store(new FileOutputStream(DIRPATH+"\\Resources\\Saves\\saveInfo.properties"),"Info of latest save");
            }
            currencySaveName = filename;
            return filename;
        }
        return "";
    }

    public static void loadSavesToSavesList(ChoiceBox<String> saves)
    {
        saves.getItems().clear();
        var folderWithSaves = new File(DIRPATH + "\\Resources\\Saves");
        File[] contents = folderWithSaves.listFiles();
        if (contents!=null && contents.length!=0 )
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
        idOfSelectedTab=numberOfTab;
    }
}
