package com.camper.SmartDesktop;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.w3c.dom.Document;

import javax.tools.Tool;
import javax.xml.xpath.XPath;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.camper.SmartDesktop.Main.addChild;
import static com.camper.SmartDesktop.Main.mainCL;

public class Note extends Application implements Initializable
{
    @FXML private ToolBar noteToolBar;
    @FXML private TextArea noteTextArea;
    @FXML private Button noteTestButton;
    @FXML private Button noteCloseButton;
    private AnchorPane root;
    private static AnchorPane selected;
    private static List<AnchorPane> notes = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        root = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/noteRu.fxml")));
        root.setLayoutX(80);
        root.setLayoutY(30);
        notes.add(root);
        addChild(root);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        noteTestButton.setOnAction(event ->
        {
            noteTextArea.appendText("Test text!");
        });


        noteCloseButton.graphicProperty().setValue(new ImageView("Images/closeButton.png"));
        noteCloseButton.setOnAction(event ->
        {
            selected = (AnchorPane) (((Button) event.getSource()).getParent());
            notes.remove(selected);
            selected.setVisible(false);
        });

        noteToolBar.setOnMouseDragged(event ->
        {
            selected = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            NodeDragger.doDragging(selected,event);
        });
    }

    private AnchorPane getRoot() { return root; }

    public static void clearSaveList() { notes.clear(); }

    public static void addNotesToXML(Document doc, boolean createEmptyXML)
    {
        var rootElement = doc.getFirstChild();

        var notesElement = doc.createElement("notes");
        rootElement.appendChild(notesElement);
        if (!createEmptyXML)
        {
            int id=1;
            for (AnchorPane note : notes)
            {
                var noteElement = doc.createElement("note" + id);
                notesElement.appendChild(noteElement);

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
            var loadingNote = new Note();
            loadingNote.start(Main.Stage);
            AnchorPane root = loadingNote.getRoot();

            double layoutX = Double.parseDouble (xPath.evaluate("/save/notes/note"+id+"/layout/layoutX/text()",doc));
            double layoutY = Double.parseDouble (xPath.evaluate("/save/notes/note"+id+"/layout/layoutY/text()",doc));
            root.setLayoutX(layoutX);
            root.setLayoutY(layoutY);

            for (Node node : root.getChildren())
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