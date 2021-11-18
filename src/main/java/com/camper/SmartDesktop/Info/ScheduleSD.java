package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.camper.SmartDesktop.Info.CalendarSD.checkUsingOfThisDateOnEventList;
import static com.camper.SmartDesktop.Info.CalendarSD.updateDayIcons;
import static com.camper.SmartDesktop.Main.*;

public class ScheduleSD extends Application implements Initializable
{

    @FXML
    private ImageView scheduleCloseButtonIV;
    @FXML
    private ImageView scheduleSettingsButtonIV;
    @FXML
    private ImageView scheduleAddNewLineButtonIV;
    @FXML
    private ToolBar scheduleToolBar;
    @FXML
    private Button scheduleCloseButton;
    @FXML
    private Button scheduleSettingsButton;
    @FXML
    private Button scheduleAddNewLineButton;
    @FXML
    private Button schedulerSaveButton;
    @FXML
    private VBox scheduleContentVbox;
    @FXML
    private DatePicker schedulerDatePicker;
    @FXML
    private TextField scheduleNameTextField;

    private boolean load = false;
    private AnchorPane ScheduleRoot;
    private final Map<CheckBox, EventOfDay> eventsOfSchedule = new HashMap<>();
    private final List<Day> scheduleDaysSaveList = new ArrayList<>();
    private int id;
    private String nameOfSchedule = null;
    private LocalDate date = null;
    private SchedulerCopySettings copySettings = null;
    private static AnchorPane selectedSchedule;
    private static Map<Integer, ScheduleSD> schedules = new HashMap<>();
    private static int nextId = 1;

    public ScheduleSD()
    {
    }

    public ScheduleSD(LocalDate date)
    {
        this.date = date;
    }

    private ScheduleSD(boolean load)
    {
        this.load = load;
    }

    public static void clearSaveList()
    {
        schedules.clear();
        nextId = 1;
    }

    private Map<CheckBox, EventOfDay> getEventsOfSchedule()
    {
        return eventsOfSchedule;
    }

