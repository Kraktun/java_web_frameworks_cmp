package it.unipd.stage.sl.controllers;

import io.quarkus.arc.profile.IfBuildProfile;
import it.unipd.stage.sl.objects.PrivateKey;
import it.unipd.stage.sl.services.TestServiceImpl;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A simple test controller
 */
@Path("/test")
@IfBuildProfile("dev")
public class TestController {

    @Inject
    TestServiceImpl testService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    // @Valid here works as below and makes sure that the response follows the rule specified in the entity
    public @Valid PrivateKey getSomething() {
        return new PrivateKey("2", "5");
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    // @Valid requires that fields of key are not blank (as specified in PrivateKey)
    public Response addSomething(@Valid PrivateKey key) {
        System.out.println(key.getD());
        return Response.ok().build();
    }

    @GET
    @Path("/plain")
    @Produces(MediaType.TEXT_PLAIN)
    public String testService() {
        return testService.saySomething();
    }
}
