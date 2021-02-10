package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

import javax.xml.xpath.XPath;
import java.awt.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.camper.SmartDesktop.Info.Day.addEventOfDay;
import static com.camper.SmartDesktop.Main.*;

public class Calendar extends Application implements Initializable
{
    @FXML private ToolBar calendarToolBar;
    @FXML private Button calendarCloseButton;
    @FXML private ChoiceBox<String> monthChoiceBox;
    @FXML private ChoiceBox<Integer> yearChoiceBox;
    private boolean load=false;
    private static AnchorPane CalendarRoot;
    private static List<Day> daysWithEvents = new ArrayList<>();
    public static List<ImageView> notificationIcons = new ArrayList<>();
    private static List<ImageView> targetIcons = new ArrayList<>();
    private static List<ImageView> scheduleIcons = new ArrayList<>();

    public Calendar(){}

    private Calendar(boolean load) { this.load=load; }

    public static AnchorPane getRoot() { return CalendarRoot; }

    public static void clearDaysWithEvents() { daysWithEvents.clear(); }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        CalendarRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/calendarRu.fxml")));
        CalendarRoot.setLayoutX(80);
        CalendarRoot.setLayoutY(30);
        addChild(CalendarRoot);
        if (!load)
        {
            //Если это не загрузка
            CalendarRoot.setAccessibleText("-1");
            CalendarRoot.setVisible(false);
        }
        else
        {
            notificationIcons.clear();
            targetIcons.clear();
            scheduleIcons.clear();
        }

        var dayWithEvent1 = addEventOfDay(LocalDate.of(2021,2,9), LocalTime.now(), Day.EventType.Goal,"test");
        dayWithEvent1.addEvent(LocalTime.now(), Day.EventType.Schedule,"test2");
        var dayWithEvent2 = addEventOfDay(LocalDate.of(2021,3,9), LocalTime.now(), Day.EventType.Notification,"testOtherDay");
        daysWithEvents.add(dayWithEvent1);
        daysWithEvents.add(dayWithEvent2);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        for (int i = 1981; i <= 2100; i++)
        {
            yearChoiceBox.getItems().add(i);
        }

        var currentYear = LocalDate.now().getYear();
        yearChoiceBox.setValue(currentYear);

        //Для будущей локализации
        //monthChoiceBox.getItems().addAll(List.of(Month.values()).stream().map(Enum::toString).map(month->(month.charAt(0) + month.substring(1).toLowerCase(Locale.ENGLISH))).collect(Collectors.toList()));

