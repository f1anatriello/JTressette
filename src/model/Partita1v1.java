package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Partita1v1 extends Partita {

    public Partita1v1(List<Giocatore> giocatori) {
        super(giocatori);
    }
    
    public String vincitore1v1(){
    	Map<String,Double> mappa = new HashMap<>();
    	
    	for (Giocatore g : giocatori) {
			mappa.put(g.nome, g.puntiOttenuti);
		}
    	
    	return mappa.entrySet().stream()
    	        .max(Map.Entry.comparingByValue())
    	        .map(Map.Entry::getKey)
    	        .orElse(null);
    }

	@Override
	public String manoVintaDa(List<Carta> terr) {
		    if (terr == null || terr.isEmpty())
		        throw new IllegalArgumentException("Terreno vuoto");

			// Seme d’uscita = seme della prima carta sul tavolo
			Seme semeUscita = terr.get(0).getSeme();

		    int bestIdx = -1;
		    int bestOrd = Integer.MIN_VALUE;

		    for (int i = 0; i < terr.size(); i++) {
		        Carta c = terr.get(i);
		        // in Tressette chi è fuori seme non può "prendere"
		        if (c.getSeme() != semeUscita) continue;

		        int ord = c.getValore().getOrdinePresa();
		        if (ord > bestOrd) {
		            bestOrd = ord;
		            bestIdx = i;
		        }
		    }

		    if (bestIdx < 0)
		        throw new IllegalStateException("Nessuna carta valida nel seme d’uscita");

		    return giocatori.get(bestIdx).nome;   // oppure giocatori.get(bestIdx).getNome() se preferisci il getter
		}
	}
    