package com.camper.SmartDesktop.StandardElements;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.w3c.dom.Document;


import javax.xml.xpath.XPath;
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

    private boolean load = false;
    private AnchorPane TableRoot;
    private int id;
    private TableView<List<String>> table = null;
    private static AnchorPane selectedTable;
    private static Map<Integer, TableSD> tables = new HashMap<>();
    private static int nextId = 1;

    public TableSD()
    {
    }

    private TableSD(boolean load)
    {
        this.load = load;
    }

    private AnchorPane getTableRoot()
    {
        return TableRoot;
    }

    public static void clearSaveList()
    {
        tables.clear();
        nextId = 1;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        logger.info("TableSD: begin start method");
        TableRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/table.fxml")));
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
        for (int row = 0; row < 100; row++)
        {
            table.getItems().add(new ArrayList<>());
        }
        TableRoot.getChildren().add(table);


        addChild(TableRoot);
        if (!load)
        {
            TableRoot.setAccessibleText(String.valueOf(idOfSelectedTab));
            var elementsOfSelectedTab = tabs.get(idOfSelectedTab);
            elementsOfSelectedTab.add(TableRoot);
        }
        logger.info("TableSD: end start method");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        logger.info("TableSD: begin initialize method");
        tableCloseButton.setOnAction(event ->
        {
            selectedTable = (AnchorPane) (((Button) event.getSource()).getParent());
            tables.remove(Integer.parseInt(selectedTable.getAccessibleHelp()));
            Main.root.getChildren().remove(selectedTable);
            logger.info("TableSD: table was removed");
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
            dialog.setHeaderText(languageBundle.getString("tableNewColumnAlert"));
            var result = dialog.showAndWait().orElse(null);
            if (result != null)
            {
                tableSD.addNewColumnToTheTable(result);
            }
        });
        logger.info("TableSD: end initialize method");
    }

    public void addNewColumnToTheTable(String columnName)
    {
        var newColumn = new TableColumn<List<String>, String>(columnName);
        this.table.getColumns().add(newColumn);
        for (var row : this.table.getItems())
        {
            row.add(row.size(), "");
        }
        int index = this.table.getColumns().size() - 1;
        newColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(index)));
        newColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        newColumn.setOnEditCommit((TableColumn.CellEditEvent<List<String>, String> editEvent) ->
        {
            TablePosition<List<String>, String> pos = editEvent.getTablePosition();

            String newValue = editEvent.getNewValue();

            int numberOfRow = pos.getRow();
            int numberOfCol = pos.getColumn();
            List<String> editList = editEvent.getTableView().getItems().get(numberOfRow);
            editList.set(numberOfCol, newValue);
        });
    }

    public static void addTablesToXML(Document doc, boolean createEmptyXML)
    {
        logger.info("TableSD: start tables saving");
        var rootElement = doc.getFirstChild();

        var tablesElement = doc.createElement("tables");
        rootElement.appendChild(tablesElement);
        if (!createEmptyXML)
        {
            int id = 1;
            for (var entry : tables.entrySet())
            {
                var tableSD = entry.getValue();
                var table = tableSD.getTableRoot();
                var tableElement = doc.createElement("table" + id);
                tableElement.setAttribute("tab", table.getAccessibleText());

                tablesElement.appendChild(tableElement);

                var visibilityElement = doc.createElement("visibility");
                tableElement.appendChild(visibilityElement);
                var visibilityValue = doc.createTextNode(String.valueOf(table.isVisible()));
                visibilityElement.appendChild(visibilityValue);

                var layoutElement = doc.createElement("layout");
                tableElement.appendChild(layoutElement);

                var layoutX = doc.createElement("layoutX");
                layoutElement.appendChild(layoutX);
                var layoutXValue = doc.createTextNode(String.valueOf((int) (table.getLayoutX())));
                layoutX.appendChild(layoutXValue);

                var layoutY = doc.createElement("layoutY");
                layoutElement.appendChild(layoutY);
                var layoutYValue = doc.createTextNode(String.valueOf((int) (table.getLayoutY())));
                layoutY.appendChild(layoutYValue);

                var columnsElement = doc.createElement("columns");
                tableElement.appendChild(columnsElement);

                if (tableSD.table != null)
                {
                    int numberOfColumn = 1;
                    for (var column : tableSD.table.getColumns())
                    {
                        var columnElement = doc.createElement("column" + numberOfColumn);
                        columnsElement.appendChild(columnElement);

                        var columnNameElement = doc.createElement("columnName");
                        columnElement.appendChild(columnNameElement);
                        var columnElementValue = doc.createTextNode(column.getText());
                        columnNameElement.appendChild(columnElementValue);

                        var rowsElement = doc.createElement("rows");
                        columnElement.appendChild(rowsElement);

                        int numberOfRowWithValue = 1;
                        int numberOfRow = 0;
                        for (var row : tableSD.table.getItems())
                        {
                            var value = row.get(numberOfColumn - 1);
                            if (!value.equals(""))
                            {
                                var rowElement = doc.createElement("row" + numberOfRowWithValue);
                                rowsElement.appendChild(rowElement);
                                rowElement.setAttribute("numberOfRow", String.valueOf(numberOfRow));
                                var rowElementValue = doc.createTextNode(value);
                                rowElement.appendChild(rowElementValue);
                                numberOfRowWithValue++;
                            }
                            numberOfRow++;
                        }
                        numberOfColumn++;
                    }
                }
                id++;
            }
        }
        logger.info("TableSD: end tables saving");
    }

    public static void loadTablesFromXML(Document doc, XPath xPath) throws Exception
    {
        logger.info("TableSD: start tables loading");
        int numberOfTables = xPath.evaluateExpression("count(/save/tables/*)", doc, Integer.class);
        for (int id = 1; id < numberOfTables + 1; id++)
        {
            var loadingTable = new TableSD(true);
            loadingTable.start(Main.Stage);
            var rootOfLoadingTable = loadingTable.getTableRoot();

            int numberOfTab = Integer.parseInt(xPath.evaluate("/save/tables/table" + id + "/@tab", doc));
            rootOfLoadingTable.setAccessibleText(String.valueOf(numberOfTab));

            var tab = tabs.get(numberOfTab);
            tab.add(rootOfLoadingTable);
            boolean visibility = Boolean.parseBoolean(xPath.evaluate("/save/tables/table" + id + "/visibility/text()", doc));
            rootOfLoadingTable.setVisible(visibility);

            double layoutX = Double.parseDouble(xPath.evaluate("/save/tables/table" + id + "/layout/layoutX/text()", doc));
            double layoutY = Double.parseDouble(xPath.evaluate("/save/tables/table" + id + "/layout/layoutY/text()", doc));
            rootOfLoadingTable.setLayoutX(layoutX);
            rootOfLoadingTable.setLayoutY(layoutY);

            int numberOfColumns = xPath.evaluateExpression("count(/save/tables/table" + id + "/columns/*)", doc, Integer.class);
            for (int numberOfColumn = 1; numberOfColumn < numberOfColumns + 1; numberOfColumn++)
            {
                String columnName = xPath.evaluate("/save/tables/table" + id + "/columns/column" + numberOfColumn + "/columnName/text()", doc);
                loadingTable.addNewColumnToTheTable(columnName);

                int numberOfRowsWithValue = xPath.evaluateExpression("count(/save/tables/table" + id + "/columns/column" + numberOfColumn + "/rows/*)", doc, Integer.class);
                for (int numberOfRowWithValue = 1; numberOfRowWithValue < numberOfRowsWithValue + 1; numberOfRowWithValue++)
                {
                    int numberOfRow = Integer.parseInt(xPath.evaluate("/save/tables/table" + id + "/columns/column" + numberOfColumn + "/rows/row" + numberOfRowWithValue +"/@numberOfRow", doc));
                    String rowValue = xPath.evaluate("/save/tables/table" + id + "/columns/column" + numberOfColumn + "/rows/row" + numberOfRowWithValue +"/text()", doc);
                    loadingTable.table.getItems().get(numberOfRow).set(numberOfColumn-1,rowValue);
                }
            }
        }
        logger.info("TableSD: end tables loading");
    }
}