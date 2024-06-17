import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import spark.utils.IOUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.*;

public class Login{

    Gson gson = new Gson();

    String baseURL = "http://localhost:6789/api/v1.0";

    @Test
    public void testLoginSuccesso(){
        try {
            URL url = new URL(baseURL + "/credenziali/mrossi/password123");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            assertEquals(200, responseCode);

            String responseBody = IOUtils.toString(connection.getInputStream());
            Map<String, String> jsonResponse = gson.fromJson(responseBody, Map.class);

            assertEquals("mrossi", jsonResponse.get("username"));
            assertEquals("password123", jsonResponse.get("password"));

        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testLoginErrore(){
        try {
            URL url = new URL(baseURL + "/credenziali/mrossi/prova");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            assertEquals(404, responseCode);

        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testGetUtenteSuccesso(){
        try {
            URL url = new URL(baseURL + "/utenti/mrossi");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            assertEquals(200, responseCode);

            String responseBody = IOUtils.toString(connection.getInputStream());
            Map<String, String> jsonResponse = gson.fromJson(responseBody, Map.class);

            assertEquals("mrossi", jsonResponse.get("username"));
            assertEquals("Mario", jsonResponse.get("nome"));
            assertEquals("Rossi", jsonResponse.get("cognome"));
            assertEquals("1", jsonResponse.get("tipo"));
            assertEquals("1234567812345678", jsonResponse.get("carta"));

        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testGetUtenteErrore(){
        try {
            URL url = new URL(baseURL + "/utenti/rossim");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            assertEquals(404, responseCode);

        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
}
