import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class EDF {
    //TODO aggiungi campo percentuale ricaricata a database ricariche
    /**
     * @param userRequesting id user requesting to charge
     * @param timeToCharge time in minutes of the requesting charge
     * @param prenotazioni list of prenotazioni
     * @param ricariche list of charges already accepted
     * @return true if charge is acceptable(completable before the end of the prenotazione), false otherwise
     */
    public static boolean isAccettable(String userRequesting, int timeToCharge, ArrayList<Prenotazioni> prenotazioni, ArrayList<Ricariche> ricariche){
        return isAccettable(userRequesting, timeToCharge, prenotazioni, ricariche, LocalDateTime.now());
    }

    public static boolean isAccettable(String userRequesting, int timeToCharge, ArrayList<Prenotazioni> prenotazioni, ArrayList<Ricariche> ricariche, LocalDateTime startTime){
        Prenotazioni prenotazioneUtente = prenotazioni.stream()
                .filter(p -> p.getUtente().equals(userRequesting))
                .findFirst()
                .orElse(null);

        prenotazioni.sort(Comparator.comparing(Prenotazioni::getTempo_uscita));
        LocalDateTime t = startTime;

        for(Prenotazioni p: prenotazioni){
            if(ricariche.stream().anyMatch(r -> r.getPrenotazione() == p.getId())){
                Ricariche ricaricaPrenotazione = ricariche.stream()
                        .filter(r -> r.getPrenotazione() == p.getId())
                        .findFirst()
                        .orElse(null);

                if(t.plusMinutes(ricaricaPrenotazione.getDurata_ricarica() - ricaricaPrenotazione.getPercentuale_erogata()).isAfter(p.getTempo_uscita())){
                    return false;
                }
                t = t.plusMinutes(ricaricaPrenotazione.getDurata_ricarica());
                System.out.println(t.plusMinutes(timeToCharge));
            }

            if(p.equals(prenotazioneUtente)){
                System.out.println("HERE");
                System.out.println(t.plusMinutes(timeToCharge));
                if(t.plusMinutes(timeToCharge).isAfter(p.getTempo_uscita())){
                    return false;
                }
                t = t.plusMinutes(timeToCharge);
            }
        }

        return true;
    }

    public static int getJobPosto(){
        return 0;
    }
}
