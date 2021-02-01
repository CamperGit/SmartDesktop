package com.camper.SmartDesktop;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException
    {
        var root = new Pane();
        //System.out.println(System.getProperty("user.dir"));
        //Parent root = FXMLLoader.load(getClass().getResource("E:\\Programming\\IdeaProjects\\SmartDesktop\\src\\main\\java\\com\\camper\\SmartDesktop\\main.fxml"));
        var scene = new Scene(root,300,275);
        stage.setScene(scene);
        stage.setTitle("MouseTest");
        stage.show();
    }
}