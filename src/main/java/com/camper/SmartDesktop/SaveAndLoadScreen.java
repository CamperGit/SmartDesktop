package com.camper.SmartDesktop;

import com.sun.scenario.Settings;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.*;
import java.util.Properties;

import static com.camper.SmartDesktop.Main.*;

public class SaveAndLoadScreen
{
    /**
     * @param event is a close window event and is needed to display a dialog box to ask the user whether he wants to save
     *              the file or not. If the value is null, no dialog box needs to be called and saving will happen forcibly,
     *              without user confirmation
     *
     *              Представляет собой событие закрытия окна и нужен чтобы выдать диалоговое окно для того, чтобы спросить
     *              у пользователя желает ли он сохранить файл или нет. Если значение равно null, то никакое диалоговое окно вызывать не
     *              нужно и сохранения произойдёт принудительно, без подтверждения от пользователя
     */
    public static void saveAll(WindowEvent event) throws ParserConfigurationException, TransformerException, IOException
    {
        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        var doc = builder.newDocument();

        var rootDocument = doc.createElement("save");
        doc.appendChild(rootDocument);

        Note.addNotesToXML(doc, false);

        var t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT,"yes"); //Отступ
        t.setOutputProperty(OutputKeys.METHOD,"xml");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        String filename = "";
        if (event==null)
        {
            filename=currencySaveName;
        }
        else
        {
            var alert = new Alert(Alert.AlertType.NONE, "Сохранить изменения?", new ButtonType("Сохранить", ButtonBar.ButtonData.YES),new ButtonType("Не сохранять", ButtonBar.ButtonData.NO),new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE));
            var alertResult = alert.showAndWait().orElse(ButtonType.CANCEL);
            if (alertResult.getButtonData() == ButtonBar.ButtonData.YES) { filename = currencySaveName; }
            if (alertResult.getButtonData() == ButtonBar.ButtonData.NO) { return; }
            if (alertResult.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) { event.consume(); return;}
        }


        saveInfo.setProperty("lastSaveName",filename);
        saveInfo.store(new FileOutputStream(DIRPATH+"\\Resources\\Saves\\saveInfo.properties"),"Info of latest save");

        t.transform(new DOMSource(doc), new StreamResult(Files.newOutputStream(Paths.get(DIRPATH + "\\Resources\\Saves\\" + filename))));
    }

    public static String loadSave(String saveNameFromChoiceBox) throws Exception
    {
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

                if (!filename.equals(""))
                {
                    var doc = builder.parse(DIRPATH + "\\Resources\\Saves\\" + filename);
                    Note.loadNotesFromXML(doc,xPath);
                }

            }
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
        saves.setValue(saveInfo.getProperty("lastSaveName"));
    }

    private static void createEmptyXML(String filename) throws ParserConfigurationException, TransformerException, IOException
    {
        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        var doc = builder.newDocument();

        var rootDocument = doc.createElement("save");
        doc.appendChild(rootDocument);

        Note.addNotesToXML(doc, true);

        var t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT,"yes"); //Отступ
        t.setOutputProperty(OutputKeys.METHOD,"xml");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        t.transform(new DOMSource(doc), new StreamResult(Files.newOutputStream(Paths.get(DIRPATH + "\\Resources\\Saves\\" + filename))));
    }

    public static String addNewSaveFile() throws IOException, TransformerException, ParserConfigurationException
    {
        String filename;
        for (int id=1;;)
        {
            filename = "save"+id+".xml";
            if (Files.exists(Paths.get(DIRPATH+"\\Resources\\Saves\\"+filename), LinkOption.NOFOLLOW_LINKS))
            { id++; }
            else {createEmptyXML(filename); break;}
        }
        return filename;
    }
}
