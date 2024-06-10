import DataBase.DbUtenti;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class RestAPI {
    public static void main(String[] args) {
        port(4568);
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

        get(baseURL + "/credenziali/:username/:password","application/json", ((request, response) -> {
            Credenziali credenziali = gestoreUtenti.getCredenziali(request.params(":username"),request.params(":password"));
            Map<String,String> finalJson = new HashMap<>();
            if(credenziali == null)
                response.status(404);
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
    }
}