        //Для русского
        monthChoiceBox.getItems().addAll(List.of(Month.values()).stream().map(month->month.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"))).map(month->(month.substring(0,1).toUpperCase() + month.substring(1))).collect(Collectors.toList()));
        String currentMonth = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"));
        currentMonth = currentMonth.substring(0,1).toUpperCase() + currentMonth.substring(1);
        monthChoiceBox.setValue(currentMonth);

        LocalDate.now().getDayOfMonth();

        var imageNotification = new Image("Images/notification14.png");
        CalendarDay1NotificationIV.setImage(imageNotification);

        var imageTarget = new Image("Images/target14.png");
        CalendarDay1TargetIV.setImage(imageTarget);
        var imageSchedule = new Image("Images/schedule14.png");
        CalendarDay1ScheduleIV.setImage(imageSchedule);

        var imageNotificationActive = new Image("Images/notification14Active.png");
        CalendarDay2NotificationIV.setImage(imageNotificationActive);
        var imageTargetActive = new Image("Images/target14Active.png");
        CalendarDay2TargetIV.setImage(imageTargetActive);
        var imageScheduleActive = new Image("Images/schedule14Active.png");
        CalendarDay2ScheduleIV.setImage(imageScheduleActive);

        calendarDay1Button.setOnAction((event ->
        {
            var alert = new Alert(Alert.AlertType.WARNING, "Выбранное сохранение было удалено или переименовано. Загрузка прервана", ButtonType.OK);
            alert.showAndWait();
        }));

        calendarCloseButton.setOnAction(event ->
        {
            CalendarRoot.setVisible(false);
            var elementsOfSelectedTab = tabs.get(Integer.parseInt(CalendarRoot.getAccessibleText()));
            elementsOfSelectedTab.remove(CalendarRoot);
            CalendarRoot.setAccessibleText("-1");
        });

        calendarToolBar.setOnMouseDragged(event ->
        {
            NodeDragger.addDraggingProperty(CalendarRoot,event);
        });
    }

    public static void addCalendarToXML(Document doc, boolean createEmptyXML)
    {
        var rootElement = doc.getFirstChild();

        var calendarElement = doc.createElement("calendar");

        var layoutElement = doc.createElement("layout");
        calendarElement.appendChild(layoutElement);

        var layoutX = doc.createElement("layoutX");
        layoutElement.appendChild(layoutX);
        var layoutXValue = doc.createTextNode(String.valueOf((int)(CalendarRoot.getLayoutX())));
        layoutX.appendChild(layoutXValue);


        var layoutY = doc.createElement("layoutY" );
        layoutElement.appendChild(layoutY);
        var layoutYValue = doc.createTextNode(String.valueOf((int)(CalendarRoot.getLayoutY())));
        layoutY.appendChild(layoutYValue);

        //Если это первый запуск или создание нового пресета мы сразу устанавливаем значение атрибуту на -1
        if (createEmptyXML)
        {
            calendarElement.setAttribute("tab","-1");
        }
        else
        {
            calendarElement.setAttribute("tab",CalendarRoot.getAccessibleText());
        }
        rootElement.appendChild(calendarElement);

        //При первой загрузке и если пользователь сам не добавил кнопкой - календарь всё равно должен быть, поэтому visibility будет false
        var visibilityElement = doc.createElement("visibility");
        calendarElement.appendChild(visibilityElement);
        var visibilityValue = doc.createTextNode(String.valueOf(false));

        var daysWithEventsElement = doc.createElement("daysWithEvents");
        calendarElement.appendChild(daysWithEventsElement);

        if (!createEmptyXML && daysWithEvents!=null && daysWithEvents.size()!=0)
        {
            visibilityValue = doc.createTextNode(String.valueOf(CalendarRoot.isVisible()));

            int numberOfDay = 1;
            for (var day : daysWithEvents)
            {
                var dayElement = doc.createElement("day" + numberOfDay);
                daysWithEventsElement.appendChild(dayElement);

                var dateElement = doc.createElement("date" );
                dayElement.appendChild(dateElement);
                var dateElementValue = doc.createTextNode(day.getDate().toString());
                dateElement.appendChild(dateElementValue);

                var eventsElement = doc.createElement("events" );
                dayElement.appendChild(eventsElement);
                int numberOfEvent = 1;
                for (var event : day.getEvents())
                {
                    var eventElement = doc.createElement("event" + numberOfEvent);
                    eventsElement.appendChild(eventElement);

                    var timeOfEventElement = doc.createElement("time");
                    eventElement.appendChild(timeOfEventElement);
                    var timeOfEventElementValue = doc.createTextNode(event.getTime().toString());
                    timeOfEventElement.appendChild(timeOfEventElementValue);

                    var typeOfEventElement = doc.createElement("type");
                    eventElement.appendChild(typeOfEventElement);
                    var typeOfEventElementValue = doc.createTextNode(event.getType().toString());
                    typeOfEventElement.appendChild(typeOfEventElementValue);

                    var infoOfEventElement = doc.createElement("info");
                    eventElement.appendChild(infoOfEventElement);
                    var infoOfEventElementValue = doc.createTextNode(event.getInfo());
                    infoOfEventElement.appendChild(infoOfEventElementValue);

                    numberOfEvent++;
                }
                numberOfDay++;
            }
        }

        visibilityElement.appendChild(visibilityValue);
    }

    public static void loadCalendarFromXML(Document doc, XPath xPath) throws Exception
    {
        //Если это загрузка последнего сохранения, то у нас будет создан пустой календарь по умолчанию, а нам его нужно удалить.
        //В случае загрузки пресета или его новом создании, данная проверка не пройдёт, потому что мы перед загрузкой
        //чистим новые элементы, в которых как раз-таки и лежит наш пустой или не пустой календарь.
        if (CalendarRoot!=null)
        {
            Main.root.getChildren().remove(CalendarRoot);
            Calendar.clearDaysWithEvents();
        }

        var loadingCalendar = new Calendar(true);
        loadingCalendar.start(Main.Stage);
        var rootOfLoadingCalendar = getRoot();

        int numberOfTab = Integer.parseInt (xPath.evaluate("/save/calendar/@tab",doc));

        //Установить в созданный элемент дополнительный текст, в котором будет лежать значение того таба, на котором элемент был создан
        rootOfLoadingCalendar.setAccessibleText(String.valueOf(numberOfTab));

        //характеристика -1 может быть указана только в том случае, если календарь был закрыт или загружен в первый раз(календарь должен быть всегда)
        if (numberOfTab!=-1)
        {
            var tab = tabs.get(numberOfTab);
            tab.add(rootOfLoadingCalendar);
        }

        boolean visibility = Boolean.parseBoolean(xPath.evaluate("/save/calendar/visibility/text()",doc));
        rootOfLoadingCalendar.setVisible(visibility);

        double layoutX = Double.parseDouble (xPath.evaluate("/save/calendar/layout/layoutX/text()",doc));
        double layoutY = Double.parseDouble (xPath.evaluate("/save/calendar/layout/layoutY/text()",doc));
        rootOfLoadingCalendar.setLayoutX(layoutX);
        rootOfLoadingCalendar.setLayoutY(layoutY);

        int countOfDaysWithEvents = xPath.evaluateExpression("count(/save/calendar/daysWithEvents/*)",doc,Integer.class);

        for (int numberOfDay = 1; numberOfDay < countOfDaysWithEvents+1; numberOfDay++)
        {

            var date = LocalDate.parse(xPath.evaluate("/save/calendar/daysWithEvents/day"+numberOfDay+"/date/text()",doc));
            var day = new Day(date);

            //Делаем цикл и вытаскиваем ивенты, после чего добавляем их в переменную day методом addEvent
            int countOfEvents = xPath.evaluateExpression("count(/save/calendar/daysWithEvents/day"+numberOfDay+"/events/*)",doc,Integer.class);
            for (int numberOfEvent = 1; numberOfEvent < countOfEvents+1; numberOfEvent++)
            {
                var time = LocalTime.parse(xPath.evaluate("/save/calendar/daysWithEvents/day"+numberOfDay+"/events/event"+numberOfEvent +"/time/text()",doc));
                var type = Enum.valueOf(Day.EventType.class,xPath.evaluate("/save/calendar/daysWithEvents/day"+numberOfDay+"/events/event"+numberOfEvent +"/type/text()",doc));
                var info =xPath.evaluate("/save/calendar/daysWithEvents/day"+numberOfDay+"/events/event"+numberOfEvent +"/info/text()",doc);

                day.addEvent(time,type,info);
            }
            daysWithEvents.add(day);
        }
    }

    private void addIconsToLists()
    {
        notificationIcons.add(CalendarDay1NotificationIV);
    }

    public static void updateCalendarIcons()
    {
        notificationIcons.get(0).setImage(new Image("Images/notification14Active.png"));
    }
    @FXML private Button calendarDay1Button;
    @FXML private ImageView CalendarDay1NotificationIV;
    @FXML private ImageView CalendarDay1TargetIV;
    @FXML private ImageView CalendarDay1ScheduleIV;

    @FXML private Button calendarDay2Button;
    @FXML private ImageView CalendarDay2NotificationIV;
    @FXML private ImageView CalendarDay2TargetIV;
    @FXML private ImageView CalendarDay2ScheduleIV;

    @FXML private Button calendarDay3Button;
    @FXML private ImageView CalendarDay3NotificationIV;
    @FXML private ImageView CalendarDay3TargetIV;
    @FXML private ImageView CalendarDay3ScheduleIV;

    @FXML private Button calendarDay4Button;
    @FXML private ImageView CalendarDay4NotificationIV;
    @FXML private ImageView CalendarDay4TargetIV;
    @FXML private ImageView CalendarDay4ScheduleIV;

    @FXML private Button calendarDay5Button;
    @FXML private ImageView CalendarDay5NotificationIV;
    @FXML private ImageView CalendarDay5TargetIV;
    @FXML private ImageView CalendarDay5ScheduleIV;

    @FXML private Button calendarDay6Button;
    @FXML private ImageView CalendarDay6NotificationIV;
    @FXML private ImageView CalendarDay6TargetIV;
    @FXML private ImageView CalendarDay6ScheduleIV;

    @FXML private Button calendarDay7Button;
    @FXML private ImageView CalendarDay7NotificationIV;
    @FXML private ImageView CalendarDay7TargetIV;
    @FXML private ImageView CalendarDay7ScheduleIV;

    @FXML private Button calendarDay8Button;
    @FXML private ImageView CalendarDay8NotificationIV;
    @FXML private ImageView CalendarDay8TargetIV;
    @FXML private ImageView CalendarDay8ScheduleIV;

    @FXML private Button calendarDay9Button;
    @FXML private ImageView CalendarDay9NotificationIV;
    @FXML private ImageView CalendarDay9TargetIV;
    @FXML private ImageView CalendarDay9ScheduleIV;

    @FXML private Button calendarDay10Button;
    @FXML private ImageView CalendarDay10NotificationIV;
    @FXML private ImageView CalendarDay10TargetIV;
    @FXML private ImageView CalendarDay10ScheduleIV;

    @FXML private Button calendarDay11Button;
    @FXML private ImageView CalendarDay11NotificationIV;
    @FXML private ImageView CalendarDay11TargetIV;
    @FXML private ImageView CalendarDay11ScheduleIV;

    @FXML private Button calendarDay12Button;
    @FXML private ImageView CalendarDay12NotificationIV;
    @FXML private ImageView CalendarDay12TargetIV;
    @FXML private ImageView CalendarDay12ScheduleIV;

    @FXML private Button calendarDay13Button;
    @FXML private ImageView CalendarDay13NotificationIV;
    @FXML private ImageView CalendarDay13TargetIV;
    @FXML private ImageView CalendarDay13ScheduleIV;

    @FXML private Button calendarDay14Button;
    @FXML private ImageView CalendarDay14NotificationIV;
    @FXML private ImageView CalendarDay14TargetIV;
    @FXML private ImageView CalendarDay14ScheduleIV;

    @FXML private Button calendarDay15Button;
    @FXML private ImageView CalendarDay15NotificationIV;
    @FXML private ImageView CalendarDay15TargetIV;
    @FXML private ImageView CalendarDay15ScheduleIV;

    @FXML private Button calendarDay16Button;
    @FXML private ImageView CalendarDay16NotificationIV;
    @FXML private ImageView CalendarDay16TargetIV;
    @FXML private ImageView CalendarDay16ScheduleIV;

    @FXML private Button calendarDay17Button;
    @FXML private ImageView CalendarDay17NotificationIV;
    @FXML private ImageView CalendarDay17TargetIV;
    @FXML private ImageView CalendarDay17ScheduleIV;

    @FXML private Button calendarDay18Button;
    @FXML private ImageView CalendarDay18NotificationIV;
    @FXML private ImageView CalendarDay18TargetIV;
    @FXML private ImageView CalendarDay18ScheduleIV;

    @FXML private Button calendarDay19Button;
    @FXML private ImageView CalendarDay19NotificationIV;
    @FXML private ImageView CalendarDay19TargetIV;
    @FXML private ImageView CalendarDay19ScheduleIV;

    @FXML private Button calendarDay20Button;
    @FXML private ImageView CalendarDay20NotificationIV;
    @FXML private ImageView CalendarDay20TargetIV;
    @FXML private ImageView CalendarDay20ScheduleIV;

    @FXML private Button calendarDay21Button;
    @FXML private ImageView CalendarDay21NotificationIV;
    @FXML private ImageView CalendarDay21TargetIV;
    @FXML private ImageView CalendarDay21ScheduleIV;

    @FXML private Button calendarDay22Button;
    @FXML private ImageView CalendarDay22NotificationIV;
    @FXML private ImageView CalendarDay22TargetIV;
    @FXML private ImageView CalendarDay22ScheduleIV;

    @FXML private Button calendarDay23Button;
    @FXML private ImageView CalendarDay23NotificationIV;
    @FXML private ImageView CalendarDay23TargetIV;
    @FXML private ImageView CalendarDay23ScheduleIV;

    @FXML private Button calendarDay24Button;
    @FXML private ImageView CalendarDay24NotificationIV;
    @FXML private ImageView CalendarDay24TargetIV;
    @FXML private ImageView CalendarDay24ScheduleIV;

    @FXML private Button calendarDay25Button;
    @FXML private ImageView CalendarDay25NotificationIV;
    @FXML private ImageView CalendarDay25TargetIV;
    @FXML private ImageView CalendarDay25ScheduleIV;

    @FXML private Button calendarDay26Button;
    @FXML private ImageView CalendarDay26NotificationIV;
    @FXML private ImageView CalendarDay26TargetIV;
    @FXML private ImageView CalendarDay26ScheduleIV;

    @FXML private Button calendarDay27Button;
    @FXML private ImageView CalendarDay27NotificationIV;
    @FXML private ImageView CalendarDay27TargetIV;
    @FXML private ImageView CalendarDay27ScheduleIV;

    @FXML private Button calendarDay28Button;
    @FXML private ImageView CalendarDay28NotificationIV;
    @FXML private ImageView CalendarDay28TargetIV;
    @FXML private ImageView CalendarDay28ScheduleIV;

    @FXML private Button calendarDay29Button;
    @FXML private ImageView CalendarDay29NotificationIV;
    @FXML private ImageView CalendarDay29TargetIV;
    @FXML private ImageView CalendarDay29ScheduleIV;

    @FXML private Button calendarDay30Button;
    @FXML private ImageView CalendarDay30NotificationIV;
    @FXML private ImageView CalendarDay30TargetIV;
    @FXML private ImageView CalendarDay30ScheduleIV;

    @FXML private Button calendarDay31Button;
    @FXML private ImageView CalendarDay31NotificationIV;
    @FXML private ImageView CalendarDay31TargetIV;
    @FXML private ImageView CalendarDay31ScheduleIV;
}
