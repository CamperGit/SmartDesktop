package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import com.camper.SmartDesktop.NodeDragger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.camper.SmartDesktop.Info.CalendarSD.updateDayIcons;
import static com.camper.SmartDesktop.Main.*;

public class UpcomingEvent extends Application implements Initializable
{
    @FXML
    private Button upcomingEventCloseButton;
    @FXML
    private ToolBar upcomingEventToolBar;
    @FXML
    private Label upcomingEventLabel, upcomingEventTimeLabel;
    @FXML
    private ImageView upcomingEventCloseButtonIV;

    private static PriorityBlockingQueue<LocalDateTime> eventsOnQueue = new PriorityBlockingQueue<>(5, LocalDateTime::compareTo);
    private static Map<LocalDateTime, EventOfDay> infoOfEvents = new HashMap<>();
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    private static Task<Integer> task;
    private static LocalDateTime upcomingEvent;
    private static boolean alreadyShowing = false;

    private static AnchorPane UpcomingEventInfoRoot = null;
    private boolean load = false;

    public static AnchorPane getUpcomingEventInfoRoot()
    {
        return UpcomingEventInfoRoot;
    }

    public UpcomingEvent()
    {
    }

    private UpcomingEvent(boolean load)
    {
        this.load = load;
    }

    public static void clearLastInfo()
    {
        Main.root.getChildren().remove(UpcomingEventInfoRoot);
        UpcomingEventInfoRoot = null;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        logger.info("UpcomingEvent: begin start method");
        UpcomingEventInfoRoot = FXMLLoader.load(Objects.requireNonNull(mainCL.getResource("FXMLs/upcomingEvent.fxml")));
        UpcomingEventInfoRoot.setLayoutX(80);
        UpcomingEventInfoRoot.setLayoutY(30);

        addChild(UpcomingEventInfoRoot);
        if (!load)
        {
            UpcomingEventInfoRoot.setAccessibleText(String.valueOf(idOfSelectedTab));
            var elementsOfSelectedTab = tabs.get(idOfSelectedTab);
            elementsOfSelectedTab.add(UpcomingEventInfoRoot);
        }
        logger.info("UpcomingEvent: end start method");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        logger.info("UpcomingEvent: begin initialize method");
        upcomingEventLabel.setText(languageBundle.getString("upcomingEventLabel"));
        upcomingEventCloseButtonIV.setImage(new Image("Images/delete30.png"));

        upcomingEventToolBar.setOnMouseDragged(event ->
        {
            UpcomingEventInfoRoot = (AnchorPane) (((ToolBar) event.getSource()).getParent());
            NodeDragger.addDraggingProperty(UpcomingEventInfoRoot, event);
        });

        upcomingEventCloseButton.setOnAction(event ->
        {
            UpcomingEventInfoRoot = (AnchorPane) (((Button) event.getSource()).getParent());
            Main.root.getChildren().remove(UpcomingEventInfoRoot);
            UpcomingEventInfoRoot = null;
            logger.info("UpcomingEvent: close upcoming event window");
        });
        logger.info("UpcomingEvent: end initialize method");
    }

