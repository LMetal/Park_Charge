import paho.mqtt.client as mqtt
import tkinter as tk
from tkinter import scrolledtext
import json

def run():
    broker = "127.0.0.1"  # se problemi modifica
    port = 10011
    username = "User"
    password = "pass5"

    # topics (wildcard -> each user)
    sosta_conclusa_topic = "ParkCharge/Notifiche/SostaConclusa/#"
    ricarica_conclusa_topic = "ParkCharge/Notifiche/RicaricaConclusa/#"
    premium_topic = "ParkCharge/Notifiche/Premium/#"
    pagamento_topic = "ParkCharge/Pagamento/#"
    

    # callback connessione
    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            print("Connected successfully")
            # Subscribe to the wildcard topics
            client.subscribe(sosta_conclusa_topic)
            client.subscribe(ricarica_conclusa_topic)
            client.subscribe(premium_topic)
            print(f"Subscribed to {sosta_conclusa_topic} , {ricarica_conclusa_topic} and {premium_topic}")
        else:
            print(f"Connection failed with code {rc}")

    # callback publish
    def on_message(client, userdata, msg):
        topic = msg.topic
        payload = json.loads(msg.payload.decode('utf-8'))

        topic_parts = topic.split('/')
        user_id = topic_parts[-1]

        if "SostaConclusa" in topic:
            totale = round(payload['costoSosta'] + payload['costoRicarica'], 2)
            message = (
                f"User {user_id} - Notifica sosta conclusa:\n"
                f"  - Tempo di sosta: {payload['tempoSosta']} minuti\n"
                f"  - Costo della sosta: {payload['costoSosta']:.2f} EUR\n"
                f"  - Kilowatt usati: {payload['kilowattUsati']} kW\n"
                f"  - Costo della ricarica: {payload['costoRicarica']:.2f} EUR\n"
                f"  - Costo totale: {totale:.2f} EUR\n"
            )
            
            # Mostra il pulsante di pagamento sotto il campo di testo
            pay_button.config(text=f"Paga {totale:.2f} EUR", command=lambda: effettua_pagamento(user_id, totale))
            pay_button.pack(side=tk.TOP, pady=10)

        elif "Premium" in topic:
            totale = payload['costo_premium']
            message = (
                f"User {user_id} - Notifica costo abbonamento premium richiesto:\n"
                f"  - Costo totale: {totale} EUR\n"
            )

            # Mostra il pulsante di pagamento sotto il campo di testo
            pay_button.config(text=f"Paga {totale} EUR", command=lambda: effettua_pagamento(user_id, totale))
            pay_button.pack(side=tk.TOP, pady=10)


        elif "RicaricaConclusa" in topic:
            message = (
                f"User {user_id} - Notifica Ricarica Conclusa:\n"
                f"  - Kilowatt usati: {payload['kilowattUsati']} kW\n"
                f"  - Costo della ricarica: {payload['costoRicarica']:.2f} EUR\n"
            )
        
        # Inserisco nella GUI il messaggio
        output_text.insert(tk.END, message + "\n")
        output_text.see(tk.END)

    # Funzione per gestire il pagamento
    def effettua_pagamento(user_id, totale):
        payment_message = {
            "username": user_id,
            "totale": totale,
            "status": "pagamento_effettuato"
        }
        client.publish(f"ParkCharge/Pagamento/{user_id}", json.dumps(payment_message))
        output_text.insert(tk.END, f"Pagamento di {totale} EUR per l'utente {user_id} effettuato.\n")
        output_text.see(tk.END)
        pay_button.pack_forget()  # Nascondi il pulsante dopo il pagamento

    # MQTT client
    client = mqtt.Client()
    client.username_pw_set(username, password)

    # Event callbacks
    client.on_connect = on_connect
    client.on_message = on_message
    client.connect(broker, port, 60)

    # GUI
    root = tk.Tk()
    root.title("User Device Emulator")
    title_label = tk.Label(root, text="User Device Emulator", font=("Arial", 16))
    title_label.pack(pady=10)
    output_text = scrolledtext.ScrolledText(root, wrap=tk.WORD, width=70, height=20, font=("Arial", 12))
    output_text.pack(pady=10, padx=10)
    pay_button = tk.Button(root, text="", font=("Arial", 14))  # Button per il pagamento
    pay_button.pack_forget()  # Nascondi il pulsante finché non è necessario

    # MQTT client
    client.loop_start()

    # GUI loop
    root.mainloop()

    # Chiusura GUI
    client.loop_stop()
    client.disconnect()
