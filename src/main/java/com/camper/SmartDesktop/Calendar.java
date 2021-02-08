package com.camper.SmartDesktop;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.camper.SmartDesktop.Main.*;

public class Calendar extends Application implements Initializable
{

    @FXML private ToolBar calendarToolBar;
    @FXML private Button calendarCloseButton;
    @FXML private boolean load=false;
    @FXML private AnchorPane root;
    @FXML private static AnchorPane selected;

    @FXML private Button calendarDay1Button;
    @FXML private ImageView CalendarDay1NotificationIV;
    @FXML private ImageView CalendarDay1TargetIV;
    @FXML private ImageView CalendarDay1ScheduleIV;

    @FXML private Button calendarDay2Button;
    @FXML private ImageView CalendarDay2NotificationIV;
    @FXML private ImageView CalendarDay2TargetIV;
    @FXML private ImageView CalendarDay2ScheduleIV;

    @FXML private Button calendarDay3Button;
    @FXML private ImageView CalendarDay3NotificationIV;
    @FXML private ImageView CalendarDay3TargetIV;
    @FXML private ImageView CalendarDay3ScheduleIV;

    @FXML private Button calendarDay4Button;
    @FXML private ImageView CalendarDay4NotificationIV;
    @FXML private ImageView CalendarDay4TargetIV;
    @FXML private ImageView CalendarDay4ScheduleIV;

    @FXML private Button calendarDay5Button;
    @FXML private ImageView CalendarDay5NotificationIV;
    @FXML private ImageView CalendarDay5TargetIV;
    @FXML private ImageView CalendarDay5ScheduleIV;

    @FXML private Button calendarDay6Button;
    @FXML private ImageView CalendarDay6NotificationIV;
    @FXML private ImageView CalendarDay6TargetIV;
    @FXML private ImageView CalendarDay6ScheduleIV;

    @FXML private Button calendarDay7Button;
    @FXML private ImageView CalendarDay7NotificationIV;
    @FXML private ImageView CalendarDay7TargetIV;
    @FXML private ImageView CalendarDay7ScheduleIV;

    @FXML private Button calendarDay8Button;
    @FXML private ImageView CalendarDay8NotificationIV;
    @FXML private ImageView CalendarDay8TargetIV;
    @FXML private ImageView CalendarDay8ScheduleIV;

    @FXML private Button calendarDay9Button;
    @FXML private ImageView CalendarDay9NotificationIV;
    @FXML private ImageView CalendarDay9TargetIV;
    @FXML private ImageView CalendarDay9ScheduleIV;

    @FXML private Button calendarDay10Button;
    @FXML private ImageView CalendarDay10NotificationIV;
    @FXML private ImageView CalendarDay10TargetIV;
    @FXML private ImageView CalendarDay10ScheduleIV;

    @FXML private Button calendarDay11Button;
    @FXML private ImageView CalendarDay11NotificationIV;
    @FXML private ImageView CalendarDay11TargetIV;
    @FXML private ImageView CalendarDay11ScheduleIV;

    @FXML private Button calendarDay12Button;
    @FXML private ImageView CalendarDay12NotificationIV;
    @FXML private ImageView CalendarDay12TargetIV;
    @FXML private ImageView CalendarDay12ScheduleIV;

    @FXML private Button calendarDay13Button;
    @FXML private ImageView CalendarDay13NotificationIV;
    @FXML private ImageView CalendarDay13TargetIV;
    @FXML private ImageView CalendarDay13ScheduleIV;

    @FXML private Button calendarDay14Button;
    @FXML private ImageView CalendarDay14NotificationIV;
    @FXML private ImageView CalendarDay14TargetIV;
    @FXML private ImageView CalendarDay14ScheduleIV;

    @FXML private Button calendarDay15Button;
    @FXML private ImageView CalendarDay15NotificationIV;
    @FXML private ImageView CalendarDay15TargetIV;
    @FXML private ImageView CalendarDay15ScheduleIV;

    @FXML private Button calendarDay16Button;
    @FXML private ImageView CalendarDay16NotificationIV;
    @FXML private ImageView CalendarDay16TargetIV;
    @FXML private ImageView CalendarDay16ScheduleIV;

    @FXML private Button calendarDay17Button;
    @FXML private ImageView CalendarDay17NotificationIV;
    @FXML private ImageView CalendarDay17TargetIV;
    @FXML private ImageView CalendarDay17ScheduleIV;

    @FXML private Button calendarDay18Button;
    @FXML private ImageView CalendarDay18NotificationIV;
    @FXML private ImageView CalendarDay18TargetIV;
    @FXML private ImageView CalendarDay18ScheduleIV;