    private List<Day> getScheduleDaysSaveList()
    {
        return scheduleDaysSaveList;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public void setDate(LocalDate date)
    {
        this.date = date;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public SchedulerCopySettings getCopySettings()
    {
        return copySettings;
    }

    public void setCopySettings(SchedulerCopySettings copySettings)
    {
        this.copySettings = copySettings;
    }

    private AnchorPane getScheduleRoot()
    {
        return ScheduleRoot;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        logger.info("ScheduleSD: begin start method");
        ScheduleRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/schedule.fxml")));
        ScheduleRoot.setLayoutX(80);
        ScheduleRoot.setLayoutY(30);
        this.id = nextId;
        nextId++;
        schedules.put(this.id, this);
        ScheduleRoot.setAccessibleHelp(String.valueOf(this.id));

        addChild(ScheduleRoot);
        if (!load)
        {
            ScheduleRoot.setAccessibleText(String.valueOf(idOfSelectedTab));
            List<Node> elementsOfSelectedTab = tabs.get(idOfSelectedTab);
            elementsOfSelectedTab.add(ScheduleRoot);
        }
        logger.info("ScheduleSD: end start method");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        logger.info("ScheduleSD: begin initialize method");
        scheduleNameTextField.setPromptText(languageBundle.getString("scheduleNamePromptTextField"));
        schedulerSaveButton.setText(languageBundle.getString("schedulerSaveButton"));

        scheduleCloseButtonIV.setImage(new Image("Images/delete30.png"));
        scheduleSettingsButtonIV.setImage(new Image("Images/settings30.png"));
        scheduleAddNewLineButtonIV.setImage(new Image("Images/add28.png"));

        scheduleAddNewLineButton.setOnAction(event ->
        {
            AnchorPane scheduleRoot = (AnchorPane) (((Button) event.getSource()).getParent());
            for (Node child : scheduleRoot.getChildren())
            {
                if (child instanceof ScrollPane)
                {
                    VBox vbox = (VBox) (((ScrollPane) child).getContent());
                    createNewLine(vbox, null, null, false, null, Integer.parseInt(scheduleRoot.getAccessibleHelp()), schedulerSaveButton);
                }
            }
            schedulerSaveButton.setDisable(false);
        });

        scheduleContentVbox.setSpacing(10);
        scheduleNameTextField.setOnKeyTyped(event ->
        {
            AnchorPane scheduleRoot = (AnchorPane) (((TextField) event.getSource()).getParent());
            ScheduleSD scheduleSD = schedules.get(Integer.parseInt(scheduleRoot.getAccessibleHelp()));
            scheduleSD.nameOfSchedule = scheduleNameTextField.getText();
        });

        scheduleCloseButton.setOnAction(event ->
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, languageBundle.getString("scheduleDeleteAlert"), new ButtonType("Да", ButtonBar.ButtonData.YES), new ButtonType("Нет", ButtonBar.ButtonData.NO));
            ButtonType alertResult = alert.showAndWait().orElse(ButtonType.CANCEL);
            if (alertResult.getButtonData().equals(ButtonBar.ButtonData.YES))
            {
                selectedSchedule = (AnchorPane) (((Button) event.getSource()).getParent());
                ScheduleSD scheduleSD = schedules.remove(Integer.parseInt(selectedSchedule.getAccessibleHelp()));
                List<Day> scheduleDaysSaveList = scheduleSD.getScheduleDaysSaveList();
                for (Day dayFromSaveList : scheduleDaysSaveList)
                {
                    LocalDate date = dayFromSaveList.getDate();
                    for (EventOfDay eventOfDay : dayFromSaveList.getEvents())
                    {
                        removeScheduleEvent(date, eventOfDay);
                    }
                    Day day = CalendarSD.checkUsingOfThisDateOnEventList(date);
                    if (day != null)
                    {
                        updateDayIcons(date, day.isHaveNotification(), day.isHaveGoal(), day.isHaveSchedule());
                    } else
                    {
                        updateDayIcons(date, false, false, false);
                    }
                }
                scheduleDaysSaveList.clear();
                Main.root.getChildren().remove(selectedSchedule);
                logger.info("ScheduleSD: schedule was deleted");
            }
        });

        scheduleToolBar.setOnMouseDragged(event ->
        {
            selectedSchedule = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            int id = Integer.parseInt(selectedSchedule.getAccessibleHelp());
            SchedulerCopySettings settings = schedules.get(id).getCopySettings();
            if (settings != null)
            {
                Main.root.getChildren().remove(settings.getCopySettingsRoot());
            }
            NodeDragger.addDraggingProperty(selectedSchedule, event);
        });

        schedulerSaveButton.setOnAction(event ->
        {
            AnchorPane scheduleRoot = (AnchorPane) (((Button) event.getSource()).getParent());
            int id = Integer.parseInt(scheduleRoot.getAccessibleHelp());
            ScheduleSD scheduleSD = schedules.get(id);
            List<Day> scheduleDaysSaveList = scheduleSD.getScheduleDaysSaveList();
            LocalDate date = scheduleSD.getDate();
            Map<CheckBox, EventOfDay>  mapWithEvents = scheduleSD.getEventsOfSchedule();
            if (date != null)
            {
                schedulerSaveButton.setDisable(true);
                if (scheduleDaysSaveList.size() != 0)
                {
                    Alert alert = new Alert(Alert.AlertType.WARNING, languageBundle.getString("scheduleChangeAlert"), new ButtonType("Да", ButtonBar.ButtonData.YES), new ButtonType("Нет", ButtonBar.ButtonData.NO));
                    ButtonType alertResult = alert.showAndWait().orElse(ButtonType.CANCEL);
                    if (alertResult.getButtonData().equals(ButtonBar.ButtonData.YES))
                    {
                        for (Day day : scheduleDaysSaveList)
                        {
                            for (EventOfDay scheduleEvent : day.getEvents())
                            {
                                removeScheduleEvent(day.getDate(), scheduleEvent);
                            }
                            Day dayFromEventList = CalendarSD.checkUsingOfThisDateOnEventList(day.getDate());
                            if (dayFromEventList != null)
                            {
                                updateDayIcons(dayFromEventList.getDate(), dayFromEventList.isHaveNotification(), dayFromEventList.isHaveGoal(), dayFromEventList.isHaveSchedule());
                            } else
                            {
                                updateDayIcons(day.getDate(), false, false, false);
                            }
                        }
                        scheduleDaysSaveList.clear();
                    }
                    if (alertResult.getButtonData().equals(ButtonBar.ButtonData.NO))
                    {
                        event.consume();
                    }
                }

                Day dayToEventList = checkUsingOfThisDateOnEventList(date);
                Day dayToSaveList = new Day(date);
                if (dayToEventList == null)
                {
                    dayToEventList = new Day(date);
                }
                for (Map.Entry<CheckBox, EventOfDay> entry : mapWithEvents.entrySet())
                {
                    CheckBox state = entry.getKey();
                    EventOfDay eventOfDay = entry.getValue();
                    if (state.isSelected())
                    {
                        if (dayToEventList.addEvent(eventOfDay))
                        {
                            dayToSaveList.addEvent(eventOfDay);
                        }
                    }
                }

                if (dayToEventList.getEvents().size() != 0 && dayToSaveList.getEvents().size() != 0)
                {
                    List<Day> daysWithEvents = CalendarSD.getDaysWithEvents();

                    if (!(daysWithEvents.contains(dayToEventList)))
                    {
                        daysWithEvents.add(dayToEventList);
                    }

                    scheduleDaysSaveList.add(dayToSaveList);

                    if (scheduleSD.getCopySettings() != null)
                    {
                        SchedulerCopySettings.ScheduleSettingsRepeat repeat = scheduleSD.getCopySettings().getRepeatSelected();
                        SchedulerCopySettings.ScheduleSettingsPeriod period = scheduleSD.getCopySettings().getPeriodSelected();
                        scheduleDaysSaveList.addAll(copySchedule(dayToSaveList, repeat, period));
                    }

                    UpcomingEvent.loadEventsToQueue(scheduleDaysSaveList);
                    for (Day dayFromList : scheduleDaysSaveList)
                    {
                        Day dayFromEventList = CalendarSD.checkUsingOfThisDateOnEventList(dayFromList.getDate());
                        if (dayFromEventList != null)
                        {
                            updateDayIcons(dayFromEventList.getDate(), dayFromEventList.isHaveNotification(), dayFromEventList.isHaveGoal(), dayFromEventList.isHaveSchedule());
                        }
                    }
                }
            } else
            {
                Alert alert = new Alert(Alert.AlertType.WARNING, languageBundle.getString("scheduleDateAlert"), ButtonType.OK);
                alert.showAndWait();
            }
        });

        schedulerDatePicker.setOnAction(event ->
        {
            ScheduleSD scheduleSD = schedules.get(Integer.parseInt((((DatePicker) (event.getSource())).getParent()).getAccessibleHelp()));
            scheduleSD.setDate(schedulerDatePicker.getValue());
            schedulerSaveButton.setDisable(false);
        });

        scheduleSettingsButton.setOnMouseClicked(event ->
        {
            int id = Integer.parseInt((((Button) (event.getSource())).getParent()).getAccessibleHelp());
            ScheduleSD scheduleSD = schedules.get(id);
            if (scheduleSD.getCopySettings() == null)
            {
                SchedulerCopySettings settings = new SchedulerCopySettings(event, id, schedulerSaveButton);
                scheduleSD.setCopySettings(settings);
                try
                {
                    settings.start(Main.Stage);
                } catch (Exception e)
                {
                    logger.error("ScheduleSD: schedulerCopySettings FXML load error", e);
                }
            } else
            {
                SchedulerCopySettings settings = scheduleSD.getCopySettings();
                Main.root.getChildren().remove(settings.getCopySettingsRoot());
                settings.showSettings(settings.getCopySettingsRoot(), event);
            }
        });
        logger.info("ScheduleSD: end initialize method");
    }

