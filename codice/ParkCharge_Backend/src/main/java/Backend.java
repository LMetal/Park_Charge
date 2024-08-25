import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.*;

public class Backend {
    static final public String username = "BackEnd";
    static final public String password = "pass3";
    private static final String brokerUrl = "tcp://localhost:10011";
    private static final String topicSensor = "ParkCharge/StatoPosti/#";
    private static final String topicPagamento = "ParkCharge/Pagamento/#";
    private static final String topicRicariche = "ParkCharge/StatoRicariche/#";
    private static MqttClient client;

    public static void start() {
        GestorePosti gestorePosti = new GestorePosti();
        GestoreRicariche gestoreRicariche = new GestoreRicariche();

        try {
            client = new MqttClient(brokerUrl, MqttClient.generateClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setAutomaticReconnect(true);

            client.connect(options);

            // Sottoscrizione ai topic
            client.subscribe(topicSensor, gestorePosti::statoPosti);
            client.subscribe(topicRicariche, gestoreRicariche::statoRicariche);
            client.subscribe(topicPagamento, Backend::stampaPagamento);

            System.out.println("Sottoscritto ai topic");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Metodo per la stampa del pagamento completato
    private static void stampaPagamento(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        System.out.println("Messaggio Device ricevuto su " + topic + ": " + payload);

        Gson gson = new Gson();
        JsonObject jsonPayload = gson.fromJson(payload, JsonObject.class);

        String username = jsonPayload.get("username").getAsString();
        float totale = jsonPayload.get("totale").getAsFloat();

        System.out.println("L'utente " + username + " ha completato il pagamento di " + totale + " euro");
    }

    // Metodo per tutti i publish del Backend
    public static void publish(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(1);
            client.publish(topic, mqttMessage);
            System.out.println("Messaggio pubblicato su " + topic + ": " + message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
