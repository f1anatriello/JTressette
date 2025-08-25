package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Partita1v1 extends Partita {

	private static Partita1v1 instance = null;

    public Partita1v1(List<Giocatore> giocatori) {
        super(giocatori);
    }

	public static Partita1v1 getInstance(List<Giocatore> giocatori) {
		if (instance == null) {
			instance = new Partita1v1(giocatori);
		}
		return instance;
	}

    @Override
    public void reset() {
        giocatori.clear();
        mazzo = null;
        finita = false;
        terreno = null;
        instance = null;
    }

    public Giocatore vincitore1v1(){
    	Map<Giocatore,Double> mappa = new HashMap<>();
    	
    	for (Giocatore g : giocatori) {
			mappa.put(g, g.puntiOttenuti);
		}
    	
    	return mappa.entrySet().stream()
    	        .max(Map.Entry.comparingByValue())
    	        .map(Map.Entry::getKey)
    	        .orElse(null);
    }

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
            // Entrambi hanno lo stesso seme → vince chi ha il valore più alto
            if (cartaPrima.getValore().getOrdinePresa() >= cartaSeconda.getValore().getOrdinePresa()) {
                vincitore = cartaPrima.getGiocatore();
            } else {
                vincitore = cartaSeconda.getGiocatore();
            }
        }
        return vincitore;
    }
}
    