package com.camper.SmartDesktop;

import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;

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
import static com.camper.SmartDesktop.Saving.createEmptyXML;

public class Loading
{
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
                    idOfSelectedTab = Integer.parseInt(xPath.evaluate("save/lastTab/@tab",doc));
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
