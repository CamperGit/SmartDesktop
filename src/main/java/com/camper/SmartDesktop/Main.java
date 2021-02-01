package com.camper.SmartDesktop;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException
    {
        var loader = Main.class.getClassLoader();
        Parent root = FXMLLoader.load(Objects.requireNonNull(loader.getResource("com/camper/SmartDesktop/StartScreen.fxml")));
        System.out.println(System.getProperty("user.dir"));
        var scene = new Scene(root,300,275);
        stage.setScene(scene);
        stage.setTitle("SmartDesktop");
        stage.show();
    }
}