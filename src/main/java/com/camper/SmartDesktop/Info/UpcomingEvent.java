package com.camper.SmartDesktop.Info;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.camper.SmartDesktop.Info.CalendarSD.updateDayIcons;

public class UpcomingEvent extends Application implements Initializable
{
    private static PriorityBlockingQueue<LocalDateTime> eventsOnQueue = new PriorityBlockingQueue<>(5, LocalDateTime::compareTo);
    private static Map<LocalDateTime, EventOfDay> infoOfEvents = new HashMap<>();
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    private static Task<Integer> task;
    private static LocalDateTime upcomingEvent;
    private static boolean alreadyShowing = false;

    @Override
    public void start(Stage primaryStage) throws Exception
    {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

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
                    try
                    {
                        Thread.sleep(100);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                Platform.runLater(() ->
                {
                    if (!alreadyShowing)
                    {
                        var otherInfoOfEvent = infoOfEvents.get(upcomingEvent);
                        var date = upcomingEvent.toLocalDate();
                        if (otherInfoOfEvent.getType().equals(Day.EventType.Task))
                        {
                            alreadyShowing = true;
                            var alert = new Alert(Alert.AlertType.WARNING, otherInfoOfEvent.getType().toString() + ": " + otherInfoOfEvent.getInfo(), ButtonType.YES, ButtonType.NO);
                            var alertResult = alert.showAndWait();
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
                            if (goalSD!=null)
                            {
                                for (var node : goalSD.getTasksOfDay().get(date).getChildren())
                                {
                                    if (node instanceof Button && node.getAccessibleHelp()!=null && node.getAccessibleHelp().equals("addNewTaskButton"))
                                    {
                                        node.setDisable(true);
                                    }
                                }
                            }

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

                            alreadyShowing = true;
                            var alert = new Alert(Alert.AlertType.WARNING, otherInfoOfEvent.getType().toString() + ": " + otherInfoOfEvent.getInfo(), ButtonType.OK);
                            alert.showAndWait();
                        }
                    }
                });
                return eventsOnQueue.size();
            }
        };

        task.setOnSucceeded(event ->
        {
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
        var localDateTime = LocalDateTime.of(date,eventOfDay.getTime());
        eventsOnQueue.remove(localDateTime);
        infoOfEvents.remove(localDateTime);
    }

    public static void runEventTask()
    {
        executorService.execute(returnTask());
    }
}
