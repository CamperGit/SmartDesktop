package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.xpath.XPath;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

import static com.camper.SmartDesktop.Main.*;

public class CalendarSD extends Application implements Initializable
{
    @FXML
    private ToolBar calendarToolBar;
    @FXML
    private Button calendarCloseButton;
    @FXML
    private ChoiceBox<String> monthChoiceBox;
    @FXML
    private ComboBox<Integer> yearComboBox;
    @FXML
    private Label calendarMondayLabel, calendarTuesdayLabel, calendarWednesdayLabel, calendarThursdayLabel, calendarFridayLabel, calendarSundayLabel, calendarSaturdayLabel;
    @FXML
    private ImageView calendarCloseButtonIV;

    private boolean load = false;
    private static AnchorPane CalendarRoot;
    private final static List<Day> daysWithEvents = new ArrayList<>();
    private final static List<ImageView> notificationIcons = new ArrayList<>();
    private final static List<ImageView> goalIcons = new ArrayList<>();
    private final static List<ImageView> scheduleIcons = new ArrayList<>();
    public static int currentDay;
    private static int selectedMonth;
    private static int selectedYear;

    public CalendarSD()
    {
    }

    private CalendarSD(boolean load)
    {
        this.load = load;
    }

    public static AnchorPane getRoot()
    {
        return CalendarRoot;
    }

    public static void clearLastInfo()
    {
        daysWithEvents.clear();
        notificationIcons.clear();
        goalIcons.clear();
        scheduleIcons.clear();
    }

