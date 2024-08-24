import paho.mqtt.client as mqtt
import json
import tkinter as tk

def run():
    broker = "127.0.0.1"
    port = 10011
    username = "Sensore"
    password = "pass2"

    sensors = {
        "1": "Sensor 1",
        "2": "Sensor 2",
        "3": "Sensor 3",
        "4": "Sensor 4",
        "5": "Sensor 5",
        "6": "Sensor 6",
        "7": "Sensor 7",
        "8": "Sensor 8",
        "9": "Sensor 9",
        "10": "Sensor 10"
    }

    # Stato dei sensori
    sensor_states = {sensor_id: "libero" for sensor_id in sensors.keys()}
    buttons = {}

    # Callback connessione
    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            print("Connected successfully")
        else:
            print(f"Connection failed with code {rc}")

    # MQTT publish
    def publish_sensor_data(sensor_id, stato):
        topic = f"ParkCharge/StatoSensore/{sensor_id}"
        payload = json.dumps({"stato": stato})
        client.publish(topic, payload)
        print(f"Published to {topic}: {payload}")

    # Aggiornamento bottone
    def update_button(sensor_id):
        state = sensor_states[sensor_id]
        button = buttons[sensor_id]['button']
        button.config(
            text=state,
            bg="red" if state == "occupato" else "green"
        )
        buttons[sensor_id]['label'].config(text=f"POSTO {sensor_id}")

    # Toggle stato sensore
    def toggle_sensor_state(sensor_id):
        current_state = sensor_states[sensor_id]
        new_state = "occupato" if current_state == "libero" else "libero"
        sensor_states[sensor_id] = new_state
        update_button(sensor_id)
        publish_sensor_data(sensor_id, new_state)

    # Callback click
    def on_button_click(sensor_id):
        toggle_sensor_state(sensor_id)

    # MQTT client
    client = mqtt.Client()
    client.username_pw_set(username, password)
    client.on_connect = on_connect  # Aggiunto callback connessione
    client.connect(broker, port, 60)

    # GUI
    root = tk.Tk()
    root.title("Sensor Emulator")
    
    # Titolo della GUI
    title_label = tk.Label(root, text="Sensor Emulator", font=("Arial", 16))
    title_label.pack(pady=10)

    # Sensor rows
    for sensor_id in sensors.keys():
        frame = tk.Frame(root)
        frame.pack(pady=10)

        # Sensor label
        label = tk.Label(frame, text=f"POSTO {sensor_id}")
        label.pack(side=tk.LEFT)

        # Sensor button
        initial_state = sensor_states[sensor_id]
        button = tk.Button(frame, text=initial_state, 
                           bg="green" if initial_state == "libero" else "red",
                           width=15,  # Button fixed width
                           command=lambda sid=sensor_id: on_button_click(sid))
        button.pack(side=tk.LEFT, padx=5)  # Padding

        buttons[sensor_id] = {'label': label, 'button': button}

    # Avvio MQTT loop
    client.loop_start()

    # GUI loop
    root.mainloop()

    # Disconnessione MQTT
    client.loop_stop()
    client.disconnect()
