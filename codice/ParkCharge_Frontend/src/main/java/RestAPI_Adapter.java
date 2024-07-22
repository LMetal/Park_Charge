import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RestAPI_Adapter {
    private static final String baseURL = "http://localhost:4568/api/v1.0";
    static Gson gson = new Gson();

    public static ArrayList<HashMap<String,Object>> get(String resource) {
        HttpURLConnection con = null;
        ArrayList<HashMap<String,Object>> prenotazioni;

        try {
            URL url = new URL(baseURL + resource);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            int responseCode = con.getResponseCode();
            if(responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Gson gson = new Gson();

                Type type = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
                prenotazioni = gson.fromJson(String.valueOf(response), type);
                return prenotazioni;
            }
            else
                return null;

        }catch (Exception e){
            e.printStackTrace();
            //return null;
        }
        finally {
            if(con != null)
                con.disconnect();
        }
        return null;
    }

    public static boolean put(String resource, Map<String, Object> data) {

        String dataJson = gson.toJson(data);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(baseURL + resource))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(dataJson))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() != 200) return false;
        } catch (Exception e) {
            return false;
        }

        return true;
    }
    public static ArrayList<HashMap<String, Object>> bodyToMap(String body) {
        System.out.println("HEREEEEEEEE");

        Type type = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
        return gson.fromJson(body, type);
    }
}
