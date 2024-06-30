import com.google.gson.Gson;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.Objects;

import static spark.Spark.*;

public class RestAPI {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        get(baseURL + "/costo", "application/json", ((request, response) -> {
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
        put(baseURL + "/costo", "application/json", ((request, response) -> {
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

        //TODO aggiungi swagger
        //richiedi ricarica, ritorna stato posteggio e ricarica utente
        get(baseURL + "/statoUtente", "application/json", ((request, response) -> {
            Ricariche ricaricaUtente = null;
            HashMap<String, Object> returnJson = new HashMap<>();
            response.status(200);
            response.type("application/json");
            var prenotazioneUtente = gestorePosti.getPrenotazioni(request.queryParams("user"));
            if(prenotazioneUtente != null){
                ricaricaUtente = gestoreRicariche.getRicariche(prenotazioneUtente.getId());
            }

            returnJson.put("utente", request.queryParams("user"));

            if(prenotazioneUtente == null){
                returnJson.put("tempo_arrivo", null);
                returnJson.put("id_ricarica", null);
            } else {
                returnJson.put("tempo_arrivo", prenotazioneUtente.getTempo_arrivo().format(formatter));

            }

            if(ricaricaUtente == null){
                returnJson.put("id_ricarica", null);
            } else {
                returnJson.put("id_ricarica", ricaricaUtente.getPrenotazione());
            }

            return returnJson;
        }), gson::toJson);

        //TODO swagger diverso
        //richiedi ricarica
        post(baseURL + "/ricariche", "application/json", ((request, response) -> {
            HashMap<String, String> responseJson = new HashMap<>();
            response.status(200);
            response.type("application/json");
            var prenotazioni = gestorePosti.getPrenotazioni();
            var ricaricheAccettate = gestoreRicariche.getRicariche();
            var user = request.queryParams("user");
            int timeToCharge;
            try{
                timeToCharge = Integer.parseInt(request.queryParams("charge_time"));
            } catch (Exception e){
                response.status(400); //bad request
                responseJson.put("outcome", "bad_request");
                return responseJson;
            }

            if(! EDF.isAccettable(request.queryParams("user"), timeToCharge, prenotazioni, ricaricheAccettate)) {
                responseJson.put("outcome", "not_acceptable");
                return responseJson;
            }

            int prenotazioneId = Objects.requireNonNull(prenotazioni.stream()
                    .filter(p -> p.getUtente().equals(user))
                    .findFirst()
                    .orElse(null)).getId();
            System.out.println(prenotazioneId);

            //add to database
            gestoreRicariche.addRicarica(timeToCharge, prenotazioneId);
            responseJson.put("outcome", "ok");
            return responseJson;
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


        //TODO aggiungi a swagger
        //monitora posti
        get(baseURL + "/posti", "application/json", ((request, response) -> {
            var ricariche = gestorePosti.getStatoPosti();

            response.status(200);
            response.type("application/json");

            return ricariche;
        }),gson::toJson);

        //TODO aggiungi a swagger
        //monitora storico
        get(baseURL + "/storico", "application/json", ((request, response) -> {
            var storico = gestorePagamenti.getStorico();
            ArrayList<HashMap<String, Object>> storicoFiltrato = new ArrayList<>();

            int year = Integer.parseInt(request.queryParams("year"));
            int month = Integer.parseInt(request.queryParams("month"));

            for(var p: storico){
                LocalDateTime tempoArrivo = LocalDateTime.parse((CharSequence) p.get("tempo_arrivo"), formatter);
                if(tempoArrivo.getYear() == year && tempoArrivo.getMonthValue() == month){
                    storicoFiltrato.add(p);
                }
            }

            response.status(200);
            response.type("application/json");

            return storicoFiltrato;
        }),gson::toJson);
    }
}
