package it.unipd.stage.sl.controllers;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpServerRequest;
import it.unipd.stage.sl.objects.Chapter;
import it.unipd.stage.sl.objects.ChapterList;
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

import static javax.ws.rs.core.Response.Status.*;

@Path("") // Note: can't have a global @Path("/chapter") otherwise it conflicts with EventController
// still @Path("") is necessary, or it doesn't work
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ChapterController {

    @GET
    @Path("/chapter")
    @Blocking
    // not transactional because it's a single call
    public Uni<Response> get() {
        return Uni.createFrom().item(Chapter.findAllChapters()) // Uni<List<Chapter>>
                .onItem().transform(Utils::nullOnEmpty) // map empty lists to null
                .onItem().ifNotNull().transform(ChapterList::new) // Uni<ChapterList>, not a multi because we want the chapter:[] structure of the JSON
                .onItem().ifNotNull().transform(list -> Response.ok(list).build()) // build response
                .onItem().ifNull().continueWith(Response.status(NOT_FOUND)::build); // not found
    }

    @POST
    @Path("/chapter")
    @Blocking
    @Transactional(value=Transactional.TxType.REQUIRED,rollbackOn={SQLException.class, IOException.class}) // standard transactional values
    // Note that I'm not really sure if Transactional is supported. see https://github.com/quarkusio/quarkus/issues/15720
    // No @Body annotation below as it's not needed. @Valid checks that the parsing JSON -> Chapter gives a valid object
    public Uni<Response> create(@Valid Chapter chapter, @Context HttpServerRequest request) { // inject request with @Context annotation
        return Uni.createFrom().item(chapter)
                .onItem().ifNotNull().invoke(ch -> ch.id = null) // remove id if it was set in the request
                .onItem().ifNotNull().invoke(ch -> ch.persist()) // save to db
                .onItem().ifNotNull().transform(ch -> Response.ok(ch).status(CREATED).location(URI.create(request.uri()+"/"+ch.id)).build()) // extract current location from request and build location of new resource
                .onItem().ifNull().continueWith(Response.status(BAD_REQUEST)::build);
    }

    @GET
    @Path("/chapter/{chapterId:([0-9]+)}")
    @Blocking
    public Uni<Response> getById(@RestPath Long chapterId) {
        return Uni.createFrom().item((Chapter) Chapter.findById(chapterId)) // cast is necessary
                .onItem().ifNotNull().transform(ch -> Response.ok(ch).build())
                .onItem().ifNull().continueWith(Response.status(NOT_FOUND)::build);
    }

    @PUT
    @Path("/chapter/{chapterId:[0-9]+}")
    @Blocking
    @Transactional(value=Transactional.TxType.REQUIRED,rollbackOn={SQLException.class, IOException.class})
    public Uni<Response> put(@RestPath Long chapterId, @Valid Chapter chapter) {
        return Uni.createFrom().item((Chapter) Chapter.findById(chapterId))
                .onItem().ifNotNull().invoke(ch -> {
                    chapter.id = null; // remove id if it was set in the request
                    if (chapter.starter != null && ch.events.contains(chapter.starter)) // check that new starter is valid
                        ch.updateNonNull(chapter); // update only valid fields
                })
                .onItem().ifNotNull().invoke(ch -> ch.persist()) // save to db
                .onItem().ifNotNull().transform(ev -> Response.ok(ev).build())
                .onItem().ifNull().continueWith(Response.status(NOT_FOUND)::build);
    }

    @DELETE
    @Path("/chapter/{chapterId:[0-9]+}")
    @Blocking
    @Transactional(value=Transactional.TxType.REQUIRED,rollbackOn={SQLException.class, IOException.class})
    public Uni<Response> delete(@RestPath Long chapterId) {
        return Uni.createFrom().item((Chapter) Chapter.findById(chapterId))
                .onItem().ifNotNull().invoke(Chapter::delete) // delete from db
                .onItem().ifNotNull().transform(ev -> Response.noContent().build())
                .onItem().ifNull().continueWith(Response.status(NOT_FOUND)::build);
    }

    @GET
    @Path("/chapter/{chapterId:[0-9]+}/start")
    @Blocking
    public Uni<Response> getStarter(@RestPath Long chapterId) {
        return Uni.createFrom().item((Chapter) Chapter.findById(chapterId))
                .onItem().ifNotNull().transform(ch -> Response.ok(ch.starter).build())
                .onItem().ifNull().continueWith(Response.status(NOT_FOUND)::build);
    }

    @PUT
    @Path("/chapter/{chapterId:[0-9]+}/start")
    @Blocking
    @Transactional(value=Transactional.TxType.REQUIRED,rollbackOn={SQLException.class, IOException.class})
    public Uni<Response> putStarter(@RestPath Long chapterId, @QueryParam("id") Long id) {
        return Uni.createFrom().item(id)
                .onItem().ifNotNull().transform(evId -> Chapter.setOrUpdateStarter(chapterId, evId))
                .onItem().ifNotNull().transform(ev -> Response.ok(ev).build())
                .onItem().ifNull().continueWith(Response.status(NOT_FOUND)::build);
    }
}
