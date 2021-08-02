package it.unipd.stage.sl.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.unipd.stage.sl.lib.rsa.exceptions.MessageTooLongException;
import it.unipd.stage.sl.objects.PrivateKey;
import it.unipd.stage.sl.objects.PublicKey;
import it.unipd.stage.sl.objects.RsaResult;
import it.unipd.stage.sl.services.RsaService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static it.unipd.stage.sl.objects.Consts.DEFAULT_KEY_LENGTH;
import static it.unipd.stage.sl.objects.Consts.DEFAULT_SEED;

/**
 * Controller for rsa endpoints.
 * Uses IO threads.
 * Uses async responses.
 */
@Path("/async/rsa")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RsaAsyncController {

    @Inject
    RsaService rsaService; // uses reactive service

    @GET
    public void getKeysAsync(@Suspended final AsyncResponse async,
                             @DefaultValue(DEFAULT_KEY_LENGTH) @QueryParam("length") String length,
                             @DefaultValue(DEFAULT_SEED) @QueryParam("seed") String seed) {
        rsaService.generateKeys(length, seed).subscribe().with(
                item -> async.resume(Response.ok(item).build()),
                fail -> async.resume(new BadRequestException())
        );
    }

    @POST
    @Path("/encrypt")
    public void encryptAsync(@Suspended final AsyncResponse async, ObjectNode objectNode){
        ObjectMapper mapper = new ObjectMapper();
        try {
            String message = objectNode.get("text").asText();
            // Use jackson mapper to extract publicKey object
            PublicKey pb = mapper.treeToValue(objectNode.get("publicKey"), PublicKey.class);
            rsaService.encryptMessage(message, pb).subscribe().with(
                    item -> async.resume(Response.ok(new RsaResult(item)).build()),
                    fail -> async.resume(new BadRequestException())
            );
        } catch (JsonProcessingException | NullPointerException e) {
            async.resume(e);
        }
    }

    @POST
    @Path("/decrypt")
    public void decryptAsync(@Suspended final AsyncResponse async,
                             ObjectNode objectNode){
        ObjectMapper mapper = new ObjectMapper();
        try {
            String message = objectNode.get("text").asText();
            // Use jackson mapper to extract publicKey object
            PublicKey pb = mapper.treeToValue(objectNode.get("publicKey"), PublicKey.class);
            PrivateKey pr = mapper.treeToValue(objectNode.get("privateKey"), PrivateKey.class);
            rsaService.decryptMessage(message, pb, pr).subscribe().with(
                    item -> async.resume(Response.ok(new RsaResult(item)).build()),
                    fail -> async.resume(new BadRequestException())
            );
        } catch (JsonProcessingException | NullPointerException e) {
            async.resume(e);
        }
    }

    @POST
    @Path("/brute")
    public void bruteAsync(@Suspended final AsyncResponse async, @Valid PublicKey publicKey) {
        rsaService.bruteKey(publicKey).subscribe().with(
                item -> async.resume(Response.ok(item).build()),
                fail -> async.resume(new BadRequestException())
        );
    }
}
