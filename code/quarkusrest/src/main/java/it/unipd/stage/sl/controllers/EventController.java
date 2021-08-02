package it.unipd.stage.sl.controllers;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpServerRequest;
import it.unipd.stage.sl.exceptions.EventIsUsedAsStarterException;
import it.unipd.stage.sl.objects.Chapter;
import it.unipd.stage.sl.objects.Event;
import it.unipd.stage.sl.objects.EventList;
import it.unipd.stage.sl.utils.Utils;
import org.jboss.resteasy.reactive.RestPath;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Path("") // Note: can't have a global @Path("/chapter/{chapterId:([0-9]+)}/event") otherwise it conflicts with ChapterController
// still @Path("") is necessary, or it doesn't work
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
// Another possible way to build a simple api https://quarkus.io/guides/rest-data-panache
public class EventController {

    @GET
    @Path("/chapter/{chapterId:([0-9]+)}/event")
    @Blocking
    public Uni<Response> get(@RestPath Long chapterId) {
        return Uni.createFrom().item(Event.findByChapter(chapterId))
                .onItem().transform(Utils::nullOnEmpty)
                .onItem().ifNotNull().transform(EventList::new)
                .onItem().ifNotNull().transform(list -> Response.ok(list).build())
                .onItem().ifNull().continueWith(Response.status(NOT_FOUND)::build);
    }

    @POST
    @Path("/chapter/{chapterId:([0-9]+)}/event")
    @Blocking
    @Transactional(value=Transactional.TxType.REQUIRED,rollbackOn={SQLException.class, IOException.class})
    public Uni<Response> create(@RestPath Long chapterId, @Valid Event event, @Context HttpServerRequest request) {
        return Uni.createFrom().item((Chapter) Chapter.findById(chapterId)) // the cast is necessary here
                .onItem().ifNotNull().transform(ch -> {
                    event.chapter = ch;
                    event.id = null;
                    // no need for chapter.events.add(event) as we don't use chapter anymore here and the link in the tables is performed with event.persist()
                    return event;
                }).onItem().ifNotNull().invoke(it -> event.persist())
                .onItem().ifNotNull().transform(ev -> Response.ok(ev).status(CREATED).location(URI.create(request.uri()+"/"+ev.id)).build())
                .onItem().ifNull().continueWith(Response.status(NOT_FOUND)::build);
    }

    @GET
    @Path("/chapter/{chapterId:([0-9]+)}/event/{eventId:[0-9]+}")
    @Blocking
    public Uni<Response> getById(@RestPath Long chapterId, @RestPath Long eventId) {
        return Uni.createFrom().item(Event.findByIdAndChapter(chapterId, eventId))
                .onItem().ifNotNull().transform(ev -> Response.ok(ev).build())
                .onItem().ifNull().continueWith(Response.status(NOT_FOUND)::build);
    }

    @PUT
    @Path("/chapter/{chapterId:([0-9]+)}/event/{eventId:[0-9]+}")
    @Blocking
    @Transactional(value=Transactional.TxType.REQUIRED,rollbackOn={SQLException.class, IOException.class})
    public Uni<Response> put(@RestPath Long chapterId, @RestPath Long eventId, @Valid Event event) {
        return Uni.createFrom().item(Event.findByIdAndChapter(chapterId, eventId))
                .onItem().ifNotNull().invoke(ev -> {
                    ev.chapter = null; // do not update chapter, but only text
                    ev.updateNonNull(event);
                })
                .onItem().ifNotNull().invoke(ev -> ev.persist())
                .onItem().ifNotNull().transform(ev -> Response.ok(ev).build())
                .onItem().ifNull().continueWith(Response.status(NOT_FOUND)::build);
    }

    @DELETE
    @Path("/chapter/{chapterId:([0-9]+)}/event/{eventId:[0-9]+}")
    @Blocking
    @Transactional(value=Transactional.TxType.REQUIRED,rollbackOn={SQLException.class, IOException.class})
    public Uni<Response> delete(@RestPath Long chapterId, @RestPath Long eventId,
                                @DefaultValue("false") @QueryParam("force") boolean force,
                                @DefaultValue("false") @QueryParam("cascade") boolean cascade) {
        return Uni.createFrom().item(Event.findByIdAndChapter(chapterId, eventId))
                .onItem().ifNotNull().invoke(ev -> {
                    boolean isStarter = Chapter.isStarter(eventId);
                    if (!force && !cascade && isStarter)
                        throw new EventIsUsedAsStarterException();
                    if (cascade)
                        Event.deleteCascade(eventId);
                    else if (isStarter)
                        Event.deleteForce(eventId);
                    else
                        Event.deleteById(eventId);
                })
                .onItem().ifNotNull().transform(ev -> Response.noContent().build())
                .onItem().ifNull().continueWith(Response.status(NOT_FOUND)::build);
    }
}