    public static ArrayList<Day> getDaysWithEvents()
    {
        return (ArrayList<Day>) daysWithEvents;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        logger.info("CalendarSD: begin start method");
        CalendarRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/calendar.fxml")));
        CalendarRoot.setLayoutX(80);
        CalendarRoot.setLayoutY(30);
        updateCalendarTodayLabel();
        addChild(CalendarRoot);
        if (!load)
        {
            //???? ??? ?? ????????
            CalendarRoot.setAccessibleText("-1");
            CalendarRoot.setVisible(false);
        }
        logger.info("CalendarSD: end start method");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        logger.info("CalendarSD: begin initialize method");
        calendarMondayLabel.setText(languageBundle.getString("calendarMondayLabel"));
        calendarTuesdayLabel.setText(languageBundle.getString("calendarTuesdayLabel"));
        calendarWednesdayLabel.setText(languageBundle.getString("calendarWednesdayLabel"));
        calendarThursdayLabel.setText(languageBundle.getString("calendarThursdayLabel"));
        calendarFridayLabel.setText(languageBundle.getString("calendarFridayLabel"));
        calendarSaturdayLabel.setText(languageBundle.getString("calendarSaturdayLabel"));
        calendarSundayLabel.setText(languageBundle.getString("calendarSundayLabel"));
        calendarCloseButtonIV.setImage(new Image("Images/delete30.png"));

        this.addIconsToLists();
        for (int i = 1981; i <= 2100; i++)
        {
            yearComboBox.getItems().add(i);
        }
        yearComboBox.setVisibleRowCount(8);

        int currentYear = LocalDate.now().getYear();
        selectedYear = currentYear;
        yearComboBox.setValue(currentYear);



        String currentMonth;
        int dayInMonth;
        if (!defaultLocale.equals(Locale.ENGLISH))
        {
            monthChoiceBox.getItems().addAll(new ArrayList<>(Arrays.asList(Month.values())).stream().map(month -> month.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"))).map(month -> (month.substring(0, 1).toUpperCase() + month.substring(1))).collect(Collectors.toList()));
            currentMonth = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"));
            currentMonth = currentMonth.substring(0, 1).toUpperCase() + currentMonth.substring(1);
            selectedMonth = getNumberOfRussianMonth(currentMonth);
            monthChoiceBox.setValue(currentMonth);
            loadEventsIconOfMonth(yearComboBox.getValue(), getNumberOfRussianMonth(monthChoiceBox.getValue()));
            dayInMonth = Month.of(getNumberOfRussianMonth(monthChoiceBox.getValue())).length(Year.isLeap(yearComboBox.getValue()));
        }
        else
        {
            monthChoiceBox.getItems().addAll(new ArrayList<>(Arrays.asList(Month.values())).stream().map(Enum::toString).map(month->(month.charAt(0) + month.substring(1).toLowerCase(Locale.ENGLISH))).collect(Collectors.toList()));
            currentMonth = LocalDate.now().getMonth().toString().toLowerCase();
            currentMonth = currentMonth.substring(0, 1).toUpperCase() + currentMonth.substring(1);
            selectedMonth = LocalDate.now().getMonth().getValue();
            monthChoiceBox.setValue(currentMonth);
            loadEventsIconOfMonth(yearComboBox.getValue(), (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue());
            dayInMonth = Month.of((Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue()).length(Year.isLeap(yearComboBox.getValue()));
        }
        if (!(dayInMonth >= 29))
        {
            calendarDay29Button.setDisable(true);
            calendarDay29Button.setVisible(false);
            day29UpperSeparator.setVisible(false);
            day29RightSeparator.setVisible(false);

        }

        if (!(dayInMonth >= 30))
        {
            calendarDay30Button.setDisable(true);
            calendarDay30Button.setVisible(false);
            day30UpperSeparator.setVisible(false);
            day30RightSeparator.setVisible(false);
        }
        if (!(dayInMonth >= 31))
        {
            calendarDay31Button.setDisable(true);
            calendarDay31Button.setVisible(false);
            day31UpperSeparator.setVisible(false);
            day31RightSeparator.setVisible(false);
        }

        monthChoiceBox.setOnAction(event ->
        {
            selectedMonth = defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue());
            deletingUnnecessaryDaysInCalendar();
        });
        yearComboBox.setOnAction(event ->
        {
            //??? ????????
            selectedYear = yearComboBox.getValue();
            deletingUnnecessaryDaysInCalendar();
        });

        calendarDay1Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 1, event));
        calendarDay2Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 2, event));
        calendarDay3Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 3, event));
        calendarDay4Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 4, event));
        calendarDay5Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 5, event));
        calendarDay6Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 6, event));
        calendarDay7Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 7, event));
        calendarDay8Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 8, event));
        calendarDay9Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 9, event));
        calendarDay10Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 10, event));
        calendarDay11Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 11, event));
        calendarDay12Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 12, event));
        calendarDay13Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 13, event));
        calendarDay14Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 14, event));
        calendarDay15Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 15, event));
        calendarDay16Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 16, event));
        calendarDay17Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 17, event));
        calendarDay18Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 18, event));
        calendarDay19Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 19, event));
        calendarDay20Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 20, event));
        calendarDay21Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 21, event));
        calendarDay22Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 22, event));
        calendarDay23Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 23, event));
        calendarDay24Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 24, event));
        calendarDay25Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 25, event));
        calendarDay26Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 26, event));
        calendarDay27Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 27, event));
        calendarDay28Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 28, event));
        calendarDay29Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 29, event));
        calendarDay30Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 30, event));
        calendarDay31Button.setOnMouseClicked(event -> checkEventsOfThisButton(yearComboBox.getValue(), defaultLocale.equals(Locale.ENGLISH) ? (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue() : getNumberOfRussianMonth(monthChoiceBox.getValue()), 31, event));

        calendarCloseButton.setOnAction(event ->
        {
            CalendarRoot.setVisible(false);
            List<Node> elementsOfSelectedTab = tabs.get(Integer.parseInt(CalendarRoot.getAccessibleText()));
            elementsOfSelectedTab.remove(CalendarRoot);
            CalendarRoot.setAccessibleText("-1");
            logger.info("CalendarSD: close calendar");
        });

        calendarToolBar.setOnMouseDragged(event -> NodeDragger.addDraggingProperty(CalendarRoot, event));

        logger.info("CalendarSD: end initialize method");
    }

    public static void addCalendarToXML(Document doc, boolean createEmptyXML)
    {
        logger.info("CalendarSD: start calendar saving");
        org.w3c.dom.Node rootElement = doc.getFirstChild();

        Element calendarElement = doc.createElement("calendar");

        Element layoutElement = doc.createElement("layout");
        calendarElement.appendChild(layoutElement);

        Element layoutX = doc.createElement("layoutX");
        layoutElement.appendChild(layoutX);
        Text layoutXValue;

        Element layoutY = doc.createElement("layoutY");
        layoutElement.appendChild(layoutY);
        Text layoutYValue;

        //??? ?????? ???????? ? ???? ???????????? ??? ?? ??????? ??????? - ????????? ?? ????? ?????? ????, ??????? visibility ????? false
        Element visibilityElement = doc.createElement("visibility");
        calendarElement.appendChild(visibilityElement);
        Text visibilityValue;

        //???? ??? ?????? ?????? ??? ???????? ?????? ??????? ?? ????? ????????????? ???????? ???????? ?? -1
        if (createEmptyXML)
        {
            calendarElement.setAttribute("tab", "-1");
            layoutXValue = doc.createTextNode("80");
            layoutYValue = doc.createTextNode("30");
            visibilityValue = doc.createTextNode(String.valueOf(false));

        } else
        {
            calendarElement.setAttribute("tab", CalendarRoot.getAccessibleText());
            layoutXValue = doc.createTextNode(String.valueOf((int) (CalendarRoot.getLayoutX())));
            layoutYValue = doc.createTextNode(String.valueOf((int) (CalendarRoot.getLayoutY())));
            visibilityValue = doc.createTextNode(String.valueOf(CalendarRoot.isVisible()));
        }
        rootElement.appendChild(calendarElement);
        layoutX.appendChild(layoutXValue);
        layoutY.appendChild(layoutYValue);
        visibilityElement.appendChild(visibilityValue);

        Element daysWithEventsElement = doc.createElement("daysWithEvents");
        calendarElement.appendChild(daysWithEventsElement);

        if (!createEmptyXML && daysWithEvents != null && daysWithEvents.size() != 0)
        {
            int numberOfDay = 1;
            for (Day day : daysWithEvents)
            {
                if (!(day.isHaveSchedule() && !day.isHaveGoal() && !day.isHaveNotification()))
                {
                    Element dayElement = doc.createElement("day" + numberOfDay);
                    daysWithEventsElement.appendChild(dayElement);

                    Element dateElement = doc.createElement("date");
                    dayElement.appendChild(dateElement);
                    Text dateElementValue = doc.createTextNode(day.getDate().toString());
                    dateElement.appendChild(dateElementValue);

                    Element eventsElement = doc.createElement("events");
                    dayElement.appendChild(eventsElement);
                    int numberOfEvent = 1;
                    for (EventOfDay event : day.getEvents())
                    {
                        if (event.getType().equals(Day.EventType.Schedule))
                        {
                            continue;
                        }
                        Element eventElement = doc.createElement("event" + numberOfEvent);
                        eventsElement.appendChild(eventElement);

                        Element timeOfEventElement = doc.createElement("time");
                        eventElement.appendChild(timeOfEventElement);
                        Text timeOfEventElementValue = doc.createTextNode(event.getTime().toString());
                        timeOfEventElement.appendChild(timeOfEventElementValue);

                        Element typeOfEventElement = doc.createElement("type");
                        eventElement.appendChild(typeOfEventElement);
                        Text typeOfEventElementValue = doc.createTextNode(event.getType().toString());
                        typeOfEventElement.appendChild(typeOfEventElementValue);

                        Element infoOfEventElement = doc.createElement("info");
                        eventElement.appendChild(infoOfEventElement);
                        Text infoOfEventElementValue = doc.createTextNode(event.getInfo());
                        infoOfEventElement.appendChild(infoOfEventElementValue);

                        numberOfEvent++;
                    }
                    numberOfDay++;
                }
            }
        }
        logger.info("CalendarSD: end calendar saving");
    }

    public static void loadCalendarFromXML(Document doc, XPath xPath) throws Exception
    {
        logger.info("CalendarSD: start calendar loading");
        int countOfDaysWithEvents = Integer.parseInt(xPath.evaluate("count(/save/calendar/daysWithEvents/*)", doc));
        for (int numberOfDay = 1; numberOfDay < countOfDaysWithEvents + 1; numberOfDay++)
        {

            LocalDate date = LocalDate.parse(xPath.evaluate("/save/calendar/daysWithEvents/day" + numberOfDay + "/date/text()", doc));
            Day day = checkUsingOfThisDateOnEventList(date);
            if (day == null)
            {
                day = new Day(date);
            }

            //?????? ???? ? ??????????? ??????, ????? ???? ????????? ?? ? ?????????? day ??????? addEvent
            int countOfEvents = Integer.parseInt(xPath.evaluate("count(/save/calendar/daysWithEvents/day" + numberOfDay + "/events/*)", doc));
            for (int numberOfEvent = 1; numberOfEvent < countOfEvents + 1; numberOfEvent++)
            {
                LocalTime time = LocalTime.parse(xPath.evaluate("/save/calendar/daysWithEvents/day" + numberOfDay + "/events/event" + numberOfEvent + "/time/text()", doc));
                Day.EventType type = Enum.valueOf(Day.EventType.class, xPath.evaluate("/save/calendar/daysWithEvents/day" + numberOfDay + "/events/event" + numberOfEvent + "/type/text()", doc));
                String info = xPath.evaluate("/save/calendar/daysWithEvents/day" + numberOfDay + "/events/event" + numberOfEvent + "/info/text()", doc);

                EventOfDay event = new EventOfDay(time, type, info);
                if (type.equals(Day.EventType.Task))
                {
                    GoalSD.addTaskOnTaskMap(event);
                }
                day.addEvent(event);
            }

            boolean haveEvents = Day.checkOfDeprecatedEvents(day, true);
            if (haveEvents && !daysWithEvents.contains(day))
            {
                daysWithEvents.add(day);
            }
        }
        UpcomingEvent.disableEventQueue(false);
        UpcomingEvent.loadEventsToQueue(daysWithEvents);

        CalendarSD loadingCalendar = new CalendarSD(true);
        loadingCalendar.start(Main.Stage);
        AnchorPane rootOfLoadingCalendar = getRoot();

        int numberOfTab = Integer.parseInt(xPath.evaluate("/save/calendar/@tab", doc));

        //?????????? ? ????????? ??????? ?????????????? ?????, ? ??????? ????? ?????? ???????? ???? ????, ?? ??????? ??????? ??? ??????
        rootOfLoadingCalendar.setAccessibleText(String.valueOf(numberOfTab));

        //?????????????? -1 ????? ???? ??????? ?????? ? ??? ??????, ???? ????????? ??? ?????? ??? ???????? ? ?????? ???(????????? ?????? ???? ??????)
        if (numberOfTab != -1)
        {
            List<Node> tab = tabs.get(numberOfTab);
            tab.add(rootOfLoadingCalendar);
        }

        boolean visibility = Boolean.parseBoolean(xPath.evaluate("/save/calendar/visibility/text()", doc));
        rootOfLoadingCalendar.setVisible(visibility);

        double layoutX = Double.parseDouble(xPath.evaluate("/save/calendar/layout/layoutX/text()", doc));
        double layoutY = Double.parseDouble(xPath.evaluate("/save/calendar/layout/layoutY/text()", doc));
        rootOfLoadingCalendar.setLayoutX(layoutX);
        rootOfLoadingCalendar.setLayoutY(layoutY);
        logger.info("CalendarSD: end calendar loading");
    }

    private void deletingUnnecessaryDaysInCalendar()
    {
        int dayInMonth;
        int numberOfMonth;
        if (defaultLocale.equals(Locale.ENGLISH))
        {
            numberOfMonth = (Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue();
            dayInMonth = Month.of((Month.valueOf(monthChoiceBox.getValue().toUpperCase())).getValue()).length(Year.isLeap(yearComboBox.getValue()));
        }
        else
        {
            numberOfMonth = getNumberOfRussianMonth(monthChoiceBox.getValue());
            dayInMonth = Month.of(getNumberOfRussianMonth(monthChoiceBox.getValue())).length(Year.isLeap(yearComboBox.getValue()));
        }
        loadEventsIconOfMonth(yearComboBox.getValue(), numberOfMonth);
        if (dayInMonth >= 29)
        {
            calendarDay29Button.setDisable(false);
            calendarDay29Button.setVisible(true);
            day29UpperSeparator.setVisible(true);
            day29RightSeparator.setVisible(true);
        } else
        {
            calendarDay29Button.setDisable(true);
            calendarDay29Button.setVisible(false);
            day29UpperSeparator.setVisible(false);
            day29RightSeparator.setVisible(false);
        }
        if (dayInMonth >= 30)
        {
            calendarDay30Button.setDisable(false);
            calendarDay30Button.setVisible(true);
            day30UpperSeparator.setVisible(true);
            day30RightSeparator.setVisible(true);
        } else
        {
            calendarDay30Button.setDisable(true);
            calendarDay30Button.setVisible(false);
            day30UpperSeparator.setVisible(false);
            day30RightSeparator.setVisible(false);
        }
        if (dayInMonth >= 31)
        {
            calendarDay31Button.setDisable(false);
            calendarDay31Button.setVisible(true);
            day31UpperSeparator.setVisible(true);
            day31RightSeparator.setVisible(true);
        } else
        {
            calendarDay31Button.setDisable(true);
            calendarDay31Button.setVisible(false);
            day31UpperSeparator.setVisible(false);
            day31RightSeparator.setVisible(false);
        }
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

    /**
     * @param date ???? ??? ???????? ????????????? ????? ???? ? ?????? ???? ? ?????????
     * @return null, ???? ????? ???? ??? ?? ??????????????, day - ???? ???? ???? ? ????? ????? ? ?????? daysWithEvents
     */
    public static Day checkUsingOfThisDateOnEventList(LocalDate date)
    {
        for (Day dayWithEvent : daysWithEvents)
        {
            if (dayWithEvent.getDate().equals(date))
            {
                return dayWithEvent;
            }
        }
        return null;
    }

    public static void updateCalendarTodayLabel()
    {
        for (Node node : CalendarRoot.getChildren())
        {
            if (node instanceof Label && node.getAccessibleHelp() != null && node.getAccessibleHelp().equals("calendarTodayLabel"))
            {
                LocalDate now = LocalDate.now();
                int month = now.getMonth().getValue();
                String monthName = null;
                if (!defaultLocale.equals(Locale.ENGLISH))
                {
                    switch (month)
                    {
                        case 1:
                            monthName = "??????";
                            break;
                        case 2:
                            monthName = "???????";
                            break;
                        case 3:
                            monthName = "?????";
                            break;
                        case 4:
                            monthName = "??????";
                            break;
                        case 5:
                            monthName = "???";
                            break;
                        case 6:
                            monthName = "????";
                            break;
                        case 7:
                            monthName = "????";
                            break;
                        case 8:
                            monthName = "???????";
                            break;
                        case 9:
                            monthName = "????????";
                            break;
                        case 10:
                            monthName = "???????";
                            break;
                        case 11:
                            monthName = "??????";
                            break;
                        case 12:
                            monthName = "???????";
                            break;
                    }
                    ((Label) node).setText(now.getDayOfMonth() + " " + monthName + " " + now.getYear());
                }
                else
                {
                    monthName = LocalDate.now().getMonth().toString();
                    monthName = monthName.charAt(0) + monthName.substring(1).toLowerCase(Locale.ENGLISH);
                    ((Label) node).setText(monthName + " " + now.getDayOfMonth() + ", " + now.getYear());
                }
                currentDay = now.getDayOfMonth();
            }
        }
    }

    private static void loadEventsIconOfMonth(int year, int month)
    {
        for (int i = 1; i <= 31; i++)
        {
            updateDayIcons(i, false, false, false);
        }
        for (Day day : daysWithEvents)
        {
            LocalDate dateOfDayWithEvent = day.getDate();
            for (int i = 1; i <= Month.of(month).length(Year.isLeap(year)); i++)
            {
                LocalDate date = LocalDate.of(year, month, i);
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
        if (notification)
        {
            notificationIcons.get(day).setImage(new Image("Images/notification14Active.png"));
        } else
        {
            notificationIcons.get(day).setImage(null);
        }

        if (goal)
        {
            goalIcons.get(day).setImage(new Image("Images/goal14Active.png"));
        } else
        {
            goalIcons.get(day).setImage(null);
        }

        if (schedule)
        {
            scheduleIcons.get(day).setImage(new Image("Images/schedule14Active.png"));
        } else
        {
            scheduleIcons.get(day).setImage(null);
        }
    }

    public static void updateDayIcons(LocalDate date, boolean notification, boolean goal, boolean schedule)
    {
        if (date.getYear() == selectedYear && date.getMonth().getValue() == selectedMonth)
        {
            int day = date.getDayOfMonth();
            day--;
            if (notification)
            {
                notificationIcons.get(day).setImage(new Image("Images/notification14Active.png"));
            } else
            {
                notificationIcons.get(day).setImage(null);
            }

            if (goal)
            {
                goalIcons.get(day).setImage(new Image("Images/goal14Active.png"));
            } else
            {
                goalIcons.get(day).setImage(null);
            }

            if (schedule)
            {
                scheduleIcons.get(day).setImage(new Image("Images/schedule14Active.png"));
            } else
            {
                scheduleIcons.get(day).setImage(null);
            }
        }
    }

    private static int getNumberOfRussianMonth(String month)
    {
        if (month.equals("??????"))
        {
            return 1;
        }
        if (month.equals("???????"))
        {
            return 2;
        }
        if (month.equals("????"))
        {
            return 3;
        }
        if (month.equals("??????"))
        {
            return 4;
        }
        if (month.equals("???"))
        {
            return 5;
        }
        if (month.equals("????"))
        {
            return 6;
        }
        if (month.equals("????"))
        {
            return 7;
        }
        if (month.equals("??????"))
        {
            return 8;
        }
        if (month.equals("????????"))
        {
            return 9;
        }
        if (month.equals("???????"))
        {
            return 10;
        }
        if (month.equals("??????"))
        {
            return 11;
        }
        return 12;
    }

    public static void checkEventsOfThisButton(int year, int month, int numberOfDay, MouseEvent event)
    {
        LocalDate dayOfThisButton = LocalDate.of(year, month, numberOfDay);
        if (daysWithEvents.size() != 0)
        {
            boolean haveEvents = false;
            for (Day day : daysWithEvents)
            {
                LocalDate dateOfDayWithEvent = LocalDate.of(day.getDate().getYear(), day.getDate().getMonth(), day.getDate().getDayOfMonth());
                if (dayOfThisButton.equals(dateOfDayWithEvent))
                {
                    try
                    {
                        new EventsOfDayInfo(event, day).start(Stage);
                    } catch (Exception e)
                    {
                        logger.error("CalendarSD: events of day info FXML load error", e);
                    }
                    haveEvents = true;
                    break;
                }
            }
            if (!haveEvents)
            {
                try
                {
                    new EventsOfDayInfo(event, new Day(dayOfThisButton)).start(Stage);
                } catch (Exception e)
                {
                    logger.error("CalendarSD: events of day info FXML load error", e);
                }
            }
        } else
        {
            try
            {
                new EventsOfDayInfo(event, new Day(dayOfThisButton)).start(Stage);
            } catch (Exception e)
            {
                logger.error("CalendarSD: events of day info FXML load error", e);
            }
        }
    }

    @FXML
    private Button calendarDay1Button;
    @FXML
    private ImageView CalendarDay1NotificationIV;
    @FXML
    private ImageView CalendarDay1GoalIV;
    @FXML
    private ImageView CalendarDay1ScheduleIV;

    @FXML
    private Button calendarDay2Button;
    @FXML
    private ImageView CalendarDay2NotificationIV;
    @FXML
    private ImageView CalendarDay2GoalIV;
    @FXML
    private ImageView CalendarDay2ScheduleIV;

    @FXML
    private Button calendarDay3Button;
    @FXML
    private ImageView CalendarDay3NotificationIV;
    @FXML
    private ImageView CalendarDay3GoalIV;
    @FXML
    private ImageView CalendarDay3ScheduleIV;

    @FXML
    private Button calendarDay4Button;
    @FXML
    private ImageView CalendarDay4NotificationIV;
    @FXML
    private ImageView CalendarDay4GoalIV;
    @FXML
    private ImageView CalendarDay4ScheduleIV;

    @FXML
    private Button calendarDay5Button;
    @FXML
    private ImageView CalendarDay5NotificationIV;
    @FXML
    private ImageView CalendarDay5GoalIV;
    @FXML
    private ImageView CalendarDay5ScheduleIV;

    @FXML
    private Button calendarDay6Button;
    @FXML
    private ImageView CalendarDay6NotificationIV;
    @FXML
    private ImageView CalendarDay6GoalIV;
    @FXML
    private ImageView CalendarDay6ScheduleIV;

    @FXML
    private Button calendarDay7Button;
    @FXML
    private ImageView CalendarDay7NotificationIV;
    @FXML
    private ImageView CalendarDay7GoalIV;
    @FXML
    private ImageView CalendarDay7ScheduleIV;

    @FXML
    private Button calendarDay8Button;
    @FXML
    private ImageView CalendarDay8NotificationIV;
    @FXML
    private ImageView CalendarDay8GoalIV;
    @FXML
    private ImageView CalendarDay8ScheduleIV;

    @FXML
    private Button calendarDay9Button;
    @FXML
    private ImageView CalendarDay9NotificationIV;
    @FXML
    private ImageView CalendarDay9GoalIV;
    @FXML
    private ImageView CalendarDay9ScheduleIV;

    @FXML
    private Button calendarDay10Button;
    @FXML
    private ImageView CalendarDay10NotificationIV;
    @FXML
    private ImageView CalendarDay10GoalIV;
    @FXML
    private ImageView CalendarDay10ScheduleIV;

    @FXML
    private Button calendarDay11Button;
    @FXML
    private ImageView CalendarDay11NotificationIV;
    @FXML
    private ImageView CalendarDay11GoalIV;
    @FXML
    private ImageView CalendarDay11ScheduleIV;

    @FXML
    private Button calendarDay12Button;
    @FXML
    private ImageView CalendarDay12NotificationIV;
    @FXML
    private ImageView CalendarDay12GoalIV;
    @FXML
    private ImageView CalendarDay12ScheduleIV;

    @FXML
    private Button calendarDay13Button;
    @FXML
    private ImageView CalendarDay13NotificationIV;
    @FXML
    private ImageView CalendarDay13GoalIV;
    @FXML
    private ImageView CalendarDay13ScheduleIV;

    @FXML
    private Button calendarDay14Button;
    @FXML
    private ImageView CalendarDay14NotificationIV;
    @FXML
    private ImageView CalendarDay14GoalIV;
    @FXML
    private ImageView CalendarDay14ScheduleIV;

    @FXML
    private Button calendarDay15Button;
    @FXML
    private ImageView CalendarDay15NotificationIV;
    @FXML
    private ImageView CalendarDay15GoalIV;
    @FXML
    private ImageView CalendarDay15ScheduleIV;

    @FXML
    private Button calendarDay16Button;
    @FXML
    private ImageView CalendarDay16NotificationIV;
    @FXML
    private ImageView CalendarDay16GoalIV;
    @FXML
    private ImageView CalendarDay16ScheduleIV;

    @FXML
    private Button calendarDay17Button;
    @FXML
    private ImageView CalendarDay17NotificationIV;
    @FXML
    private ImageView CalendarDay17GoalIV;
    @FXML
    private ImageView CalendarDay17ScheduleIV;

    @FXML
    private Button calendarDay18Button;
    @FXML
    private ImageView CalendarDay18NotificationIV;
    @FXML
    private ImageView CalendarDay18GoalIV;
    @FXML
    private ImageView CalendarDay18ScheduleIV;

    @FXML
    private Button calendarDay19Button;
    @FXML
    private ImageView CalendarDay19NotificationIV;
    @FXML
    private ImageView CalendarDay19GoalIV;
    @FXML
    private ImageView CalendarDay19ScheduleIV;

    @FXML
    private Button calendarDay20Button;
    @FXML
    private ImageView CalendarDay20NotificationIV;
    @FXML
    private ImageView CalendarDay20GoalIV;
    @FXML
    private ImageView CalendarDay20ScheduleIV;

    @FXML
    private Button calendarDay21Button;
    @FXML
    private ImageView CalendarDay21NotificationIV;
    @FXML
    private ImageView CalendarDay21GoalIV;
    @FXML
    private ImageView CalendarDay21ScheduleIV;

    @FXML
    private Button calendarDay22Button;
    @FXML
    private ImageView CalendarDay22NotificationIV;
    @FXML
    private ImageView CalendarDay22GoalIV;
    @FXML
    private ImageView CalendarDay22ScheduleIV;

    @FXML
    private Button calendarDay23Button;
    @FXML
    private ImageView CalendarDay23NotificationIV;
    @FXML
    private ImageView CalendarDay23GoalIV;
    @FXML
    private ImageView CalendarDay23ScheduleIV;

    @FXML
    private Button calendarDay24Button;
    @FXML
    private ImageView CalendarDay24NotificationIV;
    @FXML
    private ImageView CalendarDay24GoalIV;
    @FXML
    private ImageView CalendarDay24ScheduleIV;

    @FXML
    private Button calendarDay25Button;
    @FXML
    private ImageView CalendarDay25NotificationIV;
    @FXML
    private ImageView CalendarDay25GoalIV;
    @FXML
    private ImageView CalendarDay25ScheduleIV;

    @FXML
    private Button calendarDay26Button;
    @FXML
    private ImageView CalendarDay26NotificationIV;
    @FXML
    private ImageView CalendarDay26GoalIV;
    @FXML
    private ImageView CalendarDay26ScheduleIV;

    @FXML
    private Button calendarDay27Button;
    @FXML
    private ImageView CalendarDay27NotificationIV;
    @FXML
    private ImageView CalendarDay27GoalIV;
    @FXML
    private ImageView CalendarDay27ScheduleIV;

    @FXML
    private Button calendarDay28Button;
    @FXML
    private ImageView CalendarDay28NotificationIV;
    @FXML
    private ImageView CalendarDay28GoalIV;
    @FXML
    private ImageView CalendarDay28ScheduleIV;

    @FXML
    private Button calendarDay29Button;
    @FXML
    private ImageView CalendarDay29NotificationIV;
    @FXML
    private ImageView CalendarDay29GoalIV;
    @FXML
    private ImageView CalendarDay29ScheduleIV;
    @FXML
    private Separator day29UpperSeparator;
    @FXML
    private Separator day29RightSeparator;

    @FXML
    private Button calendarDay30Button;
    @FXML
    private ImageView CalendarDay30NotificationIV;
    @FXML
    private ImageView CalendarDay30GoalIV;
    @FXML
    private ImageView CalendarDay30ScheduleIV;
    @FXML
    private Separator day30UpperSeparator;
    @FXML
    private Separator day30RightSeparator;

    @FXML
    private Button calendarDay31Button;
    @FXML
    private ImageView CalendarDay31NotificationIV;
    @FXML
    private ImageView CalendarDay31GoalIV;
    @FXML
    private ImageView CalendarDay31ScheduleIV;
    @FXML
    private Separator day31UpperSeparator;
    @FXML
    private Separator day31RightSeparator;
}
