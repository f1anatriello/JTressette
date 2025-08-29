package model;

import java.util.List;

/**
 * Rappresenta una partita di briscola 2 contro 2.
 * Implementa il pattern Singleton per garantire che ci sia una sola istanza di Partita2v2.
 * Estende la classe astratta Partita.
 * @author 1957447
 */
public class Partita2v2 extends Partita {

    /**
     * Istanza singleton di Partita2v2.
     */
    private static Partita2v2 instance = null;
    private Squadra squadra1;
    private Squadra squadra2;

    /**
     * Costruttore privato per il pattern Singleton.
     * Inizializza le squadre con i giocatori forniti.
     * @param giocatori Lista di 4 giocatori (2 per squadra).
     */
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

    /**
     * Restituisce l'istanza singleton di Partita2v2.
     * Se l'istanza non esiste, la crea con i giocatori forniti.
     * @param giocatori Lista di 4 giocatori (2 per squadra).
     * @return Istanza singleton di Partita2v2.
     */
    public static Partita2v2 getInstance(List<Giocatore> giocatori) {
        if (instance == null) {
            instance = new Partita2v2(giocatori);
        }
        return instance;
    }

    /**
     * Resetta lo stato della partita, permettendo di iniziare una nuova partita.
     * Imposta l'istanza singleton a null.
     */
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
     * Determina il vincitore della partita 2v2.
     * @return La squadra che ha vinto la partita (sotto forma del primo giocatore della squadra vincente).
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
     * @return Il giocatore che ha vinto la mano.
     * @throws IllegalArgumentException se la lista di carte non è valida (null o non contiene esattamente 4 carte).
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
