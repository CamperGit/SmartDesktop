package com.camper.SmartDesktop.Info;

import com.camper.SmartDesktop.Main;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.camper.SmartDesktop.Info.DeprecatedEvents.getDaysWithDeprecatedEvents;
import static com.camper.SmartDesktop.Info.DeprecatedEvents.updateBellIcon;

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

    public void addEvent(EventOfDay event)
    {
        if (event.getType()==EventType.Notification) {this.setHaveNotification(true);}
        if (event.getType()==EventType.Goal) {this.setHaveGoal(true);}
        if (event.getType()==EventType.Schedule) {this.setHaveSchedule(true);}
        this.getEvents().add(event);
    }

    public static Day addEventOfDay(LocalDate date, LocalTime time,EventType type, String info)
    {
        var day = new Day(date);
        day.addEvent(time, type, info);
        return day;
    }

    /**
     * @return возвращает информацию о том остались ли в данном дне, после удаления, какие-либо события
     */
    public static boolean checkOfDeprecatedEvents(Day day)
    {
        var events = day.getEvents();
        var deprecatedEventsOfThisDay = new ArrayList<EventOfDay>();
        for (var event : events)
        {
            var eventTime = LocalDateTime.of(day.getDate(),event.getTime());
            if (eventTime.isBefore(LocalDateTime.now()))
            {
                deprecatedEventsOfThisDay.add(event);
            }
        }
        for (var deprecatedEvent : deprecatedEventsOfThisDay)
        {
            events.remove(deprecatedEvent);
        }

        if (deprecatedEventsOfThisDay.size()!=0)
        {
            var dayWithDeprecatedEvents = new Day(day.getDate());
            dayWithDeprecatedEvents.getEvents().addAll(deprecatedEventsOfThisDay);
            getDaysWithDeprecatedEvents().add(dayWithDeprecatedEvents);
            updateBellIcon();
        }

        day.setHaveNotification(false);
        day.setHaveGoal(false);
        day.setHaveSchedule(false);
        if (events.size()==0)
        {
            return false;
        }
        else
        {
            for (var otherEvent : events)
            {
                if (otherEvent.getType()==EventType.Notification){day.setHaveNotification(true);}
                if (otherEvent.getType()==EventType.Goal){day.setHaveGoal(true);}
                if (otherEvent.getType()==EventType.Schedule){day.setHaveSchedule(true);}
            }
            return true;
        }
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