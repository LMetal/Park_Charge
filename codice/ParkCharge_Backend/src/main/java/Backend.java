import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.*;

public class Backend {
    final public String username = "BackEnd";
    final public String password = "pass3";
    private final String brokerUrl = "tcp://localhost:10011";
    private final String topicSensor = "ParkCharge/StatoPosti/#";
    private final String topicPagamento = "ParkCharge/Pagamento/#";
    private MqttClient client;

    public void start() {
        GestorePosti gestorePosti = new GestorePosti();

        try {
            client = new MqttClient(brokerUrl, MqttClient.generateClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());

            client.connect(options);

            // Sottoscrizione ai topic
            client.subscribe(topicSensor, gestorePosti::statoPosti);
            client.subscribe(topicPagamento, this::stampaPagamento);

            System.out.println("Sottoscritto ai topic");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void stampaPagamento(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        System.out.println("Messaggio Device ricevuto su " + topic + ": " + payload);

        Gson gson = new Gson();
        JsonObject jsonPayload = gson.fromJson(payload, JsonObject.class);

        String username = jsonPayload.get("username").getAsString();
        int totale = jsonPayload.get("totale").getAsInt();

        System.out.println("L'utente " + username + " ha completato il pagamento di " + totale + " euro");
    }

    public void publish(String topic, String message) {
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
