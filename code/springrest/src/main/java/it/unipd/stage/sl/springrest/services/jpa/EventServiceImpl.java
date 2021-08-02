package it.unipd.stage.sl.springrest.services.jpa;

import it.unipd.stage.sl.springrest.objects.Chapter;
import it.unipd.stage.sl.springrest.objects.Event;
import it.unipd.stage.sl.springrest.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Chapter service: usual CRUD service + methods to 'CRU' the start event of a chapter
 */
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    ChapterRepository chapterRepository;

    @Override
    @Transactional(readOnly = true)
    public Event getById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
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
            eventRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Event getByIdAndChapter(Long id, Long chapterId) {
        Chapter c = chapterRepository.findById(chapterId).orElse(null);
        if (c == null) return null;
        return eventRepository.findByIdAndChapter(id, c);
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
            eventRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteCascade(Long id) {
        // delete event and its parent chapter
        Event e = getById(id);
        if (e != null) {
            Chapter c = e.getChapter();
            chapterRepository.deleteById(c.getId());
            return true;
        }
        return false;
    }
}
