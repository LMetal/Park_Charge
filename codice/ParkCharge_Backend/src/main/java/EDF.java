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
    public static boolean isAcceptable(String userRequesting, int timeToCharge, ArrayList<Prenotazioni> prenotazioni, ArrayList<Ricariche> ricariche){
        return isAcceptable(userRequesting, timeToCharge, prenotazioni, ricariche, LocalDateTime.now());
    }

    //iltimoo parametro per testing
    public static boolean isAcceptable(String userRequesting, int timeToCharge, ArrayList<Prenotazioni> prenotazioni, ArrayList<Ricariche> ricariche, LocalDateTime startTime){
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

                if(ricaricaPrenotazione == null) return false;
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

    /**
     * @param prenotazioni list of prenotazioni
     * @param ricariche list of ricariche
     * @return place number for the MWBot to charge, -1 if MWBot can go idle (nothing to charge)
     */
    public static int getJobPosto(ArrayList<Prenotazioni> prenotazioni, ArrayList<Ricariche> ricariche){
        prenotazioni.sort(Comparator.comparing(Prenotazioni::getTempo_uscita));

        for(Prenotazioni p: prenotazioni){
            if(ricariche.stream().anyMatch(r -> r.getPrenotazione() == p.getId())){
                return p.getPosto();
            }
        }
        return -1;
    }
}
