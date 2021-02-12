package com.camper.SmartDesktop.Info;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.camper.SmartDesktop.Main.*;

public class CompletedEvents extends Application implements Initializable
{
    private static AnchorPane checkNotificationRoot;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        checkNotificationRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/completedEvents.fxml")));
        checkNotificationRoot.setLayoutX(DEFAULT_WIDTH-512);
        checkNotificationRoot.setLayoutY(25);
        addChild(checkNotificationRoot);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }
}
