package it.unipd.stage.sl.springrest.services.jpa;

import it.unipd.stage.sl.springrest.objects.Chapter;
import it.unipd.stage.sl.springrest.objects.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventRepository extends CrudRepository<Event, Long> {

    Event findByIdAndChapter(Long id, Chapter chapter);
}
