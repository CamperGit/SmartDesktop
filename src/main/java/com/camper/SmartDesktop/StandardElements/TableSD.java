package com.camper.SmartDesktop.StandardElements;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.camper.SmartDesktop.Main.*;

public class TableSD extends Application implements Initializable
{
    @FXML
    private Button tableAddColumnButton;
    @FXML
    private Button tableCloseButton;
    @FXML
    private ToolBar tableToolBar;
    @FXML
    private TableView tableTableView;

    private boolean load=false;
    private AnchorPane TableRoot;
    private int id;
    private static AnchorPane selectedTable;
    private static Map<Integer, TableSD> tables = new HashMap<>();
    private static int nextId=1;

    public TableSD(){}

    private TableSD(boolean load)
    {this.load = load;}

    private AnchorPane getTableRoot()
    {
        return TableRoot;
    }

    public static void clearSaveList()
    {
        tables.clear();
        nextId=1;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        TableRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/tableRu.fxml")));
        TableRoot.setLayoutX(80);
        TableRoot.setLayoutY(30);
        this.id = nextId;
        nextId++;
        tables.put(this.id, this);
        TableRoot.setAccessibleHelp(String.valueOf(this.id));
        addChild(TableRoot);
        if (!load)
        {
            TableRoot.setAccessibleText(String.valueOf(idOfSelectedTab));
            var elementsOfSelectedTab = tabs.get(idOfSelectedTab);
            elementsOfSelectedTab.add(TableRoot);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        tableCloseButton.setOnAction(event ->
        {
            selectedTable = (AnchorPane) (((Button) event.getSource()).getParent());
            tables.remove(Integer.parseInt(selectedTable.getAccessibleHelp()));
            Main.root.getChildren().remove(selectedTable);
        });

        tableToolBar.setOnMouseDragged(event ->
        {
            selectedTable = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            NodeDragger.addDraggingProperty(selectedTable, event);
        });
    }
}
