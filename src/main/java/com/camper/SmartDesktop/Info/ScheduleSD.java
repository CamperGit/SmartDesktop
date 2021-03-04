package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import com.sun.prism.impl.BufferUtil;
import javafx.application.Application;
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

import javax.xml.xpath.XPath;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.camper.SmartDesktop.Info.CalendarSD.checkUsingOfThisDate;
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

    private boolean load = false;
    private AnchorPane ScheduleRoot;
    private Map<CheckBox, EventOfDay> eventsOfSchedule = new HashMap<>();
    private int id;
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
        ScheduleRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/scheduleRu.fxml")));
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
            var elementsOfSelectedTab = tabs.get(idOfSelectedTab);
            elementsOfSelectedTab.add(ScheduleRoot);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        scheduleCloseButtonIV.setImage(new Image("Images/delete30.png"));
        scheduleSettingsButtonIV.setImage(new Image("Images/settings30.png"));
        scheduleAddNewLineButtonIV.setImage(new Image("Images/add28.png"));

        scheduleAddNewLineButton.setOnAction(event ->
        {
            var scheduleRoot = (AnchorPane) (((Button) event.getSource()).getParent());
            for (var child : scheduleRoot.getChildren())
            {
                if (child instanceof ScrollPane)
                {
                    var vbox = (VBox) (((ScrollPane) child).getContent());
                    createNewLine(vbox, null, null, false, null, Integer.parseInt(scheduleRoot.getAccessibleHelp()), schedulerSaveButton);
                }
            }
            schedulerSaveButton.setDisable(false);
        });

        scheduleContentVbox.setSpacing(10);

        scheduleCloseButton.setOnAction(event ->
        {
            selectedSchedule = (AnchorPane) (((Button) event.getSource()).getParent());
            schedules.remove(Integer.parseInt(selectedSchedule.getAccessibleHelp()));
            Main.root.getChildren().remove(selectedSchedule);
        });

        scheduleToolBar.setOnMouseDragged(event ->
        {
            selectedSchedule = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            int id = Integer.parseInt(selectedSchedule.getAccessibleHelp());
            var settings = schedules.get(id).getCopySettings();
            if (settings != null)
            {
                Main.root.getChildren().remove(settings.getCopySettingsRoot());
            }
            NodeDragger.addDraggingProperty(selectedSchedule, event);
        });

        schedulerSaveButton.setOnAction(event ->
        {
            selectedSchedule = (AnchorPane) (((Button) event.getSource()).getParent());
            int id = Integer.parseInt(selectedSchedule.getAccessibleHelp());
            var scheduleSD = schedules.get(id);
            var date = scheduleSD.getDate();
            var mapWithEvents = scheduleSD.getEventsOfSchedule();

            if (date != null)
            {
                schedulerSaveButton.setDisable(true);
                var day = checkUsingOfThisDate(date);
                {
                    if (day == null)
                    {
                        day = new Day(date);
                    }
                    for (var entry : mapWithEvents.entrySet())
                    {
                        CheckBox state = entry.getKey();
                        EventOfDay eventOfDay = entry.getValue();
                        if (state.isSelected())
                        {
                            day.addEvent(eventOfDay);
                        }
                    }

                    if (day.getEvents().size() != 0)
                    {
                        CalendarSD.getDaysWithEvents().add(day);

                        var listOfDaysWithSchedules = new ArrayList<Day>();
                        listOfDaysWithSchedules.add(day);

                        if (scheduleSD.getCopySettings() != null)
                        {
                            var repeat = scheduleSD.getCopySettings().getRepeatSelected();
                            var period = scheduleSD.getCopySettings().getPeriodSelected();
                            listOfDaysWithSchedules.addAll(copySchedule(day, repeat, period));
                        }

                        UpcomingEvent.loadEventsToQueue(listOfDaysWithSchedules);
                        for (var dayFromList : listOfDaysWithSchedules)
                        {
                            updateDayIcons(dayFromList.getDate(), dayFromList.isHaveNotification(), dayFromList.isHaveGoal(), dayFromList.isHaveSchedule());
                        }
                    }
                }
            } else
            {
                //Для локализации
                String alertText = "Введите дату!";
                var alert = new Alert(Alert.AlertType.WARNING, alertText, ButtonType.OK);
                alert.showAndWait();
            }
        });

        schedulerDatePicker.setOnAction(event ->
        {
            var scheduleSD = schedules.get(Integer.parseInt((((DatePicker) (event.getSource())).getParent()).getAccessibleHelp()));
            scheduleSD.setDate(schedulerDatePicker.getValue());
            schedulerSaveButton.setDisable(false);
        });

        scheduleSettingsButton.setOnMouseClicked(event ->
        {
            int id = Integer.parseInt((((Button) (event.getSource())).getParent()).getAccessibleHelp());
            var scheduleSD = schedules.get(id);
            if (scheduleSD.getCopySettings() == null)
            {
                var settings = new SchedulerCopySettings(event, id, schedulerSaveButton);
                scheduleSD.setCopySettings(settings);
                try
                {
                    settings.start(Main.Stage);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            } else
            {
                var settings = scheduleSD.getCopySettings();
                Main.root.getChildren().remove(settings.getCopySettingsRoot());
                settings.showSettings(settings.getCopySettingsRoot(), event);
            }
        });
    }

    private Button getSaveButton(AnchorPane scheduleRoot)
    {
        for (var node : scheduleRoot.getChildren())
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
        var list = new ArrayList<Day>();
        var date = day.getDate();
        if (repeat == SchedulerCopySettings.ScheduleSettingsRepeat.DAY && period == SchedulerCopySettings.ScheduleSettingsPeriod.FOR_A_WEEK)
        {
            var limit = date.plusWeeks(1).minusDays(1);
            while (date.isBefore(limit))
            {
                date = date.plusDays(1);
                list.add(copyScheduleEventsFromDay(date, day));
            }
        }
        if (repeat == SchedulerCopySettings.ScheduleSettingsRepeat.DAY && period == SchedulerCopySettings.ScheduleSettingsPeriod.FOR_A_MONTH)
        {
            var limit = date.plusMonths(1);
            while (date.isBefore(limit))
            {
                date = date.plusDays(1);
                list.add(copyScheduleEventsFromDay(date, day));
            }
        }
        if (repeat == SchedulerCopySettings.ScheduleSettingsRepeat.WEEK && period == SchedulerCopySettings.ScheduleSettingsPeriod.FOR_A_MONTH)
        {
            var limit = date.plusMonths(1).minusWeeks(1);
            while (date.isBefore(limit))
            {
                date = date.plusWeeks(1);
                list.add(copyScheduleEventsFromDay(date, day));
            }
        }
        if (repeat == SchedulerCopySettings.ScheduleSettingsRepeat.WEEK && period == SchedulerCopySettings.ScheduleSettingsPeriod.FOR_A_YEAR)
        {
            var limit = date.plusYears(1).minusWeeks(1);
            while (date.isBefore(limit))
            {
                date = date.plusWeeks(1);
                list.add(copyScheduleEventsFromDay(date, day));
            }
        }
        if (repeat == SchedulerCopySettings.ScheduleSettingsRepeat.MONTH && period == SchedulerCopySettings.ScheduleSettingsPeriod.FOR_A_YEAR)
        {
            var limit = date.plusYears(1);
            while (date.isBefore(limit))
            {
                date = date.plusMonths(1);
                list.add(copyScheduleEventsFromDay(date, day));
            }
        }
        if (repeat == SchedulerCopySettings.ScheduleSettingsRepeat.YEAR && period == SchedulerCopySettings.ScheduleSettingsPeriod.DONT)
        {
            var limit = date.plusYears(10);
            while (date.isBefore(limit))
            {
                date = date.plusYears(1);
                list.add(copyScheduleEventsFromDay(date, day));
            }
        }
        return list;
    }

    private static Day copyScheduleEventsFromDay(LocalDate date, Day day)
    {
        var newDay = checkUsingOfThisDate(date);
        {
            if (newDay == null)
            {
                newDay = new Day(date);
                CalendarSD.getDaysWithEvents().add(newDay);
            }
            for (var event : day.getEvents())
            {
                //Мы же копируем только расписание, поэтому другие ивенты, если они есть, добавлять не нужно
                if (event.getType() == Day.EventType.Schedule)
                {
                    newDay.addEvent(event);
                }
            }
        }
        return newDay;
    }

    public static void createNewLine(VBox content, LocalTime startTime, LocalTime endTime, boolean checkBoxState, String info, int id, Button schedulerSaveButton)
    {
        var hbox = new HBox();
        Main.setRegion(hbox, 460, 25);

        var leftOffset1 = new Separator(Orientation.VERTICAL);
        leftOffset1.setVisible(false);
        leftOffset1.setPrefWidth(5);

        var hours1 = new ComboBox<String>();
        hours1.setLayoutX(5);
        var hours2 = new ComboBox<String>();
        var minutes1 = new ComboBox<String>();
        var minutes2 = new ComboBox<String>();

        Main.setRegion(hours1, 55, 25);
        Main.setRegion(hours2, 55, 25);
        Main.setRegion(minutes1, 55, 25);
        Main.setRegion(minutes2, 55, 25);

        var hoursValues = new ArrayList<String>()
        {{
            addAll(Stream.iterate(0, n -> n < 10, n -> ++n).map(Object::toString).map(n -> "0" + n).collect(Collectors.toList()));
            addAll(IntStream.iterate(10, n -> n < 24, n -> ++n).mapToObj(Integer::toString).collect(Collectors.toList()));
        }};
        var minutesValues = new ArrayList<String>()
        {{
            addAll(Stream.iterate(0, n -> n < 10, n -> ++n).map(Object::toString).map(n -> "0" + n).collect(Collectors.toList()));
            addAll(IntStream.iterate(10, n -> n < 60, n -> ++n).mapToObj(Integer::toString).collect(Collectors.toList()));
        }};

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

        var separatorBetweenTime = new Separator(Orientation.VERTICAL);
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


        var dash = new Label("-");
        dash.setAlignment(Pos.CENTER);
        dash.setFont(new Font(20));
        Main.setRegion(dash, 12, 30);

        var spacingBetweenTimeAndCheckBox = new Separator(Orientation.VERTICAL);
        spacingBetweenTimeAndCheckBox.setVisible(false);
        spacingBetweenTimeAndCheckBox.setPrefWidth(17);

        var addEventCheckBox = new CheckBox();
        addEventCheckBox.setLayoutX(5);
        addEventCheckBox.setText("Показать уведомление");
        addEventCheckBox.getStylesheets().add(Objects.requireNonNull(mainCL.getResource("FXMLs/mediumCheckBox.css")).toExternalForm());
        addEventCheckBox.setSelected(checkBoxState);


        var spacingBetweenCheckBoxAndDeletingButton = new Separator(Orientation.VERTICAL);
        spacingBetweenCheckBoxAndDeletingButton.setVisible(false);
        spacingBetweenCheckBoxAndDeletingButton.setPrefWidth(6);

        var deletingButton = new Button();
        Main.setRegion(deletingButton, 25, 25);
        deletingButton.setGraphic(new ImageView(new Image("Images/minus25.png")));

        hbox.getChildren().addAll(leftOffset1, hours1, minutes1, dash, hours2, minutes2, spacingBetweenTimeAndCheckBox, addEventCheckBox, spacingBetweenCheckBoxAndDeletingButton, deletingButton);


        var leftOffset2 = new Separator(Orientation.VERTICAL);
        leftOffset2.setVisible(false);
        leftOffset2.setPrefWidth(5);

        var text = new TextArea();
        text.setWrapText(true);
        if (info != null)
        {
            text.setText(info);
        }
        Main.setRegion(text, 435, 25);
        var hbox2 = new HBox(leftOffset2, text);

        var vbox = new VBox(8, hbox, hbox2);
        Main.setRegion(vbox, 460, 55);
        var line = new HBox(5, vbox);
        Main.setRegion(line, 460, 55);

        var hSeparator = new Separator(Orientation.HORIZONTAL);
        hSeparator.setPrefHeight(10);
        content.getChildren().addAll(line, hSeparator);

        addEventCheckBox.setOnAction(event ->
        {
            var parent = ((Node) (event.getSource())).getParent();
            var scheduleSD = schedules.get(returnAnchorId(parent));
            var map = scheduleSD.getEventsOfSchedule();
            if (addEventCheckBox.isSelected())
            {
                var eventOfDay = new EventOfDay(LocalTime.of(Integer.parseInt(hours1.getValue()), Integer.parseInt(minutes1.getValue())), Day.EventType.Schedule, text.getText());
                map.put(addEventCheckBox, eventOfDay);
            } else
            {
                map.remove(addEventCheckBox);
            }
            schedulerSaveButton.setDisable(false);
        });

        text.setOnKeyTyped(event ->
        {
            var parent = ((Node) (event.getSource())).getParent();
            var scheduleSD = schedules.get(returnAnchorId(parent));
            var map = scheduleSD.getEventsOfSchedule();
            if (addEventCheckBox.isSelected())
            {
                var eventOfDay = new EventOfDay(LocalTime.of(Integer.parseInt(hours1.getValue()), Integer.parseInt(minutes1.getValue())), Day.EventType.Schedule, text.getText());
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
            content.getChildren().remove(line);
            content.getChildren().remove(hSeparator);
        });

        //Необходимо при загрузке, чтобы добавить в мапу старые ивенты
        if (addEventCheckBox.isSelected())
        {
            var map = schedules.get(id).getEventsOfSchedule();
            var eventOfDay = new EventOfDay(LocalTime.of(Integer.parseInt(hours1.getValue()), Integer.parseInt(minutes1.getValue())), Day.EventType.Schedule, text.getText());
            map.put(addEventCheckBox, eventOfDay);
        }
    }

    public static void addSchedulesToXML(Document doc, boolean createEmptyXML)
    {
        var rootElement = doc.getFirstChild();

        var schedulesElement = doc.createElement("schedules");
        rootElement.appendChild(schedulesElement);
        if (!createEmptyXML)
        {
            int id = 1;
            for (var entry : schedules.entrySet())
            {
                //var scheduleSD = schedules.get(entry.getKey());
                var scheduleSD = entry.getValue();
                var schedule = scheduleSD.getScheduleRoot();
                var scheduleElement = doc.createElement("schedule" + id);
                scheduleElement.setAttribute("tab", schedule.getAccessibleText());

                schedulesElement.appendChild(scheduleElement);

                var visibilityElement = doc.createElement("visibility");
                scheduleElement.appendChild(visibilityElement);
                var visibilityValue = doc.createTextNode(String.valueOf(schedule.isVisible()));
                visibilityElement.appendChild(visibilityValue);

                var layoutElement = doc.createElement("layout");
                scheduleElement.appendChild(layoutElement);

                var layoutX = doc.createElement("layoutX");
                layoutElement.appendChild(layoutX);
                var layoutXValue = doc.createTextNode(String.valueOf((int) (schedule.getLayoutX())));
                layoutX.appendChild(layoutXValue);

                var layoutY = doc.createElement("layoutY");
                layoutElement.appendChild(layoutY);
                var layoutYValue = doc.createTextNode(String.valueOf((int) (schedule.getLayoutY())));
                layoutY.appendChild(layoutYValue);

                var copySettingsElement = doc.createElement("copySettings");
                scheduleElement.appendChild(copySettingsElement);
                if (scheduleSD.getCopySettings() != null)
                {
                    var settings = scheduleSD.getCopySettings();
                    var repeatElement = doc.createElement("repeat");
                    copySettingsElement.appendChild(repeatElement);
                    var repeatElementValue = doc.createTextNode(settings.getRepeatSelected().name());
                    repeatElement.appendChild(repeatElementValue);

                    var periodElement = doc.createElement("period");
                    copySettingsElement.appendChild(periodElement);
                    var periodElementValue = doc.createTextNode(settings.getPeriodSelected().name());
                    periodElement.appendChild(periodElementValue);
                }

                var dateElement = doc.createElement("date");
                scheduleElement.appendChild(dateElement);
                var dateElementValue = doc.createTextNode(String.valueOf(scheduleSD.getDate()));
                dateElement.appendChild(dateElementValue);

                VBox vbox = null;
                for (Node node : schedule.getChildren())
                {
                    if (node instanceof ScrollPane)
                    {
                        vbox = (VBox) (((ScrollPane) node).getContent());
                        break;
                    }
                }

                var linesElement = doc.createElement("lines");
                scheduleElement.appendChild(linesElement);
                assert vbox != null;
                int numberOfLine = 1;
                for (var line : vbox.getChildren())
                {
                    if (line instanceof HBox)
                    {
                        VBox vboxOfLine = (VBox) (((HBox) line).getChildren().get(0));
                        var lineElement = doc.createElement("line" + numberOfLine);
                        linesElement.appendChild(lineElement);

                        var startTimeElement = doc.createElement("startTime");
                        lineElement.appendChild(startTimeElement);
                        int startHour = 0, startMinute = 0;

                        var endTimeElement = doc.createElement("endTime");
                        lineElement.appendChild(endTimeElement);
                        int endHour = 0, endMinute = 0;

                        var checkBoxStateElement = doc.createElement("checkBoxState");
                        lineElement.appendChild(checkBoxStateElement);
                        String checkBoxState = "false";

                        var textElement = doc.createElement("text");
                        lineElement.appendChild(textElement);
                        String text = "";

                        for (var hBoxes : vboxOfLine.getChildren())
                        {
                            if (hBoxes instanceof HBox)
                            {
                                for (var node : ((HBox) hBoxes).getChildren())
                                {
                                    if (node instanceof ComboBox && node.getAccessibleHelp().equals("startTimeHours"))
                                    {
                                        startHour = Integer.parseInt(((ComboBox<String>) node).getValue());
                                        continue;
                                    }
                                    if (node instanceof ComboBox && node.getAccessibleHelp().equals("endTimeHours"))
                                    {
                                        endHour = Integer.parseInt(((ComboBox<String>) node).getValue());
                                        continue;
                                    }
                                    if (node instanceof ComboBox && node.getAccessibleHelp().equals("startTimeMinutes"))
                                    {
                                        startMinute = Integer.parseInt(((ComboBox<String>) node).getValue());
                                        continue;
                                    }
                                    if (node instanceof ComboBox && node.getAccessibleHelp().equals("endTimeMinutes"))
                                    {
                                        endMinute = Integer.parseInt(((ComboBox<String>) node).getValue());
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
                        var startTimeElementValue = doc.createTextNode(String.valueOf(LocalTime.of(startHour, startMinute)));
                        startTimeElement.appendChild(startTimeElementValue);

                        var endTimeElementValue = doc.createTextNode(String.valueOf(LocalTime.of(endHour, endMinute)));
                        endTimeElement.appendChild(endTimeElementValue);

                        var checkBoxStateElementValue = doc.createTextNode(checkBoxState);
                        checkBoxStateElement.appendChild(checkBoxStateElementValue);

                        var textElementValue = doc.createTextNode(text);
                        textElement.appendChild(textElementValue);

                        numberOfLine++;
                    }
                }
                id++;
            }
        }
    }

    public static void loadSchedulesFromXML(Document doc, XPath xPath) throws Exception
    {
        int numberOfSchedules = xPath.evaluateExpression("count(/save/schedules/*)", doc, Integer.class);
        for (int id = 1; id < numberOfSchedules + 1; id++)
        {
            var loadingSchedule = new ScheduleSD(true);
            loadingSchedule.start(Main.Stage);
            var rootOfLoadingSchedule = loadingSchedule.getScheduleRoot();
            var saveButton = loadingSchedule.getSaveButton(rootOfLoadingSchedule);
            assert saveButton != null;
            saveButton.setDisable(true);

            int numberOfTab = Integer.parseInt(xPath.evaluate("/save/schedules/schedule" + id + "/@tab", doc));
            //Установить в созданный элемент дополнительный текст, в котором будет лежать значение того таба, на котором элемент был создан
            rootOfLoadingSchedule.setAccessibleText(String.valueOf(numberOfTab));

            var tab = tabs.get(numberOfTab);
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
                var settings = new SchedulerCopySettings(repeat, period, true, id, saveButton);
                settings.start(Main.Stage);
                loadingSchedule.setCopySettings(settings);
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
            }

            int numberOfLines = xPath.evaluateExpression("count(/save/schedules/schedule" + id + "/lines/*)", doc, Integer.class);
            for (int numberOfLine = 1; numberOfLine < numberOfLines + 1; numberOfLine++)
            {
                LocalTime startTime = LocalTime.parse(xPath.evaluate("save/schedules/schedule" + id + "/lines/line" + numberOfLine + "/startTime/text()", doc));
                LocalTime endTime = LocalTime.parse(xPath.evaluate("save/schedules/schedule" + id + "/lines/line" + numberOfLine + "/endTime/text()", doc));
                boolean checkBoxState = Boolean.parseBoolean(xPath.evaluate("save/schedules/schedule" + id + "/lines/line" + numberOfLine + "/checkBoxState/text()", doc));
                String text = xPath.evaluate("save/schedules/schedule" + id + "/lines/line" + numberOfLine + "/text/text()", doc);

                assert content != null;
                createNewLine(content, startTime, endTime, checkBoxState, text, id, saveButton );
            }
        }
    }
}
