package it.unipd.stage.sl.springrest.services.jpa;

import it.unipd.stage.sl.springrest.objects.Chapter;
import it.unipd.stage.sl.springrest.objects.Event;
import org.springframework.data.repository.CrudRepository;

public interface ChapterRepository extends CrudRepository<Chapter, Long> {

    Chapter findByStarter(Event e);
}
