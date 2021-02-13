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

public class UpcomingEvent
{
    private static PriorityBlockingQueue<LocalDateTime> eventsOnQueue = new PriorityBlockingQueue<>(5,LocalDateTime::compareTo);
    private static Map<LocalDateTime,EventOfDay> infoOfEvents = new HashMap<>();
    public static ExecutorService executorService = Executors.newCachedThreadPool();
    private static Task<Integer> task;
    private static LocalDateTime upcomingEvent;

    public static Task<Integer> returnTask()
    {
        task = new Task<>()
        {
            //LocalDateTime timeOfEvent;
            @Override
            protected Integer call() throws Exception
            {
                var now = LocalDateTime.now();
                /*if (upcomingEvent!=null)
                {

                }*/
                while (upcomingEvent==null || now.isBefore(upcomingEvent))
                {
                    now=LocalDateTime.now();
                    try
                    {
                        upcomingEvent = eventsOnQueue.peek();
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                Platform.runLater(()->
                {
                    var otherInfoOfEvent = infoOfEvents.get(upcomingEvent);
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

        /*task.setOnRunning(event->
        {
            if(upcomingEvent!= null && !upcomingEvent.equals(eventsOnQueue.peek()))
            {
                task.cancel();
                executorService.execute(returnTask());
            }
        });*/

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
    }

    public static void addEventToQueue(LocalDate date, EventOfDay event)
    {
        var dateAndTime = LocalDateTime.of(date,event.getTime());
        eventsOnQueue.add(dateAndTime);
        infoOfEvents.put(dateAndTime,event);
        if (task!=null && upcomingEvent!=null && dateAndTime.isBefore(upcomingEvent))
        {
            task.cancel();
            executorService.execute(returnTask());
        }
    }
}
