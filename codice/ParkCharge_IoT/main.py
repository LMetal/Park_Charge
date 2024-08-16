import threading
import emulators.user_emulator
import emulators.mwbot_emulator
import emulators.sensors_emulator

def run_user_emulator():
    emulators.user_emulator.run()

def run_mwbot_emulator():
    emulators.mwbot_emulator.run()

def run_sensors_emulator():
    emulators.sensors_emulator.run()

#creo threads
user_thread = threading.Thread(target=run_user_emulator)
mwbot_thread = threading.Thread(target=run_mwbot_emulator)
sensors_thread = threading.Thread(target=run_sensors_emulator)

#run threads
user_thread.start()
mwbot_thread.start()
sensors_thread.start()
