package com.camper.SmartDesktop;

import com.camper.SmartDesktop.Info.CalendarSD;
import com.camper.SmartDesktop.Info.NoteSD;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.WindowEvent;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.camper.SmartDesktop.Info.UpcomingEvent.disableEventQueue;
import static com.camper.SmartDesktop.Info.UpcomingEvent.executorService;
import static com.camper.SmartDesktop.Main.*;
import static com.camper.SmartDesktop.Main.DIRPATH;

public class Saving
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
    public static void saveAll(WindowEvent event) throws ParserConfigurationException, TransformerException, IOException, InterruptedException
    {
        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        var doc = builder.newDocument();

        var rootDocument = doc.createElement("save");
        doc.appendChild(rootDocument);

        NoteSD.addNotesToXML(doc, false);
        CalendarSD.addCalendarToXML(doc,false);

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
            if (alertResult.getButtonData() == ButtonBar.ButtonData.YES)
            {
                filename = currencySaveName;

            }
            if (alertResult.getButtonData() == ButtonBar.ButtonData.NO) { disableEventQueue(true); return; }
            if (alertResult.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) { event.consume(); return;}

        }

        var rootElement = doc.getFirstChild();
        var lastTabElement = doc.createElement("lastTab");
        lastTabElement.setAttribute("tab", String.valueOf(idOfSelectedTab));
        rootElement.appendChild(lastTabElement);

        saveInfo.setProperty("lastSaveName",filename);
        saveInfo.store(new FileOutputStream(DIRPATH+"\\Resources\\Saves\\saveInfo.properties"),"Info of latest save");

        t.transform(new DOMSource(doc), new StreamResult(Files.newOutputStream(Paths.get(DIRPATH + "\\Resources\\Saves\\" + filename))));
        if(event!=null){disableEventQueue(true);} //Если событие на закрытие дошло до сюда и оно не пустое, значит пользователь
        //подтвердил сохранение и хочет выйти.
    }

    public static void createEmptyXML(String filename) throws ParserConfigurationException, TransformerException, IOException
    {
        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        var doc = builder.newDocument();

        var rootDocument = doc.createElement("save");
        doc.appendChild(rootDocument);

        NoteSD.addNotesToXML(doc, true);
        CalendarSD.addCalendarToXML(doc,true);

        var rootElement = doc.getFirstChild();
        var lastTabElement = doc.createElement("lastTab");
        lastTabElement.setAttribute("tab", "1");
        rootElement.appendChild(lastTabElement);

        var t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT,"yes"); //Отступ
        t.setOutputProperty(OutputKeys.METHOD,"xml");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        t.transform(new DOMSource(doc), new StreamResult(Files.newOutputStream(Paths.get(DIRPATH + "\\Resources\\Saves\\" + filename))));
    }

    //Создание нового пресета
    public static String addNewSaveFile() throws IOException, TransformerException, ParserConfigurationException
    {
        String filename;
        for (int id=1;;)
        {
            filename = "save"+id+".xml";
            if (Files.exists(Paths.get(DIRPATH+"\\Resources\\Saves\\"+filename)))
            { id++; }
            else {createEmptyXML(filename); break;}
        }
        return filename;
    }
}
