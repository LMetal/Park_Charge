import warnings
import paho.mqtt.client as mqtt
import json
import tkinter as tk

# Suppress DeprecationWarning
warnings.filterwarnings("ignore", category=DeprecationWarning)

# MQTT settings
broker = "127.0.0.1"  # IP address of your VM's wifi0 interface
port = 10011  # Port for Mosquitto broker
username = "Sensore"  # MQTT username
password = "pass2"    # MQTT password

# Sensor settings
sensors = {
    "1": "Sensor 1",
    "2": "Sensor 2",
    "3": "Sensor 3",
    "4": "Sensor 4",
    "5": "Sensor 5",
    "6": "Sensor 6",
    # Add more sensors if needed
}

# Internal state for each sensor
sensor_states = {sensor_id: "libero" for sensor_id in sensors.keys()}

# Create a dictionary to keep track of the button widgets
buttons = {}

# MQTT publish function
def publish_sensor_data(sensor_id, stato):
    topic = f"ParkCharge/StatoSensore/{sensor_id}/data"
    payload = json.dumps({"stato": stato})
    client.publish(topic, payload)
    print(f"Published to {topic}: {payload}")

# Update button text and color based on sensor state
def update_button(sensor_id):
    state = sensor_states[sensor_id]
    button = buttons[sensor_id]['button']
    button.config(
        text=state,
        bg="red" if state == "occupato" else "green"
    )
    buttons[sensor_id]['label'].config(text=f"POSTO {sensor_id}")

# Toggle sensor state, update button, and publish
def toggle_sensor_state(sensor_id):
    current_state = sensor_states[sensor_id]
    new_state = "occupato" if current_state == "libero" else "libero"
    sensor_states[sensor_id] = new_state
    update_button(sensor_id)
    publish_sensor_data(sensor_id, new_state)

# Button click callback
def on_button_click(sensor_id):
    toggle_sensor_state(sensor_id)

# MQTT client setup
client = mqtt.Client()
client.username_pw_set(username, password)
client.connect(broker, port, 60)

# Create the main application window
root = tk.Tk()
root.title("Sensor Emulator")

# Create a frame for each sensor to hold the label and button
for sensor_id in sensors.keys():
    frame = tk.Frame(root)
    frame.pack(pady=10)

    # Create a label for sensor number
    label = tk.Label(frame, text=f"POSTO {sensor_id}")
    label.pack(side=tk.LEFT)

    # Create a button for each sensor
    initial_state = sensor_states[sensor_id]
    button = tk.Button(frame, text=initial_state, 
                       bg="green" if initial_state == "libero" else "red",
                       width=15,  # Set a fixed width for all buttons
                       command=lambda sid=sensor_id: on_button_click(sid))
    button.pack(side=tk.LEFT, padx=5)  # Add padding around the button

    # Store both the label and button widgets in the dictionary
    buttons[sensor_id] = {'label': label, 'button': button}

# Start the Tkinter event loop
root.mainloop()

# Disconnect from MQTT broker when closing the application
client.disconnect()
