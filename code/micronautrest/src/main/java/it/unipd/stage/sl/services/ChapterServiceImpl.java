package it.unipd.stage.sl.services;

import it.unipd.stage.sl.objects.Chapter;
import it.unipd.stage.sl.objects.Event;
import it.unipd.stage.sl.services.repositories.ChapterRepository;
import it.unipd.stage.sl.services.repositories.EventRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Chapter service: usual CRUD service + methods to 'CRU' the start event of a chapter
 */
@Singleton
public class ChapterServiceImpl implements ChapterService {

    @Inject
    ChapterRepository chapterRepository;
    @Inject
    EventRepository eventRepository;

    @Override
    @Transactional // readOnly is set automatically
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
        // manual cascade implementation
        chapterRepository.findById(id).ifPresent(c -> {
            eventRepository.delete(c.getStarter());
            c.getEvents().forEach(e -> eventRepository.delete(e));
            c.setStarter(null);
            c.setEvents(null);
            chapterRepository.deleteById(id);
        });
        // there is no indication if the object existed
        return true;
    }

    @Override
    @Transactional
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
    @Transactional
    public Chapter getByStarter(Long eventId) {
        Event e = eventRepository.findById(eventId).orElse(null);
        if (e == null) return null; // no event with given id => no chapter
        return chapterRepository.findByStarter(e).orElse(null);
    }

    @Override
    @Transactional
    public List<Chapter> getAll() {
        List<Chapter> cs = new ArrayList<>();
        chapterRepository.findAll().forEach(cs::add);
        return cs;
    }
}
