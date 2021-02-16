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

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.camper.SmartDesktop.Info.CalendarSD.checkUsingOfThisDate;
import static com.camper.SmartDesktop.Info.CalendarSD.updateDayIcons;
import static com.camper.SmartDesktop.Info.Day.addEventOfDay;
import static com.camper.SmartDesktop.Main.*;

public class ScheduleSD extends Application implements Initializable
{

    @FXML private ImageView scheduleCloseButtonIV;
    @FXML private ImageView scheduleSettingsButtonIV;
    @FXML private ImageView scheduleAddNewLineButtonIV;
    @FXML private ToolBar scheduleToolBar;
    @FXML private Button scheduleCloseButton;
    @FXML private Button scheduleSettingsButton;
    @FXML private Button scheduleAddNewLineButton;
    @FXML private Button schedulerSaveButton;
    @FXML private Label schedulerTextLabel;
    @FXML private VBox scheduleContentVbox;
    @FXML private DatePicker schedulerDatePicker;

    private boolean load=false;
    private AnchorPane ScheduleRoot;
    private Map<CheckBox,EventOfDay> eventsOfSchedule = new HashMap<>();
    private int id;
    private LocalDate date=null;
    private static AnchorPane selectedSchedule;
    private static Map<Integer, ScheduleSD> schedules = new HashMap<>();
    private static int nextId=1;

    public ScheduleSD(){}
    private ScheduleSD(boolean load) { this.load=load; }

    public static void clearSaveList() {schedules.clear(); nextId=1;}

