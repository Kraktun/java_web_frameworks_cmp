package it.unipd.stage.sl.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

/**
 * Simple event entity
 */
@Entity
public class Event extends PanacheEntityBase implements ObjectUpdater<Event> {

    /*
    Can't use PanacheEntity (that includes id) because it breaks the Chapter reference
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String text; // text of the event

    @ManyToOne
    @JoinColumn(name="chapter_id", nullable=false)
    @JsonIgnoreProperties({"events", "starter", "title", "number"}) // do not serialize events when mapping to JSON to avoid returning a payload unnecessarily big
    public Chapter chapter; // chapter of which this event is part

    public static List<Event> findByChapter(Long chapterId) {
        return list("chapter.id", chapterId);
    }

    public static Event findByIdAndChapter(Long chapterId, Long id) {
        return find("id = ?1 and chapter.id = ?2", id, chapterId).firstResult();
    }

    @Transactional
    public static void deleteCascade(Long eventId) {
        Chapter c = ((Event) Event.findById(eventId)).chapter;
        c.delete();
    }

    @Transactional
    public static void deleteForce(Long eventId) {
        Event e = Event.findById(eventId);
        Chapter c = e.chapter;
        c.starter = null;
        c.persist();
        e.delete();
    }

    @Override
    public String toString() {
        String chapterId = chapter == null? "null" : String.valueOf(chapter.id);
        return "Event{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", chapter=" + chapterId + // only id to avoid loops
                '}';
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
        if (newObj.text != null)
            this.text = newObj.text;
        if (newObj.chapter != null)
            this.chapter = newObj.chapter;
    }
}
