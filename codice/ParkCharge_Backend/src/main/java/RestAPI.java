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
            if(credenziali == null || !(credenziali.getUsername().equals(request.params(":username")) && credenziali.getPassword().equals(request.params(":password"))))
                response.status(404);
            else{
                finalJson.put("username",credenziali.getUsername());
                finalJson.put("password",credenziali.getPassword());
                response.status(200);
                response.type("application/json");
            }
            return finalJson;
        }),gson::toJson);

        //Login
        get(baseURL + "/utenti/:username","application/json", ((request, response) -> {
            Utente utente = gestoreUtenti.getUtente(request.params(":username"));
            Map<String,String> finalJson = new HashMap<>();
            if(utente == null)
                response.status(404);
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

        //Modifica prezzi
        get(baseURL + "/costi", "application/json", ((request, response) -> {
            var costi = gestorePagamenti.getCosti();

            response.status(200);
            response.type("application/json");

            return costi;
        }),gson::toJson);

        //monitora prenotazioni
        get(baseURL + "/prenotazioni", "application/json", ((request, response) -> {
            var prenotazioni = gestorePosti.getPrenotazioni();

            response.status(200);
            response.type("application/json");

            return prenotazioni;
        }),gson::toJson);

        //Modifica costi
        put(baseURL + "/costi", "application/json", ((request, response) -> {
            System.out.println(request.body());

            response.status(200);
            response.type("application/json");

            return null;
        }),gson::toJson);


        //Monitora ricariche prenotate
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
                response.status(400);
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
                response.status(400);
                return "Utente non trovato";
            }
        } ),gson::toJson);

        //Prenota Posto Cambiare APIREST!!
        post(baseURL + "/prenotazioni/premium/:username", "application/json", ((request, response) -> {
            Prenotazioni prenotazione = gson.fromJson(request.body(), Prenotazioni.class);
            Utente utente = gestoreUtenti.getUtente(request.params(":username"));
            String created = gestorePosti.creaPrenotazione(prenotazione,utente.getTipo(),"prenota");


            if(created.equals("Successo")){
                response.status(201);
                response.type("application/json");
                return prenotazione;
            }
            else{
                response.status(400);
                return created;
            }
        } ),gson::toJson);

        //Occupa Posto
        post(baseURL + "/prenotazioni/:username", "application/json", ((request, response) -> {
            Prenotazioni prenotazione = gson.fromJson(request.body(), Prenotazioni.class);
            Utente utente = gestoreUtenti.getUtente(request.params(":username"));
            String created = gestorePosti.creaPrenotazione(prenotazione,utente.getTipo(),"occupa");


            if(created.equals("Successo")){
                response.status(201);
                response.type("application/json");
                return prenotazione;
            }
            else{
                response.status(400);
                return created;
            }
        } ),gson::toJson);

    }
}
