package it.unipd.stage.sl.springrest.services.jpa;

import it.unipd.stage.sl.springrest.objects.Chapter;
import it.unipd.stage.sl.springrest.objects.Event;
import it.unipd.stage.sl.springrest.services.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Chapter service: usual CRUD service + methods to 'CRU' the start event of a chapter
 */
@Service
public class ChapterServiceImpl implements ChapterService {

    @Autowired
    ChapterRepository chapterRepository;
    @Autowired
    EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public Chapter getById(Long id) {
        return chapterRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Chapter create(Chapter obj) {
        return chapterRepository.save(obj);
    }

    @Override
    @Transactional
    public Chapter update(Chapter obj) {
        if (
                (obj.getStarter() != null && // note that setting starter to null is accepted
                        (obj.getStarter().getId() == null || !eventRepository.existsById(obj.getStarter().getId()))) || // we want to set as starter an event without id or one that doesn't exist
                !chapterRepository.existsById(obj.getId()) // chapter doesn't exist
        ) return null;
        return chapterRepository.save(obj);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        try {
            chapterRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Event getStartEvent(Long chapterId) {
        return chapterRepository.findById(chapterId).map(Chapter::getStarter).orElse(null);
    }

    @Override
    @Transactional
    public Chapter setOrUpdateStartEvent(Long chapterId, Long eventId) {
        Optional<Chapter> c = chapterRepository.findById(chapterId);
        Optional<Event> e = eventRepository.findById(eventId);
        if (c.isPresent() && e.isPresent()) {
            Chapter chap = c.get();
            Event eve = e.get();
            if (!chapterId.equals(eve.getChapter().getId()))
                return null; // event is not part of this chapter
            chap.setStarter(eve);
            chapterRepository.save(chap);
            return chap;
        } else return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Chapter getByStarter(Long eventId) {
        Event e = eventRepository.findById(eventId).orElse(null);
        if (e == null) return null; // no event with given id => no chapter
        return chapterRepository.findByStarter(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Chapter> getAll() {
        List<Chapter> cs = new ArrayList<>();
        chapterRepository.findAll().forEach(cs::add);
        return cs;
    }
}
