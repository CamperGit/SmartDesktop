package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

import static com.camper.SmartDesktop.Main.*;

public class SchedulerCopySettings extends Application implements Initializable
{
    @FXML
    private RadioButton scheduleDontRepeatRadioButton;
    @FXML
    private RadioButton scheduleDayRepeatRadioButton;
    @FXML
    private RadioButton scheduleWeekRepeatRadioButton;
    @FXML
    private RadioButton scheduleMonthRepeatRadioButton;
    @FXML
    private RadioButton scheduleYearRepeatRadioButton;

    @FXML
    private RadioButton schedulerForAWeekRadioButton;
    @FXML
    private RadioButton schedulerForAMonthRadioButton;
    @FXML
    private RadioButton schedulerForAYearRadioButton;

    public ScheduleSettingsRepeat getRepeatSelected()
    {
        return repeatSelected;
    }

    public void setRepeatSelected(ScheduleSettingsRepeat repeatSelected)
    {
        this.repeatSelected = repeatSelected;
    }

    public ScheduleSettingsPeriod getPeriodSelected()
    {
        return periodSelected;
    }

    public void setPeriodSelected(ScheduleSettingsPeriod periodSelected)
    {
        this.periodSelected = periodSelected;
    }

    public void setEntered(boolean entered)
    {
        this.entered = entered;
    }

    public boolean isEntered()
    {
        return entered;
    }

    public enum ScheduleSettingsRepeat
    {DONT, DAY, WEEK, MONTH, YEAR}

    public enum ScheduleSettingsPeriod
    {DONT, FOR_A_WEEK, FOR_A_MONTH, FOR_A_YEAR}

    private AnchorPane CopySettingsRoot;
    private MouseEvent mouseEvent;
    private ScheduleSettingsRepeat repeatSelected = ScheduleSettingsRepeat.DONT;
    private ScheduleSettingsPeriod periodSelected = ScheduleSettingsPeriod.DONT;//=ScheduleSettingsPeriod.FOR_A_WEEK;
    private boolean entered = false;
    private boolean load = false;
    private int id;
    private static final Map<Integer, SchedulerCopySettings> settingsMap = new HashMap<>();

    public SchedulerCopySettings()
    {
    }

    public SchedulerCopySettings(MouseEvent mouseEvent, int idOfSchedule)
    {
        this.mouseEvent = mouseEvent;
        this.id = idOfSchedule;
    }

    public SchedulerCopySettings(String repeat, String period, boolean load)
    {
        repeatSelected = SchedulerCopySettings.ScheduleSettingsRepeat.valueOf(repeat);
        periodSelected = SchedulerCopySettings.ScheduleSettingsPeriod.valueOf(period);
        this.load = load;
    }

