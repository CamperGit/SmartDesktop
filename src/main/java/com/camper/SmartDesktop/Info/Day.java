package com.camper.SmartDesktop.Info;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Day
{

    public boolean isHaveNotification() { return haveNotification; }
    public void setHaveNotification(boolean haveNotification) { this.haveNotification = haveNotification; }

    public boolean isHaveGoal() { return haveGoal; }
    public void setHaveGoal(boolean haveGoal) { this.haveGoal = haveGoal; }

    public boolean isHaveSchedule() { return haveSchedule; }
    public void setHaveSchedule(boolean haveSchedule) { this.haveSchedule = haveSchedule; }

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
        if (type==EventType.Notification) {this.setHaveNotification(true);}
        if (type==EventType.Goal) {this.setHaveGoal(true);}
        if (type==EventType.Schedule) {this.setHaveSchedule(true);}
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