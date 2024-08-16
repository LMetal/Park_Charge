import paho.mqtt.client as mqtt
import tkinter as tk
from tkinter import scrolledtext
import json

def run():
    broker = "127.0.0.1" #se problemi modifica
    port = 10011
    username = "User"
    password = "pass5"

    #topics (wildcard -> each user)
    sosta_conclusa_topic = "ParkCharge/Notifiche/SostaConclusa/#"
    ricarica_conclusa_topic = "ParkCharge/Notifiche/RicaricaConclusa/#"

    #callback connessione
    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            print("Connected successfully")
            # Subscribe to the wildcard topics
            client.subscribe(sosta_conclusa_topic)
            client.subscribe(ricarica_conclusa_topic)
            print(f"Subscribed to {sosta_conclusa_topic} and {ricarica_conclusa_topic}")
        else:
            print(f"Connection failed with code {rc}")

    #callback publish 
    def on_message(client, userdata, msg):
        topic = msg.topic
        payload = json.loads(msg.payload.decode('utf-8'))
        
        topic_parts = topic.split('/')
        user_id = topic_parts[-1]

        if "SostaConclusa" in topic:
            message = (
                f"User {user_id} - Notifica sosta conclusa:\n"
                f"  - Tempo di sosta: {payload['tempoSosta']} minuti\n"
                f"  - Costo della sosta: {payload['costoSosta']} EUR\n"
                f"  - Kilowatt usati: {payload['kilowattUsati']} kW\n"
                f"  - Costo della ricarica: {payload['costoRicarica']} EUR\n"
            )
        elif "RicaricaConclusa" in topic:
            message = (
                f"User {user_id} - Notifica Ricarica Conclusa:\n"
                f"  - Kilowatt usati: {payload['kilowattUsati']} kW\n"
                f"  - Costo della ricarica: {payload['costoRicarica']} EUR\n"
            )
        
        #inserisco nella gui
        output_text.insert(tk.END, message + "\n")
        output_text.see(tk.END)

    #MQTT client
    client = mqtt.Client()
    client.username_pw_set(username, password)

    #event callbacks
    client.on_connect = on_connect
    client.on_message = on_message
    client.connect(broker, port, 60)

    #gui
    root = tk.Tk()
    root.title("User Device Emulator")
    title_label = tk.Label(root, text="User Device Emulator", font=("Arial", 16))
    title_label.pack(pady=10)
    output_text = scrolledtext.ScrolledText(root, wrap=tk.WORD, width=70, height=20, font=("Arial", 12))
    output_text.pack(pady=10, padx=10)

    #MQTT client
    client.loop_start()

    #gui loop
    root.mainloop()

    #chiusura gui
    client.loop_stop()
    client.disconnect()
