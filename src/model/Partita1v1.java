package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rappresenta una partita di Tressette 1 vs 1.
 * Estende la classe astratta Partita e implementa la logica specifica per il formato 1v1.
 * Utilizza il pattern Singleton per garantire che esista una sola istanza della partita.
 * @author 1957447
 */
public class Partita1v1 extends Partita {

    /**
     * L'istanza singleton della partita 1v1.
     */
	private static Partita1v1 instance = null;

    /**
     * Costruisce una nuova partita 1v1 con la lista di giocatori specificata.
     * @param giocatori
     */
    public Partita1v1(List<Giocatore> giocatori) {
        super(giocatori);
    }

    /**
     * Restituisce l'istanza singleton della partita 1v1.
     * Se l'istanza non esiste, la crea con la lista di giocatori specificata.
     * @param giocatori La lista di giocatori per la partita.
     * @return L'istanza singleton della partita 1v1.
     */
	public static Partita1v1 getInstance(List<Giocatore> giocatori) {
		if (instance == null) {
			instance = new Partita1v1(giocatori);
		}
		return instance;
	}

    /**
     * Resetta lo stato della partita, pulendo la lista di giocatori, il mazzo, il terreno e l'istanza singleton.
     */
    @Override
    public void reset() {
        giocatori.clear();
        mazzo = null;
        finita = false;
        terreno = null;
        instance = null;
    }

    /**
     * Determina il vincitore della partita 1v1 basandosi sui punti ottenuti dai giocatori.
     * @return Il giocatore che ha ottenuto il punteggio più alto.
     */
    public Giocatore vincitore1v1(){
    	Map<Giocatore,Double> mappa = new HashMap<>();
    	
        giocatori.forEach(g -> mappa.put(g, g.puntiOttenuti));
    	
    	return mappa.entrySet().stream()
    	        .max(Map.Entry.comparingByValue())
    	        .map(Map.Entry::getKey)
    	        .orElse(null);
    }

    /**
     * Determina quale giocatore ha vinto la mano corrente basandosi sulle carte sul terreno.
     * @param terr La lista delle carte giocate sul terreno.
     * @return Il giocatore che ha vinto la mano.
     */
	@Override
	public Giocatore manoVintaDa(List<Carta> terr) {
        if (terr == null || terr.size() < 2) {
            throw new IllegalArgumentException("Terreno non valido: devono esserci almeno 2 carte");
        }

        // La prima carta appartiene al giocatore che ha iniziato
        Carta cartaPrima = terr.get(0);
        Carta cartaSeconda = terr.get(1);

        Seme semeRegnante = cartaPrima.getSeme();
        Giocatore vincitore;

        if (cartaSeconda.getSeme() != semeRegnante) {
            // Vince automaticamente chi ha giocato la prima carta
            vincitore = cartaPrima.getGiocatore();
        } else {
            // Entrambi hanno lo stesso seme -> vince chi ha il valore più alto
            if (cartaPrima.getValore().getOrdinePresa() >= cartaSeconda.getValore().getOrdinePresa()) {
                vincitore = cartaPrima.getGiocatore();
            } else {
                vincitore = cartaSeconda.getGiocatore();
            }
        }
        return vincitore;
    }
}
    