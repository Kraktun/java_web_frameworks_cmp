package it.unipd.stage.sl.services;

import it.unipd.stage.sl.objects.Chapter;
import it.unipd.stage.sl.objects.Event;

import java.util.List;

/**
 * Chapter service: usual CRUD service + methods to 'CRU' the start event of a chapter
 */
public interface ChapterService extends CRUDService<Chapter> {

    Event getStartEvent(Long chapterId);

    Chapter setOrUpdateStartEvent(Long chapterId, Long eventId);

    Chapter getByStarter(Long eventId);

    List<Chapter> getAll();
}
