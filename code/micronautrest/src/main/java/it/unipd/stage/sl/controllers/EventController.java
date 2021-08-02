package it.unipd.stage.sl.controllers;


import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import it.unipd.stage.sl.errors.CreationErrorException;
import it.unipd.stage.sl.errors.EventIsUsedAsStarterException;
import it.unipd.stage.sl.objects.Chapter;
import it.unipd.stage.sl.objects.Event;
import it.unipd.stage.sl.objects.EventList;
import it.unipd.stage.sl.services.ChapterService;
import it.unipd.stage.sl.services.EventService;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.net.URI;
import java.net.URISyntaxException;


@Controller("/chapter/{chapterId}/event")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventController {

    @Inject
    EventService eventService;
    @Inject
    ChapterService chapterService;

    @Get("/")
    HttpResponse<EventList> getAll(@PathVariable Long chapterId) {
        Chapter ch = chapterService.getById(chapterId);
        if (ch == null) return HttpResponse.notFound();
        EventList cl = new EventList(ch.getEvents());
        return HttpResponse.ok(cl);
    }

    @Post("/")
    @Transactional
    HttpResponse<Event> create(@PathVariable Long chapterId, @Body Event event, HttpRequest<?> request) throws URISyntaxException {
        // check if chapter exists
        Chapter c = chapterService.getById(chapterId);
        if (c == null) return HttpResponse.notFound();
        event.setChapter(c);
        Event newEv = eventService.create(event);
        if (newEv == null) throw new CreationErrorException();
        // build uri with path to chapter for Location header
        Long id = newEv.getId();
        URI uri = (new URI(id.toString())).resolve(request.getUri());
        return HttpResponse.created(uri).body(newEv);
    }

    @Get("/{id}")
    HttpResponse<Event> getById(@PathVariable Long chapterId, @PathVariable Long id) {
        Event e = eventService.getByIdAndChapter(id, chapterId);
        if (e == null) return HttpResponse.notFound();
        return HttpResponse.ok(e);
    }

    @Put("/{id}")
    @Transactional
    HttpResponse<Event> put(@PathVariable Long chapterId, @PathVariable Long id, @Body Event event) {
        // get event from db
        Event original = eventService.getByIdAndChapter(id, chapterId);
        if (original == null)
            return HttpResponse.notFound();
        // update only valid fields
        event.setChapter(null); // do not update chapter, but only text
        original.updateNonNull(event);
        Event e = eventService.update(original);
        if (e == null)
            return HttpResponse.notFound();
        return HttpResponse.ok(e);
    }

    @Delete("/{id}")
    @Transactional
    HttpResponse<Event> delete(@PathVariable Long chapterId, @PathVariable Long id,
                                 @QueryValue(value = "force", defaultValue = "false") boolean force,
                                 @QueryValue(value = "cascade", defaultValue = "false") boolean cascade) {
        // get current event
        Event e = eventService.getByIdAndChapter(id, chapterId);
        if (e == null)
            return HttpResponse.notFound();
        // get chapter
        Chapter chapter = chapterService.getByStarter(id);
        if (!force && !cascade && chapter != null)
            throw new EventIsUsedAsStarterException();
        // else force is true or there are no dependencies so delete it anyway
        if (cascade) {
            chapterService.delete(chapterId);
            return HttpResponse.noContent();
        } else if (chapter != null) { // cascade=false and force=true and the event is a starter
            eventService.deleteForce(id);
        } else {
            // delete the event if it exists
            if (!eventService.delete(id)) {
                // nothing deleted
                HttpResponse.notFound();
            }
        }
        return HttpResponse.noContent();
    }
}
