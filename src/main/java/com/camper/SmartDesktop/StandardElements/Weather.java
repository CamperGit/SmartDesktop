package com.camper.SmartDesktop.StandardElements;

import com.camper.SmartDesktop.Info.UpcomingEvent;
import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.camper.SmartDesktop.Main.*;

public class Weather extends Application implements Initializable
{
    @FXML
    private Button weatherCloseButton;
    @FXML
    private ToolBar weatherToolBar;
    @FXML
    private WebView weatherWebView;

    private static AnchorPane WeatherRoot = null;
    private boolean load = false;

    public static AnchorPane getWeatherRoot()
    {
        return WeatherRoot;
    }

    public Weather()
    {
    }

    private Weather(boolean load)
    {
        this.load = load;
    }

    public static void clearLastInfo()
    {
        Main.root.getChildren().remove(WeatherRoot);
        WeatherRoot = null;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        WeatherRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/weather.fxml")));
        WeatherRoot.setLayoutX(80);
        WeatherRoot.setLayoutY(30);

        addChild(WeatherRoot);
        if (!load)
        {
            WeatherRoot.setAccessibleText(String.valueOf(idOfSelectedTab));
            var elementsOfSelectedTab = tabs.get(idOfSelectedTab);
            elementsOfSelectedTab.add(WeatherRoot);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        weatherToolBar.setOnMouseDragged(event ->
        {
            WeatherRoot = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            NodeDragger.addDraggingProperty(WeatherRoot, event);
        });

        weatherCloseButton.setOnAction(event ->
        {
            WeatherRoot = (AnchorPane) (((Button) event.getSource()).getParent());
            Main.root.getChildren().remove(WeatherRoot);
            WeatherRoot = null;
        });

        String site;
        if (defaultLocale.equals(Locale.ENGLISH))
        {
            site = "https://weather.com";
        } else
        {
            site = "https://yandex.ru/pogoda";
        }
        var engine = weatherWebView.getEngine();
        weatherWebView.getEngine().setUserStyleSheetLocation(Objects.requireNonNull(mainCL.getResource("FXMLs/webView.css")).toExternalForm());
        engine.load(site);
    }

    public static void addWeatherInfoToXML(Document doc, boolean createEmptyXML)
    {
        var rootElement = doc.getFirstChild();

        var weatherInfoElement = doc.createElement("weatherInfo");
        rootElement.appendChild(weatherInfoElement);
        if (!createEmptyXML && WeatherRoot != null)
        {
            weatherInfoElement.setAttribute("tab", WeatherRoot.getAccessibleText());

            var visibilityElement = doc.createElement("visibility");
            weatherInfoElement.appendChild(visibilityElement);
            var visibilityValue = doc.createTextNode(String.valueOf(WeatherRoot.isVisible()));
            visibilityElement.appendChild(visibilityValue);

            var layoutElement = doc.createElement("layout");
            weatherInfoElement.appendChild(layoutElement);

            var layoutX = doc.createElement("layoutX");
            layoutElement.appendChild(layoutX);
            var layoutXValue = doc.createTextNode(String.valueOf((int) (WeatherRoot.getLayoutX())));
            layoutX.appendChild(layoutXValue);

            var layoutY = doc.createElement("layoutY");
            layoutElement.appendChild(layoutY);
            var layoutYValue = doc.createTextNode(String.valueOf((int) (WeatherRoot.getLayoutY())));
            layoutY.appendChild(layoutYValue);
        }
    }

    public static void loadWeatherInfoFromXML(Document doc, XPath xPath) throws Exception
    {
        boolean notEmpty = xPath.evaluateExpression("count(/save/weatherInfo/*)", doc, Integer.class) != 0;
        if (notEmpty)
        {
            var loadingWeatherInfo = new Weather(true);
            loadingWeatherInfo.start(Stage);

            int numberOfTab = Integer.parseInt(xPath.evaluate("/save/weatherInfo/@tab", doc));
            WeatherRoot.setAccessibleText(String.valueOf(numberOfTab));

            var tab = tabs.get(numberOfTab);
            tab.add(WeatherRoot);
            boolean visibility = Boolean.parseBoolean(xPath.evaluate("/save/weatherInfo/visibility/text()", doc));
            WeatherRoot.setVisible(visibility);

            double layoutX = Double.parseDouble(xPath.evaluate("/save/weatherInfo/layout/layoutX/text()", doc));
            double layoutY = Double.parseDouble(xPath.evaluate("/save/weatherInfo/layout/layoutY/text()", doc));
            WeatherRoot.setLayoutX(layoutX);
            WeatherRoot.setLayoutY(layoutY);
        }
    }
}
