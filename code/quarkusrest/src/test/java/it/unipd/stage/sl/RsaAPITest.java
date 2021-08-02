package it.unipd.stage.sl;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import it.unipd.stage.sl.controllers.RsaController;
import it.unipd.stage.sl.objects.KeyHolder;
import it.unipd.stage.sl.objects.PrivateKey;
import it.unipd.stage.sl.objects.PublicKey;
import it.unipd.stage.sl.objects.RsaResult;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(RsaController.class)
public class RsaAPITest {

    /**
     * Test key generation with no values provided
     */
    @Test
    public void testGetKeys() {
        KeyHolder result = given()
                .when().get()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(KeyHolder.class);
        // System.out.println(result);
        assert result.getPrivateKey().equals(new PrivateKey("10460274474002053", "1987201354784969"));
        assert result.getPublicKey().equals(new PublicKey("10460274474002053", "5223497001888909"));
    }

    /**
     * Test key generation with no values provided
     */
    @Test
    public void testGetKeysWithParams() {
        KeyHolder result = given()
                .queryParam("length", "80")
                .queryParam("seed", "1739249")
                .when().get()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(KeyHolder.class);
        // System.out.println(result);
        assert result.getPrivateKey().equals(new PrivateKey("673583590782593190824629", "25288912844824492449485"));
        assert result.getPublicKey().equals(new PublicKey("673583590782593190824629", "47429"));
    }

    /**
     * Test encryption
     */
    @Test
    public void testEncrypt() {
        RsaResult result = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(new TextEncrypt("ciao", new PublicKey("10460274474002053", "5223497001888909")))
                .when().post("/encrypt")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(RsaResult.class);
        // System.out.println(result);
        assert result.getMessage().equals("DOERSW4A1Q==");
    }

    @Test
    public void testEncryptMissingText() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(new PublicKey("10460274474002053", "5223497001888909"))
                .when().post("/encrypt")
                .then()
                .statusCode(400);
    }

    /**
     * Test decryption
     */
    @Test
    public void testDecrypt() {
        RsaResult result = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(new TextDecrypt("DOERSW4A1Q==", new PublicKey("10460274474002053", "5223497001888909"), new PrivateKey("10460274474002053", "1987201354784969")))
                .when().post("/decrypt")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(RsaResult.class);
        // System.out.println(result);
        assert result.getMessage().equals("ciao");
    }
}