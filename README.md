# ParkCharge

**ParkCharge** è un sistema integrato per la gestione intelligente di parcheggi e stazioni di ricarica per veicoli elettrici. Il progetto include componenti backend, frontend e un gestore IoT per il controllo e il monitoraggio in tempo reale dei dispositivi di ricarica e dei sensori presenti nei parcheggi.

## Caratteristiche
- **Gestione delle prenotazioni**: Consente agli utenti di prenotare posti auto e stazioni di ricarica.
- **Monitoraggio in tempo reale**: Tramite sensori e dispositivi IoT, è possibile monitorare lo stato dei parcheggi.
- **Sistema di pagamento integrato**: Gestisce i pagamenti per le ricariche e le prenotazioni.

## Prerequisiti
Assicurati di avere i seguenti prerequisiti prima di procedere con l'installazione:
- **Python** >= 3.8
- **Mosquitto Broker MQTT** installato e configurato.

I file di configurazione per Mosquitto si trovano nella cartella `doc/MQTT` e includono:
- `configurazione_broker.conf`
- `acl.txt`
- `file_pass.txt`
- `password_chiaro.txt`

## Installazione

### Clonazione del Repository
Clona il repository sul tuo sistema:
```
git clone https://gitlab.di.unipmn.it/pissir23-24/aa23-24-gruppo6.git
```

Entra nella directory del progetto:
```
cd aa23-24-gruppo6/
```

### Avvio di Mosquitto
Avvia il broker MQTT Mosquitto utilizzando il file di configurazione forniti nella cartella `doc/MQTT`:
```
./mosquitto.exe -c configurazione_broker.conf -v
```

### Avvio del Sistema
Per avviare il sistema, esegui il file di script appropriato per il tuo sistema operativo:

- **Windows**:
```
./codice/ParkCharge.bat
```
- **Linux**:
```
./codice/ParkCharge.sh
```
Una volta avviato, il sistema sarà operativo e pronto per gestire le richieste degli utenti. Gli utenti potranno interagire con l'interfaccia frontend per prenotare posti auto, effettuare pagamenti e monitorare lo stato delle stazioni di ricarica.

## Struttura del Progetto
- **/codice**: Contiene il codice sorgente per il backend, il frontend, il gestore IoT e gli emulatori (sensori, MWBot e user Device).
- **/doc**: Include la documentazione del progetto, i file di configurazione MQTT e gli schemi del database.
- **/diagrammi**: Contiene diagrammi e schemi relativi all'architettura del sistema.