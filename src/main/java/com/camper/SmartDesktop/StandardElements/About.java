package com.camper.SmartDesktop.StandardElements;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.camper.SmartDesktop.Main.*;
import static com.camper.SmartDesktop.Main.DEFAULT_HEIGHT;

public class About extends Application implements Initializable
{
    @FXML
    private ToolBar aboutToolBar;
    @FXML
    private Button aboutCloseButton;
    @FXML
    private ImageView aboutVKIV, aboutCodeIV, aboutCloseButtonIV;
    private static AnchorPane AboutRoot = null;

    public static AnchorPane getAboutRoot()
    {
        return AboutRoot;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        logger.info("About: begin start method");
        AboutRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/about.fxml")));
        AboutRoot.setLayoutX(DEFAULT_WIDTH / 2 - 360 / 2);
        AboutRoot.setLayoutY(DEFAULT_HEIGHT / 2 - 188 / 2);
        addChild(AboutRoot);
        logger.info("About: end start method");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        logger.info("About: begin initialize method");
        aboutCloseButton.setOnAction(event ->
        {
            AboutRoot = (AnchorPane) (((Button) event.getSource()).getParent());
            Main.root.getChildren().remove(AboutRoot);
            AboutRoot = null;
            logger.info("About: about window was closed");
        });

        aboutToolBar.setOnMouseDragged(event ->
        {
            AboutRoot = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            NodeDragger.addDraggingProperty(AboutRoot, event);
        });
        aboutVKIV.setImage(new Image("Images/vk35.png"));
        aboutCodeIV.setImage(new Image("Images/github35.png"));
        aboutCloseButtonIV.setImage(new Image("Images/delete30.png"));
        logger.info("About: end initialize method");
    }
}