    private static Task<Integer> returnTask()
    {
        task = new Task<>()
        {
            @Override
            protected Integer call()
            {
                var now = LocalDateTime.now();
                while (eventsOnQueue.size() == 0)
                {
                    Thread.onSpinWait();
                }
                upcomingEvent = eventsOnQueue.peek();
                while (now.isBefore(upcomingEvent))
                {
                    now = LocalDateTime.now();
                    if (UpcomingEventInfoRoot != null && UpcomingEventInfoRoot.isVisible())
                    {
                        for (var node : UpcomingEventInfoRoot.getChildren())
                        {
                            if (node instanceof Label && node.getAccessibleHelp() != null && node.getAccessibleHelp().equals("upcomingEventTimeLabel"))
                            {
                                Platform.runLater(() ->
                                {
                                    LocalDateTime tempDateTime = LocalDateTime.from(LocalDateTime.now());

                                    long years = tempDateTime.until(upcomingEvent, ChronoUnit.YEARS);
                                    tempDateTime = tempDateTime.plusYears(years);

                                    long months = tempDateTime.until(upcomingEvent, ChronoUnit.MONTHS);
                                    tempDateTime = tempDateTime.plusMonths(months);

                                    long days = tempDateTime.until(upcomingEvent, ChronoUnit.DAYS);
                                    tempDateTime = tempDateTime.plusDays(days);


                                    long hours = tempDateTime.until(upcomingEvent, ChronoUnit.HOURS);
                                    tempDateTime = tempDateTime.plusHours(hours);

                                    long minutes = tempDateTime.until(upcomingEvent, ChronoUnit.MINUTES);
                                    tempDateTime = tempDateTime.plusMinutes(minutes);

                                    long seconds = tempDateTime.until(upcomingEvent, ChronoUnit.SECONDS);

                                    String time;
                                    if (defaultLocale.equals(Locale.ENGLISH))
                                    {
                                        time = MessageFormat.format("{0, choice,0#0{0}| 10#{0}} d : {1, choice,0#0{1}| 10#{1}} h : {2, choice,0#0{2}| 10#{2}} min : {3, choice,0#0{3}| 10#{3}} sec",
                                                days, hours, minutes, seconds);
                                    }
                                    else
                                    {
                                        time = MessageFormat.format("{0, choice,0#0{0}| 10#{0}} � : {1, choice,0#0{1}| 10#{1}} � : {2, choice,0#0{2}| 10#{2}} ��� : {3, choice,0#0{3}| 10#{3}} ���",
                                                days, hours, minutes, seconds);
                                    }
                                    ((Label) node).setText(time);
                                });
                            }
                        }
                    }
                    try
                    {
                        Thread.sleep(100);
                    } catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                    }
                }
                Platform.runLater(() ->
                {
                    if (!alreadyShowing)
                    {
                        var otherInfoOfEvent = infoOfEvents.get(upcomingEvent);
                        var date = upcomingEvent.toLocalDate();
                        var dayWithEvent = CalendarSD.checkUsingOfThisDateOnEventList(date);
                        if (dayWithEvent != null)
                        {
                            Day.checkOfDeprecatedEvents(dayWithEvent,true);
                        }
                        DeprecatedEvents.updateBellIcon(false);
                        if (otherInfoOfEvent.getType().equals(Day.EventType.Task))
                        {
                            alreadyShowing = true;
                            var alert = new Alert(Alert.AlertType.WARNING, languageBundle.getString("upcomingEventTaskAlert") + " " + otherInfoOfEvent.getInfo(), ButtonType.YES, ButtonType.NO);
                            var alertResult = alert.showAndWait();
                            logger.info("UpcomingEvent: an event has occurred - Task");
                            GoalSD.updateStateOfGoalCheckBoxes(otherInfoOfEvent, alertResult.orElse(ButtonType.NO) == ButtonType.YES);
                        }
                        if (otherInfoOfEvent.getType().equals(Day.EventType.Goal))
                        {
                            var day = Day.removeEventFromDay(date, otherInfoOfEvent);
                            if (day == null)
                            {
                                updateDayIcons(date, false, false, false);
                            } else
                            {
                                updateDayIcons(date, day.isHaveNotification(), day.isHaveGoal(), day.isHaveSchedule());
                            }

                            var goalSD = GoalSD.getGoalFromGoalName(otherInfoOfEvent.getInfo());
                            if (goalSD != null)
                            {
                                for (var node : goalSD.getTasksOfDay().get(date).getChildren())
                                {
                                    if (node instanceof Button && node.getAccessibleHelp() != null && node.getAccessibleHelp().equals("addNewTaskButton"))
                                    {
                                        node.setDisable(true);
                                    }
                                }
                            }
                            logger.info("UpcomingEvent: an event has occurred - Goal");
                            alreadyShowing = true;
                        }
                        if (!(otherInfoOfEvent.getType().equals(Day.EventType.Task)) && !(otherInfoOfEvent.getType().equals(Day.EventType.Goal)))
                        {
                            var day = Day.removeEventFromDay(date, otherInfoOfEvent);
                            if (day == null)
                            {
                                updateDayIcons(date, false, false, false);
                            } else
                            {
                                updateDayIcons(date, day.isHaveNotification(), day.isHaveGoal(), day.isHaveSchedule());
                            }

                            String typeOfEvent;
                            if (otherInfoOfEvent.getType().equals(Day.EventType.Schedule))
                            {
                                typeOfEvent = languageBundle.getString("upcomingEventScheduleAlert");
                            }
                            else
                            {
                                typeOfEvent = languageBundle.getString("upcomingEventNotificationAlert");
                            }
                            alreadyShowing = true;
                            var alert = new Alert(Alert.AlertType.WARNING, typeOfEvent + " " + otherInfoOfEvent.getInfo(), ButtonType.OK);
                            alert.showAndWait();
                            logger.info("UpcomingEvent: an event has occurred - " + typeOfEvent);
                        }
                    }
                });
                return eventsOnQueue.size();
            }
        };

        task.setOnSucceeded(event ->
        {
            logger.info("UpcomingEvent: thread task are succeeded");
            alreadyShowing = false;
            infoOfEvents.remove(eventsOnQueue.peek());
            eventsOnQueue.remove();
            if (task.getValue() != 0)
            {
                executorService.execute(returnTask());
            }
        });

