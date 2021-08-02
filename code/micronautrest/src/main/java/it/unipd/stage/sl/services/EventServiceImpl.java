package it.unipd.stage.sl.services;

import it.unipd.stage.sl.objects.Chapter;
import it.unipd.stage.sl.objects.Event;
import it.unipd.stage.sl.services.repositories.ChapterRepository;
import it.unipd.stage.sl.services.repositories.EventRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

/**
 * Event service: usual CRUD service
 */
@Singleton
public class EventServiceImpl implements EventService {

    @Inject
    EventRepository eventRepository;
    @Inject
    ChapterRepository chapterRepository;

    @Override
    @Transactional
    public Event getById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Event create(Event obj) {
        return eventRepository.save(obj);
    }

    @Override
    @Transactional
    public Event update(Event obj) {
        if (getByIdAndChapter(obj.getId(), obj.getChapter().getId()) != null)
            return eventRepository.save(obj);
        else return null;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        Event e = getById(id);
        if (e != null) {
            e.setChapter(null);
            eventRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Event getByIdAndChapter(Long id, Long chapterId) {
        Chapter c = chapterRepository.findById(chapterId).orElse(null);
        if (c == null) return null;
        return eventRepository.findByIdAndChapter(id, c).orElse(null);
    }

    @Override
    @Transactional
    public boolean deleteForce(Long id) {
        // set chapter starter as null and delete current event
        Event e = getById(id);
        if (e != null) {
            Chapter c = e.getChapter();
            if (c.getStarter().equals(e)) {
                c.setStarter(null);
                chapterRepository.save(c);
            }
            return delete(id);
        }
        return false;
    }
}
