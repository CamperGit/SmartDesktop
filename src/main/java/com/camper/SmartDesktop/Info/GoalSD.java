package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @FXML
    private ImageView goalCloseButtonIV;
    @FXML
    private Label goalFromLabel;
    @FXML
    private Label goalToLabel;

    private boolean load = false;
    private int id;
    private LocalDate startDate = null;
    private LocalDate endDate = null;
    private String nameOfGoal = null;
    private AnchorPane GoalRoot;
    private GoalSDProgressInfo progressInfo;
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

    private GoalSDProgressInfo getProgressInfo()
    {
        return progressInfo;
    }

    private void setProgressInfo(GoalSDProgressInfo progressInfo)
    {
        this.progressInfo = progressInfo;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        logger.info("GoalSD: begin start method");
        GoalRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/goal.fxml")));
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
            List<Node> elementsOfSelectedTab = tabs.get(idOfSelectedTab);
            elementsOfSelectedTab.add(GoalRoot);
        }
        logger.info("GoalSD: end start method");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        logger.info("GoalSD: begin initialize method");
        goalNameTextField.setPromptText(languageBundle.getString("goalNamePromptTextTextField"));
        goalFromLabel.setText(languageBundle.getString("goalFromLabel"));
        goalToLabel.setText(languageBundle.getString("goalToLabel"));
        goalProgressButton.setText(languageBundle.getString("goalProgressButton"));
        goalSaveButton.setText(languageBundle.getString("goalSaveButton"));

        goalCloseButtonIV.setImage(new Image("Images/delete30.png"));

        goalCloseButton.setOnAction(event ->
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, languageBundle.getString("goalDeleteAlert"), new ButtonType("Да", ButtonBar.ButtonData.YES), new ButtonType("Нет", ButtonBar.ButtonData.NO));
            ButtonType alertResult = alert.showAndWait().orElse(ButtonType.CANCEL);
            if (alertResult.getButtonData().equals(ButtonBar.ButtonData.YES))
            {
                selectedGoal = (AnchorPane) (((Button) event.getSource()).getParent());
                GoalSD goalSD = goals.remove(Integer.parseInt(selectedGoal.getAccessibleHelp()));
                Map<EventOfDay, CheckBox> checkBoxes = goalSD.getCheckBoxes();
                for (Map.Entry<EventOfDay, CheckBox> entry : checkBoxes.entrySet())
                {
                    EventOfDay eventOfDay = entry.getKey();
                    LocalDate date = LocalDate.parse(entry.getValue().getAccessibleText());
                    UpcomingEvent.removeEventFromQueue(date, eventOfDay);
                    Day.removeEventFromDay(date, eventOfDay);
                    Day day = CalendarSD.checkUsingOfThisDateOnEventList(date);
                    if (day != null)
                    {
                        updateDayIcons(date, day.isHaveNotification(), day.isHaveGoal(), day.isHaveSchedule());
                    }
                    else
                    {
                        updateDayIcons(date, false, false, false);
                    }
                }
                checkBoxes.clear();
                Main.root.getChildren().remove(selectedGoal);
                logger.info("GoalSD: delete goal");
            }
        });

        goalToolBar.setOnMouseDragged(event ->
        {
            AnchorPane root = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            NodeDragger.addDraggingProperty(root, event);
            int id = Integer.parseInt(root.getAccessibleHelp());
            GoalSDProgressInfo progress = goals.get(id).getProgressInfo();
            if (progress != null)
            {
                Main.root.getChildren().remove(progress.getGoalProgressRoot());
            }
        });

        goalStartDatePicker.setOnAction(event ->
        {
            if (goalStartDatePicker.getValue() != null)
            {
                AnchorPane root = (AnchorPane) (((DatePicker) event.getSource()).getParent());
                goals.get(Integer.parseInt(root.getAccessibleHelp())).setStartDate(goalStartDatePicker.getValue());
            }
        });

        goalEndDatePicker.setOnAction(event ->
        {
            if (goalEndDatePicker.getValue() != null)
            {
                AnchorPane root = (AnchorPane) (((DatePicker) event.getSource()).getParent());
                goals.get(Integer.parseInt(root.getAccessibleHelp())).setEndDate(goalEndDatePicker.getValue());
            }
        });

        goalNameTextField.setOnKeyTyped(event ->
        {
            if (!(goalNameTextField.getText().equals("")))
            {
                AnchorPane root = (AnchorPane) (((TextField) event.getSource()).getParent());
                goals.get(Integer.parseInt(root.getAccessibleHelp())).setNameOfGoal(goalNameTextField.getText());
            }
        });

        goalProgressButton.setOnMouseClicked(event ->
        {
            AnchorPane root = (AnchorPane) (((Button) event.getSource()).getParent());
            GoalSD goalSD = goals.get(Integer.parseInt(root.getAccessibleHelp()));
            try
            {
                if (goalSD.getProgressInfo() == null)
                {
                    GoalSDProgressInfo progress = new GoalSDProgressInfo(goalSD.id, event);
                    progress.start(Main.Stage);
                    progress.updatePieChart(goalSD.groupOfMainCheckBox, goalSD.checkBoxes);
                    goalSD.setProgressInfo(progress);
                } else
                {
                    Main.root.getChildren().remove(goalSD.getProgressInfo().getGoalProgressRoot());
                    GoalSDProgressInfo progress = goalSD.getProgressInfo();
                    progress.setMouseEvent(event);
                    progress.updatePieChart(goalSD.groupOfMainCheckBox, goalSD.checkBoxes);
                }

            } catch (Exception e)
            {
                logger.error("GoalSD: progress FXML load error");
            }
        });

        goalSaveButton.setOnAction(event ->
        {
            AnchorPane goal = (AnchorPane) (((Button) event.getSource()).getParent());
            GoalSD goalSD = goals.get(Integer.parseInt(goal.getAccessibleHelp()));
            LocalDate startDate = goalSD.getStartDate();
            String nameOfGoal = goalSD.getNameOfGoal();
            if (startDate != null && goalSD.getEndDate() != null && nameOfGoal != null)
            {
                if (checkGoalName(goalSD))
                {
                    int days = (int) ChronoUnit.DAYS.between(goalSD.getStartDate(), goalSD.getEndDate());
                    if (days > 0)
                    {
                        for (Node node : goal.getChildren())
                        {
                            if (node instanceof Separator && node.getAccessibleHelp() != null && node.getAccessibleHelp().equals("goalSaveButtonSeparator"))
                            {
                                goal.getChildren().removeAll(goalSaveButton, node);
                                break;
                            }
                        }
                        VBox content = new VBox(6);
                        content.setPadding(new Insets(8, 0, 0, 0));
                        ScrollPane scrollPane = new ScrollPane(content);
                        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                        scrollPane.setLayoutY(94);
                        scrollPane.setMinWidth(460);
                        scrollPane.setPrefWidth(460);
                        scrollPane.setMaxWidth(460);
                        scrollPane.setPrefHeight(226);
                        scrollPane.maxHeight(226);

                        ObservableList<Node> contentList = content.getChildren();
                        for (int i = 0; i < days + 1; i++)
                        {
                            LocalDate date = startDate.plusDays(i);

                            LocalTime timeOfEvent = LocalTime.of(23, 59);
                            Day day = CalendarSD.checkUsingOfThisDateOnEventList(date);
                            if (day == null)
                            {
                                day = new Day(date);
                                CalendarSD.getDaysWithEvents().add(day);
                            }
                            EventOfDay eventOfDay = new EventOfDay(timeOfEvent, Day.EventType.Goal, nameOfGoal);
                            day.addEvent(eventOfDay);

                            VBox line = goalSD.createNewLine(date, false, eventOfDay);
                            goalSD.getTasksOfDay().put(date, line);
                            contentList.add(line);

                            UpcomingEvent.addEventToQueue(date, eventOfDay);
                            updateDayIcons(day.getDate(), day.isHaveNotification(), day.isHaveGoal(), day.isHaveSchedule());
                        }

                        goal.getChildren().add(scrollPane);
                        goalNameTextField.setEditable(false);
                        goalStartDatePicker.setEditable(false);
                        goalEndDatePicker.setEditable(false);
                    } else
                    {
                        Alert alert = new Alert(Alert.AlertType.WARNING, languageBundle.getString("goalDateAlert"), ButtonType.OK);
                        alert.showAndWait();
                    }
                } else
                {
                    Alert alert = new Alert(Alert.AlertType.WARNING, languageBundle.getString("goalNameAlert"), ButtonType.OK);
                    alert.showAndWait();
                }

            }
        });
        logger.info("GoalSD: end initialize method");
    }

    /**
     * Получая имя цели возвращает объект цели, если такой объект существует
     *
     * @param nameOfGoal - имя искомого объекта
     * @return возвращает объект goalSD, который соответсвует параметру nameOfGoal, если такой существует, или null, если
     * такой не был найден
     */
    public static GoalSD getGoalFromGoalName(String nameOfGoal)
    {
        for (GoalSD goalSD : goals.values())
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
        for (GoalSD goalSD : goals.values())
        {
            CheckBox checkBox = goalSD.getCheckBoxes().get(eventOfDay);
            if (checkBox != null)
            {
                checkBox.setSelected(newState);
                for (Map.Entry<CheckBox, List<CheckBox>> entry : goalSD.getGroupOfMainCheckBox().entrySet())
                {
                    boolean found = false;
                    CheckBox goalCheckBox = entry.getKey();
                    for (CheckBox taskCheckBox : entry.getValue())
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
                        for (CheckBox taskCheckBox : entry.getValue())
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
        for (Map.Entry<EventOfDay, CheckBox> entry : this.getCheckBoxes().entrySet())
        {
            if (entry.getKey() != null && entry.getValue() != null)
            {
                EventOfDay task = entry.getKey();
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
        for (GoalSD goalSD : goals.values())
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
        for (GoalSD goalSD : goals.values())
        {
            Map<EventOfDay, CheckBox> checkBoxes = goalSD.getCheckBoxes();
            for (EventOfDay keyEvent : checkBoxes.keySet())
            {
                if (keyEvent.equals(event))
                {
                    CheckBox checkBox = checkBoxes.remove(keyEvent);
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
        for (GoalSD goal : goals.values())
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
        VBox vbox = new VBox(4);

        Label dateLabel = new Label(date.toString());
        Main.setRegion(dateLabel, 280, 25);

        Button addNewTaskButton = new Button();
        Main.setRegion(addNewTaskButton, 25, 25);
        addNewTaskButton.setStyle("-fx-background-color: #f4f4f4");
        addNewTaskButton.setGraphic(new ImageView(new Image("Images/add25.png")));
        addNewTaskButton.setAccessibleHelp("addNewTaskButton");
        if (date.isBefore(LocalDate.now()))
        {
            addNewTaskButton.setDisable(true);
        }

        CheckBox completeAllCheckBox = new CheckBox();
        completeAllCheckBox.setText(languageBundle.getString("goalCompleteAllCheckBox"));
        completeAllCheckBox.getStylesheets().add(Objects.requireNonNull(mainCL.getResource("FXMLs/mediumCheckBox.css")).toExternalForm());
        completeAllCheckBox.setSelected(checkBoxState);
        completeAllCheckBox.setAccessibleHelp("completeAllCheckBox");
        completeAllCheckBox.setAccessibleText(String.valueOf(date));

        HBox hbox = new HBox(6, addNewTaskButton, dateLabel, completeAllCheckBox);
        Main.setRegion(hbox, 460, 25);
        hbox.setPadding(new Insets(0, 8, 0, 8));

        Separator hSeparator = new Separator(Orientation.HORIZONTAL);
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
        List<String> hoursValues = new ArrayList<>();
        hoursValues.addAll(Stream.iterate(0,  n -> ++n).limit(10).map(Object::toString).map(n -> "0" + n).collect(Collectors.toList()));
        hoursValues.addAll(IntStream.iterate(10, n -> ++n).limit(15).mapToObj(Integer::toString).collect(Collectors.toList()));
        List<String> minutesValues = new ArrayList<>();
        minutesValues.addAll(Stream.iterate(0, n -> ++n).limit(10).map(Object::toString).map(n -> "0" + n).collect(Collectors.toList()));
        minutesValues.addAll(IntStream.iterate(10, n -> ++n).limit(51).mapToObj(Integer::toString).collect(Collectors.toList()));

        ComboBox<String> hours = new ComboBox<>();
        ComboBox<String> minutes = new ComboBox<>();
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

        Button preNotificationButton = new Button(languageBundle.getString("goalPrenotificationButton"));
        Main.setRegion(preNotificationButton, 122, 25);
        preNotificationButton.setGraphic(new ImageView(new Image("Images/notification25.png")));
        preNotificationButton.setContentDisplay(ContentDisplay.RIGHT);

        Separator vSeparatorBetweenTimeAndButton = new Separator(Orientation.VERTICAL);
        Main.setRegion(vSeparatorBetweenTimeAndButton, 35, 25);
        vSeparatorBetweenTimeAndButton.setVisible(false);

        CheckBox completeCheckBox = new CheckBox(languageBundle.getString("goalCompleteCheckBox"));
        Main.setRegion(completeCheckBox,82 ,25);
        completeCheckBox.getStylesheets().add(Objects.requireNonNull(mainCL.getResource("FXMLs/mediumCheckBox.css")).toExternalForm());
        completeCheckBox.setPadding(new Insets(0, 13, 0, 0));
        completeCheckBox.setAccessibleHelp("completeCheckBox");
        completeCheckBox.setAccessibleText(String.valueOf(date));

        Button deleteButton = new Button();
        Main.setRegion(deleteButton, 25, 25);
        deleteButton.setGraphic(new ImageView(new Image("Images/delete35.png")));
        deleteButton.setStyle("-fx-background-color: #f4f4f4");
        deleteButton.setAccessibleHelp("taskDeleteButton");

        HBox hbox1 = new HBox(6, hours, minutes, preNotificationButton, vSeparatorBetweenTimeAndButton, completeCheckBox, deleteButton);
        Main.setRegion(hbox1, 460, 25);
        hbox1.setPadding(new Insets(0, 8, 0, 34));


        TextField textField = new TextField();
        if (text != null)
        {
            textField.setText(text);
        }
        Main.setRegion(textField, 374, 25);
        textField.setAccessibleHelp("textField");

        Button saveButton = new Button();
        Main.setRegion(saveButton, 25, 25);
        saveButton.setGraphic(new ImageView(new Image("Images/save25.png")));
        saveButton.setStyle("-fx-background-color: #f4f4f4");
        if (completeState == null)
        {
            completeCheckBox.setDisable(true);
        } else
        {
            completeCheckBox.setSelected(completeState);
            saveButton.setDisable(true);
        }


        HBox hbox2 = new HBox(6, textField, saveButton);
        Main.setRegion(hbox2, 460, 25);
        hbox2.setPadding(new Insets(0, 8, 0, 34));

        Separator hSeparator = new Separator(Orientation.HORIZONTAL);
        Main.setRegion(hSeparator, 460, 4);
        hSeparator.setPadding(new Insets(8, 0, 8, 0));

        VBox vbox = new VBox(5, hbox1, hbox2, hSeparator);
        Main.setRegion(vbox, 460, 64);
        vbox.setAlignment(Pos.CENTER);

        line.getChildren().addAll(vbox);

        preNotificationButton.setOnAction(event ->
        {
            try
            {
                LocalTime timeOfEvent = LocalTime.of(Integer.parseInt(hours.getValue()), Integer.parseInt(minutes.getValue()));
                new PrenotificationSD(LocalDateTime.of(date, timeOfEvent), textField.getText()).start(Stage);
            } catch (Exception e)
            {
                logger.error("GoalSD: prenotification FXML load error ", e);
            }
        });

        deleteButton.setOnAction(event ->
        {
            line.getChildren().removeAll(vbox);
            this.removeTaskFromGoal(date, completeCheckBox);
        });

        //Костыль, чтобы иметь возможность переключать чекбоксы по прошедшим событиям нам обязательно нужны ключи в виде
        //событий. Вот и приходится это грузить сюда. В случае изменений ключ перезапишется и всё будет ок
        LocalTime timeOfKey = LocalTime.of(Integer.parseInt(hours.getValue()), Integer.parseInt(minutes.getValue()));
        String textOfKey = textField.getText();
        EventOfDay key = new EventOfDay(timeOfKey, Day.EventType.Task, textOfKey);
        this.getCheckBoxes().put(key, completeCheckBox);

        saveButton.setOnMouseClicked(event ->
        {
            if (date.isAfter(LocalDate.now()) || date.equals(LocalDate.now()))
            {
                this.removeTaskFromGoal(date, completeCheckBox);
                completeCheckBox.setDisable(false);

                LocalTime timeOfTask = LocalTime.of(Integer.parseInt(hours.getValue()), Integer.parseInt(minutes.getValue()));
                String textOfTask = textField.getText();
                List<Day> daysWithEvents = CalendarSD.getDaysWithEvents();
                Day day = CalendarSD.checkUsingOfThisDateOnEventList(date);
                if (day == null)
                {
                    day = new Day(date);
                    daysWithEvents.add(day);
                }
                EventOfDay task = new EventOfDay(timeOfTask, Day.EventType.Task, textOfTask);
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
                Alert alert = new Alert(Alert.AlertType.WARNING, languageBundle.getString("goalTaskSaveAlert"), ButtonType.OK);
                alert.showAndWait();
            }
            saveButton.setDisable(true);
        });

        mainCheckBox.addEventHandler(ActionEvent.ACTION, event ->
        {
            completeCheckBox.setSelected(mainCheckBox.isSelected());
        });

        this.getGroupOfMainCheckBox().get(mainCheckBox).add(completeCheckBox);

        completeCheckBox.setOnAction(e ->
        {
            for (Map.Entry<EventOfDay, CheckBox> entry : this.getCheckBoxes().entrySet())
            {
                if (entry.getKey() != null && entry.getValue() != null)
                {
                    EventOfDay task = entry.getKey();
                    if (entry.getValue().equals(completeCheckBox))
                    {
                        updateStateOfGoalCheckBoxes(task, completeCheckBox.isSelected());
                    }
                }
            }
        });

        hours.setOnAction(event -> saveButton.setDisable(date.isBefore(LocalDate.now())));
        minutes.setOnAction(hours.getOnAction());
        textField.setOnKeyTyped(event -> saveButton.setDisable(date.isBefore(LocalDate.now())));
    }

    public static void addGoalsToXML(Document doc, boolean createEmptyXML)
    {
        logger.info("GoalSD: start goals saving");
        org.w3c.dom.Node rootElement = doc.getFirstChild();


        Element goalsElement = doc.createElement("goals");
        rootElement.appendChild(goalsElement);
        if (!createEmptyXML)
        {
            int id = 1;
            for (Map.Entry<Integer, GoalSD> entry : goals.entrySet())
            {
                GoalSD goalSD = entry.getValue();
                AnchorPane goal = goalSD.getGoalRoot();

                Element goalElement = doc.createElement("goal" + id);
                goalElement.setAttribute("tab", goal.getAccessibleText());

                goalsElement.appendChild(goalElement);

                Element visibilityElement = doc.createElement("visibility");
                goalElement.appendChild(visibilityElement);
                Text visibilityValue = doc.createTextNode(String.valueOf(goal.isVisible()));
                visibilityElement.appendChild(visibilityValue);

                Element layoutElement = doc.createElement("layout");
                goalElement.appendChild(layoutElement);

                Element layoutX = doc.createElement("layoutX");
                layoutElement.appendChild(layoutX);
                Text layoutXValue = doc.createTextNode(String.valueOf((int) (goal.getLayoutX())));
                layoutX.appendChild(layoutXValue);

                Element layoutY = doc.createElement("layoutY");
                layoutElement.appendChild(layoutY);
                Text layoutYValue = doc.createTextNode(String.valueOf((int) (goal.getLayoutY())));
                layoutY.appendChild(layoutYValue);

                Element startDateElement = doc.createElement("startDate");
                goalElement.appendChild(startDateElement);

                Element endDateElement = doc.createElement("endDate");
                goalElement.appendChild(endDateElement);

                Element nameOfGoalElement = doc.createElement("nameOfGoal");
                goalElement.appendChild(nameOfGoalElement);

                Element linesElement = doc.createElement("lines");
                goalElement.appendChild(linesElement);

                if(goalSD.getNameOfGoal() != null && goalSD.getStartDate() != null && goalSD.getEndDate() != null)
                {
                    Text startDateValue = doc.createTextNode(goalSD.getStartDate().toString());
                    startDateElement.appendChild(startDateValue);

                    Text endDateValue = doc.createTextNode(goalSD.getEndDate().toString());
                    endDateElement.appendChild(endDateValue);

                    Text nameOfGoalValue = doc.createTextNode(goalSD.getNameOfGoal());
                    nameOfGoalElement.appendChild(nameOfGoalValue);

                    Map<LocalDate, VBox> sortedLines = new TreeMap<>(goalSD.getTasksOfDay());

                    int numberOfLine = 1;
                    for (VBox line : sortedLines.values())
                    {
                        Element lineElement = doc.createElement("line" + numberOfLine);
                        linesElement.appendChild(lineElement);

                        Element completeAllCheckBoxStateElement = doc.createElement("completeAllCheckBoxState");
                        lineElement.appendChild(completeAllCheckBoxStateElement);
                        Boolean completeAllCheckBoxState = null;

                        Element tasksElement = doc.createElement("tasks");
                        lineElement.appendChild(tasksElement);

                        int numberOfTask = 1;
                        for (Node tasks : line.getChildren())
                        {
                            if (completeAllCheckBoxState == null && tasks instanceof HBox)
                            {
                                for (Node goalHBoxElements : ((HBox) tasks).getChildren())
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
                                Element taskElement = doc.createElement("task" + numberOfTask);
                                tasksElement.appendChild(taskElement);

                                Element timeElement = doc.createElement("time");
                                taskElement.appendChild(timeElement);
                                int hour = 0;
                                int minute = 0;

                                Element textElement = doc.createElement("text");
                                taskElement.appendChild(textElement);
                                String text = "";

                                Element completeElement = doc.createElement("complete");
                                taskElement.appendChild(completeElement);
                                boolean complete = false;

                                for (Node hboxesOfTask : ((VBox) tasks).getChildren())
                                {
                                    if (hboxesOfTask instanceof HBox)
                                    {
                                        for (Node hboxElement : ((HBox) hboxesOfTask).getChildren())
                                        {
                                            if (hboxElement.getAccessibleHelp() != null)
                                            {
                                                if (hboxElement instanceof ComboBox && hboxElement.getAccessibleHelp().equals("hours"))
                                                {
                                                    hour = Integer.parseInt((String) (((ComboBox) hboxElement).getValue()));
                                                    continue;
                                                }
                                                if (hboxElement instanceof ComboBox && hboxElement.getAccessibleHelp().equals("minutes"))
                                                {
                                                    minute = Integer.parseInt((String) (((ComboBox) hboxElement).getValue()));
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
                                Text timeElementValue = doc.createTextNode(String.valueOf(LocalTime.of(hour, minute)));
                                timeElement.appendChild(timeElementValue);

                                Text textElementValue = doc.createTextNode(text);
                                textElement.appendChild(textElementValue);

                                Text completeElementValue = doc.createTextNode(String.valueOf(complete));
                                completeElement.appendChild(completeElementValue);

                                numberOfTask++;
                            }
                        }

                        Text completeAllCheckBoxStateElementValue = doc.createTextNode(String.valueOf(completeAllCheckBoxState));
                        completeAllCheckBoxStateElement.appendChild(completeAllCheckBoxStateElementValue);
                        numberOfLine++;
                    }
                    id++;
                }
            }
        }
        logger.info("GoalSD: end goals saving");
    }

    public static void loadGoalsFromXML(Document doc, XPath xPath) throws Exception
    {
        logger.info("GoalSD: start goals loading");
        XPathExpression goalsCompile = xPath.compile("count(/save/goals/*)");
        int numberOfGoals = Integer.parseInt((String)goalsCompile.evaluate(doc, XPathConstants.STRING));
        for (int id = 1; id < numberOfGoals + 1; id++)
        {
            GoalSD loadingGoal = new GoalSD(true);
            loadingGoal.start(Main.Stage);
            AnchorPane rootOfLoadingGoal = loadingGoal.getGoalRoot();

            int numberOfTab = Integer.parseInt(xPath.evaluate("/save/goals/goal" + id + "/@tab", doc));
            rootOfLoadingGoal.setAccessibleText(String.valueOf(numberOfTab));

            List<Node> tab = tabs.get(numberOfTab);
            tab.add(rootOfLoadingGoal);
            boolean visibility = Boolean.parseBoolean(xPath.evaluate("/save/goals/goal" + id + "/visibility/text()", doc));
            rootOfLoadingGoal.setVisible(visibility);

            double layoutX = Double.parseDouble(xPath.evaluate("/save/goals/goal" + id + "/layout/layoutX/text()", doc));
            double layoutY = Double.parseDouble(xPath.evaluate("/save/goals/goal" + id + "/layout/layoutY/text()", doc));
            rootOfLoadingGoal.setLayoutX(layoutX);
            rootOfLoadingGoal.setLayoutY(layoutY);

            XPathExpression linesCompile = xPath.compile("count(/save/goals/goal" + id + "/lines/*)");
            int numberOfLines =  Integer.parseInt((String)linesCompile.evaluate(doc, XPathConstants.STRING));
            if (numberOfLines != 0)
            {
                LocalDate startDate = LocalDate.parse(xPath.evaluate("/save/goals/goal" + id + "/startDate/text()", doc));
                LocalDate endDate = LocalDate.parse(xPath.evaluate("/save/goals/goal" + id + "/endDate/text()", doc));
                String nameOfGoal = xPath.evaluate("/save/goals/goal" + id + "/nameOfGoal/text()", doc);

                loadingGoal.setStartDate(startDate);
                loadingGoal.setEndDate(endDate);
                loadingGoal.setNameOfGoal(nameOfGoal);

                for (Node node : rootOfLoadingGoal.getChildren())
                {
                    if (node instanceof DatePicker && node.getAccessibleHelp() != null && node.getAccessibleHelp().equals("startDatePicker"))
                    {
                        ((DatePicker) node).setValue(startDate);
                        ((DatePicker) node).setEditable(false);
                        break;
                    }
                }

                for (Node node : rootOfLoadingGoal.getChildren())
                {
                    if (node instanceof DatePicker && node.getAccessibleHelp() != null && node.getAccessibleHelp().equals("endDatePicker"))
                    {
                        ((DatePicker) node).setValue(endDate);
                        ((DatePicker) node).setEditable(false);
                        break;
                    }
                }

                for (Node node : rootOfLoadingGoal.getChildren())
                {
                    if (node instanceof TextField && node.getAccessibleHelp() != null && node.getAccessibleHelp().equals("nameOfGoal"))
                    {
                        ((TextField) node).setText(nameOfGoal);
                        ((TextField) node).setEditable(false);
                        break;
                    }
                }

                VBox content = new VBox(6);
                content.setPadding(new Insets(8, 0, 0, 0));
                ScrollPane scrollPane = new ScrollPane(content);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setLayoutY(94);
                scrollPane.setMinWidth(460);
                scrollPane.setPrefWidth(460);
                scrollPane.setMaxWidth(460);
                scrollPane.setPrefHeight(226);
                scrollPane.maxHeight(226);

                rootOfLoadingGoal.getChildren().removeIf(node -> node instanceof Button && node.getAccessibleHelp() != null && node.getAccessibleHelp().equals("goalSaveButton"));
                rootOfLoadingGoal.getChildren().removeIf(node -> node instanceof Separator && node.getAccessibleHelp() != null && node.getAccessibleHelp().equals("goalSaveButtonSeparator"));

                ObservableList<Node> contentList = content.getChildren();

                for (int numberOfLine = 1; numberOfLine < numberOfLines + 1; numberOfLine++)
                {
                    boolean completeAllCheckBoxState = Boolean.parseBoolean(xPath.evaluate("/save/goals/goal" + id + "/lines/line" + numberOfLine + "/completeAllCheckBoxState/text()", doc));

                    LocalDate date = startDate.plusDays(numberOfLine - 1);
                    LocalTime timeOfEvent = LocalTime.of(23, 59);
                    EventOfDay eventOfDay = new EventOfDay(timeOfEvent, Day.EventType.Goal, nameOfGoal);

                    VBox line = loadingGoal.createNewLine(date, completeAllCheckBoxState, eventOfDay);
                    loadingGoal.getTasksOfDay().put(date, line);
                    contentList.add(line);

                    Map<EventOfDay, CheckBox> checkBoxes = loadingGoal.getCheckBoxes();

                    XPathExpression tasksCompile = xPath.compile("count(/save/goals/goal" + id + "/lines/line" + numberOfLine + "/tasks/*)");
                    int numberOfTasks =  Integer.parseInt((String)tasksCompile.evaluate(doc, XPathConstants.STRING));
                    for (int numberOfTask = 1; numberOfTask < numberOfTasks + 1; numberOfTask++)
                    {
                        LocalTime time = LocalTime.parse(xPath.evaluate("/save/goals/goal" + id + "/lines/line" + numberOfLine + "/tasks/task" + numberOfTask + "/time/text()", doc));
                        String text = xPath.evaluate("/save/goals/goal" + id + "/lines/line" + numberOfLine + "/tasks/task" + numberOfTask + "/text/text()", doc);
                        boolean complete = Boolean.parseBoolean(xPath.evaluate("/save/goals/goal" + id + "/lines/line" + numberOfLine + "/tasks/task" + numberOfTask + "/complete/text()", doc));
                        CheckBox completeAllCheckBox = checkBoxes.get(eventOfDay);
                        loadingGoal.createNewTaskLine(line, completeAllCheckBox, date, time, text, complete);
                    }
                }
                rootOfLoadingGoal.getChildren().add(scrollPane);
            }
        }
        logger.info("GoalSD: end goals loading");
    }
}
