package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
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

    public Map<EventOfDay, CheckBox> getCheckBoxes()
    {
        return checkBoxes;
    }

    private Map<CheckBox, List<CheckBox>> getGroupOfMainCheckBox()
    {
        return groupOfMainCheckBox;
    }

    public static Map<Integer, GoalSD> getGoals()
    {
        return goals;
    }

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

                            var line = goalSD.createNewLine(date, false, eventOfDay);
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

    /*public static void hideGoal(GoalSD goalSD)
    {
        var goalRoot = goalSD.getGoalRoot();
        tabs.get(Integer.parseInt(goalRoot.getAccessibleText())).remove(goalRoot);
        goalRoot.setVisible(false);
        goalRoot.setAccessibleText("-1");
        var elementsOfSelectedTab = tabs.get(-1);
        elementsOfSelectedTab.add(goalRoot);
    }

    public static void fireSaveButton(GoalSD goalSD)
    {
        var goalRoot = goalSD.getGoalRoot();
        for (var node : goalRoot.getChildren())
        {
            if (node instanceof Button && node.getAccessibleHelp() != null && node.getAccessibleHelp().equals("goalSaveButton"))
            {
                ((Button) node).fire();
                break;
            }
        }
    }*/

    /**
     * Получая имя цели возвращает объект цели, если такой объект существует
     *
     * @param nameOfGoal - имя искомого объекта
     * @return возвращает объект goalSD, который соответсвует параметру nameOfGoal, если такой существует, или null, если
     * такой не был найден
     */
    public static GoalSD getGoalFromGoalName(String nameOfGoal)
    {
        for (var goalSD : goals.values())
        {
            if (goalSD.getNameOfGoal().equals(nameOfGoal))
            {
                return goalSD;
            }
        }
        return null;
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

    public void removeTaskFromGoal(LocalDate date, CheckBox completeCheckBox)
    {
        for (var entry : this.getCheckBoxes().entrySet())
        {
            if (entry.getKey() != null && entry.getValue() != null)
            {
                var task = entry.getKey();
                if (entry.getValue().equals(completeCheckBox))
                {
                    UpcomingEvent.removeEventFromQueue(date, task);
                    Day.removeEventFromDay(date, task);
                    this.getCheckBoxes().remove(task, completeCheckBox);
                    return;
                }
            }
        }
    }

    public static String getGoalNameOfEventTask(EventOfDay eventOfDay)
    {
        for (var goalSD : goals.values())
        {
            if (goalSD.getCheckBoxes().containsKey(eventOfDay))
            {
                return goalSD.getNameOfGoal();
            }
        }
        return null;
    }

    public static void addTaskOnTaskMap(EventOfDay event)
    {
        for (var goalSD : goals.values())
        {
            var checkBoxes = goalSD.getCheckBoxes();
            for (var keyEvent : checkBoxes.keySet())
            {
                if (keyEvent.getType().equals(event.getType()) && keyEvent.getInfo().equals(event.getInfo()) && keyEvent.getTime().equals(event.getTime()))
                {
                    var checkBox = checkBoxes.get(keyEvent);
                    checkBoxes.remove(keyEvent);
                    checkBoxes.put(event, checkBox);
                    return;
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

    public VBox createNewLine(LocalDate date, boolean checkBoxState, EventOfDay goal)
    {
        var vbox = new VBox(4);

        var dateLabel = new Label(date.toString());
        Main.setRegion(dateLabel, 280, 25);

        var addNewTaskButton = new Button();
        Main.setRegion(addNewTaskButton, 25, 25);
        addNewTaskButton.setStyle("-fx-background-color: #f4f4f4");
        addNewTaskButton.setGraphic(new ImageView(new Image("Images/add25.png")));
        addNewTaskButton.setAccessibleHelp("addNewTaskButton");
        if (date.isBefore(LocalDate.now()))
        {
            addNewTaskButton.setDisable(true);
        }

        String completeAllText = "Всё выполнено";
        var completeAllCheckBox = new CheckBox();
        completeAllCheckBox.setText(completeAllText);
        completeAllCheckBox.getStylesheets().add(Objects.requireNonNull(mainCL.getResource("FXMLs/mediumCheckBox.css")).toExternalForm());
        completeAllCheckBox.setSelected(checkBoxState);
        completeAllCheckBox.setAccessibleHelp("completeAllCheckBox");

        var hbox = new HBox(6, addNewTaskButton, dateLabel, completeAllCheckBox);
        Main.setRegion(hbox, 460, 25);
        hbox.setPadding(new Insets(0, 8, 0, 8));

        var hSeparator = new Separator(Orientation.HORIZONTAL);
        Main.setRegion(hSeparator, 460, 6);
        vbox.getChildren().addAll(hbox, hSeparator);

        this.getCheckBoxes().put(goal, completeAllCheckBox);
        this.getGroupOfMainCheckBox().put(completeAllCheckBox, new ArrayList<>());

        addNewTaskButton.setOnAction(event -> createNewTaskLine(vbox, completeAllCheckBox, date, null, null, null));

        return vbox;
    }

    /**
     * @param line          - контейнер, куда будут добавляться элементы
     * @param mainCheckBox  - главный чекбокс группы задач, на который крепятся пару обработчиков. Если null, то он вызывается
     *                      из scrollPane и данные привязки не нужны.
     * @param date          - дата для создания ивента
     * @param time          - время для установки значений пользовательского интерфейса и создания ивента
     * @param text          - текст для установки значений пользовательского интерфейса и создания ивента
     * @param completeState - состояние чекбокса для установки значений пользовательского интерфейса и создания ивента
     */
    private void createNewTaskLine(VBox line, CheckBox mainCheckBox, LocalDate date, LocalTime time, String text, Boolean completeState)
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

        var vSeparatorBetweenTimeAndButton = new Separator(Orientation.VERTICAL);
        Main.setRegion(vSeparatorBetweenTimeAndButton, 35, 25);
        vSeparatorBetweenTimeAndButton.setVisible(false);

        String completeText = "Готово";
        var completeCheckBox = new CheckBox();
        completeCheckBox.setText(completeText);
        completeCheckBox.getStylesheets().add(Objects.requireNonNull(mainCL.getResource("FXMLs/mediumCheckBox.css")).toExternalForm());
        completeCheckBox.setPadding(new Insets(0, 13, 0, 0));
        completeCheckBox.setAccessibleHelp("completeCheckBox");

        var deleteButton = new Button();
        Main.setRegion(deleteButton, 25, 25);
        deleteButton.setGraphic(new ImageView(new Image("Images/delete35.png")));
        deleteButton.setStyle("-fx-background-color: #f4f4f4");
        deleteButton.setAccessibleHelp("taskDeleteButton");

        var hbox1 = new HBox(6, hours, minutes, preNotificationButton, vSeparatorBetweenTimeAndButton, completeCheckBox, deleteButton);
        Main.setRegion(hbox1, 460, 25);
        hbox1.setPadding(new Insets(0, 8, 0, 34));


        TextField textField = new TextField();
        if (text != null)
        {
            textField.setText(text);
        }
        Main.setRegion(textField, 374, 25);
        textField.setAccessibleHelp("textField");

        var saveButton = new Button();
        Main.setRegion(saveButton, 25, 25);
        saveButton.setGraphic(new ImageView(new Image("Images/save25.png")));
        saveButton.setStyle("-fx-background-color: #f4f4f4");
        if (completeState==null)
        {
            completeCheckBox.setDisable(true);
        }
        else
        {
            completeCheckBox.setSelected(completeState);
            saveButton.setDisable(true);
        }


        var hbox2 = new HBox(6, textField, saveButton);
        Main.setRegion(hbox2, 460, 25);
        hbox2.setPadding(new Insets(0, 8, 0, 34));

        var hSeparator = new Separator(Orientation.HORIZONTAL);
        Main.setRegion(hSeparator, 460, 4);
        hSeparator.setPadding(new Insets(8, 0, 8, 0));

        var vbox = new VBox(5, hbox1, hbox2, hSeparator);
        Main.setRegion(vbox, 460, 64);
        vbox.setAlignment(Pos.CENTER);

        line.getChildren().addAll(vbox);

        preNotificationButton.setOnAction(event ->
        {

        });

        deleteButton.setOnAction(event ->
        {
            line.getChildren().removeAll(vbox);
            this.removeTaskFromGoal(date, completeCheckBox);
        });

        //Костыль, чтобы иметь возможность переключать чекбоксы по прошедшим событиям нам обязательно нужны ключи в виде
        //событий. Вот и приходится это грузить сюда. В случае изменений ключ перезапишется и всё будет ок
        var timeOfKey = LocalTime.of(Integer.parseInt(hours.getValue()), Integer.parseInt(minutes.getValue()));
        String textOfKey = textField.getText();
        var key = new EventOfDay(timeOfKey, Day.EventType.Task, textOfKey);
        this.getCheckBoxes().put(key, completeCheckBox);

        saveButton.setOnMouseClicked(event ->
        {
            if (date.isAfter(LocalDate.now()) || date.equals(LocalDate.now()))
            {
                this.removeTaskFromGoal(date, completeCheckBox);
                completeCheckBox.setDisable(false);

                var timeOfTask = LocalTime.of(Integer.parseInt(hours.getValue()), Integer.parseInt(minutes.getValue()));
                String textOfTask = textField.getText();
                var daysWithEvents = CalendarSD.getDaysWithEvents();
                var day = CalendarSD.checkUsingOfThisDate(date);
                if (day == null)
                {
                    day = new Day(date);
                    daysWithEvents.add(day);
                }
                var task = new EventOfDay(timeOfTask, Day.EventType.Task, textOfTask);
                if (day.addEvent(task))
                {
                    this.getCheckBoxes().put(task, completeCheckBox);
                    UpcomingEvent.addEventToQueue(date, task);
                } else if (day.getEvents().size() == 0)
                {
                    daysWithEvents.remove(day);
                }
            } else
            {
                String alertText = "Нельзя сохранять изменения в прошедшем событии!";
                var alert = new Alert(Alert.AlertType.WARNING, alertText, ButtonType.OK);
                alert.showAndWait();
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

        hours.setOnAction(event ->
                {
                    if (date.isBefore(LocalDate.now()))
                    {
                        saveButton.setDisable(true);
                    } else
                    {
                        saveButton.setDisable(false);
                    }
                }
        );
        minutes.setOnAction(hours.getOnAction());
        textField.setOnKeyTyped(event ->
        {
            if (date.isBefore(LocalDate.now()))
            {
                saveButton.setDisable(true);
            } else
            {
                saveButton.setDisable(false);
            }
        });
    }

    public static void addGoalsToXML(Document doc, boolean createEmptyXML)
    {
        var rootElement = doc.getFirstChild();

        var goalsElement = doc.createElement("goals");
        rootElement.appendChild(goalsElement);
        if (!createEmptyXML)
        {
            int id = 1;
            for (var entry : goals.entrySet())
            {
                var goalSD = entry.getValue();
                var goal = goalSD.getGoalRoot();
                var goalElement = doc.createElement("goal" + id);
                goalElement.setAttribute("tab", goal.getAccessibleText());

                goalsElement.appendChild(goalElement);

                var visibilityElement = doc.createElement("visibility");
                goalElement.appendChild(visibilityElement);
                var visibilityValue = doc.createTextNode(String.valueOf(goal.isVisible()));
                visibilityElement.appendChild(visibilityValue);

                var layoutElement = doc.createElement("layout");
                goalElement.appendChild(layoutElement);

                var layoutX = doc.createElement("layoutX");
                layoutElement.appendChild(layoutX);
                var layoutXValue = doc.createTextNode(String.valueOf((int) (goal.getLayoutX())));
                layoutX.appendChild(layoutXValue);

                var layoutY = doc.createElement("layoutY");
                layoutElement.appendChild(layoutY);
                var layoutYValue = doc.createTextNode(String.valueOf((int) (goal.getLayoutY())));
                layoutY.appendChild(layoutYValue);

                var startDateElement = doc.createElement("startDate");
                goalElement.appendChild(startDateElement);
                var startDateValue = doc.createTextNode(goalSD.getStartDate().toString());
                startDateElement.appendChild(startDateValue);

                var endDateElement = doc.createElement("endDate");
                goalElement.appendChild(endDateElement);
                var endDateValue = doc.createTextNode(goalSD.getEndDate().toString());
                endDateElement.appendChild(endDateValue);

                var nameOfGoalElement = doc.createElement("nameOfGoal");
                goalElement.appendChild(nameOfGoalElement);
                var nameOfGoalValue = doc.createTextNode(goalSD.getNameOfGoal());
                nameOfGoalElement.appendChild(nameOfGoalValue);

                var linesElement = doc.createElement("lines");
                goalElement.appendChild(linesElement);

                var sortedLines = new TreeMap<>(goalSD.getTasksOfDay());

                int numberOfLine = 1;
                for (var line : sortedLines.values())
                {
                    var lineElement = doc.createElement("line" + numberOfLine);
                    linesElement.appendChild(lineElement);

                    var completeAllCheckBoxStateElement = doc.createElement("completeAllCheckBoxState");
                    lineElement.appendChild(completeAllCheckBoxStateElement);
                    Boolean completeAllCheckBoxState = null;

                    var tasksElement = doc.createElement("tasks");
                    lineElement.appendChild(tasksElement);

                    int numberOfTask = 1;
                    for (var tasks : line.getChildren())
                    {
                        if (completeAllCheckBoxState == null && tasks instanceof HBox)
                        {
                            for (var goalHBoxElements : ((HBox) tasks).getChildren())
                            {
                                if (goalHBoxElements instanceof CheckBox && goalHBoxElements.getAccessibleHelp() != null && goalHBoxElements.getAccessibleHelp().equals("completeAllCheckBox"))
                                {
                                    completeAllCheckBoxState = ((CheckBox) goalHBoxElements).isSelected();
                                    break;
                                }
                            }
                        }

                        if (tasks instanceof VBox)
                        {
                            var taskElement = doc.createElement("task" + numberOfTask);
                            tasksElement.appendChild(taskElement);

                            var timeElement = doc.createElement("time");
                            taskElement.appendChild(timeElement);
                            int hour = 0;
                            int minute = 0;

                            var textElement = doc.createElement("text");
                            taskElement.appendChild(textElement);
                            String text = "";

                            var completeElement = doc.createElement("complete");
                            taskElement.appendChild(completeElement);
                            boolean complete = false;

                            for (var hboxesOfTask : ((VBox) tasks).getChildren())
                            {
                                if (hboxesOfTask instanceof HBox)
                                {
                                    for (var hboxElement : ((HBox) hboxesOfTask).getChildren())
                                    {
                                        if (hboxElement.getAccessibleHelp() != null)
                                        {
                                            if (hboxElement instanceof ComboBox && hboxElement.getAccessibleHelp().equals("hours"))
                                            {
                                                hour = Integer.parseInt(((ComboBox<String>) hboxElement).getValue());
                                                continue;
                                            }
                                            if (hboxElement instanceof ComboBox && hboxElement.getAccessibleHelp().equals("minutes"))
                                            {
                                                minute = Integer.parseInt(((ComboBox<String>) hboxElement).getValue());
                                                continue;
                                            }
                                            if (hboxElement instanceof TextField && hboxElement.getAccessibleHelp().equals("textField"))
                                            {
                                                text = ((TextField) hboxElement).getText();
                                                continue;
                                            }
                                            if (hboxElement instanceof CheckBox && hboxElement.getAccessibleHelp().equals("completeCheckBox"))
                                            {
                                                complete = ((CheckBox) hboxElement).isSelected();
                                            }
                                        }
                                    }
                                }
                            }
                            var timeElementValue = doc.createTextNode(String.valueOf(LocalTime.of(hour, minute)));
                            timeElement.appendChild(timeElementValue);

                            var textElementValue = doc.createTextNode(text);
                            textElement.appendChild(textElementValue);

                            var completeElementValue = doc.createTextNode(String.valueOf(complete));
                            completeElement.appendChild(completeElementValue);

                            numberOfTask++;
                        }
                    }

                    var completeAllCheckBoxStateElementValue = doc.createTextNode(String.valueOf(completeAllCheckBoxState));
                    completeAllCheckBoxStateElement.appendChild(completeAllCheckBoxStateElementValue);
                    numberOfLine++;
                }
            }
        }
    }

    public static void loadGoalsFromXML(Document doc, XPath xPath) throws Exception
    {
        int numberOfGoals = xPath.evaluateExpression("count(/save/goals/*)", doc, Integer.class);
        for (int id = 1; id < numberOfGoals + 1; id++)
        {
            var loadingGoal = new GoalSD(true);
            loadingGoal.start(Main.Stage);
            var rootOfLoadingGoal = loadingGoal.getGoalRoot();

            int numberOfTab = Integer.parseInt(xPath.evaluate("/save/goals/goal" + id + "/@tab", doc));
            rootOfLoadingGoal.setAccessibleText(String.valueOf(numberOfTab));

            var tab = tabs.get(numberOfTab);
            tab.add(rootOfLoadingGoal);
            boolean visibility = Boolean.parseBoolean(xPath.evaluate("/save/goals/goal" + id + "/visibility/text()", doc));
            rootOfLoadingGoal.setVisible(visibility);

            double layoutX = Double.parseDouble(xPath.evaluate("/save/goals/goal" + id + "/layout/layoutX/text()", doc));
            double layoutY = Double.parseDouble(xPath.evaluate("/save/goals/goal" + id + "/layout/layoutY/text()", doc));
            rootOfLoadingGoal.setLayoutX(layoutX);
            rootOfLoadingGoal.setLayoutY(layoutY);

            var startDate = LocalDate.parse(xPath.evaluate("/save/goals/goal" + id + "/startDate/text()", doc));
            var endDate = LocalDate.parse(xPath.evaluate("/save/goals/goal" + id + "/endDate/text()", doc));
            String nameOfGoal = xPath.evaluate("/save/goals/goal" + id + "/nameOfGoal/text()", doc);

            loadingGoal.setStartDate(startDate);
            loadingGoal.setEndDate(endDate);
            loadingGoal.setNameOfGoal(nameOfGoal);

            int numberOfLines = xPath.evaluateExpression("count(/save/goals/goal" + id + "/lines/*)", doc, Integer.class);
            if (numberOfLines != 0)
            {
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

                rootOfLoadingGoal.getChildren().removeIf(node -> node instanceof Button && node.getAccessibleHelp() != null && node.getAccessibleHelp().equals("goalSaveButton"));
                rootOfLoadingGoal.getChildren().removeIf(node -> node instanceof Separator && node.getAccessibleHelp() != null && node.getAccessibleHelp().equals("goalSaveButtonSeparator"));

                var contentList = content.getChildren();

                for (int numberOfLine = 1; numberOfLine < numberOfLines + 1; numberOfLine++)
                {
                    boolean completeAllCheckBoxState = Boolean.parseBoolean(xPath.evaluate("/save/goals/goal" + id + "/lines/line" + numberOfLine + "/completeAllCheckBoxState/text()", doc));

                    var date = startDate.plusDays(numberOfLine - 1);
                    var timeOfEvent = LocalTime.of(23, 59);
                    /*var day = CalendarSD.checkUsingOfThisDate(date);
                    if (day == null)
                    {
                        day = new Day(date);
                        CalendarSD.getDaysWithEvents().add(day);
                    }*/
                    var eventOfDay = new EventOfDay(timeOfEvent, Day.EventType.Goal, nameOfGoal);
                    //day.addEvent(eventOfDay);

                    var line = loadingGoal.createNewLine(date, completeAllCheckBoxState, eventOfDay);
                    loadingGoal.getTasksOfDay().put(date, line);
                    contentList.add(line);

                    //UpcomingEvent.addEventToQueue(date, eventOfDay);
                    //updateDayIcons(day.getDate(), day.isHaveNotification(), day.isHaveGoal(), day.isHaveSchedule());

                    var checkBoxes = loadingGoal.getCheckBoxes();
                    int numberOfTasks = xPath.evaluateExpression("count(/save/goals/goal" + id + "/lines/line" + numberOfLine + "/tasks/*)", doc, Integer.class);
                    for (int numberOfTask = 1; numberOfTask < numberOfTasks + 1; numberOfTask++)
                    {
                        var time = LocalTime.parse(xPath.evaluate("/save/goals/goal" + id + "/lines/line" + numberOfLine + "/tasks/task" + numberOfTask + "/time/text()", doc));
                        String text = xPath.evaluate("/save/goals/goal" + id + "/lines/line" + numberOfLine + "/tasks/task" + numberOfTask + "/text/text()", doc);
                        boolean complete = Boolean.parseBoolean(xPath.evaluate("/save/goals/goal" + id + "/lines/line" + numberOfLine + "/tasks/task" + numberOfTask + "/complete/text()", doc));
                        var completeAllCheckBox = checkBoxes.get(eventOfDay);
                        loadingGoal.createNewTaskLine(line, completeAllCheckBox, date, time, text, complete);
                    }
                }
                rootOfLoadingGoal.getChildren().add(scrollPane);
            }
        }
    }
}
