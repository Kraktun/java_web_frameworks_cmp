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
import it.unipd.stage.sl.objects.Chapter;
import it.unipd.stage.sl.objects.Event;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Example class to test api endpoints.
 */
@MicronautTest(rollback = false) // i.e. keep results from method to method
public class ChapterAPITest {

    @Inject
    @Client("${micronaut.server.context-path}")
    HttpClient httpClient;

    final static String CHAPTER_PATH = "/chapter";
    final static String EVENT_PATH = "/chapter/{cid}/event";

    @Test
    public void getEmptyChapters() {
        get404(1289L);
    }

    @Test
    public void get() {
        assert simpleGet(1L).body().getId() == 1L;
    }

    @Test
    public void addAndGet() {
        Chapter c = new Chapter();
        c.setNumber("12");
        c.setTitle("title12");
        Chapter result = addValid(c).body();
        assert result.getId() > 0;
        Chapter res2 = simpleGet(result.getId()).body();
        assert result.getId() == res2.getId();
    }

    @Test
    public void put() {
        Chapter c = new Chapter();
        c.setNumber("12");
        c.setTitle("title12");
        Chapter result = addValid(c).body();
        Event e = new Event();
        e.setText("EEE");
        e.setChapter(result);
        Event returned = addEvent(result.getId(), e).body();
        String newTitle = "modified";
        String newNumber = "modified number";
        result.setTitle(newTitle);
        result.setNumber(newNumber);
        result.setStarter(returned);
        Chapter edited = putValid(result.getId(), result).body();
        edited = simpleGet(edited.getId()).body(); // get to make sure it's been persisted
        assert edited.getNumber().equals(newNumber) && edited.getTitle().equals(newTitle);
    }

    protected void get404(Long id) {
        HttpRequest<String> req = HttpRequest.GET(CHAPTER_PATH + "/" + id);
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
            httpClient.toBlocking().exchange(req);
        });
        System.out.println(e.getStatus());
        assert e.getStatus() == HttpStatus.NOT_FOUND;
    }

    protected HttpResponse<Chapter> simpleGet(Long id) {
        HttpRequest<String> req = HttpRequest.GET(CHAPTER_PATH + "/" + id);
        return httpClient.toBlocking().exchange(req, Chapter.class);
    }

    protected HttpResponse<Chapter> addValid(Chapter c) {
        MutableHttpRequest<Chapter> req = HttpRequest.POST(CHAPTER_PATH, c);
        HttpResponse<Chapter> result = httpClient.toBlocking().exchange(req, Chapter.class);
        assert result.getStatus() == HttpStatus.CREATED;
        return result;
    }

    protected HttpResponse<Event> addEvent(Long id, Event e) {
        Map<String, Object> params = new HashMap<>();
        params.put("cid", id);
        URI uri = UriBuilder.of(EVENT_PATH).expand(params);
        MutableHttpRequest<Event> req = HttpRequest.POST(uri, e);
        HttpResponse<Event> result = httpClient.toBlocking().exchange(req, Event.class);
        assert result.getStatus() == HttpStatus.CREATED;
        return result;
    }

    protected HttpResponse<Chapter> putValid(Long id, Chapter c) {
        MutableHttpRequest<Chapter> req = HttpRequest.PUT(CHAPTER_PATH + "/" + id, c);
        HttpResponse<Chapter> result = httpClient.toBlocking().exchange(req, Chapter.class);
        assert result.getStatus() == HttpStatus.OK;
        return result;
    }

    protected void putInvalid(Long id, Chapter c) {
        MutableHttpRequest<Chapter> req = HttpRequest.PUT(CHAPTER_PATH + "/" + id, c);
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () -> {
            httpClient.toBlocking().exchange(req);
        });
        System.out.println(e.getStatus());
        assert e.getStatus() == HttpStatus.BAD_REQUEST;
    }
}
