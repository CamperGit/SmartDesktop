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
    private static double xUndo=0;
    private static double yUndo=0;

    public Note()
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

        noteToolBar.setOnMouseDragged(event ->
        {
            //Сделать проверку, чтобы не наезжало на панель инструментов!!!
            selected = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            if (xUndo==0 && yUndo==0)
            {
                xUndo=event.getX();
                yUndo=event.getY();
                /*selected.setLayoutX((int)xUndo - (int)event.getX());
                selected.setLayoutY((int)yUndo - (int)event.getY());*/
            }
            else
            {
                /*selected.setLayoutX((int)event.getX()-(int)xUndo);
                selected.setLayoutY((int)event.getY()-(int)yUndo);*/
                //int x = (int) ((int)event.getX()-((int)xUndo-selected.getLayoutX()));
                //int y = (int) ((int)event.getY()-((int)yUndo-selected.getLayoutY()));
                selected.setLayoutX((int)event.getX()-((int)xUndo-selected.getLayoutX()));
                selected.setLayoutY((int)event.getY()-((int)yUndo-selected.getLayoutY()));
            }




            /*selected.setLayoutX(event.getX()-(event.getX() - (selected.getLayoutX()+10)));
            selected.setLayoutY(event.getY()-(event.getY() - (selected.getLayoutX()+10)));*/
            /*selected.setTranslateX(event.getX());
            selected.setTranslateY(event.getY());*/
            /*selected.setTranslateY(10);
            selected.setLayoutX(selected.getTranslateX());
            selected.setLayoutY(event.getY()-selected.getLayoutY());
            selected.getTranslateX()*/
        });

    }
}

class LastCord
{
    private static double xUndo;
}