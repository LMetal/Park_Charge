#!/bin/bash

# Percorso base
BASE_DIR="$(dirname "$(readlink -f "$0")")"

# Avvia i JAR
echo "Avvio ParkCharge_Backend..."
java -jar "$BASE_DIR/ParkCharge_Backend/out/artifacts/ParkCharge_Backend_jar/ParkCharge_Backend.jar" &

echo "Avvio ParkCharge_Frontend..."
java -jar "$BASE_DIR/ParkCharge_Frontend/out/artifacts/ParkCharge_Frontend_jar/ParkCharge_Frontend.jar" &

echo "Avvio ParkCharge_GestoreIoT..."
java -jar "$BASE_DIR/ParkCharge_GestoreIoT/out/artifacts/ParkCharge_GestoreIoT_jar/ParkCharge_GestoreIoT.jar" &

# Avvia main.py
echo "Avvio main.py..."
python3 "$BASE_DIR/ParkCharge_IoT/main.py" &

# Mostra i processi in esecuzione
echo "Servizi avviati"
wait
