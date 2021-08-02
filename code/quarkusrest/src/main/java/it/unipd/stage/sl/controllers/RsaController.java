package it.unipd.stage.sl.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.smallrye.mutiny.Uni;
import it.unipd.stage.sl.lib.rsa.exceptions.MessageTooLongException;
import it.unipd.stage.sl.objects.PrivateKey;
import it.unipd.stage.sl.objects.PublicKey;
import it.unipd.stage.sl.objects.RsaResult;
import it.unipd.stage.sl.services.RsaService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static it.unipd.stage.sl.objects.Consts.DEFAULT_KEY_LENGTH;
import static it.unipd.stage.sl.objects.Consts.DEFAULT_SEED;

/**
 * Controller for rsa endpoints.
 * Uses IO threads, but not async.
 * Exceptions are mapped once thrown.
 */
@Path("/rsa")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RsaController {

    @Inject
    RsaService rsaService; // reactive service

    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        return "pong";
    }

    @GET
    public Uni<Response> getKeys(@DefaultValue(DEFAULT_KEY_LENGTH) @QueryParam("length") String length,
                                 @DefaultValue(DEFAULT_SEED) @QueryParam("seed") String seed) {
        return rsaService.generateKeys(length, seed).onItem().transform(item -> Response.ok(item).build());
    }

    @POST
    @Path("/encrypt")
    public Uni<Response> encrypt(ObjectNode objectNode) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String message = objectNode.get("text").asText();
        // Use jackson mapper to extract publicKey object
        PublicKey pb = mapper.treeToValue(objectNode.get("publicKey"), PublicKey.class);
        return rsaService.encryptMessage(message, pb).onItem().transform(item -> Response.ok(new RsaResult(item)).build());
    }

    @POST
    @Path("/decrypt")
    public Uni<Response> decrypt(ObjectNode objectNode) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String message = objectNode.get("text").asText();
        PublicKey pb = mapper.treeToValue(objectNode.get("publicKey"), PublicKey.class);
        PrivateKey pr = mapper.treeToValue(objectNode.get("privateKey"), PrivateKey.class);
        return rsaService.decryptMessage(message, pb, pr).onItem().transform(item -> Response.ok(new RsaResult(item)).build());
    }

    /*
    Originally this used a get request, but quarkus does not parse the body of get requests.
    In HTTP ref. there is no clear statement about the use of body in get requests (e.g. elasticsearch uses them).
     */
    @POST
    @Path("/brute")
    public Uni<Response> brute(@Valid PublicKey publicKey) {
        return rsaService.bruteKey(publicKey).onItem().transform(item -> Response.ok(item).build());
    }
}
