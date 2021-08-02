package it.unipd.stage.sl.springrest.controllers;

import it.unipd.stage.sl.springrest.errors.CreationErrorException;
import it.unipd.stage.sl.springrest.errors.EventIsUsedAsStarterException;
import it.unipd.stage.sl.springrest.objects.Chapter;
import it.unipd.stage.sl.springrest.objects.Event;
import it.unipd.stage.sl.springrest.objects.EventList;
import it.unipd.stage.sl.springrest.services.ChapterService;
import it.unipd.stage.sl.springrest.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping(value = "/chapter/{chapterId}/event", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private ChapterService chapterService;

    @GetMapping(value = {"", "/"})
    ResponseEntity<EventList> getAll(@PathVariable Long chapterId) {
        Chapter ch = chapterService.getById(chapterId);
        if (ch == null) return ResponseEntity.notFound().build();
        EventList cl = new EventList(ch.getEvents());
        return ResponseEntity.ok(cl);
    }

    @PostMapping(value = {"", "/"})
    @Transactional
    ResponseEntity<Event> create(@PathVariable Long chapterId, @RequestBody Event event) {
        // check if chapter exists
        Chapter c = chapterService.getById(chapterId);
        if (c == null) return ResponseEntity.notFound().build();
        event.setChapter(c);
        Event newEv = eventService.create(event);
        if (newEv == null) throw new CreationErrorException();
        // build uri with path to chapter for Location header
        Long id = newEv.getId();
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(uri).body(newEv);
    }

    @GetMapping(value = "/{id}")
    ResponseEntity<Event> getById(@PathVariable Long chapterId, @PathVariable Long id) {
        Event e = eventService.getByIdAndChapter(id, chapterId);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(e);
    }

    @PutMapping(value = "/{id}")
    @Transactional
    ResponseEntity<Event> put(@PathVariable Long chapterId, @PathVariable Long id, @RequestBody Event event) {
        // get event from db
        Event original = eventService.getByIdAndChapter(id, chapterId);
        if (original == null)
            return ResponseEntity.notFound().build();
        // update only valid fields, i.e. text
        event.setChapter(null); // do not update chapter, but only text
        original.updateNonNull(event);
        Event e = eventService.update(original);
        if (e == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(e);
    }

    @DeleteMapping(value = "/{id}")
    @Transactional
    ResponseEntity<Event> delete(@PathVariable Long chapterId, @PathVariable Long id,
                                 @RequestParam(value = "force", required = false, defaultValue = "false") boolean force,
                                 @RequestParam(value = "cascade", required = false, defaultValue = "false") boolean cascade) {
        Event e = eventService.getByIdAndChapter(id, chapterId);
        if (e == null)
            return ResponseEntity.notFound().build();
        // get chapter
        Chapter chapter = chapterService.getByStarter(id);
        if (!force && !cascade && chapter != null)
            throw new EventIsUsedAsStarterException();
        // else force is true or there are no dependencies so delete it anyway
        if (cascade) {
            eventService.deleteCascade(id);
            return ResponseEntity.noContent().build();
        } else if (chapter != null) { // cascade=false and force=true and the event is a starter
            eventService.deleteForce(id);
        } else {
            // delete the event if it exists
            if (!eventService.delete(id)) {
                // nothing deleted
                ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.noContent().build();
    }
}
