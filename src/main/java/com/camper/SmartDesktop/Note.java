package com.camper.SmartDesktop;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.camper.SmartDesktop.Main.addChild;
import static com.camper.SmartDesktop.Main.mainCL;

public class Note extends Application implements Initializable
{
    @FXML private TextArea noteTextArea;
    @FXML private Button noteTestButton;

    public Note() throws IOException
    {

    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        AnchorPane root = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/noteRu.fxml")));
        addChild(root);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        noteTestButton.setOnAction(event ->
        {
            noteTextArea.appendText("Test text!");
        });
    }
}
