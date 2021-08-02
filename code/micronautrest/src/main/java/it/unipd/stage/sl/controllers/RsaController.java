package it.unipd.stage.sl.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import it.unipd.stage.sl.lib.rsa.exceptions.MessageTooLongException;
import it.unipd.stage.sl.lib.rsa.exceptions.UninitializedPrivateKeyException;
import it.unipd.stage.sl.objects.KeyHolder;
import it.unipd.stage.sl.objects.PrivateKey;
import it.unipd.stage.sl.objects.PublicKey;
import it.unipd.stage.sl.objects.RsaResult;
import it.unipd.stage.sl.services.RsaService;

import javax.inject.Inject;

import static it.unipd.stage.sl.objects.Consts.DEFAULT_KEY_LENGTH;
import static it.unipd.stage.sl.objects.Consts.DEFAULT_SEED;

/**
 * Controller for rsa endpoints
 */
@Controller("/rsa")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RsaController {

    @Inject
    // not private or native compilation breaks, or use @ReflectiveAccess in RsaServiceImpl
    RsaService rsaService;

    @Get("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    String ping() {
        return "pong";
    }

    @Get("/")
    HttpResponse<KeyHolder> getKeys(@QueryValue(defaultValue = DEFAULT_KEY_LENGTH) String length, @QueryValue(defaultValue = DEFAULT_SEED) String seed) {
        try {
            return HttpResponse.ok(rsaService.generateKeys(Integer.parseInt(length), Long.parseLong(seed)));
        } catch (NumberFormatException e) {
            return HttpResponse.badRequest();
        }
    }

    @Post("/encrypt")
    HttpResponse<RsaResult> encrypt(@Body ObjectNode objectNode) throws MessageTooLongException {
        // we don't keep a local POJO for this type of requests, so we need a manual mapping
        ObjectMapper mapper = new ObjectMapper();
        try {
            String message = objectNode.get("text").asText();
            // Use jackson mapper to extract publicKey object
            PublicKey pb = mapper.treeToValue(objectNode.get("publicKey"), PublicKey.class);
            String result = rsaService.encryptMessage(message, pb);
            return HttpResponse.ok(new RsaResult(result));
        } catch (JsonProcessingException | NullPointerException e) {
            return HttpResponse.badRequest();
        }
    }

    @Post("/decrypt")
    HttpResponse<RsaResult> decrypt(@Body ObjectNode objectNode) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String message = objectNode.get("text").asText();
            // Use jackson mapper to extract publicKey and privateKey objects
            PublicKey pb = mapper.treeToValue(objectNode.get("publicKey"), PublicKey.class);
            PrivateKey pr = mapper.treeToValue(objectNode.get("privateKey"), PrivateKey.class);
            String result = rsaService.decryptMessage(message, pb, pr);
            return HttpResponse.ok(new RsaResult(result));
        } catch (JsonProcessingException | UninitializedPrivateKeyException | NullPointerException e) {
            return HttpResponse.badRequest();
        }
    }

    @Post("/brute")
    HttpResponse<PrivateKey> brute(@Body PublicKey publicKey) {
        return HttpResponse.ok(rsaService.bruteKey(publicKey));
    }
}