    private static void removeScheduleEvent(LocalDate date, EventOfDay event)
    {
        UpcomingEvent.removeEventFromQueue(date, event);
        Day.removeEventFromDay(date, event);
    }

    private Button getSaveButton(AnchorPane scheduleRoot)
    {
        for (Node node : scheduleRoot.getChildren())
        {
            if (node instanceof Button && node.getAccessibleHelp() != null && node.getAccessibleHelp().equals("scheduleSaveButton"))
            {
                return (Button) node;
            }
        }
        return null;
    }

    private static List<Day> copySchedule(Day day, SchedulerCopySettings.ScheduleSettingsRepeat repeat, SchedulerCopySettings.ScheduleSettingsPeriod period)
    {
        List<Day> list = new ArrayList<>();
        LocalDate date = day.getDate();
        if (repeat == SchedulerCopySettings.ScheduleSettingsRepeat.DAY && period == SchedulerCopySettings.ScheduleSettingsPeriod.FOR_A_WEEK)
        {
            LocalDate limit = date.plusWeeks(1).minusDays(1);
            while (date.isBefore(limit))
            {
                date = date.plusDays(1);
                Day clonedDay = copyScheduleEventsFromDay(date, day);
                if (clonedDay != null)
                {
                    list.add(clonedDay);
                }
            }
        }
        if (repeat == SchedulerCopySettings.ScheduleSettingsRepeat.DAY && period == SchedulerCopySettings.ScheduleSettingsPeriod.FOR_A_MONTH)
        {
            LocalDate limit = date.plusMonths(1);
            while (date.isBefore(limit))
            {
                date = date.plusDays(1);
                Day clonedDay = copyScheduleEventsFromDay(date, day);
                if (clonedDay != null)
                {
                    list.add(clonedDay);
                }
            }
        }
        if (repeat == SchedulerCopySettings.ScheduleSettingsRepeat.WEEK && period == SchedulerCopySettings.ScheduleSettingsPeriod.FOR_A_MONTH)
        {
            LocalDate limit = date.plusMonths(1).minusWeeks(1);
            while (date.isBefore(limit))
            {
                date = date.plusWeeks(1);
                Day clonedDay = copyScheduleEventsFromDay(date, day);
                if (clonedDay != null)
                {
                    list.add(clonedDay);
                }
            }
        }
        if (repeat == SchedulerCopySettings.ScheduleSettingsRepeat.WEEK && period == SchedulerCopySettings.ScheduleSettingsPeriod.FOR_A_YEAR)
        {
            LocalDate limit = date.plusYears(1).minusWeeks(1);
            while (date.isBefore(limit))
            {
                date = date.plusWeeks(1);
                Day clonedDay = copyScheduleEventsFromDay(date, day);
                if (clonedDay != null)
                {
                    list.add(clonedDay);
                }
            }
        }
        if (repeat == SchedulerCopySettings.ScheduleSettingsRepeat.MONTH && period == SchedulerCopySettings.ScheduleSettingsPeriod.FOR_A_YEAR)
        {
            LocalDate limit = date.plusYears(1);
            while (date.isBefore(limit))
            {
                date = date.plusMonths(1);
                Day clonedDay = copyScheduleEventsFromDay(date, day);
                if (clonedDay != null)
                {
                    list.add(clonedDay);
                }
            }
        }
        if (repeat == SchedulerCopySettings.ScheduleSettingsRepeat.YEAR && period == SchedulerCopySettings.ScheduleSettingsPeriod.DONT)
        {
            LocalDate limit = date.plusYears(10);
            while (date.isBefore(limit))
            {
                date = date.plusYears(1);
                Day clonedDay = copyScheduleEventsFromDay(date, day);
                if (clonedDay != null)
                {
                    list.add(clonedDay);
                }
            }
        }
        return list;
    }

