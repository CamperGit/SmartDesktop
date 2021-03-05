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
        int leftDownCornerY = (int) (mouseEvent.getSceneY() - mouseEvent.getY()) + 38 + 4;//38 - высота кнопки
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
        updateScrollArea(date,true, true, true);

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
            updateScrollArea(date,notificationCheckBox.isSelected(), goalsCheckBox.isSelected(), schedulerCheckBox.isSelected());
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
            updateScrollArea(date,notificationCheckBox.isSelected(), goalsCheckBox.isSelected(), schedulerCheckBox.isSelected());
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
            updateScrollArea(date,notificationCheckBox.isSelected(), goalsCheckBox.isSelected(), schedulerCheckBox.isSelected());
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
            updateScrollArea(date,notificationCheckBox.isSelected(), goalsCheckBox.isSelected(), schedulerCheckBox.isSelected());
        });
        allTypesCheckBox.setSelected(true);
        notificationCheckBox.setSelected(true);
        goalsCheckBox.setSelected(true);
        schedulerCheckBox.setSelected(true);
    }

    private static void updateScrollArea(LocalDate date, boolean notification, boolean goal, boolean schedule)
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
                if (type == Day.EventType.Notification && notification)
                {
                    icon.setImage(new Image("Images/notification25.png"));
                    icon.setFitHeight(25);
                    icon.setFitWidth(25);
                    addInfoOfEvent(date, event, icon, content);
                }
                if (type == Day.EventType.Goal && goal)
                {
                    if (!(goalsWithTask.containsKey(event.getInfo())))
                    {
                        goalsWithTask.put(event.getInfo(), new ArrayList<>());
                    }
                }
                if (type == Day.EventType.Schedule && schedule)
                {
                    icon.setImage(new Image("Images/schedule35.png"));
                    icon.setFitHeight(25);
                    icon.setFitWidth(25);
                    addInfoOfEvent(date, event, icon, content);
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
                    var line = addGoalOnScrollPane(entry.getKey(), entry.getValue());
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
        return;
    }

    private static void addInfoOfEvent(LocalDate date, EventOfDay event, ImageView icon, VBox content)
    {
        var time = new TextField(event.getTime().toString());
        Main.setRegion(time, 45, 25);
        time.setEditable(false);

        var vSeparator = new Separator(Orientation.VERTICAL);
        Main.setRegion(vSeparator, 4, 25);

        var info = new TextField(event.getInfo());
        Main.setRegion(info, 319, 25);
        info.setEditable(false);

        var deleteButton = new Button();
        Main.setRegion(deleteButton, 25, 25);
        deleteButton.setGraphic(new ImageView(new Image("Images/delete35.png")));
        deleteButton.setStyle("-fx-background-color: #f4f4f4");

        var hbox = new HBox(6, icon, time, vSeparator, info, deleteButton);
        Main.setRegion(hbox, 460, 25);
        hbox.setPadding(new Insets(0, 8, 0, 8));

        content.getChildren().add(hbox);

        deleteButton.setOnAction(e ->
        {
            if (event.getType().equals(Day.EventType.Schedule))
            {
                ScheduleSD.removeEventFromEventList(date,event);
            }
            if (event.getType().equals(Day.EventType.Notification))
            {
                NotificationSD.removeNotificationFromEventList(date,event);
            }
            content.getChildren().remove(hbox);
        });

        /*var hSeparator = new Separator(Orientation.VERTICAL);

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
        */
    }

    private static VBox addGoalOnScrollPane(String nameOfGoal, List<EventOfDay> tasks)
    {
        var line = new VBox(3);
        line.setPadding(new Insets(4, 0, 0, 0));
        var childList = line.getChildren();

        var nameOfGoalLabel = new Label(nameOfGoal);
        Main.setRegion(nameOfGoalLabel, 379, 25);
        nameOfGoalLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 16));
        nameOfGoalLabel.setAlignment(Pos.CENTER);

        var icon = new ImageView(new Image("Images/target25.png"));
        icon.setFitWidth(25);
        icon.setFitHeight(25);

        var leftOffset = new Separator(Orientation.VERTICAL);
        Main.setRegion(leftOffset, 4, 25);
        leftOffset.setVisible(false);

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
            if (goalSD != null)
            {
                checkBox.setSelected(goalSD.getCheckBoxes().get(task).isSelected());
            } else
            {
                checkBox.setSelected(false);
            }

            var taskInfo = new HBox(6, time, vSeparator, info, checkBox, deleteButton);
            Main.setRegion(taskInfo, 460, 25);
            taskInfo.setPadding(new Insets(0, 8, 0, 8));

            childList.add(taskInfo);

            checkBox.setOnAction(event -> GoalSD.updateStateOfGoalCheckBoxes(task, checkBox.isSelected()));
            deleteButton.setOnAction(event ->
            {
                if (goalSD != null)
                {
                    childList.remove(taskInfo);
                    var checkBoxInGoalRoot = goalSD.getCheckBoxes().get(task);
                    for (var node : ((HBox) (checkBoxInGoalRoot.getParent())).getChildren())
                    {
                        if (node instanceof Button && node.getAccessibleHelp() != null && node.getAccessibleHelp().equals("taskDeleteButton"))
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
