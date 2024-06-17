import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class PrenotaPosto{

    GestorePosti gestorePosti = new GestorePosti();

    @Test
    public void testCreaPrenotazione(){
    }

    @Test
    public void testCreaPrenotazioneErrore(){

    }

    @Test
    public void testVerificaDisponibilita(){

    }
}

/*
public String creaPrenotazione(Prenotazioni nuovaPrenotazione, int tipo, String provenienza) {
    if(tipo == 2 || (tipo == 1 && provenienza.equals("occupa"))){
        LocalDateTime now = LocalDateTime.now();
        nuovaPrenotazione.setTempo_arrivo(now.format(formatter));
    }
    String comandoPostiAutoSql = "SELECT * FROM PostoAuto";
    System.out.println(comandoPostiAutoSql);
    ArrayList<Integer> idPostiAuto = new ArrayList<>();
    var rsPostiAuto = dbPrenotazioni.query(comandoPostiAutoSql);
    for (HashMap<String,Object> postoAutoRs : rsPostiAuto){
        idPostiAuto.add((int)postoAutoRs.get("id"));//tutti i posti auto
    }

    String comandoPrenotazioniSql = "SELECT * FROM prenotazioni";
    System.out.println(comandoPrenotazioniSql);
    ArrayList<Prenotazioni> prenotazioni = new ArrayList<>();
    var rs = dbPrenotazioni.query(comandoPrenotazioniSql);
    for (HashMap<String,Object> prenotazioniRs : rs){
        int id = (int)prenotazioniRs.get("id");
        LocalDateTime tempoArrivo = LocalDateTime.parse((CharSequence) prenotazioniRs.get("tempo_arrivo"), formatter);
        LocalDateTime tempoUscita = LocalDateTime.parse((CharSequence) prenotazioniRs.get("tempo_uscita"), formatter);
        String utente = (String)prenotazioniRs.get("utente");
        int posto = (int)prenotazioniRs.get("posto");
        prenotazioni.add(new Prenotazioni(id, tempoArrivo, tempoUscita, utente, posto));//tutte le prenotazioni
    }

    if(this.verificaDisponibilta(idPostiAuto,prenotazioni,nuovaPrenotazione)){
        String comandoSql = "INSERT INTO Prenotazioni (tempo_arrivo, tempo_uscita, utente, posto) VALUES ('" + nuovaPrenotazione.getTempo_arrivo().format(formatter) + "', '" + nuovaPrenotazione.getTempo_uscita().format(formatter) + "', '" + nuovaPrenotazione.getUtente() + "', " + nuovaPrenotazione.getPosto() + ");";
        System.out.println(comandoSql);
        if(dbPrenotazioni.update(comandoSql))
            return "Successo";
        else
            return "Errore nella crezione della prenotazione";
    }

    return "Nessun posto disponibile nel periodo richiesto";
}

private boolean verificaDisponibilta(ArrayList<Integer> idPostiAuto, ArrayList<Prenotazioni> prenotazioni, Prenotazioni nuovaPrenotazione) {
    ArrayList<Integer> postiLiberi = new ArrayList<>(idPostiAuto);

    for (Prenotazioni prenotazione : prenotazioni) {
        if (prenotazione.getTempo_arrivo().isBefore(nuovaPrenotazione.getTempo_uscita()) &&
                prenotazione.getTempo_uscita().isAfter(nuovaPrenotazione.getTempo_arrivo())) {
            postiLiberi.remove((Integer) prenotazione.getPosto());
        }
    }

    if (postiLiberi.isEmpty()) {
        return false;
    } else {
        int postoAssegnato = postiLiberi.get(0);
        nuovaPrenotazione.setPosto(postoAssegnato);

        return true;
    }
}

*/
