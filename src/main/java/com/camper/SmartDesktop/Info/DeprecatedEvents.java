package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.camper.SmartDesktop.Main.*;

public class DeprecatedEvents extends Application implements Initializable
{
    @FXML private CheckBox notificationCheckBox;
    @FXML private CheckBox goalsCheckBox;
    @FXML private CheckBox schedulerCheckBox;
    @FXML private CheckBox allTypesCheckBox;
    private static AnchorPane checkDeprecatedEventsRoot;
    private static List<Day> daysWithDeprecatedEvents = new ArrayList<>();
    private static boolean entered=false;

    public static List<Day> getDaysWithDeprecatedEvents() { return daysWithDeprecatedEvents; }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        checkDeprecatedEventsRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/deprecatedEvents.fxml")));
        checkDeprecatedEventsRoot.setLayoutX(DEFAULT_WIDTH-512);
        checkDeprecatedEventsRoot.setLayoutY(25);
        updateScrollArea(true,true,true);

        checkDeprecatedEventsRoot.setOnMouseEntered(event-> entered=true);
        checkDeprecatedEventsRoot.setOnMouseExited(event ->
        {
            if (entered)
            {
                entered=false;
                daysWithDeprecatedEvents.clear();
                Main.root.getChildren().remove(checkDeprecatedEventsRoot);
                updateBellIcon();
            }
        });

        addChild(checkDeprecatedEventsRoot);
        //daysWithDeprecatedEvents.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        notificationCheckBox.setOnAction(event->
        {
            updateScrollArea(notificationCheckBox.isSelected(),goalsCheckBox.isSelected(),schedulerCheckBox.isSelected());
            if (!notificationCheckBox.isSelected()){allTypesCheckBox.setSelected(false);}
            if (notificationCheckBox.isSelected()&&goalsCheckBox.isSelected()&&schedulerCheckBox.isSelected())
            { allTypesCheckBox.setSelected(true); }
        });

        goalsCheckBox.setOnAction(event->
        {
            updateScrollArea(notificationCheckBox.isSelected(),goalsCheckBox.isSelected(),schedulerCheckBox.isSelected());
            if (!goalsCheckBox.isSelected()){allTypesCheckBox.setSelected(false);}
            if (notificationCheckBox.isSelected()&&goalsCheckBox.isSelected()&&schedulerCheckBox.isSelected())
            { allTypesCheckBox.setSelected(true); }
        });

        schedulerCheckBox.setOnAction(event->
        {
            updateScrollArea(notificationCheckBox.isSelected(),goalsCheckBox.isSelected(),schedulerCheckBox.isSelected());
            if (!schedulerCheckBox.isSelected()){allTypesCheckBox.setSelected(false);}
            if (notificationCheckBox.isSelected()&&goalsCheckBox.isSelected()&&schedulerCheckBox.isSelected())
            { allTypesCheckBox.setSelected(true); }
        });

        allTypesCheckBox.setOnAction(event->
        {
            if (allTypesCheckBox.isSelected())
            {
                notificationCheckBox.setSelected(true);
                goalsCheckBox.setSelected(true);
                schedulerCheckBox.setSelected(true);
            }
            else
            {
                notificationCheckBox.setSelected(false);
                goalsCheckBox.setSelected(false);
                schedulerCheckBox.setSelected(false);
            }
            updateScrollArea(notificationCheckBox.isSelected(),goalsCheckBox.isSelected(),schedulerCheckBox.isSelected());
        });
        allTypesCheckBox.setSelected(true);
        notificationCheckBox.setSelected(true);
        goalsCheckBox.setSelected(true);
        schedulerCheckBox.setSelected(true);
    }

    public static void updateBellIcon()
    {
        for (Node node : Main.root.getChildren())
        {
            if (node instanceof Button && node.getAccessibleHelp()!=null && node.getAccessibleHelp().equals("deprecatedEventsBell"))
            {
                var button = (Button)node;
                if (daysWithDeprecatedEvents.size()!=0)
                {
                    button.setGraphic(new ImageView(new Image("Images/bell25Active.png")));
                }
                else
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

            VBox vbox=new VBox();
            var events = day.getEvents();
            if (events.size()!=0)
            {
                for (var event : events)
                {
                    var type = event.getType();

                    var icon = new ImageView();
                    icon.setFitWidth(25);
                    icon.setFitHeight(25);
                    icon.setLayoutX(0);
                    HBox hbox = null;
                    if (type==Day.EventType.Notification && notification)
                    {
                        icon.setImage(new Image("Images/notification42.png"));
                        hbox = addInfoOfEvent(event,icon);
                    }
                    if (type==Day.EventType.Goal && goal)
                    {
                        icon.setImage(new Image("Images/goal42.png"));
                        hbox = addInfoOfEvent(event,icon);
                    }
                    if (type==Day.EventType.Schedule && schedule)
                    {
                        icon.setImage(new Image("Images/schedule42.png"));
                        hbox = addInfoOfEvent(event,icon);
                    }
                    if (hbox!=null)
                    {
                        var hSeparator = new Separator();
                        if (!(vbox.getChildren().contains(date) && vbox.getChildren().contains(hSeparatorUnderDate)))
                        {
                            vbox.getChildren().addAll(date, hSeparatorUnderDate, hbox, hSeparator);
                        }
                        else {vbox.getChildren().addAll(hbox, hSeparator);}
                        vbox.setMaxWidth(477);
                        vbox.setPrefWidth(477);
                        vbox.setMinWidth(477);
                        vbox.setAlignment(Pos.CENTER);
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
        for(var node : childList)
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
        info.setPrefWidth(360);
        info.setPrefHeight(25);
        info.setMaxHeight(25);
        info.setMinHeight(25);
        info.setEditable(false);
        info.setWrapText(true);

        var hbox = new HBox(4,icon,hSeparator,time,info);
        hbox.setPrefWidth(479);
        hbox.setPrefHeight(42);
        hbox.setMinWidth(479);
        hbox.setMinHeight(42);
        hbox.setMaxWidth(479);//456
        hbox.setMaxHeight(42);
        hbox.setAlignment(Pos.CENTER);
        hbox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

        return hbox;
    }
}
