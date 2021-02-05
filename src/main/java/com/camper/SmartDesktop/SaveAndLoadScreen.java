package com.camper.SmartDesktop;

import com.sun.scenario.Settings;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

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
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import static com.camper.SmartDesktop.Main.*;

public class SaveAndLoadScreen
{
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


        String filename = "";
        String mess = "Да";
        String encodedMess = URLEncoder.encode(mess, "UTF-8");

        var alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save?", new ButtonType(encodedMess, ButtonBar.ButtonData.YES),ButtonType.NO,ButtonType.CANCEL);
        ButtonType alertResult = alert.showAndWait().get();
        if (alertResult.getButtonData() == ButtonBar.ButtonData.YES);
        {
            filename = lastSaveName.getProperty("lastSaveName");
        }


        /*var dialog = new FileChooser();
        dialog.setInitialDirectory(new File(DIRPATH+"\\Resources\\Saves\\"));
        dialog.setInitialFileName("save.xml");
        var result = dialog.showSaveDialog(Stage);
        String filename = "";
        if (result == null)
        {
            filename= lastSaveName.getProperty("lastSaveName");
        }
        else
        {
            filename = result.getName();
        }*/

        lastSaveName.setProperty("lastSaveName",filename);
        lastSaveName.store(new FileOutputStream(DIRPATH+"\\Resources\\Saves\\latestSave.properties"),"Info of latest save");

        t.transform(new DOMSource(doc), new StreamResult(Files.newOutputStream(Paths.get(DIRPATH + "\\Resources\\Saves\\" + filename))));
    }

    public static void loadAll(String saveNameFromChoiceBox) throws Exception
    {
        var folderWithSaves = new File(DIRPATH + "\\Resources\\Saves");



        File[] contents = folderWithSaves.listFiles();
        if (contents!=null && contents.length!=0 )
        {
            int numberOfSaves=0;
            for (File file : contents)
            {
                if (file.getAbsolutePath().endsWith(".xml"))
                {
                    numberOfSaves++;
                    break;
                }
            }
            if (numberOfSaves!=0)
            {
                var factory = DocumentBuilderFactory.newInstance();
                final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
                final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
                factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
                var builder = factory.newDocumentBuilder();
                var xPathFactory = XPathFactory.newInstance();
                var xPath = xPathFactory.newXPath();

                String filename="";
                if (saveNameFromChoiceBox==null)
                {
                    if (Files.exists(Paths.get(DIRPATH + "\\Resources\\Saves\\"+lastSaveName.getProperty("lastSaveName"))))
                    {
                        filename = lastSaveName.getProperty("lastSaveName");
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


                lastSaveName.setProperty("lastSaveName",filename);
                lastSaveName.store(new FileOutputStream(DIRPATH+"\\Resources\\Saves\\latestSave.properties"),"Info of latest save");
            }
        }
    }
}
