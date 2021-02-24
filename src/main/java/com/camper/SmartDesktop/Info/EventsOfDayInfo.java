package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import com.sun.javafx.font.FontFactory;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import javax.swing.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static com.camper.SmartDesktop.Main.*;

public class EventsOfDayInfo extends Application implements Initializable
{
    @FXML
    private CheckBox notificationCheckBox;
    @FXML
    private CheckBox goalsCheckBox;
    @FXML
    private CheckBox schedulerCheckBox;
    @FXML
    private CheckBox allTypesCheckBox;
    @FXML
    private Button addNotificationButton;
    @FXML
    private Button addGoalsButton;
    @FXML
    private Button addScheduleButton;
    @FXML
    private ImageView addNotificationButtonIV;
    @FXML
    private ImageView addGoalsButtonIV;
    @FXML
    private ImageView addScheduleButtonIV;
    private static AnchorPane paneOfInfoRoot;
    private static List<EventOfDay> events;
    private static boolean entered = false;
    private static LocalDate date = null;
    private MouseEvent mouseEvent;


    public EventsOfDayInfo()
    {
    }

    public EventsOfDayInfo(MouseEvent mouseEvent, Day dayWithEvents)
    {
        if (paneOfInfoRoot != null)
        {
            events.clear();
            Main.root.getChildren().remove(paneOfInfoRoot);
            paneOfInfoRoot = null;
            date = null;
        }
        events = new ArrayList<>(dayWithEvents.getEvents());
        this.mouseEvent = mouseEvent;
        date = dayWithEvents.getDate();
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        paneOfInfoRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/calendarEventsOfDayInfoRu.fxml")));
        int leftDownCornerX = (int) (mouseEvent.getSceneX() - mouseEvent.getX());
        int leftDownCornerY = (int) (mouseEvent.getSceneY() - mouseEvent.getY()) + 38 + 4;//38 - ������ ������
        int layoutX = leftDownCornerX;
        int layoutY = leftDownCornerY;
        int width = 460;
        int height = 280;

        if (leftDownCornerX + width > DEFAULT_WIDTH)
        {
            layoutX = leftDownCornerX - width;
        }
        if (leftDownCornerY + height > DEFAULT_HEIGHT)
        {
            layoutY = leftDownCornerY - height - 38;
        }
        paneOfInfoRoot.setLayoutX(layoutX);
        paneOfInfoRoot.setLayoutY(layoutY);
        updateScrollArea(true, true, true);

        paneOfInfoRoot.setOnMouseEntered(event -> entered = true);
        paneOfInfoRoot.setOnMouseExited(event ->
        {
            if (entered)
            {
                entered = false;
                events.clear();
                Main.root.getChildren().remove(paneOfInfoRoot);
                paneOfInfoRoot = null;
                date = null;
            }
        });
        Main.root.setOnMouseClicked(event ->
        {
            if (paneOfInfoRoot != null && Main.root.getChildren().contains(paneOfInfoRoot))
            {
                if (!(paneOfInfoRoot.contains(event.getX(), event.getY())))
                {
                    events.clear();
                    Main.root.getChildren().remove(paneOfInfoRoot);
                    paneOfInfoRoot = null;
                    date = null;
                }
            }
        });
        addChild(paneOfInfoRoot);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        addNotificationButtonIV.setImage(new Image("Images/add18.png"));
        addGoalsButtonIV.setImage(new Image("Images/add18.png"));
        addScheduleButtonIV.setImage(new Image("Images/add18.png"));

        addNotificationButton.setOnAction(event ->
        {
            try
            {
                new NotificationSD(date).start(Stage);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        addGoalsButton.setOnAction(event ->
        {
            /*var content = returnContentFromScrollPane();
            if (content != null)
            {
                var startSeparator = new Separator(Orientation.HORIZONTAL);
                Main.setRegion(startSeparator, 460, 2);

                var startDatePicker = new DatePicker();
                var endDatePicker = new DatePicker();
                Main.setRegion(startDatePicker, 120, 25);
                Main.setRegion(endDatePicker, 120, 25);

                String startLabelText = "C";
                var label1 = new Label(startLabelText);
                label1.setFont(Font.font("Times New Roman", FontPosture.REGULAR, 16));
                Main.setRegion(label1, 22, 25);
                label1.setAlignment(Pos.CENTER);

                var vSeparator = new Separator(Orientation.VERTICAL);
                Main.setRegion(vSeparator, 6, 25);
                vSeparator.setVisible(false);

                String endLabelText = "��";
                var label2 = new Label(endLabelText);
                label2.setFont(Font.font("Times New Roman", FontPosture.REGULAR, 16));
                Main.setRegion(label2, 22, 25);
                label2.setAlignment(Pos.CENTER_RIGHT);

                var goalName = new TextField();
                Main.setRegion(goalName, 380, 25);
                goalName.setPromptText("�������� ����");

                var saveButton = new Button();
                Main.setRegion(saveButton, 25, 25);
                saveButton.setGraphic(new ImageView(new Image("Images/save25.png")));
                saveButton.setStyle("-fx-background-color: #f4f4f4");

                var cancelButton = new Button();
                Main.setRegion(cancelButton, 25, 25);
                cancelButton.setGraphic(new ImageView(new Image("Images/close35.png")));
                cancelButton.setStyle("-fx-background-color: #f4f4f4");

                var endSeparator = new Separator(Orientation.HORIZONTAL);
                Main.setRegion(endSeparator, 460, 2);

                var hbox1 = new HBox(6, goalName, saveButton, cancelButton);
                var hbox2 = new HBox(6, label1, startDatePicker, vSeparator, label2, endDatePicker);
                hbox1.setPadding(new Insets(0, 8, 0, 8));
                hbox2.setPadding(new Insets(0, 8, 0, 8));

                var vbox = new VBox(3, startSeparator, hbox1, hbox2, endSeparator);
                Main.setRegion(vbox, 460, 55);
                vbox.setPadding(new Insets(4, 0, 4, 0));

                content.getChildren().add(vbox);

                cancelButton.setOnAction(e -> content.getChildren().remove(vbox));
                saveButton.setOnAction(e ->
                {
                    try
                    {
                        var goalSD = new GoalSD();
                        goalSD.setStartDate(startDatePicker.getValue());
                        goalSD.setEndDate(endDatePicker.getValue());
                        goalSD.setNameOfGoal(goalName.getText());
                        goalSD.start(Main.Stage);
                        GoalSD.fireSaveButton(goalSD);
                        GoalSD.hideGoal(goalSD);
                    } catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                    content.getChildren().remove(vbox);
                    updateScrollArea(notificationCheckBox.isSelected(), goalsCheckBox.isSelected(), schedulerCheckBox.isSelected());
                });
            }*/
            try
            {
                new GoalSD().start(Stage);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        });

        addScheduleButton.setOnAction(event ->
        {
            try
            {
                new ScheduleSD(date).start(Stage);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        notificationCheckBox.setOnAction(event ->
        {
            updateScrollArea(notificationCheckBox.isSelected(), goalsCheckBox.isSelected(), schedulerCheckBox.isSelected());
            if (!notificationCheckBox.isSelected())
            {
                allTypesCheckBox.setSelected(false);
            }
            if (notificationCheckBox.isSelected() && goalsCheckBox.isSelected() && schedulerCheckBox.isSelected())
            {
                allTypesCheckBox.setSelected(true);
            }
        });

        goalsCheckBox.setOnAction(event ->
        {
            updateScrollArea(notificationCheckBox.isSelected(), goalsCheckBox.isSelected(), schedulerCheckBox.isSelected());
            if (!goalsCheckBox.isSelected())
            {
                allTypesCheckBox.setSelected(false);
            }
            if (notificationCheckBox.isSelected() && goalsCheckBox.isSelected() && schedulerCheckBox.isSelected())
            {
                allTypesCheckBox.setSelected(true);
            }
        });

        schedulerCheckBox.setOnAction(event ->
        {
            updateScrollArea(notificationCheckBox.isSelected(), goalsCheckBox.isSelected(), schedulerCheckBox.isSelected());
            if (!schedulerCheckBox.isSelected())
            {
                allTypesCheckBox.setSelected(false);
            }
            if (notificationCheckBox.isSelected() && goalsCheckBox.isSelected() && schedulerCheckBox.isSelected())
            {
                allTypesCheckBox.setSelected(true);
            }
        });

        allTypesCheckBox.setOnAction(event ->
        {
            if (allTypesCheckBox.isSelected())
            {
                notificationCheckBox.setSelected(true);
                goalsCheckBox.setSelected(true);
                schedulerCheckBox.setSelected(true);
            } else
            {
                notificationCheckBox.setSelected(false);
                goalsCheckBox.setSelected(false);
                schedulerCheckBox.setSelected(false);
            }
            updateScrollArea(notificationCheckBox.isSelected(), goalsCheckBox.isSelected(), schedulerCheckBox.isSelected());
        });
        allTypesCheckBox.setSelected(true);
        notificationCheckBox.setSelected(true);
        goalsCheckBox.setSelected(true);
        schedulerCheckBox.setSelected(true);
    }

    /*private static VBox returnContentFromScrollPane()
    {
        if (paneOfInfoRoot != null)
        {
            for (var node : paneOfInfoRoot.getChildren())
            {
                if (node instanceof ScrollPane)
                {
                    return (VBox) ((ScrollPane) node).getContent();
                }
            }
        }
        return null;
    }*/

    private static void updateScrollArea(boolean notification, boolean goal, boolean schedule)
    {
        var content = new VBox(8);
        content.setMaxWidth(459);
        content.setMaxHeight(252);
        content.setPrefWidth(459);
        content.setPrefHeight(252);
        content.setMinWidth(459);

        var goalsWithTask = new HashMap<String, List<EventOfDay>>();

        if (events != null && events.size() != 0)
        {
            events.sort(Comparator.comparing(EventOfDay::getTime));
            for (var event : events)
            {
                var type = event.getType();

                var icon = new ImageView();
                icon.setFitWidth(35);
                icon.setFitHeight(35);
                icon.setLayoutX(2);
                HBox hbox = null;
                if (type == Day.EventType.Notification && notification)
                {
                    icon.setImage(new Image("Images/notification42.png"));
                    hbox = addInfoOfEvent(event, icon);
                }
                if (type == Day.EventType.Goal && goal)
                {
                    if (!(goalsWithTask.containsKey(event.getInfo())))
                    {
                        goalsWithTask.put(event.getInfo(), new ArrayList<>());
                    }
                    /*icon.setImage(new Image("Images/goal42.png"));
                    hbox = addInfoOfEvent(event, icon);*/
                }
                if (type == Day.EventType.Schedule && schedule)
                {
                    icon.setImage(new Image("Images/schedule42.png"));
                    hbox = addInfoOfEvent(event, icon);
                }
                if (type == Day.EventType.Task)
                {
                    String goalName = GoalSD.getGoalNameOfEventTask(event);
                    if (!(goalsWithTask.containsKey(goalName)))
                    {
                        goalsWithTask.put(goalName, new ArrayList<>());
                    }
                    goalsWithTask.get(goalName).add(event);
                }
                if (hbox != null)
                {
                    content.getChildren().add(hbox);
                }
            }
        }

        if (goal && goalsWithTask.size() != 0)
        {
            for (var entry : goalsWithTask.entrySet())
            {
                GoalSD goalSD = null;
                for (var g : GoalSD.getGoals().values())
                {
                    if (g.getNameOfGoal().equals(entry.getKey()))
                    {
                        goalSD = g;
                        break;
                    }
                }
                if (goalSD != null)
                {
                    var oldLine = goalSD.getTasksOfDay().get(date);
                    var line = addGoalOnScrollPane(entry.getKey(), entry.getValue(), date);
                    content.getChildren().add(line);
                }
            }
        }

        var scroller = new ScrollPane(content);
        scroller.setVisible(true);
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setLayoutY(40);
        var childList = paneOfInfoRoot.getChildren();
        for (var node : childList)
        {
            if (node instanceof ScrollPane)
            {
                childList.remove(node);
                childList.add(scroller);
                return;
            }
        }
        childList.add(scroller);
    }

    private static HBox addInfoOfEvent(EventOfDay event, ImageView icon)
    {
        var hSeparator = new Separator(Orientation.VERTICAL);

        var time = new TextField(LocalTime.of(event.getTime().getHour(), event.getTime().getMinute()).toString());
        time.setPrefWidth(45);
        time.setMinWidth(45);
        time.setEditable(false);

        var info = new TextArea(event.getInfo());
        info.setPrefWidth(346);
        info.setPrefHeight(42);
        info.setEditable(false);
        info.setWrapText(true);

        var hbox = new HBox(4, icon, hSeparator, time, info);
        Main.setRegion(hbox, 456, 42);
        hbox.setAlignment(Pos.CENTER);
        hbox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

        return hbox;
    }

    private static VBox addGoalOnScrollPane(String nameOfGoal, List<EventOfDay> tasks, LocalDate date)
    {
        var line = new VBox(3);
        line.setPadding(new Insets(4, 0, 0, 0));
        var childList = line.getChildren();

        var nameOfGoalLabel = new Label(nameOfGoal);
        Main.setRegion(nameOfGoalLabel, 379, 25);
        nameOfGoalLabel.setFont(Font.font(null, FontWeight.BOLD, 16));
        nameOfGoalLabel.setAlignment(Pos.CENTER);

        var icon = new ImageView(new Image("images/target25.png"));
        icon.setFitWidth(25);
        icon.setFitHeight(25);

        var leftOffset = new Separator(Orientation.VERTICAL);
        Main.setRegion(leftOffset, 4, 25);
        leftOffset.setVisible(false);

        /*var addButton = new Button();
        Main.setRegion(addButton, 25, 25);
        addButton.setGraphic(new ImageView(new Image("Images/add28.png")));

        var showButton = new Button();
        Main.setRegion(showButton, 25, 25);
        showButton.setGraphic(new ImageView(new Image("Images/show35.png")));

        var hbox1 = new HBox(6, icon, nameOfGoalLabel, addButton, showButton);*/
        var hbox1 = new HBox(6, leftOffset, icon, nameOfGoalLabel);
        Main.setRegion(hbox1, 460, 25);
        hbox1.setPadding(new Insets(0, 8, 0, 8));

        var hSeparator = new Separator(Orientation.HORIZONTAL);
        Main.setRegion(hSeparator, 460, 4);

        childList.addAll(hbox1, hSeparator);

        for (var task : tasks)
        {
            var time = new TextField(task.getTime().toString());
            Main.setRegion(time, 45, 25);
            time.setEditable(false);

            var vSeparator = new Separator(Orientation.VERTICAL);
            Main.setRegion(vSeparator, 4, 25);

            var info = new TextField(task.getInfo());
            Main.setRegion(info, 319, 25);
            info.setEditable(false);

            var deleteButton = new Button();
            Main.setRegion(deleteButton, 25, 25);
            deleteButton.setGraphic(new ImageView(new Image("Images/delete35.png")));
            deleteButton.setStyle("-fx-background-color: #f4f4f4");

            var checkBox = new CheckBox();
            checkBox.getStylesheets().add(Objects.requireNonNull(mainCL.getResource("FXMLs/mediumCheckBox.css")).toExternalForm());
            Main.setRegion(checkBox, 25, 25);
            var goalSD = GoalSD.getGoalFromGoalName(nameOfGoal);
            if (goalSD!=null)
            {
                checkBox.setSelected(goalSD.getCheckBoxes().get(task).isSelected());
            }
            else
            {
                checkBox.setSelected(false);
            }

            /*var editButton = new Button();
            Main.setRegion(editButton, 25, 25);
            editButton.setGraphic(new ImageView(new Image("Images/edit25.png")));
            editButton.setStyle("-fx-background-color: #f4f4f4");

            var taskInfo = new HBox(6, time, vSeparator, info, checkBox, deleteButton, editButton);*/
            var taskInfo = new HBox(6, time, vSeparator, info, checkBox, deleteButton);
            Main.setRegion(taskInfo, 460, 25);
            taskInfo.setPadding(new Insets(0, 8, 0, 8));

            childList.add(taskInfo);

            checkBox.setOnAction(event -> GoalSD.updateStateOfGoalCheckBoxes(task,checkBox.isSelected()));
            deleteButton.setOnAction(event->
            {
                if (goalSD!=null)
                {
                    childList.remove(taskInfo);
                    var checkBoxInGoalRoot = goalSD.getCheckBoxes().get(task);
                    for (var node : ((HBox)(checkBoxInGoalRoot.getParent())).getChildren())
                    {
                        if (node instanceof Button && node.getAccessibleHelp()!=null && node.getAccessibleHelp().equals("taskDeleteButton"))
                        {
                            ((Button) node).fire();
                        }
                    }
                }
            });
        }

        var endHSeparator = new Separator(Orientation.HORIZONTAL);
        Main.setRegion(endHSeparator, 460, 4);
        childList.add(endHSeparator);

        return line;
    }
}
