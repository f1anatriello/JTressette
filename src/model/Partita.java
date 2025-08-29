package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una partita di Tressette.
 * Gestisce il mazzo, i giocatori, il terreno di gioco e lo stato della partita.
 * La classe è astratta e richiede l'implementazione di metodi specifici per la logica di gioco.
 * @author 1957447
 */
public abstract class Partita {
	
	protected final List<Giocatore> giocatori;
	protected Mazzo mazzo;
	protected boolean finita;
	protected List<Carta> terreno;
	
	/**
	 * Costruisce una nuova partita con la lista di giocatori specificata.
	 * @param giocatori
	 */
	protected Partita(List<Giocatore> giocatori) {
		this.giocatori = giocatori;
	}

	/**
	 * Resetta lo stato della partita.
	 */
	public abstract void reset();

	/**
	 * Inizializza la partita: crea e mescola il mazzo, assegna le mani ai giocatori e resetta il terreno.
	 */
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
	
	/**
	 * Determina quale giocatore ha vinto la mano corrente basandosi sulle carte sul terreno.
	 * @param terr
	 * @return Il giocatore che ha vinto la mano.
	 */
	public abstract Giocatore manoVintaDa(List<Carta> terr);
	
	public void aggiungiAlTerreno(Carta c) {
		terreno.add(c);
	}

	/**
	 * Indica se la partita è finita.
	 * @return true se la partita è finita, false altrimenti.
	 */
	public boolean isFinita() {
		return finita;
	}

	/**
	 * Imposta lo stato di fine partita.
	 * @param finita
	 */
	public void setFinita(boolean finita) {
		this.finita = finita;
	}

	/**
	 * Restituisce la lista dei giocatori nella partita.
	 * @return La lista dei giocatori.
	 */
	public List<Giocatore> getGiocatori() {
		return giocatori;
	}

	/**
	 * Imposta la lista dei giocatori nella partita.
	 * @param giocatori La nuova lista di giocatori.
	 * @return La lista aggiornata dei giocatori.
	 */
	public List<Giocatore> setGiocatori(List<Giocatore> giocatori) {
		this.giocatori.clear();
		this.giocatori.addAll(giocatori);
		return this.giocatori;
	}

	/**
	 * Restituisce il mazzo di carte utilizzato nella partita.
	 * @return Il mazzo di carte.
	 */
	public Mazzo getMazzo() {
		return mazzo;
	}

	/**
	 * Determina la carta vincente tra quelle giocate sul terreno.
	 * @param terr
	 * @return La carta vincente.
	 */
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

	/**
	 * Determina l'indice del giocatore che ha vinto la mano corrente.
	 * @param terr
	 * @return L'indice del giocatore vincitore.
	 */
	public int indiceVincitore(List<Carta> terr) {
	    Carta v = cartaVincente(terr);
	    for (int i = 0; i < terr.size(); i++)
	        if (terr.get(i) == v) return i; // stessa istanza calata
	    return -1;
	}

	/**
	 * Controlla se il mazzo è vuoto.
	 * @return true se il mazzo è vuoto, false altrimenti.
	 */
	public boolean isMazzoVuoto() {
		return mazzo.isEmpty();
	}

	/**
	 * Restituisce le carte attualmente sul terreno di gioco.
	 * @return La lista delle carte sul terreno.
	 */
	public List<Carta> getTerreno() {
		return terreno;
	}

	/**
	 * Pulisce il terreno di gioco rimuovendo tutte le carte.
	 */
	public void clearTerreno() {
		this.terreno.clear();
	}

}
