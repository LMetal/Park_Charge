import DataBase.DbPrenotazioni;
import DataBase.DbStorico;
import DataBase.DbUtenti;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
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
    DbStorico dbStorico = new DbStorico();

    GestorePagamenti gestorePagamenti = new GestorePagamenti();
    GestoreUtenti gestoreUtenti = new GestoreUtenti();
    GestorePosti gestorePosti = new GestorePosti();
    GestoreRicariche gestoreRicariche = new GestoreRicariche();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/credenziali/mrossi/password123"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Credenziali credenziali = gson.fromJson(response.body(), Credenziali.class);

            assertEquals(200, response.statusCode());
            assertEquals("mrossi", credenziali.getUsername());
            assertEquals("password123", credenziali.getPassword());
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testLoginErroreApi(){
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/credenziali/mrossi/prova"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());

        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testGetUtenteSuccessoApi(){
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/utenti/mrossi"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Utente utente = gson.fromJson(response.body(), Utente.class);

            assertEquals(200, response.statusCode());
            assertEquals("mrossi", utente.getUsername());
            assertEquals("Mario", utente.getNome());
            assertEquals("Rossi", utente.getCognome());
            assertEquals(1, utente.getTipo());
            assertEquals("1234567812345678", utente.getCarta());
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testGetUtenteErroreApi(){
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/utenti/rossim"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(404, response.statusCode());
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
    public void testPrenotaPostoApi(){
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
    public void testPrenotaPostoErroreApi(){
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

    @Test
    public void testOccupaPostoApi(){
        try {
            Map<String, Object> prenotazioneMap = new HashMap<>();
            LocalDateTime now = LocalDateTime.now();
            String tempo_uscita = now.plusHours(2).format(formatter);
            prenotazioneMap.put("tempo_uscita",tempo_uscita);
            prenotazioneMap.put("utente","mrossi");

            String prenotazione = gson.toJson(prenotazioneMap);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/prenotazioni/mrossi"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(prenotazione))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, response.statusCode());

            dbPrenotazioni.update("DELETE FROM Prenotazioni WHERE tempo_uscita = '" + tempo_uscita + "'");

        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testOccupaPostoErroreApi(){
        try {
            ArrayList<String> nows = new ArrayList<>();
            for (int i = 0; i<10; i++){
                LocalDateTime now = LocalDateTime.now();
                String tempo_uscita = now.plusHours(2).format(formatter);
                nows.add(tempo_uscita);
                Map<String, Object> prenotazioneMap = new HashMap<>();
                prenotazioneMap.put("tempo_uscita",tempo_uscita);
                prenotazioneMap.put("utente","mrossi");

                String prenotazione = gson.toJson(prenotazioneMap);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(baseURL + "/prenotazioni/mrossi"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(prenotazione))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(201, response.statusCode());
            }

            Map<String, Object> prenotazioneMap = new HashMap<>();
            LocalDateTime now = LocalDateTime.now();
            String tempo_uscita = now.plusHours(2).format(formatter);
            prenotazioneMap.put("tempo_uscita",tempo_uscita);
            prenotazioneMap.put("utente","mrossi");

            String prenotazione = gson.toJson(prenotazioneMap);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/prenotazioni/mrossi"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(prenotazione))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedResponse = "Nessun posto disponibile nel periodo richiesto";
            String actualResponse = gson.fromJson(response.body(), String.class);
            assertEquals(400, response.statusCode());
            assertEquals(expectedResponse, actualResponse);


            for (int i = 0; i < 10; i++)
                dbPrenotazioni.update("DELETE FROM Prenotazioni WHERE tempo_uscita = '" + nows.get(i) + "'");

        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testModificaPrenotazioneApi(){
        try {
            dbPrenotazioni.update("INSERT INTO Prenotazioni (tempo_arrivo, tempo_uscita, utente, posto) VALUES ('2024-06-12 08:00:00', '2024-06-12 09:00:00', 'utenteTest', 11)");

            Map<String, Object> modificaPrenotazioneMap = new HashMap<>();
            modificaPrenotazioneMap.put("tempo_arrivo", "2024-06-12 09:00:00");
            modificaPrenotazioneMap.put("tempo_uscita", "2024-06-12 10:00:00");

            String prenotazioneModificata = gson.toJson(modificaPrenotazioneMap);

            ArrayList<Prenotazioni> prenotazioni = gestorePosti.getPrenotazioni();
            Prenotazioni prenotazioneVecchia = null;
            for (Prenotazioni prenotazione : prenotazioni){
                if(prenotazione.getUtente().equals("utenteTest")){
                    prenotazioneVecchia = new Prenotazioni(prenotazione.getId(),prenotazione.getTempo_arrivo(),prenotazione.getTempo_uscita(),prenotazione.getUtente(),prenotazione.getPosto());
                }
            }

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/prenotazioni/modifica/" + prenotazioneVecchia.getId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(prenotazioneModificata))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedResponse = "Prenotazione modificata";
            String actualResponse = gson.fromJson(response.body(), String.class);

            assertEquals(200, response.statusCode());
            assertEquals(expectedResponse, actualResponse);

            dbPrenotazioni.update("DELETE FROM Prenotazioni WHERE id = '" + prenotazioneVecchia.getId() + "'");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testModificaPrenotazioneErroreApi(){
        try {
            Map<String, Object> modificaPrenotazioneMap = new HashMap<>();
            modificaPrenotazioneMap.put("tempo_arrivo", "2024-06-01 08:00:00");
            modificaPrenotazioneMap.put("tempo_uscita", " 2024-06-01 10:00:00");

            String prenotazioneModificata = gson.toJson(modificaPrenotazioneMap);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/prenotazioni/modifica/9999"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(prenotazioneModificata))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedResponse = "Prenotazione non modificata";
            String actualResponse = gson.fromJson(response.body(), String.class);

            assertEquals(400, response.statusCode());
            assertEquals(expectedResponse, actualResponse);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testEliminaPrenotazioneApi(){
        dbPrenotazioni.update("INSERT INTO Prenotazioni (id, tempo_arrivo, tempo_uscita, utente, posto) VALUES ('998','2024-06-12 08:00:00', '2024-06-12 09:00:00', 'utente1', 1)");
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/prenotazioni/998"))
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String expectedResponse = "Prenotazione eliminata";
            String actualResponse = gson.fromJson(response.body(), String.class);

            assertEquals(200, response.statusCode());
            assertEquals(expectedResponse, actualResponse);
        }catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testEliminaPrenotazioneApiErrore(){
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/prenotazioni/998"))
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String expectedResponse = "Errore nell'eliminazione della prenotazione";
            String actualResponse = gson.fromJson(response.body(), String.class);

            assertEquals(400, response.statusCode());
            assertEquals(expectedResponse, actualResponse);
        }catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testAggiornaPrezziApi(){
        try {
            ArrayList<HashMap<String, Object>> prezziIniziali = gestorePagamenti.getCosti();

            Map<String, Object> aggiornaPrezzoMap = new HashMap<>();
            aggiornaPrezzoMap.put("costo_posteggio", "10");
            aggiornaPrezzoMap.put("costo_ricarica", "12");
            aggiornaPrezzoMap.put("penale", "30");
            aggiornaPrezzoMap.put("costo_premium", "50");

            String prezzoAggiornato = gson.toJson(aggiornaPrezzoMap);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + "/costo"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(prezzoAggiornato))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedResponse = "Prezzi aggiornati";
            String actualResponse = gson.fromJson(response.body(), String.class);

            assertEquals(200, response.statusCode());
            assertEquals(expectedResponse,actualResponse);


            dbStorico.update("UPDATE Costi SET costo_posteggio = \"" + prezziIniziali.get(0).get("costo_posteggio") + "\", costo_ricarica = \"" + prezziIniziali.get(0).get("costo_ricarica") + "\", penale = \"" + prezziIniziali.get(0).get("penale") + "\", costo_premium = \"" + prezziIniziali.get(0).get("costo_premium") + "\" WHERE id = '1'");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testStatoUtente() throws URISyntaxException, IOException, InterruptedException {
        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseURL + "/statoUtente?user=lverdi"))
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HashMap<String, Object> statoUtente = gson.fromJson(response.body(), type);

        assertEquals("lverdi", statoUtente.get("utente"));
        assertEquals("si", statoUtente.get("occupazione_iniziata"));
        assertEquals("2024-06-01 09:00:00", statoUtente.get("tempo_arrivo"));
        assertEquals("si", statoUtente.get("caricando"));
        assertEquals(2.0, statoUtente.get("id_prenotazione"));


        //nuovo utente
        gestoreUtenti.creaUtenti(new Utente("nom", "cogn", "prova", 1, "1234"), new Credenziali("prova", "pass123"));
        request = HttpRequest.newBuilder()
                .uri(new URI(baseURL + "/statoUtente?user=prova"))
                .GET()
                .header("Content-Type", "application/json")
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        statoUtente = gson.fromJson(response.body(), type);

        assertEquals("prova", statoUtente.get("utente"));
        assertEquals("no", statoUtente.get("occupazione_iniziata"));
        assertEquals("null", statoUtente.get("tempo_arrivo"));
        assertEquals("no", statoUtente.get("caricando"));
        assertEquals("null", statoUtente.get("id_prenotazione"));


        LocalDateTime now = LocalDateTime.now();
        //nuova prenotazione
        gestorePosti.creaPrenotazione(new Prenotazioni(1000, now, now.plusHours(1), "prova", 1), 1, "occupa");

        request = HttpRequest.newBuilder()
                .uri(new URI(baseURL + "/statoUtente?user=prova"))
                .GET()
                .header("Content-Type", "application/json")
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        statoUtente = gson.fromJson(response.body(), type);

        assertNotNull(gestorePosti.getPrenotazioni().stream().filter(p -> p.getUtente().equals("prova")));
        assertEquals("prova", statoUtente.get("utente"));
        assertEquals("si", statoUtente.get("occupazione_iniziata"));
        assertEquals(now.truncatedTo(ChronoUnit.SECONDS), LocalDateTime.parse((CharSequence) statoUtente.get("tempo_arrivo"), formatter));
        assertEquals("no", statoUtente.get("caricando"));

        dbUtenti.update("DELETE FROM Credenziali WHERE username = 'prova'");
        dbUtenti.update("DELETE FROM Utente WHERE username = 'prova'");
        dbPrenotazioni.update("DELETE FROM Prenotazioni WHERE Utente = 'prova'");
    }

    @Test
    public void testStatoUtenteErrato() throws URISyntaxException, IOException, InterruptedException {
        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseURL + "/statoUtente?user=userInesistente"))
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HashMap<String, Object> statoUtente = gson.fromJson(response.body(), type);

        assertEquals("null", statoUtente.get("utente"));
        assertEquals("no", statoUtente.get("occupazione_iniziata"));
        assertEquals("null", statoUtente.get("tempo_arrivo"));
        assertEquals("no", statoUtente.get("caricando"));
        assertEquals("null", statoUtente.get("id_prenotazione"));
    }

    //@Test
    public void testRichiediRicarica() throws URISyntaxException, IOException, InterruptedException {
        //setup utente, prenotazione
        LocalDateTime now = LocalDateTime.now();
        gestoreUtenti.creaUtenti(new Utente("nom", "cogn", "prova", 1, "1234"), new Credenziali("prova", "pass123"));
        gestorePosti.creaPrenotazione(new Prenotazioni(1000, now, now.plusHours(1), "prova", 1), 1, "occupa");

        //nuova ricarica
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(new URI(baseURL + "/ricariche?user=prova&charge_time=30"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("ok"));
        assertNotNull(gestoreRicariche.getRicariche().stream().filter(r -> r.getPrenotazione() == 1000));
        //assertEquals();

        dbUtenti.update("DELETE FROM Credenziali WHERE username = 'prova'");
        dbUtenti.update("DELETE FROM Utente WHERE username = 'prova'");
        dbPrenotazioni.update("DELETE FROM Prenotazioni WHERE Utente = 'prova'");
    }

    @Test
    public void testMonitoraPostiAmministratore() throws URISyntaxException, IOException, InterruptedException {
        Type type = new TypeToken<List<Map<String, Object>>>(){}.getType();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseURL + "/posti"))
                .GET()
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Map<String, String>> stato = gson.fromJson(response.body(), type);

        assertEquals(10, stato.size());

        assertEquals(1.0, stato.get(0).get("id"));
        assertEquals(0.0, stato.get(0).get("disponibilita"));

        assertEquals(2.0, stato.get(1).get("id"));
        assertEquals(0.0, stato.get(1).get("disponibilita"));

        assertEquals(10.0, stato.get(9).get("id"));
        assertEquals(1.0, stato.get(9).get("disponibilita"));
    }


    @Test
    public void testMonitoraPrenotazioneAmministratore() throws URISyntaxException, IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseURL + "/prenotazioni"))
                .GET()
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<List<Prenotazioni>>() {}.getType();
        List<Prenotazioni> prenotazioniList = gson.fromJson(response.body(), listType);

        assertEquals(9, prenotazioniList.size());
    }
}
