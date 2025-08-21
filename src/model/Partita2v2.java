package model;

import java.util.List;

public class Partita2v2 extends Partita {

    private static Partita2v2 instance = null;
    private Squadra squadra1;
    private Squadra squadra2;

    public Partita2v2(List<Giocatore> giocatori) {
        super(giocatori);
        if (giocatori.size() != 4) {
            throw new IllegalArgumentException("Una partita 2v2 richiede esattamente 4 giocatori");
        }
        squadra1 = new Squadra("Squadra1", giocatori.get(0), giocatori.get(2));
        squadra2 = new Squadra("Squadra2", giocatori.get(1), giocatori.get(3));
    }

    public static Partita2v2 getInstance(List<Giocatore> giocatori) {
        if (instance == null) {
            instance = new Partita2v2(giocatori);
        }
        return instance;
    }

    /**
     * Restituisce il nome della squadra vincente (Nord-Sud o Est-Ovest)
     */
    public String vincitore2v2() {
        // Squadra Nord-Sud = giocatori 0 e 2
        double puntiNS = squadra1.getPunteggioTotale();

        // Squadra Est-Ovest = giocatori 1 e 3
        double puntiEO = squadra2.getPunteggioTotale();

        if (puntiNS > puntiEO) {
            return squadra1.getNome();
        } else if (puntiEO > puntiNS) {
            return squadra2.getNome();
        } else {
            return "Pareggio";
        }
    }

    /**
     * Determina il vincitore della mano (tra 4 carte giocate).
     * @param terr Lista di 4 carte (una per ciascun giocatore).
     */
    @Override
    public Giocatore manoVintaDa(List<Carta> terr) {
        if (terr == null || terr.size() != 4) {
            throw new IllegalArgumentException("Terreno non valido: devono esserci esattamente 4 carte");
        }

        // La prima carta giocata stabilisce il seme dominante
        Seme semeRegnante = terr.get(0).getSeme();

        Giocatore vincitore = giocatori.get(0); // inizio con il primo
        Carta cartaVincente = terr.get(0);

        // confronto le altre 3 carte
        for (int i = 1; i < terr.size(); i++) {
            Carta cartaAttuale = terr.get(i);

            if (cartaAttuale.getSeme() == semeRegnante) {
                if (cartaAttuale.getValore().getPunti() > cartaVincente.getValore().getPunti()) {
                    cartaVincente = cartaAttuale;
                    vincitore = giocatori.get(i);
                }
            }
        }

        return vincitore;
    }
}