        return task;
    }

    public static void loadEventsToQueue(List<Day> daysWithEvents)
    {
        if (daysWithEvents.size() != 0)
        {
            for (var day : daysWithEvents)
            {
                var events = day.getEvents();
                var date = day.getDate();

                for (var event : events)
                {
                    addEventToQueue(date, event);
                }
            }
        }
        if (task == null && upcomingEvent == null)
        {
            runEventTask();
        }
    }

    public static void addEventToQueue(LocalDate date, EventOfDay event)
    {
        var dateAndTime = LocalDateTime.of(date, event.getTime());
        while (infoOfEvents.containsKey(dateAndTime))
        {
            dateAndTime = dateAndTime.plusNanos(1);
        }
        eventsOnQueue.add(dateAndTime);
        infoOfEvents.put(dateAndTime, event);
        if (task != null && upcomingEvent != null && (dateAndTime.isBefore(upcomingEvent) || dateAndTime.equals(upcomingEvent)))
        {
            task.cancel();
            runEventTask();
        }
    }

    public static void disableEventQueue(boolean exit) throws InterruptedException
    {
        if (task != null)
        {
            task.cancel();
            task = null;
            upcomingEvent = null;
        }
        if (exit)
        {
            executorService.shutdownNow();
            if (!executorService.awaitTermination(100, TimeUnit.MICROSECONDS))
            {
                System.exit(0);
            }
        } else
        {
            eventsOnQueue.clear();
            infoOfEvents.clear();
        }
    }

    public static void removeEventFromQueue(LocalDate date, EventOfDay eventOfDay)
    {
        if (task!=null)
        {
            task.cancel();
            var localDateTime = LocalDateTime.of(date, eventOfDay.getTime());
            eventsOnQueue.remove(localDateTime);
            infoOfEvents.remove(localDateTime);
            runEventTask();
        }
    }

    public static void runEventTask()
    {
        executorService.execute(returnTask());
        logger.info("UpcomingEvent: new task has been started");
    }

    public static void addUpcomingEventInfoToXML(Document doc, boolean createEmptyXML)
    {
        logger.info("UpcomingEvent: start saving upcoming event element");
        var rootElement = doc.getFirstChild();

        var upcomingEventInfoElement = doc.createElement("upcomingEventInfo");
        rootElement.appendChild(upcomingEventInfoElement);
        if (!createEmptyXML && UpcomingEventInfoRoot != null)
        {
            upcomingEventInfoElement.setAttribute("tab", UpcomingEventInfoRoot.getAccessibleText());

            var visibilityElement = doc.createElement("visibility");
            upcomingEventInfoElement.appendChild(visibilityElement);
            var visibilityValue = doc.createTextNode(String.valueOf(UpcomingEventInfoRoot.isVisible()));
            visibilityElement.appendChild(visibilityValue);

            var layoutElement = doc.createElement("layout");
            upcomingEventInfoElement.appendChild(layoutElement);

            var layoutX = doc.createElement("layoutX");
            layoutElement.appendChild(layoutX);
            var layoutXValue = doc.createTextNode(String.valueOf((int) (UpcomingEventInfoRoot.getLayoutX())));
            layoutX.appendChild(layoutXValue);

            var layoutY = doc.createElement("layoutY");
            layoutElement.appendChild(layoutY);
            var layoutYValue = doc.createTextNode(String.valueOf((int) (UpcomingEventInfoRoot.getLayoutY())));
            layoutY.appendChild(layoutYValue);
        }
        logger.info("UpcomingEvent: end saving upcoming event element");
    }

    public static void loadUpcomingEventInfoFromXML(Document doc, XPath xPath) throws Exception
    {
        logger.info("UpcomingEvent: start loading upcoming event element");
        boolean notEmpty = xPath.evaluateExpression("count(/save/upcomingEventInfo/*)", doc, Integer.class) != 0;
        if (notEmpty)
        {
            var loadingUpcomingEventInfo = new UpcomingEvent(true);
            loadingUpcomingEventInfo.start(Stage);

            int numberOfTab = Integer.parseInt(xPath.evaluate("/save/upcomingEventInfo/@tab", doc));
            UpcomingEventInfoRoot.setAccessibleText(String.valueOf(numberOfTab));

            var tab = tabs.get(numberOfTab);
            tab.add(UpcomingEventInfoRoot);
            boolean visibility = Boolean.parseBoolean(xPath.evaluate("/save/upcomingEventInfo/visibility/text()", doc));
            UpcomingEventInfoRoot.setVisible(visibility);

            double layoutX = Double.parseDouble(xPath.evaluate("/save/upcomingEventInfo/layout/layoutX/text()", doc));
            double layoutY = Double.parseDouble(xPath.evaluate("/save/upcomingEventInfo/layout/layoutY/text()", doc));
            UpcomingEventInfoRoot.setLayoutX(layoutX);
            UpcomingEventInfoRoot.setLayoutY(layoutY);
        }
        logger.info("UpcomingEvent: end loading upcoming event element");
    }
}
