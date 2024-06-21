import DataBase.DbPrenotazioni;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;


public class GestorePosti {
    private DbPrenotazioni dbPrenotazioni;
    DateTimeFormatter formatter;

    public GestorePosti(){
        this.dbPrenotazioni = new DbPrenotazioni();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public ArrayList<Prenotazioni> getPrenotazioni(){
        ArrayList<Prenotazioni> listaPrenotazioni = new ArrayList<>();
        String comandoSql = "SELECT * FROM Prenotazioni";
        System.out.println(comandoSql);
        var rs = dbPrenotazioni.query(comandoSql);

        for(HashMap<String, Object> record : rs){
            listaPrenotazioni.add(new Prenotazioni((Integer) record.get("id"), LocalDateTime.parse((CharSequence) record.get("tempo_arrivo"), formatter), LocalDateTime.parse((CharSequence) record.get("tempo_uscita"), formatter), (String) record.get("utente"), (Integer) record.get("posto")));
        }
        return listaPrenotazioni;
    }

    public Prenotazioni creaPrenotazione(Prenotazioni nuovaPrenotazione, int tipo, String provenienza) {
        if(tipo == 2 || (tipo == 1 && provenienza.equals("occupa"))){
            LocalDateTime now = LocalDateTime.now();
            nuovaPrenotazione.setTempo_arrivo(now.format(formatter));
        }

        ArrayList<Integer> idPostiAuto = this.getIdPostiAuto();
        ArrayList<Prenotazioni> prenotazioni = this.getPrenotazioni();

        if(this.verificaDisponibilta(idPostiAuto,prenotazioni,nuovaPrenotazione)){
            String comandoSql = "INSERT INTO Prenotazioni (tempo_arrivo, tempo_uscita, utente, posto) VALUES ('" + nuovaPrenotazione.getTempo_arrivo().format(formatter) + "', '" + nuovaPrenotazione.getTempo_uscita().format(formatter) + "', '" + nuovaPrenotazione.getUtente() + "', " + nuovaPrenotazione.getPosto() + ");";
            System.out.println(comandoSql);
            if(dbPrenotazioni.update(comandoSql))
                return nuovaPrenotazione;
        }

        return null;
    }

    private ArrayList<Integer> getIdPostiAuto() {
        String comandoPostiAutoSql = "SELECT * FROM PostoAuto";
        System.out.println(comandoPostiAutoSql);
        ArrayList<Integer> idPostiAuto = new ArrayList<>();
        var rsPostiAuto = dbPrenotazioni.query(comandoPostiAutoSql);
        for (HashMap<String, Object> postoAutoRs : rsPostiAuto) {
            idPostiAuto.add((int) postoAutoRs.get("id"));
        }
        return idPostiAuto;
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

    public Prenotazioni getPrenotazione(String id) {
        String comandoSql = "SELECT * FROM Prenotazioni WHERE id = \"" + id + "\"";
        System.out.println(comandoSql);
        var rs = dbPrenotazioni.query(comandoSql);

        if(rs.isEmpty())
            return null;

        return new Prenotazioni((Integer) rs.get(0).get("id"), LocalDateTime.parse((CharSequence) rs.get(0).get("tempo_arrivo"), formatter), LocalDateTime.parse((CharSequence) rs.get(0).get("tempo_uscita"), formatter), (String) rs.get(0).get("utente"), (Integer) rs.get(0).get("posto"));

    }

    public Prenotazioni modificaPrenotazione(Prenotazioni nuovaPrenotazione, Prenotazioni vecchiaPrenotazione) {

        if(vecchiaPrenotazione == null) return null;

        ArrayList<Integer> idPostiAuto = this.getIdPostiAuto();
        ArrayList<Prenotazioni> prenotazioni = this.getPrenotazioni();

        prenotazioni.removeIf(prenotazione -> prenotazione.getId() == vecchiaPrenotazione.getId());

        if(this.verificaDisponibilta(idPostiAuto,prenotazioni,nuovaPrenotazione)){
            String comandoSql = "UPDATE Prenotazioni SET tempo_arrivo = \"" + nuovaPrenotazione.getTempo_arrivo().format(formatter) + "\", tempo_uscita = \"" + nuovaPrenotazione.getTempo_uscita().format(formatter) + "\" WHERE id = \"" + vecchiaPrenotazione.getId() + "\";";
            System.out.println(comandoSql);
            dbPrenotazioni.update(comandoSql);
            vecchiaPrenotazione.setTempo_arrivo(nuovaPrenotazione.getTempo_arrivo().format(formatter));
            vecchiaPrenotazione.setTempo_uscita(nuovaPrenotazione.getTempo_uscita().format(formatter));
            return vecchiaPrenotazione;
        }
        return null;
    }

    public boolean cancellaPrenotazione(String id) {
        String comandoSql = "DELETE FROM Prenotazioni WHERE id = \"" + id + "\";";
        System.out.println(comandoSql);
        return dbPrenotazioni.update(comandoSql);
    }
}
