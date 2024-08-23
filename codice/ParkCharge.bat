@echo off
setlocal

:: Percorso base
set BASE_DIR=%~dp0

:: Avvia i JAR
echo Avvio ParkCharge_Backend...
start "" java -jar "%BASE_DIR%ParkCharge_Backend\out\artifacts\ParkCharge_Backend_jar\ParkCharge_Backend.jar"

echo Avvio ParkCharge_Frontend...
start "" java -jar "%BASE_DIR%ParkCharge_Frontend\out\artifacts\ParkCharge_Frontend_jar\ParkCharge_Frontend.jar"

echo Avvio ParkCharge_GestoreIoT...
start "" java -jar "%BASE_DIR%ParkCharge_GestoreIoT\out\artifacts\ParkCharge_GestoreIoT_jar\ParkCharge_GestoreIoT.jar"

:: Avvia main.py
echo Avvio main.py...
start "" python "%BASE_DIR%ParkCharge_IoT\main.py"

:: Mostra i processi in esecuzione
echo Servizi avviati
pause
