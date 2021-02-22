package com.camper.SmartDesktop;

import com.camper.SmartDesktop.Info.CalendarSD;
import com.camper.SmartDesktop.Info.NoteSD;
import com.camper.SmartDesktop.Info.ScheduleSD;
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
                    }
                } else
                {
                    if (Files.exists(Paths.get(DIRPATH + "\\Resources\\Saves\\" + saveNameFromChoiceBox)))
                    {
                        filename = saveNameFromChoiceBox;
                    }
                }

                Document doc;
                if (!filename.equals(""))
                {
                    doc = builder.parse(DIRPATH + "\\Resources\\Saves\\" + filename);
                }
                //�������� ��������, ���� � ��� ��� ������� �������� �� ��������� �����������, ��� ���������� ���������� ���.
                //����� �� ��������� ������ ����������, ������� ����� �����, ������ ��� �� ��� ������ ��� �������
                else if (Files.exists(Paths.get(DIRPATH + "\\Resources\\Saves\\" + saveInfo.getProperty("lastSaveName"))))
                {
                    var alert = new Alert(Alert.AlertType.WARNING, "��������� ���������� ���� ������� ��� �������������. �������� ��������", ButtonType.OK);
                    alert.showAndWait();

                    filename = saveInfo.getProperty("lastSaveName");
                    assert saves != null;
                    saves.getItems().remove(saveNameFromChoiceBox);

                    doc = builder.parse(DIRPATH + "\\Resources\\Saves\\" + saveInfo.getProperty("lastSaveName"));
                }
                //�������� ��������, ���� � ��� ��� ���������� �������� ����������� ���� ���������� ����������.
                //����� �� ������ ����� ������ ���������� � ��������� ���.
                else
                {
                    var alert = new Alert(Alert.AlertType.WARNING, "��������� ���������� ���� ������� ��� �������������. �������� ��������", ButtonType.OK);
                    alert.showAndWait();

                    filename = addNewSaveFile();

                    doc = builder.parse(DIRPATH + "\\Resources\\Saves\\" + filename);
                    saveInfo.setProperty("lastSaveName", filename);
                    saveInfo.store(new FileOutputStream(DIRPATH + "\\Resources\\Saves\\saveInfo.properties"), "Info of latest save");
                }

                NoteSD.loadNotesFromXML(doc, xPath);
                ScheduleSD.loadSchedulesFromXML(doc, xPath);
                //��������� ������ ������ ��������� ���������!!!
                CalendarSD.loadCalendarFromXML(doc, xPath);

                idOfSelectedTab = Integer.parseInt(xPath.evaluate("save/lastTab/@tab", doc));
            }
            //�������� ������� ����� ���������� ��� ����� ������ ������� ��� � ����� ���� ���� properties, �� ��� ������ ������
            else
            {
                filename = "save1.xml";
                createEmptyXML(filename);
                saveInfo.setProperty("lastSaveName", filename);
                saveInfo.store(new FileOutputStream(DIRPATH + "\\Resources\\Saves\\saveInfo.properties"), "Info of latest save");
                runEventTask();
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
