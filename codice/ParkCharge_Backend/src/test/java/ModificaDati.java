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

public class ModificaDati{
    Gson gson = new Gson();
    DbUtenti dbUtenti = new DbUtenti();

    String baseURL = "http://localhost:6789/api/v1.0";

    @Test
    public void testModificaUtenteSuccesso(){
        try {
            dbUtenti.update("INSERT INTO Credenziali (username,password) VALUES ('UsernameTest','PasswordTest')");
            dbUtenti.update("INSERT INTO Utente (username,nome,cognome,tipo,carta) VALUES ('UsernameTest','NomeTest','CognomeTest', 1 ,'CartaTest')");

            Map<String, Object> combinedMap = new HashMap<>();
            combinedMap.put("nome", "TestNome");
            combinedMap.put("cognome", "TestCognome");
            combinedMap.put("carta", "TestCarta");

            String utenteTest = gson.toJson(combinedMap);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/utenti/UsernameTest"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(utenteTest))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Utente utente = gson.fromJson(response.body(), Utente.class);
            assertEquals(200, response.statusCode());
            assertEquals(utente.getNome(),"TestNome");
            assertEquals(utente.getCognome(),"TestCognome");
            assertEquals(utente.getCarta(),"TestCarta");

            dbUtenti.update("DELETE FROM Credenziali WHERE username = 'UsernameTest'");
            dbUtenti.update("DELETE FROM Utente WHERE username = 'UsernameTest'");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testModificaUtenteErrore(){
        try {
            dbUtenti.update("INSERT INTO Credenziali (username,password) VALUES ('UsernameTest','PasswordTest')");
            dbUtenti.update("INSERT INTO Utente (username,nome,cognome,tipo,carta) VALUES ('UsernameTest','NomeTest','CognomeTest', 1 ,'CartaTest')");

            Map<String, Object> combinedMap = new HashMap<>();
            combinedMap.put("nome", "TestNome");
            combinedMap.put("cognome", "TestCognome");
            combinedMap.put("carta", "TestCarta");

            String utenteTest = gson.toJson(combinedMap);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/utenti/TestUsername"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(utenteTest))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseString = gson.fromJson(response.body(), String.class);
            assertEquals(400, response.statusCode());
            assertEquals(responseString,"Utente non trovato");

            dbUtenti.update("DELETE FROM Credenziali WHERE username = 'UsernameTest'");
            dbUtenti.update("DELETE FROM Utente WHERE username = 'UsernameTest'");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
}