    public static void removeEventFromEventList(LocalDate date, EventOfDay scheduleEvent)
    {
        schedules:
        for (ScheduleSD scheduleSD : schedules.values())
        {
            List<Day> days = scheduleSD.getScheduleDaysSaveList();
            for (Day day : days)
            {
                if (day.getDate().equals(date))
                {
                    for (EventOfDay event : day.getEvents())
                    {
                        if (event.equals(scheduleEvent))
                        {
                            removeScheduleEvent(date, event);
                            day.getEvents().remove(event);
                            Day dayFromEventsList = CalendarSD.checkUsingOfThisDateOnEventList(date);
                            if (dayFromEventsList != null)
                            {
                                updateDayIcons(date, dayFromEventsList.isHaveNotification(), dayFromEventsList.isHaveGoal(), dayFromEventsList.isHaveSchedule());
                            } else
                            {
                                updateDayIcons(date, false, false, false);
                            }
                            break;
                        }
                    }
                }
                if (day.getEvents().size() == 0)
                {
                    days.remove(day);
                    break schedules;
                }
            }
        }
    }

    private static void removeDeprecatedScheduleEvents(List<Day> scheduleDaysSaveList)
    {
        List<Day> daysToRemove = new ArrayList<>();
        for (Day day : scheduleDaysSaveList)
        {
            boolean haveEvents = Day.checkOfDeprecatedEvents(day, false);
            if (!haveEvents)
            {
                daysToRemove.add(day);
            }
        }
        scheduleDaysSaveList.removeAll(daysToRemove);
    }


    private static Day copyScheduleEventsFromDay(LocalDate date, Day day)
    {
        boolean added = false;
        Day dayFromEventList = checkUsingOfThisDateOnEventList(date);
        Day newDay = new Day(date);
        List<Day> daysWithEvents = CalendarSD.getDaysWithEvents();
        if (dayFromEventList == null)
        {
            dayFromEventList = new Day(date);
        }
        for (EventOfDay event : day.getEvents())
        {
            //Мы же копируем только расписание, поэтому другие ивенты, если они есть, добавлять не нужно
            if (event.getType().equals(Day.EventType.Schedule))
            {
                if (dayFromEventList.addEvent(event))
                {
                    newDay.addEvent(event);
                    added = true;
                }
            }
        }
        if (added)
        {
            if (!(daysWithEvents.contains(dayFromEventList)))
            {
                daysWithEvents.add(dayFromEventList);
            }
            return newDay;
        } else
        {
            return null;
        }
    }

