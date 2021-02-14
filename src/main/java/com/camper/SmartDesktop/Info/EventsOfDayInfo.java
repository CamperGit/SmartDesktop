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

import javax.swing.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.camper.SmartDesktop.Main.*;

public class EventsOfDayInfo extends Application implements Initializable
{
    @FXML private CheckBox notificationCheckBox;
    @FXML private CheckBox goalsCheckBox;
    @FXML private CheckBox schedulerCheckBox;
    @FXML private CheckBox allTypesCheckBox;
    @FXML private Button addNotificationButton;
    @FXML private Button addGoalsButton;
    @FXML private Button addScheduleButton;
    @FXML private ImageView addNotificationButtonIV;
    @FXML private ImageView addGoalsButtonIV;
    @FXML private ImageView addScheduleButtonIV;
    private static AnchorPane paneOfInfoRoot;
    private static List<EventOfDay> events;
    private static boolean entered=false;
    private static LocalDate date=null;
    private MouseEvent mouseEvent;


    public EventsOfDayInfo(){}
    public EventsOfDayInfo(MouseEvent mouseEvent, Day dayWithEvents )
    {
        if (paneOfInfoRoot!=null)
        {
            events.clear();
            Main.root.getChildren().remove(paneOfInfoRoot);
            paneOfInfoRoot=null;
            date=null;
        }
        events = new ArrayList<>(dayWithEvents.getEvents());
        this.mouseEvent=mouseEvent;
        date = dayWithEvents.getDate();
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        paneOfInfoRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/calendarEventsOfDayInfoRu.fxml")));
        int leftDownCornerX = (int) (mouseEvent.getSceneX()-mouseEvent.getX());
        int leftDownCornerY = (int) (mouseEvent.getSceneY()-mouseEvent.getY())+38+4;//38 - высота кнопки
        int layoutX=leftDownCornerX;
        int layoutY=leftDownCornerY;
        int width = 460;
        int height = 280;

        if (leftDownCornerX+width>DEFAULT_WIDTH)
        {
            layoutX=leftDownCornerX-width;
        }
        if (leftDownCornerY+height>DEFAULT_HEIGHT)
        {
            layoutY=leftDownCornerY-height-38;
        }
        paneOfInfoRoot.setLayoutX(layoutX);
        paneOfInfoRoot.setLayoutY(layoutY);
        updateScrollArea(true,true,true);

        paneOfInfoRoot.setOnMouseEntered(event-> entered=true);
        paneOfInfoRoot.setOnMouseExited(event ->
        {
            if (entered)
            {
                entered=false;
                events.clear();
                Main.root.getChildren().remove(paneOfInfoRoot);
                paneOfInfoRoot=null;
                date=null;
            }
        });
        Main.root.setOnMouseClicked(event->
        {
            if (paneOfInfoRoot!=null && Main.root.getChildren().contains(paneOfInfoRoot))
            {
                if (!(paneOfInfoRoot.contains(event.getX(),event.getY())))
                {
                    events.clear();
                    Main.root.getChildren().remove(paneOfInfoRoot);
                    paneOfInfoRoot=null;
                    date=null;
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
            try { new NotificationSD(date).start(Stage); }
            catch (Exception e)
            { e.printStackTrace(); }
        });

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

    private void updateScrollArea(boolean notification, boolean goal, boolean schedule)
    {
        var content = new VBox(8);
        content.setMaxWidth(459);
        content.setMaxHeight(252);
        content.setPrefWidth(459);
        content.setPrefHeight(252);
        content.setMinWidth(459);

        if (events.size()!=0)
        {
            for (var event : events)
            {
                var type = event.getType();

                var icon = new ImageView();
                icon.setFitWidth(35);
                icon.setFitHeight(35);
                icon.setLayoutX(2);
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
                    content.getChildren().add(hbox);
                }
            }
        }
        var scroller = new ScrollPane(content);
        scroller.setVisible(true);
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setLayoutY(40);
        var childList = paneOfInfoRoot.getChildren();
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
        //paneOfInfoRoot.getChildren().removeIf(node -> node instanceof ScrollPane);
    }

    private HBox addInfoOfEvent(EventOfDay event, ImageView icon)
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

        var hbox = new HBox(4,icon,hSeparator,time,info);
        hbox.setPrefWidth(456);
        hbox.setPrefHeight(42);
        hbox.setMinWidth(456);
        hbox.setMinHeight(42);
        hbox.setMaxWidth(456);
        hbox.setMaxHeight(42);
        hbox.setAlignment(Pos.CENTER);
        hbox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

        return hbox;
    }
}
