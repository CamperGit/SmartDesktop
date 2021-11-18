package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
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
    @FXML
    private Label prenotificationRemindInLabel,prenotificationDaysLabel, prenotificationHoursLabel, prenotificationMinutesLabel;
    @FXML
    private ImageView prenotificationCloseButtonIV;

    private AnchorPane PrenotificationRoot;
    private int id;
    private int selectedMinute=5;
    private int selectedHour;
    private int selectedDay;
    private static LocalDateTime dateTime = null;
    private static String taskText = null;
    private static AnchorPane selectedPrenotification;
    private static Map<Integer, PrenotificationSD> prenotifications = new HashMap<>();
    private static int nextId = 1;

    public PrenotificationSD()
    {
    }

    public PrenotificationSD(LocalDateTime date, String taskText)
    {
        PrenotificationSD.dateTime = date;
        PrenotificationSD.taskText = taskText;
    }

    public static void clearSaveList()
    {
        prenotifications.clear();
        nextId = 1;
    }


    @Override
    public void start(Stage primaryStage) throws Exception
    {
        logger.info("PrenotificationSD: begin start method");
        PrenotificationRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/prenotification.fxml")));
        PrenotificationRoot.setLayoutX(DEFAULT_WIDTH / 2 - 340 / 2);
        PrenotificationRoot.setLayoutY(DEFAULT_HEIGHT / 2 - 244 / 2);

        this.id = nextId;
        nextId++;
        prenotifications.put(this.id, this);
        PrenotificationRoot.setAccessibleHelp(String.valueOf(this.id));

        addChild(PrenotificationRoot);
        logger.info("PrenotificationSD: end start method");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        logger.info("PrenotificationSD: begin initialize method");
        prenotificationRemindInLabel.setText(languageBundle.getString("prenotificationRemindInLabel"));
        prenotificationDaysLabel.setText(languageBundle.getString("prenotificationDaysLabel"));
        prenotificationHoursLabel.setText(languageBundle.getString("prenotificationHoursLabel"));
        prenotificationMinutesLabel.setText(languageBundle.getString("prenotificationMinutesLabel"));
        prenotificationAddButton.setText(languageBundle.getString("prenotificationAddButton"));
        prenotificationCancelButton.setText(languageBundle.getString("prenotificationCancelButton"));
        prenotificationCloseButtonIV.setImage(new Image("Images/delete30.png"));

        List<String> minutesValues = new ArrayList<>();
        minutesValues.addAll(Stream.iterate(0, n -> ++n).limit(10).map(Object::toString).map(n -> "0" + n).collect(Collectors.toList()));
        minutesValues.addAll(IntStream.iterate(10, n -> ++n).limit(51).mapToObj(Integer::toString).collect(Collectors.toList()));

        prenotificationComboBoxMinutes.getItems().addAll(minutesValues);

        List<String> hoursValues = new ArrayList<>();
        hoursValues.addAll(Stream.iterate(0,  n -> ++n).limit(10).map(Object::toString).map(n -> "0" + n).collect(Collectors.toList()));
        hoursValues.addAll(IntStream.iterate(10, n -> ++n).limit(15).mapToObj(Integer::toString).collect(Collectors.toList()));
        prenotificationComboBoxHours.getItems().addAll(hoursValues);

        List<String> daysValue = new ArrayList<>();
        daysValue.addAll(Stream.iterate(0, n -> ++n).limit(10).map(Object::toString).map(n -> "0" + n).collect(Collectors.toList()));
        daysValue.addAll(IntStream.iterate(10, n -> ++n).limit(22).mapToObj(Integer::toString).collect(Collectors.toList()));
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
            logger.info("PrenotificationSD: prenotification was closed");
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
            if (result < 1)
            {
                result = 1;
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
            AnchorPane prenotificationRoot = (AnchorPane)((HBox) (((ComboBox<String>) event.getSource()).getParent())).getParent();
            PrenotificationSD prenotificationSD = prenotifications.get(Integer.parseInt(prenotificationRoot.getAccessibleHelp()));
            prenotificationSD.selectedMinute = Integer.parseInt(prenotificationComboBoxMinutes.getValue());
        });
        prenotificationComboBoxMinutes.setOnAction(event ->
        {
            AnchorPane prenotificationRoot = (AnchorPane)((HBox) (((ComboBox<String>) event.getSource()).getParent())).getParent();
            PrenotificationSD prenotificationSD = prenotifications.get(Integer.parseInt(prenotificationRoot.getAccessibleHelp()));
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
            AnchorPane prenotificationRoot = (AnchorPane)((HBox) (((ComboBox<String>) event.getSource()).getParent())).getParent();
            PrenotificationSD prenotificationSD = prenotifications.get(Integer.parseInt(prenotificationRoot.getAccessibleHelp()));
            prenotificationSD.selectedHour = Integer.parseInt(prenotificationComboBoxHours.getValue());
        });
        prenotificationComboBoxHours.setOnAction(event ->
        {
            AnchorPane prenotificationRoot = (AnchorPane)((HBox) (((ComboBox<String>) event.getSource()).getParent())).getParent();
            PrenotificationSD prenotificationSD = prenotifications.get(Integer.parseInt(prenotificationRoot.getAccessibleHelp()));
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
            AnchorPane prenotificationRoot = (AnchorPane)((HBox) (((ComboBox<String>) event.getSource()).getParent())).getParent();
            PrenotificationSD prenotificationSD = prenotifications.get(Integer.parseInt(prenotificationRoot.getAccessibleHelp()));
            prenotificationSD.selectedDay = Integer.parseInt(prenotificationComboBoxDays.getValue());
        });
        prenotificationComboBoxDays.setOnAction(event ->
        {
            AnchorPane prenotificationRoot = (AnchorPane)((HBox) (((ComboBox<String>) event.getSource()).getParent())).getParent();
            PrenotificationSD prenotificationSD = prenotifications.get(Integer.parseInt(prenotificationRoot.getAccessibleHelp()));
            prenotificationSD.selectedDay = Integer.parseInt(prenotificationComboBoxDays.getValue());
        });

        if (defaultLocale.equals(Locale.ENGLISH))
        {
            prenotificationTextArea.appendText("In 5 minutes the event will begin: " + taskText);
        }
        else
        {
            prenotificationTextArea.appendText("Через 5 минут начнётся событие: " + taskText);
        }

        EventHandler<ActionEvent> listener = event ->
        {
            String days = prenotificationComboBoxDays.getValue();
            String hours = prenotificationComboBoxHours.getValue();
            String minutes = prenotificationComboBoxMinutes.getValue();
            prenotificationTextArea.setText("");
            if (defaultLocale.equals(Locale.ENGLISH))
            {
                prenotificationTextArea.appendText("In ");
                if (!days.equals("00"))
                {
                    if (days.equals("01"))
                    {
                        prenotificationTextArea.appendText(days + " day, ");
                    }
                    else
                    {
                        prenotificationTextArea.appendText(days + " days, ");
                    }
                }
                if (!hours.equals("00"))
                {
                    if (hours.equals("01"))
                    {
                        prenotificationTextArea.appendText(hours + " hour and ");
                    }
                    else
                    {
                        prenotificationTextArea.appendText(hours + " hours and ");
                    }
                }
                if (minutes.equals("01"))
                {
                    prenotificationTextArea.appendText(minutes + " minute ");
                }
                else
                {
                    prenotificationTextArea.appendText(minutes + " minutes ");
                }
                prenotificationTextArea.appendText("the event will begin: " + taskText);
            }
            else
            {
                prenotificationTextArea.appendText("Через ");
                if (!days.equals("00"))
                {
                    if (days.equals("01"))
                    {
                        prenotificationTextArea.appendText(days + " день, ");
                    }
                    else
                    {
                        prenotificationTextArea.appendText(days + " дней, ");
                    }
                }
                if (!hours.equals("00"))
                {
                    if (hours.equals("01"))
                    {
                        prenotificationTextArea.appendText(hours + " час и ");
                    }
                    else
                    {
                        prenotificationTextArea.appendText(hours + " часов и ");
                    }
                }
                if (minutes.equals("01"))
                {
                    prenotificationTextArea.appendText(minutes + " минуту ");
                }
                else
                {
                    prenotificationTextArea.appendText(minutes + " минут ");
                }
                prenotificationTextArea.appendText("начнётся событие: " + taskText);
            }
        };

        prenotificationComboBoxDays.setOnAction(listener);
        prenotificationComboBoxHours.setOnAction(listener);
        prenotificationComboBoxMinutes.setOnAction(listener);

        prenotificationAddButton.setOnAction(event ->
        {
            selectedPrenotification = (AnchorPane) (((Button) event.getSource()).getParent());
            PrenotificationSD prenotificationSD = prenotifications.get(Integer.parseInt(selectedPrenotification.getAccessibleHelp()));
            LocalDateTime dateTimeOfEvent = dateTime.minusDays(prenotificationSD.selectedDay).minusHours(prenotificationSD.selectedHour).minusMinutes(prenotificationSD.selectedMinute);
            LocalDate dateOfEvent = dateTimeOfEvent.toLocalDate();
            LocalTime timeOfEvent = dateTimeOfEvent.toLocalTime();
            List<Day> daysWithEvents = getDaysWithEvents();
            Day day = checkUsingOfThisDateOnEventList(dateOfEvent);
            {
                if (day == null)
                {
                    day = new Day(dateOfEvent);
                }
                EventOfDay eventOfDay = new EventOfDay(timeOfEvent, Day.EventType.Notification, prenotificationTextArea.getText());
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
                logger.info("PrenotificationSD: prenotification was added");
            }
        });
        logger.info("PrenotificationSD: end initialize method");
    }
}
