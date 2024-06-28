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
        int port = 4568;

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        port(port);
        Gson gson = new Gson();
        String baseURL = "/api/v1.0";


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
        get(baseURL + "/credenziali/:username/:password","application/json", ((request, response) -> {
            Credenziali credenziali = gestoreUtenti.getCredenziali(request.params(":username"),request.params(":password"));
            Map<String,String> finalJson = new HashMap<>();
            if(credenziali == null || !(credenziali.getUsername().equals(request.params(":username")) && credenziali.getPassword().equals(request.params(":password")))){
                response.status(404);
                return "Credenziali dell'utente non trovate";
            }
            else{
                finalJson.put("username",credenziali.getUsername());
                finalJson.put("password",credenziali.getPassword());
                response.status(200);
                response.type("application/json");
            }
            return finalJson;
        }),gson::toJson);

        get(baseURL + "/utenti/:username","application/json", ((request, response) -> {
            Utente utente = gestoreUtenti.getUtente(request.params(":username"));
            Map<String,String> finalJson = new HashMap<>();
            if(utente == null){
                response.status(404);
                return "Utente non trovate";
            }
            else{
                finalJson.put("username",utente.getUsername());
                finalJson.put("nome",utente.getNome());
                finalJson.put("cognome",utente.getCognome());
                finalJson.put("tipo",String.valueOf(utente.getTipo()));
                finalJson.put("carta", utente.getCarta());

                response.status(200);
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
        post(baseURL + "/utenti", "application/json", ((request, response) -> {
            Utente utente = gson.fromJson(request.body(), Utente.class);
            Credenziali credenziali = gson.fromJson(request.body(), Credenziali.class);
            String created = gestoreUtenti.creaUtenti(utente,credenziali);

            if(created.equals("Successo")){
                response.status(201);
                response.type("application/json");
                return utente;
            }
            else{
                response.status(400);
                return created;
            }
        } ),gson::toJson);

        //Modifica Dati Utente
        put(baseURL + "/utenti/:username", "application/json", ((request, response) -> {
            Utente utente = gson.fromJson(request.body(), Utente.class);
            String username = request.params(":username");
            boolean update = gestoreUtenti.modificaDatiUtente(username,utente);

            if(update){
                response.status(200);
                response.type("application/json");
                return utente;
            }
            else{
                response.status(404);
                return "Utente non trovato";
            }
        } ),gson::toJson);

        //Diventa Premium
        put(baseURL + "/utenti/tipo/:username", "application/json", ((request, response) -> {
            String username = request.params(":username");
            boolean update = gestoreUtenti.diventaPremium(username);

            if(update){
                int costoPremium = gestorePagamenti.getCostoPremium();
                response.status(200);
                response.type("application/json");
                return costoPremium;
            }
            else{
                response.status(404);
                return "Utente non trovato";
            }
        } ),gson::toJson);

        //Prenota Posto
        post(baseURL + "/prenotazioni/premium/:username", "application/json", ((request, response) -> {
            Prenotazioni prenotazione = gson.fromJson(request.body(), Prenotazioni.class);
            Utente utente = gestoreUtenti.getUtente(request.params(":username"));
            Prenotazioni nuovaPrenotazione = gestorePosti.creaPrenotazione(prenotazione,utente.getTipo(),"prenota");

            if(nuovaPrenotazione != null){
                response.status(201);
                response.type("application/json");
                return nuovaPrenotazione;
            }
            else{
                response.status(400);
                return "Nessun posto disponibile nel periodo richiesto";
            }
        } ),gson::toJson);

        //Occupa Posto
        post(baseURL + "/prenotazioni/:username", "application/json", ((request, response) -> {
            Prenotazioni prenotazione = gson.fromJson(request.body(), Prenotazioni.class);
            Utente utente = gestoreUtenti.getUtente(request.params(":username"));
            Prenotazioni nuovaPrenotazione = gestorePosti.creaPrenotazione(prenotazione,utente.getTipo(),"occupa");


            if(nuovaPrenotazione != null){
                response.status(201);
                response.type("application/json");
                return nuovaPrenotazione;
            }
            else{
                response.status(400);
                return "Nessun posto disponibile al momento";
            }
        } ),gson::toJson);

        //Modifica Prenotazione
        put(baseURL + "/prenotazioni/modifica/:id", "application/json", ((request, response) -> {
            Prenotazioni nuovaPrenotazione = gson.fromJson(request.body(), Prenotazioni.class);
            Prenotazioni vecchiaPrenotazione = gestorePosti.getPrenotazione(request.params(":id"));
            Prenotazioni prenotazione = gestorePosti.modificaPrenotazione(nuovaPrenotazione,vecchiaPrenotazione);

            if(prenotazione != null){
                response.status(200);
                response.type("application/json");
                return prenotazione;
            }
            else{
                response.status(404);
                return "Prenotazione non modificata";
            }
        } ),gson::toJson);

        //Cancella Prenotazione
        delete(baseURL + "/prenotazioni/:id", "application/json", ((request, response) -> {
            boolean delete = gestorePosti.cancellaPrenotazione(request.params(":id"));

            if(delete){
                response.status(204);
                response.type("application/json");
                return "Prenotazione eliminata";
            }
            else{
                response.status(404);
                return "Errore nell'eliminazione della prenotazione";
            }
        } ),gson::toJson);

        //Aggiorna Prezzi
        put(baseURL + "/costo", "application/json", ((request, response) -> {
            Costi costo = gson.fromJson(request.body(), Costi.class);
            boolean update = gestorePagamenti.aggiornaPrezzi(costo);

            if(update){
                response.status(200);
                response.type("application/json");
                return costo;
            }
            else{
                response.status(400);
                return "Prezzi non aggiornati";
            }
        } ),gson::toJson);

        get(baseURL + "/prenotazioni/:username", "application/json", ((request, response) -> {
            var prenotazione = gestorePosti.getPrenotazioneUsername(request.params(":username"));

            if(prenotazione != null){
                response.status(200);
                response.type("application/json");
                return prenotazione;
            }
            else{
                response.status(404);
                return "Nessuna prenotazione dell'utente specificato";
            }
        } ),gson::toJson);
    }
}
