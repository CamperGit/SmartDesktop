package com.camper.SmartDesktop.Info;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Day
{
    public enum EventType{Goal, Notification, Schedule};
    private LocalDate date;
    private boolean haveGoal = false;
    private boolean haveNotification = false;
    private boolean haveSchedule = false;
    private List<EventOfDay> events = new ArrayList<>();

    Day(LocalDate date)
    {
        this.date=date;
    }


    public LocalDate getDate() { return date; }
    public List<EventOfDay> getEvents() { return events; }

    public void addEvent(LocalTime time,EventType type, String info)
    {
        var event = new EventOfDay(time, type, info);
        this.getEvents().add(event);
    }

    public static Day addEventOfDay(LocalDate date, LocalTime time,EventType type, String info)
    {
        var day = new Day(date);
        day.addEvent(time, type, info);
        return day;
    }
}

class EventOfDay
{
    private LocalTime time;
    private Day.EventType type;
    private String info;

    public EventOfDay(LocalTime time, Day.EventType type, String info)
    {
        this.time=time;
        this.type=type;
        this.info=info;
    }

    public LocalTime getTime() { return time; }

    public Day.EventType getType() { return type; }

    public String getInfo() { return info; }
}