package com.camper.SmartDesktop;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.tools.Tool;
import java.net.URL;
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

    public Note()
    {

    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        AnchorPane root = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/noteRu.fxml")));
        root.setLayoutX(80);
        root.setLayoutY(30);
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
}