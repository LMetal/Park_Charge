import org.eclipse.paho.client.mqttv3.MqttMessage;

//  ./mosquitto_pub -u "BackEnd" -P "pass3" -t "ParkCharge/RichiediRicarica/1" -p 10011 -h localhost -m '{"target":2,"percentualeRicarica":10}'

// Gestisce i comandi di ricarica dai topic ParkCharge/RichiediRicarica/ID_MWBot e ParkCharge/EseguiRicarica/ID_MWBot
public class CommandStrategy implements MqttStrategy {
    private final GestoreIoT gestoreIoT;

    public CommandStrategy(GestoreIoT gestoreIoT) {
        this.gestoreIoT = gestoreIoT;
    }

    @Override
    public void handleMessage(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        System.out.println("Comando ricevuto su " + topic + ": " + payload);

        String idMWBot = topic.split("/")[2];
        String backendTopic = "ParkCharge/EseguiRicarica/" + idMWBot;
        gestoreIoT.publish(backendTopic, payload);
    }
}