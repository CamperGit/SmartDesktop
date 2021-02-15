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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

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
    private static AnchorPane selectedNote;
    private static List<AnchorPane> notes = new ArrayList<>();

    public NoteSD() {}
    private NoteSD(boolean load)
    {
        this.load=load;
    }

    private AnchorPane getNoteRoot() { return NoteRoot; }

    public static void clearSaveList() { notes.clear(); }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        NoteRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/noteRu.fxml")));
        NoteRoot.setLayoutX(80);
        NoteRoot.setLayoutY(30);
        notes.add(NoteRoot);
        addChild(NoteRoot);
        if (!load)
        {
            //Установить в созданный элемент дополнительный текст, в котором будет лежать значение того таба, на котором элемент был создан
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


        //noteCloseButton.graphicProperty().setValue(new ImageView("Images/closeButton28.png"));
        noteCloseButton.setOnAction(event ->
        {
            selectedNote = (AnchorPane) (((Button) event.getSource()).getParent());
            notes.remove(selectedNote);
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
            int noteNumber=1;
            for (AnchorPane note : notes)
            {

                var noteElement = doc.createElement("note" + noteNumber);
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
                noteNumber++;
            }
        }
    }

    public static void loadNotesFromXML(Document doc, XPath xPath) throws Exception
    {
        int numberOfNotes = xPath.evaluateExpression("count(/save/notes/*)",doc,Integer.class);
        for (int noteNumber = 1; noteNumber < numberOfNotes+1; noteNumber++)
        {
            var loadingNote = new NoteSD(true);
            loadingNote.start(Main.Stage);
            var rootOfLoadingNote = loadingNote.getNoteRoot();

            int numberOfTab = Integer.parseInt (xPath.evaluate("/save/notes/note"+noteNumber+"/@tab",doc));
            //Установить в созданный элемент дополнительный текст, в котором будет лежать значение того таба, на котором элемент был создан
            rootOfLoadingNote.setAccessibleText(String.valueOf(numberOfTab));

            var tab = tabs.get(numberOfTab);
            tab.add(rootOfLoadingNote);
            boolean visibility = Boolean.parseBoolean(xPath.evaluate("/save/notes/note"+noteNumber+"/visibility/text()",doc));
            rootOfLoadingNote.setVisible(visibility);

            double layoutX = Double.parseDouble (xPath.evaluate("/save/notes/note"+noteNumber+"/layout/layoutX/text()",doc));
            double layoutY = Double.parseDouble (xPath.evaluate("/save/notes/note"+noteNumber+"/layout/layoutY/text()",doc));
            rootOfLoadingNote.setLayoutX(layoutX);
            rootOfLoadingNote.setLayoutY(layoutY);

            for (Node node : rootOfLoadingNote.getChildren())
            {
                if(node instanceof TextArea)
                {
                    String text = xPath.evaluate("save/notes/note"+noteNumber+"/text/text()",doc);
                    ((TextArea) node).setText(text);
                }
            }
        }
    }
}