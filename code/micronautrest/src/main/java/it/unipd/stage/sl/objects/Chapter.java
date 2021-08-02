package it.unipd.stage.sl.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * Chapter entity
 */
@Entity // entity already includes @Introspected
// almost 1:1 copy of the objects from the other frameworks
public class Chapter implements ObjectUpdater<Chapter> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY to allow external loading in data.sql
    Long id;

    @Column(nullable = false)
    String number; // number of the chapter. String because we may want some unusual numbering (4a etc.)
    @Column(nullable = false)
    String title; // title of the chapter

    @OneToOne(fetch = FetchType.EAGER) // with lazy it does not work properly
    @JsonIgnoreProperties({"chapter"}) // avoid infinite recursive loop
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Event starter; // first event of this chapter

    // cascade doesn't work properly in micronaut and has to be manually implemented in chapterService
    @OneToMany(mappedBy = "chapter", fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"chapter"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<Event> events;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Event getStarter() {
        return starter;
    }

    public void setStarter(Event starter) {
        this.starter = starter;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chapter chapter = (Chapter) o;
        return Objects.equals(id, chapter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public void updateNonNull(Chapter newObj) {
        if (newObj == null) return;
        if (newObj.getTitle() != null)
            this.title = newObj.getTitle();
        if (newObj.getStarter() != null)
            this.starter = newObj.getStarter();
        if (newObj.getNumber() != null)
            this.number = newObj.getNumber();
        if (newObj.getEvents() != null)
            this.events = newObj.getEvents();
    }
}
