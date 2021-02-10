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
    private final static List<Day> daysWithEvents = new ArrayList<>();
    private final static List<ImageView> notificationIcons = new ArrayList<>();
    private final static List<ImageView> goalIcons = new ArrayList<>();
    private final static List<ImageView> scheduleIcons = new ArrayList<>();
    private final static int DAYS_IN_MONTH = Month.of(LocalDate.now().getMonth().getValue()).length(LocalDate.now().isLeapYear());

    public Calendar(){}

    private Calendar(boolean load) { this.load=load; }

    public static AnchorPane getRoot() { return CalendarRoot; }

    public static void clearLastInfo()
    {
        daysWithEvents.clear();
        notificationIcons.clear();
        goalIcons.clear();
        scheduleIcons.clear();
    }

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
           /* notificationIcons.clear();
            goalIcons.clear();
            scheduleIcons.clear();*/
        }
        /*var dayWithEvent1 = addEventOfDay(LocalDate.of(2021,2,9), LocalTime.now(), Day.EventType.Goal,"test");
        dayWithEvent1.addEvent(LocalTime.now(), Day.EventType.Schedule,"test2");
        var dayWithEvent2 = addEventOfDay(LocalDate.of(2021,3,9), LocalTime.now(), Day.EventType.Notification,"testOtherDay");
        daysWithEvents.add(dayWithEvent1);
        daysWithEvents.add(dayWithEvent2);*/
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        this.addIconsToLists();
        for (int i = 1981; i <= 2100; i++)
        {
            yearChoiceBox.getItems().add(i);
        }

        var currentYear = LocalDate.now().getYear();
        yearChoiceBox.setValue(currentYear);

        //Для будущей локализации(Для английского)
        //monthChoiceBox.getItems().addAll(List.of(Month.values()).stream().map(Enum::toString).map(month->(month.charAt(0) + month.substring(1).toLowerCase(Locale.ENGLISH))).collect(Collectors.toList()));

        //Для русского
        monthChoiceBox.getItems().addAll(List.of(Month.values()).stream().map(month->month.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"))).map(month->(month.substring(0,1).toUpperCase() + month.substring(1))).collect(Collectors.toList()));
        String currentMonth = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"));
        currentMonth = currentMonth.substring(0,1).toUpperCase() + currentMonth.substring(1);
        monthChoiceBox.setValue(currentMonth);

        monthChoiceBox.setOnAction(event ->
        {
            //Для русского
            loadEventsIconOfMonth(yearChoiceBox.getValue(),getNumberOfRussianMonth(monthChoiceBox.getValue()));
        });
        yearChoiceBox.setOnAction(event ->
        {
            //Для русского
            loadEventsIconOfMonth(yearChoiceBox.getValue(),getNumberOfRussianMonth(monthChoiceBox.getValue()));
        });
        loadEventsIconOfMonth(yearChoiceBox.getValue(),getNumberOfRussianMonth(monthChoiceBox.getValue()));

        calendarDay1Button.setOnAction(event ->
        {
            int numberOfDay = Integer.parseInt(calendarDay1Button.getText());
            if (daysWithEvents.size()!=0)
            {
               // LocalDate.of(yearChoiceBox.getValue(),monthChoiceBox.getValue())
            }
            else
            { }
        });

        /* var imageNotification = new Image("Images/notification14.png");
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
        CalendarDay2ScheduleIV.setImage(imageScheduleActive);*/

        /*calendarDay1Button.setOnAction((event ->
        {
            var alert = new Alert(Alert.AlertType.WARNING, "Выбранное сохранение было удалено или переименовано. Загрузка прервана", ButtonType.OK);
            alert.showAndWait();
        }));*/

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
        Text layoutXValue;

        var layoutY = doc.createElement("layoutY" );
        layoutElement.appendChild(layoutY);
        Text layoutYValue;

        //При первой загрузке и если пользователь сам не добавил кнопкой - календарь всё равно должен быть, поэтому visibility будет false
        var visibilityElement = doc.createElement("visibility");
        calendarElement.appendChild(visibilityElement);
        Text visibilityValue;

        //Если это первый запуск или создание нового пресета мы сразу устанавливаем значение атрибуту на -1
        if (createEmptyXML)
        {
            calendarElement.setAttribute("tab","-1");
            layoutXValue = doc.createTextNode("80");
            layoutYValue = doc.createTextNode("30");
            visibilityValue = doc.createTextNode(String.valueOf(false));

        }
        else
        {
            calendarElement.setAttribute("tab",CalendarRoot.getAccessibleText());
            layoutXValue = doc.createTextNode(String.valueOf((int)(CalendarRoot.getLayoutX())));
            layoutYValue = doc.createTextNode(String.valueOf((int)(CalendarRoot.getLayoutY())));
            visibilityValue = doc.createTextNode(String.valueOf(CalendarRoot.isVisible()));
        }
        rootElement.appendChild(calendarElement);
        layoutX.appendChild(layoutXValue);
        layoutY.appendChild(layoutYValue);
        visibilityElement.appendChild(visibilityValue);

        var daysWithEventsElement = doc.createElement("daysWithEvents");
        calendarElement.appendChild(daysWithEventsElement);

        if (!createEmptyXML && daysWithEvents!=null && daysWithEvents.size()!=0)
        {
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
    }

    public static void loadCalendarFromXML(Document doc, XPath xPath) throws Exception
    {
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
    }

    private void addIconsToLists()
    {
        notificationIcons.add(CalendarDay1NotificationIV);
        notificationIcons.add(CalendarDay2NotificationIV);
        notificationIcons.add(CalendarDay3NotificationIV);
        notificationIcons.add(CalendarDay4NotificationIV);
        notificationIcons.add(CalendarDay5NotificationIV);
        notificationIcons.add(CalendarDay6NotificationIV);
        notificationIcons.add(CalendarDay7NotificationIV);
        notificationIcons.add(CalendarDay8NotificationIV);
        notificationIcons.add(CalendarDay9NotificationIV);
        notificationIcons.add(CalendarDay10NotificationIV);
        notificationIcons.add(CalendarDay11NotificationIV);
        notificationIcons.add(CalendarDay12NotificationIV);
        notificationIcons.add(CalendarDay13NotificationIV);
        notificationIcons.add(CalendarDay14NotificationIV);
        notificationIcons.add(CalendarDay15NotificationIV);
        notificationIcons.add(CalendarDay16NotificationIV);
        notificationIcons.add(CalendarDay17NotificationIV);
        notificationIcons.add(CalendarDay18NotificationIV);
        notificationIcons.add(CalendarDay19NotificationIV);
        notificationIcons.add(CalendarDay20NotificationIV);
        notificationIcons.add(CalendarDay21NotificationIV);
        notificationIcons.add(CalendarDay22NotificationIV);
        notificationIcons.add(CalendarDay23NotificationIV);
        notificationIcons.add(CalendarDay24NotificationIV);
        notificationIcons.add(CalendarDay25NotificationIV);
        notificationIcons.add(CalendarDay26NotificationIV);
        notificationIcons.add(CalendarDay27NotificationIV);
        notificationIcons.add(CalendarDay28NotificationIV);
        notificationIcons.add(CalendarDay29NotificationIV);
        notificationIcons.add(CalendarDay30NotificationIV);
        notificationIcons.add(CalendarDay31NotificationIV);

        goalIcons.add(CalendarDay1GoalIV);
        goalIcons.add(CalendarDay2GoalIV);
        goalIcons.add(CalendarDay3GoalIV);
        goalIcons.add(CalendarDay4GoalIV);
        goalIcons.add(CalendarDay5GoalIV);
        goalIcons.add(CalendarDay6GoalIV);
        goalIcons.add(CalendarDay7GoalIV);
        goalIcons.add(CalendarDay8GoalIV);
        goalIcons.add(CalendarDay9GoalIV);
        goalIcons.add(CalendarDay10GoalIV);
        goalIcons.add(CalendarDay11GoalIV);
        goalIcons.add(CalendarDay12GoalIV);
        goalIcons.add(CalendarDay13GoalIV);
        goalIcons.add(CalendarDay14GoalIV);
        goalIcons.add(CalendarDay15GoalIV);
        goalIcons.add(CalendarDay16GoalIV);
        goalIcons.add(CalendarDay17GoalIV);
        goalIcons.add(CalendarDay18GoalIV);
        goalIcons.add(CalendarDay19GoalIV);
        goalIcons.add(CalendarDay20GoalIV);
        goalIcons.add(CalendarDay21GoalIV);
        goalIcons.add(CalendarDay22GoalIV);
        goalIcons.add(CalendarDay23GoalIV);
        goalIcons.add(CalendarDay24GoalIV);
        goalIcons.add(CalendarDay25GoalIV);
        goalIcons.add(CalendarDay26GoalIV);
        goalIcons.add(CalendarDay27GoalIV);
        goalIcons.add(CalendarDay28GoalIV);
        goalIcons.add(CalendarDay29GoalIV);
        goalIcons.add(CalendarDay30GoalIV);
        goalIcons.add(CalendarDay31GoalIV);

        scheduleIcons.add(CalendarDay1ScheduleIV);
        scheduleIcons.add(CalendarDay2ScheduleIV);
        scheduleIcons.add(CalendarDay3ScheduleIV);
        scheduleIcons.add(CalendarDay4ScheduleIV);
        scheduleIcons.add(CalendarDay5ScheduleIV);
        scheduleIcons.add(CalendarDay6ScheduleIV);
        scheduleIcons.add(CalendarDay7ScheduleIV);
        scheduleIcons.add(CalendarDay8ScheduleIV);
        scheduleIcons.add(CalendarDay9ScheduleIV);
        scheduleIcons.add(CalendarDay10ScheduleIV);
        scheduleIcons.add(CalendarDay11ScheduleIV);
        scheduleIcons.add(CalendarDay12ScheduleIV);
        scheduleIcons.add(CalendarDay13ScheduleIV);
        scheduleIcons.add(CalendarDay14ScheduleIV);
        scheduleIcons.add(CalendarDay15ScheduleIV);
        scheduleIcons.add(CalendarDay16ScheduleIV);
        scheduleIcons.add(CalendarDay17ScheduleIV);
        scheduleIcons.add(CalendarDay18ScheduleIV);
        scheduleIcons.add(CalendarDay19ScheduleIV);
        scheduleIcons.add(CalendarDay20ScheduleIV);
        scheduleIcons.add(CalendarDay21ScheduleIV);
        scheduleIcons.add(CalendarDay22ScheduleIV);
        scheduleIcons.add(CalendarDay23ScheduleIV);
        scheduleIcons.add(CalendarDay24ScheduleIV);
        scheduleIcons.add(CalendarDay25ScheduleIV);
        scheduleIcons.add(CalendarDay26ScheduleIV);
        scheduleIcons.add(CalendarDay27ScheduleIV);
        scheduleIcons.add(CalendarDay28ScheduleIV);
        scheduleIcons.add(CalendarDay29ScheduleIV);
        scheduleIcons.add(CalendarDay30ScheduleIV);
        scheduleIcons.add(CalendarDay31ScheduleIV);
    }

    private static void loadEventsIconOfMonth(int year, int month)
    {
        for (int i =1; i<=31; i++)
        {
            updateDayIcons(i,false,false,false);
        }
        for (var day : daysWithEvents)
        {
            var dateOfDayWithEvent = LocalDate.of(day.getDate().getYear(),day.getDate().getMonth(),day.getDate().getDayOfMonth());
            for (int i = 1; i<=DAYS_IN_MONTH; i++)
            {
                var date = LocalDate.of(year,month,i);
                if (dateOfDayWithEvent.equals(date))
                {
                   updateDayIcons(i, day.isHaveNotification(), day.isHaveGoal(), day.isHaveSchedule());
                   break;
                }
            }
        }
    }

    public static void updateDayIcons(int day, boolean notification, boolean goal, boolean schedule)
    {
        day--;
        if (notification) {notificationIcons.get(day).setImage(new Image("Images/notification14Active.png"));}
        else {notificationIcons.get(day).setImage(null); }

        if (goal) {goalIcons.get(day).setImage(new Image("Images/goal14Active.png"));}
        else {goalIcons.get(day).setImage(null); }

        if (schedule) {scheduleIcons.get(day).setImage(new Image("Images/schedule14Active.png"));}
        else {scheduleIcons.get(day).setImage(null); }
    }

    private static int getNumberOfRussianMonth(String month)
    {
        if (month.equals("Январь")) {return 1;}
        if (month.equals("Февраль")) {return 2;}
        if (month.equals("Март")) {return 3;}
        if (month.equals("Апрель")) {return 4;}
        if (month.equals("Май")) {return 5;}
        if (month.equals("Июнь")) {return 6;}
        if (month.equals("Июль")) {return 7;}
        if (month.equals("Август")) {return 8;}
        if (month.equals("Сентябрь")) {return 9;}
        if (month.equals("Октябрь")) {return 10;}
        if (month.equals("Ноябрь")) {return 11;}
        return 12;
    }

    @FXML private Button calendarDay1Button;
    @FXML private ImageView CalendarDay1NotificationIV;
    @FXML private ImageView CalendarDay1GoalIV;
    @FXML private ImageView CalendarDay1ScheduleIV;

    @FXML private Button calendarDay2Button;
    @FXML private ImageView CalendarDay2NotificationIV;
    @FXML private ImageView CalendarDay2GoalIV;
    @FXML private ImageView CalendarDay2ScheduleIV;

    @FXML private Button calendarDay3Button;
    @FXML private ImageView CalendarDay3NotificationIV;
    @FXML private ImageView CalendarDay3GoalIV;
    @FXML private ImageView CalendarDay3ScheduleIV;

    @FXML private Button calendarDay4Button;
    @FXML private ImageView CalendarDay4NotificationIV;
    @FXML private ImageView CalendarDay4GoalIV;
    @FXML private ImageView CalendarDay4ScheduleIV;

    @FXML private Button calendarDay5Button;
    @FXML private ImageView CalendarDay5NotificationIV;
    @FXML private ImageView CalendarDay5GoalIV;
    @FXML private ImageView CalendarDay5ScheduleIV;

    @FXML private Button calendarDay6Button;
    @FXML private ImageView CalendarDay6NotificationIV;
    @FXML private ImageView CalendarDay6GoalIV;
    @FXML private ImageView CalendarDay6ScheduleIV;

    @FXML private Button calendarDay7Button;
    @FXML private ImageView CalendarDay7NotificationIV;
    @FXML private ImageView CalendarDay7GoalIV;
    @FXML private ImageView CalendarDay7ScheduleIV;

    @FXML private Button calendarDay8Button;
    @FXML private ImageView CalendarDay8NotificationIV;
    @FXML private ImageView CalendarDay8GoalIV;
    @FXML private ImageView CalendarDay8ScheduleIV;

    @FXML private Button calendarDay9Button;
    @FXML private ImageView CalendarDay9NotificationIV;
    @FXML private ImageView CalendarDay9GoalIV;
    @FXML private ImageView CalendarDay9ScheduleIV;

    @FXML private Button calendarDay10Button;
    @FXML private ImageView CalendarDay10NotificationIV;
    @FXML private ImageView CalendarDay10GoalIV;
    @FXML private ImageView CalendarDay10ScheduleIV;

    @FXML private Button calendarDay11Button;
    @FXML private ImageView CalendarDay11NotificationIV;
    @FXML private ImageView CalendarDay11GoalIV;
    @FXML private ImageView CalendarDay11ScheduleIV;

    @FXML private Button calendarDay12Button;
    @FXML private ImageView CalendarDay12NotificationIV;
    @FXML private ImageView CalendarDay12GoalIV;
    @FXML private ImageView CalendarDay12ScheduleIV;

    @FXML private Button calendarDay13Button;
    @FXML private ImageView CalendarDay13NotificationIV;
    @FXML private ImageView CalendarDay13GoalIV;
    @FXML private ImageView CalendarDay13ScheduleIV;

    @FXML private Button calendarDay14Button;
    @FXML private ImageView CalendarDay14NotificationIV;
    @FXML private ImageView CalendarDay14GoalIV;
    @FXML private ImageView CalendarDay14ScheduleIV;

    @FXML private Button calendarDay15Button;
    @FXML private ImageView CalendarDay15NotificationIV;
    @FXML private ImageView CalendarDay15GoalIV;
    @FXML private ImageView CalendarDay15ScheduleIV;

    @FXML private Button calendarDay16Button;
    @FXML private ImageView CalendarDay16NotificationIV;
    @FXML private ImageView CalendarDay16GoalIV;
    @FXML private ImageView CalendarDay16ScheduleIV;

    @FXML private Button calendarDay17Button;
    @FXML private ImageView CalendarDay17NotificationIV;
    @FXML private ImageView CalendarDay17GoalIV;
    @FXML private ImageView CalendarDay17ScheduleIV;

    @FXML private Button calendarDay18Button;
    @FXML private ImageView CalendarDay18NotificationIV;
    @FXML private ImageView CalendarDay18GoalIV;
    @FXML private ImageView CalendarDay18ScheduleIV;

    @FXML private Button calendarDay19Button;
    @FXML private ImageView CalendarDay19NotificationIV;
    @FXML private ImageView CalendarDay19GoalIV;
    @FXML private ImageView CalendarDay19ScheduleIV;

    @FXML private Button calendarDay20Button;
    @FXML private ImageView CalendarDay20NotificationIV;
    @FXML private ImageView CalendarDay20GoalIV;
    @FXML private ImageView CalendarDay20ScheduleIV;

    @FXML private Button calendarDay21Button;
    @FXML private ImageView CalendarDay21NotificationIV;
    @FXML private ImageView CalendarDay21GoalIV;
    @FXML private ImageView CalendarDay21ScheduleIV;

    @FXML private Button calendarDay22Button;
    @FXML private ImageView CalendarDay22NotificationIV;
    @FXML private ImageView CalendarDay22GoalIV;
    @FXML private ImageView CalendarDay22ScheduleIV;

    @FXML private Button calendarDay23Button;
    @FXML private ImageView CalendarDay23NotificationIV;
    @FXML private ImageView CalendarDay23GoalIV;
    @FXML private ImageView CalendarDay23ScheduleIV;

    @FXML private Button calendarDay24Button;
    @FXML private ImageView CalendarDay24NotificationIV;
    @FXML private ImageView CalendarDay24GoalIV;
    @FXML private ImageView CalendarDay24ScheduleIV;

    @FXML private Button calendarDay25Button;
    @FXML private ImageView CalendarDay25NotificationIV;
    @FXML private ImageView CalendarDay25GoalIV;
    @FXML private ImageView CalendarDay25ScheduleIV;

    @FXML private Button calendarDay26Button;
    @FXML private ImageView CalendarDay26NotificationIV;
    @FXML private ImageView CalendarDay26GoalIV;
    @FXML private ImageView CalendarDay26ScheduleIV;

    @FXML private Button calendarDay27Button;
    @FXML private ImageView CalendarDay27NotificationIV;
    @FXML private ImageView CalendarDay27GoalIV;
    @FXML private ImageView CalendarDay27ScheduleIV;

    @FXML private Button calendarDay28Button;
    @FXML private ImageView CalendarDay28NotificationIV;
    @FXML private ImageView CalendarDay28GoalIV;
    @FXML private ImageView CalendarDay28ScheduleIV;

    @FXML private Button calendarDay29Button;
    @FXML private ImageView CalendarDay29NotificationIV;
    @FXML private ImageView CalendarDay29GoalIV;
    @FXML private ImageView CalendarDay29ScheduleIV;

    @FXML private Button calendarDay30Button;
    @FXML private ImageView CalendarDay30NotificationIV;
    @FXML private ImageView CalendarDay30GoalIV;
    @FXML private ImageView CalendarDay30ScheduleIV;

    @FXML private Button calendarDay31Button;
    @FXML private ImageView CalendarDay31NotificationIV;
    @FXML private ImageView CalendarDay31GoalIV;
    @FXML private ImageView CalendarDay31ScheduleIV;
}
