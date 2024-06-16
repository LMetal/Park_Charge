import DataBase.DbUtenti;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
public class CreaUtente extends RestApiTest{

    Gson gson = new Gson();
    DbUtenti dbUtenti = new DbUtenti();

    @Test
    public void testCreaUtenteSuccesso(){
        String baseURL = getBaseURL();
        try {
            Utente utente = new  Utente("NomeTest","CognomeTest", "UsernameTest",1,"CartaTest");
            Credenziali credenziali = new Credenziali("UsernameTest","PasswordTest");

            Map<String, Object> combinedMap = new HashMap<>();
            combinedMap.put("nome", utente.getNome());
            combinedMap.put("cognome", utente.getCognome());
            combinedMap.put("username", utente.getUsername());
            combinedMap.put("tipo", utente.getTipo());
            combinedMap.put("carta", utente.getCarta());
            combinedMap.put("password", credenziali.getPassword());

            String utenteTest = gson.toJson(combinedMap);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/utenti"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(utenteTest))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, response.statusCode());

            dbUtenti.update("DELETE FROM Credenziali WHERE username = 'UsernameTest'");
            dbUtenti.update("DELETE FROM Utente WHERE username = 'UsernameTest'");

        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testCreaUtenteErrore(){
        String baseURL = getBaseURL();
        try {
            Utente utente = new  Utente("NomeTest","CognomeTest", "mrossi",1,"CartaTest");
            Credenziali credenziali = new Credenziali("UsernameTest","PasswordTest");

            Map<String, Object> combinedMap = new HashMap<>();
            combinedMap.put("nome", utente.getNome());
            combinedMap.put("cognome", utente.getCognome());
            combinedMap.put("username", utente.getUsername());
            combinedMap.put("tipo", utente.getTipo());
            combinedMap.put("carta", utente.getCarta());
            combinedMap.put("password", credenziali.getPassword());

            String utenteTest = gson.toJson(combinedMap);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/utenti"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(utenteTest))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(400, response.statusCode());
            String expectedResponse = "Username gia' esistente";
            String actualResponse = gson.fromJson(response.body(), String.class);

            assertEquals(expectedResponse, actualResponse);

        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
}