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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.net.URL;
import java.util.List;
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
    @FXML
    private ImageView weatherCloseButtonIV;

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
        logger.info("Weather: begin start method");
        WeatherRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/weather.fxml")));
        WeatherRoot.setLayoutX(80);
        WeatherRoot.setLayoutY(30);

        addChild(WeatherRoot);
        if (!load)
        {
            WeatherRoot.setAccessibleText(String.valueOf(idOfSelectedTab));
            List<javafx.scene.Node> elementsOfSelectedTab = tabs.get(idOfSelectedTab);
            elementsOfSelectedTab.add(WeatherRoot);
        }
        logger.info("Weather: end start method");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        logger.info("Weather: begin initialize method");
        weatherCloseButtonIV.setImage(new Image("Images/delete30.png"));
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
            logger.info("Weather: weather element was closed");
        });

        String site;
        if (defaultLocale.equals(Locale.ENGLISH))
        {
            site = "https://weather.com";
        } else
        {
            site = "https://yandex.ru/pogoda";
        }
        WebEngine engine = weatherWebView.getEngine();
        weatherWebView.getEngine().setUserStyleSheetLocation(Objects.requireNonNull(mainCL.getResource("FXMLs/webView.css")).toExternalForm());
        engine.load(site);
        logger.info("Weather: end initialize method");
    }

    public static void addWeatherInfoToXML(Document doc, boolean createEmptyXML)
    {
        logger.info("Weather: start saving weather");
        Node rootElement = doc.getFirstChild();

        Element weatherInfoElement = doc.createElement("weatherInfo");
        rootElement.appendChild(weatherInfoElement);
        if (!createEmptyXML && WeatherRoot != null)
        {
            weatherInfoElement.setAttribute("tab", WeatherRoot.getAccessibleText());

            Element visibilityElement = doc.createElement("visibility");
            weatherInfoElement.appendChild(visibilityElement);
            Text visibilityValue = doc.createTextNode(String.valueOf(WeatherRoot.isVisible()));
            visibilityElement.appendChild(visibilityValue);

            Element layoutElement = doc.createElement("layout");
            weatherInfoElement.appendChild(layoutElement);

            Element layoutX = doc.createElement("layoutX");
            layoutElement.appendChild(layoutX);
            Text layoutXValue = doc.createTextNode(String.valueOf((int) (WeatherRoot.getLayoutX())));
            layoutX.appendChild(layoutXValue);

            Element layoutY = doc.createElement("layoutY");
            layoutElement.appendChild(layoutY);
            Text layoutYValue = doc.createTextNode(String.valueOf((int) (WeatherRoot.getLayoutY())));
            layoutY.appendChild(layoutYValue);
        }
        logger.info("Weather: end saving weather");
    }

    public static void loadWeatherInfoFromXML(Document doc, XPath xPath) throws Exception
    {
        logger.info("Weather: start loading weather");
        XPathExpression weatherCompile = xPath.compile("count(/save/weatherInfo/*)");
        boolean notEmpty = Integer.parseInt((String)weatherCompile.evaluate(doc, XPathConstants.STRING)) != 0;
        if (notEmpty)
        {
            Weather loadingWeatherInfo = new Weather(true);
            loadingWeatherInfo.start(Stage);

            int numberOfTab = Integer.parseInt(xPath.evaluate("/save/weatherInfo/@tab", doc));
            WeatherRoot.setAccessibleText(String.valueOf(numberOfTab));

            List<javafx.scene.Node> tab = tabs.get(numberOfTab);
            tab.add(WeatherRoot);
            boolean visibility = Boolean.parseBoolean(xPath.evaluate("/save/weatherInfo/visibility/text()", doc));
            WeatherRoot.setVisible(visibility);

            double layoutX = Double.parseDouble(xPath.evaluate("/save/weatherInfo/layout/layoutX/text()", doc));
            double layoutY = Double.parseDouble(xPath.evaluate("/save/weatherInfo/layout/layoutY/text()", doc));
            WeatherRoot.setLayoutX(layoutX);
            WeatherRoot.setLayoutY(layoutY);
        }
        logger.info("Weather: end loading weather");
    }
}
