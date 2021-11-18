package com.camper.SmartDesktop;

import com.camper.SmartDesktop.Info.*;
import com.camper.SmartDesktop.StandardElements.TableSD;
import com.camper.SmartDesktop.StandardElements.Weather;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.WindowEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

import static com.camper.SmartDesktop.Info.UpcomingEvent.disableEventQueue;
import static com.camper.SmartDesktop.Main.*;
import static com.camper.SmartDesktop.Main.DIRPATH;

public class Saving
{
    /**
     * @param event is a close window event and is needed to display a dialog box to ask the user whether he wants to save
     *              the file or not. If the value is null, no dialog box needs to be called and saving will happen forcibly,
     *              without user confirmation
     *              <p>
     *              ������������ ����� ������� �������� ���� � ����� ����� ������ ���������� ���� ��� ����, ����� ��������
     *              � ������������ ������ �� �� ��������� ���� ��� ���. ���� �������� ����� null, �� ������� ���������� ���� �������� ��
     *              ����� � ���������� ��������� �������������, ��� ������������� �� ������������
     */
    public static void saveAll(WindowEvent event) throws ParserConfigurationException, TransformerException, IOException, InterruptedException
    {
        logger.info("Saving: start saving all");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element rootDocument = doc.createElement("save");
        doc.appendChild(rootDocument);

        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty(OutputKeys.METHOD, "xml");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        String filename = "";
        if (event == null)
        {
            filename = currencySaveName;
        } else
        {
            Alert alert;
            if (defaultLocale.equals(Locale.ENGLISH))
            {
                alert = new Alert(Alert.AlertType.NONE, languageBundle.getString("savingSaveChangesAlert"), new ButtonType("Save", ButtonBar.ButtonData.YES), new ButtonType("Do not save", ButtonBar.ButtonData.NO), new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE));
            }
            else
            {
                alert = new Alert(Alert.AlertType.NONE, languageBundle.getString("savingSaveChangesAlert"), new ButtonType("���������", ButtonBar.ButtonData.YES), new ButtonType("�� ���������", ButtonBar.ButtonData.NO), new ButtonType("������", ButtonBar.ButtonData.CANCEL_CLOSE));
            }
            ButtonType alertResult = alert.showAndWait().orElse(ButtonType.CANCEL);
            if (alertResult.getButtonData() == ButtonBar.ButtonData.YES)
            {
                filename = currencySaveName;

            }
            if (alertResult.getButtonData() == ButtonBar.ButtonData.NO)
            {
                disableEventQueue(true);
                logger.info("Saving: saving was canceled");
                return;
            }
            if (alertResult.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE)
            {
                logger.info("Saving: exit was interrupted");
                event.consume();
                return;
            }

        }

        logger.info("Saving: start saving elements");
        NoteSD.addNotesToXML(doc, false);
        ScheduleSD.addSchedulesToXML(doc, false);
        GoalSD.addGoalsToXML(doc, false);
        TableSD.addTablesToXML(doc, false);
        Weather.addWeatherInfoToXML(doc, false);
        UpcomingEvent.addUpcomingEventInfoToXML(doc, false);
        CalendarSD.addCalendarToXML(doc, false);
        logger.info("Saving: end saving elements");

        Node rootElement = doc.getFirstChild();
        Element lastTabElement = doc.createElement("lastTab");
        lastTabElement.setAttribute("tab", String.valueOf(idOfSelectedTab));
        rootElement.appendChild(lastTabElement);

        saveInfo.setProperty("lastSaveName", filename);
        saveInfo.setProperty("language", defaultLocale.getLanguage());
        saveInfo.store(new FileOutputStream(DIRPATH + "\\Resources\\Saves\\saveInfo.properties"), "Info of latest save");
        logger.info("Saving: create new saveInfo.properties");

        t.transform(new DOMSource(doc), new StreamResult(Files.newOutputStream(Paths.get(DIRPATH + "\\Resources\\Saves\\" + filename))));
        logger.info("Saving: create save file");
        if (event != null)
        {
            disableEventQueue(true);
        } //���� ������� �� �������� ����� �� ���� � ��� �� ������, ������ ������������
        //���������� ���������� � ����� �����.
        logger.info("Saving: end saving all");
    }

    public static void createEmptyXML(String filename) throws ParserConfigurationException, TransformerException, IOException
    {
        logger.info("Saving: start creating new empty xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element rootDocument = doc.createElement("save");
        doc.appendChild(rootDocument);

        logger.info("Saving: start creating new empty xml elements on save file");
        NoteSD.addNotesToXML(doc, true);
        ScheduleSD.addSchedulesToXML(doc, true);
        GoalSD.addGoalsToXML(doc, true);
        TableSD.addTablesToXML(doc, true);
        Weather.addWeatherInfoToXML(doc, true);
        UpcomingEvent.addUpcomingEventInfoToXML(doc, true);
        CalendarSD.addCalendarToXML(doc, true);
        logger.info("Saving: end creating new empty xml elements on save file");

        Node rootElement = doc.getFirstChild();
        Element lastTabElement = doc.createElement("lastTab");
        lastTabElement.setAttribute("tab", "1");
        rootElement.appendChild(lastTabElement);

        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes"); //������
        t.setOutputProperty(OutputKeys.METHOD, "xml");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        t.transform(new DOMSource(doc), new StreamResult(Files.newOutputStream(Paths.get(DIRPATH + "\\Resources\\Saves\\" + filename))));
        logger.info("Saving: end creating new empty xml");
    }

    //�������� ������ �������
    public static String addNewSaveFile() throws IOException, TransformerException, ParserConfigurationException
    {
        String filename;
        for (int id = 1; ; )
        {
            filename = "save" + id + ".xml";
            if (Files.exists(Paths.get(DIRPATH + "\\Resources\\Saves\\" + filename)))
            {
                id++;
            } else
            {
                createEmptyXML(filename);
                break;
            }
        }
        return filename;
    }
}
