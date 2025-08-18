package model;

import java.util.ArrayList;
import java.util.List;

public abstract class Partita {
	
	protected final List<Giocatore> giocatori;
	protected Mazzo mazzo;
	protected boolean finita;
	protected List<Carta> terreno;
	
	protected Partita(List<Giocatore> giocatori) {
		this.giocatori = giocatori;
	}
	
	public void inizializzaPartita() {
		this.mazzo = new Mazzo();
		this.mazzo.mescola();
		mazzo.mescola();
		giocatori.forEach(g -> {
			g.getMano().clear();
			g.puntiOttenuti = 0;
			mazzo.assegnaMano(g); // assegna 10 carte a ciascun giocatore
		});
		terreno = new ArrayList<>(4);
	}
	
	public abstract String manoVintaDa(List<Carta> terr);
	
	public void aggiungiAlTerreno(Carta c) {
		terreno.add(c);
	}

	public boolean isFinita() {
		return finita;
	}

	public void setFinita(boolean finita) {
		this.finita = finita;
	}

	public List<Giocatore> getGiocatori() {
		return giocatori;
	}

	public Carta cartaVincente(List<Carta> terr) {
	    Seme semeUscita = terr.get(0).getSeme();
	    Carta best = null;
	    int bestOrd = Integer.MIN_VALUE;
	    for (Carta c : terr) {
	        if (c.getSeme() != semeUscita) continue;
	        int ord = c.getValore().getOrdinePresa();
	        if (ord > bestOrd) { bestOrd = ord; best = c; }
	    }
	    return best;
	}

	public int indiceVincitore(List<Carta> terr) {
	    Carta v = cartaVincente(terr);
	    for (int i = 0; i < terr.size(); i++)
	        if (terr.get(i) == v) return i; // stessa istanza calata
	    return -1;
	}

	public boolean isMazzoVuoto() {
		return mazzo.isEmpty();
	}

	public List<Carta> getTerreno() {
		return terreno;
	}

}
