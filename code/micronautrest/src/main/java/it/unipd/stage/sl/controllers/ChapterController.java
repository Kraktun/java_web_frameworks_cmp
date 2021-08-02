package it.unipd.stage.sl.controllers;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import it.unipd.stage.sl.errors.CreationErrorException;
import it.unipd.stage.sl.objects.Chapter;
import it.unipd.stage.sl.objects.ChapterList;
import it.unipd.stage.sl.objects.Event;
import it.unipd.stage.sl.services.ChapterService;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


@Controller("/chapter")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChapterController {

    @Inject
    ChapterService chapterService;

    @Get("/") // the slash is mandatory
    HttpResponse<ChapterList> getAll() {
        List<Chapter> c = chapterService.getAll();
        ChapterList cl = new ChapterList(c);
        return HttpResponse.ok(cl);
    }

    @Post("/")
    HttpResponse<Chapter> create(@Body Chapter chapter, HttpRequest<?> request) throws URISyntaxException {
        chapter.setId(null); // avoid db errors if we get a payload with an id
        Chapter newC = chapterService.create(chapter);
        if (newC == null) throw new CreationErrorException();
        Long id = newC.getId();
        // build uri with path to chapter for Location header
        URI uri = (new URI(id.toString())).resolve(request.getUri());
        return HttpResponse.created(uri).body(chapter);
    }

    @Get("/{id}")
    HttpResponse<Chapter> getById(@PathVariable Long id) {
        Chapter c = chapterService.getById(id);
        if (c == null) return HttpResponse.notFound();
        return HttpResponse.ok(c);
    }

    @Put("/{id}")
    @Transactional
    HttpResponse<Chapter> put(@PathVariable Long id, @Body Chapter chapter) {
        // recover current chapter from db
        Chapter original = chapterService.getById(id);
        // This logic would be better in the service, but we may want to provide specific errors for different misconfigurations,
        // so I'd keep it here.
        // we may have provided only the id for the starter, so update it first
        if (chapter.getStarter() != null) {
            if (!original.getEvents().contains(chapter.getStarter())) {
                // we are trying to set a starter not part of the events of this chapter
                return HttpResponse.notFound();
            }
        }
        // update only valid fields
        original.updateNonNull(chapter);
        Chapter c = chapterService.update(original);
        if (c == null)
            return HttpResponse.notFound();
        return HttpResponse.ok(c);
    }

    @Delete("/{id}")
    HttpResponse<Chapter> delete(@PathVariable Long id) {
        if (chapterService.delete(id))
            return HttpResponse.noContent();
        else
            return HttpResponse.notFound();
    }

    @Get("/{id}/start")
    HttpResponse<Event> getStarter(@PathVariable Long id) {
        Event e = chapterService.getStartEvent(id);
        if (e == null) return HttpResponse.notFound();
        return HttpResponse.ok(e);
    }

    @Put("/{chId}/start")
    HttpResponse<Event> putStarter(@PathVariable Long chId, @QueryValue(value = "id") Long eventId) {
        Chapter c = chapterService.setOrUpdateStartEvent(chId, eventId);
        if (c == null || c.getStarter() == null)
            return HttpResponse.notFound();
        return HttpResponse.ok(c.getStarter());
    }
}
