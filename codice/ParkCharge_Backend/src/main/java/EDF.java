import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class EDF {
    public static boolean isAccettable(String userRequesting, int timeToCharge,ArrayList<Prenotazioni> prenotazioni, ArrayList<Ricariche> ricariche){
        Prenotazioni prenotazionUtente = (Prenotazioni) prenotazioni.stream().filter(p -> p.getUtente().equals(userRequesting));
        prenotazioni.sort(Comparator.comparing(Prenotazioni::getTempo_uscita));

        LocalDateTime t = prenotazioni.get(0).getTempo_arrivo(); //TODO sistema
        for(Prenotazioni p: prenotazioni){
            if(ricariche.stream().anyMatch(r -> r.getPrenotazione() == p.getId())){
                Ricariche ric = (Ricariche) ricariche.stream().filter(r -> r.getPrenotazione() == p.getId());
                if(t.plusMinutes(ric.getDurata_ricarica()).isAfter(p.getTempo_uscita())){
                    return false;
                }
                t = t.plusMinutes(ric.getDurata_ricarica());
            }

            if(p.equals(prenotazionUtente)){
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
