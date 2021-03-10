package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.camper.SmartDesktop.Info.CalendarSD.*;
import static com.camper.SmartDesktop.Main.*;
import static com.camper.SmartDesktop.Main.addChild;

public class PrenotificationSD extends Application implements Initializable
{
    @FXML
    private Button prenotificationCloseButton;
    @FXML
    private Button prenotificationAddButton;
    @FXML
    private Button prenotificationCancelButton;
    @FXML
    private ToolBar prenotificationToolBar;
    @FXML
    private ComboBox<String> prenotificationComboBoxMinutes;
    @FXML
    private ComboBox<String> prenotificationComboBoxHours;
    @FXML
    private ComboBox<String> prenotificationComboBoxDays;
    @FXML
    private TextArea prenotificationTextArea;

    private AnchorPane PrenotificationRoot;
    private int id;
    private int selectedMinute=5;
    private int selectedHour;
    private int selectedDay;
    private static LocalDateTime dateTime = null;
    private static AnchorPane selectedPrenotification;
    private static Map<Integer, PrenotificationSD> prenotifications = new HashMap<>();
    private static int nextId = 1;

    public PrenotificationSD()
    {
    }

    public PrenotificationSD(LocalDateTime date)
    {
        PrenotificationSD.dateTime = date;
    }

    public static void clearSaveList()
    {
        prenotifications.clear();
        nextId = 1;
    }


