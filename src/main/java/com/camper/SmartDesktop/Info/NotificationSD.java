package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.camper.SmartDesktop.Info.CalendarSD.*;
import static com.camper.SmartDesktop.Main.*;


public class NotificationSD extends Application implements Initializable
{
    @FXML
    private Button notificationAddButton;
    @FXML
    private Button notificationCancelButton;
    @FXML
    private Button notificationCloseButton;
    @FXML
    private DatePicker notificationDatePicker;
    @FXML
    private ComboBox<String> notificationComboBoxHours;
    @FXML
    private ComboBox<String> notificationComboBoxMinutes;
    @FXML
    private TextArea notificationTextArea;
    @FXML
    private ToolBar notificationToolBar;
    @FXML
    private ImageView notificationCloseButtonIV;

    private AnchorPane NotificationRoot;
    private int id;
    private static LocalDate date = null;
    private static AnchorPane selectedNotification;
    private static Map<Integer, NotificationSD> notifications = new HashMap<>();
    private static int nextId = 1;

    public NotificationSD()
    {
    }

    public NotificationSD(LocalDate date)
    {
        NotificationSD.date = date;
    }

    private AnchorPane getNotificationRoot()
    {
        return NotificationRoot;
    }

    public static void clearSaveList()
    {
        notifications.clear();
        nextId = 1;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        logger.info("NotificationSD: begin start method");
        NotificationRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/notification.fxml")));
        NotificationRoot.setLayoutX(DEFAULT_WIDTH / 2 - 340 / 2);
        NotificationRoot.setLayoutY(DEFAULT_HEIGHT / 2 - 248 / 2);

        this.id = nextId;
        nextId++;
        notifications.put(this.id, this);
        NotificationRoot.setAccessibleHelp(String.valueOf(this.id));

        addChild(NotificationRoot);
        logger.info("NotificationSD: end start method");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        logger.info("NotificationSD: begin initialize method");
        notificationAddButton.setText(languageBundle.getString("notificationAddButton"));
        notificationCancelButton.setText(languageBundle.getString("notificationCancelButton"));
        notificationCloseButtonIV.setImage(new Image("Images/delete30.png"));


        List<String> hoursValues = new ArrayList<>();
        hoursValues.addAll(Stream.iterate(0,  n -> ++n).limit(10).map(Object::toString).map(n -> "0" + n).collect(Collectors.toList()));
        hoursValues.addAll(IntStream.iterate(10, n -> ++n).limit(15).mapToObj(Integer::toString).collect(Collectors.toList()));
        List<String> minutesValues = new ArrayList<>();
        minutesValues.addAll(Stream.iterate(0, n -> ++n).limit(10).map(Object::toString).map(n -> "0" + n).collect(Collectors.toList()));
        minutesValues.addAll(IntStream.iterate(10, n -> ++n).limit(51).mapToObj(Integer::toString).collect(Collectors.toList()));

        notificationToolBar.setOnMouseDragged(event ->
        {
            selectedNotification = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            NodeDragger.addDraggingProperty(selectedNotification, event);
        });

        notificationCloseButton.setOnAction(event ->
        {
            selectedNotification = (AnchorPane) (((Button) event.getSource()).getParent());
            notifications.remove(Integer.parseInt(selectedNotification.getAccessibleHelp()));
            Main.root.getChildren().remove(selectedNotification);
            logger.info("NotificationSD: notification was closed");
        });

        notificationCancelButton.setOnAction(notificationCloseButton.getOnAction());

        notificationComboBoxHours.getItems().addAll(hoursValues);
        String hour = LocalTime.now().getHour() < 10 ? "0" + LocalTime.now().getHour() : String.valueOf(LocalTime.now().getHour());
        notificationComboBoxHours.setValue(hour);
        notificationComboBoxHours.setVisibleRowCount(6);

        notificationComboBoxHours.setOnScroll(event ->
        {
            int deltaY = (int) event.getDeltaY() / 25;
            int result = Integer.parseInt(notificationComboBoxHours.getValue()) + deltaY;
            if (result < 0)
            {
                result = 0;
            }
            if (result > 23)
            {
                result = 23;
            }
            String resultString = String.valueOf(result);
            if (result < 10)
            {
                resultString = "0" + result;
            }
            notificationComboBoxHours.setValue(resultString);
        });

        notificationComboBoxMinutes.getItems().addAll(minutesValues);
        String minute = LocalTime.now().getMinute() < 10 ? "0" + LocalTime.now().getMinute() : String.valueOf(LocalTime.now().getMinute());
        notificationComboBoxMinutes.setValue(minute);
        notificationComboBoxMinutes.setVisibleRowCount(6);

        notificationComboBoxMinutes.setOnScroll(event ->
        {
            int deltaY = (int) event.getDeltaY() / 25;
            int result = Integer.parseInt(notificationComboBoxMinutes.getValue()) + deltaY;
            if (result < 0)
            {
                result = 0;
            }
            if (result > 59)
            {
                result = 59;
            }
            String resultString = String.valueOf(result);
            if (result < 10)
            {
                resultString = "0" + result;
            }
            notificationComboBoxMinutes.setValue(resultString);
        });

        if (date != null)
        {
            notificationDatePicker.setValue(date);
        }

        notificationAddButton.setOnAction(event ->
        {
            LocalDate dateOfEvent = notificationDatePicker.getValue();
            LocalTime timeOfEvent = LocalTime.of(Integer.parseInt(notificationComboBoxHours.getValue()), Integer.parseInt(notificationComboBoxMinutes.getValue()));
            List<Day> daysWithEvents = getDaysWithEvents();
            if (dateOfEvent != null)
            {
                Day day = checkUsingOfThisDateOnEventList(dateOfEvent);
                {
                    if (day == null)
                    {
                        day = new Day(dateOfEvent);
                    }
                    EventOfDay eventOfDay = new EventOfDay(timeOfEvent, Day.EventType.Notification, notificationTextArea.getText());
                    if (day.addEvent(eventOfDay))
                    {
                        UpcomingEvent.addEventToQueue(day.getDate(), eventOfDay);
                        updateDayIcons(day.getDate(), day.isHaveNotification(), day.isHaveGoal(), day.isHaveSchedule());
                    }
                    if (!(daysWithEvents.contains(day)))
                    {
                        daysWithEvents.add(day);
                    }

                    selectedNotification = (AnchorPane) (((Button) event.getSource()).getParent());
                    Main.root.getChildren().remove(selectedNotification);
                    logger.info("NotificationSD: notification was added");
                }
            }
        });
        logger.info("NotificationSD: end initialize method");
    }

    public static void removeNotificationFromEventList(LocalDate date, EventOfDay notificationEvent)
    {
        Day day = CalendarSD.checkUsingOfThisDateOnEventList(date);
        UpcomingEvent.removeEventFromQueue(date, notificationEvent);
        Day.removeEventFromDay(date, notificationEvent);
        if (day != null)
        {
            updateDayIcons(date, day.isHaveNotification(), day.isHaveGoal(), day.isHaveSchedule());
        }
        else
        {
            updateDayIcons(date, false, false, false);
        }
    }
}
