package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
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
    private Map<CheckBox, List<CheckBox>> groupOfGoalCheckBox;
    private Map<EventOfDay, CheckBox> eventsOfCheckBoxes;
    private MouseEvent mouseEvent;
    private boolean entered = false;
    private int id;
    private int done;
    private int onProgress;
    private int notDone;
    private static Map<Integer, GoalSDProgressInfo> progressInfoMap = new HashMap<>();

    public GoalSDProgressInfo(int idOfGoal, Map<CheckBox, List<CheckBox>> groupOfGoalCheckBox, Map<EventOfDay, CheckBox> eventsOfCheckBoxes, MouseEvent mouseEvent)
    {
        this.id = idOfGoal;
        this.groupOfGoalCheckBox = groupOfGoalCheckBox;
        this.eventsOfCheckBoxes=eventsOfCheckBoxes;
        this.mouseEvent=mouseEvent;
    }

    public void setEntered(boolean entered)
    {
        this.entered = entered;
    }

    public boolean isEntered()
    {
        return entered;
    }

    public AnchorPane getGoalProcessRoot()
    {
        return goalProgressRoot;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        sortCheckBoxes(groupOfGoalCheckBox,eventsOfCheckBoxes, this);
        var chart = new PieChart();
        chart.getData().addAll(
                new PieChart.Data("Выполнено", notDone),
                new PieChart.Data("В процессе", onProgress),
                new PieChart.Data("Не выполнено", done));
        chart.setTitle("Прогресс выполнения цели");
        Main.setRegion(chart,400,320);
        goalProgressRoot = new AnchorPane(chart);
        goalProgressRoot.setAccessibleHelp(String.valueOf(id));
        goalProgressRoot.setStyle("-fx-background-color: #f4f4f4");
        Main.setRegion(goalProgressRoot,400,320);
        progressInfoMap.put(id, this);

        int leftUpperCornerX = (int) (mouseEvent.getSceneX() - mouseEvent.getX()) - 363;
        int leftUpperCornerY = (int) (mouseEvent.getSceneY() - mouseEvent.getY())-60;
        int layoutX = leftUpperCornerX + 460;
        int width = 400;

        if (layoutX + width > DEFAULT_WIDTH)
        {
            layoutX = layoutX - 460 - width;
        }
        goalProgressRoot.setLayoutX(layoutX);
        goalProgressRoot.setLayoutY(leftUpperCornerY);
        addChild(goalProgressRoot);

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
                Main.root.getChildren().remove(progressInfo.getGoalProcessRoot());
                progressInfoMap.remove(id);
            }
        });
    }

    private void sortCheckBoxes(Map<CheckBox, List<CheckBox>> groupOfGoalCheckBox, Map<EventOfDay, CheckBox> eventsOfCheckBoxes, GoalSDProgressInfo progressInfo)
    {
        var map = new HashMap<CheckBox,EventOfDay>();
        for (var entry : eventsOfCheckBoxes.entrySet())
        {
            map.put(entry.getValue(),entry.getKey());
        }

        for (var entry : groupOfGoalCheckBox.entrySet())
        {
            for (var checkBoxOfTask : entry.getValue())
            {
                var time = map.get(checkBoxOfTask).getTime();
                var date = LocalDate.parse(checkBoxOfTask.getAccessibleText());
                var dateTime = LocalDateTime.of(date,time);
                if (!(checkBoxOfTask.isSelected()) && dateTime.isBefore(LocalDateTime.now()))
                {
                    progressInfo.notDone++;
                }
                if (!(checkBoxOfTask.isSelected()) && dateTime.isAfter(LocalDateTime.now()))
                {
                    progressInfo.onProgress++;
                }
                if (checkBoxOfTask.isSelected())
                {
                    progressInfo.done++;
                }
            }
            if (entry.getValue().size()==0)
            {
                progressInfo.notDone++;
            }
        }
    }
}
