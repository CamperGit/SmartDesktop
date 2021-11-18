package com.camper.SmartDesktop.Info;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.camper.SmartDesktop.Info.DeprecatedEvents.getDaysWithDeprecatedEvents;
import static com.camper.SmartDesktop.Info.DeprecatedEvents.updateBellIcon;
import static com.camper.SmartDesktop.Main.languageBundle;

public class Day implements Cloneable
{

    public boolean isHaveNotification()
    {
        return haveNotification;
    }

    public void setHaveNotification(boolean haveNotification)
    {
        this.haveNotification = haveNotification;
    }

    public boolean isHaveGoal()
    {
        return haveGoal;
    }

    public void setHaveGoal(boolean haveGoal)
    {
        this.haveGoal = haveGoal;
    }

    public boolean isHaveSchedule()
    {
        return haveSchedule;
    }

    public void setHaveSchedule(boolean haveSchedule)
    {
        this.haveSchedule = haveSchedule;
    }

    public enum EventType
    {Goal, Notification, Schedule, Task}

    private LocalDate date;
    private boolean haveGoal = false;
    private boolean haveNotification = false;
    private boolean haveSchedule = false;
    private List<EventOfDay> events = new ArrayList<>();

    Day(LocalDate date)
    {
        this.date = date;
    }


    public LocalDate getDate()
    {
        return date;
    }

    public List<EventOfDay> getEvents()
    {
        return events;
    }

    public boolean addEvent(LocalTime time, EventType type, String info)
    {
        EventOfDay event = new EventOfDay(time, type, info);
        return this.addEvent(event);
    }

    /**
     * @param event событие для добавления
     * @return true, если удалось добавить событие и false, если такое уже существует
     */
    public boolean addEvent(EventOfDay event)
    {
        for (EventOfDay eventOfDay : events)
        {
            if (eventOfDay.getType().equals(event.getType()) && eventOfDay.getTime() == event.getTime() && eventOfDay.getInfo().equals(event.getInfo()))
            {
                if (event.getType().equals(EventType.Notification))
                {
                    Alert alert = new Alert(Alert.AlertType.WARNING, languageBundle.getString("equalsNotificationEventAlert"), ButtonType.OK);
                    alert.showAndWait();
                }
                if (event.getType().equals(EventType.Task))
                {
                    Alert alert = new Alert(Alert.AlertType.WARNING, languageBundle.getString("equalsGoalEventAlert"), ButtonType.OK);
                    alert.showAndWait();
                }
                return false;
            }
        }
        if (event.getType() == EventType.Notification)
        {
            this.setHaveNotification(true);
        }
        if (event.getType() == EventType.Goal)
        {
            this.setHaveGoal(true);
        }
        if (event.getType() == EventType.Schedule)
        {
            this.setHaveSchedule(true);
        }
        this.getEvents().add(event);
        return true;
    }

    /**
     * @return возвращает информацию о том остались ли в данном дне, после удаления, какие-либо события
     */
    public static boolean checkOfDeprecatedEvents(Day day, boolean addToDeprecatedEventsList)
    {
        List<EventOfDay> events = day.getEvents();
        List<EventOfDay> deprecatedEventsOfThisDay = new ArrayList<>();
        for (EventOfDay event : events)
        {
            LocalDateTime eventTime = LocalDateTime.of(day.getDate(), event.getTime());
            if (eventTime.isBefore(LocalDateTime.now()))
            {
                deprecatedEventsOfThisDay.add(event);
            }
        }
        for (EventOfDay deprecatedEvent : deprecatedEventsOfThisDay)
        {
            events.remove(deprecatedEvent);
        }

        if (deprecatedEventsOfThisDay.size() != 0 && addToDeprecatedEventsList)
        {
            Day dayWithDeprecatedEvents = null;
            List<Day> daysWithDeprecatedEvents = getDaysWithDeprecatedEvents();
            for(Day dayFromList : daysWithDeprecatedEvents)
            {
                if (dayFromList.getDate().equals(day.getDate()))
                {
                    dayWithDeprecatedEvents=dayFromList;
                }
            }
            if (dayWithDeprecatedEvents == null)
            {
                dayWithDeprecatedEvents = new Day(day.getDate());
                daysWithDeprecatedEvents.add(dayWithDeprecatedEvents);
            }
            dayWithDeprecatedEvents.getEvents().addAll(deprecatedEventsOfThisDay);
            DeprecatedEvents.increaseTheNumberOfEvent(dayWithDeprecatedEvents.getEvents().size());
            DeprecatedEvents.updateBellIcon(true);
        }

        day.setHaveNotification(false);
        day.setHaveGoal(false);
        day.setHaveSchedule(false);
        if (events.size() == 0)
        {
            return false;
        } else
        {
            for (EventOfDay otherEvent : events)
            {
                if (otherEvent.getType() == EventType.Notification)
                {
                    day.setHaveNotification(true);
                }
                if (otherEvent.getType() == EventType.Goal)
                {
                    day.setHaveGoal(true);
                }
                if (otherEvent.getType() == EventType.Schedule)
                {
                    day.setHaveSchedule(true);
                }
            }
            return true;
        }
    }

    /**
     * @param date  - дата дня для поиска среди списка
     * @param event - событие для удаления
     * @return возвращает null, если в дне больше не осталось событий. Если же события ещё есть, то возвращает день, из
     * которого было удалено событие
     */
    public static Day removeEventFromDay(LocalDate date, EventOfDay event)
    {
        Day day = null;
        List<Day> daysWithEvents = CalendarSD.getDaysWithEvents();
        for (Day dayWithEvent : daysWithEvents)
        {
            if (dayWithEvent.getDate().equals(date))
            {
                List<EventOfDay> events = dayWithEvent.getEvents();
                events.remove(event);
                if (events.size() != 0)
                {
                    day = dayWithEvent;
                    day.setHaveNotification(false);
                    day.setHaveGoal(false);
                    day.setHaveSchedule(false);
                    for (EventOfDay eventFromList : events)
                    {
                        if (eventFromList.getType() == EventType.Notification)
                        {
                            day.setHaveNotification(true);
                        }
                        if (eventFromList.getType() == EventType.Goal)
                        {
                            day.setHaveGoal(true);
                        }
                        if (eventFromList.getType() == EventType.Schedule)
                        {
                            day.setHaveSchedule(true);
                        }
                    }
                } else
                {
                    daysWithEvents.remove(dayWithEvent);
                }
                break;
            }
        }
        return day;
    }

    @Override
    public Day clone() throws CloneNotSupportedException
    {
        Day clonedDay = (Day) super.clone();
        clonedDay.events = new ArrayList<>(this.getEvents());
        return clonedDay;
    }
}

class EventOfDay
{
    private LocalTime time;
    private Day.EventType type;
    private String info;

    public EventOfDay(LocalTime time, Day.EventType type, String info)
    {
        this.time = time;
        this.type = type;
        this.info = info;
    }

    public LocalTime getTime()
    {
        return time;
    }

    public Day.EventType getType()
    {
        return type;
    }

    public String getInfo()
    {
        return info;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventOfDay that = (EventOfDay) o;
        return Objects.equals(time, that.time) && type == that.type && Objects.equals(info, that.info);
    }
}
