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

public class DiventaPremium{
    Gson gson = new Gson();
    DbUtenti dbUtenti = new DbUtenti();
    GestorePagamenti gestorePagamenti = new GestorePagamenti();
    GestoreUtenti gestoreUtenti = new GestoreUtenti();

    String baseURL = "http://localhost:6789/api/v1.0";

    @Test
    public void testDiventaPremiumSuccesso(){
        try {
            dbUtenti.update("INSERT INTO Credenziali (username,password) VALUES ('UsernameTest','PasswordTest')");
            dbUtenti.update("INSERT INTO Utente (username,nome,cognome,tipo,carta) VALUES ('UsernameTest','NomeTest','CognomeTest', 2 ,'CartaTest')");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/utenti/tipo/UsernameTest"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int costoPremium = gson.fromJson(response.body(), Integer.class);
            int costoPremiumEffettivo = gestorePagamenti.getCostoPremium();
            Utente utente = gestoreUtenti.getUtente("UsernameTest");
            assertEquals(200, response.statusCode());
            assertEquals(costoPremium,costoPremiumEffettivo);
            assertEquals(utente.getTipo(),1);

            dbUtenti.update("DELETE FROM Credenziali WHERE username = 'UsernameTest'");
            dbUtenti.update("DELETE FROM Utente WHERE username = 'UsernameTest'");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testDiventaPremiumErrore(){
        try {
            dbUtenti.update("INSERT INTO Credenziali (username,password) VALUES ('UsernameTest','PasswordTest')");
            dbUtenti.update("INSERT INTO Utente (username,nome,cognome,tipo,carta) VALUES ('UsernameTest','NomeTest','CognomeTest', 1 ,'CartaTest')");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/utenti/tipo/TestUsername"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.noBody())
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
