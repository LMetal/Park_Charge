import org.eclipse.paho.client.mqttv3.MqttMessage;

// Interfaccia per tutte le strategie MQTT
public interface MqttStrategy {
    void handleMessage(String topic, MqttMessage message);
}
