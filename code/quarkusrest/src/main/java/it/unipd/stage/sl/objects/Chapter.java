package it.unipd.stage.sl.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Chapter entity.
 * Almost 1:1 copy of the objects from the other frameworks
 * Note that we can't use lazy fetch in quarkus in this case.
 */
@Entity
public class Chapter extends PanacheEntityBase implements ObjectUpdater<Chapter> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY to allow external loading in import.sql
    public Long id;

    @Column(nullable = false)
    public String number; // number of the chapter. String because we may want some unusual numbering (4a etc)
    @Column(nullable = false)
    public String title; // title of the chapter

    @OneToOne // can't use lazy fetch
    @JsonIgnoreProperties({"chapter"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Event starter; // first event of this chapter

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER) // eager fetch is mandatory here unfortunately
    @JsonIgnoreProperties({"chapter"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<Event> events;

    // getters and setters are auto generated by quarkus, but require public fields (...)

    // What follows are some static methods that follow the Panache pattern and pretty much work the same way as a CRUDRepository (note that also that pattern is available in quarkus)

    // for some reason findAll or listAll can't be called outside here as it does not allow casting, so this is a simple workaround
    public static List<Chapter> findAllChapters() {
        return listAll();
    }

    public static boolean isStarter(Long eventId) {
        return find("starter.id", eventId).count() > 0;
    }

    @Transactional
    public static Event setOrUpdateStarter(Long chapterId, Long starterId) {
        Event e = Event.findById(starterId);
        Chapter c = Chapter.findById(chapterId);
        if (e == null || c == null || !Objects.equals(e.chapter.id, chapterId))
            return null;
        c.starter = e;
        c.persist();
        return e;
    }

    @Override
    public String toString() {
        // It took a whole day to find out that when there are null values a check is necessary because Quarkus doesn't report a nullPointerException,
        // but an invalid reflection in an upstream class that has nothing to do with this. That's Quarkus for you.
        String starterString = String.valueOf(starter);
        String eventsString = events == null? "" : events.stream().map(Event::toString).collect(Collectors.joining(", "));
        return "Chapter{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", title='" + title + '\'' +
                ", starter=" + starterString +
                ", events=[" + eventsString +
                "]}";
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
        if (newObj.title != null)
            this.title = newObj.title;
        if (newObj.starter != null)
            this.starter = newObj.starter;
        if (newObj.number != null)
            this.number = newObj.number;
        if (newObj.events != null)
            this.events = newObj.events;
    }
}
