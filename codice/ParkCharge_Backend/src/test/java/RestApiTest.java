import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.net.ServerSocket;

import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;

public class RestApiTest {

    private static int port;

    @BeforeAll
    public static void setUp() {
        port = getAvailablePort();
        RestAPI.main(new String[]{String.valueOf(port)});
        awaitInitialization();
    }

    @AfterAll
    public static void tearDown() {
        stop();
    }

    private static int getAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Errore nel trovare porte libere", e);
        }
    }

    protected String getBaseURL() {
        return "http://localhost:" + port + "/api/v1.0";
    }
}
