package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rappresenta un mazzo di carte standard da 40 carte utilizzato nel gioco del Tressette.
 * Fornisce metodi per mescolare, pescare carte e assegnare mani ai giocatori.
 * @author 1957447
 */
public class Mazzo {
    private final List<Carta> carte;

    /**
     * Costruisce un nuovo mazzo di carte standard da 40 carte.
     * Le carte sono inizialmente ordinate per seme e valore.
     */
    public Mazzo() {
        this.carte = new ArrayList<>(40);
        for (Seme seme : Seme.values()) {
            for (Valore valore : Valore.values()) {
                carte.add(new Carta(seme, valore));
            }
        }
    }

    /**
     * Restituisce la lista delle carte attualmente presenti nel mazzo.
     *
     * @return La lista di carte nel mazzo.
     */
    public List<Carta> getCarte() {
        return carte;
    }

    /**
     * Mescola le carte nel mazzo in modo casuale.
     */
    public void mescola() {
        Collections.shuffle(carte);
    }

    /**
     * Pesca una singola carta dalla cima del mazzo.
     * @return Carta oppure null se il mazzo è vuoto.
     */
    public Carta pesca() {
        if (isEmpty()) return null;
        return carte.remove(carte.size() - 1);
    }

    /**
     * Assegna una mano completa al player.
     * @return 10 carte che costituiranno la mano del player.
     */
    public void assegnaMano(Giocatore g) {
        for(int i = 0; i < 10; i++) {
            g.mano.add(pesca());
        }
    }

    /**
     * Controlla se il mazzo è esaurito.
     * @return true se non ci sono più carte nel mazzo, false altrimenti.
     */
    public boolean isEmpty() {
        return carte.isEmpty();
    }

    /**
     * Restituisce il numero di carte attualmente presenti nel mazzo.
     * @return Il numero intero di carte rimanenti.
     */
    public int carteRimanenti() {
        return carte.size();
    }
}