    public static void createNewLine(VBox content, LocalTime startTime, LocalTime endTime, boolean checkBoxState, String info, int id, Button schedulerSaveButton)
    {
        HBox hbox = new HBox();
        Main.setRegion(hbox, 460, 25);

        Separator leftOffset1 = new Separator(Orientation.VERTICAL);
        leftOffset1.setVisible(false);
        leftOffset1.setPrefWidth(5);

        ComboBox<String> hours1 = new ComboBox<>();
        hours1.setLayoutX(5);
        ComboBox<String> hours2 = new ComboBox<>();
        ComboBox<String> minutes1 = new ComboBox<>();
        ComboBox<String> minutes2 = new ComboBox<>();

        Main.setRegion(hours1, 55, 25);
        Main.setRegion(hours2, 55, 25);
        Main.setRegion(minutes1, 55, 25);
        Main.setRegion(minutes2, 55, 25);

        List<String> hoursValues = new ArrayList<>();
        hoursValues.addAll(Stream.iterate(0,  n -> ++n).limit(10).map(Object::toString).map(n -> "0" + n).collect(Collectors.toList()));
        hoursValues.addAll(IntStream.iterate(10, n -> ++n).limit(15).mapToObj(Integer::toString).collect(Collectors.toList()));
        List<String> minutesValues = new ArrayList<>();
        minutesValues.addAll(Stream.iterate(0, n -> ++n).limit(10).map(Object::toString).map(n -> "0" + n).collect(Collectors.toList()));
        minutesValues.addAll(IntStream.iterate(10, n -> ++n).limit(51).mapToObj(Integer::toString).collect(Collectors.toList()));

        hours1.getItems().addAll(hoursValues);
        if (startTime == null)
        {
            hours1.setValue("16");
        } else
        {
            String hour = startTime.getHour() < 10 ? "0" + startTime.getHour() : String.valueOf(startTime.getHour());
            hours1.setValue(hour);
        }
        hours1.setAccessibleHelp("startTimeHours");
        hours1.setVisibleRowCount(6);
        hours2.getItems().addAll(hoursValues);
        if (endTime == null)
        {
            hours2.setValue("17");
        } else
        {
            String hour = endTime.getHour() < 10 ? "0" + endTime.getHour() : String.valueOf(endTime.getHour());
            hours2.setValue(hour);
        }
        hours2.setAccessibleHelp("endTimeHours");
        hours2.setVisibleRowCount(6);

        Separator separatorBetweenTime = new Separator(Orientation.VERTICAL);
        separatorBetweenTime.setVisible(false);
        separatorBetweenTime.setOpacity(0);
        Main.setRegion(separatorBetweenTime, 12, 25);

        minutes1.getItems().addAll(minutesValues);
        if (startTime == null)
        {
            minutes1.setValue("00");
        } else
        {
            String minute = startTime.getMinute() < 10 ? "0" + startTime.getMinute() : String.valueOf(startTime.getMinute());
            minutes1.setValue(minute);
        }
        minutes1.setAccessibleHelp("startTimeMinutes");
        minutes1.setVisibleRowCount(6);
        minutes2.getItems().addAll(minutesValues);
        if (endTime == null)
        {
            minutes2.setValue("00");
        } else
        {
            String minute = endTime.getMinute() < 10 ? "0" + endTime.getMinute() : String.valueOf(endTime.getMinute());
            minutes2.setValue(minute);
        }
        minutes2.setAccessibleHelp("endTimeMinutes");
        minutes2.setVisibleRowCount(6);


        Label dash = new Label("-");
        dash.setAlignment(Pos.CENTER);
        dash.setFont(new Font(20));
        Main.setRegion(dash, 12, 30);

        Separator spacingBetweenTimeAndCheckBox = new Separator(Orientation.VERTICAL);
        spacingBetweenTimeAndCheckBox.setVisible(false);
        spacingBetweenTimeAndCheckBox.setPrefWidth(15);

        CheckBox addEventCheckBox = new CheckBox(languageBundle.getString("scheduleShowNoticeCheckBox"));
        Main.setRegion(addEventCheckBox, 157, 25);
        addEventCheckBox.setLayoutX(5);
        addEventCheckBox.getStylesheets().add(Objects.requireNonNull(mainCL.getResource("FXMLs/mediumCheckBox.css")).toExternalForm());
        addEventCheckBox.setSelected(checkBoxState);


        Separator spacingBetweenCheckBoxAndDeletingButton = new Separator(Orientation.VERTICAL);
        spacingBetweenCheckBoxAndDeletingButton.setVisible(false);
        spacingBetweenCheckBoxAndDeletingButton.setPrefWidth(6);

        Button deletingButton = new Button();
        Main.setRegion(deletingButton, 25, 25);
        deletingButton.setGraphic(new ImageView(new Image("Images/minus25.png")));

        hbox.getChildren().addAll(leftOffset1, hours1, minutes1, dash, hours2, minutes2, spacingBetweenTimeAndCheckBox, addEventCheckBox, spacingBetweenCheckBoxAndDeletingButton, deletingButton);


        Separator leftOffset2 = new Separator(Orientation.VERTICAL);
        leftOffset2.setVisible(false);
        leftOffset2.setPrefWidth(5);

        TextArea text = new TextArea();
        text.setWrapText(true);
        if (info != null)
        {
            text.setText(info);
        }
        Main.setRegion(text, 435, 25);
        HBox hbox2 = new HBox(leftOffset2, text);

        VBox vbox = new VBox(8, hbox, hbox2);
        Main.setRegion(vbox, 460, 55);
        HBox line = new HBox(5, vbox);
        Main.setRegion(line, 460, 55);

        Separator hSeparator = new Separator(Orientation.HORIZONTAL);
        hSeparator.setPrefHeight(10);
        content.getChildren().addAll(line, hSeparator);



        addEventCheckBox.setOnAction(event ->
        {
            Node parent = ((Node) (event.getSource())).getParent();
            ScheduleSD scheduleSD = schedules.get(returnAnchorId(parent));
            Map<CheckBox, EventOfDay> map = scheduleSD.getEventsOfSchedule();
            if (addEventCheckBox.isSelected())
            {
                EventOfDay eventOfDay = new EventOfDay(LocalTime.of(Integer.parseInt(hours1.getValue()), Integer.parseInt(minutes1.getValue())), Day.EventType.Schedule, text.getText());
                map.put(addEventCheckBox, eventOfDay);
            } else
            {
                map.remove(addEventCheckBox);
            }
            schedulerSaveButton.setDisable(false);
        });

        text.setOnKeyTyped(event ->
        {
            Node parent = ((Node) (event.getSource())).getParent();
            ScheduleSD scheduleSD = schedules.get(returnAnchorId(parent));
            Map<CheckBox, EventOfDay> map = scheduleSD.getEventsOfSchedule();
            if (addEventCheckBox.isSelected())
            {
                EventOfDay eventOfDay = new EventOfDay(LocalTime.of(Integer.parseInt(hours1.getValue()), Integer.parseInt(minutes1.getValue())), Day.EventType.Schedule, text.getText());
                map.put(addEventCheckBox, eventOfDay);

            } else
            {
                map.remove(addEventCheckBox);
            }
            schedulerSaveButton.setDisable(false);
        });

        hours1.setOnAction(addEventCheckBox.getOnAction());
        hours2.setOnAction(addEventCheckBox.getOnAction());
        minutes1.setOnAction(addEventCheckBox.getOnAction());
        minutes2.setOnAction(addEventCheckBox.getOnAction());


        deletingButton.setOnAction(event ->
        {
            Node parent = ((Node) (event.getSource())).getParent();
            ScheduleSD scheduleSD = schedules.get(returnAnchorId(parent));
            Map<CheckBox, EventOfDay> map = scheduleSD.getEventsOfSchedule();
            EventOfDay scheduleEvent = map.get(addEventCheckBox);
            removingEventFromDay:
            for (Day dayFromSaveList : scheduleSD.getScheduleDaysSaveList())
            {
                for (EventOfDay eventFromSaveListDay : dayFromSaveList.getEvents())
                {
                    if (eventFromSaveListDay.equals(scheduleEvent))
                    {
                        dayFromSaveList.getEvents().remove(eventFromSaveListDay);
                        removeScheduleEvent(dayFromSaveList.getDate(), eventFromSaveListDay);
                        Day dayFromEventList = CalendarSD.checkUsingOfThisDateOnEventList(dayFromSaveList.getDate());
                        if (dayFromEventList != null)
                        {
                            updateDayIcons(dayFromEventList.getDate(), dayFromEventList.isHaveNotification(), dayFromEventList.isHaveGoal(), dayFromEventList.isHaveSchedule());
                        } else
                        {
                            updateDayIcons(dayFromSaveList.getDate(), false, false, false);
                        }
                        continue removingEventFromDay;
                    }
                }
            }

            content.getChildren().remove(line);
            content.getChildren().remove(hSeparator);
        });

        //Необходимо при загрузке, чтобы добавить в мапу старые ивенты
        if (addEventCheckBox.isSelected())
        {
            Map<CheckBox, EventOfDay> map = schedules.get(id).getEventsOfSchedule();
            EventOfDay eventOfDay = new EventOfDay(LocalTime.of(Integer.parseInt(hours1.getValue()), Integer.parseInt(minutes1.getValue())), Day.EventType.Schedule, text.getText());
            map.put(addEventCheckBox, eventOfDay);
        }
    }

