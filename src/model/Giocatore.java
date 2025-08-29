package model;

import java.util.*;
/**
 * Rappresenta un giocatore in una partita di Tressette.
 * Un giocatore ha un nome, una mano di carte, un punteggio ottenuto e appartiene a una squadra (forse).
 * La classe estende Observable per notificare gli osservatori alla fine della partita.
 * @author 1957447
 */
public abstract class Giocatore extends Observable{
	protected String nome;
	protected List<Carta> mano = new ArrayList<>(10);
	protected double puntiOttenuti;
	protected Squadra squadra;

	/**
	 * Costruisce un nuovo giocatore con il nome specificato.
	 * @param nome
	 */
	public Giocatore(String nome) {
		this.nome = nome;
	}


	/**
	 * Riceve una carta e la aggiunge alla mano del giocatore.
	 * @param c
	 */
	public void riceviCarta(Carta c) {
		if (c == null) throw new IllegalArgumentException("La carta non può essere nulla");
		mano.add(c);
	}
	
	/**
	 * Rimuove una carta dalla mano del giocatore.
	 * @param c
	 */
	public String getNome() {
		return nome;
	}

	/** Restituisce la mano corrente del giocatore.
	 * @return La lista di carte nella mano del giocatore.
	 */
	public List<Carta> getMano() {
		return mano;
	}

	/**
	 * Restituisce i punti ottenuti dal giocatore.
	 * @return I punti ottenuti dal giocatore.
	 */
	public double getPunti() {
		return puntiOttenuti;
	}
	
	/**
	 * Aggiunge punti al totale del giocatore.
	 * @param p I punti da aggiungere.
	 */
	public void aggiungiPunti(double p) {
		this.puntiOttenuti += p;
	}
	
	/**
	 * Imposta il nome del giocatore.
	 * @param nome Il nuovo nome del giocatore.
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	/**
	 * Imposta la mano del giocatore.
	 * @param mano
	 */
	public void setMano(List<Carta> mano) {
		this.mano = mano;
	}
	
	/**
	 * Imposta i punti ottenuti dal giocatore.
	 * @param puntiOttenuti I nuovi punti ottenuti dal giocatore.
	 */
	public Squadra getSquadra() {
		return squadra;
	}

	/**
	 * Imposta la squadra del giocatore.
	 * @param squadra La squadra a cui il giocatore appartiene.
	 */
	public void setSquadra(Squadra squadra) {
		this.squadra = squadra;
	}

	/** 
	 * Notifica gli osservatori che la partita è terminata e se il giocatore ha vinto o perso.
	 * Deve essere chiamato alla fine della partita.
	 * @param p La partita corrente.
	 * @return La carta scelta dal giocatore.
	 */
	public void notificaFinePartita(boolean haVinto) {
		setChanged();
		notifyObservers(Boolean.valueOf(haVinto));
	}

}
