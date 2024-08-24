import paho.mqtt.client as mqtt
import json
import tkinter as tk

def run():
    broker = "127.0.0.1"
    port = 10011
    username = "MWBot"
    password = "pass4"
    topic_publish = "ParkCharge/StatoMWBot/1"
    topic_subscribe = "ParkCharge/EseguiRicarica/1"

    # Initial state
    posto = "idle"
    percentualeRicarica = 0
    target_percentualeRicarica = 0

    # MQTT publish function
    def publish_status(stato):
        data = {
            "statoCarica": stato,
            "posizione": posto if posto != "nessuno" else 0,  # Send 0 if posto is "nessuno"
            "percentualeRicarica": percentualeRicarica,
            "KW_Emessi": float(percentualeRicarica)
        }
        payload = json.dumps(data)
        client.publish(topic_publish, payload, qos=2)
        print(f"Published: {payload}")

    # MQTT on_message callback
    def on_message(client, userdata, message):
        nonlocal posto, percentualeRicarica, target_percentualeRicarica
        data = json.loads(message.payload.decode())
        target = data.get("target", posto)
        target_percentualeRicarica = data.get("percentualeRicarica", 0)

        # Check for reset condition
        if target == -1:
            posto = "idle"
            percentualeRicarica = 0
            percentuale_label.config(text=f"Percentuale Ricarica: {percentualeRicarica}%", bg="green")
            richiesta_label.config(text=f"Percentuale Richiesta: {target_percentualeRicarica}%")
            posto_label.config(text=f"Posto: {posto}")
            root.update_idletasks()
            return

        if target != posto:
            posto = target
            percentualeRicarica = 0
            publish_status("Charging")
            posto_label.config(text=f"Posto: {posto}")
            percentuale_label.config(text=f"Percentuale Ricarica: {percentualeRicarica}%", bg="orange")
        else:
            percentuale_label.config(text=f"Percentuale Ricarica: {target_percentualeRicarica}%", bg="orange")

        richiesta_label.config(text=f"Percentuale Richiesta: {target_percentualeRicarica}%")
        root.update_idletasks()

    # Increment percentage function
    def increment_percentage():
        nonlocal percentualeRicarica, posto
        if posto != "nessuno" and percentualeRicarica < target_percentualeRicarica:
            percentualeRicarica += 1
            percentuale_label.config(text=f"Percentuale Ricarica: {percentualeRicarica}%")
            publish_status("Charging")
            if percentualeRicarica == target_percentualeRicarica:
                percentuale_label.config(bg="green")
                publish_status("Finito")
                posto = "idle"  # Set posto to "idle"
                posto_label.config(text=f"Posto: {posto}")
        root.update_idletasks()

    # MQTT client setup
    client = mqtt.Client()
    client.username_pw_set(username, password)
    client.reconnect_delay_set(min_delay=1, max_delay=120)
    client.connect(broker, port, 60)
    print("Connected to broker")
    client.subscribe(topic_subscribe)
    print(f"Subscribed to topic: {topic_subscribe}\n")
    client.on_message = on_message
    client.loop_start()

    # GUI setup
    root = tk.Tk()
    root.title("MWBot Monitor")
    root.geometry("600x250")

    posto_label = tk.Label(root, text=f"Posto: {posto}", font=("Arial", 14))
    posto_label.pack(pady=10)
    percentuale_label = tk.Label(root, text=f"Percentuale Ricarica: {percentualeRicarica}%", font=("Arial", 14), bg="green", width=30)
    percentuale_label.pack(pady=10)
    richiesta_label = tk.Label(root, text=f"Percentuale Richiesta: {target_percentualeRicarica}%", font=("Arial", 12))
    richiesta_label.pack(pady=10)

    increment_button = tk.Button(root, text="Incrementa", command=increment_percentage, font=("Arial", 14))
    increment_button.pack(pady=10)

    # Start GUI
    root.mainloop()

    # Cleanup on close
    client.loop_stop()
    client.disconnect()

