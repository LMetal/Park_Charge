import DataBase.DbUtenti;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

import static spark.Spark.*;

public class RestAPI {
    public static void main(String[] args) {
        // Imposta la porta su cui il server ascolterà
        int port = 4568;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        port(port);

        // Creazione istanza Gson per serializzazione/deserializzazione JSON
        Gson gson = new Gson();
        String baseURL = "/api/v1.0";

        // Creazione istanze dei gestori
        GestoreUtenti gestoreUtenti = new GestoreUtenti();
        GestorePagamenti gestorePagamenti = new GestorePagamenti();
        GestorePosti gestorePosti = new GestorePosti();
        GestoreRicariche gestoreRicariche = new GestoreRicariche();

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST,PUT,DELETE");
            response.header("Access-Control-Allow-Headers", "Content-Type");
        });

        //Login
        // Endpoint per il login
        get(baseURL + "/credenziali/:username/:password","application/json", ((request, response) -> {
            // Recupera le credenziali dell'utente usando il gestore
            Credenziali credenziali = gestoreUtenti.getCredenziali(request.params(":username"),request.params(":password"));
            Map<String,String> finalJson = new HashMap<>();
            if(credenziali == null || !(credenziali.getUsername().equals(request.params(":username")) && credenziali.getPassword().equals(request.params(":password")))){
                response.status(404);  // Se le credenziali non sono corrette, restituisce errore 404
                return "Credenziali dell'utente non trovate";
            }
            else{
                finalJson.put("username",credenziali.getUsername());
                finalJson.put("password",credenziali.getPassword());
                response.status(200); // Se le credenziali sono corrette, restituisce 200 OK
                response.type("application/json");
            }
            return finalJson;
        }),gson::toJson);

        // Endpoint per ottenere informazioni su un utente
        get(baseURL + "/utenti/:username","application/json", ((request, response) -> {
            // Recupera le informazioni dell'utente usando il gestore
            Utente utente = gestoreUtenti.getUtente(request.params(":username"));
            Map<String,String> finalJson = new HashMap<>();
            if(utente == null){
                response.status(404); // Se l'utente non esiste, restituisce errore 404
                return "Utente non trovate";
            }
            else{
                finalJson.put("username",utente.getUsername());
                finalJson.put("nome",utente.getNome());
                finalJson.put("cognome",utente.getCognome());
                finalJson.put("tipo",String.valueOf(utente.getTipo()));
                finalJson.put("carta", utente.getCarta());

                response.status(200); // Se l'utente esiste, restituisce 200 OK
                response.type("application/json");
            }
            return finalJson;
        }),gson::toJson);

        get(baseURL + "/costi", "application/json", ((request, response) -> {
            var costi = gestorePagamenti.getCosti();

            response.status(200);
            response.type("application/json");

            return costi;
        }),gson::toJson);

        get(baseURL + "/prenotazioni", "application/json", ((request, response) -> {
            var prenotazioni = gestorePosti.getPrenotazioni();

            response.status(200);
            response.type("application/json");

            return prenotazioni;
        }),gson::toJson);

        get(baseURL + "/ricariche", "application/json", ((request, response) -> {
            var ricariche = gestoreRicariche.getRicariche();

            response.status(200);
            response.type("application/json");

            return ricariche;
        }),gson::toJson);

        //Crea Utente
        // Endpoint per creare un nuovo utente
        post(baseURL + "/utenti", "application/json", ((request, response) -> {
            // Deserializza il corpo della richiesta in un oggetto Utente e Credenziali
            Utente utente = gson.fromJson(request.body(), Utente.class);
            Credenziali credenziali = gson.fromJson(request.body(), Credenziali.class);
            // Crea un nuovo utente usando il gestore
            String created = gestoreUtenti.creaUtenti(utente,credenziali);

            if(created.equals("Successo")){
                response.status(201); // Se la creazione ha successo, restituisce 201 Created
                response.type("application/json");
                return utente;
            }
            else{
                response.status(400); // Se la creazione fallisce, restituisce errore 400
                return created;
            }
        } ),gson::toJson);

        //Modifica Dati Utente
        // Endpoint per modificare i dati di un utente
        put(baseURL + "/utenti/:username", "application/json", ((request, response) -> {
            // Deserializza il corpo della richiesta in un oggetto Utente
            Utente utente = gson.fromJson(request.body(), Utente.class);
            String username = request.params(":username");
            // Modifica i dati dell'utente usando il gestore
            boolean update = gestoreUtenti.modificaDatiUtente(username,utente);

            if(update){
                response.status(200); // Se la modifica ha successo, restituisce 200 OK
                response.type("application/json");
                return utente;
            }
            else{
                response.status(404); // Se l'utente non esiste, restituisce errore 404
                return "Utente non trovato";
            }
        } ),gson::toJson);

        //Diventa Premium
        // Endpoint per far diventare un utente premium
        put(baseURL + "/utenti/tipo/:username", "application/json", ((request, response) -> {
            String username = request.params(":username");
            // Aggiorna il tipo dell'utente a premium usando il gestore
            boolean update = gestoreUtenti.diventaPremium(username);

            if(update){
                int costoPremium = gestorePagamenti.getCostoPremium();
                response.status(200); // Se l'aggiornamento ha successo, restituisce 200 OK
                response.type("application/json");
                return costoPremium;
            }
            else{
                response.status(404); // Se l'utente non esiste, restituisce errore 404
                return "Utente non trovato";
            }
        } ),gson::toJson);

        //Prenota Posto
        // Endpoint per prenotare un posto per un utente premium
        post(baseURL + "/prenotazioni/premium/:username", "application/json", ((request, response) -> {
            // Deserializza il corpo della richiesta in un oggetto Prenotazioni
            Prenotazioni prenotazione = gson.fromJson(request.body(), Prenotazioni.class);
            // Recupera l'utente usando il gestore
            Utente utente = gestoreUtenti.getUtente(request.params(":username"));
            // Crea una nuova prenotazione usando il gestore
            Prenotazioni nuovaPrenotazione = gestorePosti.creaPrenotazione(prenotazione, utente.getTipo(), "prenota");
            if (nuovaPrenotazione != null) {
                response.status(201);  // Se la prenotazione ha successo, restituisce 201 Created
                response.type("application/json");
                return nuovaPrenotazione;
            } else {
                response.status(400);  // Se non ci sono posti disponibili, restituisce errore 400
                return "Nessun posto disponibile nel periodo richiesto";
            }
        } ),gson::toJson);

        //Occupa Posto
        // Endpoint per occupare un posto
        post(baseURL + "/prenotazioni/:username", "application/json", ((request, response) -> {
            // Deserializza il corpo della richiesta in un oggetto Prenotazioni
            Prenotazioni prenotazione = gson.fromJson(request.body(), Prenotazioni.class);
            // Recupera l'utente usando il gestore
            Utente utente = gestoreUtenti.getUtente(request.params(":username"));
            // Crea una nuova prenotazione usando il gestore
            Prenotazioni nuovaPrenotazione = gestorePosti.creaPrenotazione(prenotazione, utente.getTipo(), "occupa");
            if (nuovaPrenotazione != null) {
                response.status(201);  // Se l'occupazione ha successo, restituisce 201 Created
                response.type("application/json");
                return nuovaPrenotazione;
            } else {
                response.status(400);  // Se non ci sono posti disponibili, restituisce errore 400
                return "Nessun posto disponibile al momento";
            }
        }), gson::toJson);

        //Modifica Prenotazione
        // Endpoint per modificare una prenotazione
        put(baseURL + "/prenotazioni/modifica/:id", "application/json", ((request, response) -> {
            // Deserializza il corpo della richiesta in un oggetto Prenotazioni
            Prenotazioni nuovaPrenotazione = gson.fromJson(request.body(), Prenotazioni.class);
            // Recupera la vecchia prenotazione usando il gestore
            Prenotazioni vecchiaPrenotazione = gestorePosti.getPrenotazione(request.params(":id"));
            // Modifica la prenotazione usando il gestore
            Prenotazioni prenotazione = gestorePosti.modificaPrenotazione(nuovaPrenotazione, vecchiaPrenotazione);
            if (prenotazione != null) {
                response.status(200);  // Se la modifica ha successo, restituisce 200 OK
                response.type("application/json");
                return prenotazione;
            } else {
                response.status(404);  // Se la prenotazione non esiste, restituisce errore 404
                return "Prenotazione non modificata";
            }
        }), gson::toJson);

        //Cancella Prenotazione
        // Endpoint per cancellare una prenotazione
        delete(baseURL + "/prenotazioni/:id", "application/json", ((request, response) -> {
            // Cancella la prenotazione usando il gestore
            boolean delete = gestorePosti.cancellaPrenotazione(request.params(":id"));
            if (delete) {
                response.status(204);  // Se la cancellazione ha successo, restituisce 204 No Content
                response.type("application/json");
                return "Prenotazione eliminata";
            } else {
                response.status(404);  // Se la prenotazione non esiste, restituisce errore 404
                return "Errore nell'eliminazione della prenotazione";
            }
        }), gson::toJson);

        //Aggiorna Prezzi
        // Endpoint per aggiornare i prezzi
        put(baseURL + "/costo", "application/json", ((request, response) -> {
            // Deserializza il corpo della richiesta in un oggetto Costi
            Costi costo = gson.fromJson(request.body(), Costi.class);
            // Aggiorna i prezzi usando il gestore
            boolean update = gestorePagamenti.aggiornaPrezzi(costo);
            if (update) {
                response.status(200);  // Se l'aggiornamento ha successo, restituisce 200 OK
                response.type("application/json");
                return costo;
            } else {
                response.status(400);  // Se l'aggiornamento fallisce, restituisce errore 400
                return "Prezzi non aggiornati";
            }
        }), gson::toJson);

        // Endpoint per ottenere le prenotazioni di un utente specifico
        get(baseURL + "/prenotazioni/:username", "application/json", ((request, response) -> {
            // Recupera le prenotazioni dell'utente usando il gestore
            var prenotazione = gestorePosti.getPrenotazioneUsername(request.params(":username"));
            if (prenotazione != null) {
                response.status(200);  // Se le prenotazioni esistono, restituisce 200 OK
                response.type("application/json");
                return prenotazione;
            } else {
                response.status(404);  // Se l'utente non ha prenotazioni, restituisce errore 404
                return "Nessuna prenotazione dell'utente specificato";
            }
        }), gson::toJson);
    }
}
