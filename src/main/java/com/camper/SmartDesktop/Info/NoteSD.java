package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import java.net.URL;
import java.util.*;

import static com.camper.SmartDesktop.Main.*;

public class NoteSD extends Application implements Initializable
{
    @FXML private ImageView closeButtonImage;
    @FXML private ToolBar noteToolBar;
    @FXML private TextArea noteTextArea;
    @FXML private Button noteTestButton;
    @FXML private Button noteCloseButton;
    private boolean load=false;
    private AnchorPane NoteRoot;
    private int id;
    private static AnchorPane selectedNote;
    private static Map<Integer,NoteSD> notes = new HashMap<>();
    private static int nextId=1;

    public NoteSD() { }
    private NoteSD(boolean load) { this.load=load; }

    private AnchorPane getNoteRoot() { return NoteRoot; }

    public static void clearSaveList() { notes.clear(); nextId=1;}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        NoteRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/noteRu.fxml")));
        NoteRoot.setLayoutX(80);
        NoteRoot.setLayoutY(30);
        this.id=nextId;
        nextId++;
        notes.put(this.id,this);
        NoteRoot.setAccessibleHelp(String.valueOf(this.id));
        addChild(NoteRoot);
        if (!load)
        {
            NoteRoot.setAccessibleText(String.valueOf(idOfSelectedTab));
            var elementsOfSelectedTab = tabs.get(idOfSelectedTab);
            elementsOfSelectedTab.add(NoteRoot);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        var image = new Image("Images/closeButton40.png",40,40,false,false);
        closeButtonImage.setImage(image);

        noteTestButton.setOnAction(event ->
        {
            noteTextArea.appendText("Test text!");
        });

        noteCloseButton.setOnAction(event ->
        {
            selectedNote = (AnchorPane) (((Button) event.getSource()).getParent());
            notes.remove(Integer.parseInt(selectedNote.getAccessibleHelp()));
            Main.root.getChildren().remove(selectedNote);
        });

        noteToolBar.setOnMouseDragged(event ->
        {
            selectedNote = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            NodeDragger.addDraggingProperty(selectedNote,event);
        });
    }

    public static void addNotesToXML(Document doc, boolean createEmptyXML)
    {
        var rootElement = doc.getFirstChild();

        var notesElement = doc.createElement("notes");
        rootElement.appendChild(notesElement);
        if (!createEmptyXML)
        {
            int id=1;
            for (var entry : notes.entrySet())
            {
                var noteSD = notes.get(entry.getKey());
                var note = noteSD.getNoteRoot();
                var noteElement = doc.createElement("note" + id);
                //Получить значение таба, при котором был создан элемент
                noteElement.setAttribute("tab",note.getAccessibleText());

                notesElement.appendChild(noteElement);

                var visibilityElement = doc.createElement("visibility");
                noteElement.appendChild(visibilityElement);
                var visibilityValue = doc.createTextNode(String.valueOf(note.isVisible()));
                visibilityElement.appendChild(visibilityValue);

                var layoutElement = doc.createElement("layout");
                noteElement.appendChild(layoutElement);

                var layoutX = doc.createElement("layoutX" );
                layoutElement.appendChild(layoutX);
                var layoutXValue = doc.createTextNode(String.valueOf((int)(note.getLayoutX())));
                layoutX.appendChild(layoutXValue);

                var layoutY = doc.createElement("layoutY" );
                layoutElement.appendChild(layoutY);
                var layoutYValue = doc.createTextNode(String.valueOf((int)(note.getLayoutY())));
                layoutY.appendChild(layoutYValue);

                var textElement = doc.createElement("text");
                noteElement.appendChild(textElement);
                for (Node node : note.getChildren())
                {
                    if(node instanceof TextArea)
                    {
                        String text = ((TextArea) node).getText();
                        var textElementValue = doc.createTextNode(text);
                        textElement.appendChild(textElementValue);
                    }
                }
                id++;
            }
        }
    }

    public static void loadNotesFromXML(Document doc, XPath xPath) throws Exception
    {
        int numberOfNotes = xPath.evaluateExpression("count(/save/notes/*)",doc,Integer.class);
        for (int id = 1; id < numberOfNotes+1; id++)
        {
            var loadingNote = new NoteSD(true);
            loadingNote.start(Main.Stage);
            var rootOfLoadingNote = loadingNote.getNoteRoot();

            int numberOfTab = Integer.parseInt (xPath.evaluate("/save/notes/note"+id+"/@tab",doc));
            //Установить в созданный элемент дополнительный текст, в котором будет лежать значение того таба, на котором элемент был создан
            rootOfLoadingNote.setAccessibleText(String.valueOf(numberOfTab));

            var tab = tabs.get(numberOfTab);
            tab.add(rootOfLoadingNote);
            boolean visibility = Boolean.parseBoolean(xPath.evaluate("/save/notes/note"+id+"/visibility/text()",doc));
            rootOfLoadingNote.setVisible(visibility);

            double layoutX = Double.parseDouble (xPath.evaluate("/save/notes/note"+id+"/layout/layoutX/text()",doc));
            double layoutY = Double.parseDouble (xPath.evaluate("/save/notes/note"+id+"/layout/layoutY/text()",doc));
            rootOfLoadingNote.setLayoutX(layoutX);
            rootOfLoadingNote.setLayoutY(layoutY);

            for (Node node : rootOfLoadingNote.getChildren())
            {
                if(node instanceof TextArea)
                {
                    String text = xPath.evaluate("save/notes/note"+id+"/text/text()",doc);
                    ((TextArea) node).setText(text);
                }
            }
        }
    }
}