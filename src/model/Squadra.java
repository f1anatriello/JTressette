package model;

import java.util.*;

/**
 * Rappresenta una squadra di due giocatori in una partita 2 contro 2.
 * Ogni squadra ha un nome, due membri e un punteggio totale.
 * @author 1957447
 */

public class Squadra {
    private String nome;
    private List<Giocatore> membri = new ArrayList<>(2);
    private int punteggioTotale;

    /**
     * Costruttore della squadra con nome e due giocatori.
     * @param nome
     * @param g1
     * @param g2
     */
    public Squadra(String nome, Giocatore g1, Giocatore g2) {
        this.nome = nome;
        aggiungiGiocatore(nome, g1, g2);
    }

    /**
     * Aggiunge i due giocatori alla squadra.
     * @param nome
     * @param g1
     * @param g2
     */
    public void aggiungiGiocatore(String nome, Giocatore g1, Giocatore g2) {
    	if (g1 == null || g2 == null) {
            throw new IllegalArgumentException("I giocatori di una squadra non possono essere nulli.");
        }
        if (g1.equals(g2)) {
            throw new IllegalArgumentException("Una squadra deve essere composta da due giocatori distinti.");
        }
        this.nome = nome;
        this.membri = List.of(g1,g2);
        this.punteggioTotale = 0;
    }

    /**
     * Restituisce la lista dei membri della squadra.
     * @return Lista dei membri della squadra.
     */
    public List<Giocatore> getMembri() {
        return membri;
    }

    /**
     * Aggiunge punti al punteggio totale della squadra.
     * @param punti Punti da aggiungere.
     */
    public void aggiungiPunti(double punti) {
        punteggioTotale += punti;
    }
    
    /**
     * Verifica se un giocatore appartiene alla squadra.
     * @param g Giocatore da verificare.
     * @return true se il giocatore appartiene alla squadra, false altrimenti.
     */
    public boolean contieneGiocatore(Giocatore g) {
        return membri.contains(g);
    }

    /**
     * Restituisce una rappresentazione testuale della squadra.
     * @return Rappresentazione testuale della squadra.
     */
    @Override
	public String toString() {
		return "Squadra [nome=" + nome + ", membri=" + membri + ", punteggioTotale=" + punteggioTotale + "]";
	}

    /**
     * Override di hashCode e equals per confrontare le squadre in base ai loro membri e nome.
     */
	@Override
	public int hashCode() {
		return Objects.hash(membri, nome, punteggioTotale);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Squadra other = (Squadra) obj;
		return Objects.equals(membri, other.membri) && Objects.equals(nome, other.nome)
				&& punteggioTotale == other.punteggioTotale;
	}

    /**
     * Getter per il punteggio totale della squadra.
     * @return Punteggio totale della squadra.
     */
	public int getPunteggioTotale() {
        return punteggioTotale;
    }

    /**
     * Getter per il nome della squadra.
     * @return Nome della squadra.
     */
    public String getNome() {
        return nome;
    }
}