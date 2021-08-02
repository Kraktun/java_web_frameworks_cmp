package it.unipd.stage.sl.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Events as a list without chapter for the getAll method
 */
public class EventList {

    @JsonIgnoreProperties({"chapter"})
    List<Event> events;

    public EventList() {
    }

    public EventList(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        String eventsString = events == null? "" : events.stream().map(Event::toString).collect(Collectors.joining(", "));
        return "EventList{" +
                "events=[" + eventsString +
                "]}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventList eventList = (EventList) o;
        return Objects.equals(events, eventList.events);
    }

    @Override
    public int hashCode() {
        return Objects.hash(events);
    }
}
