package it.unipd.stage.sl.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.unipd.stage.sl.lib.rsa.exceptions.MessageTooLongException;
import it.unipd.stage.sl.lib.rsa.exceptions.UninitializedPrivateKeyException;
import it.unipd.stage.sl.objects.KeyHolder;
import it.unipd.stage.sl.objects.PrivateKey;
import it.unipd.stage.sl.objects.PublicKey;
import it.unipd.stage.sl.objects.RsaResult;
import it.unipd.stage.sl.services.BlockingRsaService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static it.unipd.stage.sl.objects.Consts.DEFAULT_KEY_LENGTH;
import static it.unipd.stage.sl.objects.Consts.DEFAULT_SEED;

/**
 * Controller for rsa endpoints.
 * Uses worker threads.
 * Exceptions are automatically managed by the wrappers.
 */
@Path("/blocking/rsa")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BlockingRsaController {

    @Inject
    BlockingRsaService blockingRsaService;

    @GET
    public Response getKeys(@DefaultValue(DEFAULT_KEY_LENGTH) @QueryParam("length") String length,
                        @DefaultValue(DEFAULT_SEED) @QueryParam("seed") String seed) {
        KeyHolder keys = blockingRsaService.generateKeys(Integer.parseInt(length), Long.parseLong(seed));
        return Response.ok(keys).build();
    }

    @POST
    @Path("/encrypt")
    public Response encrypt(ObjectNode objectNode) throws JsonProcessingException, MessageTooLongException {
        ObjectMapper mapper = new ObjectMapper();
        String message = objectNode.get("text").asText();
        // Use jackson mapper to extract publicKey object
        PublicKey pb = mapper.treeToValue(objectNode.get("publicKey"), PublicKey.class);
        String result = blockingRsaService.encryptMessage(message, pb);
        return Response.ok(new RsaResult(result)).build();
    }

    @POST
    @Path("/decrypt")
    public Response decrypt(ObjectNode objectNode) throws JsonProcessingException, UninitializedPrivateKeyException {
        ObjectMapper mapper = new ObjectMapper();
        String message = objectNode.get("text").asText();
        // Use jackson mapper to extract publicKey object
        PublicKey pb = mapper.treeToValue(objectNode.get("publicKey"), PublicKey.class);
        PrivateKey pr = mapper.treeToValue(objectNode.get("privateKey"), PrivateKey.class);
        String result = blockingRsaService.decryptMessage(message, pb, pr);
        return Response.ok(new RsaResult(result)).build();
    }

    /*
    Originally this used a get request, but quarkus does not parse the body of get requests.
    In HTTP ref. there is no clear statement about the use of body in get requests (e.g. elasticsearch uses them).
     */
    @POST
    @Path("/brute")
    public Response brute(@Valid PublicKey publicKey) {
        return Response.ok(blockingRsaService.bruteKey(publicKey)).build();
    }
}
