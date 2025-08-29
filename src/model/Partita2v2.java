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
        squadra1 = new Squadra("Squadra1", giocatori.get(0), giocatori.get(1));
        squadra2 = new Squadra("Squadra2", giocatori.get(2), giocatori.get(3));
        giocatori.get(0).setSquadra(squadra1);
        giocatori.get(1).setSquadra(squadra1);
        giocatori.get(2).setSquadra(squadra2);
        giocatori.get(3).setSquadra(squadra2);
    }

    public static Partita2v2 getInstance(List<Giocatore> giocatori) {
        if (instance == null) {
            instance = new Partita2v2(giocatori);
        }
        return instance;
    }

    @Override
    public void reset() {
        giocatori.clear();
        mazzo = null;
        finita = false;
        terreno = null;
        squadra1 = null;
        squadra2 = null;
        instance = null;
    }

    /**
     * Restituisce il nome della squadra vincente (Nord-Sud o Est-Ovest)
     */
    public Giocatore vincitore2v2() {
        return List.of(squadra1, squadra2).stream()
            .max((s1, s2) -> Double.compare(s1.getPunteggioTotale(), s2.getPunteggioTotale()))
            .map(squadra -> squadra == squadra1 ? giocatori.get(0) : giocatori.get(2))
            .orElse(null);
    }

    /**
     * Determina il vincitore della mano (tra 4 carte giocate).
     * @param terr Lista di 4 carte (una per ciascun giocatore).
     */
    @Override
    public Giocatore manoVintaDa(List<Carta> terr) {
        if (terr == null || terr.size() != 4) {
            if (terr == null) {
                throw new IllegalArgumentException("Terreno non valido: terreno è null");
            }
            System.out.println(terr.size());
            throw new IllegalArgumentException("Terreno non valido: devono esserci esattamente 4 carte");
        }

        // La prima carta giocata stabilisce il seme dominante
        Seme semeRegnante = terr.get(0).getSeme();

        Carta cartaVincente = terr.get(0);
        Giocatore vincitore = cartaVincente.getGiocatore();

        // confronto le altre 3 carte
        for (int i = 1; i < terr.size(); i++) {
            Carta cartaAttuale = terr.get(i);

            if (cartaAttuale.getSeme() == semeRegnante) {
                if (cartaAttuale.getValore().getOrdinePresa() > cartaVincente.getValore().getOrdinePresa()) {
                    cartaVincente = cartaAttuale;
                    vincitore = cartaAttuale.getGiocatore();
                }
            }
        }
        System.out.println("Vincitore della mano: " + vincitore.getNome() + " con " + cartaVincente.toString());
        return vincitore;
    }
}
