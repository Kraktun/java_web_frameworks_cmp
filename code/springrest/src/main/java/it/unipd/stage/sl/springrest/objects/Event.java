package it.unipd.stage.sl.springrest.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Objects;

/**
 * Event entity
 * See Chapter object for an explanation of the annotations used.
 */
@Entity
public class Event implements ObjectUpdater<Event> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String text; // text of the event

    @ManyToOne
    @JoinColumn(name="chapter_id", nullable=false)
    @JsonIgnoreProperties({"events", "starter", "title", "number"}) // do not serialize events when mapping to JSON to avoid returning a payload unnecessarily big
    Chapter chapter; // chapter of which this event is part

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
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
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public void updateNonNull(Event newObj) {
        if (newObj == null) return;
        if (newObj.getText() != null)
            this.text = newObj.getText();
        if (newObj.getChapter() != null)
            this.chapter = newObj.getChapter();
    }
}
