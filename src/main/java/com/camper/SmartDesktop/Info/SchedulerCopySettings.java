package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

import static com.camper.SmartDesktop.Main.*;

public class SchedulerCopySettings extends Application implements Initializable
{
    @FXML private RadioButton scheduleDontRepeatRadioButton;
    @FXML private RadioButton scheduleDayRepeatRadioButton;
    @FXML private RadioButton scheduleWeekRepeatRadioButton;
    @FXML private RadioButton scheduleMonthRepeatRadioButton;
    @FXML private RadioButton scheduleYearRepeatRadioButton;

    @FXML private RadioButton schedulerForAWeekRadioButton;
    @FXML private RadioButton schedulerForAMonthRadioButton;
    @FXML private RadioButton schedulerForAYearRadioButton;

    public ScheduleSettingsRepeat getRepeatSelected() { return repeatSelected; }
    public void setRepeatSelected(ScheduleSettingsRepeat repeatSelected) { this.repeatSelected = repeatSelected; }

    public ScheduleSettingsPeriod getPeriodSelected() { return periodSelected; }
    public void setPeriodSelected(ScheduleSettingsPeriod periodSelected) { this.periodSelected = periodSelected; }

    public void setEntered(boolean entered) {this.entered=entered;}
    public boolean isEntered() { return entered; }

    public enum ScheduleSettingsRepeat{DONT, DAY, WEEK, MONTH, YEAR}
    public enum ScheduleSettingsPeriod{FOR_A_WEEK, FOR_A_MONTH, FOR_A_YEAR}

    private AnchorPane CopySettingsRoot;
    private MouseEvent mouseEvent;
    private ScheduleSettingsRepeat repeatSelected=ScheduleSettingsRepeat.DONT;
    private ScheduleSettingsPeriod periodSelected=ScheduleSettingsPeriod.FOR_A_WEEK;
    private boolean entered=false;
    private int id;
    private static Map<Integer,SchedulerCopySettings> settingsMap = new HashMap<>();


    public SchedulerCopySettings(){}
    public SchedulerCopySettings(MouseEvent mouseEvent, int idOfSchedule)
    {
        this.mouseEvent=mouseEvent;
        this.id=idOfSchedule;
    }

    public AnchorPane getCopySettingsRoot() {return CopySettingsRoot;}

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        CopySettingsRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/scheduleSettingsRu.fxml")));
        CopySettingsRoot.setAccessibleHelp(String.valueOf(id));
        settingsMap.put(id,this);

        CopySettingsRoot.setOnMouseEntered(event->
        {
            int id = Main.returnAnchorId(((Node) event.getSource()).getParent());
            settingsMap.get(id).setEntered(true);
        });
        CopySettingsRoot.setOnMouseExited(event ->
        {
            int id = Main.returnAnchorId(((Node) event.getSource()).getParent());
            var settings = settingsMap.get(id);
            if (settings.isEntered())
            {
                settings.setEntered(false);
                Main.root.getChildren().remove(settings.getCopySettingsRoot());
            }
        });

        showSettings(CopySettingsRoot);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        var repeat = new ToggleGroup();
        scheduleDontRepeatRadioButton.setToggleGroup(repeat);
        scheduleDayRepeatRadioButton.setToggleGroup(repeat);
        scheduleWeekRepeatRadioButton.setToggleGroup(repeat);
        scheduleMonthRepeatRadioButton.setToggleGroup(repeat);
        scheduleYearRepeatRadioButton.setToggleGroup(repeat);
        scheduleDontRepeatRadioButton.setSelected(true);

        var period = new ToggleGroup();
        schedulerForAWeekRadioButton.setToggleGroup(period);
        schedulerForAMonthRadioButton.setToggleGroup(period);
        schedulerForAYearRadioButton.setToggleGroup(period);

        scheduleDontRepeatRadioButton.setOnAction(event->
        {
            int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
            settingsMap.get(id).setRepeatSelected(ScheduleSettingsRepeat.DONT);
        });

        scheduleDayRepeatRadioButton.setOnAction(event->
        {
            int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
            settingsMap.get(id).setRepeatSelected(ScheduleSettingsRepeat.DAY);
        });

        scheduleWeekRepeatRadioButton.setOnAction(event->
        {
            int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
            settingsMap.get(id).setRepeatSelected(ScheduleSettingsRepeat.WEEK);
        });

        scheduleMonthRepeatRadioButton.setOnAction(event->
        {
            int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
            settingsMap.get(id).setRepeatSelected(ScheduleSettingsRepeat.MONTH);
        });

        scheduleYearRepeatRadioButton.setOnAction(event->
        {
            int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
            settingsMap.get(id).setRepeatSelected(ScheduleSettingsRepeat.YEAR);
        });


        schedulerForAWeekRadioButton.setOnAction(event->
        {
            int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
            settingsMap.get(id).setPeriodSelected(ScheduleSettingsPeriod.FOR_A_WEEK);
        });

        schedulerForAWeekRadioButton.setOnAction(event->
        {
            int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
            settingsMap.get(id).setPeriodSelected(ScheduleSettingsPeriod.FOR_A_MONTH);
        });

        schedulerForAWeekRadioButton.setOnAction(event->
        {
            int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
            settingsMap.get(id).setPeriodSelected(ScheduleSettingsPeriod.FOR_A_YEAR);
        });
    }

    public void showSettings(AnchorPane root)
    {
        int leftUpperCornerX = (int) (mouseEvent.getSceneX()-mouseEvent.getX())-314;//314 - Расстояние от края кнопки до края элемента
        int leftUpperCornerY = (int) (mouseEvent.getSceneY()-mouseEvent.getY());
        int layoutX=leftUpperCornerX+460;
        int width = 246;

        if (layoutX+width>DEFAULT_WIDTH)
        {
            layoutX=layoutX-460-width;
        }
        root.setLayoutX(layoutX);
        root.setLayoutY(leftUpperCornerY);

        addChild(root);
    }
}