    public AnchorPane getCopySettingsRoot()
    {
        return CopySettingsRoot;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        CopySettingsRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/scheduleSettingsRu.fxml")));
        CopySettingsRoot.setAccessibleHelp(String.valueOf(id));
        settingsMap.put(id, this);

        CopySettingsRoot.setOnMouseEntered(event ->
        {
            int id;
            if (event.getSource() instanceof AnchorPane)
            {
                id = Integer.parseInt(((AnchorPane) event.getSource()).getAccessibleHelp());
            } else
            {
                id = Main.returnAnchorId(((Node) event.getSource()).getParent());
            }
            settingsMap.get(id).setEntered(true);
        });
        CopySettingsRoot.setOnMouseExited(event ->
        {
            int id;
            if (event.getSource() instanceof AnchorPane)
            {
                id = Integer.parseInt(((AnchorPane) event.getSource()).getAccessibleHelp());
            } else
            {
                id = Main.returnAnchorId(((Node) event.getSource()).getParent());
            }
            var settings = settingsMap.get(id);
            if (settings.isEntered())
            {
                settings.setEntered(false);
                Main.root.getChildren().remove(settings.getCopySettingsRoot());
            }
        });

        if (!load)
        {
            showSettings(CopySettingsRoot, mouseEvent);
        }
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

        scheduleDontRepeatRadioButton.setAccessibleText("DONT");
        scheduleDayRepeatRadioButton.setAccessibleText("DAY");
        scheduleWeekRepeatRadioButton.setAccessibleText("WEEK");
        scheduleMonthRepeatRadioButton.setAccessibleText("MONTH");
        scheduleYearRepeatRadioButton.setAccessibleText("YEAR");

        var period = new ToggleGroup();
        schedulerForAWeekRadioButton.setToggleGroup(period);
        schedulerForAMonthRadioButton.setToggleGroup(period);
        schedulerForAYearRadioButton.setToggleGroup(period);

        schedulerForAWeekRadioButton.setAccessibleText("FOR_A_WEEK");
        schedulerForAMonthRadioButton.setAccessibleText("FOR_A_MONTH");
        schedulerForAYearRadioButton.setAccessibleText("FOR_A_YEAR");

        scheduleDontRepeatRadioButton.setOnAction(event ->
        {
            int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
            settingsMap.get(id).setRepeatSelected(ScheduleSettingsRepeat.DONT);
            schedulerForAWeekRadioButton.setDisable(true);
            schedulerForAMonthRadioButton.setDisable(true);
            schedulerForAYearRadioButton.setDisable(true);
            schedulerForAWeekRadioButton.setSelected(false);
            schedulerForAMonthRadioButton.setSelected(false);
            schedulerForAYearRadioButton.setSelected(false);
        });

        scheduleDayRepeatRadioButton.setOnAction(event ->
        {
            int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
            settingsMap.get(id).setRepeatSelected(ScheduleSettingsRepeat.DAY);
            schedulerForAWeekRadioButton.setDisable(false);
            schedulerForAMonthRadioButton.setDisable(false);
            schedulerForAYearRadioButton.setDisable(true);
            schedulerForAYearRadioButton.setSelected(false);
        });

        scheduleWeekRepeatRadioButton.setOnAction(event ->
        {
            int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
            settingsMap.get(id).setRepeatSelected(ScheduleSettingsRepeat.WEEK);
            schedulerForAWeekRadioButton.setDisable(true);
            schedulerForAMonthRadioButton.setDisable(false);
            schedulerForAYearRadioButton.setDisable(false);
            schedulerForAWeekRadioButton.setSelected(false);
        });

        scheduleMonthRepeatRadioButton.setOnAction(event ->
        {
            int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
            settingsMap.get(id).setRepeatSelected(ScheduleSettingsRepeat.MONTH);
            schedulerForAWeekRadioButton.setDisable(true);
            schedulerForAMonthRadioButton.setDisable(true);
            schedulerForAYearRadioButton.setDisable(false);
            schedulerForAWeekRadioButton.setSelected(false);
            schedulerForAMonthRadioButton.setSelected(false);
        });

        scheduleYearRepeatRadioButton.setOnAction(event ->
        {
            int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
            settingsMap.get(id).setRepeatSelected(ScheduleSettingsRepeat.YEAR);
            settingsMap.get(id).setPeriodSelected(ScheduleSettingsPeriod.DONT);
            schedulerForAWeekRadioButton.setDisable(true);
            schedulerForAMonthRadioButton.setDisable(true);
            schedulerForAYearRadioButton.setDisable(true);
            schedulerForAWeekRadioButton.setSelected(false);
            schedulerForAMonthRadioButton.setSelected(false);
            schedulerForAYearRadioButton.setSelected(false);
        });


        schedulerForAWeekRadioButton.setOnAction(event ->
        {
            if (scheduleDayRepeatRadioButton.isSelected())
            {
                int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
                settingsMap.get(id).setPeriodSelected(ScheduleSettingsPeriod.FOR_A_WEEK);
            } else
            {
                schedulerForAWeekRadioButton.setSelected(false);
                event.consume();
            }
        });

        schedulerForAMonthRadioButton.setOnAction(event ->
        {
            if (scheduleDayRepeatRadioButton.isSelected() || scheduleWeekRepeatRadioButton.isSelected())
            {
                int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
                settingsMap.get(id).setPeriodSelected(ScheduleSettingsPeriod.FOR_A_MONTH);
            } else
            {
                schedulerForAMonthRadioButton.setSelected(false);
                event.consume();
            }

        });

        schedulerForAYearRadioButton.setOnAction(event ->
        {
            if (scheduleWeekRepeatRadioButton.isSelected() || scheduleMonthRepeatRadioButton.isSelected())
            {
                int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
                settingsMap.get(id).setPeriodSelected(ScheduleSettingsPeriod.FOR_A_YEAR);
            } else
            {
                schedulerForAYearRadioButton.setSelected(false);
                event.consume();
            }
        });
    }

    public void showSettings(AnchorPane root, MouseEvent mouseEvent)
    {
        int leftUpperCornerX = (int) (mouseEvent.getSceneX() - mouseEvent.getX()) - 314;//314 - Расстояние от края кнопки до края элемента
        int leftUpperCornerY = (int) (mouseEvent.getSceneY() - mouseEvent.getY());
        int layoutX = leftUpperCornerX + 460;
        int width = 246;

        if (layoutX + width > DEFAULT_WIDTH)
        {
            layoutX = layoutX - 460 - width;
        }
        root.setLayoutX(layoutX);
        root.setLayoutY(leftUpperCornerY);
        updateRadioButtons(root);
        addChild(root);
    }

    private void updateRadioButtons(AnchorPane root)
    {
        var settings = settingsMap.get(Integer.parseInt(root.getAccessibleHelp()));
        String repeat = settings.getRepeatSelected().name();
        String period = settings.getPeriodSelected().name();
        var toolBar = ((ToolBar) root.getChildren().get(0));
        for (Node node : toolBar.getItems())
        {
            if (node instanceof VBox)
            {
                for (Node button : ((VBox) node).getChildren())
                {
                    if (button instanceof RadioButton && button.getAccessibleText().equals(repeat))
                    {
                        ((RadioButton) button).setSelected(true);
                    }
                    if (button instanceof RadioButton && button.getAccessibleText().equals(period))
                    {
                        ((RadioButton) button).setSelected(true);
                    }
                }
            }
        }
    }
}