    public static void addSchedulesToXML(Document doc, boolean createEmptyXML)
    {
        logger.info("ScheduleSD: start schedules saving");
        org.w3c.dom.Node rootElement = doc.getFirstChild();

        Element schedulesElement = doc.createElement("schedules");
        rootElement.appendChild(schedulesElement);
        if (!createEmptyXML)
        {
            int id = 1;
            for (Map.Entry<Integer, ScheduleSD> entry : schedules.entrySet())
            {
                ScheduleSD scheduleSD = entry.getValue();
                AnchorPane schedule = scheduleSD.getScheduleRoot();
                List<Day> daysSaveList = scheduleSD.getScheduleDaysSaveList();
                Element scheduleElement = doc.createElement("schedule" + id);
                scheduleElement.setAttribute("tab", schedule.getAccessibleText());

                schedulesElement.appendChild(scheduleElement);

                Element visibilityElement = doc.createElement("visibility");
                scheduleElement.appendChild(visibilityElement);
                Text visibilityValue = doc.createTextNode(String.valueOf(schedule.isVisible()));
                visibilityElement.appendChild(visibilityValue);

                Element layoutElement = doc.createElement("layout");
                scheduleElement.appendChild(layoutElement);

                Element layoutX = doc.createElement("layoutX");
                layoutElement.appendChild(layoutX);
                Text layoutXValue = doc.createTextNode(String.valueOf((int) (schedule.getLayoutX())));
                layoutX.appendChild(layoutXValue);

                Element layoutY = doc.createElement("layoutY");
                layoutElement.appendChild(layoutY);
                Text layoutYValue = doc.createTextNode(String.valueOf((int) (schedule.getLayoutY())));
                layoutY.appendChild(layoutYValue);

                Element nameOfScheduleElement = doc.createElement("nameOfSchedule");
                scheduleElement.appendChild(nameOfScheduleElement);
                Text nameOfScheduleElementValue;
                if (scheduleSD.nameOfSchedule != null)
                {
                    nameOfScheduleElementValue = doc.createTextNode(scheduleSD.nameOfSchedule);
                } else
                {
                    nameOfScheduleElementValue = doc.createTextNode("");
                }
                nameOfScheduleElement.appendChild(nameOfScheduleElementValue);

                Element copySettingsElement = doc.createElement("copySettings");
                scheduleElement.appendChild(copySettingsElement);
                if (scheduleSD.getCopySettings() != null)
                {
                    SchedulerCopySettings settings = scheduleSD.getCopySettings();
                    Element repeatElement = doc.createElement("repeat");
                    copySettingsElement.appendChild(repeatElement);
                    Text repeatElementValue = doc.createTextNode(settings.getRepeatSelected().name());
                    repeatElement.appendChild(repeatElementValue);

                    Element periodElement = doc.createElement("period");
                    copySettingsElement.appendChild(periodElement);
                    Text periodElementValue = doc.createTextNode(settings.getPeriodSelected().name());
                    periodElement.appendChild(periodElementValue);
                }

                removeDeprecatedScheduleEvents(daysSaveList);

                Element dateElement = doc.createElement("date");
                scheduleElement.appendChild(dateElement);
                Text dateElementValue = doc.createTextNode(String.valueOf(scheduleSD.getDate()));
                dateElement.appendChild(dateElementValue);

                Element daysWithEventsElement = doc.createElement("daysWithEvents");
                scheduleElement.appendChild(daysWithEventsElement);

                int numberOfDay = 1;
                for (Day day : daysSaveList)
                {
                    Element dayElement = doc.createElement("day" + numberOfDay);
                    daysWithEventsElement.appendChild(dayElement);

                    Element dayDateElement = doc.createElement("dayDate");
                    dayElement.appendChild(dayDateElement);
                    Text dayDateElementValue = doc.createTextNode(day.getDate().toString());
                    dayDateElement.appendChild(dayDateElementValue);

                    Element eventsElement = doc.createElement("events");
                    dayElement.appendChild(eventsElement);
                    int numberOfEvent = 1;
                    for (EventOfDay event : day.getEvents())
                    {
                        Element eventElement = doc.createElement("event" + numberOfEvent);
                        eventsElement.appendChild(eventElement);

                        Element timeOfEventElement = doc.createElement("time");
                        eventElement.appendChild(timeOfEventElement);
                        Text timeOfEventElementValue = doc.createTextNode(event.getTime().toString());
                        timeOfEventElement.appendChild(timeOfEventElementValue);

                        Element typeOfEventElement = doc.createElement("type");
                        eventElement.appendChild(typeOfEventElement);
                        Text typeOfEventElementValue = doc.createTextNode(event.getType().toString());
                        typeOfEventElement.appendChild(typeOfEventElementValue);

                        Element infoOfEventElement = doc.createElement("info");
                        eventElement.appendChild(infoOfEventElement);
                        Text infoOfEventElementValue = doc.createTextNode(event.getInfo());
                        infoOfEventElement.appendChild(infoOfEventElementValue);

                        numberOfEvent++;
                    }
                    numberOfDay++;
                }

                VBox vbox = null;
                for (Node node : schedule.getChildren())
                {
                    if (node instanceof ScrollPane)
                    {
                        vbox = (VBox) (((ScrollPane) node).getContent());
                        break;
                    }
                }

                Element linesElement = doc.createElement("lines");
                scheduleElement.appendChild(linesElement);
                assert vbox != null;
                int numberOfLine = 1;
                for (Node line : vbox.getChildren())
                {
                    if (line instanceof HBox)
                    {
                        VBox vboxOfLine = (VBox) (((HBox) line).getChildren().get(0));
                        Element lineElement = doc.createElement("line" + numberOfLine);
                        linesElement.appendChild(lineElement);

                        Element startTimeElement = doc.createElement("startTime");
                        lineElement.appendChild(startTimeElement);
                        int startHour = 0, startMinute = 0;

                        Element endTimeElement = doc.createElement("endTime");
                        lineElement.appendChild(endTimeElement);
                        int endHour = 0, endMinute = 0;

                        Element checkBoxStateElement = doc.createElement("checkBoxState");
                        lineElement.appendChild(checkBoxStateElement);
                        String checkBoxState = "false";

                        Element textElement = doc.createElement("text");
                        lineElement.appendChild(textElement);
                        String text = "";

                        for (Node hBoxes : vboxOfLine.getChildren())
                        {
                            if (hBoxes instanceof HBox)
                            {
                                for (Node node : ((HBox) hBoxes).getChildren())
                                {
                                    if (node instanceof ComboBox && node.getAccessibleHelp().equals("startTimeHours"))
                                    {
                                        startHour = Integer.parseInt((String) (((ComboBox) node).getValue()));
                                        continue;
                                    }
                                    if (node instanceof ComboBox && node.getAccessibleHelp().equals("endTimeHours"))
                                    {
                                        endHour = Integer.parseInt((String) (((ComboBox) node).getValue()));
                                        continue;
                                    }
                                    if (node instanceof ComboBox && node.getAccessibleHelp().equals("startTimeMinutes"))
                                    {
                                        startMinute = Integer.parseInt((String) (((ComboBox) node).getValue()));
                                        continue;
                                    }
                                    if (node instanceof ComboBox && node.getAccessibleHelp().equals("endTimeMinutes"))
                                    {
                                        endMinute = Integer.parseInt((String) (((ComboBox) node).getValue()));
                                        continue;
                                    }
                                    if (node instanceof CheckBox)
                                    {
                                        checkBoxState = String.valueOf(((CheckBox) node).isSelected());
                                        continue;
                                    }
                                    if (node instanceof TextArea)
                                    {
                                        text = ((TextArea) node).getText();
                                    }
                                }
                            }
                        }
                        Text startTimeElementValue = doc.createTextNode(String.valueOf(LocalTime.of(startHour, startMinute)));
                        startTimeElement.appendChild(startTimeElementValue);

                        Text endTimeElementValue = doc.createTextNode(String.valueOf(LocalTime.of(endHour, endMinute)));
                        endTimeElement.appendChild(endTimeElementValue);

                        Text checkBoxStateElementValue = doc.createTextNode(checkBoxState);
                        checkBoxStateElement.appendChild(checkBoxStateElementValue);

                        Text textElementValue = doc.createTextNode(text);
                        textElement.appendChild(textElementValue);

                        numberOfLine++;
                    }
                }
                id++;
            }
        }
        logger.info("ScheduleSD: end schedules saving");
    }

