package it.unipd.stage.sl.springrest.services;

import it.unipd.stage.sl.springrest.objects.Event;

import java.util.List;

/**
 * CRUD service for Events
 */
public interface EventService extends CRUDService<Event> {

    Event getByIdAndChapter(Long id, Long chapterId);

    boolean deleteForce(Long id);

    boolean deleteCascade(Long id);
}
