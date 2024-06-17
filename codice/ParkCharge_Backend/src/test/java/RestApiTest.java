import DataBase.DbPrenotazioni;
import DataBase.DbUtenti;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.utils.IOUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static spark.Spark.stop;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class RestApiTest {
    Gson gson = new Gson();

    String baseURL = "http://localhost:6789/api/v1.0";

    DbUtenti dbUtenti = new DbUtenti();
    DbPrenotazioni dbPrenotazioni = new DbPrenotazioni();

    GestorePagamenti gestorePagamenti = new GestorePagamenti();
    GestoreUtenti gestoreUtenti = new GestoreUtenti();

    @BeforeAll
    public static void setUp(){
        RestAPI.main(new String[]{"6789"});
    }

    @AfterAll
    public static void tearDown() {
        stop();
    }

    @Test
    public void testLoginSuccessoApi(){
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
    public void testLoginErroreApi(){
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
    public void testGetUtenteSuccessoApi(){
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
    public void testGetUtenteErroreApi(){
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

    @Test
    public void testModificaUtenteSuccessoApi(){
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
    public void testModificaUtenteErroreApi(){
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

    @Test
    public void testCreaUtenteSuccessoApi(){
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
    public void testCreaUtenteErroreApi(){
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

    @Test
    public void testDiventaPremiumSuccessoApi(){
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
    public void testDiventaPremiumErroreApi(){
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

    @Test
    public void testCreaPrenotazioneApi(){
        try {
            Map<String, Object> prenotazioneMap = new HashMap<>();
            prenotazioneMap.put("tempo_arrivo","2024-06-12 08:00:00");
            prenotazioneMap.put("tempo_uscita","2024-06-12 09:00:00");
            prenotazioneMap.put("utente","mrossi");

            String prenotazione = gson.toJson(prenotazioneMap);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/prenotazioni/premium/mrossi"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(prenotazione))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, response.statusCode());

            dbPrenotazioni.update("DELETE FROM Prenotazioni WHERE tempo_arrivo = '2024-06-12 08:00:00'");

        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testCreaPrenotazioneErroreApi(){
        try {
            for (int i = 0; i<10; i++){
                Map<String, Object> prenotazioneMap = new HashMap<>();
                prenotazioneMap.put("tempo_arrivo","2024-06-12 08:00:00");
                prenotazioneMap.put("tempo_uscita","2024-06-12 09:00:00");
                prenotazioneMap.put("utente","mrossi");

                String prenotazione = gson.toJson(prenotazioneMap);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(baseURL + "/prenotazioni/premium/mrossi"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(prenotazione))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(201, response.statusCode());
            }

            Map<String, Object> prenotazioneMap = new HashMap<>();
            prenotazioneMap.put("tempo_arrivo","2024-06-12 08:00:00");
            prenotazioneMap.put("tempo_uscita","2024-06-12 09:00:00");
            prenotazioneMap.put("utente","mrossi");

            String prenotazione = gson.toJson(prenotazioneMap);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/prenotazioni/premium/mrossi"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(prenotazione))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedResponse = "Nessun posto disponibile nel periodo richiesto";
            String actualResponse = gson.fromJson(response.body(), String.class);
            assertEquals(400, response.statusCode());
            assertEquals(expectedResponse, actualResponse);


            for (int i = 0; i < 10; i++)
                dbPrenotazioni.update("DELETE FROM Prenotazioni WHERE tempo_arrivo = '2024-06-12 08:00:00'");

        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
}
