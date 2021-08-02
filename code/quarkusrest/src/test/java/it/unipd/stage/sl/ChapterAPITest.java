package it.unipd.stage.sl;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import it.unipd.stage.sl.objects.*;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class ChapterAPITest {

    /**
     * Test empty db
     */
    @Test
    public void getEmptyChapters() {
        get404(1829L);
    }

    @Test
    public void add() {
        Chapter c = new Chapter();
        c.number = "12";
        c.title = "title12";
        Chapter result = addValid(c);
        assert result.id > 0;
    }

    @Test
    public void getByIdEmpty() {
        given()
                .accept(ContentType.JSON)
                .pathParam("id", "19999")
                .when().get("/chapter/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    public void addAndGet() {
        Chapter c = new Chapter();
        c.number = "123";
        c.title = "title123";
        Chapter result = addValid(c);
        assert result.id > 0;
        Chapter reported = getValid(result.id);
        assert reported.equals(result);
    }

    @Test
    public void addAndGetAll() {
        Chapter c = new Chapter();
        c.number = "123";
        c.title = "title123";
        Chapter result = addValid(c);
        assert result.id > 0;
        ChapterList reported = getAllValid();
        assert reported.getChapters().contains(result);
    }

    @Test
    public void addAndPut() {
        Chapter c = new Chapter();
        c.number = "123";
        c.title = "title123";
        Chapter result = addValid(c);
        assert result.id > 0;
        Event e = new Event();
        e.text = "EEE";
        e.chapter = result;
        Event returned = EventAPITest.addValid(result.id, e);
        String newTitle = "modified";
        String newNumber = "modified number";
        result.title = newTitle;
        result.number = newNumber;
        // putInvalid(result.id, result); // missing starter => BAD REQUEST
        result.starter = returned;
        Chapter edited = putValid(result.id, result);
        edited = getValid(edited.id); // get to make sure it's been persisted
        assert edited.number.equals(newNumber) && edited.title.equals(newTitle);
    }

    @Test
    public void addAndGetStarter() {
        Chapter c = new Chapter();
        c.number = "123";
        c.title = "title123";
        Chapter result = addValid(c);
        assert result.id > 0;
        Event e = new Event();
        e.text = "EEE";
        e.chapter = result;
        Event returned = EventAPITest.addValid(result.id, e);
        putStarter(result.id, returned.id);
        Event starter = getStarter(result.id);
        assert starter.text.equals(e.text);
    }

    @Test
    public void addAndDelete() {
        Chapter c = new Chapter();
        c.number = "123";
        c.title = "title123";
        Chapter result = addValid(c);
        assert result.id > 0;
        delete(result.id);
    }

    protected static Chapter addValid(Chapter c) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.ANY)
                .body(c)
                .when().post("/chapter")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .extract()
                .as(Chapter.class);
    }

    protected static Chapter putValid(Long id, Chapter c) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.ANY)
                .body(c)
                .pathParam("id", id)
                .when().put("/chapter/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Chapter.class);
    }

    protected static void putInvalid(Long id, Chapter c) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.ANY)
                .body(c)
                .pathParam("id", id)
                .when().put("/chapter/{id}")
                .then()
                .statusCode(400);
    }

    protected static Chapter getValid(Long id) {
        return given()
                .accept(ContentType.JSON)
                .pathParam("id", id)
                .when().get("/chapter/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Chapter.class);
    }

    protected static void get404(Long id) {
        given()
                .accept(ContentType.JSON)
                .pathParam("id", id)
                .when().get("/chapter/{id}")
                .then()
                .statusCode(404);
    }

    protected static Event getStarter(Long id) {
        return given()
                .accept(ContentType.JSON)
                .pathParam("id", id)
                .when().get("/chapter/{id}/start")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Event.class);
    }

    protected static Event putStarter(Long id, Long starter) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.ANY)
                .pathParam("id", id)
                .queryParam("id", starter)
                .when().put("/chapter/{id}/start")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Event.class);
    }

    protected static ChapterList getAllValid() {
        return given()
                .accept(ContentType.JSON)
                .when().get("/chapter")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(ChapterList.class);
    }

    protected static void delete(Long id) {
        given()
                .accept(ContentType.ANY)
                .pathParam("id", id)
                .when().delete("/chapter/{id}")
                .then()
                .statusCode(204); // no content
    }
}