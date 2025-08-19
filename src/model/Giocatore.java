package model;

import java.util.*;

public abstract class Giocatore {
	protected String nome;
	protected List<Carta> mano = new ArrayList<>(10);
	protected double puntiOttenuti;


	public Giocatore(String nome) {
		this.nome = nome;
	}

	public void riceviCarta(Carta c) {
		if (c == null) throw new IllegalArgumentException("La carta non può essere nulla");
		mano.add(c);
	}
	
	public String getNome() {
		return nome;
	}


	public List<Carta> getMano() {
		return mano;
	}

	
	public double getPunti() {
		return puntiOttenuti;
	}
	
	public void aggiungiPunti(double p) {
		this.puntiOttenuti += p;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	

	public void setMano(List<Carta> mano) {
		this.mano = mano;
	}
	

}