    public Map<CheckBox, EventOfDay> getEventsOfSchedule() { return eventsOfSchedule; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    private AnchorPane getScheduleRoot() {return ScheduleRoot;}

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        ScheduleRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/scheduleRu.fxml")));
        ScheduleRoot.setLayoutX(80);
        ScheduleRoot.setLayoutY(30);
        this.id=nextId;
        nextId++;
        schedules.put(this.id,this);
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

        scheduleAddNewLineButton.setOnAction(event->
        {
            selectedSchedule = (AnchorPane) (((Button) event.getSource()).getParent());
            for (var child : selectedSchedule.getChildren())
            {
                if (child instanceof ScrollPane)
                {
                    var vbox = (VBox)(((ScrollPane)child).getContent());
                    createNewLine(vbox.getChildren());
                }
            }
        });

        scheduleContentVbox.setSpacing(10);
        createNewLine(scheduleContentVbox.getChildren());

        scheduleCloseButton.setOnAction(event ->
        {
            selectedSchedule = (AnchorPane) (((Button) event.getSource()).getParent());
            schedules.remove(Integer.parseInt(selectedSchedule.getAccessibleHelp()));
            Main.root.getChildren().remove(selectedSchedule);
        });

        scheduleToolBar.setOnMouseDragged(event ->
        {
            selectedSchedule = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            NodeDragger.addDraggingProperty(selectedSchedule,event);
        });

        schedulerSaveButton.setOnAction(event->
        {
            selectedSchedule = (AnchorPane) (((Button) event.getSource()).getParent());
            int id = Integer.parseInt(selectedSchedule.getAccessibleHelp());
            var scheduleSD = schedules.get(id);
            var date = scheduleSD.getDate();
            var mapWithEvents = scheduleSD.getEventsOfSchedule();

            if (date!=null)
            {
                var day = checkUsingOfThisDate(date);
                {
                    if (day == null)
                    {
                        day = new Day(date);
                        CalendarSD.getDaysWithEvents().add(day);
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
                    UpcomingEvent.loadEventsToQueue(List.of(day));
                    updateDayIcons(date,day.isHaveNotification(),day.isHaveGoal(),day.isHaveSchedule());
                }
            }
            else
            {
                var alert = new Alert(Alert.AlertType.WARNING, "Введите дату!", ButtonType.OK);
                alert.showAndWait();
            }
        });

        schedulerDatePicker.setOnAction(event->
        {
            var scheduleSD = schedules.get(Integer.parseInt((((DatePicker)(event.getSource())).getParent()).getAccessibleHelp()));
            scheduleSD.setDate(schedulerDatePicker.getValue());
        });

    }

    private void createNewLine(ObservableList<Node> content)
    {
        var hbox = new HBox();
        Main.setRegion(hbox,460,25);

        var leftOffset1 = new Separator(Orientation.VERTICAL);
        leftOffset1.setVisible(false);
        leftOffset1.setPrefWidth(5);

        var numbers0_9 = new String[10];
        for (int i = 0;i<10;i++)
        {
            numbers0_9[i]="0"+i;
        }
        var hours1 = new ComboBox<String>();
        hours1.setLayoutX(5);
        var hours2 = new ComboBox<String>();
        var minutes1 = new ComboBox<String>();
        var minutes2 = new ComboBox<String>();

        Main.setRegion(hours1,55,25);
        Main.setRegion(hours2,55,25);
        Main.setRegion(minutes1,55,25);
        Main.setRegion(minutes2,55,25);

        hours1.getItems().addAll(numbers0_9);
        hours1.getItems().addAll(IntStream.iterate(10, n->n<24, n->++n).mapToObj(Integer::toString).collect(Collectors.toList()));
        hours1.setValue("16");
        hours1.setVisibleRowCount(6);
        hours2.getItems().addAll(numbers0_9);
        hours2.getItems().addAll(IntStream.iterate(10, n->n<24, n->++n).mapToObj(Integer::toString).collect(Collectors.toList()));
        hours2.setValue("17");
        hours2.setVisibleRowCount(6);

        var separatorBetweenTime = new Separator(Orientation.VERTICAL);
        separatorBetweenTime.setVisible(false);
        separatorBetweenTime.setOpacity(0);
        Main.setRegion(separatorBetweenTime,12,25);

        minutes1.getItems().addAll(numbers0_9);
        minutes1.getItems().addAll(IntStream.iterate(10,n->n<60, n->++n).mapToObj(Integer::toString).collect(Collectors.toList()));
        minutes1.setValue("00");
        minutes1.setVisibleRowCount(6);
        minutes2.getItems().addAll(numbers0_9);
        minutes2.getItems().addAll(IntStream.iterate(10,n->n<60, n->++n).mapToObj(Integer::toString).collect(Collectors.toList()));
        minutes2.setValue("00");
        minutes2.setVisibleRowCount(6);


        var dash = new Label("-");
        dash.setAlignment(Pos.CENTER);
        dash.setFont(new Font(20));
        Main.setRegion(dash,12,30);

        var spacingBetweenTimeAndCheckBox = new Separator(Orientation.VERTICAL);
        spacingBetweenTimeAndCheckBox.setVisible(false);
        spacingBetweenTimeAndCheckBox.setPrefWidth(17);

        var addEventCheckBox = new CheckBox();
        addEventCheckBox.setLayoutX(5);
        addEventCheckBox.setText("Показать уведомление");
        addEventCheckBox.getStylesheets().add(Objects.requireNonNull(mainCL.getResource("FXMLs/mediumCheckBox.css")).toExternalForm());



        var spacingBetweenCheckBoxAndDeletingButton = new Separator(Orientation.VERTICAL);
        spacingBetweenCheckBoxAndDeletingButton.setVisible(false);
        spacingBetweenCheckBoxAndDeletingButton.setPrefWidth(6);

        var deletingButton = new Button();
        Main.setRegion(deletingButton,25,25);
        deletingButton.setGraphic(new ImageView(new Image("Images/minus25.png")));

        hbox.getChildren().addAll(leftOffset1,hours1,minutes1,dash,hours2,minutes2,spacingBetweenTimeAndCheckBox,addEventCheckBox,spacingBetweenCheckBoxAndDeletingButton,deletingButton);


        var leftOffset2 = new Separator(Orientation.VERTICAL);
        leftOffset2.setVisible(false);
        leftOffset2.setPrefWidth(5);

        var text = new TextArea();
        text.setWrapText(true);
        Main.setRegion(text,435,25);
        var hbox2 = new HBox(leftOffset2,text);

        var vbox = new VBox(8,hbox, hbox2);
        Main.setRegion(vbox,460,55);
        var line = new HBox(5,vbox);
        Main.setRegion(line,460,55);

        var hSeparator = new Separator(Orientation.HORIZONTAL);
        hSeparator.setPrefHeight(10);
        content.addAll(line,hSeparator);

        addEventCheckBox.setOnAction(event ->
        {
            var parent = ((Node)(event.getSource())).getParent();
            var scheduleSD = schedules.get(returnAnchorId(parent));
            var map = scheduleSD.getEventsOfSchedule();
            if (addEventCheckBox.isSelected())
            {
                var eventOfDay = new EventOfDay(LocalTime.of(Integer.parseInt(hours1.getValue()),Integer.parseInt(minutes1.getValue())), Day.EventType.Schedule,text.getText());
                map.put(addEventCheckBox,eventOfDay);

            }
            else
            {
                map.remove(addEventCheckBox);
            }
        });

        text.setOnKeyTyped(event->
        {
            var parent = ((Node)(event.getSource())).getParent();
            var scheduleSD = schedules.get(returnAnchorId(parent));
            var map = scheduleSD.getEventsOfSchedule();
            if (addEventCheckBox.isSelected())
            {
                var eventOfDay = new EventOfDay(LocalTime.of(Integer.parseInt(hours1.getValue()),Integer.parseInt(minutes1.getValue())), Day.EventType.Schedule,text.getText());
                map.put(addEventCheckBox,eventOfDay);

            }
            else
            {
                map.remove(addEventCheckBox);
            }
        });

        hours1.setOnAction(addEventCheckBox.getOnAction());
        hours2.setOnAction(addEventCheckBox.getOnAction());
        minutes1.setOnAction(addEventCheckBox.getOnAction());
        minutes2.setOnAction(addEventCheckBox.getOnAction());


        deletingButton.setOnAction(event->
        {
            content.remove(line);
            content.remove(hSeparator);
        });
    }
}
