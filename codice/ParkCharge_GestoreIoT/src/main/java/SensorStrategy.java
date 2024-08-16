import org.eclipse.paho.client.mqttv3.MqttMessage;

// Gestisce i messaggi dal topic ParkCharge/StatoSensore/ID_Posto
public class SensorStrategy implements MqttStrategy{
    private final GestoreIoT gestoreIoT;

    public SensorStrategy(GestoreIoT gestoreIoT) {
        this.gestoreIoT = gestoreIoT;
    }

    @Override
    public void handleMessage(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        System.out.println("Messaggio sensore ricevuto su " + topic + ": " + payload);
        String idPosto = topic.split("/")[2];
        String backendTopic = "ParkCharge/StatoPosti/" + idPosto;
        gestoreIoT.publish(backendTopic, payload);
    }
}
