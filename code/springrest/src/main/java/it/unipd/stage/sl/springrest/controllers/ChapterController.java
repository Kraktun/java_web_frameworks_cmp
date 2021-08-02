package it.unipd.stage.sl.springrest.controllers;

import it.unipd.stage.sl.springrest.errors.CreationErrorException;
import it.unipd.stage.sl.springrest.objects.Chapter;
import it.unipd.stage.sl.springrest.objects.ChapterList;
import it.unipd.stage.sl.springrest.objects.Event;
import it.unipd.stage.sl.springrest.services.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping(value = "/chapter", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @GetMapping(value = {"", "/"})
    ResponseEntity<ChapterList> getAll() {
        List<Chapter> c = chapterService.getAll();
        ChapterList cl = new ChapterList(c);
        return ResponseEntity.ok(cl);
    }

    @PostMapping(value = {"", "/"})
    ResponseEntity<Chapter> create(@RequestBody Chapter chapter) {
        chapter.setId(null); // avoid db errors if we get a payload with an id
        Chapter newC = chapterService.create(chapter);
        if (newC == null) throw new CreationErrorException();
        Long id = newC.getId();
        // build uri with path to chapter for Location header
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(uri).body(chapter);
    }

    @GetMapping(value = "/{id}")
    ResponseEntity<Chapter> getById(@PathVariable Long id) {
        Chapter c = chapterService.getById(id);
        if (c == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(c);
    }

    @PutMapping(value = "/{id}")
    @Transactional
    ResponseEntity<Chapter> put(@PathVariable Long id, @RequestBody Chapter chapter) {
        // recover events from db
        Chapter original = chapterService.getById(id);
        // This logic would be better in the service, but we may want to provide specific errors for different misconfigurations,
        // so I'd keep it here.
        // we may have provided only the id for the starter, so update it first
        if (chapter.getStarter() != null) {
            if (!original.getEvents().contains(chapter.getStarter())) {
                // we are trying to set a starter not part of the events of this chapter
                return ResponseEntity.notFound().build();
            }
        }
        // update only valid fields
        original.updateNonNull(chapter);
        Chapter c = chapterService.update(original);
        if (c == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(c);
    }

    @DeleteMapping(value = "/{id}")
    ResponseEntity<Chapter> delete(@PathVariable Long id) {
        if (chapterService.delete(id))
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{id}/start")
    ResponseEntity<Event> getStarter(@PathVariable Long id) {
        Event e = chapterService.getStartEvent(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(e);
    }

    @PutMapping(value = "/{id}/start")
    ResponseEntity<Event> putStarter(@PathVariable Long id, @RequestParam(value = "id") Long eventId) {
        Chapter c = chapterService.setOrUpdateStartEvent(id, eventId);
        if (c == null || c.getStarter() == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(c.getStarter());
    }
}
