package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import javafx.application.Application;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalTime;
import java.util.*;

import static com.camper.SmartDesktop.Main.*;

public class DeprecatedEvents extends Application implements Initializable
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
    private Label deprecatedEventsLabel;
    @FXML
    private Label deprecatedEventsShowLabel;
    private static int numberOfEvents = 0;
    private static AnchorPane checkDeprecatedEventsRoot;
    private static List<Day> daysWithDeprecatedEvents = new ArrayList<>();
    private static boolean entered = false;

    public static List<Day> getDaysWithDeprecatedEvents()
    {
        return daysWithDeprecatedEvents;
    }

    public static void clearLastInfo()
    {
        daysWithDeprecatedEvents.clear();
    }


    @Override
    public void start(Stage primaryStage) throws Exception
    {
        checkDeprecatedEventsRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/deprecatedEvents.fxml")));
        checkDeprecatedEventsRoot.setLayoutX(DEFAULT_WIDTH - 512);
        checkDeprecatedEventsRoot.setLayoutY(25);
        updateScrollArea(true, true, true);

        checkDeprecatedEventsRoot.setOnMouseEntered(event -> entered = true);
        checkDeprecatedEventsRoot.setOnMouseExited(event ->
        {
            if (entered)
            {
                entered = false;
                Main.root.getChildren().remove(checkDeprecatedEventsRoot);
                updateBellIcon(false);
            }
        });

        addChild(checkDeprecatedEventsRoot);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        deprecatedEventsLabel.setText(languageBundle.getString("deprecatedEventsLabel"));
        deprecatedEventsShowLabel.setText(languageBundle.getString("deprecatedEventsShowLabel"));
        notificationCheckBox.setText(languageBundle.getString("deprecatedEventsNotificationCheckBox"));
        goalsCheckBox.setText(languageBundle.getString("deprecatedEventsGoalCheckBox"));
        schedulerCheckBox.setText(languageBundle.getString("deprecatedEventsScheduleCheckBox"));
        allTypesCheckBox.setText(languageBundle.getString("deprecatedEventsAllTypesCheckBox"));

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

    public static void increaseTheNumberOfEvent(int countOfEvent)
    {
        numberOfEvents += countOfEvent;
        while (numberOfEvents > 100)
        {
            var day = daysWithDeprecatedEvents.remove(0);
            numberOfEvents -= day.getEvents().size();
        }
    }

    public static void updateBellIcon(boolean state)
    {
        for (Node node : Main.root.getChildren())
        {
            if (node instanceof Button && node.getAccessibleHelp() != null && node.getAccessibleHelp().equals("deprecatedEventsBell"))
            {
                var button = (Button) node;
                if (state)
                {
                    button.setGraphic(new ImageView(new Image("Images/bell25Active.png")));
                } else
                {
                    button.setGraphic(new ImageView(new Image("Images/bell25.png")));
                }
                break;
            }
        }
    }

    private void updateScrollArea(boolean notification, boolean goal, boolean schedule)
    {
        var content = new VBox(12);
        content.setMaxWidth(478);
        content.setMaxHeight(235);
        content.setPrefWidth(478);
        content.setPrefHeight(235);
        content.setMinWidth(478);

        for (var day : daysWithDeprecatedEvents)
        {
            var date = new Label(day.getDate().toString());
            date.setAlignment(Pos.CENTER);
            var hSeparatorUnderDate = new Separator();

            var goalsWithTask = new HashMap<String, List<EventOfDay>>();

            VBox vbox = new VBox();
            vbox.setMaxWidth(477);
            vbox.setPrefWidth(477);
            vbox.setMinWidth(477);
            vbox.setAlignment(Pos.CENTER);
            var events = day.getEvents();
            if (events.size() != 0)
            {
                vbox.getChildren().addAll(date, hSeparatorUnderDate);
                events.sort(Comparator.comparing(EventOfDay::getTime));
                for (var event : events)
                {
                    var type = event.getType();

                    var icon = new ImageView();
                    icon.setFitWidth(25);
                    icon.setFitHeight(25);
                    icon.setLayoutX(0);
                    HBox hbox = null;
                    if (type == Day.EventType.Notification && notification)
                    {
                        icon.setImage(new Image("Images/notification25.png"));
                        hbox = addInfoOfEvent(event, icon);
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
                        var hSeparator = new Separator();
                        vbox.getChildren().addAll(hbox, hSeparator);
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
                        vbox.getChildren().add(line);
                    }
                }
            }
            content.getChildren().add(vbox);
        }
        var scroller = new ScrollPane(content);
        scroller.setVisible(true);
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setLayoutY(65);
        var childList = checkDeprecatedEventsRoot.getChildren();
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

    private HBox addInfoOfEvent(EventOfDay event, ImageView icon)
    {
        var hSeparator = new Separator(Orientation.VERTICAL);

        var time = new TextField(LocalTime.of(event.getTime().getHour(), event.getTime().getMinute()).toString());
        time.setPrefWidth(45);
        time.setMinWidth(45);
        time.setEditable(false);

        var info = new TextArea(event.getInfo());
        info.setPrefWidth(364);
        info.setPrefHeight(25);
        info.setMaxHeight(25);
        info.setMinHeight(25);
        info.setEditable(false);
        info.setWrapText(true);

        var rightOffset = new Separator(Orientation.VERTICAL);
        Main.setRegion(rightOffset, 6, 25);
        rightOffset.setVisible(false);

        var hbox = new HBox(4, icon, hSeparator, time, info, rightOffset);
        Main.setRegion(hbox, 479, 42);
        hbox.setAlignment(Pos.CENTER);

        return hbox;
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

        var hbox1 = new HBox(12, icon, nameOfGoalLabel);
        Main.setRegion(hbox1, 460, 25);
        hbox1.setPadding(new Insets(0, 8, 0, 8));

        var hSeparator = new Separator(Orientation.HORIZONTAL);
        Main.setRegion(hSeparator, 477, 4);

        childList.addAll(hbox1, hSeparator);

        for (var task : tasks)
        {
            var time = new TextField(task.getTime().toString());
            Main.setRegion(time, 45, 25);
            time.setEditable(false);

            var vSeparator = new Separator(Orientation.VERTICAL);
            Main.setRegion(vSeparator, 4, 25);

            var info = new TextField(task.getInfo());
            Main.setRegion(info, 360, 25);
            info.setEditable(false);

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

            var taskInfo = new HBox(6, time, vSeparator, info, checkBox);
            Main.setRegion(taskInfo, 460, 25);
            taskInfo.setPadding(new Insets(0, 8, 0, 8));

            childList.add(taskInfo);

            checkBox.setOnAction(event -> GoalSD.updateStateOfGoalCheckBoxes(task, checkBox.isSelected()));
        }

        var endHSeparator = new Separator(Orientation.HORIZONTAL);
        Main.setRegion(endHSeparator, 477, 4);
        childList.add(endHSeparator);

        return line;
    }
}
