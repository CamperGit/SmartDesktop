package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.camper.SmartDesktop.Main.*;

public class SchedulerCopySettings extends Application implements Initializable
{
    private AnchorPane CopySettingsRoot;
    private MouseEvent mouseEvent;

    public SchedulerCopySettings(){}
    public SchedulerCopySettings(MouseEvent mouseEvent, Day dayWithEvents )
    {
        this.mouseEvent=mouseEvent;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        CopySettingsRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/scheduleSettingsRu.fxml")));
        int leftUpperCornerX = (int) (mouseEvent.getSceneX()-mouseEvent.getX())-314;//314 - Расстояние от края кнопки до края элемента
        int leftUpperCornerY = (int) (mouseEvent.getSceneY()-mouseEvent.getY());
        int layoutX=leftUpperCornerX+460;
        int width = 246;

        if (layoutX+width>DEFAULT_WIDTH)
        {
            layoutX=layoutX-460-width;
        }
        CopySettingsRoot.setLayoutX(layoutX);
        CopySettingsRoot.setLayoutY(leftUpperCornerY);
        addChild(CopySettingsRoot);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }
}
