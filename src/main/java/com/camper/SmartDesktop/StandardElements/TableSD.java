package com.camper.SmartDesktop.StandardElements;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import java.net.URL;
import java.util.*;

import static com.camper.SmartDesktop.Main.*;

public class TableSD extends Application implements Initializable
{
    @FXML
    private Button tableAddColumnButton;
    @FXML
    private Button tableCloseButton;
    @FXML
    private ToolBar tableToolBar;

    private boolean load=false;
    private AnchorPane TableRoot;
    private int id;
    /*private TableView<List<String>> table;*/
    private ObservableList<List<String>> data = FXCollections.observableArrayList();
    private TableView<ObservableList<StringProperty>> table;
    private ArrayList<String> myList = new ArrayList<>();
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

    private ObservableList<List<String>> getData()
    {
        return data;
    }

    /*public void setTable(TableView<List<String>> table)
    {
        this.table = table;
    }

    public TableView<List<String>> getTable()
    {
        return table;
    }*/

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

        table = new TableView<>();
        table.setEditable(true);
        table.setPrefWidth(540);
        table.setPrefHeight(400);
        table.setLayoutY(25);
        TableRoot.getChildren().add(table);


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

        tableAddColumnButton.setOnAction(event ->
        {
            var tableSD = tables.get(Integer.parseInt(((AnchorPane) (((Button) event.getSource()).getParent())).getAccessibleHelp()));
            var dialog = new TextInputDialog();
            dialog.setHeaderText("Введите название столбца");
            var result = dialog.showAndWait().orElse(null);
            if (result!=null)
            {
                var newColumn = new TableColumn<ObservableList<StringProperty>,String>(result);
                //newColumn.setCellValueFactory(new PropertyValueFactory<>(result));
                newColumn.setCellFactory(TextFieldTableCell.forTableColumn());
                tableSD.table.getColumns().add(newColumn);

                for (int row = 0; row < 100; row++) {
                    ObservableList<StringProperty> data = FXCollections.observableArrayList();
                    data.add(tableSD.table.getColumns().size()-1, new SimpleStringProperty(""));
                    tableSD.table.getItems().add(data);
                }

                newColumn.setOnEditCommit((TableColumn.CellEditEvent<ObservableList<StringProperty>, String> editEvent) ->
                {
                    TablePosition<ObservableList<StringProperty>, String> pos = editEvent.getTablePosition();

                    String newValue = editEvent.getNewValue();

                    int row = pos.getRow();
                    ObservableList<StringProperty> list = editEvent.getTableView().getItems().get(row);
                    list.remove(pos.getColumn());
                    list.add(new SimpleStringProperty(newValue));
                });

            }
        });

    }
}