    @FXML private Button calendarDay19Button;
    @FXML private ImageView CalendarDay19NotificationIV;
    @FXML private ImageView CalendarDay19TargetIV;
    @FXML private ImageView CalendarDay19ScheduleIV;

    @FXML private Button calendarDay20Button;
    @FXML private ImageView CalendarDay20NotificationIV;
    @FXML private ImageView CalendarDay20TargetIV;
    @FXML private ImageView CalendarDay20ScheduleIV;

    @FXML private Button calendarDay21Button;
    @FXML private ImageView CalendarDay21NotificationIV;
    @FXML private ImageView CalendarDay21TargetIV;
    @FXML private ImageView CalendarDay21ScheduleIV;

    @FXML private Button calendarDay22Button;
    @FXML private ImageView CalendarDay22NotificationIV;
    @FXML private ImageView CalendarDay22TargetIV;
    @FXML private ImageView CalendarDay22ScheduleIV;

    @FXML private Button calendarDay23Button;
    @FXML private ImageView CalendarDay23NotificationIV;
    @FXML private ImageView CalendarDay23TargetIV;
    @FXML private ImageView CalendarDay23ScheduleIV;

    @FXML private Button calendarDay24Button;
    @FXML private ImageView CalendarDay24NotificationIV;
    @FXML private ImageView CalendarDay24TargetIV;
    @FXML private ImageView CalendarDay24ScheduleIV;

    @FXML private Button calendarDay25Button;
    @FXML private ImageView CalendarDay25NotificationIV;
    @FXML private ImageView CalendarDay25TargetIV;
    @FXML private ImageView CalendarDay25ScheduleIV;

    @FXML private Button calendarDay26Button;
    @FXML private ImageView CalendarDay26NotificationIV;
    @FXML private ImageView CalendarDay26TargetIV;
    @FXML private ImageView CalendarDay26ScheduleIV;

    @FXML private Button calendarDay27Button;
    @FXML private ImageView CalendarDay27NotificationIV;
    @FXML private ImageView CalendarDay27TargetIV;
    @FXML private ImageView CalendarDay27ScheduleIV;

    @FXML private Button calendarDay28Button;
    @FXML private ImageView CalendarDay28NotificationIV;
    @FXML private ImageView CalendarDay28TargetIV;
    @FXML private ImageView CalendarDay28ScheduleIV;

    @FXML private Button calendarDay29Button;
    @FXML private ImageView CalendarDay29NotificationIV;
    @FXML private ImageView CalendarDay29TargetIV;
    @FXML private ImageView CalendarDay29ScheduleIV;

    @FXML private Button calendarDay30Button;
    @FXML private ImageView CalendarDay30NotificationIV;
    @FXML private ImageView CalendarDay30TargetIV;
    @FXML private ImageView CalendarDay30ScheduleIV;

    @FXML private Button calendarDay31Button;
    @FXML private ImageView CalendarDay31NotificationIV;
    @FXML private ImageView CalendarDay31TargetIV;
    @FXML private ImageView CalendarDay31ScheduleIV;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        //root = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/calendarRu.fxml")));
        root = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/calendarRu.fxml")));
        root.setLayoutX(80);
        root.setLayoutY(30);
        addChild(root);
        /*if (!load)
        {
            //���������� � ��������� ������� �������������� �����, � ������� ����� ������ �������� ���� ����, �� ������� ������� ��� ������
            root.setAccessibleText(String.valueOf(idOfSelectedTab));
            var elementsOfSelectedTab = tabs.get(idOfSelectedTab);
            elementsOfSelectedTab.add(root);
        }*/
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        var imageNotification = new Image("Images/notification14.png");
        CalendarDay1NotificationIV.setImage(imageNotification);
        var imageTarget = new Image("Images/target14.png");
        CalendarDay1TargetIV.setImage(imageTarget);
        var imageSchedule = new Image("Images/schedule14.png");
        CalendarDay1ScheduleIV.setImage(imageSchedule);

        calendarDay1Button.setOnAction((event ->
        {
            var alert = new Alert(Alert.AlertType.WARNING, "��������� ���������� ���� ������� ��� �������������. �������� ��������", ButtonType.OK);
            alert.showAndWait();
        }));

        calendarCloseButton.setOnAction(event ->
        {
            selected = (AnchorPane) (((Button) event.getSource()).getParent());
            selected.setVisible(false);
            Main.root.getChildren().remove(selected);
        });

        calendarToolBar.setOnMouseDragged(event ->
        {
            selected = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            NodeDragger.addDraggingProperty(selected,event);
        });
    }
}
