import org.eclipse.paho.client.mqttv3.MqttMessage;

//./mosquitto_pub -u "GestoreIoT" -P "pass1" -t "ParkCharge/EseguiRicarica/1" -p 10011 -h localhost -m '{"target":2,"percentualeRicarica":20}'

// Gestisce i messaggi dai topic ParkCharge/StatoMWBot/ID_MWBot e ParkCharge/StatoRicariche/ID_MWBot
public class MWBotStrategy implements MqttStrategy{
    private final GestoreIoT gestoreIoT;

    public MWBotStrategy(GestoreIoT gestoreIoT) {
        this.gestoreIoT = gestoreIoT;
    }

    @Override
    public void handleMessage(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        System.out.println("Messaggio MWBot ricevuto su " + topic + ": " + payload);

        String idMWBot = topic.split("/")[2];
        String backendTopic = "ParkCharge/StatoRicariche/" + idMWBot;
        gestoreIoT.publish(backendTopic, payload);
    }
}
