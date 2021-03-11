package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.camper.SmartDesktop.Main.*;

public class GoalSDProgressInfo extends Application
{
    private AnchorPane goalProgressRoot;
    private MouseEvent mouseEvent;
    private boolean entered = false;
    private int id;
    private int done;
    private int underway;
    private int notDone;
    private static Map<Integer, GoalSDProgressInfo> progressInfoMap = new HashMap<>();

    public GoalSDProgressInfo(int idOfGoal, MouseEvent mouseEvent)
    {
        this.id = idOfGoal;
        this.mouseEvent = mouseEvent;
        goalProgressRoot = new AnchorPane();
        Main.setRegion(goalProgressRoot, 400, 320);
    }

    public void setEntered(boolean entered)
    {
        this.entered = entered;
    }

    public boolean isEntered()
    {
        return entered;
    }

    public void setMouseEvent(MouseEvent mouseEvent)
    {
        this.mouseEvent = mouseEvent;
    }

    public AnchorPane getGoalProgressRoot()
    {
        return goalProgressRoot;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        logger.info("GoalSDProgressInfo: begin start method");
        goalProgressRoot.setAccessibleHelp(String.valueOf(id));
        goalProgressRoot.setStyle("-fx-background-color: #f4f4f4");
        Main.setRegion(goalProgressRoot, 400, 320);
        progressInfoMap.put(id, this);

        goalProgressRoot.setOnMouseEntered(event ->
        {
            int id;
            if (event.getSource() instanceof AnchorPane)
            {
                id = Integer.parseInt(((AnchorPane) event.getSource()).getAccessibleHelp());
            } else
            {
                id = Main.returnAnchorId(((Node) event.getSource()).getParent());
            }
            progressInfoMap.get(id).setEntered(true);
        });
        goalProgressRoot.setOnMouseExited(event ->
        {
            int id;
            if (event.getSource() instanceof AnchorPane)
            {
                id = Integer.parseInt(((AnchorPane) event.getSource()).getAccessibleHelp());
            } else
            {
                id = Main.returnAnchorId(((Node) event.getSource()).getParent());
            }
            var progressInfo = progressInfoMap.get(id);
            if (progressInfo.isEntered())
            {
                progressInfo.setEntered(false);
                Main.root.getChildren().remove(progressInfo.getGoalProgressRoot());
                logger.info("GoalSDProgressInfo: close progress info");
            }
        });
        logger.info("GoalSDProgressInfo: end start method");
    }

    public void updatePieChart(Map<CheckBox, List<CheckBox>> groupOfGoalCheckBox, Map<EventOfDay, CheckBox> eventsOfCheckBoxes)
    {
        sortCheckBoxes(groupOfGoalCheckBox, eventsOfCheckBoxes, this);
        var chart = new PieChart();
        chart.getData().addAll(
                new PieChart.Data(languageBundle.getString("goalProgressInfoPieChartNotDone"), notDone),
                new PieChart.Data(languageBundle.getString("goalProgressInfoPieChartUnderway"), underway),
                new PieChart.Data(languageBundle.getString("goalProgressInfoPieChartDone"), done));
        chart.setTitle(languageBundle.getString("goalProgressInfoChartTitle"));
        Main.setRegion(chart, 400, 320);

        int leftUpperCornerX = (int) (mouseEvent.getSceneX() - mouseEvent.getX()) - 363;
        int leftUpperCornerY = (int) (mouseEvent.getSceneY() - mouseEvent.getY()) - 60;
        int layoutX = leftUpperCornerX + 460;
        int width = 400;

        if (layoutX + width > DEFAULT_WIDTH)
        {
            layoutX = layoutX - 460 - width;
        }
        goalProgressRoot.setLayoutX(layoutX);
        goalProgressRoot.setLayoutY(leftUpperCornerY);
        goalProgressRoot.getChildren().clear();
        goalProgressRoot.getChildren().add(chart);
        addChild(goalProgressRoot);
    }

    private void sortCheckBoxes(Map<CheckBox, List<CheckBox>> groupOfGoalCheckBox, Map<EventOfDay, CheckBox> eventsOfCheckBoxes, GoalSDProgressInfo progressInfo)
    {
        this.done = 0;
        this.underway = 0;
        this.notDone = 0;
        var map = new HashMap<CheckBox, EventOfDay>();
        for (var entry : eventsOfCheckBoxes.entrySet())
        {
            map.put(entry.getValue(), entry.getKey());
        }

        for (var entry : groupOfGoalCheckBox.entrySet())
        {
            for (var checkBoxOfTask : entry.getValue())
            {
                var time = map.get(checkBoxOfTask).getTime();
                var date = LocalDate.parse(checkBoxOfTask.getAccessibleText());
                var dateTime = LocalDateTime.of(date, time);
                if (!(checkBoxOfTask.isSelected()) && dateTime.isBefore(LocalDateTime.now()))
                {
                    progressInfo.notDone++;
                }
                if (!(checkBoxOfTask.isSelected()) && dateTime.isAfter(LocalDateTime.now()))
                {
                    progressInfo.underway++;
                }
                if (checkBoxOfTask.isSelected())
                {
                    progressInfo.done++;
                }
            }
            if (entry.getValue().size() == 0)
            {
                var selectAllCheckBox = entry.getKey();
                var time = map.get(selectAllCheckBox).getTime();
                var date = LocalDate.parse(selectAllCheckBox.getAccessibleText());
                var dateTime = LocalDateTime.of(date, time);
                if (!(selectAllCheckBox.isSelected()) && dateTime.isBefore(LocalDateTime.now()))
                {
                    progressInfo.notDone++;
                }
                if (!(selectAllCheckBox.isSelected()) && dateTime.isAfter(LocalDateTime.now()))
                {
                    progressInfo.underway++;
                }
                if (selectAllCheckBox.isSelected())
                {
                    progressInfo.done++;
                }
            }
        }
    }
}
