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

        var alert = new Alert(Alert.AlertType.NONE, "Сохранить изменения?", new ButtonType("Сохранить", ButtonBar.ButtonData.YES),new ButtonType("Не сохранять", ButtonBar.ButtonData.NO),new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE));
        var alertResult = alert.showAndWait().orElse(ButtonType.CANCEL);
        if (alertResult.getButtonData() == ButtonBar.ButtonData.YES)
        {
            if (saveInfo.getProperty("lastSaveName").equals(""))
            { filename="save1.xml";}
            else { filename = saveInfo.getProperty("lastSaveName");
            }
        }
        if (alertResult.getButtonData() == ButtonBar.ButtonData.NO) { return; }
        if (alertResult.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) { event.consume(); return;}

        saveInfo.setProperty("lastSaveName",filename);
        saveInfo.store(new FileOutputStream(DIRPATH+"\\Resources\\Saves\\saveInfo.properties"),"Info of latest save");

        t.transform(new DOMSource(doc), new StreamResult(Files.newOutputStream(Paths.get(DIRPATH + "\\Resources\\Saves\\" + filename))));
    }

    public static void loadAll(String saveNameFromChoiceBox) throws Exception
    {
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

                String filename="";
                if (saveNameFromChoiceBox==null)
                {
                    if (Files.exists(Paths.get(DIRPATH + "\\Resources\\Saves\\"+saveInfo.getProperty("lastSaveName"))))
                    {
                        filename = saveInfo.getProperty("lastSaveName");
                        saveInfo.setProperty("currencySaveName",filename);
                    }
                }
                else
                {
                    if (Files.exists(Paths.get(DIRPATH + "\\Resources\\Saves\\"+saveNameFromChoiceBox)))
                    {
                        filename = saveNameFromChoiceBox;
                        saveInfo.setProperty("currencySaveName",filename);
                    }
                }

                if (!filename.equals(""))
                {
                    var doc = builder.parse(DIRPATH + "\\Resources\\Saves\\" + filename);
                    Note.loadNotesFromXML(doc,xPath);
                    saveInfo.setProperty("lastSaveName",filename);
                    saveInfo.store(new FileOutputStream(DIRPATH+"\\Resources\\Saves\\saveInfo.properties"),"Info of latest save");
                }
            }
        }
    }

    public static void loadSavesToSavesList(ChoiceBox<String> saves)
    {
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
        saves.setValue(saveInfo.getProperty("currencySaveName"));
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
