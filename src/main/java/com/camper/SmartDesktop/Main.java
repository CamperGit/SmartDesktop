package com.camper.SmartDesktop;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application
{
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) throws IOException
    {
        var loader = Main.class.getClassLoader();
        Parent root = FXMLLoader.load(Objects.requireNonNull(loader.getResource("FXMLs/StartScreen.fxml")));
        var kit = Toolkit.getDefaultToolkit();
        var screenSize = kit.getScreenSize();
        var scene = new Scene(root,screenSize.width,screenSize.height-66);
        stage.setScene(scene);
        stage.setTitle("SmartDesktop");
        stage.show();
    }
}