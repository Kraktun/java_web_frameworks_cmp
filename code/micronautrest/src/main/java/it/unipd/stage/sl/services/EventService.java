package it.unipd.stage.sl.services;

import it.unipd.stage.sl.objects.Event;

/**
 * CRUD service for Events
 */
public interface EventService extends CRUDService<Event> {

    Event getByIdAndChapter(Long id, Long chapterId);

    boolean deleteForce(Long id);
}
