package model;

import java.util.*;

public class Squadra {
    private String nome;
    private List<Giocatore> membri = new ArrayList<>(2);
    private int punteggioTotale;

    public Squadra(String nome, Giocatore g1, Giocatore g2) {
        this.nome = nome;
        aggiungiGiocatore(nome, g1, g2);
    }

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

    public List<Giocatore> getMembri() {
        return membri;
    }

    public void aggiungiPunti(double punti) {
        punteggioTotale += punti;
    }
    
    public boolean contieneGiocatore(Giocatore g) {
        return membri.contains(g);
    }

    @Override
	public String toString() {
		return "Squadra [nome=" + nome + ", membri=" + membri + ", punteggioTotale=" + punteggioTotale + "]";
	}

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

	public int getPunteggioTotale() {
        return punteggioTotale;
    }

    public String getNome() {
        return nome;
    }
}