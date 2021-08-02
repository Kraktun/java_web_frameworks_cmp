package it.unipd.stage.sl;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import it.unipd.stage.sl.objects.KeyHolder;
import it.unipd.stage.sl.objects.PrivateKey;
import it.unipd.stage.sl.objects.PublicKey;
import it.unipd.stage.sl.objects.RsaResult;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
public class RsaAPITest {

    @Inject
    @Client("${micronaut.server.context-path}") // i.e. /api/v2
    HttpClient httpClient;

    private final String RSA_PATH = "/rsa";

    @Test
    public void testGetKeys() {
        HttpRequest<String> req = HttpRequest.GET(RSA_PATH);
        HttpResponse<KeyHolder> result = httpClient.toBlocking().exchange(req, KeyHolder.class);
        assert result.getStatus() == HttpStatus.OK;
        assert result.body() != null;
        assert result.body().getPrivateKey().equals(new PrivateKey("10460274474002053", "1987201354784969"));
        assert result.body().getPublicKey().equals(new PublicKey("10460274474002053", "5223497001888909"));
    }

    /**
     * Test key generation with no values provided
     */
    @Test
    public void testGetKeysWithParams() {
        URI uri = UriBuilder.of(RSA_PATH).queryParam("length", "80")
                .queryParam("seed", "1739249").build();
        HttpRequest<String> req = HttpRequest.GET(uri);
        HttpResponse<KeyHolder> result = httpClient.toBlocking().exchange(req, KeyHolder.class);
        assert result.getStatus() == HttpStatus.OK;
        assert result.body() != null;
        assert result.body().getPrivateKey().equals(new PrivateKey("673583590782593190824629", "25288912844824492449485"));
        assert result.body().getPublicKey().equals(new PublicKey("673583590782593190824629", "47429"));
    }

    @Test
    public void testEncrypt() {
        URI uri = UriBuilder.of(RSA_PATH + "/encrypt").build();
        MutableHttpRequest<TextEncrypt> req = HttpRequest.POST(uri, new TextEncrypt("ciao", new PublicKey("10460274474002053", "5223497001888909")));
        HttpResponse<RsaResult> result = httpClient.toBlocking().exchange(req, RsaResult.class);
        assert result.getStatus() == HttpStatus.OK;
        assert result.body().getMessage().equals("DOERSW4A1Q==");
    }

    @Test
    public void testEncryptMissingText() {
        URI uri = UriBuilder.of(RSA_PATH + "/encrypt").build();
        MutableHttpRequest<PublicKey> req = HttpRequest.POST(uri, new PublicKey("10460274474002053", "5223497001888909"));
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
            httpClient.toBlocking().exchange(req);
        });
        assert e.getStatus() == HttpStatus.BAD_REQUEST;
    }

    @Test
    public void testDecrypt() {
        URI uri = UriBuilder.of(RSA_PATH + "/decrypt").build();
        MutableHttpRequest<TextDecrypt> req = HttpRequest.POST(uri, new TextDecrypt("DOERSW4A1Q==", new PublicKey("10460274474002053", "5223497001888909"), new PrivateKey("10460274474002053", "1987201354784969")));
        HttpResponse<RsaResult> result = httpClient.toBlocking().exchange(req, RsaResult.class);
        assert result.getStatus() == HttpStatus.OK;
        assert result.body().getMessage().equals("ciao");
    }
}