    @Override
    public void start(Stage primaryStage) throws Exception
    {
        if (defaultLocale.equals(new Locale("ru","RU")))
        {
            PrenotificationRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/prenotificationRu.fxml")));
        }
        else
        {
            PrenotificationRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/prenotificationEn.fxml")));
        }
        PrenotificationRoot.setLayoutX(DEFAULT_WIDTH / 2 - 340 / 2);
        PrenotificationRoot.setLayoutY(DEFAULT_HEIGHT / 2 - 244 / 2);

        this.id = nextId;
        nextId++;
        prenotifications.put(this.id, this);
        PrenotificationRoot.setAccessibleHelp(String.valueOf(this.id));

        addChild(PrenotificationRoot);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        var minutesValues = new ArrayList<String>()
        {{
            addAll(Stream.iterate(0, n -> n < 10, n -> ++n).map(Object::toString).map(n -> "0" + n).collect(Collectors.toList()));
            addAll(IntStream.iterate(10, n -> n < 60, n -> ++n).mapToObj(Integer::toString).collect(Collectors.toList()));
        }};
        prenotificationComboBoxMinutes.getItems().addAll(minutesValues);

        var hoursValues = new ArrayList<String>()
        {{
            addAll(Stream.iterate(0, n -> n < 10, n -> ++n).map(Object::toString).map(n -> "0" + n).collect(Collectors.toList()));
            addAll(IntStream.iterate(10, n -> n < 24, n -> ++n).mapToObj(Integer::toString).collect(Collectors.toList()));
        }};
        prenotificationComboBoxHours.getItems().addAll(hoursValues);

        var daysValue = new ArrayList<String>()
        {{
            addAll(Stream.iterate(0, n -> n < 10, n -> ++n).map(Object::toString).map(n -> "0" + n).collect(Collectors.toList()));
            addAll(IntStream.iterate(10, n -> n < 31, n -> ++n).mapToObj(Integer::toString).collect(Collectors.toList()));
        }};
        prenotificationComboBoxDays.getItems().addAll(daysValue);

        prenotificationToolBar.setOnMouseDragged(event ->
        {
            selectedPrenotification = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            NodeDragger.addDraggingProperty(selectedPrenotification, event);
        });

        prenotificationCloseButton.setOnAction(event ->
        {
            selectedPrenotification = (AnchorPane) (((Button) event.getSource()).getParent());
            prenotifications.remove(Integer.parseInt(selectedPrenotification.getAccessibleHelp()));
            Main.root.getChildren().remove(selectedPrenotification);
        });

        prenotificationCancelButton.setOnAction(prenotificationCloseButton.getOnAction());
        prenotificationTextArea.setWrapText(true);

        prenotificationComboBoxMinutes.setValue("05");
        prenotificationComboBoxHours.setValue("00");
        prenotificationComboBoxDays.setValue("00");

        prenotificationComboBoxMinutes.setOnScroll(event ->
        {
            int deltaY = (int) event.getDeltaY() / 25;
            int result = Integer.parseInt(prenotificationComboBoxMinutes.getValue()) + deltaY;
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
            prenotificationComboBoxMinutes.setValue(resultString);
            var prenotificationRoot = (AnchorPane)((HBox) (((ComboBox<String>) event.getSource()).getParent())).getParent();
            var prenotificationSD = prenotifications.get(Integer.parseInt(prenotificationRoot.getAccessibleHelp()));
            prenotificationSD.selectedMinute = Integer.parseInt(prenotificationComboBoxMinutes.getValue());
        });
        prenotificationComboBoxMinutes.setOnAction(event ->
        {
            var prenotificationRoot = (AnchorPane)((HBox) (((ComboBox<String>) event.getSource()).getParent())).getParent();
            var prenotificationSD = prenotifications.get(Integer.parseInt(prenotificationRoot.getAccessibleHelp()));
            prenotificationSD.selectedMinute = Integer.parseInt(prenotificationComboBoxMinutes.getValue());
        });

        prenotificationComboBoxHours.setOnScroll(event ->
        {
            int deltaY = (int) event.getDeltaY() / 25;
            int result = Integer.parseInt(prenotificationComboBoxHours.getValue()) + deltaY;
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
            prenotificationComboBoxHours.setValue(resultString);
            var prenotificationRoot = (AnchorPane)((HBox) (((ComboBox<String>) event.getSource()).getParent())).getParent();
            var prenotificationSD = prenotifications.get(Integer.parseInt(prenotificationRoot.getAccessibleHelp()));
            prenotificationSD.selectedHour = Integer.parseInt(prenotificationComboBoxHours.getValue());
        });
        prenotificationComboBoxHours.setOnAction(event ->
        {
            var prenotificationRoot = (AnchorPane)((HBox) (((ComboBox<String>) event.getSource()).getParent())).getParent();
            var prenotificationSD = prenotifications.get(Integer.parseInt(prenotificationRoot.getAccessibleHelp()));
            prenotificationSD.selectedHour = Integer.parseInt(prenotificationComboBoxHours.getValue());
        });

        prenotificationComboBoxDays.setOnScroll(event ->
        {
            int deltaY = (int) event.getDeltaY() / 25;
            int result = Integer.parseInt(prenotificationComboBoxDays.getValue()) + deltaY;
            if (result < 0)
            {
                result = 0;
            }
            if (result > 30)
            {
                result = 30;
            }
            String resultString = String.valueOf(result);
            if (result < 10)
            {
                resultString = "0" + result;
            }
            prenotificationComboBoxDays.setValue(resultString);
            var prenotificationRoot = (AnchorPane)((HBox) (((ComboBox<String>) event.getSource()).getParent())).getParent();
            var prenotificationSD = prenotifications.get(Integer.parseInt(prenotificationRoot.getAccessibleHelp()));
            prenotificationSD.selectedDay = Integer.parseInt(prenotificationComboBoxDays.getValue());
        });
        prenotificationComboBoxDays.setOnAction(event ->
        {
            var prenotificationRoot = (AnchorPane)((HBox) (((ComboBox<String>) event.getSource()).getParent())).getParent();
            var prenotificationSD = prenotifications.get(Integer.parseInt(prenotificationRoot.getAccessibleHelp()));
            prenotificationSD.selectedDay = Integer.parseInt(prenotificationComboBoxDays.getValue());
        });

        prenotificationAddButton.setOnAction(event ->
        {
            selectedPrenotification = (AnchorPane) (((Button) event.getSource()).getParent());
            var prenotificationSD = prenotifications.get(Integer.parseInt(selectedPrenotification.getAccessibleHelp()));
            var dateTimeOfEvent = dateTime.minusDays(prenotificationSD.selectedDay).minusHours(prenotificationSD.selectedHour).minusMinutes(prenotificationSD.selectedMinute);
            var dateOfEvent = dateTimeOfEvent.toLocalDate();
            var timeOfEvent = dateTimeOfEvent.toLocalTime();
            var daysWithEvents = getDaysWithEvents();
            var day = checkUsingOfThisDateOnEventList(dateOfEvent);
            {
                if (day == null)
                {
                    day = new Day(dateOfEvent);
                }
                var eventOfDay = new EventOfDay(timeOfEvent, Day.EventType.Notification, prenotificationTextArea.getText());
                if (day.addEvent(eventOfDay))
                {
                    UpcomingEvent.addEventToQueue(day.getDate(), eventOfDay);
                    updateDayIcons(day.getDate(), day.isHaveNotification(), day.isHaveGoal(), day.isHaveSchedule());
                }
                if (!(daysWithEvents.contains(day)))
                {
                    daysWithEvents.add(day);
                }

                selectedPrenotification = (AnchorPane) (((Button) event.getSource()).getParent());
                Main.root.getChildren().remove(selectedPrenotification);
            }
        });
    }
}
