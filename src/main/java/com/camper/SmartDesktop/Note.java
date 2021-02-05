package com.camper.SmartDesktop;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.w3c.dom.Document;

import javax.tools.Tool;
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
    private static AnchorPane selected;
    private static List<AnchorPane> notes = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        AnchorPane root = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/noteRu.fxml")));
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

        noteToolBar.setOnMouseDragged(event ->
        {
            selected = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            NodeDragger.doDragging(selected,event);
        });
    }

    public static void addNotesToXML(Document doc)
    {
        var rootElement = doc.getFirstChild();

        var notesElement = doc.createElement("notes");
        rootElement.appendChild(notesElement);
        int id=1;
        for (AnchorPane note : notes)
        {
            var noteElement = doc.createElement("note" + id);
            notesElement.appendChild(noteElement);

            var layoutElement = doc.createElement("layout"+id);
            noteElement.appendChild(layoutElement);

            var layoutX = doc.createElement("layoutX"+id);
            layoutElement.appendChild(layoutX);
            var layoutXValue = doc.createTextNode(String.valueOf((int)(note.getLayoutX())));
            layoutX.appendChild(layoutXValue);

            var layoutY = doc.createElement("layoutY"+id);
            layoutElement.appendChild(layoutY);
            var layoutYValue = doc.createTextNode(String.valueOf((int)(note.getLayoutY())));
            layoutY.appendChild(layoutYValue);

            var textElement = doc.createElement("text"+id);
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