package it.unipd.stage.sl.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.micronaut.core.annotation.Introspected;

import java.util.List;

/**
 * Events as a list without chapter for the getAll method
 */
@Introspected
public class EventList {

    @JsonIgnoreProperties({"chapter"})
    List<Event> events;

    public EventList() {
    }

    public EventList(List<Event> chapters) {
        this.events = chapters;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> chapters) {
        this.events = chapters;
    }
}
