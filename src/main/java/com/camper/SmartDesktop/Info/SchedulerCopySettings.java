package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
    private RadioButton scheduleDontRepeatRadioButton, scheduleDayRepeatRadioButton, scheduleWeekRepeatRadioButton, scheduleMonthRepeatRadioButton, scheduleYearRepeatRadioButton;

    @FXML
    private RadioButton schedulerForAWeekRadioButton, schedulerForAMonthRadioButton, schedulerForAYearRadioButton;

    @FXML
    private Label scheduleSettingsRepeatLabel, scheduleSettingsDuringLabel;

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
    private Button saveButton;
    private ScheduleSettingsRepeat repeatSelected = ScheduleSettingsRepeat.DONT;
    private ScheduleSettingsPeriod periodSelected = ScheduleSettingsPeriod.DONT;//=ScheduleSettingsPeriod.FOR_A_WEEK;
    private boolean entered = false;
    private boolean load = false;
    private int id;
    private static final Map<Integer, SchedulerCopySettings> settingsMap = new HashMap<>();

    public SchedulerCopySettings()
    {
    }

    public SchedulerCopySettings(MouseEvent mouseEvent, int idOfSchedule, Button saveButton)
    {
        this.mouseEvent = mouseEvent;
        this.id = idOfSchedule;
        this.saveButton = saveButton;
    }

    public SchedulerCopySettings(String repeat, String period, boolean load, int idOfSchedule, Button saveButton)
    {
        this(null, idOfSchedule, saveButton);
        repeatSelected = ScheduleSettingsRepeat.valueOf(repeat);
        periodSelected = ScheduleSettingsPeriod.valueOf(period);
        this.load = load;
    }

    public AnchorPane getCopySettingsRoot()
    {
        return CopySettingsRoot;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        logger.info("ScheduleCopySettings: begin start method");
        CopySettingsRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/scheduleSettings.fxml")));
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
            SchedulerCopySettings settings = settingsMap.get(id);
            if (settings.isEntered())
            {
                settings.setEntered(false);
                Main.root.getChildren().remove(settings.getCopySettingsRoot());
                logger.info("ScheduleCopySettings: close copySettings window");
            }
        });

        if (!load)
        {
            showSettings(CopySettingsRoot, mouseEvent);
        }
        logger.info("ScheduleCopySettings: begin start method");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        logger.info("ScheduleCopySettings: begin initialize method");
        scheduleSettingsRepeatLabel.setText(languageBundle.getString("scheduleSettingsRepeatLabel"));
        scheduleSettingsDuringLabel.setText(languageBundle.getString("scheduleSettingsDuringLabel"));
        scheduleDontRepeatRadioButton.setText(languageBundle.getString("scheduleDontRepeatRadioButton"));
        scheduleDayRepeatRadioButton.setText(languageBundle.getString("scheduleDayRepeatRadioButton"));
        scheduleWeekRepeatRadioButton.setText(languageBundle.getString("scheduleWeekRepeatRadioButton"));
        scheduleMonthRepeatRadioButton.setText(languageBundle.getString("scheduleMonthRepeatRadioButton"));
        scheduleYearRepeatRadioButton.setText(languageBundle.getString("scheduleYearRepeatRadioButton"));
        schedulerForAWeekRadioButton.setText(languageBundle.getString("schedulerForAWeekRadioButton"));
        schedulerForAMonthRadioButton.setText(languageBundle.getString("schedulerForAMonthRadioButton"));
        schedulerForAYearRadioButton.setText(languageBundle.getString("schedulerForAYearRadioButton"));

        ToggleGroup repeat = new ToggleGroup();
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

        ToggleGroup period = new ToggleGroup();
        schedulerForAWeekRadioButton.setToggleGroup(period);
        schedulerForAMonthRadioButton.setToggleGroup(period);
        schedulerForAYearRadioButton.setToggleGroup(period);

        schedulerForAWeekRadioButton.setAccessibleText("FOR_A_WEEK");
        schedulerForAMonthRadioButton.setAccessibleText("FOR_A_MONTH");
        schedulerForAYearRadioButton.setAccessibleText("FOR_A_YEAR");

        scheduleDontRepeatRadioButton.setOnAction(event ->
        {
            Node node = ((RadioButton) event.getSource()).getParent().getParent();
            if (node != null)
            {
                int id = Main.returnAnchorId(node);
                SchedulerCopySettings settings = settingsMap.get(id);
                settings.repeatSelected = ScheduleSettingsRepeat.DAY;
                settings.saveButton.setDisable(false);
            }
            schedulerForAWeekRadioButton.setDisable(true);
            schedulerForAMonthRadioButton.setDisable(true);
            schedulerForAYearRadioButton.setDisable(true);
            schedulerForAWeekRadioButton.setSelected(false);
            schedulerForAMonthRadioButton.setSelected(false);
            schedulerForAYearRadioButton.setSelected(false);
        });

        scheduleDayRepeatRadioButton.setOnAction(event ->
        {
            Node node = ((RadioButton) event.getSource()).getParent().getParent();
            if (node != null)
            {
                int id = Main.returnAnchorId(node);
                SchedulerCopySettings settings = settingsMap.get(id);
                settings.repeatSelected = ScheduleSettingsRepeat.DAY;
                settings.saveButton.setDisable(false);
            }
            schedulerForAWeekRadioButton.setDisable(false);
            schedulerForAMonthRadioButton.setDisable(false);
            schedulerForAYearRadioButton.setDisable(true);
            schedulerForAYearRadioButton.setSelected(false);
        });

        scheduleWeekRepeatRadioButton.setOnAction(event ->
        {
            Node node = ((RadioButton) event.getSource()).getParent().getParent();
            if (node != null)
            {
                int id = Main.returnAnchorId(node);
                SchedulerCopySettings settings = settingsMap.get(id);
                settings.repeatSelected = ScheduleSettingsRepeat.WEEK;
                settings.saveButton.setDisable(false);
            }
            schedulerForAWeekRadioButton.setDisable(true);
            schedulerForAMonthRadioButton.setDisable(false);
            schedulerForAYearRadioButton.setDisable(false);
            schedulerForAWeekRadioButton.setSelected(false);
        });

        scheduleMonthRepeatRadioButton.setOnAction(event ->
        {
            Node node = ((RadioButton) event.getSource()).getParent().getParent();
            if (node != null)
            {
                int id = Main.returnAnchorId(node);
                SchedulerCopySettings settings = settingsMap.get(id);
                settings.repeatSelected = ScheduleSettingsRepeat.MONTH;
                settings.saveButton.setDisable(false);
            }
            schedulerForAWeekRadioButton.setDisable(true);
            schedulerForAMonthRadioButton.setDisable(true);
            schedulerForAYearRadioButton.setDisable(false);
            schedulerForAWeekRadioButton.setSelected(false);
            schedulerForAMonthRadioButton.setSelected(false);
        });

        scheduleYearRepeatRadioButton.setOnAction(event ->
        {
            Node node = ((RadioButton) event.getSource()).getParent().getParent();
            if (node != null)
            {
                int id = Main.returnAnchorId(node);
                SchedulerCopySettings settings = settingsMap.get(id);
                settings.repeatSelected = ScheduleSettingsRepeat.YEAR;
                settings.periodSelected = ScheduleSettingsPeriod.DONT;
                settings.saveButton.setDisable(false);
            }
            schedulerForAWeekRadioButton.setDisable(true);
            schedulerForAMonthRadioButton.setDisable(true);
            schedulerForAYearRadioButton.setDisable(true);
            schedulerForAWeekRadioButton.setSelected(false);
            schedulerForAMonthRadioButton.setSelected(false);
            schedulerForAYearRadioButton.setSelected(false);
        });


        schedulerForAWeekRadioButton.setOnAction(event ->
        {
            int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
            SchedulerCopySettings settings = settingsMap.get(id);
            settings.periodSelected = ScheduleSettingsPeriod.FOR_A_WEEK;
            settings.saveButton.setDisable(false);
        });

        schedulerForAMonthRadioButton.setOnAction(event ->
        {
            int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
            SchedulerCopySettings settings = settingsMap.get(id);
            settings.periodSelected = ScheduleSettingsPeriod.FOR_A_MONTH;
            settings.saveButton.setDisable(false);

        });

        schedulerForAYearRadioButton.setOnAction(event ->
        {
            int id = Main.returnAnchorId(((RadioButton) event.getSource()).getParent());
            SchedulerCopySettings settings = settingsMap.get(id);
            settings.periodSelected = ScheduleSettingsPeriod.FOR_A_YEAR;
            settings.saveButton.setDisable(false);
        });
        logger.info("ScheduleCopySettings: end initialize method");
    }

    public void fireSchedulerCopySettingsRadioButton(String buttonName)
    {
        ToolBar toolBar=null;
        for (Node node : this.getCopySettingsRoot().getChildren())
        {
            if (node instanceof ToolBar)
            {
                toolBar=(ToolBar) node;
                break;
            }
        }
        if (toolBar!=null)
        {
            for (Node node : toolBar.getItems())
            {
                if(node instanceof VBox)
                {
                    for (Node vboxChild : ((VBox) node).getChildren())
                    {
                        if (vboxChild instanceof RadioButton && vboxChild.getAccessibleText() != null && vboxChild.getAccessibleText().equals(buttonName))
                        {
                            ((RadioButton) vboxChild).fire();
                        }
                    }
                }
            }
        }
    }

    public void showSettings(AnchorPane root, MouseEvent mouseEvent)
    {
        int leftUpperCornerX = (int) (mouseEvent.getSceneX() - mouseEvent.getX()) - 314;//314 - Расстояние от края кнопки до края элемента
        int leftUpperCornerY = (int) (mouseEvent.getSceneY() - mouseEvent.getY());
        int layoutX = leftUpperCornerX + 460;
        int width = 246;//Ширина элемента

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
        SchedulerCopySettings settings = settingsMap.get(Integer.parseInt(root.getAccessibleHelp()));
        String repeat = settings.getRepeatSelected().name();
        String period = settings.getPeriodSelected().name();
        ToolBar toolBar = ((ToolBar) root.getChildren().get(0));
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
