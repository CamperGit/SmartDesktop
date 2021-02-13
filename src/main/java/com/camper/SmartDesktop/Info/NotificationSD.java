package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.camper.SmartDesktop.Info.CalendarSD.getDaysWithEvents;
import static com.camper.SmartDesktop.Info.CalendarSD.updateDayIcons;
import static com.camper.SmartDesktop.Info.Day.addEventOfDay;
import static com.camper.SmartDesktop.Main.*;

public class NotificationSD extends Application implements Initializable
{
    @FXML private Button notificationAddButton;
    @FXML private Button notificationCancelButton;
    @FXML private Button notificationCloseButton;
    @FXML private DatePicker notificationDatePicker;
    @FXML private ComboBox<String> notificationComboBoxHours;
    @FXML private ComboBox<String> notificationComboBoxMinutes;
    @FXML private TextArea notificationTextArea;
    @FXML private ToolBar notificationToolBar;

    private AnchorPane NotificationRoot;
    private static AnchorPane selectedNotification;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        NotificationRoot= FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/notificationRu.fxml")));
        NotificationRoot.setLayoutX(DEFAULT_WIDTH/2-340/2);
        NotificationRoot.setLayoutY(DEFAULT_HEIGHT/2-248/2);
        addChild(NotificationRoot);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        notificationToolBar.setOnMouseDragged(event ->
        {
            selectedNotification = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            NodeDragger.addDraggingProperty(selectedNotification,event);
        });

        notificationCloseButton.setOnAction(event->
        {
            selectedNotification = (AnchorPane) (((Button) event.getSource()).getParent());
            Main.root.getChildren().remove(selectedNotification);
        });

        for (int i =0;i<9;i++)
        {
            notificationComboBoxHours.getItems().add("0"+i);
        }
        notificationComboBoxHours.getItems().addAll(IntStream.iterate(10,n->n<=24, n->++n).mapToObj(Integer::toString).collect(Collectors.toList()));
        notificationComboBoxHours.setValue(String.valueOf(LocalTime.now().getHour()));
        notificationComboBoxHours.setVisibleRowCount(6);

        for (int i =1;i<9;i++)
        {
            notificationComboBoxMinutes.getItems().add("0"+i);
        }
        notificationComboBoxMinutes.getItems().addAll(IntStream.iterate(10,n->n<=60, n->++n).mapToObj(Integer::toString).collect(Collectors.toList()));
        notificationComboBoxMinutes.setValue(String.valueOf(LocalTime.now().getMinute()));
        notificationComboBoxMinutes.setVisibleRowCount(6);

        notificationAddButton.setOnAction(event ->
        {
            var dateOfEvent = notificationDatePicker.getValue();
            var timeOfEvent = LocalTime.of(Integer.parseInt(notificationComboBoxHours.getValue()),Integer.parseInt(notificationComboBoxMinutes.getValue()));
            var daysWithEvents = getDaysWithEvents();
            if (dateOfEvent!=null)
            {
                for (var day : daysWithEvents)
                {
                    if (day.getDate().equals(dateOfEvent))
                    {
                        day.addEvent(timeOfEvent, Day.EventType.Notification, notificationTextArea.getText());
                        updateDayIcons(day.getDate(),day.isHaveNotification(),day.isHaveGoal(),day.isHaveSchedule());
                        selectedNotification = (AnchorPane) (((Button) event.getSource()).getParent());
                        Main.root.getChildren().remove(selectedNotification);
                        return;
                    }
                }
                var day = addEventOfDay(dateOfEvent, timeOfEvent, Day.EventType.Notification,notificationTextArea.getText());
                daysWithEvents.add(day);
                updateDayIcons(day.getDate(),day.isHaveNotification(),day.isHaveGoal(),day.isHaveSchedule());
                selectedNotification = (AnchorPane) (((Button) event.getSource()).getParent());
                Main.root.getChildren().remove(selectedNotification);
            }
        });

        notificationCancelButton.setOnAction(notificationCloseButton.getOnAction());
    }
}