    public static void loadSchedulesFromXML(Document doc, XPath xPath) throws Exception
    {
        logger.info("ScheduleSD: start schedules loading");
        XPathExpression schedulesCompile = xPath.compile("count(/save/schedules/*)");
        int numberOfSchedules = Integer.parseInt((String)schedulesCompile.evaluate(doc, XPathConstants.STRING));
        for (int id = 1; id < numberOfSchedules + 1; id++)
        {
            ScheduleSD loadingSchedule = new ScheduleSD(true);
            loadingSchedule.start(Main.Stage);
            AnchorPane rootOfLoadingSchedule = loadingSchedule.getScheduleRoot();
            List<Day> daysSaveList = loadingSchedule.getScheduleDaysSaveList();
            Button saveButton = loadingSchedule.getSaveButton(rootOfLoadingSchedule);


            int numberOfTab = Integer.parseInt(xPath.evaluate("/save/schedules/schedule" + id + "/@tab", doc));
            //Установить в созданный элемент дополнительный текст, в котором будет лежать значение того таба, на котором элемент был создан
            rootOfLoadingSchedule.setAccessibleText(String.valueOf(numberOfTab));

            List<Node> tab = tabs.get(numberOfTab);
            tab.add(rootOfLoadingSchedule);
            boolean visibility = Boolean.parseBoolean(xPath.evaluate("/save/schedules/schedule" + id + "/visibility/text()", doc));
            rootOfLoadingSchedule.setVisible(visibility);

            double layoutX = Double.parseDouble(xPath.evaluate("/save/schedules/schedule" + id + "/layout/layoutX/text()", doc));
            double layoutY = Double.parseDouble(xPath.evaluate("/save/schedules/schedule" + id + "/layout/layoutY/text()", doc));
            rootOfLoadingSchedule.setLayoutX(layoutX);
            rootOfLoadingSchedule.setLayoutY(layoutY);

            if (Integer.parseInt(xPath.evaluate("count(/save/schedules/schedule" + id + "/copySettings/*)", doc)) != 0)
            {
                String repeat = xPath.evaluate("save/schedules/schedule" + id + "/copySettings/repeat/text()", doc);
                String period = xPath.evaluate("save/schedules/schedule" + id + "/copySettings/period/text()", doc);
                SchedulerCopySettings settings = new SchedulerCopySettings(repeat, period, true, id, saveButton);
                settings.start(Main.Stage);
                settings.fireSchedulerCopySettingsRadioButton(repeat);
                loadingSchedule.setCopySettings(settings);
                assert saveButton != null;
                saveButton.setDisable(true);
            }

            XPathExpression daysCompile = xPath.compile("count(/save/schedules/schedule" + id + "/daysWithEvents/*)");
            int countOfDaysWithEvents = Integer.parseInt((String)daysCompile.evaluate(doc, XPathConstants.STRING));
            for (int numberOfDay = 1; numberOfDay < countOfDaysWithEvents + 1; numberOfDay++)
            {

                LocalDate date = LocalDate.parse(xPath.evaluate("/save/schedules/schedule" + id + "/daysWithEvents/day" + numberOfDay + "/dayDate/text()", doc));
                Day day = new Day(date);

                XPathExpression eventsCompile = xPath.compile("count(/save/schedules/schedule" + id + "/daysWithEvents/day" + numberOfDay + "/events/*)");
                int countOfEvents = Integer.parseInt((String)eventsCompile.evaluate(doc, XPathConstants.STRING));

                for (int numberOfEvent = 1; numberOfEvent < countOfEvents + 1; numberOfEvent++)
                {
                    LocalTime time = LocalTime.parse(xPath.evaluate("/save/schedules/schedule" + id + "/daysWithEvents/day" + numberOfDay + "/events/event" + numberOfEvent + "/time/text()", doc));
                    Day.EventType type = Day.EventType.Schedule;//Enum.valueOf(Day.EventType.class, xPath.evaluate("/save/calendar/daysWithEvents/day" + numberOfDay + "/events/event" + numberOfEvent + "/type/text()", doc));
                    String info = xPath.evaluate("/save/schedules/schedule" + id + "/daysWithEvents/day" + numberOfDay + "/events/event" + numberOfEvent + "/info/text()", doc);

                    EventOfDay event = new EventOfDay(time, type, info);
                    day.addEvent(event);
                }
                boolean haveEvents = Day.checkOfDeprecatedEvents(day, true);
                if (haveEvents)
                {
                    daysSaveList.add(day);
                }
            }

            List<Day> daysWithEvents = CalendarSD.getDaysWithEvents();
            for (Day dayFromSaveList : daysSaveList)
            {
                Day dayFromEventList = CalendarSD.checkUsingOfThisDateOnEventList(dayFromSaveList.getDate());
                if (dayFromEventList == null)
                {
                    daysWithEvents.add(dayFromSaveList.clone());
                } else
                {
                    for (EventOfDay eventFromSaveList : dayFromEventList.getEvents())
                    {
                        dayFromEventList.addEvent(eventFromSaveList);
                    }
                }
            }

            VBox content = null;
            for (Node node : rootOfLoadingSchedule.getChildren())
            {
                if (node instanceof DatePicker)
                {
                    String dateOnString = xPath.evaluate("save/schedules/schedule" + id + "/date/text()", doc);
                    if (!(dateOnString.equals("null")))
                    {
                        LocalDate date = LocalDate.parse(dateOnString);
                        ((DatePicker) node).setValue(date);
                        loadingSchedule.setDate(date);
                    }
                }
                if (node instanceof ScrollPane)
                {
                    content = (VBox) (((ScrollPane) node).getContent());
                }
                if (node instanceof TextField)
                {
                    String scheduleName = xPath.evaluate("save/schedules/schedule" + id + "/nameOfSchedule/text()", doc);
                    ((TextField) node).setText(scheduleName);
                    loadingSchedule.nameOfSchedule = scheduleName;

                }
            }

            XPathExpression linesCompile = xPath.compile("count(/save/schedules/schedule" + id + "/lines/*)");
            int numberOfLines = Integer.parseInt((String)linesCompile.evaluate(doc, XPathConstants.STRING));
            for (int numberOfLine = 1; numberOfLine < numberOfLines + 1; numberOfLine++)
            {
                LocalTime startTime = LocalTime.parse(xPath.evaluate("save/schedules/schedule" + id + "/lines/line" + numberOfLine + "/startTime/text()", doc));
                LocalTime endTime = LocalTime.parse(xPath.evaluate("save/schedules/schedule" + id + "/lines/line" + numberOfLine + "/endTime/text()", doc));
                boolean checkBoxState = Boolean.parseBoolean(xPath.evaluate("save/schedules/schedule" + id + "/lines/line" + numberOfLine + "/checkBoxState/text()", doc));
                String text = xPath.evaluate("save/schedules/schedule" + id + "/lines/line" + numberOfLine + "/text/text()", doc);

                assert content != null;
                createNewLine(content, startTime, endTime, checkBoxState, text, id, saveButton);
            }
        }
        logger.info("ScheduleSD: end schedules loading");
    }
}
