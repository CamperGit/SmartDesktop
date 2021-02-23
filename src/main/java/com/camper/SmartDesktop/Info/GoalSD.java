package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.camper.SmartDesktop.Info.CalendarSD.updateDayIcons;
import static com.camper.SmartDesktop.Main.*;

public class GoalSD extends Application implements Initializable
{
    @FXML
    private Button goalCloseButton;
    @FXML
    private Button goalSaveButton;
    @FXML
    private Button goalProgressButton;
    @FXML
    private DatePicker goalStartDatePicker;
    @FXML
    private DatePicker goalEndDatePicker;
    @FXML
    private TextField goalNameTextField;
    @FXML
    private ToolBar goalToolBar;

    private boolean load = false;
    private int id;
    private LocalDate startDate = null;
    private LocalDate endDate = null;
    private String nameOfGoal = null;
    private AnchorPane GoalRoot;
    private Map<LocalDate, VBox> tasksOfDay = new HashMap<>();
    private Map<EventOfDay, CheckBox> checkBoxes = new HashMap<>();
    private Map<CheckBox, List<CheckBox>> groupOfMainCheckBox = new HashMap<>();
    private static int nextId = 1;
    private static AnchorPane selectedGoal;
    private static Map<Integer, GoalSD> goals = new HashMap<>();

    public GoalSD()
    {
    }

    private GoalSD(boolean load)
    {
        this.load = load;
    }

    public void setStartDate(LocalDate startDate)
    {
        this.startDate = startDate;
    }

    public LocalDate getStartDate()
    {
        return startDate;
    }

    public void setEndDate(LocalDate endDate)
    {
        this.endDate = endDate;
    }

    public LocalDate getEndDate()
    {
        return endDate;
    }

    public void setNameOfGoal(String nameOfGoal)
    {
        this.nameOfGoal = nameOfGoal;
    }

    public String getNameOfGoal()
    {
        return nameOfGoal;
    }

    public static void clearSaveList()
    {
        goals.clear();
        nextId = 1;
    }

    public Map<LocalDate, VBox> getTasksOfDay()
    {
        return tasksOfDay;
    }

    private Map<EventOfDay, CheckBox> getCheckBoxes()
    {
        return checkBoxes;
    }

    private Map<CheckBox, List<CheckBox>> getGroupOfMainCheckBox()
    {
        return groupOfMainCheckBox;
    }

    //public static Map<Integer,GoalSD> getGoals() {return goals;}

