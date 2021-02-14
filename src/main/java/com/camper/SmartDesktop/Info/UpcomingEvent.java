package com.camper.SmartDesktop.Info;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.camper.SmartDesktop.Info.CalendarSD.updateDayIcons;

public class UpcomingEvent
{
    private static PriorityBlockingQueue<LocalDateTime> eventsOnQueue = new PriorityBlockingQueue<>(5,LocalDateTime::compareTo);
    private static Map<LocalDateTime,EventOfDay> infoOfEvents = new HashMap<>();
    public static ExecutorService executorService = Executors.newCachedThreadPool();
    private static Task<Integer> task;
    private static LocalDateTime upcomingEvent;

    private static Task<Integer> returnTask()
    {
        task = new Task<>()
        {
            @Override
            protected Integer call() throws Exception
            {
                var now = LocalDateTime.now();
                while (eventsOnQueue.size()==0)
                {
                    Thread.onSpinWait();
                }
                upcomingEvent=eventsOnQueue.peek();
                while (now.isBefore(upcomingEvent))
                {
                    now=LocalDateTime.now();
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                Platform.runLater(()->
                {
                    var otherInfoOfEvent = infoOfEvents.get(upcomingEvent);
                    var date = upcomingEvent.toLocalDate();
                    var day = Day.removeEventFromDay(date,otherInfoOfEvent);
                    if (day==null)
                    {
                        updateDayIcons(date,false,false,false);

                    }
                    else
                    {
                        updateDayIcons(date,day.isHaveNotification(),day.isHaveGoal(),day.isHaveSchedule());
                    }

                    var alert = new Alert(Alert.AlertType.WARNING, otherInfoOfEvent.getType().toString()+": " + otherInfoOfEvent.getInfo(), ButtonType.OK);
                    alert.showAndWait();
                });
                return eventsOnQueue.size();
            }
        };

        task.setOnSucceeded(event->
        {
            eventsOnQueue.remove();
            if (task.getValue()!=0)
            {
                executorService.execute(returnTask());
            }
        });

        return task;
    }

    public static void loadEventsToQueue(List<Day> daysWithEvents)
    {
        if (daysWithEvents.size()!=0)
        {
            for (var day : daysWithEvents)
            {
                var events = day.getEvents();
                var date = day.getDate();

                for (var event : events)
                {
                    addEventToQueue(date,event);
                }
            }
        }
        runEventTask();
    }

    public static void addEventToQueue(LocalDate date, EventOfDay event)
    {
        var dateAndTime = LocalDateTime.of(date,event.getTime());
        eventsOnQueue.add(dateAndTime);
        infoOfEvents.put(dateAndTime,event);
        if (task!=null && upcomingEvent!=null && dateAndTime.isBefore(upcomingEvent))
        {
            task.cancel();
            runEventTask();
        }
    }

    public static void disableEventQueue(boolean exit) throws InterruptedException
    {
        if (task!=null) { task.cancel(); task=null; }
        if (exit)
        {
            executorService.shutdownNow();
            if (!executorService.awaitTermination(100, TimeUnit.MICROSECONDS))
            {
                System.exit(0);
            }
        }
        else
        {
            eventsOnQueue.clear();
            infoOfEvents.clear();
        }
    }

    public static void runEventTask()
    {
        executorService.execute(returnTask());
    }
}
