import org.eclipse.paho.client.mqttv3.*;

public class GestoreIoT {

    final public String username = "GestoreIoT";
    final public String password = "pass1";
    private final String brokerUrl = "tcp://localhost:10011";
    private final String topicSensor = "ParkCharge/StatoSensore/#";
    private final String topicMWBot = "ParkCharge/StatoMWBot/#";
    private final String topicCommand = "ParkCharge/RichiediRicarica/#";

    private MqttClient client;
    private MqttStrategy sensorStrategy;
    private MqttStrategy mwBotStrategy;
    private MqttStrategy commandStrategy;

    public static void main(String[] args) {
        GestoreIoT gestoreIoT = new GestoreIoT();
        gestoreIoT.start();
    }

    public void start() {
        try {
            client = new MqttClient(brokerUrl, MqttClient.generateClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());

            client.connect(options);

            // Inizializzazione delle strategie
            sensorStrategy = new SensorStrategy(this);
            mwBotStrategy = new MWBotStrategy(this);
            commandStrategy = new CommandStrategy(this);

            // Sottoscrizione ai topic
            client.subscribe(topicSensor, (topic, message) -> sensorStrategy.handleMessage(topic, message));
            client.subscribe(topicMWBot, (topic, message) -> mwBotStrategy.handleMessage(topic, message));
            client.subscribe(topicCommand, (topic, message) -> commandStrategy.handleMessage(topic, message));

            System.out.println("Sottoscritto ai topic");
        } catch (MqttException e) {
            e.printStackTrace();
        }
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

    /*
    public void stop() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
     */
}