    private AnchorPane getGoalRoot()
    {
        return GoalRoot;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        GoalRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/goalRu.fxml")));
        GoalRoot.setLayoutX(80);
        GoalRoot.setLayoutY(30);
        this.id = nextId;
        nextId++;
        goals.put(this.id, this);
        GoalRoot.setAccessibleHelp(String.valueOf(this.id));

        addChild(GoalRoot);
        if (!load)
        {
            GoalRoot.setAccessibleText(String.valueOf(idOfSelectedTab));
            var elementsOfSelectedTab = tabs.get(idOfSelectedTab);
            elementsOfSelectedTab.add(GoalRoot);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        goalCloseButton.setOnAction(event ->
        {
            selectedGoal = (AnchorPane) (((Button) event.getSource()).getParent());
            goals.remove(Integer.parseInt(selectedGoal.getAccessibleHelp()));
            Main.root.getChildren().remove(selectedGoal);
        });

        goalToolBar.setOnMouseDragged(event ->
        {
            var root = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            NodeDragger.addDraggingProperty(root, event);
        });

        goalStartDatePicker.setOnAction(event ->
        {
            if (goalStartDatePicker.getValue() != null)
            {
                var root = (AnchorPane) (((DatePicker) event.getSource()).getParent());
                goals.get(Integer.parseInt(root.getAccessibleHelp())).setStartDate(goalStartDatePicker.getValue());
            }
        });

        goalEndDatePicker.setOnAction(event ->
        {
            if (goalEndDatePicker.getValue() != null)
            {
                var root = (AnchorPane) (((DatePicker) event.getSource()).getParent());
                goals.get(Integer.parseInt(root.getAccessibleHelp())).setEndDate(goalEndDatePicker.getValue());
            }
        });

        goalNameTextField.setOnKeyTyped(event ->
        {
            if (!(goalNameTextField.getText().equals("")))
            {
                var root = (AnchorPane) (((TextField) event.getSource()).getParent());
                goals.get(Integer.parseInt(root.getAccessibleHelp())).setNameOfGoal(goalNameTextField.getText());
            }
        });

        goalSaveButton.setOnAction(event ->
        {
            var goal = (AnchorPane) (((Button) event.getSource()).getParent());
            var goalSD = goals.get(Integer.parseInt(goal.getAccessibleHelp()));
            var startDate = goalSD.getStartDate();
            var nameOfGoal = goalSD.getNameOfGoal();
            if (startDate != null && goalSD.getEndDate() != null && nameOfGoal != null)
            {
                if (checkGoalName(goalSD))
                {
                    int days = (int) ChronoUnit.DAYS.between(goalSD.getStartDate(), goalSD.getEndDate());
                    if (days > 0)
                    {
                        for (var node : goal.getChildren())
                        {
                            if (node instanceof Separator && node.getAccessibleHelp() != null && node.getAccessibleHelp().equals("goalSaveButtonSeparator"))
                            {
                                goal.getChildren().removeAll(goalSaveButton, node);
                                break;
                            }
                        }
                        var content = new VBox(6);
                        content.setPadding(new Insets(8, 0, 0, 0));
                        var scrollPane = new ScrollPane(content);
                        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                        scrollPane.setLayoutY(94);
                        scrollPane.setMinWidth(460);
                        scrollPane.setPrefWidth(460);
                        scrollPane.setMaxWidth(460);
                        scrollPane.setPrefHeight(226);
                        scrollPane.maxHeight(226);

                        var contentList = content.getChildren();
                        for (int i = 0; i < days + 1; i++)
                        {
                            var date = startDate.plusDays(i);

                            var timeOfEvent = LocalTime.of(23, 59);
                            var day = CalendarSD.checkUsingOfThisDate(date);
                            if (day == null)
                            {
                                day = new Day(date);
                                CalendarSD.getDaysWithEvents().add(day);
                            }
                            var eventOfDay = new EventOfDay(timeOfEvent, Day.EventType.Goal, nameOfGoal);
                            day.addEvent(eventOfDay);

                            var line = goalSD.createNewLine(date, false);
                            goalSD.getTasksOfDay().put(date, line);
                            contentList.add(line);

                            UpcomingEvent.addEventToQueue(date, eventOfDay);
                            updateDayIcons(day.getDate(), day.isHaveNotification(), day.isHaveGoal(), day.isHaveSchedule());
                        }

                        goal.getChildren().add(scrollPane);
                    } else
                    {
                        //Для локализации
                        String alertText = "Конечная дата должна быть больше начальной!";
                        var alert = new Alert(Alert.AlertType.WARNING, alertText, ButtonType.OK);
                        alert.showAndWait();
                    }
                } else
                {
                    String alertText = "Имя цели обязано быть уникальным!";
                    var alert = new Alert(Alert.AlertType.WARNING, alertText, ButtonType.OK);
                    alert.showAndWait();
                }

            }
        });


    }

    public static void updateStateOfGoalCheckBoxes(EventOfDay eventOfDay, boolean newState)
    {
        for (var goalSD : goals.values())
        {
            var checkBox = goalSD.getCheckBoxes().get(eventOfDay);
            if (checkBox != null)
            {
                checkBox.setSelected(newState);
                for (var entry : goalSD.getGroupOfMainCheckBox().entrySet())
                {
                    boolean found = false;
                    var goalCheckBox = entry.getKey();
                    for (var taskCheckBox : entry.getValue())
                    {
                        if (taskCheckBox.equals(checkBox))
                        {
                            found = true;
                            break;
                        }
                    }
                    if (found)
                    {
                        goalCheckBox.setSelected(true);
                        for (var taskCheckBox : entry.getValue())
                        {
                            if (!(taskCheckBox.isSelected()))
                            {
                                goalCheckBox.setSelected(false);
                                break;
                            }
                        }
                        return;
                    }
                }
            }
        }
    }

    /**
     * Проверяет уникальность имени для цели. Это обязательное условие для работы программы
     *
     * @param goalSD объект, который не должен учавствовать в переборе
     * @return true - если имя уникально, false - если нет
     */
    private static boolean checkGoalName(GoalSD goalSD)
    {
        for (var goal : goals.values())
        {
            if (goal == goalSD)
            {
                continue;
            }
            if (goal.getNameOfGoal().equals(goalSD.getNameOfGoal())) //Мы можем быть уверены, что nameOfGoal != null
            {
                return false;
            }
        }
        return true;
    }

    public VBox createNewLine(LocalDate date, boolean checkBoxState)
    {
        var vbox = new VBox(4);

        var dateLabel = new Label(date.toString());
        Main.setRegion(dateLabel, 280, 25);

        var addNewTaskButton = new Button();
        Main.setRegion(addNewTaskButton, 25, 25);
        addNewTaskButton.setStyle("-fx-background-color: #f4f4f4");
        addNewTaskButton.setGraphic(new ImageView(new Image("Images/add25.png")));

        String completeAllText = "Всё выполнено";
        var completeAllCheckBox = new CheckBox();
        completeAllCheckBox.setText(completeAllText);
        completeAllCheckBox.getStylesheets().add(Objects.requireNonNull(mainCL.getResource("FXMLs/mediumCheckBox.css")).toExternalForm());
        completeAllCheckBox.setSelected(checkBoxState);

        var hbox = new HBox(6, addNewTaskButton, dateLabel, completeAllCheckBox);
        Main.setRegion(hbox, 460, 25);
        hbox.setPadding(new Insets(0, 8, 0, 8));

        var hSeparator = new Separator(Orientation.HORIZONTAL);
        Main.setRegion(hSeparator, 460, 6);
        vbox.getChildren().addAll(hbox, hSeparator);

        this.getGroupOfMainCheckBox().put(completeAllCheckBox, new ArrayList<>());

        addNewTaskButton.setOnAction(event ->
        {
            createNewTaskLine(vbox, completeAllCheckBox, date, null, null, false);
        });

        return vbox;
    }

    private void createNewTaskLine(VBox line, CheckBox mainCheckBox, LocalDate date, LocalTime time, String text, boolean completeState)
    {
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


        var hours = new ComboBox<String>();
        var minutes = new ComboBox<String>();
        Main.setRegion(hours, 55, 25);
        Main.setRegion(minutes, 55, 25);
        hours.getItems().addAll(hoursValues);
        minutes.getItems().addAll(minutesValues);
        hours.setAccessibleHelp("hours");
        minutes.setAccessibleHelp("minutes");
        if (time == null)
        {
            hours.setValue("00");
            minutes.setValue("00");
        } else
        {
            hours.setValue(time.getHour() < 10 ? "0" + time.getHour() : String.valueOf(time.getHour()));
            minutes.setValue(time.getMinute() < 10 ? "0" + time.getMinute() : String.valueOf(time.getMinute()));
        }

        String preNotificationButtonText = "Напомнить за";
        var preNotificationButton = new Button(preNotificationButtonText);
        Main.setRegion(preNotificationButton, 122, 25);
        preNotificationButton.setGraphic(new ImageView(new Image("Images/prenotification25.png")));
        preNotificationButton.setContentDisplay(ContentDisplay.RIGHT);
        //preNotificationButton.setStyle("-fx-background-color: #f4f4f4");

        var vSeparatorBetweenTimeAndButton = new Separator(Orientation.VERTICAL);
        Main.setRegion(vSeparatorBetweenTimeAndButton, 35, 25);
        vSeparatorBetweenTimeAndButton.setVisible(false);

        String completeText = "Готово";
        var completeCheckBox = new CheckBox();
        completeCheckBox.setText(completeText);
        completeCheckBox.getStylesheets().add(Objects.requireNonNull(mainCL.getResource("FXMLs/mediumCheckBox.css")).toExternalForm());
        completeCheckBox.setSelected(completeState);
        completeCheckBox.setPadding(new Insets(0, 13, 0, 0));
        completeCheckBox.setDisable(true);

        var deleteButton = new Button();
        Main.setRegion(deleteButton, 25, 25);
        deleteButton.setGraphic(new ImageView(new Image("Images/delete35.png")));
        deleteButton.setStyle("-fx-background-color: #f4f4f4");

        var hbox1 = new HBox(6, hours, minutes, preNotificationButton, vSeparatorBetweenTimeAndButton, completeCheckBox, deleteButton);
        Main.setRegion(hbox1, 460, 25);
        hbox1.setPadding(new Insets(0, 8, 0, 34));


        TextField textField = new TextField();
        if (text != null)
        {
            textField.setText(text);
        }
        Main.setRegion(textField, 374, 25);

        var saveButton = new Button();
        Main.setRegion(saveButton, 25, 25);
        saveButton.setGraphic(new ImageView(new Image("Images/save25.png")));
        saveButton.setStyle("-fx-background-color: #f4f4f4");

        var hbox2 = new HBox(6, textField, saveButton);
        Main.setRegion(hbox2, 460, 25);
        hbox2.setPadding(new Insets(0, 8, 0, 34));

        var vbox = new VBox(5, hbox1, hbox2);
        Main.setRegion(vbox, 460, 55);
        vbox.setAlignment(Pos.CENTER);

        var hSeparator = new Separator(Orientation.HORIZONTAL);
        Main.setRegion(hSeparator, 460, 4);

        line.getChildren().addAll(vbox, hSeparator);

        preNotificationButton.setOnAction(event ->
        {

        });

        deleteButton.setOnAction(event -> line.getChildren().removeAll(vbox, hSeparator));

        saveButton.setOnAction(event ->
        {
            completeCheckBox.setDisable(false);
            var timeOfEvent = LocalTime.of(Integer.parseInt(hours.getValue()), Integer.parseInt(minutes.getValue()));
            String textOfEvent = textField.getText();
            var daysWithEvents = CalendarSD.getDaysWithEvents();
            var day = CalendarSD.checkUsingOfThisDate(date);
            if (day == null)
            {
                day = new Day(date);
                daysWithEvents.add(day);
            }
            var task = new EventOfDay(timeOfEvent, Day.EventType.Task, textOfEvent);
            if (day.addEvent(task))
            {
                this.getCheckBoxes().put(task, completeCheckBox);
                UpcomingEvent.addEventToQueue(date, task);
            } else if (day.getEvents().size() == 0)
            {
                daysWithEvents.remove(day);
            }
            saveButton.setDisable(true);
        });

        mainCheckBox.addEventHandler(ActionEvent.ACTION, event ->
        {
            if (mainCheckBox.isSelected())
            {
                completeCheckBox.setSelected(true);
            } else
            {
                completeCheckBox.setSelected(false);
            }
        });

        this.getGroupOfMainCheckBox().get(mainCheckBox).add(completeCheckBox);

        completeCheckBox.setOnAction(e ->
        {
            for (var entry : this.getCheckBoxes().entrySet())
            {
                if (entry.getKey() != null && entry.getValue() != null)
                {
                    var task = entry.getKey();
                    if (entry.getValue().equals(completeCheckBox))
                    {
                        updateStateOfGoalCheckBoxes(task, completeCheckBox.isSelected());
                    }
                }
            }
        });

        hours.setOnAction(event->
        {
            saveButton.setDisable(false);
        });
        minutes.setOnAction(hours.getOnAction());
        textField.setOnKeyTyped(event->saveButton.setDisable(false));
    }
}
