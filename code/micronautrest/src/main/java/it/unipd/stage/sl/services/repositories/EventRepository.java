package it.unipd.stage.sl.services.repositories;

import io.micronaut.context.annotation.Executable;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import it.unipd.stage.sl.objects.Chapter;
import it.unipd.stage.sl.objects.Event;

import java.util.Optional;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {

    @Executable
    Optional<Event> findByIdAndChapter(Long id, Chapter chapter);
}
