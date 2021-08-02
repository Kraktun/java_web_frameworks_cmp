package it.unipd.stage.sl.springrest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.unipd.stage.sl.lib.rsa.exceptions.MessageTooLongException;
import it.unipd.stage.sl.lib.rsa.exceptions.UninitializedPrivateKeyException;
import it.unipd.stage.sl.springrest.objects.*;
import it.unipd.stage.sl.springrest.services.RsaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static it.unipd.stage.sl.springrest.objects.Consts.DEFAULT_KEY_LENGTH;
import static it.unipd.stage.sl.springrest.objects.Consts.DEFAULT_SEED;

/**
 * Controller for rsa endpoints
 */
@RestController // => no need for @ResponseBody
@RequestMapping(value = "/rsa", produces = MediaType.APPLICATION_JSON_VALUE)
public class RsaController {

    @Autowired
    private RsaService rsaService;

    @GetMapping(value = "/ping", produces = MediaType.TEXT_PLAIN_VALUE)
    String ping() {
        return "pong";
    }

    @GetMapping(value = {"", "/"})
    ResponseEntity<KeyHolder> getKeys(@RequestParam(defaultValue = DEFAULT_KEY_LENGTH) String length, @RequestParam(defaultValue = DEFAULT_SEED) String seed) {
        try {
            return ResponseEntity.ok(rsaService.generateKeys(Integer.parseInt(length), Long.parseLong(seed)));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/encrypt")
    ResponseEntity<RsaResult> encrypt(@RequestBody ObjectNode objectNode) throws MessageTooLongException {
        // we don't keep a local POJO for this type of requests, so we need a manual mapping
        ObjectMapper mapper = new ObjectMapper();
        try {
            String message = objectNode.get("text").asText();
            // Use jackson mapper to extract publicKey object
            PublicKey pb = mapper.treeToValue(objectNode.get("publicKey"), PublicKey.class);
            String result = rsaService.encryptMessage(message, pb);
            return ResponseEntity.ok(new RsaResult(result));
        } catch (JsonProcessingException | NullPointerException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/decrypt")
    ResponseEntity<RsaResult> decrypt(@RequestBody ObjectNode objectNode) throws UninitializedPrivateKeyException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String message = objectNode.get("text").asText();
            // Use jackson mapper to extract publicKey and privateKey objects
            PublicKey pb = mapper.treeToValue(objectNode.get("publicKey"), PublicKey.class);
            PrivateKey pr = mapper.treeToValue(objectNode.get("privateKey"), PrivateKey.class);
            String result = rsaService.decryptMessage(message, pb, pr);
            return ResponseEntity.ok(new RsaResult(result));
        } catch (JsonProcessingException | NullPointerException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/brute", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PrivateKey> brute(@RequestBody PublicKey publicKey) {
        return ResponseEntity.ok(rsaService.bruteKey(publicKey));
    }
}
