package it.unipd.stage.sl;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import it.unipd.stage.sl.objects.Chapter;
import it.unipd.stage.sl.objects.Event;
import it.unipd.stage.sl.objects.EventList;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class EventAPITest {

    /**
     * Test empty db
     */
    @Test
    public void getEmptyEvents() {
        given()
                .pathParam("id", "19999")
                .when().get("/chapter/{id}/event")
                .then()
                .statusCode(404);
    }

    @Test
    public void add() {
        Chapter c = new Chapter();
        c.number = "12";
        c.title = "title12";
        Chapter res = ChapterAPITest.addValid(c);
        assert res.id > 0;
        Event e = new Event();
        e.chapter = res;
        e.text = "my text event";
        Event resE = addValid(res.id, e);
        assert resE.id > 0;
    }

    @Test
    public void getByIdEmpty() {
        given()
                .accept(ContentType.JSON)
                .pathParam("id", "19999")
                .pathParam("eventid", "1222")
                .when().get("/chapter/{id}/event/{eventid}")
                .then()
                .statusCode(404);
    }

    @Test
    public void addAndGet() {
        Chapter c = new Chapter();
        c.number = "123";
        c.title = "title123";
        Chapter res = ChapterAPITest.addValid(c);
        assert res.id > 0;
        Event e = new Event();
        e.chapter = res;
        e.text = "my text event";
        Event resE = addValid(res.id, e);
        Event reported = getValid(e.chapter.id, resE.id);
        assert reported.equals(resE);
    }

    @Test
    public void addAndPut() {
        Chapter c = new Chapter();
        c.number = "123";
        c.title = "title123";
        Chapter res = ChapterAPITest.addValid(c);
        assert res.id > 0;
        Event e = new Event();
        e.chapter = res;
        e.text = "my text event";
        Event resE = addValid(res.id, e);
        String newText = "mynewtext";
        resE.text = newText;
        Event result = putValid(resE);
        assert result.text.equals(newText);
    }

    @Test
    public void addAndGetAll() {
        Chapter c = new Chapter();
        c.number = "123";
        c.title = "title123";
        Chapter res = ChapterAPITest.addValid(c);
        assert res.id > 0;
        Event e = new Event();
        e.chapter = res;
        e.text = "my text event";
        Event resE = addValid(res.id, e);
        EventList reported = getAllValid(e.chapter.id);
        assert reported.getEvents().contains(resE);
    }

    @Test
    public void deleteSimple() {
        Chapter c = new Chapter();
        c.number = "123";
        c.title = "title123";
        Chapter res = ChapterAPITest.addValid(c);
        assert res.id > 0;
        Event e = new Event();
        e.chapter = res;
        e.text = "my text event";
        Event resE = addValid(res.id, e);
        delete(res.id, resE.id, 204, false, false);
    }

    @Test
    public void delete() {
        Chapter c = new Chapter();
        c.number = "123";
        c.title = "title123";
        Chapter res = ChapterAPITest.addValid(c);
        assert res.id > 0;
        Event e = new Event();
        e.chapter = res;
        e.text = "my text event";
        Event resE = addValid(res.id, e);
        // set as starter
        ChapterAPITest.putStarter(res.id, resE.id);
        // test failing delete
        delete(res.id, resE.id, 400, false, false);
        // try force
        delete(res.id, resE.id, 204, false, true);
        // readd
        resE = addValid(res.id, e);
        // readd starter
        ChapterAPITest.putStarter(res.id, resE.id);
        Chapter cc = ChapterAPITest.getValid(res.id);
        assert cc.id > 0 && cc.starter.id.equals(resE.id);
        // delete cascade
        delete(res.id, resE.id, 204, true, false);
        ChapterAPITest.get404(res.id);
    }

    protected static Event addValid(Long chapterId, Event e) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.ANY)
                .body(e)
                .pathParam("id", chapterId)
                .when().post("/chapter/{id}/event")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract()
                .as(Event.class);
    }

    protected static Event putValid(Event e) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.ANY)
                .body(e)
                .pathParam("id", e.chapter.id)
                .pathParam("eventid", e.id)
                .when().put("/chapter/{id}/event/{eventid}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Event.class);
    }

    protected static Event getValid(Long chapterId, Long eventId) {
        return given()
                .accept(ContentType.JSON)
                .pathParam("id", chapterId)
                .pathParam("eventid", eventId)
                .when().get("/chapter/{id}/event/{eventid}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Event.class);
    }

    protected static void getInvalid(Long chapterId, Long eventId) {
        given()
                .accept(ContentType.JSON)
                .pathParam("id", chapterId)
                .pathParam("eventid", eventId)
                .when().get("/chapter/{id}/event/{eventid}")
                .then()
                .statusCode(404);
    }

    protected static EventList getAllValid(Long chapterId) {
        return given()
                .accept(ContentType.JSON)
                .pathParam("id", chapterId)
                .when().get("/chapter/{id}/event")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(EventList.class);
    }

    protected static void delete(Long chapterId, Long eventId, int expectedStatus, boolean cascade, boolean force) {
        given()
                .accept(ContentType.JSON)
                .pathParam("id", chapterId)
                .pathParam("eventid", eventId)
                .queryParam("cascade", cascade)
                .queryParam("force", force)
                .when().delete("/chapter/{id}/event/{eventid}")
                .then()
                .statusCode(expectedStatus);
    }
}